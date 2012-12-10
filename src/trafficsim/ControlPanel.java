package trafficsim;

import java.awt.*;
import javax.swing.*;

/**
 * class ControlPanel used to display a control panel on the form
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class ControlPanel extends JPanel {

	protected final static int controlPanelWidth = 120;
	private static int textX, textY;

	/*
	 * constructor for ControlPanel, set the size, border, etc
	 */
	public ControlPanel() {
		super();
		setSize(controlPanelWidth, DisplayPanel.screenHeight);
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	/*
	 * drawAndOffset is a helper method to draw text to the display panel and update
	 * the location so that the next text will be draw yOffset below this
	 *		@param gr2 the graphics context to draw to
	 *		@param msg the string to write/draw
	 *		@param yOffset the offset (in px) to move down after writing
	 */
	public static void drawAndOffset(Graphics2D gr2, String msg, int yOffset) {
		gr2.drawString(msg, textX, textY);
		textY += yOffset;
	}

	/*
	 * paintComponent method to draw the control panel
	 *		@param gr the graphics object to draw to
	 */
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D gr2 = (Graphics2D) gr;
		textX = DisplayPanel.screenWidth + 5;
		textY = 15;

		drawAndOffset(gr2, "Keys", 10);
		drawAndOffset(gr2, "---------------------------", 10);
		drawAndOffset(gr2, "(C)lear Selection", 15);
		drawAndOffset(gr2, "(W)rite Outfile", 15);
		drawAndOffset(gr2, "(D)elete sel. vertex", 15);
		drawAndOffset(gr2, "(R)eload from file", 15);
		drawAndOffset(gr2, "SPC start/stop", 15);
		drawAndOffset(gr2, "ESC to exit", 10);
		drawAndOffset(gr2, "---------------------------", 10);
		drawAndOffset(gr2, "LMB select/move", 15);
		drawAndOffset(gr2, "MMB add vertex", 15);
		drawAndOffset(gr2, "RMB connect to sel.", 15);
		drawAndOffset(gr2, "Stats", 10);
		drawAndOffset(gr2, "---------------------------", 10);
		drawAndOffset(gr2, "|V| = " + Integer.toString(TrafficSimulatorApp.intersections.size()), 15);
		drawAndOffset(gr2, "|E| = " + Integer.toString(TrafficSimulatorApp.roads.size()), 15);
		drawAndOffset(gr2, "|Cars| = " + Integer.toString(TrafficSimulatorApp.vehicles.size()), 15);
		drawAndOffset(gr2, "Results", 10);
		drawAndOffset(gr2, "---------------------------", 10);
		drawAndOffset(gr2, "time = " + Integer.toString(TrafficSimulatorApp.getSimTime()) + " s", 15);
		drawAndOffset(gr2, "Wait = " + Integer.toString(TrafficSimulatorApp.waitTime), 15);
		drawAndOffset(gr2, "In Sim = " + Integer.toString(TrafficSimulatorApp.timeInSim), 15);
		drawAndOffset(gr2, "Cars = " + Integer.toString(TrafficSimulatorApp.throughput), 15);
		drawAndOffset(gr2, "Per Vertex:", 15);
		drawAndOffset(gr2, " wait:cars", 15);

		// for each intersection
		for (Intersection i : TrafficSimulatorApp.intersections) {
			if (i.getInDegree() > 2) {
				drawAndOffset(gr2, "V" + i.getIndex() + " = " + i.getWaitTime() + ":" + i.getThroughput(), 15);
			}
		}
	}
}
