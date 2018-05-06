
package musicaflight.dashboard.games;

import musicaflight.avianutils.*;

public class Breakout extends Game {

	public Breakout() {
		super(true);
	}

	static float xVel = 100, yVel = 100;
	static float ballX, ballY;
	static float paddleX;

	static AvianRectangle ball = new AvianRectangle(0, 0, 3, 3);
	static AvianRectangle paddle = new AvianRectangle(0, 0, 50, 3);

	@Override
	protected void gameKeyboard() {
		// TODO Auto-generated method stub

	}

	public void gameMouse() {
		paddleX = mx - 25;

		if (AvianInput.isMouseButtonDown(1)) {
			xVel = 100;
			yVel = 100;
			ballX = 0;
			ballY = 0;
		}
	}

	public void gameLogic() {
		ballX += xVel / 100f;
		ballY += yVel / 100f;

		if (ballX < 0) {
			ballX = 0;
			xVel *= -1;
		}
		if (ballY < 0) {
			ballY = 0;
			yVel *= -1;
		}
		if (ballX + ball.getH() > width) {
			ballX = width - ball.getH();
			xVel *= -1;
		}
		if (ballX + ball.getW() > paddleX && ballX < paddleX + paddle.getW() && ballY < paddle.getY() + paddle.getH()) {
			if (ballY + ball.getH() > paddle.getY()) {
				ballY = paddle.getY() - ball.getH();
				yVel *= -1.1;
			}
		}
	}

	public void gameRender() {
		ball.set(ballX, ballY, 15, 15);
		ball.render(new AvianColor(255, 255, 255));

		if (paddleX > width - paddle.getW() - paddle.getH())
			paddleX = width - paddle.getW() - paddle.getH();
		if (paddleX < paddle.getH())
			paddleX = paddle.getH();

		paddle.set(paddleX, 294, 50, 3);
		paddle.render(new AvianColor(255, 255, 255));
	}

	@Override
	public String getName() {
		return "Breakout";
	}

}
