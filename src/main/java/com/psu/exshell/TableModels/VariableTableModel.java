package com.psu.exshell.TableModels;

import com.psu.exshell.Knowledge.Variable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class VariableTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Name", "Type", "Domain"};
    private final ArrayList<Variable> data;

    public VariableTableModel(ArrayList<Variable> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public Object getValueAt(int row, int col) {
        var variable = data.get(row);
        switch (col) {
            case 0:
                return variable.getName();
            case 1:
                return variable.getType().toString();
            default:
                return variable.getDomain().toString();
        }
    }
    
    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
}
