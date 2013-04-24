import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bomb extends GameObj {

	private String img_file = "bombermanSprites.png";
	public static final int VEL_X = 0;
	public static final int VEL_Y = 0;

	private static BufferedImage img;
	private int timer;
	private int rad;
	private Player owner;

	public Bomb(int p_x, int p_y, int size, int courtWidth, int courtHeight,
			int interval, int rad, Player p) {
		super(VEL_X, VEL_Y, p_x, p_y, size, size, courtWidth, courtHeight);
		timer = 3000 / interval;
		owner = p;
		this.rad = rad;
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file)).getSubimage(243, 90, 20,
						20);
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}

	public void countdown() {
		timer--;
	}

	public int time() {
		return timer;
	}

	public void blow(GameCourt gameCourt) {
		GameObj obj;
		GameObj[][] map = gameCourt.getMap();
		int mapCols = map.length;
		int mapRows = map[0].length;
		int blockSize = gameCourt.getBlockSize();
		int upRad = 0;
		int downRad = 0;
		int leftRad = 0;
		int rightRad = 0;
		isBlown = true;

		for (int col = Math.max(0, pos_y / blockSize - 1); col >= Math.max(0,
				pos_y / blockSize - rad); col--) {
			upRad++;
			if (map[pos_x / blockSize][col] instanceof Grass
					|| map[pos_x / blockSize][col] instanceof PowerUp) {
			} else {
				break;
			}
		}
		for (int col = Math.min(mapRows - 1, pos_y / blockSize + 1); col <= Math
				.min(mapRows - 1, pos_y / blockSize + rad); col++) {
			downRad++;
			if (map[pos_x / blockSize][col] instanceof Grass
					|| map[pos_x / blockSize][col] instanceof PowerUp) {
			} else {
				break;
			}
		}

		for (int row = Math.max(0, pos_x / blockSize - 1); row >= Math.max(0,
				pos_x / blockSize - rad); row--) {
			leftRad++;
			if (map[row][pos_y / blockSize] instanceof Grass
					|| map[row][pos_y / blockSize] instanceof PowerUp) {
			} else {
				break;
			}
		}
		for (int row = Math.min(mapRows - 1, pos_x / blockSize + 1); row <= Math
				.min(mapCols - 1, pos_x / blockSize + rad); row++) {
			rightRad++;
			if (map[row][pos_y / blockSize] instanceof Grass
					|| map[row][pos_y / blockSize] instanceof PowerUp) {
			} else {
				break;
			}
		}

		for (int row = Math.max(0, pos_x / blockSize - leftRad); row <= Math
				.min(pos_x / blockSize + rightRad, mapCols - 1); row++) {
			for (int col = Math.max(0, pos_y / blockSize - upRad); col <= Math
					.min(pos_y / blockSize + downRad, mapRows - 1); col++) {
				// Check for blowing up players
				if (row == pos_x / blockSize || col == pos_y / blockSize) {
					for (Player p : gameCourt.getPlayers()) {
						if (gameCourt.getMap()[row][col].intersects(p)) {
							p.blown();
							if (p != owner) {
								owner.gainKill();
							}
						}
					}
				}
				// Check for blowing up bricks and other bombs
				if (row == pos_x / blockSize && col == pos_y / blockSize) {
					continue;
				} else if (row == pos_x / blockSize || col == pos_y / blockSize) {
					obj = gameCourt.getMap()[row][col];
					if ((obj instanceof Brick) && !obj.isBlown()) {
						obj.blown();
					} else if (obj instanceof Bomb && !obj.isBlown()) {
						((Bomb) obj).blow(gameCourt);
					}
				}
			}
		}
		owner.bombBlown();
	}
}