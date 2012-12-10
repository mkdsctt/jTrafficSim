package trafficsim;

import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.Point;

/**
 * class SimMouseInputHandler is used to handle all mouse input events
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class SimMouseInputHandler extends MouseAdapter {

	protected static int selectedVertex = -1;
	protected static boolean mouseButtonDown = false;
	protected static boolean isVertexPressed = false;
	protected static boolean isControlPanelPressed = false;
	private Point cplClick;
	private TrafficSimulatorApp tsa;

	/*
	 * default constructor for SimMouseInputHandler. just keep a reference to the
	 * instance of TrafficSimulatorApp
	 *		@param theTSA the trafficsimulatorapp to associate the mouse handler with
	 */
	public SimMouseInputHandler(TrafficSimulatorApp theTSA) {
		this.tsa = theTSA;
		cplClick = new Point();  // initialize the control panel click point
	}

	/*
	 * handle left click event
	 * @param e the mouse event to handle
	 */
	public void handleLeftClick(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		double circleX, circleY, dist;

		// this should always be the case but here we go anyway, since mosuerelease
		//	will reset this flag
		if (!mouseButtonDown) {
			mouseButtonDown = true;
		}

		// see if we are in the control panel
		if (mouseX > DisplayPanel.screenWidth) {
			isControlPanelPressed = true;
			cplClick = e.getPoint();
		}

		// see if we are selecting a vertex
		// for each intersection
		for (Intersection i : TrafficSimulatorApp.intersections) {
			// process the intersection
			circleX = i.getX(); // for convenience
			circleY = (DisplayPanel.screenHeight - 1) - i.getY();

			// determine the distance between the center of the circle and the mouse click
			dist = Point2D.distance(mouseX, mouseY, circleX, circleY);

			// if the click was inside the current vertex
			if (dist <= DisplayPanel.vertexRadius) {
				// "select" the vertex
				isVertexPressed = true;
				selectedVertex = i.getIndex();
			}
		}
	}

	/*
	 * handle the right click event
	 * @param e the mouse event
	 */
	public void handleMiddleClick(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();

		TrafficSimulatorApp.intersections.add(new SimpleIntersection(
			(double) mouseX,
			(double) (DisplayPanel.screenHeight - 1) - mouseY,
			(double) TrafficSimulatorApp.defaultSwitchingInterval));
		selectedVertex = TrafficSimulatorApp.intersections.get(TrafficSimulatorApp.intersections.size() - 1).getIndex();

	}

	/*
	 * handle the right click event
	 * @param e the mouse event
	 */
	public void handleRightClick(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		double circleX, circleY, dist;

		// quit if no vertex selected
		if (selectedVertex < 0) {
			// if there is no selected vertex
			return; // quit the event handler
		}

		// check to see if the right click was in a vertex
		for (int i = 0; i < TrafficSimulatorApp.intersections.size(); i++) {
			// for convenience
			circleX = TrafficSimulatorApp.intersections.get(i).getX();
			circleY = (DisplayPanel.screenHeight - 1) - TrafficSimulatorApp.intersections.get(i).getY();

			// determine the distance between the center of the circle and the mouse click
			dist = Point2D.distance(mouseX, mouseY, circleX, circleY);

			// if the click was inside the current vertex being checked
			if (dist <= DisplayPanel.vertexRadius && i != selectedVertex) {
				// add an edge from the selected vertex to this one
				TrafficSimulatorApp.roads.add(new Road(selectedVertex, i, TrafficSimulatorApp.defaultQueueSize));

				// additionally add the opposite direction edge
				TrafficSimulatorApp.roads.add(new Road(i, selectedVertex, TrafficSimulatorApp.defaultQueueSize));
			}
		}
	}

	/*
	 * handle mouse press events
	 * @param e the MouseEvent to handle
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (TrafficSimulatorApp.isSimulating()) {
			// do nothing if we are simulating.. stop messing around you
			return;
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			// handle left click
			handleLeftClick(e);
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			// this is actually for the middle click
			handleMiddleClick(e);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// right click
			handleRightClick(e);
		}
	}

	/*
	 * handle mouse release events
	 * @param e the mouse release event object
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (mouseButtonDown) {
			// clear flags
			mouseButtonDown = false;
			isVertexPressed = false;
			isControlPanelPressed = false;
		}
	}

	/*
	 * handle mouse drag events
	 * @param e the mouse release event object
	 */
	@Override
	public void mouseDragged(MouseEvent me) {
		if (isControlPanelPressed) {
			tsa.setLocation(tsa.getX() + (me.getX() - cplClick.x), tsa.getY() + (me.getY() - cplClick.y));
			return;
		}
		if (isVertexPressed) {
			TrafficSimulatorApp.intersections.get(selectedVertex).setX((double) me.getX());
			TrafficSimulatorApp.intersections.get(selectedVertex).setY((DisplayPanel.screenHeight - 1) - ((double) me.getY()));
		}
	}

	/*
	 * handle mouse wheel input events
	 * @param mwe the mouse wheel event object to handle
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		if (TrafficSimulatorApp.isSimulating()) {
			// do nothing if we are simulating.. stop messing around you
			return;
		}

		if (selectedVertex < 0) {
			// if there isnt a vertex selected, do nothing
			return;
		}

		if (TrafficSimulatorApp.intersections.get(selectedVertex).getN() - mwe.getWheelRotation() < 0) {
			// if the desired change would make the value negative, do nothing
			return;
		}

		//TODO: change this if trying to support one way roads
		// currently we treat these intersections as special cases where no light switching is needed
		// basically they are just like a turn in the road
		//if (SimpleIntersection.getInDegree(selectedVertex) == 2 && SimpleIntersection.getOutDegree(selectedVertex) == 2) {
		if (TrafficSimulatorApp.intersections.get(selectedVertex).getInDegree() == 2 && TrafficSimulatorApp.intersections.get(selectedVertex).getOutDegree() == 2) {
			// if it is a non-intersection node, do nothing
			return;
		}

		// if we got past all the checks, we change the value
		TrafficSimulatorApp.intersections.get(selectedVertex).setN(TrafficSimulatorApp.intersections.get(selectedVertex).getN() - mwe.getWheelRotation());
	}
}
