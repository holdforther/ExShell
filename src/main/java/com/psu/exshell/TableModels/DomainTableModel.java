package com.psu.exshell.TableModels;

import com.psu.exshell.Knowledge.Domain;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DomainTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Name"};
    private final ArrayList<Domain> data;

    public DomainTableModel(ArrayList<Domain> data) {
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
        return data.get(row).getName();
    }

    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
}
