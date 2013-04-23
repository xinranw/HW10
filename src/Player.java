import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

	/*
	 * Finds the directions in which the player will collide with the given game
	 * object
	 */
	public Set<Direction> willIntersectDir(GameObj obj) {
		Set<Direction> directions = new TreeSet<Direction>();
		int next_x = pos_x + v_x;
		int next_y = pos_y + v_y;
		int next_obj_x = obj.pos_x + obj.v_x;
		int next_obj_y = obj.pos_y + obj.v_y;
		if (v_x > 0) {
			if (next_x + width >= next_obj_x) {
				directions.add(Direction.RIGHT);
			} else if (next_obj_x + obj.width >= next_x) {
				directions.add(Direction.LEFT);
			}
		} else if (v_x < 0) {
			if (next_x + width >= next_obj_x) {
				directions.add(Direction.LEFT);
			} else if (next_obj_x + obj.width >= next_x) {
				directions.add(Direction.RIGHT);
			}
		}
		if (v_y > 0) {
			if (next_y + height >= next_obj_y) {
				directions.add(Direction.DOWN);
			} else if (next_obj_y + obj.height >= next_y) {
				directions.add(Direction.UP);
			}
		} else if (v_y < 0) {
			if (next_y + height >= next_obj_y) {
				directions.add(Direction.DOWN);
			} else if (next_obj_y + obj.height >= next_y) {
				directions.add(Direction.UP);
			}
		}
		return directions;
	}

	// A new move method that accounts for collisions with certain game objects
	public void move(GameCourt gameCourt) {
		// Set to contain all possible directions of collision
		Set<Direction> dirs = new TreeSet<Direction>();
		GameObj obj;
		// Checks for hitting a wall
		if (hitWall() != null) {
			dirs.add(hitWall());
		}
		// Iterates through nearby blocks to check for directions of collision
		for (int row = Math.max(0, pos_x / gameCourt.BLOCK_SIZE - 1); row <= Math
				.min(pos_x / gameCourt.BLOCK_SIZE + 1, gameCourt.HOR_BLOCKS - 1); row++) {
			for (int col = Math.max(0, pos_y / gameCourt.BLOCK_SIZE - 1); col <= Math
					.min(pos_y / gameCourt.BLOCK_SIZE + 1,
							gameCourt.VER_BLOCKS - 1); col++) {
				obj = gameCourt.getMap()[row][col];
				if ((obj instanceof Wall || obj instanceof Brick)
						&& willIntersect(obj)) {
					dirs.addAll(willIntersectDir(obj));
				}
			}
		}
		// Adjusts velocity if there will be a collision in that direction
		if (dirs.contains(Direction.LEFT)) {
			v_x = Math.max(0, v_x);
		}
		if (dirs.contains(Direction.RIGHT)) {
			v_x = Math.min(0, v_x);
		}
		if (dirs.contains(Direction.UP)) {
			v_y = Math.max(0, v_y);
		}
		if (dirs.contains(Direction.DOWN)) {
			v_y = Math.min(0, v_y);
		}
		move();
	}
}