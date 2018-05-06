
package musicaflight.dashboard.games;

import musicaflight.avianutils.*;
import musicaflight.dashboard.Fonts;
import musicaflight.dashboard.GameScreen;

public abstract class Game {

	boolean pausable = true;

	public Game(boolean pausable) {
		this.pausable = pausable;
	}

	public void keyboard() {
		if (!pausable || (pausable && !GameScreen.paused)) {
			gameKeyboard();
		}
	}

	public void mouse(float mouseX, float mouseY) {
		Game.mx = mouseX;
		Game.my = mouseY;
		if (!pausable || (pausable && !GameScreen.paused)) {
			gameMouse();
		}
	}

	public void logic(float w, float h) {
		Game.width = w;
		Game.height = h;
		if (!pausable || (pausable && !GameScreen.paused)) {
			gameLogic();
		}
	}

	private AvianRectangle fade = new AvianRectangle();

	public void render() {
		gameRender();
		if (GameScreen.paused && pausable) {
			fade.set(0, 0, GameScreen.width, GameScreen.height);
			fade.render(AvianColor.black(50));
			Fonts.Vegur_Small.drawString("Click to resume...", (AvianApp.getWidth() / 2) - 20, (GameScreen.height / 2f), AvianColor.white(GameScreen.focus ? 200 : 100), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
		}
	}

	static float mx, my;
	static float width, height;

	protected abstract void gameKeyboard();

	protected abstract void gameMouse();

	protected abstract void gameLogic();

	protected abstract void gameRender();

	public abstract String getName();

}
