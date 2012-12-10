/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

/**
 *
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public abstract class Intersection {

	private double x;  // the x coordinate for the center of the intersection
	private double y;	 // the y coord
	private double n;	 // n is the switching interval for the lights, avg interarrival time for source nodes
	private int waitTime = 0;
	private int throughput = 0;

	public Intersection(double x, double y, double n) {
		this.x = x;
		this.y = y;
		this.n = n;
	}

	/*
	 * return the in degree for the node
	 *		@return integer value -- the in degree for the node/intersection
	 */
	public int getInDegree() {
		int count = 0;
		for (Road r : TrafficSimulatorApp.roads) {
			if (r.getToVertex() == getIndex()) {
				count++;
			}
		}
		return count;
	}

	/*
	 * return the out degree for the node
	 *		@return integer value -- the out degree for the node/intersection
	 */
	public int getOutDegree() {
		int count = 0;
		for (Road r : TrafficSimulatorApp.roads) {
			if (r.getFromVertex() == getIndex()) {
				count++;
			}
		}
		return count;
	}

	/*
	 * get the index for the node
	 *		@return integer value -- the index of the node in the TrafficSimulatorApp list of nodes
	 */
	public int getIndex() {
		return TrafficSimulatorApp.intersections.indexOf(this);
	}

	/*
	 * return the N value for the node
	 *		@return double value -- the n value (switching rate / arrival rate) for a node
	 */
	public double getN() {
		return n;
	}

	/*
	 * set the N value for the node
	 *		@param n the n value (switching rate / arrival rate) for the node
	 */
	public void setN(double n) {
		this.n = n;
	}

	/*
	 * return the X coordinate
	 *		@return double value -- the x coordinate of the node
	 */
	public double getX() {
		return x;
	}

	/*
	 * set the X coord for the node
	 *		@param x the x coordinate for the node
	 */
	public void setX(double x) {
		this.x = x;
	}

	/*
	 * return the Y coordinate
	 *		@return double value -- the Y coordinate of the node
	 */
	public double getY() {
		return y;
	}

	/*
	 * set the Y coord for the node
	 *		@param y the y coordinate for the node
	 */
	public void setY(double y) {
		this.y = y;
	}

	public int getThroughput() {
		return throughput;
	}

	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public void delete() {
		// ensure that we have a selected vertex
		if (SimMouseInputHandler.selectedVertex < 0) {
			// exit if there is no selection
			return;
		}

		if (TrafficSimulatorApp.debugOutput) {
			System.out.println("Deleting vertex " + SimMouseInputHandler.selectedVertex);
		}

		// remove all edges touching the vertex
		for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
			// iterate over all edges

			if (TrafficSimulatorApp.roads.get(i).getFromVertex() == SimMouseInputHandler.selectedVertex || TrafficSimulatorApp.roads.get(i).getToVertex() == SimMouseInputHandler.selectedVertex) {
				// if either endpoint of the edge is on the vertex to remove
				if (TrafficSimulatorApp.debugOutput) {
					System.out.print("removing edge " + i + " ");
				}

				TrafficSimulatorApp.roads.remove(i); // remove the edge

				// update i since we removed the current one
				i--;
			}
		}

		// fix edges by subtracting one from the (fromVertex and toVertex)
		// this is to account for the fact that vertices with indices greater
		// than the one just deleted will have their indices decremented
		for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
			if (TrafficSimulatorApp.roads.get(i).getFromVertex() > SimMouseInputHandler.selectedVertex) {
				TrafficSimulatorApp.roads.get(i).setFromVertex(TrafficSimulatorApp.roads.get(i).getFromVertex() - 1);
			}

			if (TrafficSimulatorApp.roads.get(i).getToVertex() > SimMouseInputHandler.selectedVertex) {
				TrafficSimulatorApp.roads.get(i).setToVertex(TrafficSimulatorApp.roads.get(i).getToVertex() - 1);
			}
		}

		// here we have removed all edges, now remove the vertex
		TrafficSimulatorApp.intersections.remove(SimMouseInputHandler.selectedVertex);

		// no selected vertex
		SimMouseInputHandler.selectedVertex = -1;
	}

	public abstract void updateIntersection();
}
