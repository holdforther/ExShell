package com.psu.exshell.Dialogs;

import com.psu.exshell.Application.Mode;
import com.psu.exshell.Application.State;
import com.psu.exshell.Knowledge.*;
import com.psu.exshell.TableModels.FactTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EditRule extends JDialog {

    // Data and models
    private final Rule rule;
    private final Mode editMode;
    private final State context;
    private final FactTableModel premiseTableModel;
    private final FactTableModel conclusionTableModel;
    // Interactive components
    private final JTextField tfName;
    private final JTable tablePremise;
    private final JTable tableConclusion;
    private final JButton btnCreatePremiseFact;
    private final JButton btnDeletePremiseFact;
    private final JButton btnCreateConclusionFact;
    private final JButton btnDeleteConclusionFact;
    private final JTextField tfExplanation;
    // Dialog components
    private final JOptionPane optionPane;

    public EditRule(JFrame parent, Rule rule, Mode editMode, State context) {
        super(parent, "Editing Rule", true);
        this.rule = rule;
        this.editMode = editMode;
        this.context = context;

        var panelName = new JPanel(new GridLayout(1, 1));
        panelName.setBorder(BorderFactory.createTitledBorder("Name"));
        tfName = new JTextField(rule.getName());
        panelName.add(tfName);

        var panelPremise = new JPanel(new BorderLayout());
        panelPremise.setBorder(BorderFactory.createTitledBorder("Premise"));
        premiseTableModel = new FactTableModel(rule.getPremise());
        tablePremise = new JTable(premiseTableModel);
        tablePremise.setFillsViewportHeight(true);
        var panelTablePremise = new JScrollPane(tablePremise);
        btnCreatePremiseFact = new JButton("Create");
        btnCreatePremiseFact.addActionListener(e -> createPremiseFact());
        btnDeletePremiseFact = new JButton("Delete");
        btnDeletePremiseFact.addActionListener(e -> deletePremiseFact());
        var panelButtonsPremise = new JPanel(new GridLayout(1, 2));
        panelButtonsPremise.add(btnCreatePremiseFact);
        panelButtonsPremise.add(btnDeletePremiseFact);
        panelPremise.add(panelTablePremise, BorderLayout.CENTER);
        panelPremise.add(panelButtonsPremise, BorderLayout.PAGE_END);

        var panelConclusion = new JPanel(new BorderLayout());
        panelConclusion.setBorder(BorderFactory.createTitledBorder("Conclusion"));
        var conclusion = new ArrayList<Fact>();
        conclusion.add(rule.getConclusion());
        conclusionTableModel = new FactTableModel(conclusion);
        tableConclusion = new JTable(conclusionTableModel);
        tableConclusion.setFillsViewportHeight(true);
        var panelTableConclusion = new JScrollPane(tableConclusion);
        btnCreateConclusionFact = new JButton("Create");
        btnCreateConclusionFact.addActionListener(e -> createConclusionFact());
        btnDeleteConclusionFact = new JButton("Delete");
        btnDeleteConclusionFact.addActionListener(e -> deleteConclusionFact());
        var panelButtonsConclusion = new JPanel(new GridLayout(1, 2));
        panelButtonsConclusion.add(btnCreateConclusionFact);
        panelButtonsConclusion.add(btnDeleteConclusionFact);
        panelConclusion.add(panelTableConclusion, BorderLayout.CENTER);
        panelConclusion.add(panelButtonsConclusion, BorderLayout.PAGE_END);

        var panelFacts = new JPanel(new GridLayout(1, 2));
        panelFacts.add(panelPremise);
        panelFacts.add(panelConclusion);

        var panelExplanation = new JPanel(new GridLayout(1, 1));
        panelExplanation.setBorder(BorderFactory.createTitledBorder("Explanation"));
        tfExplanation = new JTextField(rule.getExplanation());
        panelExplanation.add(tfExplanation);

        var panelEditRule = new JPanel(new BorderLayout());
        panelEditRule.add(panelName, BorderLayout.PAGE_START);
        panelEditRule.add(panelFacts, BorderLayout.CENTER);
        panelEditRule.add(panelExplanation, BorderLayout.PAGE_END);

        optionPane = new JOptionPane(panelEditRule, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
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

    private void createPremiseFact() {
        var factCopy = new Fact();
        var dialogCreateFact = new CreateFact(null, factCopy, Mode.CREATE_PREMISE_FACT, context);
        if (dialogCreateFact.getDialogResult() == JOptionPane.OK_OPTION) {
            rule.getPremise().add(factCopy);
            premiseTableModel.fireTableDataChanged();
        }
    }

    private void deletePremiseFact() {
        var factIdx = tablePremise.getSelectedRow();
        if (factIdx == -1) {
            return;
        }
        rule.getPremise().remove(factIdx);
		premiseTableModel.fireTableDataChanged();
    }

    private void createConclusionFact() {
        var factCopy = new Fact();
        var dialogCreateFact = new CreateFact(null, factCopy, Mode.CREATE_CONCLUSION_FACT, context);
        if (dialogCreateFact.getDialogResult() == JOptionPane.OK_OPTION) {
            var conclusion = rule.getConclusion();
            conclusion.setVariable(factCopy.getVariable());
            conclusion.setValue(factCopy.getValue());
            conclusionTableModel.fireTableDataChanged();
        }
    }

    private void deleteConclusionFact() {
        var conclusion = rule.getConclusion();
        conclusion.setValue(new Value());
        conclusion.setVariable(new Variable());
        conclusionTableModel.fireTableDataChanged();
    }
    
    private void checkInputs() {
        var name = tfName.getText().trim();
        var premise = rule.getPremise();
        var conclusion = rule.getConclusion();
        var explanation = tfExplanation.getText().trim();
        if (name.isEmpty()) {
            tfName.grabFocus();
            JOptionPane.showMessageDialog(null, "Rule name can't be empty!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        if (premise.isEmpty()) {
            btnCreatePremiseFact.grabFocus();
            JOptionPane.showMessageDialog(null, "Rule's premise must contain some facts!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        if (conclusion.getVariable().getName().equals("") && conclusion.getValue().getValue().equals("")) {
            btnCreateConclusionFact.grabFocus();
            JOptionPane.showMessageDialog(null, "Rule's conclusion is empty!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        boolean isUnique = true;
        for (var r : context.getRules()) {
            if (r.getName().equalsIgnoreCase(name)) {
                isUnique = false;
                break;
            }
        }
        if (!isUnique && !rule.getName().equalsIgnoreCase(name)) {
            JOptionPane.showMessageDialog(null, "This rule already exists!", "Editing error", JOptionPane.ERROR_MESSAGE);
            tfName.grabFocus();
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        rule.setName(name);
        rule.setExplanation(explanation);
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
