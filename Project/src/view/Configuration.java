/**
 * 
 */
package view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Danielson Flávio Xavier da Silva
 * Class for configuration window
 *
 */
public class Configuration extends JFrame {

	/**
	 * Need for extension of JFrame
	 */
	private static final long serialVersionUID = 2L;
	// Numbers variables
    private int width; //width of the window
    private int height; //height of the window
    private ButtonsListenerConfiguration buttonsAction; // Class for actions of the buttons
    private List<JTextField> texts; // List for the JTextFields
    
	/**
	 * Get the list of jtextfields
	 * @return the jtextfields
	 */
	public List<JTextField> getTexts() {
		return texts;
	}

	private static Configuration instance;
	
	public static synchronized Configuration getInstance(ResourceBundle messages) {
		if (instance == null) {
			instance = new Configuration(messages);
		}
		return instance;
	}
	
	private Configuration(ResourceBundle messages) {
		//Size of the window
		this.width = 700;
		this.height = 650;
		this.setSize(width, height);
        // Initialize the window in the center of the monitor
        this.setLocationRelativeTo(null);  
		//Gets the actions for the buttons
        buttonsAction = new ButtonsListenerConfiguration(messages, this);
        // List for the JTextFields
        texts = new ArrayList<JTextField>();
        // Initialize the window with 80% of the screen resolution
        this.setSize(width, height);
        //this.setLayout(new GridLayout(0,2));
        // Set the FlowLayout
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        // Set the default visible for the window
        this.seteVisible(false);
        // Disappear the buttons of the window
        this.setUndecorated(true);
        // Set the fields in the window
        this.setFields(this, messages);
        // Take the options away
        
	}
	
	/**
	 * Sets the window visible or note
	 * @param decision true if turn the window visible and false otherwise
	 */
	public void seteVisible(boolean decision) {
        // Pop-up the configuration window
        this.setVisible(decision);
	}

	/**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path Where the icon is located
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
	 * @param messages Variable for internationalization
	 */
	public void setFields(JFrame frame, ResourceBundle messages) {
    	Container contentPane = frame.getContentPane();
    	double[] param1 = {1000, 0.1320, 1.995, 2.375, 4.7};
    	contentPane.add(createElement(messages,5,"group1", param1));
    	double[] param2 = {1000, 0.1320, 4.95, 5.5, 15.5};
    	contentPane.add(createElement(messages,5,"group2", param2));
    	double[] param3 = {0.75};
    	contentPane.add(createElement(messages,1,"group3", param3));
    	double[] param4 = {45};
    	contentPane.add(createElement(messages,1,"group4", param4));
    	double[] param5 = {50, 2.3, 26, 596};
    	contentPane.add(createElement(messages,4,"group5", param5));
    	double[] param6 = {90, 0.45, 4.48, 1.95};
    	contentPane.add(createElement(messages,4,"group6", param6));
    	double[] param7 = {0.5, 37, 1.01, 0.8, 1.21};
    	contentPane.add(createElement(messages,5,"group7", param7));
    	double[] param8 = {600, 585, 60, 15.3, 90};
    	contentPane.add(createElement(messages,5,"group8", param8));
    	double[] param9 = {10000, 1000, 10000, 10000, 10000, 0};
    	contentPane.add(createElement(messages,6,"group9", param9));
    	double[] param10 = {10, 10, 10, 10, 10, 10};
    	contentPane.add(createElement(messages,6,"group10", param10));
    	
        JButton buttonCancel = new JButton(messages.getString("buttonConfigOut"));
        buttonCancel.putClientProperty("id", 2);
        buttonCancel.addMouseListener(buttonsAction);
        buttonCancel.setPreferredSize(new Dimension(200, 40));
    	
    	JButton buttonOK = new JButton(messages.getString("buttonConfigOK"));
        buttonOK.putClientProperty("id", 1);
        buttonOK.addMouseListener(buttonsAction);
        buttonOK.setPreferredSize(new Dimension(200, 40));
        
        contentPane.add(buttonCancel);
        contentPane.add(buttonOK);

	}
	
	/**
	 * Create an element for the configuration
	 * @param messages internationalization
	 * @param fields Parameter for how many fields to create
	 * @param title Title of groupbox
	 * @param p values to put in the text fields
	 * @return panel with element parameters
	 */
	private Component createElement(ResourceBundle messages, int fields, String title, double[] p) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString(title)));
		// Labels and Values
		int sizeTextField = 10;
		for (int i = 1; i <= fields; i++) {
			JLabel label = new JLabel(messages.getString(title+"opt"+i), JLabel.LEFT);
			JTextField field = new JTextField(sizeTextField);
			field.setText(String.valueOf(p[i-1]));
			field.setHorizontalAlignment(JTextField.CENTER);
			panel.add(label);
			panel.add(field);
			texts.add(field);
		}		
		return panel;
	}

	/**
	 * Create the plunger parameters
	 * @param messages internationalization
	 * @return panel with the plunger parameters
	 */
	/*private Component createPlunger(ResourceBundle messages) {
		return null;
	}*/

	/**
	 * Create the reservoir parameters
	 * @param messages internationalization
	 * @return panel with the reservoir parameters
	 */
	/*private Component createReservoir(ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString("group5")));
		// Labels
		JLabel label1 = new JLabel(messages.getString("group5opt1"), JLabel.LEFT);
		JLabel label2 = new JLabel(messages.getString("group5opt2"), JLabel.LEFT);
		JLabel label3 = new JLabel(messages.getString("group5opt3"), JLabel.LEFT);
		JLabel label4 = new JLabel(messages.getString("group5opt4"), JLabel.LEFT);
		// Values
		int sizeTextField = 10;
		JTextField field1 = new JTextField(sizeTextField);
		JTextField field2 = new JTextField(sizeTextField);
		JTextField field3 = new JTextField(sizeTextField);
		JTextField field4 = new JTextField(sizeTextField);
		// Adding
		panel.add(label1);
		panel.add(field1);
		panel.add(label2);
		panel.add(field2);
		panel.add(label3);
		panel.add(field3);
		panel.add(label4);
		panel.add(field4);
		
		return panel;
	}*/
	
	/**
	 * Create the production line parameters
	 * @param messages internationalization
	 * @return panel with the production line parameters
	 */
	/*private Component createProductionLine(ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString("group4")));
		// Labels
		JLabel label1 = new JLabel(messages.getString("group4opt1"), JLabel.LEFT);
		// Values
		int sizeTextField = 10;
		JTextField field1 = new JTextField(sizeTextField);
		// Adding
		panel.add(label1);
		panel.add(field1);
		
		return panel;
	}*/

	/**
	 * Create the motor valve parameters
	 * @param messages internationalization
	 * @return panel with the motor valve parameters
	 */
	/*private Component createMotorValve(ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString("group3")));
		// Labels
		JLabel label1 = new JLabel(messages.getString("group3opt1"), JLabel.LEFT);
		// Values
		int sizeTextField = 10;
		JTextField field1 = new JTextField(sizeTextField);
		// Adding
		panel.add(label1);
		panel.add(field1);
		
		return panel;
	}*/

	/**
	 * Create the casing parameters
	 * @param messages Internationalization
	 * @return panel with casing parameters
	 */
	/*private Component createCasing(ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString("group2")));
		// Labels
		JLabel label1 = new JLabel(messages.getString("group2opt1"), JLabel.LEFT);
		JLabel label2 = new JLabel(messages.getString("group2opt2"), JLabel.LEFT);
		JLabel label3 = new JLabel(messages.getString("group2opt3"), JLabel.LEFT);
		JLabel label4 = new JLabel(messages.getString("group2opt4"), JLabel.LEFT);
		JLabel label5 = new JLabel(messages.getString("group2opt5"), JLabel.LEFT);
		// Values
		int sizeTextField = 10;
		JTextField field1 = new JTextField(sizeTextField);
		JTextField field2 = new JTextField(sizeTextField);
		JTextField field3 = new JTextField(sizeTextField);
		JTextField field4 = new JTextField(sizeTextField);
		JTextField field5 = new JTextField(sizeTextField);
		// Adding
		panel.add(label1);
		panel.add(field1);
		panel.add(label2);
		panel.add(field2);
		panel.add(label3);
		panel.add(field3);
		panel.add(label4);
		panel.add(field4);
		panel.add(label5);
		panel.add(field5);
		
		return panel;
	}*/

	/**
	 * Create the tubing parameters
	 * @param messages Internationalization
	 * @return Panel with the parameters
	 */
	/*private Component createTubing(ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createTitledBorder(messages.getString("group1")));
		// Labels
		JLabel label1 = new JLabel(messages.getString("group1opt1"), JLabel.LEFT);
		JLabel label2 = new JLabel(messages.getString("group1opt2"), JLabel.LEFT);
		JLabel label3 = new JLabel(messages.getString("group1opt3"), JLabel.LEFT);
		JLabel label4 = new JLabel(messages.getString("group1opt4"), JLabel.LEFT);
		JLabel label5 = new JLabel(messages.getString("group1opt5"), JLabel.LEFT);
		// Values
		int sizeTextField = 10;
		JTextField field1 = new JTextField(sizeTextField);
		JTextField field2 = new JTextField(sizeTextField);
		JTextField field3 = new JTextField(sizeTextField);
		JTextField field4 = new JTextField(sizeTextField);
		JTextField field5 = new JTextField(sizeTextField);
		// Adding
		panel.add(label1);
		panel.add(field1);
		panel.add(label2);
		panel.add(field2);
		panel.add(label3);
		panel.add(field3);
		panel.add(label4);
		panel.add(field4);
		panel.add(label5);
		panel.add(field5);
		
		return panel;
	}*/

}













