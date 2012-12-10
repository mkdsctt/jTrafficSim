package trafficsim;

import java.awt.Color;

/**
 * class to represent a vehicle in the simulation
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class Vehicle {

	private static final double speed = 1.0;
	protected double vehicleX;
	protected double vehicleY;
	public double finalX;
	public double finalY;
	protected double dX;
	protected double dY;
	protected int onEdge;
	protected boolean isQueued;
	protected int queueIndex;
	private Color vehicleColor;
	public int createTime;
	public int waitTime;


	/*
	 * constructor for Vehicle, simply takes as parameter the edge the car is on, this will be used to guide it towards
	 * the destination of the edge.
	 *		@param edge the edge the car will "ride", this edge is directed so it's all we need to know
	 */
	public Vehicle(int edge) {
		setEdge(edge);
		isQueued = false;
		//vehicleColor = palette[(int)(Math.random()*palette.length)];
		vehicleColor = ColorChooser.getRandomColor();
		createTime = TrafficSimulatorApp.simTime;
	}

	/*
	 * set the edge to a new edge, reset dX,dY etc
	 */
	public void setEdge(int edge) {
		onEdge = edge;
		vehicleX = TrafficSimulatorApp.intersections.get(TrafficSimulatorApp.roads.get(edge).getFromVertex()).getX();
		vehicleY = TrafficSimulatorApp.intersections.get(TrafficSimulatorApp.roads.get(edge).getFromVertex()).getY();
		finalX = TrafficSimulatorApp.intersections.get(TrafficSimulatorApp.roads.get(edge).getToVertex()).getX();
		finalY = TrafficSimulatorApp.intersections.get(TrafficSimulatorApp.roads.get(edge).getToVertex()).getY();
		dX = finalX - vehicleX;
		dY = finalY - vehicleY;
		double hyp = Math.hypot(dX, dY);
		dX = dX / hyp * speed;
		dY = dY / hyp * speed;
		isQueued = false;
	}

	/*
	 * simply return the car color
	 *		@return Color object for the car
	 */
	public Color getVehicleColor() {
		return vehicleColor;
	}

	/*
	 * get the x coordinate of the vehicle position
	 *		@return x coordinate as a double value
	 */
	public double getVehicleX() {
		return vehicleX;
	}

	/*
	 * get the y coordinate of the vehicle position
	 *		@return y coordinate as a double value
	 */
	public double getVehicleY() {
		return vehicleY;
	}

	/*
	 * get the edge/road the car is riding along
	 *		@return the id of the edge/road the vehicle is on
	 */
	public int getOnEdge() {
		return onEdge;
	}

	/*
	 * make the driving decisions, let the roads be numbered some way and try to go along them
	 */
	public int chooseDirection(int choices) {
		return (int) (choices * Math.random() + 1);
	}

	public void setPosition(double x, double y) {
		vehicleX = x;
		vehicleY = y;
	}

	/*
	 * method will increment the position of a car in such a way that it
	 * "rides" along the edges of the graph... literally just a naive line drawing algo.
	 */
	// change 11/14/11 -- when stepping by x, make the y checks in the possible dY = 0 case "soft" i.e. inclusive
	//						when stepping by y, make the x			...			dX = 0 case ..
	// this was to fix the dX = 0 and/or dY = 0 cases
	public void updatePosition() {
		TrafficSimulatorApp.timeInSim++;
		if (isQueued) {
			TrafficSimulatorApp.waitTime++;
		}
		if (!isQueued) {
			if (Math.abs(dY) > Math.abs(dX)) {
				// step by y
				double ratio = Math.abs(dX) / Math.abs(dY);
				if (dY > 0) {
					// dY > 0
					if (dX > 0) {
						// dY > 0, dX > 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX + ratio < finalX && vehicleY + 1 < finalY) {
							vehicleX += ratio;
							vehicleY += 1;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					} else {
						// dY > 0, dX <= 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX - ratio >= finalX && vehicleY + 1 < finalY) {
							vehicleX -= ratio;
							vehicleY += 1;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					}

				} else {
					// dY <= 0
					if (dX > 0) {
						// dY <= 0, dX > 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX + ratio < finalX && vehicleY - 1 > finalY) {
							vehicleX += ratio;
							vehicleY -= 1;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					} else {
						// dY <= 0, dX <= 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX - ratio >= finalX && vehicleY - 1 > finalY) {
							vehicleX -= ratio;
							vehicleY -= 1;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					}
				}

			} else {
				// here Math.abs(dY) <= Math.abs(dX)
				// step by x
				double ratio = Math.abs(dY) / Math.abs(dX);
				if (dY > 0) {
					// dY > 0
					if (dX > 0) {
						// dY > 0, dX > 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX + 1 < finalX && vehicleY + ratio < finalY) {
							vehicleX += 1;
							vehicleY += ratio;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					} else {
						// dY > 0, dX <= 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX - 1 > finalX && vehicleY + ratio < finalY) {
							vehicleX -= 1;
							vehicleY += ratio;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					}

				} else {
					// dY <= 0
					if (dX > 0) {
						// dY <= 0, dX > 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX + 1 < finalX && vehicleY - ratio >= finalY) {
							vehicleX += 1;
							vehicleY -= ratio;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					} else {
						// dY <= 0, dX <= 0
						// with this if statement we will stop 1 iteration short
						if (vehicleX - 1 > finalX && vehicleY - ratio >= finalY) {
							vehicleX -= 1;
							vehicleY -= ratio;
						} else {
							// join the queue
							TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
						}
					}
				}
			}
		} else {
			// we are queued

			// increment waitTime by one tick
			waitTime++;
		}

		// check to see if the driver should stop early (due to the line of cars)
		if (Math.abs(vehicleX - finalX) < 0.1 + Math.abs(dX) * (16 + 9 * TrafficSimulatorApp.roads.get(onEdge).getQueueSize())
			&& Math.abs(vehicleY - finalY) < 0.1 + Math.abs(dY) * (16 + 9 * TrafficSimulatorApp.roads.get(onEdge).getQueueSize()) && !isQueued) {
			TrafficSimulatorApp.roads.get(onEdge).joinQueue(this);
		}
	}
}
