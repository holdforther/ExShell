package com.psu.exshell.Dialogs;

import com.psu.exshell.Application.Mode;
import com.psu.exshell.Application.State;
import com.psu.exshell.Knowledge.Domain;
import com.psu.exshell.Knowledge.Value;
import com.psu.exshell.TableModels.ValueTableModel;

import javax.swing.*;
import java.awt.*;

public final class EditDomain extends JDialog {
    // Data and models
    private final State context;
    private final Domain domain;
    private final Mode editMode; 
    private final ValueTableModel valueTableModel;
    // Interactive components
    private final JTextField tfName;
    private final JTable tableValues;
    private final JTextField tfValue;
    private final JButton btnAdd;
    private final JButton btnDelete;
    // Dialog components
    private final JOptionPane optionPane;

    public EditDomain(JFrame parent, Domain domain, Mode editMode, State context) {
        super(parent, "Editing Domain", true);
        this.domain = domain;
        this.context = context;
        this.editMode = editMode;
        
        valueTableModel = new ValueTableModel(domain.getValues());
        tableValues = new JTable(valueTableModel);
        tableValues.setFillsViewportHeight(true);
        var panelTableValues = new JScrollPane(tableValues);
        panelTableValues.setBorder(BorderFactory.createTitledBorder("Domain values"));

        var panelName = new JPanel(new GridLayout(3, 1));
        panelName.setBorder(BorderFactory.createTitledBorder("Name"));
        tfName = new JTextField(domain.getName());
        panelName.add(tfName);
        panelName.add(new JLabel()); // Just as an empty space
        panelName.add(new JLabel());

        var panelValue = new JPanel(new GridLayout(3, 1));
        panelValue.setBorder(BorderFactory.createTitledBorder("Value"));
        tfValue = new JTextField();
        btnAdd = new JButton("Create");
        btnAdd.addActionListener(e -> addValue());
        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteValue());
        panelValue.add(tfValue);
        panelValue.add(btnAdd);
        panelValue.add(btnDelete);

        var panelInput = new JPanel(new GridLayout(1, 2));
        panelInput.add(panelName);
        panelInput.add(panelValue);

        var panelEditDomain = new JPanel(new BorderLayout());
        panelEditDomain.add(panelInput, BorderLayout.PAGE_START);
        panelEditDomain.add(panelTableValues, BorderLayout.CENTER);

        optionPane = new JOptionPane(panelEditDomain, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
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

    private void addValue() {
        var str = tfValue.getText().trim();
        if (str.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Value is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            tfValue.grabFocus();
            return;
        }
        var isUnique = true;
        var values = domain.getValues();
        for (var v : values) {
            if (v.getValue().equals(str)) {
                isUnique = false;
                break;
            }
        }
        if (!isUnique) {
            JOptionPane.showMessageDialog(this, "Value already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            tfValue.grabFocus();
            return;
        }
        values.add(new Value(str));
        valueTableModel.fireTableDataChanged();
        tfValue.setText("");
    }

    private void deleteValue() {
        int valueIdx = tableValues.getSelectedRow();
        if (valueIdx == -1) {
            return;
        }
        // If we are updating the domain then we check if it is used in rules
        // Otherwise (a new domain is being created) just delete
        var values = domain.getValues();
        if (editMode == Mode.UPDATE) {
            boolean isUsed = false;
            var sb = new StringBuilder();
            eachRule:
            for (var r : context.getRules()) {
                // Check the rule's premise
                for (var f : r.getPremise()) {
                    if (domain.equals(f.getVariable().getDomain()) && f.getValue().equals(values.get(valueIdx))) {
                        isUsed = true;
                        sb.append(r).append(", ");
                        continue eachRule;
                    }
                }
                // Check the rule's conclusion
                var factDomain = r.getConclusion().getVariable().getDomain();
                if (domain.equals(factDomain) && r.getConclusion().getValue().equals(values.get(valueIdx))) {
                    sb.append(r).append(", ");
                    isUsed = true;
                }
            }
            if (isUsed) {
                // Delete last ", "
                sb.delete(sb.length() - 2, sb.length() - 1);
                var msg = "Can't delete value, it's being used in following rules: " + sb.toString();
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        values.remove(valueIdx);
        valueTableModel.fireTableDataChanged();
    }

    private void checkInputs() {
        var name = tfName.getText().trim();
        var values = domain.getValues();
        if (name.isEmpty()) {
            tfName.grabFocus();
            JOptionPane.showMessageDialog(null, "Domain name can't be empty!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        if (values.isEmpty()) {
            btnAdd.grabFocus();
            JOptionPane.showMessageDialog(null, "Domain must contain some values!", "Input error", JOptionPane.ERROR_MESSAGE);
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        // Check if the new domain name is unique
        boolean isUnique = true;
        for (var d : context.getDomains()) {
            if (d.getName().equalsIgnoreCase(name)) {
                isUnique = false;
                break;
            }
        }
        if (!isUnique && !domain.getName().equalsIgnoreCase(name)) {
            JOptionPane.showMessageDialog(null, "This domain already exists!", "Editing error", JOptionPane.ERROR_MESSAGE);
            tfName.grabFocus();
            optionPane.setValue(JOptionPane.UNDEFINED_CONDITION);
            return;
        }
        domain.setName(name);
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
