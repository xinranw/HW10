/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	public void run() {
		// NOTE : recall that the 'final' keyword notes inmutability
		// even for local variables.

		// Top-level frame in which game components live
		// Be sure to change "TOP LEVEL FRAME" to the name of your game
		final JFrame frame = new JFrame("TOP LEVEL FRAME");
		frame.setLocation(100, 100);

		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);
		status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.X_AXIS));
		final JLabel statusP1 = new JLabel("Player 1", JLabel.LEFT);
		final JLabel statusP2 = new JLabel("Player 2", JLabel.RIGHT);
		final JLabel gameStatus = new JLabel("Game Status", JLabel.CENTER);
		status_panel.add(statusP1);
		status_panel.add(new JSeparator());
		status_panel.add(gameStatus);
		status_panel.add(new JSeparator());
		status_panel.add(statusP2);

		// Main playing area
		final GameCourt court = new GameCourt(statusP1, statusP2, gameStatus);
		frame.add(court, BorderLayout.CENTER);

		// Reset button
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);
		final JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.reset();
			}
		});
		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame helpFrame = new JFrame("Help");
				JEditorPane helpPane = new JEditorPane();
				File helpFile = new File("help.html");
				try {
					helpPane.setPage(helpFile.toURI().toURL());
					helpPane.setEditable(false);
					helpPane.setPreferredSize(new Dimension(300, 400));
					helpFrame.setLocation(900, 100);
					helpFrame.add(helpPane);
					helpFrame.pack();
					helpFrame.setVisible(true);
				} catch (IOException err) {
					System.err.println("Attempted to read a bad URL: "
							+ helpFile.getName());
				}
			}
		});
		control_panel.add(reset);
		control_panel.add(help);

		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start game
		court.reset();
	}

	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it NOTE: Do NOT delete! You MUST include this
	 * in the final submission of your game.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}
