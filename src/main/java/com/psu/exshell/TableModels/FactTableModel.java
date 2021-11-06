package com.psu.exshell.TableModels;

import com.psu.exshell.Knowledge.Fact;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class FactTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Description"};
    private final ArrayList<Fact> data;

    public FactTableModel(ArrayList<Fact> data) {
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
        var fact = data.get(row);
        return fact.getVariable() + " = " + fact.getValue();
    }
    
    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
}
