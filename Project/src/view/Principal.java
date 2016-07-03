package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import algorithm.Conversion;

/**
 * @author Danielson Flávio Xavier da Silva
 */
public class Principal extends JFrame {

	private static final long serialVersionUID = 1L;
	// Numbers variables
    private int width; //width of the window
    private int height; //height of the window
    private double sizeRate; // Rate size of the window
    private double previoustime; // Store the last time ploted into chart
    private int counttimes; //counts the time for printing into chart
    private Map<String,Double> series; // Store several series for the chart
    private JButton buttons[]; // Reference to the buttons
    private ButtonsListenerPrincipal buttonsAction; // Class for actions of the buttons
    private JFreeChart chart; // Chart to plot variables
    private XYSeriesCollection dataset; // Sets of data for the chart
    private Conversion conv;
    PrintWriter file;
    
	private static Principal instance;
	
	public static synchronized Principal getInstance(ResourceBundle messages) {
		if (instance == null) {
			instance = new Principal(messages);
		}
		return instance;
	}	
    
    /**
     * Constructor
     * @param messages Variable for internationalization
     */
	private Principal(ResourceBundle messages)
    {
        // Initialize variables
		previoustime = 0;
		counttimes = 0;
        String title = messages.getString("title");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        sizeRate = 0.9;
        width  = (int) (Math.round(dim.width*sizeRate));
        height = (int) (Math.round(dim.height*sizeRate));
        buttonsAction = new ButtonsListenerPrincipal(messages);
        buttons = new JButton[8];
        conv = new Conversion();
        // Setting up the icon
        URL url = this.getClass().getResource("/resources/icon.png");    
        Image icon = Toolkit.getDefaultToolkit().getImage(url);    
        this.setIconImage(icon);        
        try {
			file = new PrintWriter("the-file-name.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
        // Add the Chart
        addChartToPane(this,messages);
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
        button.putClientProperty("id", 1);
        button.addMouseListener(buttonsAction);
        buttons[0] = button;
        toolBar.add(button);
        // Pause button
    	icone = new ImageIcon(getClass().getResource("/resources/pause.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 2);
        button.addMouseListener(buttonsAction);
        buttons[1] = button;
        toolBar.add(button);
        // Stop button
    	icone = new ImageIcon(getClass().getResource("/resources/stop.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 3);
        button.addMouseListener(buttonsAction);
        buttons[2] = button;
        toolBar.add(button);
        
        // Add a separator
        toolBar.addSeparator();
        
        // First Cycle
        icone = new ImageIcon(getClass().getResource("/resources/first.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 4);
        button.addMouseListener(buttonsAction);
        buttons[3] = button;
        toolBar.add(button);
        // Previous Cycle
        icone = new ImageIcon(getClass().getResource("/resources/previous.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 5);
        button.addMouseListener(buttonsAction);
        buttons[4] = button;
        toolBar.add(button);
        // Next Cycle
        icone = new ImageIcon(getClass().getResource("/resources/next.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 6);
        button.addMouseListener(buttonsAction);
        buttons[5] = button;
        toolBar.add(button);
        // Last Cycle
        icone = new ImageIcon(getClass().getResource("/resources/last.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 7);
        button.addMouseListener(buttonsAction);
        buttons[6] = button;
        toolBar.add(button);
        
        // Add a Separator
        toolBar.addSeparator();
        
        // Configuration
        icone = new ImageIcon(getClass().getResource("/resources/configuration.png"));
        button = new JButton();
        button.setIcon(icone);
        button.putClientProperty("id", 8);
        button.addMouseListener(buttonsAction);
        buttons[7] = button;
        toolBar.add(button);
    }
    
    /**
     * Add the charts to the program
     * 
     * @param frame Container that will receive the components
     * @param messages Variable for internationalization
     */    
    private void addChartToPane(JFrame frame, ResourceBundle messages) {
    	XYSeries series0 = new XYSeries(messages.getString("Qlres"));
    	XYSeries series1 = new XYSeries(messages.getString("gasflow"));
    	XYSeries series2 = new XYSeries(messages.getString("PtbgT"));
    	XYSeries series3 = new XYSeries(messages.getString("pp"));
    	XYSeries series4 = new XYSeries(messages.getString("PcsgB"));
    	XYSeries series5 = new XYSeries(messages.getString("PcsgT"));
    	XYSeries series6 = new XYSeries(messages.getString("Lslg"));
    	XYSeries series7 = new XYSeries(messages.getString("Ltbg"));
    	XYSeries series8 = new XYSeries(messages.getString("Hplg"));
    	XYSeries series9 = new XYSeries(messages.getString("v0"));    	
    	XYSeries series10 = new XYSeries(messages.getString("Time"));
    	    	
    	dataset = new XYSeriesCollection();
    	dataset.addSeries(series0);
    	dataset.addSeries(series1);
    	dataset.addSeries(series2);
    	dataset.addSeries(series3);
    	dataset.addSeries(series4);
    	dataset.addSeries(series5);
    	dataset.addSeries(series6);
    	dataset.addSeries(series7);
    	dataset.addSeries(series8);
    	dataset.addSeries(series9);
    	dataset.addSeries(series10);
    	
    	chart = ChartFactory.createXYLineChart(
    		messages.getString("chartTitle"), // Chart Title
    		messages.getString("chartXlabel"), // X Label
    		messages.getString("chartYlabel"), // Y Label
    		dataset, // XYSeriesCollection
    		PlotOrientation.VERTICAL,
    		true, // include legend
    		true, // include tooltips
    		false // include urls
    	);
    	
    	XYPlot plot = (XYPlot) chart.getPlot();
    	plot.setBackgroundPaint(Color.WHITE);
    	plot.setAxisOffset(new RectangleInsets(5,5,5,5));
    	plot.setDomainGridlinePaint(Color.BLACK);
    	plot.setRangeGridlinePaint(Color.BLACK);
    	
    	NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
    	rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    	
    	Container contentPane = frame.getContentPane();
    	ChartPanel chartPanel = new ChartPanel(chart);
    	contentPane.add(chartPanel, BorderLayout.CENTER);
   	
    }

	/**
	 * @return the series
	 */
	public Map<String,Double> getSeries() {
		return series;
	}

	/**
	 * @return the buttons
	 */
	public JButton[] getButtons() {
		return buttons;
	}
	
	public void paint(Map<String,Double> p) {
		//Paint the background of the chart in this point if
		//the new time is bigger than the previous one
		//System.out.println("Ok1");
		//System.out.println("-Previous: " + (double)Math.round(this.previoustime * 1000d) / 1000d);
		//System.out.println("-Actual: " + (double)Math.round(p.get("tempo") * 1000d) / 1000d);
		//this.previoustime = (double)Math.round(this.previoustime * 1000d) / 1000d;
		//double actualtime = (double)Math.round(p.get("tempo") * 1000d) / 1000d;
		//if ( (this.previoustime - actualtime) < 0 ) {
			XYPlot xyPlot = chart.getXYPlot();
			ValueAxis domainAxis = xyPlot.getDomainAxis();
			/*if ( p.get("tempo") > 50 ) {
				domainAxis.setRange(p.get("tempo") - 50 ,p.get("tempo") );
			}*/
			//JOptionPane.showMessageDialog(null, p);
			List<XYSeries> series = dataset.getSeries();
	
			//series.get(0).add(p.get("tempo"), p.get("Qlres"));
			//series.get(1).add(p.get("tempo"), p.get("gasflow"));
			series.get(2).add((double)p.get("tempo"), conv.paToPsi(p.get("PtbgT")));
			//series.get(3).add(p.get("tempo"), p.get("pp"));
			series.get(4).add((double)p.get("tempo"), conv.paToPsi(p.get("PcsgB")));
			series.get(5).add((double)p.get("tempo"), conv.paToPsi(p.get("PcsgT")));
			//series.get(6).add(p.get("tempo"), p.get("Lslg"));
			//series.get(7).add(p.get("tempo"), p.get("Ltbg"));
			series.get(8).add(p.get("tempo"), p.get("Hplg"));
			//series.get(9).add(p.get("tempo"), p.get("v0"));

			
			
			//List<String> keys = new ArrayList<String>(p.keySet());
			//for (String key: keys) {
			//	this.file.print( key + ": " + p.get(key) + "|");
			//}
			//this.file.println();
			//Files.write(this.file, p);
			/*counttimes = counttimes + 1;
			if ( counttimes > 0) {
				counttimes = 0;
				Marker marker = new IntervalMarker(this.previoustime, actualtime);
				System.out.println("-Previous: " + this.previoustime);
				System.out.println("-Actual: " + actualtime);
				Color back;
				System.out.println("Ok2");
				//Color(int r, int g, int b, int a)
				//Creates an sRGB color with the specified red, green, blue, and alpha values in the range (0 - 255).
				switch ((p.get("stage")).intValue()) {
					//Plunger rise
					case 3:
						back = new Color(219,72,72,100);
						break;
					//Liquid production
					case 4:
						back = new Color(72,219,72,100);
						break;
					//Afterflow
					case 6:
						back = new Color(79,79,219,100);
						break;
					//Build-up
					case 7:
						back = new Color(114,114,41,100);
						break;
					//Stage not covered
					default:
						//System.out.println("Ok3");
						back = new Color(37,37,37,200);
						break;
				}
				marker.setPaint(back);
				//System.out.println("Ok4");
				xyPlot.addDomainMarker(marker);
				//System.out.println("Ok5");
				this.previoustime = actualtime;
				//System.out.println("Ok6");
			}
		}*/
	}
	
	/**
	 * Resets the chart and makes everything to the program return to
	 * its initial state
	 */
	public void reboot() {
		for ( int i = 0; i < 10; i++ ) {
			dataset.getSeries(i).clear();
		}
	}
    
}