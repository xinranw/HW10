import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Grass extends GameObj {

	public static final String img_file = "grass.png";
	public static final int VEL_X = 0;
	public static final int VEL_Y = 0;

	private static BufferedImage img;

	public Grass(int p_x, int p_y, int size, int courtWidth, int courtHeight) {
		super(VEL_X, VEL_Y, p_x, p_y, size, size, courtWidth, courtHeight);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}
	
	@Override
	public void draw(Graphics g){
		 g.drawImage(img, pos_x, pos_y, width, height, null); 
	}
}
