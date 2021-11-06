package com.psu.exshell.Dialogs;

import com.psu.exshell.Application.Mode;
import com.psu.exshell.Application.State;
import com.psu.exshell.Knowledge.Domain;
import com.psu.exshell.Knowledge.VarType;
import com.psu.exshell.Knowledge.Variable;

import javax.swing.*;
import java.awt.*;

public class EditVariable extends JDialog {

    // Data and models
    private final Variable variable;
    private final State context;
    // Interactive components
    private final JTextField tfName;
    private final JComboBox<Domain> cbDomain;
    private final JButton btnCreate;
    private final JRadioButton rbRequested;
    private final JRadioButton rbInferred;
    private final JRadioButton rbInferredRequested;
    private final JTextField tfQuestion;
    // Dialog components
    private final JOptionPane optionPane;

    public EditVariable(JFrame parent, Variable variable, State context) {
        super(parent, "Editing Variable", true);
        this.variable = variable;
        this.context = context;

        var panelName = new JPanel(new GridLayout(1, 1));
        panelName.setBorder(BorderFactory.createTitledBorder("Name"));
        tfName = new JTextField(variable.getName());
        panelName.add(tfName);

        var panelDomain = new JPanel(new GridLayout(1, 2));
        panelDomain.setBorder(BorderFactory.createTitledBorder("Domain"));
        cbDomain = new JComboBox<>();
        context.getDomains().forEach(d -> cbDomain.addItem(d));
        cbDomain.setSelectedItem(variable.getDomain());
        btnCreate = new JButton("Create Domain");
        btnCreate.addActionListener(e -> createDomain());
        panelDomain.add(cbDomain);
        panelDomain.add(btnCreate);

        var panelType = new JPanel();
        panelType.setBorder(BorderFactory.createTitledBorder("Type"));
        rbInferredRequested = new JRadioButton("Infer-Requested");
        rbInferred = new JRadioButton("Inferred");
        rbRequested = new JRadioButton("Requested");
        var group = new ButtonGroup();
        group.add(rbRequested);
        group.add(rbInferred);
        group.add(rbInferredRequested);
        rbRequested.setSelected(variable.getType() == VarType.REQUESTED);
        rbInferred.setSelected(variable.getType() == VarType.INFERRED);
        rbInferredRequested.setSelected(variable.getType() == VarType.INFERRED_REQUESTED);
        panelType.add(rbRequested);
        panelType.add(rbInferred);
        panelType.add(rbInferredRequested);

        var panelQuestion = new JPanel(new GridLayout(1, 1));
        panelQuestion.setBorder(BorderFactory.createTitledBorder("Question"));
        tfQuestion = new JTextField(variable.getQuestion());
        panelQuestion.add(tfQuestion);

        var panelEditVariable = new JPanel(new GridLayout(4, 1));
        panelEditVariable.add(panelName);
        panelEditVariable.add(panelDomain);
        panelEditVariable.add(panelType);
        panelEditVariable.add(panelQuestion);

        optionPane = new JOptionPane(panelEditVariable, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
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

    private void createDomain() {
        var domain = new Domain();
        var dialogCreateDomain = new EditDomain(null, domain, Mode.CREATE, context);
        if (dialogCreateDomain.getDialogResult() == JOptionPane.OK_OPTION) {
            context.add(domain);
            var allDomains = context.getDomains();
            cbDomain.addItem(allDomains.get(allDomains.size() - 1));
            cbDomain.updateUI();
        }
    }

    private void checkInputs() {
        var name = tfName.getText().trim();
        if (name.isEmpty()) {
            tfName.grabFocus();
            JOptionPane.showMessageDialog(null, "Variable name can't be empty!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        var type = VarType.REQUESTED;
        if (rbInferred.isSelected()) {
            type = VarType.INFERRED;
        } else if (rbInferredRequested.isSelected()) {
            type = VarType.INFERRED_REQUESTED;
        }
        var domain = (Domain) cbDomain.getSelectedItem();
        if (domain == null) {
            cbDomain.grabFocus();
            JOptionPane.showMessageDialog(null, "Variable must have a domain!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        var question = tfQuestion.getText().trim();
        if (question.isEmpty()) {
            tfQuestion.grabFocus();
            JOptionPane.showMessageDialog(null, "Question can't be empty!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        boolean isUnique = true;
        for (var v : context.getVariables()) {
            if (v.getName().equalsIgnoreCase(name)) {
                isUnique = false;
                break;
            }
        }
        if (!isUnique && !variable.getName().equalsIgnoreCase(name)) {
            JOptionPane.showMessageDialog(null, "This variable already exists!", "Editing error", JOptionPane.ERROR_MESSAGE);
            tfName.grabFocus();
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        variable.setName(name);
        variable.setDomain(domain);
        variable.setType(type);
        variable.setQuestion(question);
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
