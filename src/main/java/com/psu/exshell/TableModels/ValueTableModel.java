package com.psu.exshell.TableModels;

import com.psu.exshell.Knowledge.Value;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ValueTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Value"};
    private final ArrayList<Value> data;

    public ValueTableModel(ArrayList<Value> data) {
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
        return data.get(row).getValue();
    }
    
    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
}
