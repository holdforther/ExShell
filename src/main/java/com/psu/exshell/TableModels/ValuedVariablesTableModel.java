package com.psu.exshell.TableModels;

import com.psu.exshell.Knowledge.Value;
import com.psu.exshell.Knowledge.Variable;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class ValuedVariablesTableModel extends AbstractTableModel {

	private final String[] columnNames = {"Name", "Value"};
	private final ArrayList<Variable> variables;
	private final ArrayList<Value> values;
	
	public ValuedVariablesTableModel(ArrayList<Variable> variables, ArrayList<Value> values) {
		this.variables = variables;
		this.values = values;
	}

	@Override
	public int getRowCount() {
		return variables.size();
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
		switch (col) {
			case 0:
				return variables.get(row).getName();
			case 1:
				return values.get(row).toString();
			default:
				return values.get(row).toString();
		}
	}

	@Override
	public Class getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}
}
