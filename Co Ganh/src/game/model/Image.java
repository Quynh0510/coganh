package game.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
	
	public static java.awt.Image RED = getImage("Red.png");
	public static java.awt.Image BLUE = getImage("Blue.png");
	

	private static java.awt.Image getImage(String name) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("src/game/res/" + name));
		} catch (IOException e) {}
		return image;
	}
}
