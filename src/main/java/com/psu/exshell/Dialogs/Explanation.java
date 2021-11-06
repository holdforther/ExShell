package com.psu.exshell.Dialogs;

import com.psu.exshell.Knowledge.Value;
import com.psu.exshell.Knowledge.Variable;
import com.psu.exshell.TableModels.ValuedVariablesTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;

public class Explanation extends JDialog {
	
	// Data and models
	private final String goalValue;
	// Interactive components
	private final JTree treeRules;
	// Dialog components
	private final JOptionPane optionPane;
	
	public Explanation(JFrame parent, String goalValue, Hashtable<?, ?> hierarchy, Map<Variable, Value> valuedVariables) {
		super(parent, "Explanation", true);
		this.goalValue = goalValue;
		
		var labelValue = new JLabel(goalValue);
		var panelGoalValue = new JPanel(new GridLayout(1, 1));
		panelGoalValue.setBorder(BorderFactory.createTitledBorder("Value"));
		panelGoalValue.add(labelValue);
		
		treeRules = new JTree(hierarchy);
		var panelTreeView = new JScrollPane(treeRules);
		var panelTree = new JPanel(new GridLayout(1, 1));
		panelTree.setBorder(BorderFactory.createTitledBorder("Inference tree"));
		panelTree.add(panelTreeView);
		
		var panelGoal = new JPanel(new BorderLayout());
		panelGoal.setBorder(BorderFactory.createTitledBorder("Goal"));
		panelGoal.add(panelGoalValue, BorderLayout.PAGE_START);
		panelGoal.add(panelTree, BorderLayout.CENTER);
		
		var variables = new ArrayList<>(valuedVariables.keySet());
		var values = new ArrayList<>(valuedVariables.values());
		var tableModel = new ValuedVariablesTableModel(variables, values);
		var tableValuedVariables = new JTable(tableModel);
		var scrollPanelTable = new JScrollPane(tableValuedVariables);
		var panelValuedVariables = new JPanel(new GridLayout(1, 1));
		panelValuedVariables.setBorder(BorderFactory.createTitledBorder("Values"));
		panelValuedVariables.add(scrollPanelTable);
		
		var panelExplanation = new JPanel(new GridLayout(1, 2));
		panelExplanation.add(panelGoal);
		panelExplanation.add(panelValuedVariables);
		
		optionPane = new JOptionPane(panelExplanation, JOptionPane.PLAIN_MESSAGE);
		optionPane.addPropertyChangeListener((evt) -> {
			String prop = evt.getPropertyName();
			if ((evt.getSource() == optionPane) && (JOptionPane.VALUE_PROPERTY.equals(prop))) {
				//If you were going to check something before closing the window, you'd do it here.
				int result = getDialogResult();
				if (result == JOptionPane.UNDEFINED_CONDITION) {
					return;
				}
				if (result == JOptionPane.OK_OPTION) {
					dispose();
				} else {
					dispose();
				}
			}
		});

		setContentPane(optionPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public int getDialogResult() {
		var selectedValue = optionPane.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		var options = optionPane.getOptions();
		//If there is not an array of option buttons:
		if (options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue);
			}
			return JOptionPane.CLOSED_OPTION;
		}
		//If there is an array of option buttons:
		for (int counter = 0, maxCounter = options.length;
				counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}
}
