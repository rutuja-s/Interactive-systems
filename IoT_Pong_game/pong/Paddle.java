package pong;

import java.awt.Color;
import java.awt.Graphics;

public class Paddle
{

	public int paddleNumber;
	
	private final int OFFSET = 5;
	
	private final int RANGE = 20;

	public int x, y, width = 50, height = 250;

	public int score;

	public Paddle(Pong pong, int paddleNumber)
	{
		this.paddleNumber = paddleNumber;

		if (paddleNumber == 1)
		{
			this.x = 0;
		}

		if (paddleNumber == 2)
		{
			this.x = pong.width - width;
		}

		this.y = pong.height / 2 - this.height / 2;
	}

	public void render(Graphics g)
	{
		
		g.setColor(Color.WHITE);
		g.fillRect(x, y, width, height);
	}

	public void move(int position)
	{
		int speed = 15;
		
		y = (int)(( (position - OFFSET) / (float)(RANGE) ) * (Pong.pong.height - height));
		
		if (y < 0) {
			y = 0;
		}
		else if (y + height > Pong.pong.height) {
			y = Pong.pong.height - height;
		}
		
/*
		if (up)
		{
			if (y - speed > 0)
			{
				y -= speed;
			}
			else
			{
				y = 0;
			}
		}
		else
		{
			if (y + height + speed < Pong.pong.height)
			{
				y += speed;
			}
			else
			{
				y = Pong.pong.height - height;
			}
		}
		
		*/
	}

}
