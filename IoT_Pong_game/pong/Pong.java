package pong;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

import jssc.SerialPortException;
import receive.Receive;
import receive.SerialReceive;

public class Pong implements ActionListener, KeyListener {

	public final Color PaddleColor[] = { Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN };
	private final String PaddleColorString[] = { "Blue", "Red", "Yellow", "Green" };

	public int xOffset = 0, yOffset = 0;

	public Color backgroundColor = Color.WHITE, contrastColor = Color.BLACK, ballColor = Color.BLACK, defaultPaddleColor = Color.BLACK;

	public static Receive receive;

	public static Pong pong;

	public int width = 700, height = 700;

	public Renderer renderer;

	public Paddle player1;

	public Paddle player2;

	public Paddle player3;

	public Paddle player4;

	public Ball ball;

	public boolean bot = false, selectingDifficulty, four_player = false;

	public boolean w, s, up, down;

	public int gameStatus = 0, scoreLimit = 5, playerWon; // 0 = Menu, 1 = Paused, 2 = Playing, 3 = Over

	public int botDifficulty, botMoves, botCooldown = 0;

	public Random random;

	public JFrame jframe;

	public long wait_timer;

	public Pong() {
		Timer timer = new Timer(20, this);
		random = new Random();

		jframe = new JFrame("Pong");

		renderer = new Renderer();

		jframe.setSize(width + 15, height + 35);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(renderer);
		jframe.addKeyListener(this);
		jframe.setBackground(Color.BLACK);

		timer.start();

		File file = new File("./port.txt");
		String port = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			port = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("port.txt does not exist");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("couldn't read port.txt");
		}
		if (port == null) {
			System.out.println("no port defined");
		} else {
			receive = new SerialReceive();
			receive.start(port);
		}

		WindowListener pongWindowListener = new PongWindowListener(receive);
		jframe.addWindowListener(pongWindowListener);

		jframe.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				resize();
			}
		});

	}

	public void resize() {
		if (gameStatus == 1 || gameStatus == 2) {
			if (four_player) {
				int min = Integer.min(jframe.getHeight() - 35, jframe.getWidth() - 15);
				pong.height = min;
				pong.width = min;
				player4.resize();
			} else {
				pong.height = jframe.getHeight() - 35;
				pong.width = jframe.getWidth() - 15;
			}
			player2.resize();
		} else {
			pong.height = jframe.getHeight() - 35;
			pong.width = jframe.getWidth() - 15;
		}
	}

	public void start() {
		gameStatus = 2;
		player1 = new Paddle(pong, 1);
		player2 = new Paddle(pong, 2);
		player3 = new Paddle(pong, 3);
		player4 = new Paddle(pong, 4);
		if (!four_player)
			try {
				receive.game_start(2);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		resize();
		ball = new Ball(this);
		wait_timer = System.currentTimeMillis();
	}

	public void update() throws SerialPortException, InterruptedException {
		if (player1.score >= scoreLimit) {
			wait_timer = System.currentTimeMillis();
			playerWon = 1;
			gameStatus = 3;
			receive.winner(playerWon);
		}
		if (player2.score >= scoreLimit) {
			wait_timer = System.currentTimeMillis();
			playerWon = 2;
			gameStatus = 3;
			receive.winner(playerWon);
		}
		if (four_player) {
			if (player3.score >= scoreLimit) {
				wait_timer = System.currentTimeMillis();
				playerWon = 3;
				gameStatus = 3;
				receive.winner(playerWon);
			}
			if (player4.score >= scoreLimit) {
				wait_timer = System.currentTimeMillis();
				playerWon = 4;
				gameStatus = 3;
				receive.winner(playerWon);
			}
		}

		player1.move(receive.ultrasonic_0());
		player2.move(receive.ultrasonic_1());

		if (four_player) {
			player3.move(receive.ultrasonic_2());
			player4.move(receive.ultrasonic_3());
		}

		long currentTime = System.currentTimeMillis();
		if ((currentTime - wait_timer) / 1000F >= 3) {
			if (four_player)
				ball.update(player1, player2, player3, player4);
			else
				ball.update(player1, player2);
		}
	}

	public void render(Graphics2D g) {
		if (receive.ambient_light() == 0) {
			defaultPaddleColor = Color.WHITE;
			ballColor = Color.WHITE;
			backgroundColor = Color.BLACK;
			contrastColor = Color.WHITE;
		} else {
			defaultPaddleColor = Color.BLACK;
			ballColor = Color.BLACK;
			backgroundColor = Color.WHITE;
			contrastColor = Color.BLACK;
		}
		g.setBackground(Color.BLACK);
		g.setColor(backgroundColor);
		g.fillRect(0, 0, jframe.getWidth(), jframe.getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (gameStatus == 0) {
			resize();
			g.setColor(contrastColor);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, 50);

			if (!selectingDifficulty) {
				g.setFont(new Font("Arial", 1, 30));

				g.drawString("Press a button to start a game \n", width / 2 - 200, height / 2);

				// g.drawString("Press Space to Play", width / 2 - 150, height / 2 - 25);
				// g.drawString("Press Shift for 4 player", width / 2 - 200, height / 2 + 25);
				// g.drawString("<< Score Limit: " + scoreLimit + " >>", width / 2 - 150, height
				// / 2 + 75);
			}
		}

		if (selectingDifficulty) {
			String string = botDifficulty == 0 ? "Easy" : (botDifficulty == 1 ? "Medium" : "Hard");

			g.setFont(new Font("Arial", 1, 30));

			g.drawString("<< Bot Difficulty: " + string + " >>", width / 2 - 180, height / 2 - 25);
			g.drawString("Press Space to Play", width / 2 - 150, height / 2 + 25);
		}

		if (gameStatus == 1) {
			g.setColor(contrastColor);
			g.setFont(new Font("Arial", 1, 50));
			g.drawString("PAUSED", width / 2 - 103, height / 2 - 25);
		}

		if (gameStatus == 1 || gameStatus == 2) {
			xOffset = (jframe.getWidth() - width) / 2;
			yOffset = (jframe.getHeight() - height) / 2;
			g.setColor(backgroundColor);
			g.fillRect(xOffset, yOffset, width, height);
			g.setColor(contrastColor);

			if (four_player) {
				float dash_pattern[] = { 25, 50 };
				g.setStroke(new BasicStroke(5f, 2, 1, 0, dash_pattern, 0));
				g.drawLine(width / 2 + xOffset, yOffset, width / 2 + xOffset, height + yOffset);
				g.drawLine(xOffset, height / 2 + yOffset, width + xOffset, height / 2 + yOffset);

			} else {
				g.setStroke(new BasicStroke(5f));
				g.drawLine(width / 2 + xOffset, yOffset, width / 2 + xOffset, height + yOffset);

				g.setStroke(new BasicStroke(2f));
				g.drawOval(width / 2 - 150 + xOffset, height / 2 - 150 + yOffset, 300, 300);
			}

			g.setFont(new Font("Arial", 1, 50));

			if (four_player) {
				g.setColor(PaddleColor[0]);
				g.drawString(String.valueOf(player1.score), player1.width + 25 + xOffset, height / 2 - 10 + yOffset);
				g.setColor(PaddleColor[1]);
				g.drawString(String.valueOf(player2.score), width - player2.width - 50 + xOffset, height / 2 - 10 + yOffset);
				g.setColor(PaddleColor[2]);
				g.drawString(String.valueOf(player3.score), width / 2 + 10 + xOffset, player3.height + 25 + yOffset);
				g.setColor(PaddleColor[3]);
				g.drawString(String.valueOf(player4.score), width / 2 + 10 + xOffset, height - player4.height - 25 + yOffset);
				g.setColor(contrastColor);
			} else {
				g.drawString(String.valueOf(player1.score), width / 2 - 90 + xOffset, 50 + yOffset);
				g.drawString(String.valueOf(player2.score), width / 2 + 65 + xOffset, 50 + yOffset);
			}

			player1.render(g);
			player2.render(g);
			if (four_player) {
				player3.render(g);
				player4.render(g);
			}

			long currentTime = System.currentTimeMillis();
			if ((currentTime - wait_timer) / 1000F <= 3) {
				g.setColor(Color.GRAY);
				g.setFont(new Font("Arial", 1, 150));
				g.drawString(Integer.toString(3 - (int) ((currentTime - wait_timer) / 1000F)), width / 2 - 25, height / 2);
			} else {
				ball.render(g);
			}
		}

		if (gameStatus == 3) {
			resize();
			g.setColor(contrastColor);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, 50);

			g.setColor(PaddleColor[playerWon - 1]);
			g.drawString(PaddleColorString[playerWon - 1] + " Player wins!", width / 2 - 165, height / 2 - 50);
			g.setColor(contrastColor);

			g.setFont(new Font("Arial", 1, 30));

			// g.drawString("Press Space to Play Again", width / 2 - 185, height / 2 - 25);
			// g.drawString("Press ESC for Menu", width / 2 - 140, height / 2 + 25);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (gameStatus) {
		case 2:
			try {
				update();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SerialPortException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 0:
			int game_start = receive.game_start();
			if (game_start == 2) {
				four_player = false;
				start();
			} else {
				if (game_start == 4) {
					four_player = true;
					start();
				}
			}
			break;
		case 3:
			long currentTime = System.currentTimeMillis();
			if ((currentTime - wait_timer) / 1000F >= 10) {
				receive.game_start();
				gameStatus = 0;
			}
			break;
		}

		/*
		 * if (gameStatus == 2) {
		 * try {
		 * try {
		 * update();
		 * } catch (InterruptedException e1) {
		 * // TODO Auto-generated catch block
		 * e1.printStackTrace();
		 * }
		 * } catch (SerialPortException e1) {
		 * // TODO Auto-generated catch block
		 * e1.printStackTrace();
		 * }
		 * } else {
		 * if (gameStatus == 0) {
		 * int game_start = receive.game_start();
		 * if (game_start == 2) {
		 * four_player = false;
		 * start();
		 * } else {
		 * if (game_start == 4) {
		 * four_player = true;
		 * start();
		 * }
		 * }
		 * } else {
		 * if (gameStatus == 3) {
		 * 
		 * }
		 * }
		 * }
		 */

		renderer.repaint();
	}

	public static void main(String[] args) {
		pong = new Pong();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int id = e.getKeyCode();

		if (id == KeyEvent.VK_W) {
			w = true;
		} else if (id == KeyEvent.VK_S) {
			s = true;
		} else if (id == KeyEvent.VK_UP) {
			up = true;
		} else if (id == KeyEvent.VK_DOWN) {
			down = true;
		} else if (id == KeyEvent.VK_RIGHT) {
			/*
			 * if (selectingDifficulty) { if (botDifficulty < 2) { botDifficulty++; } else {
			 * botDifficulty = 0; } } else if (gameStatus == 0) { scoreLimit++; }
			 */
		} else if (id == KeyEvent.VK_LEFT) {
			/*
			 * if (selectingDifficulty) { if (botDifficulty > 0) { botDifficulty--; } else {
			 * botDifficulty = 2; } } else if (gameStatus == 0 && scoreLimit > 1) {
			 * scoreLimit--; }
			 */
		} else if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3)) {
			gameStatus = 0;
			four_player = false;
		} else if (id == KeyEvent.VK_SHIFT && gameStatus == 0) {
			four_player = true;
			// bot = true;
			selectingDifficulty = false;
			start();
		} else if (id == KeyEvent.VK_SPACE) {
			if (gameStatus == 0 || gameStatus == 3) {
				if (!selectingDifficulty) {
					// bot = false;
				} else {
					selectingDifficulty = false;
				}

				start();
			} else if (gameStatus == 1) {
				gameStatus = 2;
			} else if (gameStatus == 2) {
				gameStatus = 1;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int id = e.getKeyCode();

		if (id == KeyEvent.VK_W) {
			w = false;
		} else if (id == KeyEvent.VK_S) {
			s = false;
		} else if (id == KeyEvent.VK_UP) {
			up = false;
		} else if (id == KeyEvent.VK_DOWN) {
			down = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
}
