package trafficsim;

import java.awt.*;
import javax.swing.*;

/**
 * class DisplayPanel used to draw the simulation display on the form
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class DisplayPanel extends JPanel {

	protected static Font tinyFont;
	protected static Font smallFont;
	protected static Font largeFont;
	private static BasicStroke thinLine;
	private static BasicStroke thickLine;
	protected static int screenWidth;
	protected static int screenHeight;
	protected final static int queueWidth = 24;
	protected final static int queueHeight = 8;
	protected final static int vertexRadius = 15;
	protected final static int vehicleRadius = 4;
	protected final static float roadWidth = 4f;

	/*
	 * DisplayPanel constructor
	 */
	public DisplayPanel() {
		super();
		setSize(screenWidth, screenHeight);
		setBorder(BorderFactory.createLineBorder(Color.black));

		tinyFont = new Font("Dialog", Font.PLAIN, 8);
		smallFont = new Font("Dialog", Font.PLAIN, 12);
		largeFont = new Font("Dialog", Font.BOLD, 12);
		thinLine = new BasicStroke(1f);
		thickLine = new BasicStroke(roadWidth);
	}

	/*
	 * helper function to "map" values so that we can use the coordinates more comfortably
	 *		@param y the original y offset as a double
	 *		@return double value mapped to invert the y axis
	 */
	private double mapY(double y) {
		return (screenHeight - 1) - y;
	}

	/*
	 * draw a road
	 *		@param gr2 the graphics context to draw to
	 *		@param r the road object we should draw
	 */
	private void drawRoad(Graphics2D gr2, Road r) {
		gr2.setStroke(thickLine);
		gr2.setColor(Color.black);

		//draw the line, mapping coordinates to be "normal" i.e. like math graphs
		gr2.drawLine((int) TrafficSimulatorApp.intersections.get(r.getFromVertex()).getX(), (int) mapY(TrafficSimulatorApp.intersections.get(r.getFromVertex()).getY()),
					 (int) TrafficSimulatorApp.intersections.get(r.getToVertex()).getX(), (int) mapY(TrafficSimulatorApp.intersections.get(r.getToVertex()).getY()));
	}

	/*
	 * draw a vehicle object to the display
	 *		@param gr2 the graphics context to draw to
	 *		@param v the vehicle object we should draw
	 */
	private void drawVehicle(Graphics2D gr2, Vehicle v) {
		int x, y;
		// calculate the x and y values (to the center) adjusting the coordinates by the radius
		x = (int) v.getVehicleX() - vehicleRadius;
		y = (screenHeight - 1) - ((int) v.getVehicleY() + vehicleRadius);

		// fill the oval
		gr2.setColor(v.getVehicleColor());
		gr2.fillOval(x, y, vehicleRadius * 2, vehicleRadius * 2);

		// draw the border/outline
		gr2.setColor(Color.black);
		gr2.setStroke(thinLine); // 1px width line
		gr2.drawOval(x, y, vehicleRadius * 2, vehicleRadius * 2);
	}

	/*
	 * draw an intersection object to the display
	 *		@param gr2 the graphics context to draw to
	 *		@param intersection the intersection object to draw
	 */
	private void drawIntersection(Graphics2D gr2, Intersection intersection) {
		int x, y;
		double n;
		// calculate the x and y values (to the center) adjusting the coordinates by the radius
		x = (int) intersection.getX() - vertexRadius;
		y = (int) mapY(intersection.getY() + vertexRadius);
		n = intersection.getN();

		// fill the oval
		if (intersection.getIndex() == SimMouseInputHandler.selectedVertex) {
			// for selected vertex
			gr2.setColor(Color.yellow);
		} else if (intersection.getInDegree() == 1 && intersection.getOutDegree() == 1) {
			// for source vertices
			gr2.setColor(Color.green);
		} else if (intersection.getInDegree() == 2 && intersection.getOutDegree() == 2) {
			// for a non-switching "intersection" -- acts like a sharp bend in the road, no stopping
			gr2.setColor(Color.blue);
		} else {
			// for the "real" intersection vertices
			gr2.setColor(Color.red);  // we want red vertices
		}
		gr2.fillOval(x, y, vertexRadius * 2, vertexRadius * 2);

		// draw the vertex id's
		gr2.setColor(Color.black);
		gr2.setFont(smallFont);
		gr2.drawString(Integer.toString(intersection.getIndex()), x, y);

		// draw the parameter if an intersection or a source vertex
		if (intersection.getInDegree() != 2 && intersection.getOutDegree() != 2) {
			gr2.setFont(largeFont);
			gr2.drawString(Double.toString(n), x + 5, y + 20);
		}

		// draw the border/outline
		gr2.setStroke(thinLine); // 1px width line
		gr2.drawOval(x, y, vertexRadius * 2, vertexRadius * 2);
	}

	/*
	 * paintComponent method to draw the display panel
	 *		@param gr the graphics object to draw to
	 */
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D gr2 = (Graphics2D) gr;
		int x0, y0, x1, y1;

		//TODO: Move this call, no need to do it repeatedly..
		setBackground(Color.white);

		// draw edges
		for (Road r : TrafficSimulatorApp.roads) {
			// draw the road and its queue
			drawRoad(gr2, r);
		}

		// draw each intersection
		for (Intersection intersection : TrafficSimulatorApp.intersections) {
			// repeat for each vertex
			drawIntersection(gr2, intersection);
		}

		// draw vehicles
		for (Vehicle vehicle : TrafficSimulatorApp.vehicles) {
			// repeat for each vehicle
			drawVehicle(gr2, vehicle);
		}
	}
}
