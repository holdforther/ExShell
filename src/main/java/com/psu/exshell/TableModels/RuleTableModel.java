package com.psu.exshell.TableModels;

import com.psu.exshell.Knowledge.Rule;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class RuleTableModel extends AbstractTableModel implements Reorderable{

    private final String[] columnNames = {"Name", "Description"};
    private final ArrayList<Rule> data;

    public RuleTableModel(ArrayList<Rule> data) {
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
        var rule = data.get(row);
        if (col == 0) {
            return rule.getName();
        } else {
            var sb = new StringBuilder("IF ");
            rule.getPremise().forEach(fact -> sb.append(fact.toString()).append(" AND "));
            // Delete last " AND "
            sb.delete(sb.length() - 5, sb.length() - 1);
			sb.append(" THEN ");
			sb.append(rule.getConclusion().toString());
            return sb.toString();
			//return rule.getExplanation();
        }
    }
    
    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
	
	@Override
	public void reorder(int fromIndex, int toIndex) {
		Rule from = data.get(fromIndex);
        toIndex = Math.min(toIndex, data.size() - 1);
        Rule to = data.get(toIndex);
        data.set(fromIndex, to);
        data.set(toIndex, from);
	}
}
