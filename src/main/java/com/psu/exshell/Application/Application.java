package com.psu.exshell.Application;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Application {
	private static State state;
	private static String savePath;
	private static GUI gui;

	private static void saveInstance(boolean isSaveAs) {
		if (savePath == null || isSaveAs) {
			var fileChooser = new JFileChooser(".");
			fileChooser.setDialogTitle("Select savefile");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("ExShell files (.exsh)", "exsh"));
			var option = fileChooser.showOpenDialog(null);
			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			} else {
				savePath = fileChooser.getSelectedFile().getAbsolutePath();
				var splitted = savePath.split("\\.");
				if (!splitted[splitted.length - 1].equals("exsh")) {
					savePath += ".exsh";
				}
			}
		}
		state.save(savePath);
	}

	private static void restoreInstance() {
		var fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle("Select savefile");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter("ExShell files (.exsh)", "exsh"));
		var option = fileChooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			savePath = fileChooser.getSelectedFile().getAbsolutePath();
			state.load(savePath);
		}
		gui.updateTables();
	}

	private static void clearInstance() {
		state.clear();
		savePath = null;
		gui.updateTables();
	}

	private static void createAndShowGUI() {
		state = new State();
		gui = new GUI(state);
		var menu = gui.getMenuBar().getMenu(0);
		menu.getItem(0).addActionListener(e -> restoreInstance());
		menu.getItem(1).addActionListener(e -> clearInstance());
		menu.getItem(3).addActionListener(e -> saveInstance(false));
		menu.getItem(4).addActionListener(e -> saveInstance(true));
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException | ClassNotFoundException |
				InstantiationException | IllegalAccessException e) {
			System.out.println(e.getMessage());
		}
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(Application::createAndShowGUI);
	}
}
