/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

/**
 * SimpleIntersection
 * @author Will, Michael
 */
public class SimpleIntersection extends Intersection {

	private int ticksTillNext = -1;
	private int activeRoad;
	private int[][] roadConnections;
	private boolean delaying = false;

	public SimpleIntersection(double x, double y, double n) {
		super(x, y, n);
		if (n > 0) {
			ticksTillNext = getArrivalDelay(getN() / 3600);
		}
	}

	public int getArrivalDelay(double averageArrivalRate) {
		return (int) ((-1 / (averageArrivalRate)) * Math.log(1 - Math.random()));
	}

	@Override
	public void updateIntersection() {

		if (getInDegree() == 1) {
			// if we are a sink/source node
			if (ticksTillNext == 0) {
				for (Road r : TrafficSimulatorApp.roads) {
					if (r.getFromVertex() == getIndex()) {
						TrafficSimulatorApp.vehicles.add(new Vehicle(r.getIndex()));
					}
				}
				ticksTillNext = getArrivalDelay(getN() / 3600);
				return;
			}
			ticksTillNext--;
			//TODO pop vehicles off and destroy them
			for (Road r : TrafficSimulatorApp.roads) {
				if (r.getToVertex() == getIndex() && r.getQueueSize() > 0) {
					TrafficSimulatorApp.throughput++;
					TrafficSimulatorApp.vehicles.remove(r.popVehicle());
				}
			}
			return;
		}

		if (getInDegree() == 2) {
			// if we are a non-switching node, a bend in the road, or something else
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
			return;
		}

		if (getInDegree() > 2) {
			// for "real" (switching) intersections
			for (int i = 0; i < TrafficSimulatorApp.roads.size(); i++) {
				if (TrafficSimulatorApp.roads.get(i).getToVertex() == getIndex()) {
					setWaitTime(getWaitTime() + TrafficSimulatorApp.roads.get(i).getQueueSize());
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
						setThroughput(getThroughput() + 1);
					}
				}
				ticksTillNext--;
			} else {
				if (delaying) {
					ticksTillNext = (int) getN();
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
}
