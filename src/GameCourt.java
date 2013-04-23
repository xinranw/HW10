/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;

/**
 * GameCourt
 * 
 * This class holds the primary game logic of how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// the state of the game logic
	private Square square; // the Black Square, keyboard control
	private Circle snitch; // the Golden Snitch, bounces
	private Poison poison; // the Poison Mushroom, doesn't move
	private Player p1;
	private Player p2;

	private GameObj[][] map;

	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)

	// Game constants
	public static final int COURT_WIDTH = 570;
	public static final int COURT_HEIGHT = 390;
	public static final int PLAYER_VELOCITY = 2;
	public static final int BLOCK_SIZE = 30;
	public static final int HOR_BLOCKS = COURT_WIDTH / BLOCK_SIZE;
	public static final int VER_BLOCKS = COURT_HEIGHT / BLOCK_SIZE;

	// Update interval for timer in milliseconds
	public static final int INTERVAL = 35;

	public GameCourt(JLabel status) {
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// The timer is an object which triggers an action periodically
		// with the given INTERVAL. One registers an ActionListener with
		// this timer, whose actionPerformed() method will be called
		// each time the timer triggers. We define a helper method
		// called tick() that actually does everything that should
		// be done in a single timestep.
		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		// Enable keyboard focus on the court area
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		// this key listener allows the square to move as long
		// as an arrow key is pressed, by changing the square's
		// velocity accordingly. (The tick method below actually
		// moves the square.)
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_A) {
					p1.v_x = -PLAYER_VELOCITY;
					p1.setSprite(Sprite.LEFT);
				} else if (e.getKeyCode() == KeyEvent.VK_D) {
					p1.v_x = PLAYER_VELOCITY;
					p1.setSprite(Sprite.RIGHT);
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					p1.v_y = PLAYER_VELOCITY;
					p1.setSprite(Sprite.DOWN);
				} else if (e.getKeyCode() == KeyEvent.VK_W) {
					p1.v_y = -PLAYER_VELOCITY;
					p1.setSprite(Sprite.UP);
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					p2.v_x = -PLAYER_VELOCITY;
					p2.setSprite(Sprite.LEFT);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					p2.v_x = PLAYER_VELOCITY;
					p2.setSprite(Sprite.RIGHT);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					p2.v_y = PLAYER_VELOCITY;
					p2.setSprite(Sprite.DOWN);
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					p2.v_y = -PLAYER_VELOCITY;
					p2.setSprite(Sprite.UP);
				}

				/*
				 * if (e.getKeyCode() == KeyEvent.VK_F){
				 * 
				 * }
				 */
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D
						|| e.getKeyCode() == KeyEvent.VK_A) {
					p1.v_x = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_S
						|| e.getKeyCode() == KeyEvent.VK_W) {
					p1.v_y = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT
						|| e.getKeyCode() == KeyEvent.VK_LEFT) {
					p2.v_x = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_UP) {
					p2.v_y = 0;
				}
			}
		});

		this.status = status;
	}

	/**
	 * (Re-)set the state of the game to its initial state.
	 */
	public void reset() {
		square = new Square(COURT_WIDTH, COURT_HEIGHT);
		poison = new Poison(COURT_WIDTH, COURT_HEIGHT);
		snitch = new Circle(COURT_WIDTH, COURT_HEIGHT);

		map = new GameObj[HOR_BLOCKS][VER_BLOCKS];

		// Populate map with grass, bricks, or walls
		for (int row = 0; row < HOR_BLOCKS; row++) {
			for (int col = 0; col < VER_BLOCKS; col++) {
				if (row % 2 == 1 && col % 2 == 1) {
					map[row][col] = new Wall(row * BLOCK_SIZE,
							col * BLOCK_SIZE, BLOCK_SIZE, COURT_WIDTH,
							COURT_HEIGHT);
				} else if (!(row == 0 && col == 0) && !(row == 1 && col == 0)
						&& !(row == 0 && col == 1)
						&& !(row == HOR_BLOCKS - 1 && col == VER_BLOCKS - 1)
						&& !(row == HOR_BLOCKS - 2 && col == VER_BLOCKS - 1)
						&& !(row == HOR_BLOCKS - 1 && col == VER_BLOCKS - 2)
						&& Math.random() < .8) {
					map[row][col] = new Brick(row * BLOCK_SIZE, col
							* BLOCK_SIZE, BLOCK_SIZE, COURT_WIDTH, COURT_HEIGHT);
				} else {
					map[row][col] = new Grass(row * BLOCK_SIZE, col
							* BLOCK_SIZE, BLOCK_SIZE, COURT_WIDTH, COURT_HEIGHT);
				}
			}
		}

		p1 = new Player(1, 1, BLOCK_SIZE - 4, COURT_WIDTH, COURT_HEIGHT,
				Sprite.RIGHT, "bombermanSprites.png");
		p2 = new Player(COURT_WIDTH - BLOCK_SIZE - 1, COURT_HEIGHT - BLOCK_SIZE
				- 1, BLOCK_SIZE - 1, COURT_WIDTH, COURT_HEIGHT, Sprite.LEFT,
				"bombermanSprites.png");

		playing = true;
		status.setText("Running...");

		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		Set<Direction> p1_dir = new TreeSet<Direction>();
		Set<Direction> p2_dir = new TreeSet<Direction>();

		if (playing) {
			// advance the square and snitch in their
			// current direction.
			square.move();
			snitch.move();

			// make the snitch bounce off walls...
			snitch.bounce(snitch.hitWall());
			// ...and the mushroom
			snitch.bounce(snitch.hitObj(poison));

			// check for the game end conditions
			if (square.intersects(poison)) {
				playing = false;
				status.setText("You lose!");
			} else if (square.intersects(snitch)) {
				playing = false;
				status.setText("You win!");
			}

			status.setText("Running..." + p1.v_x + ", " + p1.v_y);

			if (p1.hitWall() != null) {
				p1_dir.add(p1.hitWall());
			}
			if (p2.hitWall() != null) {
				p2_dir.add(p2.hitWall());
			}
			for (int row = Math.max(0, p1.pos_x / BLOCK_SIZE - 1); row <= Math
					.min(p1.pos_x / BLOCK_SIZE + 1, HOR_BLOCKS - 1); row++) {
				for (int col = Math.max(0, p1.pos_y / BLOCK_SIZE - 1); col <= Math
						.min(p1.pos_y / BLOCK_SIZE + 1, VER_BLOCKS - 1); col++) {
					if ((map[row][col] instanceof Wall || map[row][col] instanceof Brick)
							&& p1.willIntersect(map[row][col])) {
						p1_dir.addAll(p1.willIntersectDir(map[row][col]));
						System.out.println(row + ", " + col);
					}
				}
			}
			if (p1_dir.contains(Direction.LEFT)) {
				p1.v_x = Math.max(0, p1.v_x);
			}
			if (p1_dir.contains(Direction.RIGHT)) {
				p1.v_x = Math.min(0, p1.v_x);
			}
			if (p1_dir.contains(Direction.UP)) {
				p1.v_y = Math.max(0, p1.v_y);
			}
			if (p1_dir.contains(Direction.DOWN)) {
				p1.v_y = Math.min(0, p1.v_y);
			}
			
			for (int row = Math.max(0, p2.pos_x / BLOCK_SIZE - 1); row <= Math
					.min(p2.pos_x / BLOCK_SIZE + 1, HOR_BLOCKS - 1); row++) {
				for (int col = Math.max(0, p2.pos_y / BLOCK_SIZE - 1); col <= Math
						.min(p2.pos_y / BLOCK_SIZE + 1, VER_BLOCKS - 1); col++) {
					if ((map[row][col] instanceof Wall || map[row][col] instanceof Brick)
							&& p2.willIntersect(map[row][col])) {
						p2_dir.addAll(p2.willIntersectDir(map[row][col]));
						System.out.println(row + ", " + col);
					}
				}
			}
			if (p2_dir.contains(Direction.LEFT)) {
				p2.v_x = Math.max(0, p2.v_x);
			}
			if (p2_dir.contains(Direction.RIGHT)) {
				p2.v_x = Math.min(0, p2.v_x);
			}
			if (p2_dir.contains(Direction.UP)) {
				p2.v_y = Math.max(0, p2.v_y);
			}
			if (p2_dir.contains(Direction.DOWN)) {
				p2.v_y = Math.min(0, p2.v_y);
			}
			p1_dir.clear();
			p2_dir.clear();

			p1.move();
			p2.move();

			// update the display
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		square.draw(g);
		poison.draw(g);
		snitch.draw(g);
		for (int row = 0; row < HOR_BLOCKS; row++) {
			for (int col = 0; col < VER_BLOCKS; col++) {
				map[row][col].draw(g);
			}
		}
		p1.draw(g);
		p2.draw(g);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}
