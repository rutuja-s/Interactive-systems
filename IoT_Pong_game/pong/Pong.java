package pong;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import receive.Receive;
import receive.SerialReceive;

public class Pong implements ActionListener, KeyListener
{
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

	public int gameStatus = 0, scoreLimit = 7, playerWon; //0 = Menu, 1 = Paused, 2 = Playing, 3 = Over

	public int botDifficulty, botMoves, botCooldown = 0;

	public Random random;

	public JFrame jframe;

	public Pong()
	{
		Timer timer = new Timer(20, this);
		random = new Random();

		jframe = new JFrame("Pong");

		renderer = new Renderer();

		jframe.setSize(width + 15, height + 35);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(renderer);
		jframe.addKeyListener(this);

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
	}

	public void start()
	{
		gameStatus = 2;
		player1 = new Paddle(this, 1);
		player2 = new Paddle(this, 2);
		if (four_player) {
			
			player3 = new Paddle(this, 3);
			player4 = new Paddle(this, 4);
		}
		
		ball = new Ball(this);
	}

	public void update()
	{
		if (player1.score >= scoreLimit)
		{
			playerWon = 1;
			gameStatus = 3;
		}

		if (player2.score >= scoreLimit)
		{
			gameStatus = 3;
			playerWon = 2;
		}
		
		
		
		player1.move(receive.ultrasonic_0());
		player2.move(receive.ultrasonic_1());

		if(bot) {
			player3.move(receive.ultrasonic_2());
			player4.move(receive.ultrasonic_3());
		}

		ball.update(player1, player2);
	}

	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (gameStatus == 0)
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, 50);

			if (!selectingDifficulty)
			{
				g.setFont(new Font("Arial", 1, 30));

				g.drawString("Press Space to Play", width / 2 - 150, height / 2 - 25);
				g.drawString("Press Shift for 4 player", width / 2 - 200, height / 2 + 25);
				g.drawString("<< Score Limit: " + scoreLimit + " >>", width / 2 - 150, height / 2 + 75);
			}
		}

		if (selectingDifficulty)
		{
			String string = botDifficulty == 0 ? "Easy" : (botDifficulty == 1 ? "Medium" : "Hard");

			g.setFont(new Font("Arial", 1, 30));

			g.drawString("<< Bot Difficulty: " + string + " >>", width / 2 - 180, height / 2 - 25);
			g.drawString("Press Space to Play", width / 2 - 150, height / 2 + 25);
		}

		if (gameStatus == 1)
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));
			g.drawString("PAUSED", width / 2 - 103, height / 2 - 25);
		}

		if (gameStatus == 1 || gameStatus == 2)
		{
			g.setColor(Color.WHITE);

			g.setStroke(new BasicStroke(5f));

			g.drawLine(width / 2, 0, width / 2, height);

			g.setStroke(new BasicStroke(2f));

			g.drawOval(width / 2 - 150, height / 2 - 150, 300, 300);

			g.setFont(new Font("Arial", 1, 50));

			g.drawString(String.valueOf(player1.score), width / 2 - 90, 50);
			g.drawString(String.valueOf(player2.score), width / 2 + 65, 50);

			player1.render(g);
			player2.render(g);
			if (four_player) {
				player3.render(g);
				player4.render(g);
			}
			ball.render(g);
		}

		if (gameStatus == 3)
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, 50);

			if (bot && playerWon == 2)
			{
				g.drawString("The Bot Wins!", width / 2 - 170, 200);
			}
			else
			{
				g.drawString("Player " + playerWon + " Wins!", width / 2 - 165, 200);
			}

			g.setFont(new Font("Arial", 1, 30));

			g.drawString("Press Space to Play Again", width / 2 - 185, height / 2 - 25);
			g.drawString("Press ESC for Menu", width / 2 - 140, height / 2 + 25);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (gameStatus == 2)
		{
			update();
		}

		renderer.repaint();
	}

	public static void main(String[] args)
	{
		pong = new Pong();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int id = e.getKeyCode();

		if (id == KeyEvent.VK_W)
		{
			w = true;
		}
		else if (id == KeyEvent.VK_S)
		{
			s = true;
		}
		else if (id == KeyEvent.VK_UP)
		{
			up = true;
		}
		else if (id == KeyEvent.VK_DOWN)
		{
			down = true;
		}
		else if (id == KeyEvent.VK_RIGHT)
		{
			if (selectingDifficulty)
			{
				if (botDifficulty < 2)
				{
					botDifficulty++;
				}
				else
				{
					botDifficulty = 0;
				}
			}
			else if (gameStatus == 0)
			{
				scoreLimit++;
			}
		}
		else if (id == KeyEvent.VK_LEFT)
		{
			if (selectingDifficulty)
			{
				if (botDifficulty > 0)
				{
					botDifficulty--;
				}
				else
				{
					botDifficulty = 2;
				}
			}
			else if (gameStatus == 0 && scoreLimit > 1)
			{
				scoreLimit--;
			}
		}
		else if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3))
		{
			gameStatus = 0;
		}
		else if (id == KeyEvent.VK_SHIFT && gameStatus == 0)
		{
			four_player = true;
			bot = true;
			selectingDifficulty = true;
		}
		else if (id == KeyEvent.VK_SPACE)
		{
			if (gameStatus == 0 || gameStatus == 3)
			{
				if (!selectingDifficulty)
				{
					bot = false;
				}
				else
				{
					selectingDifficulty = false;
				}

				start();
			}
			else if (gameStatus == 1)
			{
				gameStatus = 2;
			}
			else if (gameStatus == 2)
			{
				gameStatus = 1;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		int id = e.getKeyCode();

		if (id == KeyEvent.VK_W)
		{
			w = false;
		}
		else if (id == KeyEvent.VK_S)
		{
			s = false;
		}
		else if (id == KeyEvent.VK_UP)
		{
			up = false;
		}
		else if (id == KeyEvent.VK_DOWN)
		{
			down = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}
}
