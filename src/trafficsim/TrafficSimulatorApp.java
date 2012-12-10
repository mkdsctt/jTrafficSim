package trafficsim;

import java.util.ArrayList;
import java.awt.Toolkit;
import javax.swing.*;

/**
 * The TrafficSimulatorApp simulates cars in a simple network of roads and intersections
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class TrafficSimulatorApp extends JFrame implements Runnable {

	protected static ArrayList<Intersection> intersections;	// list of intersections
	protected static ArrayList<Road> roads;
	protected static ArrayList<Vehicle> vehicles;	// list of vehicle objects
	private static Thread displayer;
	private static String inputFilename;
	private static String outputFilename;
	public static int simTime;
	private static boolean simulating = false;
	protected final static int defaultQueueSize = 30;
	protected final static double defaultSwitchingInterval = 30.0f;
	protected final static boolean debugOutput = false;
	protected final static boolean centerScreen = true;
	public static int waitTime = 0;
	public static int timeInSim = 0;
	public static int throughput = 0;
	JPanel displayPanel;
	JPanel controlPanel;
	private static int frameInterval = 32;

	/*
	 * simulator constructor, called after main instantiates a simulation
	 */
	public TrafficSimulatorApp() {
		super("TrafficSim Final");

		// set some basic settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);

		// instantiate our mouse input handler
		SimMouseInputHandler inputHandler = new SimMouseInputHandler(this);

		// add all the input listener objects
		addKeyListener(new SimKeyInputHandler(this));
		addMouseListener(inputHandler);
		addMouseMotionListener(inputHandler);
		addMouseWheelListener(inputHandler);

		//add the two main window components -- the display panel and the control panel
		displayPanel = new DisplayPanel();
		controlPanel = new ControlPanel();
		getContentPane().add(displayPanel);
		getContentPane().add(controlPanel);

		// initialize our virtual time to zero 
		// the unit of time is just an iteration of the main loop
		simTime = 0;

		// set the size to fit the simulation window and the control panel
		updateWSize();

		// if we want to center the window
		if (centerScreen) {
			// convenience
			int systemScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
			int systemScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

			// set to center
			setLocation((int) ((systemScreenWidth / 2.0) - (getWidth() / 2.0)), (int) ((systemScreenHeight / 2.0) - (getHeight() / 2.0)));
		} else {
			// set to just off top left corner
			setLocation(10, 10);
		}

		// show the window and start
		setVisible(true);
		displayer = new Thread(this);
	}

	/*
	 * get the input filename. This file will be read to load the simulation parameters
	 * see details of file formatting for more information
	 *		@return a String indicating the name of the file to read from
	 */
	public static String getInputFilename() {
		return inputFilename;
	}

	/*
	 * called to ensure that the window reflects the sizes of the most recently opened file
	 *  only matters after a file other than the launch arguments have been opened
	 */
	public void updateWSize() {
		displayPanel.setSize(DisplayPanel.screenWidth, DisplayPanel.screenHeight);
		setSize(DisplayPanel.screenWidth + ControlPanel.controlPanelWidth, DisplayPanel.screenHeight);
	}

	/*
	 * set the input filename. This file will be read to load the simulation parameters
	 * see details of file formatting for more information
	 *		@param inputFilename the string to set the input file name to
	 */
	public static void setInputFilename(String inputFilename) {
		TrafficSimulatorApp.inputFilename = inputFilename;
	}

	/*
	 * return the output filename. this is the file that will be saved to when
	 * the user chooses to save.
	 *		@return a String indicating the name of the file
	 */
	public static String getOutputFilename() {
		return outputFilename;
	}

	/*
	 * return the simulation time
	 *		@return int of the simulation time
	 */
	public static int getSimTime() {
		return simTime;
	}

	public static int getFrameInterval() {
		return frameInterval;
	}

	public static void setFrameInterval(int frameInterval) {
		TrafficSimulatorApp.frameInterval = frameInterval;
	}

	/*
	 * return whether the simulation is running or not
	 *		@return boolean indicating whether the simulation is running
	 */
	public static boolean isSimulating() {
		return simulating;
	}

	/*
	 * set simulating flag.  can be used to start or stop.
	 *		@param simulating whether or not the simulation should be running
	 */
	public static void setSimulating(boolean simulating) {
		TrafficSimulatorApp.simulating = simulating;
	}

	/*
	 * check for proper caller usage
	 *		@param args the arguments array passed to the program
	 */
	private static void checkArgs(String[] args) {
		// check for proper call usage
		if (args.length != 2) {
			System.out.println(""
				+ "incorrect usage\n"
				+ "expected: java TrafficSimulatorApp roaddesc_file output_file");
			System.exit(-1);
		}
	}

	/*
	 * run the simulation
	 */
	@Override
	public void run() {
		while (true) {
			if (simulating) {
				// since we are currently simulating increment the tick/frame count
				simTime++;

				// for each vehicle
				for (Vehicle v : vehicles) {
					v.updatePosition();  // update the position
				}

				// for each intersection
				for (Intersection i : intersections) {
					i.updateIntersection(); // process the intersection
				}
			}

			// wait a frame interval 32,16,8 ... just pick something
			try {
				Thread.sleep(frameInterval);
			} catch (InterruptedException ie) {
				break;
			}

			// udpate the display
			this.repaint();
		}
	}

	/*
	 * helper routine to initialize some of the simulation objects
	 */
	private static void initialize() {
		intersections = new ArrayList<Intersection>();
		roads = new ArrayList<Road>();
		vehicles = new ArrayList<Vehicle>();
	}

	/**
	 * main method for the simulator.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// check usage
		checkArgs(args);

		initialize();

		// convenience purposes only
		inputFilename = args[0];
		outputFilename = args[1];

		// open our input file, load all its settings and set up the simulation
		SimFileHandler.readAndParseFile(inputFilename);

		// at this point we are initialized just fire up the simulator
		new TrafficSimulatorApp();
		new ColorChooser();
		TrafficSimulatorApp.displayer.start();
	}
}
