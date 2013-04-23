import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends GameObj {
	public static final int VEL_X = 0;
	public static final int VEL_Y = 0;

	private BufferedImage spriteMap;
	private BufferedImage spriteImg;

	public Player(int p_x, int p_y, int size, int courtWidth, int courtHeight,
			Sprite sprite, String file) {
		super(VEL_X, VEL_Y, p_x, p_y, size, size, courtWidth, courtHeight);
		try {
			if (spriteMap == null) {
				spriteMap = ImageIO.read(new File(file));
			}
			spriteImg = setSprite(sprite);
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(spriteImg, pos_x, pos_y, width, height, null);
	}

	public BufferedImage setSprite(Sprite sprite) {
		switch (sprite) {
		case LEFT:
			spriteImg = spriteMap.getSubimage(32, 0, 20, 20);
			break;
		case RIGHT:
			spriteImg = spriteMap.getSubimage(93, 0, 20, 20);
			break;
		case UP:
			spriteImg = spriteMap.getSubimage(62, 0, 20, 20);
			break;
		case DOWN:
			spriteImg = spriteMap.getSubimage(2, 0, 20, 20);
			break;
		}
		return spriteImg;

	}
}