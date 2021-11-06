package com.psu.exshell.Application;

import com.psu.exshell.TableModels.DomainTableModel;
import com.psu.exshell.TableModels.RuleTableModel;
import com.psu.exshell.TableModels.VariableTableModel;
import com.psu.exshell.Knowledge.*;
import com.psu.exshell.Dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.ref.WeakReference;
import javax.swing.table.DefaultTableModel;


public class GUI {

    // Data and models
    private final State appState;
    private DomainTableModel domainTableModel;
    private VariableTableModel variableTableModel;
    private RuleTableModel ruleTableModel;

    // Interactive components
    private final JFrame frameMain;
    private final JMenuBar menuBar;
    private JTable tableDomains;
    private JTable tableVariables;
    private JTable tableRules;

    public GUI(State appState) {
        this.appState = appState;

        frameMain = new JFrame("ExShell");
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMain.setSize(800, 600);
        frameMain.setVisible(true);
        frameMain.setLocationRelativeTo(null);

        menuBar = new JMenuBar();
        var menu = new JMenu("File"); // You can set key with setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        var menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menu.add(menuItem);
        menuItem = new JMenuItem("Close", KeyEvent.VK_C);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Save", KeyEvent.VK_S);
        menu.add(menuItem);
        menuItem = new JMenuItem("Save as");
        menu.add(menuItem);
        menu = new JMenu("Expert");
        menuItem = new JMenuItem("Consult", KeyEvent.VK_C);
		menuItem.addActionListener(e -> showConsultWindow());
        menu.add(menuItem);
        menuBar.add(menu);

        var tabbedPane = createTabbedPane();

        frameMain.setJMenuBar(menuBar);
        frameMain.add(tabbedPane);
    }

    private JTabbedPane createTabbedPane() {
        var tabbedPane = new JTabbedPane();
        var panelRules = createRulesPanel();
        var panelVariables = createVariablesPanel();
        var panelDomains = createDomainsPanel();
        tabbedPane.addTab("Rules", panelRules);
        tabbedPane.addTab("Variables", panelVariables);
        tabbedPane.addTab("Domains", panelDomains);
        return tabbedPane;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    private JPanel createDomainsPanel() {
        // Init all elements
        var panelDomains = new JPanel();
        var hBoxTop = new JPanel();
        var hBoxBottom = new JPanel();
        var btnCreate = new JButton("Create");
        var btnUpdate = new JButton("Update");
        var btnDelete = new JButton("Delete");
        domainTableModel = new DomainTableModel(appState.getDomains());
        tableDomains = new JTable(domainTableModel);
        var panelTable = new JScrollPane(tableDomains);
        tableDomains.setFillsViewportHeight(true);
        // Set layout
        panelDomains.setLayout(new BorderLayout());
        hBoxTop.setLayout(new BoxLayout(hBoxTop, BoxLayout.X_AXIS));
        hBoxBottom.setLayout(new BoxLayout(hBoxBottom, BoxLayout.X_AXIS));
        // Add all elements
        var rigidAreaDim = new Dimension(5, 0);
        hBoxTop.add(btnCreate);
        hBoxTop.add(Box.createRigidArea(rigidAreaDim));
        hBoxTop.add(btnUpdate);
        hBoxTop.add(Box.createRigidArea(rigidAreaDim));
        hBoxTop.add(btnDelete);
        panelDomains.add(hBoxTop, BorderLayout.PAGE_START);
        panelDomains.add(panelTable, BorderLayout.CENTER);
        panelDomains.add(hBoxBottom, BorderLayout.PAGE_END);

        btnCreate.addActionListener(e -> showEditDomainDialog(Mode.CREATE));
        btnUpdate.addActionListener(e -> showEditDomainDialog(Mode.UPDATE));
        btnDelete.addActionListener(e -> deleteDomain());
        // Set alignment
        hBoxTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        hBoxBottom.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panelDomains;
    }

    private void showEditDomainDialog(Mode editMode) {
        var domainIdx = tableDomains.getSelectedRow();
        if (domainIdx == -1 && editMode == Mode.UPDATE) {
            return;
        }
        Domain domainCopy;
        if (domainIdx > -1 && editMode == Mode.UPDATE) {
            domainCopy = new Domain(appState.getDomains().get(domainIdx));
        } else {
            domainCopy = new Domain();
        }
        var dialogEditDomain = new EditDomain(frameMain, domainCopy, editMode, appState);
        if (dialogEditDomain.getDialogResult() == JOptionPane.OK_OPTION) {
            if (editMode == Mode.UPDATE) {
                var oldDomain = appState.getDomains().get(domainIdx);
                oldDomain.setName(domainCopy.getName());
                oldDomain.setValues(domainCopy.getValues());
            } else {
                appState.add(domainCopy);
            }
            domainTableModel.fireTableDataChanged();
        }
    }

    private void deleteDomain() {
        int domainIdx = tableDomains.getSelectedRow();
        if (domainIdx == -1) {
            return;
        }
        var domain = appState.getDomains().get(domainIdx);
        var isUsed = false;
        var sb = new StringBuilder();
        for (var variable : appState.getVariables()) {
            if (variable.getDomain().equals(domain)) {
                sb.append(variable).append(", ");
                isUsed = true;
            }
        }
        if (isUsed) {
            sb.delete(sb.length() - 2, sb.length() - 1);
            var msg = "Can't delete domain, it's being used in following variables: " + sb;
            JOptionPane.showMessageDialog(frameMain, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        appState.removeDomain(domainIdx);
        domainTableModel.fireTableDataChanged();
    }

    private JPanel createVariablesPanel() {
        var panelVariables = new JPanel();
        var hBoxTop = new JPanel();
        var hBoxBottom = new JPanel();
        var btnCreate = new JButton("Create");
        var btnUpdate = new JButton("Update");
        var btnDelete = new JButton("Delete");
        variableTableModel = new VariableTableModel(appState.getVariables());
        tableVariables = new JTable(variableTableModel);
        var panelTable = new JScrollPane(tableVariables);
        tableVariables.setFillsViewportHeight(true);
        // Set layout
        panelVariables.setLayout(new BorderLayout());
        hBoxTop.setLayout(new BoxLayout(hBoxTop, BoxLayout.X_AXIS));
        hBoxBottom.setLayout(new BoxLayout(hBoxBottom, BoxLayout.X_AXIS));
        // Add all elements
        var rigidAreaDim = new Dimension(5, 0);
        hBoxTop.add(btnCreate);
        hBoxTop.add(Box.createRigidArea(rigidAreaDim));
        hBoxTop.add(btnUpdate);
        hBoxTop.add(Box.createRigidArea(rigidAreaDim));
        hBoxTop.add(btnDelete);
        //hBoxBottom.add(question);
        panelVariables.add(hBoxTop, BorderLayout.PAGE_START);
        panelVariables.add(panelTable, BorderLayout.CENTER);
        panelVariables.add(hBoxBottom, BorderLayout.PAGE_END);
        btnCreate.addActionListener(e -> showEditVariableWindow(Mode.CREATE));
        btnUpdate.addActionListener(e -> showEditVariableWindow(Mode.UPDATE));
        btnDelete.addActionListener(e -> deleteVariable());
        // Set alignment
        hBoxTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        hBoxBottom.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panelVariables;
    }

    private void showEditVariableWindow(Mode editMode) {
        int variableIdx = tableVariables.getSelectedRow();
        if (variableIdx == -1 && editMode == Mode.UPDATE) {
            return;
        }
        Variable variableCopy;
        if (variableIdx > -1 && editMode == Mode.UPDATE) {
            variableCopy = new Variable(appState.getVariables().get(variableIdx));
        } else {
            variableCopy = new Variable();
        }
        var dialogEditVariable = new EditVariable(frameMain, variableCopy, appState);
        if (dialogEditVariable.getDialogResult() == JOptionPane.OK_OPTION) {
            if (editMode == Mode.UPDATE) {
                var oldVariable = appState.getVariables().get(variableIdx);
                oldVariable.setName(variableCopy.getName());
                oldVariable.setType(variableCopy.getType());
                oldVariable.setDomain(variableCopy.getDomain());
                oldVariable.setQuestion(variableCopy.getQuestion());
            } else {
                appState.add(variableCopy);
            }
            variableTableModel.fireTableDataChanged();
        }
    }

    private void deleteVariable() {
        int variableIdx = tableVariables.getSelectedRow();
        if (variableIdx < 0) {
            return;
        }
        var variable = appState.getVariables().get(variableIdx);
        var isUsed = false;
        var sb = new StringBuilder();
        eachRule: 
        for (var r : appState.getRules()) {
            for (var f : r.getPremise()) {
                if (f.getVariable().equals(variable)) {
                    isUsed = true;
                    sb.append(r).append(", ");
                    continue eachRule;
                }
            }
            if (r.getConclusion().getVariable().equals(variable)) {
                isUsed = true;
                sb.append(r).append(", ");
            }
        }
        if (isUsed) {
            sb.delete(sb.length() - 2, sb.length() - 1);
            var msg = "Can't delete variable, it's being used in following rules: " + sb;
            JOptionPane.showMessageDialog(frameMain, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        appState.removeVariable(variableIdx);
        variableTableModel.fireTableDataChanged();
    }

    public JPanel createRulesPanel() {
        var panelRules = new JPanel();
        var hBoxTop = new JPanel();
        var hBoxBottom = new JPanel();
        var createButton = new JButton("Create");
        var updateButton = new JButton("Update");
        var deleteButton = new JButton("Delete");
        ruleTableModel = new RuleTableModel(appState.getRules());
        tableRules = new JTable(ruleTableModel);
		var dndMouseHandler = new MouseHandler(tableRules, ruleTableModel);
		tableRules.addMouseMotionListener(dndMouseHandler);
		tableRules.addMouseListener(dndMouseHandler);
        var scrollTable = new JScrollPane(tableRules);
        // Set layout
        panelRules.setLayout(new BorderLayout());
        hBoxTop.setLayout(new BoxLayout(hBoxTop, BoxLayout.X_AXIS));
        hBoxBottom.setLayout(new BoxLayout(hBoxBottom, BoxLayout.X_AXIS));
        // Add all elements
        Dimension rigidAreaDim = new Dimension(5, 0);
        hBoxTop.add(createButton);
        hBoxTop.add(Box.createRigidArea(rigidAreaDim));
        hBoxTop.add(updateButton);
        hBoxTop.add(Box.createRigidArea(rigidAreaDim));
        hBoxTop.add(deleteButton);
        panelRules.add(hBoxTop, BorderLayout.PAGE_START);
        panelRules.add(scrollTable, BorderLayout.CENTER);
        panelRules.add(hBoxBottom, BorderLayout.PAGE_END);
        createButton.addActionListener(e -> showEditRuleWindow(Mode.CREATE));
        updateButton.addActionListener(e -> showEditRuleWindow(Mode.UPDATE));
        deleteButton.addActionListener(e -> deleteRule());
        // Set alignment
        hBoxTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        hBoxBottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableRules.setFillsViewportHeight(true);

        return panelRules;
    }

    private void showEditRuleWindow(Mode editMode) {
        int ruleIdx = tableRules.getSelectedRow();
        if (ruleIdx == -1 && editMode == Mode.UPDATE) {
            return;
        }
        Rule ruleCopy;
        if (ruleIdx > -1 && editMode == Mode.UPDATE) {
            ruleCopy = new Rule(appState.getRules().get(ruleIdx));
        } else {
            ruleCopy = new Rule();
        }
        var dialogEditRule = new EditRule(frameMain, ruleCopy, editMode, appState);
        if (dialogEditRule.getDialogResult() == JOptionPane.OK_OPTION) {
            if (editMode == Mode.UPDATE) {
                var oldRule = appState.getRules().get(ruleIdx);
                oldRule.setName(ruleCopy.getName());
                oldRule.setPremise(ruleCopy.getPremise());
                oldRule.setConclusion(ruleCopy.getConclusion());
                oldRule.setExplanation(ruleCopy.getExplanation());
            } else {
                appState.add(ruleCopy);
            }
            ruleTableModel.fireTableDataChanged();
        }
    }

    private void deleteRule() {
        int ruleIdx = tableRules.getSelectedRow();
        if (ruleIdx > -1) {
            appState.removeRule(ruleIdx);
            ruleTableModel.fireTableDataChanged();
        }
    }

    public void updateTables() {
        domainTableModel.fireTableDataChanged();
        variableTableModel.fireTableDataChanged();
        ruleTableModel.fireTableDataChanged();
    }
	
	private void showConsultWindow() {
		Variable goal;
		var dialogSelectGoal = new SelectGoal(frameMain, appState);
		if (dialogSelectGoal.getDialogResult() != JOptionPane.OK_OPTION) {
			return;
		}
		goal = dialogSelectGoal.getGoal();
		
		var panelQuestion = new JPanel(new GridLayout(2, 1));
		var labelQuestion = new JLabel();
		var cbValue = new JComboBox<Value>();
		panelQuestion.add(labelQuestion);
		panelQuestion.add(cbValue);
		
		var consulter = new Consulter(goal, appState);
		Mode inferrerState;
		while ((inferrerState = consulter.conclude()) == Mode.NEED_QUESTION) {
			var nextVar = consulter.getNextVariableToInit();
			var question = nextVar.getQuestion();
			if (question.equals("?")) {
				labelQuestion.setText(nextVar.getName() + "?");
			} else {
				labelQuestion.setText(question);
			}
			cbValue.removeAllItems();
			nextVar.getDomain().getValues().forEach(v -> cbValue.addItem(v));
			panelQuestion.updateUI();
			JOptionPane.showMessageDialog(frameMain, panelQuestion, "Consulting", JOptionPane.OK_OPTION);
			var selectedValue = (Value) cbValue.getSelectedItem();
			consulter.setValue(nextVar, selectedValue, null);
			System.out.println(selectedValue);
		}
		// Explanation
		String message;
		if (inferrerState == Mode.GOAL_INFERRED) {
			message = consulter.getValue(goal).toString();
		} else {
			message = "Goal cannot be inferred";
		}
		JOptionPane.showMessageDialog(frameMain, "Result: " + message);
		
		var dialogExplanation = new Explanation(frameMain, message, consulter.getHierarchy(), consulter.getValuedVariables());
	}
	
	private static class MouseHandler implements MouseListener, MouseMotionListener {

		private Integer row = null;

		private final WeakReference<JTable> table;
		private final WeakReference<RuleTableModel> tableModel;

		public MouseHandler(JTable table, RuleTableModel model) {
			this.table = new WeakReference<>(table);
			this.tableModel = new WeakReference<>(model);
		}

		@Override
		public void mouseClicked(MouseEvent event) {
		}

		@Override
		public void mousePressed(MouseEvent event) {
			JTable table;
			if ((table = this.table.get()) == null) {
				return;
			}
			int viewRowIndex = table.rowAtPoint(event.getPoint());
			row = table.convertRowIndexToModel(viewRowIndex);
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			row = null;
		}

		@Override
		public void mouseEntered(MouseEvent event) {
		}

		@Override
		public void mouseExited(MouseEvent event) {
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			JTable table;
			RuleTableModel tableModel;
			if ((table = this.table.get()) == null || (tableModel = this.tableModel.get()) == null) {
				return;
			}

			int viewRowIndex = table.rowAtPoint(event.getPoint());
			int currentRow = table.convertRowIndexToModel(viewRowIndex);

			if (row == null || currentRow == row || currentRow == -1) {
				return;
			}
			tableModel.reorder(row, currentRow);
			row = currentRow;
			table.setRowSelectionInterval(viewRowIndex, viewRowIndex);
		}

		@Override
		public void mouseMoved(MouseEvent event) {
		}
	}
}
