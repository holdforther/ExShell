package com.psu.exshell.Dialogs;

import com.psu.exshell.Application.State;
import com.psu.exshell.Knowledge.VarType;
import com.psu.exshell.Knowledge.Variable;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SelectGoal extends JDialog {

	// Data and models
	private final State context;
	private Variable goal;
	// Interactive components
	private final JComboBox<Variable> cbVariable;
	// Dialog components
	private final JOptionPane optionPane;

	public SelectGoal(JFrame parent, State context) {
		super(parent, "Please, select GOAL variable", true);
		this.context = context;

		var panelGoal = new JPanel(new GridLayout(1, 1));
		panelGoal.setBorder(BorderFactory.createTitledBorder("Goal"));
		cbVariable = new JComboBox<>();
		this.context.getVariables().forEach(v -> {
			if (v.getType() != VarType.REQUESTED) {
				cbVariable.addItem(v);
			}
		});
		panelGoal.add(cbVariable);

		optionPane = new JOptionPane(panelGoal, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		optionPane.addPropertyChangeListener((evt) -> {
			String prop = evt.getPropertyName();
			if ((evt.getSource() == optionPane) && (JOptionPane.VALUE_PROPERTY.equals(prop))) {
				//If you were going to check something before closing the window, you'd do it here.
				int result = getDialogResult();
				if (result == JOptionPane.UNDEFINED_CONDITION) {
					return;
				}
				if (result == JOptionPane.OK_OPTION) {
					checkInputs();
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

	private void checkInputs() {
		var variable = (Variable) cbVariable.getSelectedItem();
		if (variable == null) {
			cbVariable.grabFocus();
			JOptionPane.showMessageDialog(null, "Please, select the variable", "Input error", JOptionPane.ERROR_MESSAGE);
			optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
			return;
		}
		goal = variable;
		dispose();
	}

	public Variable getGoal() {
		return goal;
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
