/**
 * 
 */
package view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * @author Danielson Flávio Xavier da Silva Class for configuration window
 *
 */
public class Configuration extends JFrame {

	/**
	 * Need for extension of JFrame
	 */
	private static final long serialVersionUID = 2L;
	// Numbers variables
	private int width; // width of the window
	private int height; // height of the window
	private ButtonsListenerConfiguration buttonsAction; // Class for actions of
														// the buttons
	private List<JTextField> texts; // List for the JTextFields
	private JRadioButton controller1; // Variable for controller 1
	private JRadioButton controller2; // Variable for controller 2
	private JRadioButton controller3; // Variable for controller 3
	private ResourceBundle messages; // Variable for selected languages
	private JPanel controllerOptions; // Panel with options for the controller
	private int typeController; // Store the type of controller chose by user

	/**
	 * Get the list of jtextfields
	 * 
	 * @return the jtextfields
	 */
	public List<JTextField> getTexts() {
		return texts;
	}
	
	public int getTypeController() {
		return this.typeController;
	}

	private static Configuration instance;

	public static synchronized Configuration getInstance(ResourceBundle messages) {
		if (instance == null) {
			instance = new Configuration(messages);
		}
		return instance;
	}

	private Configuration(ResourceBundle messages) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		double sizeRate = 0.7;
		width = (int) (Math.round(dim.width * sizeRate));
		height = (int) (Math.round(dim.height * sizeRate));
		this.messages = messages;
		// Gets the actions for the buttons
		buttonsAction = new ButtonsListenerConfiguration(messages, this);
		// List for the JTextFields
		texts = new ArrayList<JTextField>();
		// Initialize the window with 80% of the screen resolution
		this.setSize(width, height);
		// Initialize the window in the center of the monitor
		this.setLocationRelativeTo(null);
		// this.setLayout(new GridLayout(0,2));
		// Set the FlowLayout
		//this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setLayout(new GridLayout(0, 2));
		// Set the default visible for the window
		this.seteVisible(false);
		// Disappear the buttons of the window
		this.setUndecorated(false);
		// Set the fields in the window
		Container contentPane = this.getContentPane();
		// JScrollPane jsp = new
		// JScrollPane(contentPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		// JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setFields(this, messages);
		// Controller options
		contentPane.add(createControllerOptions(messages));
		// create panel for controller options
		controllerOptions = new JPanel();
		// Buttons OK and Cancel
		contentPane.add(createButtons(messages));		
	}

	/**
	 * Sets the window visible or note
	 * 
	 * @param decision
	 *            true if turn the window visible and false otherwise
	 */
	public void seteVisible(boolean decision) {
		// Pop-up the configuration window
		this.setVisible(decision);
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 * 
	 * @param path
	 *            Where the icon is located
	 * @return The image icon located in path
	 */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Configuration.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the fields within the window
	 * 
	 * @param messages
	 *            Variable for internationalization
	 */
	public void setFields(JFrame frame, ResourceBundle messages) {
		Container contentPane = frame.getContentPane();
		//double[] param1 = { 1000, 0.1320, 1.995, 2.375, 4.7 };
		double[] param1 = { 1176, 0.25908, 1.995, 2.375, 4.7 };
		contentPane.add(createElement(messages, 5, "group1", param1));
		double[] param2 = { 1176, 0.1320, 4.95, 5.5, 15.5 };
		contentPane.add(createElement(messages, 5, "group2", param2));
		double[] param3 = { 0.75 };
		contentPane.add(createElement(messages, 1, "group3", param3));
		//double[] param4 = { 45 };
		double[] param4 = { 70 };
		contentPane.add(createElement(messages, 1, "group4", param4));
		//double[] param5 = { 50, 2.3, 26, 596 };
		double[] param5 = { 61.4, 2.3, 26, 949 };
		contentPane.add(createElement(messages, 4, "group5", param5));
		//double[] param6 = { 90, 0.45, 4.48, 1.95 };
		double[] param6 = { 90, 0.45, 3.6, 1.95 };
		contentPane.add(createElement(messages, 4, "group6", param6));
		//double[] param7 = { 0.5, 37, 1.01, 0.8, 1.21 };
		double[] param7 = { 0, 45, 1.07, 0.75, 1.21 };
		contentPane.add(createElement(messages, 5, "group7", param7));
		//double[] param8 = { 600, 585, 60, 15.3, 90 };
		double[] param8 = { 600, 585, 54, 15.3, 366 };
		contentPane.add(createElement(messages, 5, "group8", param8));
		double[] param9 = { 10000, 1000, 10000, 10000, 10000, 0 };
		contentPane.add(createElement(messages, 6, "group9", param9));
		double[] param10 = { 10, 10, 10, 10, 10, 10 };
		contentPane.add(createElement(messages, 6, "group10", param10));
	}

	/**
	 * Create an element for the configuration
	 * 
	 * @param messages
	 *            internationalization
	 * @param fields
	 *            Parameter for how many fields to create
	 * @param title
	 *            Title of groupbox
	 * @param p
	 *            values to put in the text fields
	 * @return panel with element parameters
	 */
	private Component createElement(ResourceBundle messages, int fields, String title, double[] p) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 4));
		// panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString(title)));
		// Labels and Values
		int sizeTextField = 3;
		for (int i = 1; i <= fields; i++) {
			JLabel label = new JLabel(messages.getString(title + "opt" + i), JLabel.LEFT);
			JTextField field = new JTextField();
			field.setColumns(sizeTextField);
			field.setText(String.valueOf(p[i - 1]));
			field.setHorizontalAlignment(JTextField.CENTER);
			panel.add(label);
			panel.add(field);
			texts.add(field);
		}
		return panel;
	}

	/**
	 * Create the controller options inside Configuration window
	 * 
	 * @param messages
	 *            Labels of the options
	 * @return Component with the controller options up-to-date
	 */
	private Component createControllerOptions(ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString("controllerGroup")));

		RadioButtonHandler handler = new RadioButtonHandler();

		JLabel myLabel = new JLabel(messages.getString("controllerQuestion"), JLabel.LEFT);
		controller1 = new JRadioButton(messages.getString("controller1"), false);
		controller2 = new JRadioButton(messages.getString("controller2"), false);
		controller3 = new JRadioButton(messages.getString("controller3"), false);

		panel.add(myLabel);
		panel.add(controller1);
		panel.add(controller2);
		panel.add(controller3);

		controller1.addItemListener(handler);
		controller2.addItemListener(handler);
		controller3.addItemListener(handler);

		ButtonGroup groupOptions = new ButtonGroup();
		groupOptions.add(controller1);
		groupOptions.add(controller2);
		groupOptions.add(controller3);

		return panel;
	}

	private class RadioButtonHandler implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent event) {
			if (controller1.isSelected()) {
				createControllerOptions(1);
				typeController = 1;
			}
			if (controller2.isSelected()) {
				createControllerOptions(2);
				typeController = 2;
			}
			if (controller3.isSelected()) {
				createControllerOptions(3);
				typeController = 3;
			}
		}
	}

	/**
	 * Creates the buttons OK and Cancel
	 * 
	 * @param messages
	 *            Internationalization of software
	 * @return Button created
	 */
	private Component createButtons(ResourceBundle messages) {
		JButton buttonOK = new JButton(messages.getString("buttonConfigOK"));
		buttonOK.putClientProperty("id", 1);
		buttonOK.addMouseListener(buttonsAction);
		buttonOK.setPreferredSize(new Dimension(200, 40));
		
		JButton buttonCancel = new JButton(messages.getString("buttonConfigOut"));
		buttonCancel.putClientProperty("id", 2);
		buttonCancel.addMouseListener(buttonsAction);
		buttonCancel.setPreferredSize(new Dimension(200, 40));
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		panel.add(buttonOK);
		panel.add(buttonCancel);

		return panel;
	}

	/**
	 * Create a component for setting the controller options
	 * 
	 * @param option
	 *            An identification for which controller is selected
	 * @return A component with text fields and labels for setting the
	 *         controller options
	 */
	private void createControllerOptions(int option) {
		Container contentPane = this.getContentPane();
		controllerOptions.removeAll();
		controllerOptions.setLayout(new GridLayout(0, 2));
		controllerOptions.setBorder(BorderFactory.createTitledBorder(messages.getString("optController")));
		switch (option) {
		case 1:
			for (int i = 1; i <= 3; i++) {
				JLabel label = new JLabel(messages.getString("controller1" + "opt" + i), JLabel.LEFT);
				JTextField field = new JTextField(5);
				field.setHorizontalAlignment(JTextField.CENTER);
				controllerOptions.add(label);
				controllerOptions.add(field);
				texts.add(field);
			}
			contentPane.add(controllerOptions);
			controllerOptions.setVisible(true);
			break;
		case 2:
			for (int i = 1; i <= 5; i++) {
				JLabel label = new JLabel(messages.getString("controller2" + "opt" + i), JLabel.LEFT);
				JTextField field = new JTextField(5);
				field.setHorizontalAlignment(JTextField.CENTER);
				controllerOptions.add(label);
				controllerOptions.add(field);
				texts.add(field);
			}
			contentPane.add(controllerOptions);
			controllerOptions.setVisible(true);
			break;
		case 3:
			controllerOptions.removeAll();
			controllerOptions.setVisible(false);
			break;
		default:
			break;
		}
		contentPane.revalidate();
	}

}
