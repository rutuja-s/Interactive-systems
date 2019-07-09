package pong;

import java.awt.Graphics;

public class Paddle {

	private final int StandardWidth = 25, StandardHeight = 250;

	public int paddleNumber;

	private final int OFFSET = 5;

	private final int RANGE = 20;

	public int x, y, width = StandardWidth, height = StandardHeight;

	public int score;

	private Pong pong;

	public Paddle(Pong pong, int paddleNumber) {
		this.pong = pong;
		this.paddleNumber = paddleNumber;

		if (pong.four_player) {
			if (paddleNumber == 1) {
				this.x = 0;
				this.y = width;
			}

			if (paddleNumber == 2) {
				this.x = pong.width - width;
				this.y = width;
			}
			if (paddleNumber == 3) {
				width = StandardHeight;
				height = StandardWidth;
				this.x = height;
				this.y = 0;
			}

			if (paddleNumber == 4) {
				width = StandardHeight;
				height = StandardWidth;
				this.x = height;
				this.y = pong.height - height;
			}

		} else {
			if (paddleNumber == 1) {
				this.x = 0;
			}

			if (paddleNumber == 2) {
				this.x = pong.width - width;
			}

			this.y = pong.height / 2 - this.height / 2;
		}

	}

	public void render(Graphics g) {
		// if (pong.four_player) {
		g.setColor(pong.PaddleColor[paddleNumber - 1]);
		// } else {
		// g.setColor(pong.defaultPaddleColor);
		// }
		g.fillRect(x + pong.xOffset, y + pong.yOffset, width, height);
	}

	public void move(int position) {
		int speed = 15;
		if (!pong.four_player) {

			y = (Pong.pong.height - height) - (int) (((position - OFFSET) / (float) (RANGE)) * (Pong.pong.height - height));

			if (y < 0) {
				y = 0;
			} else if (y + height > Pong.pong.height) {
				y = Pong.pong.height - height;
			}

		} else {
			if (paddleNumber == 1 || paddleNumber == 2) {
				y = (Pong.pong.height - height - 2 * width)
						- (int) (width + ((((position - OFFSET) / (float) (RANGE)) * (Pong.pong.height - height - 2 * width))));

				if (y < width) {
					y = width;
				} else if (y + height > Pong.pong.height - width) {
					y = Pong.pong.height - height - width;
				}

			}

			else {

				x = (int) (height + ((((position - OFFSET) / (float) (RANGE)) * (Pong.pong.width - width - 2 * height))));

				if (x < height) {
					x = height;
				} else if (x + width > Pong.pong.width - height) {
					x = Pong.pong.width - height - width;
				}

			}

		}

	}

	public void resize() {
		if (paddleNumber == 2) {
			this.x = pong.width - width;
		}
		if (pong.four_player) {
			if (paddleNumber == 4) {
				this.y = pong.height - height;
			}
		}
	}

}
