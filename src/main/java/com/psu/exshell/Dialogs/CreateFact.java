package com.psu.exshell.Dialogs;

import com.psu.exshell.Application.Mode;
import com.psu.exshell.Application.State;
import com.psu.exshell.Knowledge.Fact;
import com.psu.exshell.Knowledge.Value;
import com.psu.exshell.Knowledge.VarType;
import com.psu.exshell.Knowledge.Variable;

import javax.swing.*;
import java.awt.*;

public class CreateFact extends JDialog {
    
    // Data and models
    private final Fact fact;
    private final State context;
	private final Mode factType;
    // Interactive components
    private final JComboBox<Variable> cbVariable;
    private final JButton btnAddVariable;
    private final JComboBox<Value> cbValue;
    // Dialog components
    private final JOptionPane optionPane;

    public CreateFact(JFrame parent, Fact fact, Mode factType, State context) {
        super(parent, "Creating new Fact", true);
        this.fact = fact;
        this.context = context;
		this.factType = factType;
        
        var panelVariable = new JPanel(new BorderLayout());
        panelVariable.setBorder(BorderFactory.createTitledBorder("Variable"));
        cbVariable = new JComboBox<>();
        context.getVariables().forEach(v -> {
			if (factType == Mode.CREATE_PREMISE_FACT) {
				cbVariable.addItem(v);
			} else if (v.getType() != VarType.REQUESTED) {
				cbVariable.addItem(v);
			}
		});
        cbVariable.addActionListener(e -> setComboBoxValue(cbVariable.getSelectedIndex()));
        btnAddVariable = new JButton("Create");
        btnAddVariable.addActionListener(e -> createVariable());
        panelVariable.add(cbVariable, BorderLayout.CENTER);
        panelVariable.add(btnAddVariable, BorderLayout.LINE_END);

        var panelValue = new JPanel(new GridLayout(1, 1));
        panelValue.setBorder(BorderFactory.createTitledBorder("Value"));
        cbValue = new JComboBox<>();
        setComboBoxValue(cbVariable.getSelectedIndex());
        panelValue.add(cbValue);

        var labelOperation = new JLabel("=", SwingConstants.CENTER);

        var panelAddFact = new JPanel(new GridLayout(3, 1));
        panelAddFact.add(panelVariable);
        panelAddFact.add(labelOperation);
        panelAddFact.add(panelValue);

        optionPane = new JOptionPane(panelAddFact, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
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

    private void setComboBoxValue(int variableIdx) {
        if (variableIdx == -1) {
            return;
        }
        var variable = cbVariable.getItemAt(variableIdx);
        cbValue.removeAllItems();
        variable.getDomain().getValues().forEach(v -> cbValue.addItem(v));
        cbValue.updateUI();
    }

    private void createVariable() {
        var variable = new Variable();
        var dialogCreateVariable = new EditVariable(null, variable, context);
        if (dialogCreateVariable.getDialogResult() == JOptionPane.OK_OPTION) {
            context.add(variable);
			if (factType == Mode.CREATE_CONCLUSION_FACT && variable.getType() == VarType.REQUESTED) {
				return;
			}
            var allVariables = context.getVariables();
            cbVariable.addItem(allVariables.get(allVariables.size() - 1));
            cbVariable.updateUI();
        }
    }
    
    private void checkInputs() {
        var variable = (Variable) cbVariable.getSelectedItem();
        var value = (Value) cbValue.getSelectedItem();
        if (variable == null) {
            cbVariable.grabFocus();
            JOptionPane.showMessageDialog(null, "Please, select the variable", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        if (value == null) {
            cbValue.grabFocus();
            JOptionPane.showMessageDialog(null, "Please, select the value", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        fact.setVariable(variable);
        fact.setValue(value);
        dispose();
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
