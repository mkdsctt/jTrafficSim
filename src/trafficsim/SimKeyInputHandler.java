/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

import java.awt.event.*;
import javax.swing.JFileChooser;

/*
 * class used to handle key inputs to the form
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class SimKeyInputHandler extends KeyAdapter {

	private TrafficSimulatorApp tsa;
	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

	/*
	 * SimKeyInputHandler constructor, just keep track of the simulator instance
	 */
	public SimKeyInputHandler(TrafficSimulatorApp app) {
		tsa = app;
	}

	/*
	 * handle keypress events, check keycode and perform appropriate action
	 *		@param e the KeyEvent object to handle
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if (e.getKeyCode() == KeyEvent.VK_C) {
			// Key C -- "clear" -- clears the selected node

			SimMouseInputHandler.selectedVertex = -1;
		} else if (e.getKeyCode() == KeyEvent.VK_F) {
			// Key + -- "faster" -- increase simulation speed -- decrease frame interval

			if (TrafficSimulatorApp.getFrameInterval() >= 2) {
				TrafficSimulatorApp.setFrameInterval(TrafficSimulatorApp.getFrameInterval() >> 1);
				if (TrafficSimulatorApp.debugOutput) {
					System.out.println("Interval:" + TrafficSimulatorApp.getFrameInterval());
				}
			}

		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			// Key - -- "slower" -- increase frame interval

			TrafficSimulatorApp.setFrameInterval(TrafficSimulatorApp.getFrameInterval() << 1);
			if (TrafficSimulatorApp.debugOutput) {
				System.out.println("Interval:" + TrafficSimulatorApp.getFrameInterval());
			}
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			// Key W -- "write" -- write the description to the output file

			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				if (TrafficSimulatorApp.debugOutput) {
					System.out.println("Writing to:" + fileChooser.getSelectedFile().getName());
				}
				SimFileHandler.writeToOutput(fileChooser.getSelectedFile().getName());
			}
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			// Key D -- "delete" -- delete the selected vertex, and remove its edges

			TrafficSimulatorApp.intersections.get(SimMouseInputHandler.selectedVertex).delete();
		} else if (e.getKeyCode() == KeyEvent.VK_O) {
			// Key O -- "open" -- show open file dialog and laod file

			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				if (TrafficSimulatorApp.debugOutput) {
					System.out.println("Opening : " + fileChooser.getSelectedFile().getName());
				}
				SimFileHandler.readAndParseFile(fileChooser.getSelectedFile().getName());

				tsa.updateWSize();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_R) {
			// Key R -- "reload" -- reload the original starting file

			SimFileHandler.readAndParseFile(TrafficSimulatorApp.getInputFilename());
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			// Key space -- "start/stop" -- start the simulation

			if (!TrafficSimulatorApp.isSimulating()) {
				TrafficSimulatorApp.setSimulating(true);
				return;
			}
			TrafficSimulatorApp.setSimulating(false);

		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			// Key Up arrow -- move the window up

			tsa.setLocation(tsa.getX(), tsa.getY() - 10);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			// Key Down arrow -- move the window down

			tsa.setLocation(tsa.getX(), tsa.getY() + 10);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			// Key Left arrow -- move the window left

			tsa.setLocation(tsa.getX() - 10, tsa.getY());
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			// Key Right arrow -- move the window right
			tsa.setLocation(tsa.getX() + 10, tsa.getY());
		}
	}
}
