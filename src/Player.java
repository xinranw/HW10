import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

public class Player extends GameObj {
	// Original position of the player, used for resetting
	private final int ORIGINAL_PX;
	private final int ORIGINAL_PY;

	private BufferedImage spriteMap, spriteImg;

	private Set<Direction> dirs = new TreeSet<Direction>();
	private Direction playerDir;
	private Boolean placedBomb;
	private int rad, bombs, speed;
	private int deaths = 0, kills = 0, bombCounter = 0;

	// Initiates new player, sets default values and displays its sprite
	public Player(int p_x, int p_y, int width, int height, int courtWidth,
			int courtHeight, Direction dir, String file) {
		super(0, 0, p_x, p_y, width, height, courtWidth, courtHeight);
		ORIGINAL_PX = p_x;
		ORIGINAL_PY = p_y;
		playerDir = dir;
		placedBomb = false;
		rad = 3;
		bombs = 1;
		speed = 3;
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

	// Sets the sprite based on its direction
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
		GameObj[][] map = gameCourt.getMap();
		int blockSize = gameCourt.getBlockSize();
		int mapCols = map.length;
		int mapRows = map[0].length;
		// Checks for hitting a wall
		if (hitWall() != null) {
			dirs.add(hitWall());
		}

		/*
		 * Uses the placedBomb boolean to determine when a player has moved away
		 * from the bomb he just placed. If there is a bomb in the direction he
		 * is going, trap him and stop him from moving
		 */

		if (v_x < 0) {
			currObj = map[(pos_x + width) / blockSize][(pos_y + height / 2)
					/ blockSize];
			nextObj = map[Math.max(0, (pos_x + width) / blockSize
					- 1)][(pos_y + height / 2) / blockSize];
		} else if (v_x > 0) {
			currObj = map[(pos_x) / blockSize][(pos_y + height / 2)
					/ blockSize];
			nextObj = map[Math.min(mapCols - 1, (pos_x)
					/ blockSize + 1)][(pos_y + height / 2)
					/ blockSize];
		} else if (v_y < 0) {
			currObj = map[(pos_x + width / 2) / blockSize][Math.min(mapRows - 1, (pos_y + height)
					/ blockSize)];
			nextObj = map[(pos_x + width / 2) / blockSize][Math.max(
					0, (pos_y + height) / blockSize - 1)];
		} else if (v_y > 0) {
			currObj = map[(pos_x + width / 2) / blockSize][(pos_y)
					/ blockSize];
			nextObj = map[(pos_x + width / 2) / blockSize][Math.min(
					mapRows - 1, (pos_y) / blockSize
							+ 1)];
		}
		if (nextObj instanceof PowerUp) {
			((PowerUp) nextObj).used();
			((PowerUp) nextObj).gain(this);
		}
		if (placedBomb) {
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
		for (int row = Math.max(0, pos_x / blockSize - 1); row <= Math
				.min(pos_x / blockSize + 1, mapCols - 1); row++) {
			for (int col = Math.max(0, pos_y / blockSize - 1); col <= Math
					.min(pos_y / blockSize + 1,
							mapRows - 1); col++) {
				currObj = map[row][col];
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

	// Places a bomb at the player's current location and associates the bomb to
	// the player as its owner
	public void bomb(GameObj[][] map, int blockSize, int interval) {
		if (bombCounter < bombs){
			int row = 0, col = 0;
			if (v_x < 0) {
				row = (pos_x + width) / blockSize;
			} else if (v_x > 0) {
				row = pos_x / blockSize;
			} else if (v_x == 0) {
				row = (pos_x + width / 2) / blockSize;
			}
			if (v_y < 0) {
				col = (pos_y + height) / blockSize;
			} else if (v_y > 0) {
				col = pos_y / blockSize;
			} else if (v_y == 0) {
				col = (pos_y + height / 2) / blockSize;
			}
			if (!(map[row][col] instanceof Bomb)) {
				map[row][col] = new Bomb(row * blockSize, col * blockSize,
						blockSize, map.length * blockSize, map[0].length
								* blockSize, interval, rad, this);
				placedBomb = true;
			}
			bombCounter++;
		}
	}

	public void gainKill() {
		kills++;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void increaseRad() {
		rad = Math.min(6, rad + 1);
	}

	public void increaseBombs() {
		bombs = Math.min(6, bombs + 1);
	}
	
	public void bombBlown(){
		bombCounter = Math.max(0, bombCounter - 1);
	}

	public void increaseSpeed() {
		speed = Math.min(4, speed + 1);
	}

	public int getSpeed() {
		return speed;
	}
	

	// Reset the player after death
	public void reset(GameCourt gameCourt) {
		dirs.clear();
		isBlown = false;
		placedBomb = false;
		bombCounter = 0;
		deaths++;
		this.pos_x = ORIGINAL_PX;
		this.pos_y = ORIGINAL_PY;
	}
}