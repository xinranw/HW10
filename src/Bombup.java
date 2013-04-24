import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bombup extends PowerUp {

	public static final String img_file = "powerups.png";
	public static final int VEL_X = 0;
	public static final int VEL_Y = 0;

	private static BufferedImage img;

	public Bombup(int p_x, int p_y, int size, int courtWidth, int courtHeight) {
		super(p_x, p_y, size, courtWidth, courtHeight);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file)).getSubimage(0, 0, 32,
						32);
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}
	
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}
	
	public void gain(Player p){
		p.increaseBombs();
	}
}
