package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

public class Principal extends JFrame {

	// Numbers variables
    private int width; //width of the window
    private int height; //height of the window
    private double sizeRate; // Rate size of the window
    
	public Principal(ResourceBundle messages)
    {
        // Initialize variables
        String title = messages.getString("title");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        sizeRate = 0.9;
        width  = (int) (Math.round(dim.width*sizeRate));
        height = (int) (Math.round(dim.height*sizeRate));

        // Setting up the icon
        URL url = this.getClass().getResource("/resources/icon.png");    
        Image icon = Toolkit.getDefaultToolkit().getImage(url);    
        this.setIconImage(icon);        
        
        // Close operation
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Initialize the window with 80% of the screen resolution
        this.setSize(width, height);
        // Title of the window
        this.setTitle(title);
        // Border Layout for the components
        this.setLayout(new BorderLayout());
        // Initialize the window in the center of the monitor
        this.setLocationRelativeTo(null);  
        // Add the menu bar
        addMenuBarToPane(this,messages);
        // Add the tool bar
        addToolBarToPane(this,messages);
        // Pop-up the main window
        this.setVisible(true);
    }
		
    /**
     * Add a menu bar to the program
     * 
     * @param frame Container that will receive the components
     * @param messages Variable for internationalization
     */    
    private void addMenuBarToPane(JFrame frame, ResourceBundle messages)
    {
        // Setting up the menu bar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        // Setting the menu categories
        JMenu fileMenu = new JMenu(messages.getString("MenuFile"));
        JMenu helpMenu = new JMenu(messages.getString("MenuHelp"));
        JMenu toolMenu = new JMenu(messages.getString("MenuTools"));
        menuBar.add(fileMenu);
        menuBar.add(toolMenu);
        menuBar.add(helpMenu);
        // Setting the menu items
        JMenuItem exitAction = new JMenuItem(messages.getString("MenuItemExit"));
        JMenuItem aboutAction = new JMenuItem(messages.getString("MenuItemAbout"));
        JMenuItem helpAction = new JMenuItem(messages.getString("MenuItemHelp"));
        JMenuItem ConfigurationAction = new JMenuItem(messages.getString("MenuItemConfiguration"));
        // Setting up the adding
        fileMenu.add(exitAction);
        toolMenu.add(ConfigurationAction);
        helpMenu.add(aboutAction);
        helpMenu.add(helpAction);
    }
    
    /**
     * Add a tool bar to the program
     * 
     * @param frame Container that will receive the components
     * @param messages Variable for internationalization
     */    
    private void addToolBarToPane(JFrame frame, ResourceBundle messages)
    {
    	// Create the tool bar
    	JToolBar toolBar = new JToolBar();
    	toolBar.setFloatable(false);
    	toolBar.setRollover(true);
    	Container contentPane = frame.getContentPane();
    	contentPane.add(toolBar, BorderLayout.NORTH);
    	
    	JButton button = null;
    	Icon icone = null;
    	// Play button
    	icone = new ImageIcon(getClass().getResource("/resources/play.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        // Pause button
    	icone = new ImageIcon(getClass().getResource("/resources/pause.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        // Stop button
    	icone = new ImageIcon(getClass().getResource("/resources/stop.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        
        // Add a separator
        toolBar.addSeparator();
        
        // First Cycle
        icone = new ImageIcon(getClass().getResource("/resources/first.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        // Previous Cycle
        icone = new ImageIcon(getClass().getResource("/resources/previous.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        // Next Cycle
        icone = new ImageIcon(getClass().getResource("/resources/next.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        // Last Cycle
        icone = new ImageIcon(getClass().getResource("/resources/last.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
        
        // Add a Separator
        toolBar.addSeparator();
        
        // Configuration
        icone = new ImageIcon(getClass().getResource("/resources/configuration.png"));
        button = new JButton();
        button.setIcon(icone);
        toolBar.add(button);
    }
}















