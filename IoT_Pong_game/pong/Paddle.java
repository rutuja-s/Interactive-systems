package pong;

import java.awt.Color;
import java.awt.Graphics;

import com.sun.org.apache.xpath.internal.FoundIndex;

public class Paddle
{

	public int paddleNumber;

	private final int OFFSET = 5;

	private final int RANGE = 20;

	public int x, y, width = 50, height = 250;

	public int score;

	private Pong pong;

	public Paddle(Pong pong, int paddleNumber)
	{
		this.pong = pong;
		this.paddleNumber = paddleNumber;




		if (pong.four_player) {


			//System.out.println("4 player");
			if (paddleNumber == 1)
			{
				this.x = 0;
				this.y = width;
			}

			if (paddleNumber == 2)
			{
				this.x = pong.width - width;
				this.y = width;
			}
			if (paddleNumber == 3)
			{
				width = 250;
				height = 50;
				this.x = height;
				this.y = 0;
				//System.out.println("value3" + this.y);

			}

			if (paddleNumber == 4)
			{
				width = 250;
				height = 50;
				this.x = height;
				this.y = pong.height - height;
				//System.out.println("value4" + this.y);
			}

		}
		else {
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


	}

	public void render(Graphics g)
	{

		g.setColor(Color.WHITE);

		g.fillRect(x, y, width, height);


	}

	public void move(int position)
	{
		int speed = 15;

		if(!pong.four_player) {

			y = (int)(( (position - OFFSET) / (float)(RANGE) ) * (Pong.pong.height - height));

			if (y < 0) {
				y = 0;
			}
			else if (y + height > Pong.pong.height) {
				y = Pong.pong.height - height;
			}

		}
		else {
			if(paddleNumber==1 || paddleNumber==2) {
				y = (int)(width + ((( (position - OFFSET) / (float)(RANGE) ) * (Pong.pong.height - height - 2*width))));

				if (y < width) {
					y = width;
				}
				else if (y + height > Pong.pong.height - width) {
					y = Pong.pong.height - height - width;
				}
				
			}
			
			
		}

	}

}
