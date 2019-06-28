package pong;

import java.awt.Graphics;
import java.util.Random;

import receive.Receive;

public class Ball {

	public int x, y, width = 25, height = 25;

	public int motionX, motionY;

	public Random random;

	private Pong pong;

	public int amountOfHits;

	public int lastHit = 0;

	private int speed = 5;

	private Receive receive;

	public Ball(Pong pong) {
		this.pong = pong;

		this.receive = pong.receive;

		this.random = new Random();

		spawn();
	}

	public void update(Paddle paddle1, Paddle paddle2) {
		// speed = 20 * (receive.ball_speed());
		this.x += motionX * speed;
		this.y += motionY * speed;

		if (!pong.four_player)
			if (this.y + height - motionY > pong.height || this.y + motionY < 0) {
				if (this.motionY < 0) {
					this.y = 0;
					this.motionY = random.nextInt(4);

					if (motionY == 0) {
						motionY = 1;
					}
				} else {
					this.motionY = -random.nextInt(4);
					this.y = pong.height - height;

					if (motionY == 0) {
						motionY = -1;
					}
				}
			}

		if (checkCollision(paddle1) == 1) {
			this.motionX = 1 + (amountOfHits / 5);
			this.motionY = -2 + random.nextInt(4);

			if (motionY == 0) {
				motionY = 1;
			}

			amountOfHits++;
			lastHit = 1;
		} else if (checkCollision(paddle2) == 1) {
			this.motionX = -1 - (amountOfHits / 5);
			this.motionY = -2 + random.nextInt(4);

			if (motionY == 0) {
				motionY = 1;
			}

			amountOfHits++;
			lastHit = 2;
		}
		if (!pong.four_player) {
			if (checkCollision(paddle1) == 2) {
				paddle2.score++;
				spawn();
			} else if (checkCollision(paddle2) == 2) {
				paddle1.score++;
				spawn();
			}
		}
	}

	public void update(Paddle paddle1, Paddle paddle2, Paddle paddle3, Paddle paddle4) {
		update(paddle1, paddle2);
		if (checkCollision(paddle3) == 1) {
			this.motionX = -2 + random.nextInt(4);
			this.motionY = 1 + (amountOfHits / 5);

			if (motionX == 0) {
				motionX = 1;
			}

			amountOfHits++;
			lastHit = 3;
		} else if (checkCollision(paddle4) == 1) {
			this.motionX = -2 + random.nextInt(4);
			this.motionY = -1 - (amountOfHits / 5);

			if (motionX == 0) {
				motionX = 1;
			}

			amountOfHits++;
			lastHit = 4;
		}
		if (checkGoal(paddle1, paddle2, paddle3, paddle4)) {
			switch (lastHit) {
			case 1:
				paddle1.score++;
				break;
			case 2:
				paddle2.score++;
				break;
			case 3:
				paddle3.score++;
			case 4:
				paddle4.score++;
			default:
				break;
			}
			spawn();
		}
	}

	public void spawn() {
		this.amountOfHits = 0;
		this.lastHit = 0;
		this.x = pong.width / 2 - this.width / 2;
		this.y = pong.height / 2 - this.height / 2;

		this.motionY = -2 + random.nextInt(4);

		if (motionY == 0) {
			motionY = 1;
		}

		if (random.nextBoolean()) {
			motionX = 1;
		} else {
			motionX = -1;
		}
	}

	public int checkCollision(Paddle paddle) {
		if (this.x < paddle.x + paddle.width && this.x + width > paddle.x && this.y < paddle.y + paddle.height && this.y + height > paddle.y) {
			return 1; // bounce
		} else if ((paddle.x > x && paddle.paddleNumber == 1) || (paddle.x < x - width && paddle.paddleNumber == 2)
				|| (paddle.y > y && paddle.paddleNumber == 3) || (paddle.y < y - height && paddle.paddleNumber == 4)) {
			return 2; // score
		}
		return 0; // nothing
	}

	public boolean checkGoal(Paddle paddle1, Paddle paddle2, Paddle paddle3, Paddle paddle4) {
		if ((paddle1.x > x) || (paddle2.x < x - width) || (paddle3.y > y) || (paddle4.y < y - height)) {
			return true;
		} else {
			return false;
		}
	}

	public void render(Graphics g) {
		if (pong.four_player && lastHit != 0) {
			g.setColor(pong.PaddleColor[lastHit - 1]);
		} else {
			g.setColor(pong.ballColor);
		}
		g.fillOval(x, y, width, height);
	}

}
