package com.psu.exshell.Application;

import com.psu.exshell.Knowledge.Fact;
import com.psu.exshell.Knowledge.Rule;
import com.psu.exshell.Knowledge.Value;
import com.psu.exshell.Knowledge.VarType;
import com.psu.exshell.Knowledge.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class Consulter {

	// Memory
	private final State context;
	private final Variable goal;
	private final ArrayList<Variable> variables;
	private final Stack<Variable> variablesToInit;
	private final Map<Variable, Value> valuedVariables;
	private final ArrayList<Rule> rules;
	private final Map<String, Rule> variableHierarchy;

	public Consulter(Variable goal, State context) {
		this.goal = goal;
		this.context = context;

		variables = new ArrayList<>(context.getVariables());
		rules = new ArrayList<>(context.getRules());
		variablesToInit = new Stack<>();
		variablesToInit.push(goal);
		valuedVariables = new HashMap<>();
		variableHierarchy = new HashMap<>();
	}

	public Variable getNextVariableToInit() {
		return variablesToInit.peek();
	}

	public void setValue(Variable variable, Value newValue, Rule inferredRule) {
		valuedVariables.put(variable, newValue);
		variableHierarchy.put(variable.getName(), inferredRule);
		variables.remove(variable);
		// Delete all false rules
		rules.removeIf(r -> r.getPremise().stream().anyMatch(f -> checkFact(f) == Mode.FACT_FALSE));
	}

	public Value getValue(Variable variable) {
		return valuedVariables.get(variable);
	}
	
	public Hashtable<Object, Object> getHierarchy() {
		var stackVariables = new Stack<String>();
		var stackHashtables = new Stack<Hashtable>();
		var root = new Hashtable<>();
		stackVariables.push(goal.toString());
		stackHashtables.push(root);
		while (!stackVariables.isEmpty()) {
			var topVariable = stackVariables.pop();
			if (topVariable == null) {
				stackHashtables.pop();
				continue;
			}
			var topHashtable = stackHashtables.peek();
			var rule = variableHierarchy.get(topVariable);
			if (rule == null) {
				// Leaf node
				topHashtable.put(topVariable, new String[0]);
			} else {
				var newHashtable = new Hashtable<>();
				topHashtable.put(topVariable + " (" + rule.getName() + ")", newHashtable);
				stackVariables.push(null);
				rule.getPremise().forEach(f -> stackVariables.push(f.getVariable().getName()));
				stackHashtables.push(newHashtable);
			}
		}
		return root;
	}
	
	public Map<Variable, Value> getValuedVariables() {
		return valuedVariables;
	}

	private Mode checkFact(Fact f) {
		if (valuedVariables.containsKey(f.getVariable())) {
			if (valuedVariables.get(f.getVariable()).equals(f.getValue())) {
				return Mode.FACT_TRUE;
			} else {
				return Mode.FACT_FALSE;
			}
		}
		return Mode.FACT_UNKNOWN;
	}

	public Mode conclude() {
		var inferrerState = Mode.UNDEFINED_INFERRER_STATE;
		if (rules.stream().filter(r -> r.getConclusion().getVariable().equals(goal)).count() == 0) {
			return Mode.GOAL_NOT_INFERABLE;
		}
		while (inferrerState == Mode.UNDEFINED_INFERRER_STATE) {
			var localGoal = variablesToInit.peek();
			if (valuedVariables.containsKey(localGoal)) {
				variablesToInit.pop();
				if (localGoal.equals(goal)) {
					inferrerState = Mode.GOAL_INFERRED;
				}
			} else {
				var inferringRules = rules.stream().filter(r
						-> r.getConclusion().getVariable().equals(localGoal)).collect(Collectors.toList());
				if (inferringRules.size() > 0) {
					var unknownFacts = inferringRules.get(0).getPremise().stream()
							.filter(f -> checkFact(f) == Mode.FACT_UNKNOWN)
							.collect(Collectors.toList());
					if (unknownFacts.isEmpty()) {
						this.setValue(localGoal, inferringRules.get(0).getConclusion().getValue(), inferringRules.get(0));
					} else {
						var firstFact = unknownFacts.get(0);
						variablesToInit.push(firstFact.getVariable());
					}
				} else {
					if (localGoal.getType() != VarType.INFERRED) {
						inferrerState = Mode.NEED_QUESTION;
					} else {
						var rulesToDelete = new ArrayList<>();
						rules.forEach(r -> {
							boolean needRemove = false;
							for (var f : r.getPremise()) {
								if (f.getVariable().equals(localGoal)) {
									needRemove = true;
									break;
								}
							}
							if (needRemove) {
								rulesToDelete.add(r);
							}
						});
						rules.removeAll(rulesToDelete);
						variables.remove(localGoal);
						variablesToInit.pop();
						if (variablesToInit.isEmpty()) {
							inferrerState = Mode.GOAL_NOT_INFERABLE;
						}
					}
				}
			}
		}
		return inferrerState;
	}
}
