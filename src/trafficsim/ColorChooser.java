package trafficsim;

import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;

/**
 * Class ColorChooser is used to help with creating vehicles of random colors
 * @author Michael Scott <mkdsctt@gmail.com>
 */
public class ColorChooser {

	private static ArrayList<Color> colors;
	private static Random rng;

	/*
	 * ColorChooser constructor, add some colors to the list
	 */
	public ColorChooser() {
		colors = new ArrayList<Color>();
		colors.add(new Color(0x00ffff));
		colors.add(new Color(0x666666));
		colors.add(new Color(0x999999));
		colors.add(new Color(0xcccccc));
		colors.add(new Color(0xffffff));
		colors.add(new Color(0x00ff39));
		colors.add(new Color(0x00ff33));
		colors.add(new Color(0x00cc00));
		colors.add(new Color(0x006600));
		colors.add(new Color(0x009999));
		colors.add(new Color(0x330033));
		colors.add(new Color(0xcc00cc));
		colors.add(new Color(0x0099ff));
		colors.add(new Color(0x66ffff));
		colors.add(new Color(0x66ff99));
		colors.add(new Color(0x66ff33));
		colors.add(new Color(0xcc9900));
		colors.add(new Color(0xff9933));
		colors.add(new Color(0x3333ff));
		colors.add(new Color(0x9933ff));
		colors.add(new Color(0x660033));
		colors.add(new Color(0x660000));
		colors.add(new Color(0x9933ff));  // purple 
		colors.add(new Color(0xff33cc));  // pink
		colors.add(new Color(0xff6600));  // orange
		colors.add(new Color(0x99ff33));  // lime
		colors.add(new Color(0x00ffff));  // aqua
		colors.add(new Color(0xcc9900));  // gold
		colors.add(new Color(0x666600));  // purple-blue
		colors.add(new Color(0x006666));  // windows
		rng = new Random(621062106210l);
	}

	/*
	 * getRandomColor - get a random color.
	 *		@return a randomly chosen color object
	 */
	public static Color getRandomColor() {
		return colors.get(rng.nextInt(colors.size()));
	}
}
