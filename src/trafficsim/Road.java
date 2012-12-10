package trafficsim;

import java.util.ArrayList;

/**
 * Class Road used to represent an edge in the graph, or a "road".  additionally each
 * road has a queue.  (the waiting line before the intersection)
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class Road {

	private ArrayList<Vehicle> queue;
	private int fromVertex;
	private int toVertex;
	private int queueCapacity;

	/*
	 * simple constructor for the Road class
	 *		@param fromVertex the source/from vertex connected to the road
	 *		@param toVertex the dextiation/to vertex connected to the road
	 *		@param queueCapacity the maximum number of vehicles that can fit in the queue
	 */
	public Road(int fromVertex, int toVertex, int queueCapacity) {
		this.fromVertex = fromVertex;
		this.toVertex = toVertex;
		this.queueCapacity = queueCapacity;
		this.queue = new ArrayList<Vehicle>();
	}

	/*
	 * return the from vertex
	 *		@return integer value of id of the from/source vertex
	 */
	public int getFromVertex() {
		return fromVertex;
	}

	/*
	 * return the maximum queue capacity
	 *		@return the maximum queue capacity
	 */
	public int getQueueCapacity() {
		return queueCapacity;
	}

	/*
	 * return the current queue size
	 *		@return the current size of the queue
	 */
	public int getQueueSize() {
		return queue.size();
	}

	/*
	 * return the to vertex
	 *		@return integer value of id of the to/destination vertex
	 */
	public int getToVertex() {
		return toVertex;
	}

	public int getIndex() {
		return TrafficSimulatorApp.roads.indexOf(this);
	}

	/*
	 * manually set the from vertex for the edge/road.  this is used when deleting
	 * a node/vertex/intersection, since many edges will need to update the values
	 * of the from/to vertex to ensure that they point to the correct vertices, 
	 * though the indices have changed
	 *		@param fromVertex the id of the vertex to set the fromVertex parameter to
	 */
	public void setFromVertex(int fromVertex) {
		this.fromVertex = fromVertex;
	}

	/*
	 * manually set the to vertex for the edge/road.  this is used similarly to 
	 * setFromVertex, it is used when deleting nodes
	 *		@param toVertex the id of the vertex to set the toVertex parameter to
	 */
	public void setToVertex(int toVertex) {
		this.toVertex = toVertex;
	}

	/*
	 * add a vehicle to the queue, if possible
	 *		@param v the vehicle to add
	 *		@return integer value of the position joined in the queue, -1 if queue is full
	 */
	public int joinQueue(Vehicle v) {
		// if the queue is full
		if (queue.size() == queueCapacity) {
			return -1; // return -1 on full queue
		}

		// get the value of the index for the newly added item.
		// since the size counts from 1 we do not need to increment the value
		int newIndex = queue.size();

		// add the vehicle
		queue.add(v);

		// update the queued flag
		v.isQueued = true;
		setQueuePositions();
		// return the position in the queue of the newly added vehicle
		return newIndex;
	}

	/*
	 * popVehicle() - remove the first car in line and return it
	 *		@return the Vehicle object for the first car in the queue, null on error
	 */
	public Vehicle popVehicle() {
		// if there are no vehicles
		if (queue.size() <= 0) {
			return null;
		}

		// pop the first vehicle off and return it
		Vehicle ret = queue.remove(0);

		// make sure to update the queued flag
		ret.isQueued = false;
		setQueuePositions();
		return ret;
	}

	/*
	 * set queue positions
	 */
	public void setQueuePositions() {
		double fromX = TrafficSimulatorApp.intersections.get(getFromVertex()).getX();
		double fromY = TrafficSimulatorApp.intersections.get(getFromVertex()).getY();
		double toX = TrafficSimulatorApp.intersections.get(getToVertex()).getX();
		double toY = TrafficSimulatorApp.intersections.get(getToVertex()).getY();
		double dX = toX - fromX;
		double dY = toY - fromY;
		double hyp = Math.hypot(dX, dY);
		dX = dX / hyp;
		dY = dY / hyp;
		for (int i = 0; i < queue.size(); i++) {
			queue.get(i).setPosition(toX - dX * (16 + 9 * i), toY - dY * (16 + 9 * i));
		}
	}
}
