import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Speedup extends PowerUp {

	public static final String img_file = "powerups.png";
	public static final int VEL_X = 0;
	public static final int VEL_Y = 0;

	private static BufferedImage img;

	public Speedup(int p_x, int p_y, int size, int courtWidth, int courtHeight) {
		super(p_x, p_y, size, courtWidth, courtHeight);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file)).getSubimage(32, 0, 32,
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
		p.increaseSpeed();
	}
}
