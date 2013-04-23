import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

public class Player extends GameObj {
	private int v;

	private final int ORIGINAL_PX;
	private final int ORIGINAL_PY;

	private BufferedImage spriteMap;
	private BufferedImage spriteImg;

	private Set<Direction> dirs = new TreeSet<Direction>();
	private Direction playerDir;
	public Boolean placedBomb;

	public Player(int p_x, int p_y, int width, int height, int courtWidth, int courtHeight,
			Direction dir, String file) {
		super(0, 0, p_x, p_y, width, height, courtWidth, courtHeight);
		ORIGINAL_PX = p_x;
		ORIGINAL_PY = p_y;
		playerDir = dir;
		placedBomb = false;
		try {
			if (spriteMap == null) {
				spriteMap = ImageIO.read(new File(file));
			}
			spriteImg = setSprite(dir);
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(spriteImg, pos_x, pos_y, width, height, null);
	}

	public BufferedImage setSprite(Direction direction) {
		switch (direction) {
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
		playerDir = direction;
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
				directions.add(Direction.UP);
			} else if (next_obj_y + obj.height >= next_y) {
				directions.add(Direction.DOWN);
			}
		}
		return directions;
	}

	// A new move method that accounts for collisions with certain game objects
	public void move(GameCourt gameCourt) {
		GameObj currObj = null;
		GameObj nextObj = null;
		// Checks for hitting a wall
		if (hitWall() != null) {
			dirs.add(hitWall());
		}

		if (placedBomb) {
			if (v_x < 0) {
				currObj = gameCourt.getMap()[(pos_x + width)
						/ gameCourt.BLOCK_SIZE][(pos_y + height / 2)
						/ gameCourt.BLOCK_SIZE];
				nextObj = gameCourt.getMap()[Math.max(0, (pos_x + width)
						/ gameCourt.BLOCK_SIZE - 1)][(pos_y + height / 2)
						/ gameCourt.BLOCK_SIZE];
			} else if (v_x > 0) {
				currObj = gameCourt.getMap()[(pos_x) / gameCourt.BLOCK_SIZE][(pos_y + height / 2)
						/ gameCourt.BLOCK_SIZE];
				nextObj = gameCourt.getMap()[Math.min(gameCourt.HOR_BLOCKS,
						(pos_x) / gameCourt.BLOCK_SIZE + 1)][(pos_y + height / 2)
						/ gameCourt.BLOCK_SIZE];
			} else if (v_y < 0) {
				currObj = gameCourt.getMap()[(pos_x + width / 2)
						/ gameCourt.BLOCK_SIZE][(pos_y + height)
						/ gameCourt.BLOCK_SIZE];
				nextObj = gameCourt.getMap()[(pos_x + width / 2)
						/ gameCourt.BLOCK_SIZE][Math.max(0, (pos_y + height)
						/ gameCourt.BLOCK_SIZE - 1)];
			} else if (v_y > 0) {
				currObj = gameCourt.getMap()[(pos_x + width / 2)
						/ gameCourt.BLOCK_SIZE][(pos_y) / gameCourt.BLOCK_SIZE];
				nextObj = gameCourt.getMap()[(pos_x + width / 2)
						/ gameCourt.BLOCK_SIZE][Math.min(gameCourt.VER_BLOCKS,
						(pos_y) / gameCourt.BLOCK_SIZE + 1)];
			}
			if (nextObj instanceof Bomb) {
				dirs.add(Direction.UP);
				dirs.add(Direction.DOWN);
				dirs.add(Direction.LEFT);
				dirs.add(Direction.RIGHT);
			} else if (currObj != null && intersects(currObj)
					&& !(currObj instanceof Bomb)) {
				placedBomb = !placedBomb;
			}
		}

		// Iterates through nearby blocks to check for directions of collision
		for (int row = Math.max(0, pos_x / gameCourt.BLOCK_SIZE - 1); row <= Math
				.min(pos_x / gameCourt.BLOCK_SIZE + 1, gameCourt.HOR_BLOCKS - 1); row++) {
			for (int col = Math.max(0, pos_y / gameCourt.BLOCK_SIZE - 1); col <= Math
					.min(pos_y / gameCourt.BLOCK_SIZE + 1,
							gameCourt.VER_BLOCKS - 1); col++) {
				currObj = gameCourt.getMap()[row][col];

				if ((currObj instanceof Wall || currObj instanceof Brick || (currObj instanceof Bomb && !placedBomb))
						&& willIntersect(currObj)) {
					dirs.addAll(willIntersectDir(currObj));
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
		dirs.clear();
	}

	public void bomb(GameObj[][] map, int blockSize, int interval) {
		int row = (int) Math.round(pos_x / (double) blockSize);
		int col = (int) Math.round(pos_y / (double) blockSize);
		if (!(map[row][col] instanceof Bomb)) {
			map[row][col] = new Bomb(row * blockSize, col * blockSize,
					blockSize, map.length * blockSize, map[0].length
							* blockSize, interval, this);
			placedBomb = true;
		}
	}

	public void reset(GameCourt gameCourt) {
		this.pos_x = ORIGINAL_PX;
		this.pos_y = ORIGINAL_PY;
		dirs.clear();
		isBlown = false;
		placedBomb = false;
	}
}