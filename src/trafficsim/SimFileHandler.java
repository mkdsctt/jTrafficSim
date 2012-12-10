package trafficsim;

import java.io.*;

/**
 *	Class SimFileHandler is a helper class for reading/writing the road networks from/to files on disk.
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class SimFileHandler {

	private static BufferedReader fileInput;
	private static int numVertices = 0;
	private static int numEdges = 0;
	private static int linesRead = 0;
	private static TrafficSimulatorApp tsa;

	/*
	 * SimFileHandler constructor, simply initialize some things
	 */
	public SimFileHandler(TrafficSimulatorApp theTSA) {
		fileInput = null;
		linesRead = 0;
		tsa = theTSA;
	}

	/*
	 * read one line from the input configuration file, skipping blank lines, comments, and the end of file (EOF)
	 *		@param fileReader the BufferedReader to read from
	 *		@return the string read, or blank string/null in the oddball cases
	 */
	private static String readFileLine(BufferedReader fileReader) {
		String line = new String();
		try {
			line = fileReader.readLine();
		} catch (IOException ioe) {
			System.out.println("IOException:" + ioe.getMessage());
		}

		// see if we reached the end of the file
		if (line == null) {
			return null;
		}

		// if we read a blank line
		if (line.length() == 0) {
			return ""; // skip the blank line
		}

		// remove leading/trailing spaces
		line = line.trim();

		// if we have a comment
		if (line.toCharArray()[0] == '#') {
			return ""; // skip comment lines
		}

		return line;
	}

	/*
	 * prints the simulation settings from the split input string array
	 * @param s the string array containing the setting information
	 */
	private static void printSettingsFromString(String[] s) {
		// print formatted setting info
		System.out.format(""
			+ "|V|:%0$-8s"
			+ "|E|:%0$-8s"
			+ "width:%0$-8s"
			+ "height:%0$-8s\n",
						  s[0],
						  s[1],
						  s[2],
						  s[3]);
	}

	/*
	 * parse one line from the input file, adding vertices and edges as dictated
	 *		@param line the raw string read from the file
	 */
	private static void parseFileLine(String line) {
		if (line == null) {
			return;
		}
		if (line.length() == 0) {
			return;
		}
		linesRead++; // increment the count of read lines
		if (linesRead == 1) {
			// here we are on the information line. we want to extract the number of 
			// vertices, edges, and the screen dimensions

			// split the line on token "," (comma separated values)
			String[] parts = line.split(",");

			// parse the data from the info line
			numVertices = Integer.parseInt(parts[0]);
			numEdges = Integer.parseInt(parts[1]);

			// get the screen width/height from the file
			DisplayPanel.screenWidth = Integer.parseInt(parts[2]);
			DisplayPanel.screenHeight = Integer.parseInt(parts[3]);

			printSettingsFromString(parts);

		} else {
			// here we know we have either a line describing either a vertex or an edge
			String[] parts = line.split(",");

			if (linesRead < numVertices + 2) {
				// in this case we should still be reading vertices

				TrafficSimulatorApp.intersections.add(new SimpleIntersection(
					Double.parseDouble(parts[0].trim()),
					Double.parseDouble(parts[1].trim()),
					Double.parseDouble(parts[2].trim())));
			} else {
				// here we should be reading an edge
				//System.out.println("Adding" + parts[0].trim() + "," + parts[1].trim());
				TrafficSimulatorApp.roads.add(new Road(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), TrafficSimulatorApp.defaultQueueSize));
			}
		}
	}

	/*
	 * closes the input file (called after completely reading file or before exiting)
	 *		@param fi the BufferedReader object which has been reading from the file
	 */
	private static void closeInputFile(BufferedReader fi) {
		try {
			fi.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	/*
	 * read and parse the input file, handle everything, load all values.
	 * if the user had made some changes to the current simulation, they will
	 * be discarded
	 *		@param filename the name of the file to open and parse
	 */
	protected static void readAndParseFile(String filename) {
		String line; // temporary holder

		if (TrafficSimulatorApp.debugOutput) {
			System.out.println("loading from input file:" + filename);
		}

		TrafficSimulatorApp.setInputFilename(filename);

		// reset as necessary to get ready to reload input
		TrafficSimulatorApp.intersections.clear();
		TrafficSimulatorApp.roads.clear();
		TrafficSimulatorApp.vehicles.clear();
		linesRead = 0;

		// open the road network description file
		fileInput = openFile(filename);

		// read input file, parse parameters and initialize data
		do {
			// grab an input line
			line = readFileLine(fileInput);

			// do something with it
			parseFileLine(line);

		} while (line != null);

		//tsa.setWSize(DisplayPanel.screenWidth + ControlPanel.controlPanelWidth, DisplayPanel.screenHeight);

		// close the file after we are done
		closeInputFile(fileInput);
	}

	/*
	 * write entire description to selected output file
	 *		@param filename the name of the file to write our output to
	 */
	protected static void writeToOutput(String filename) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));

			// write the settings line
			writer.write(
				Integer.toString(TrafficSimulatorApp.intersections.size()) + ","
				+ Integer.toString(TrafficSimulatorApp.roads.size()) + ","
				+ Integer.toString(DisplayPanel.screenWidth) + ","
				+ Integer.toString(DisplayPanel.screenHeight) + "\n");

			// write out each vertex
			for (int i = 0; i < TrafficSimulatorApp.intersections.size(); i++) {
				writer.write(
					Double.toString(TrafficSimulatorApp.intersections.get(i).getX()) + ","
					+ Double.toString(TrafficSimulatorApp.intersections.get(i).getY()) + ","
					+ Double.toString(TrafficSimulatorApp.intersections.get(i).getN()) + "\n");
			}

			// write out each edge
			for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
				writer.write(
					TrafficSimulatorApp.roads.get(i).getFromVertex() + ","
					+ TrafficSimulatorApp.roads.get(i).getToVertex() + "\n");
			}

			writer.flush();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	/*
	 * opens the input file for reading
	 *		@param filename the name of the file to open, relative to current directory
	 */
	private static BufferedReader openFile(String filename) {
		try {
			// create a BufferedReader to read the file
			return new BufferedReader(new FileReader(filename));
		} catch (IOException ioe) {
			// print the exception message
			System.out.println("IOException:" + ioe.getMessage());

			// print the cwd to assist with file placement
			System.out.println("cwd:" + System.getProperty("user.dir"));
			System.exit(-1);
		}
		return null;
	}
}
