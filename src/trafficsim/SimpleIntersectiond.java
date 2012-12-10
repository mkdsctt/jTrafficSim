package trafficsim;

/**
 * Intersection class. used to model an intersection in the simulation.  Basically a glorified
 * graph vertex
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class SimpleIntersectiond extends Intersection {

	private double x;  // the x coordinate for the center of the intersection
	private double y;	 // the y coord
	private double n;	 // n is the switching interval for the lights, avg interarrival time for source nodes
	private int ticksTillNext;
	private int activeRoad;
	private int[][] roadConnections;
	private boolean delaying = false;
	public int waitTime = 0;
	public int throughput = 0;

	/*
	 * Intersection constructor, not much special here
	 *		@param x the x coordinate for the center of the intersection
	 *		@param y the y coordinate for the center of the intersection
	 *		@param n the light switch time or average arrival rate (for source nodes)
	 */
	public SimpleIntersectiond(double x, double y, double n) {
		super(x, y, n);
		ticksTillNext = -1;
		activeRoad = 0;
	}

	/*
	 * get the in degree for a vertex by searching edges for roads leading to the
	 * chosen vertex
	 *		@param vertexId the id of the vertex to get the in degree of
	 *		@return the in degree of the selected vertex
	 */
	protected static int getInDegree(int vertexId) {
		int count = 0;
		for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
			if (TrafficSimulatorApp.roads.get(i).getToVertex() == vertexId) {
				// if the edge is pointing to the vertex we are interested in
				count++; // increment the count
			}
		}

		// return the total number of edges incident to the vertex
		return count;
	}

	/*
	 * get the out degree for a vertex, does so by examining the list of edges and counting
	 * the number rooted on the chosen vertex
	 *		@param vertexId the id of the vertex to get the out degree of
	 *		@return the out degree of the selected vertex
	 */
	protected static int getOutDegree(int vertexId) {
		int count = 0;
		for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
			if (TrafficSimulatorApp.roads.get(i).getFromVertex() == vertexId) {
				// if the edge is rooted at the vertex we are interested in
				count++; // increment count
			}
		}

		// return the total number of edges originating from the node with vertexId
		return count;
	}

	/*
	 * update this vertex, either setting the ticksTillNext or popping cars onto new roads
	 */
	@Override
	public void updateIntersection() {
		if (getInDegree() == 1) {
			if (ticksTillNext < 1) {
				for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
					if (TrafficSimulatorApp.roads.get(i).getFromVertex() == getIndex()) {
						// if the edge is rooted at the vertex we are interested in
						TrafficSimulatorApp.vehicles.add(new Vehicle(i));
					}
				}
				//U = 1-lambda*e^(-lambda*x)
				//ln((1-U)/lambda)/-lambda
				ticksTillNext = (int) (n * Math.log(Math.random() * n));
			} else {
				ticksTillNext--;
			}
			//TODO pop vehicles off and destroy them
			for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
				if (TrafficSimulatorApp.roads.get(i).getToVertex() == getIndex()) {
					Vehicle v = TrafficSimulatorApp.roads.get(i).popVehicle();
					if (v != null) {
						TrafficSimulatorApp.throughput++;
						TrafficSimulatorApp.vehicles.remove(v);
					}
				}
			}
		} else if (getInDegree() == 2) {
			//TODO make one way work
			int firstInRoad = -1;
			int secondInRoad = -1;
			int firstOutRoad = -1;
			int secondOutRoad = -1;
			for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
				if (TrafficSimulatorApp.roads.get(i).getToVertex() == getIndex()) {
					if (firstInRoad == -1) {
						firstInRoad = i;
					} else {
						secondInRoad = i;
					}
				}
				if (TrafficSimulatorApp.roads.get(i).getFromVertex() == getIndex()) {
					if (firstOutRoad == -1) {
						firstOutRoad = i;
					} else {
						secondOutRoad = i;
					}
				}
			}
			Vehicle v = TrafficSimulatorApp.roads.get(firstInRoad).popVehicle();
			if (v != null) {
				v.setEdge(secondOutRoad);
				v.isQueued = false;
			}
			v = null;
			v = TrafficSimulatorApp.roads.get(secondInRoad).popVehicle();
			if (v != null) {
				v.setEdge(firstOutRoad);
				v.isQueued = false;
			}
		} else if (getInDegree() > 2) {
			for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
				if (TrafficSimulatorApp.roads.get(i).getToVertex() == getIndex()) {
					waitTime += TrafficSimulatorApp.roads.get(i).getQueueSize();
				}
			}
			roadConnections = new int[getInDegree()][getOutDegree()];
			int inEdge = 0;
			for (int i = 0; i < getInDegree(); i++) {
				int fromVertex = -1;
				while (fromVertex < 0) {
					if (TrafficSimulatorApp.roads.get(inEdge).getToVertex() == getIndex()) {
						roadConnections[i][0] = inEdge;
						fromVertex = TrafficSimulatorApp.roads.get(inEdge).getFromVertex();
					}
					inEdge++;
				}
				int outEdge = 0;
				int outSpot = 1;
				while (outEdge < TrafficSimulatorApp.roads.size() && outSpot <= getOutDegree()) {
					if (TrafficSimulatorApp.roads.get(outEdge).getFromVertex() == getIndex() && TrafficSimulatorApp.roads.get(outEdge).getToVertex() != fromVertex) {
						roadConnections[i][outSpot] = outEdge;
						outSpot++;
					}
					outEdge++;
				}
			}
			if (ticksTillNext >= 1) {
				if (ticksTillNext % 10 == 0 && !delaying) {
					Vehicle v = TrafficSimulatorApp.roads.get(roadConnections[activeRoad][0]).popVehicle();
					if (v != null) {
						v.setEdge(roadConnections[activeRoad][v.chooseDirection(getOutDegree() - 1)]);
						v.isQueued = false;
						throughput++;
					}
				}
				ticksTillNext--;
			} else {
				if (delaying) {
					ticksTillNext = (int) n;
					activeRoad = (activeRoad + 1) % getInDegree();
					delaying = false;
				} else {
					delaying = true;
					ticksTillNext = 5;
				}
			}
		}
		return;
	}

	/*
	 * routine which will delete the desired vertex.  we need to remove all edges which have
	 * an endpoint on the vertex to select.  Additionally, we must modify the vertex id's for
	 * all those vertices which had an id above the selected id.  (otherwise the edges go all
	 * haywire)
	 */
	protected static void deleteSelectedVertex() {
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
}
