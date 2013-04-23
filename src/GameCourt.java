/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.HashSet;

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
	private Set<Player> players = new HashSet<Player>();

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
					p1.setSprite(Direction.LEFT);
				} else if (e.getKeyCode() == KeyEvent.VK_D) {
					p1.v_x = PLAYER_VELOCITY;
					p1.setSprite(Direction.RIGHT);
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					p1.v_y = PLAYER_VELOCITY;
					p1.setSprite(Direction.DOWN);
				} else if (e.getKeyCode() == KeyEvent.VK_W) {
					p1.v_y = -PLAYER_VELOCITY;
					p1.setSprite(Direction.UP);
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					p2.v_x = -PLAYER_VELOCITY;
					p2.setSprite(Direction.LEFT);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					p2.v_x = PLAYER_VELOCITY;
					p2.setSprite(Direction.RIGHT);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					p2.v_y = PLAYER_VELOCITY;
					p2.setSprite(Direction.DOWN);
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					p2.v_y = -PLAYER_VELOCITY;
					p2.setSprite(Direction.UP);
				}

				if (e.getKeyCode() == KeyEvent.VK_F) {
					p1.bomb(map, BLOCK_SIZE, INTERVAL);
				}
				if (e.getKeyCode() == KeyEvent.VK_L) {
					p2.bomb(map, BLOCK_SIZE, INTERVAL);
				}

			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D) {
					if (p1.v_x >= 0) {
						p1.v_x = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_A) {
					if (p1.v_x <= 0) {
						p1.v_x = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_S) {
					if (p1.v_y >= 0) {
						p1.v_y = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_W) {
					if (p1.v_y <= 0) {
						p1.v_y = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (p2.v_x >= 0) {
						p2.v_x = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (p2.v_x <= 0) {
						p2.v_x = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (p2.v_y >= 0) {
						p2.v_y = 0;
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (p2.v_y <= 0) {
						p2.v_y = 0;
					}
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
		// Add players
		p1 = new Player(1, 1, BLOCK_SIZE - 6, BLOCK_SIZE - 5, COURT_WIDTH,
				COURT_HEIGHT, Direction.RIGHT, "bombermanSprites.png");
		p2 = new Player(COURT_WIDTH - BLOCK_SIZE + 2, COURT_HEIGHT - BLOCK_SIZE
				+ 2, BLOCK_SIZE - 6, BLOCK_SIZE - 5, COURT_WIDTH, COURT_HEIGHT,
				Direction.LEFT, "bombermanSprites.png");

		players.add(p1);
		players.add(p2);

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

		if (playing) {
			// check for the game end conditions
			if (square.intersects(poison)) {
				playing = false;
				status.setText("You lose!");
			} else if (square.intersects(snitch)) {
				playing = false;
				status.setText("You win!");
			}

			status.setText("Running...");
			for (int row = 0; row < HOR_BLOCKS; row++) {
				for (int col = 0; col < VER_BLOCKS; col++) {
					if (map[row][col] instanceof Bomb) {
						Bomb b = (Bomb) map[row][col];
						b.countdown();
						if (b.time() <= 0) {
							b.blow(this);
						}
					}
				}
			}
			for (int row = 0; row < HOR_BLOCKS; row++) {
				for (int col = 0; col < VER_BLOCKS; col++) {
					if ((map[row][col] instanceof Bomb
							|| map[row][col] instanceof Brick || map[row][col] instanceof Player)
							&& map[row][col].isBlown()) {
						map[row][col] = new Grass(row * BLOCK_SIZE, col
								* BLOCK_SIZE, BLOCK_SIZE, COURT_WIDTH,
								COURT_HEIGHT);
					}
				}
			}
			if (p1.isBlown()) {
				p1.reset(this);
			}
			if (p2.isBlown()) {
				p2.reset(this);
			}

			p1.move(this);
			p2.move(this);

			// update the display
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
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

	public GameObj[][] getMap() {
		return map;
	}

	public Set<Player> getPlayers() {
		return players;
	}
}
