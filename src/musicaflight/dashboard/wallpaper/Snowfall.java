
package musicaflight.dashboard.wallpaper;

import static org.lwjgl.opengl.GL11.*;

import musicaflight.avianutils.*;

public class Snowfall extends WallpaperEffect {

	static AvianCircle flake = new AvianCircle();

	int amount = 300;

	Snowflake[] snowflakes = new Snowflake[amount];

	public Snowfall() {
		super(true);
		for (int i = 0; i < amount; i++) {
			if (snowflakes[i] == null)
				snowflakes[i] = new Snowflake();
		}
	}

	@Override
	public void logic() {
		for (int i = 0; i < amount; i++) {
			if (snowflakes[i] == null)
				continue;
			if (snowflakes[i].x < 0 || snowflakes[i].y > AvianApp.getHeight() || snowflakes[i].x > AvianApp.getWidth()) {
				snowflakes[i].reset();
			} else {
				snowflakes[i].logic();
			}
		}
	}

	@Override
	public void render() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		for (int i = 0; i < amount; i++) {
			if (snowflakes[i] == null)
				continue;
			flake.setDiameter(snowflakes[i].size);
			flake.setX(snowflakes[i].x);
			flake.setY(snowflakes[i].y);
			flake.render(true);
		}

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}

	@Override
	public String getName() {
		return "Snowfall";
	}

	private class Snowflake {

		float size, x, y, xv, yv;

		Snowflake() {
			reset();
		}

		public void logic() {
			x += xv;
			y += yv;
		}

		public void reset() {
			size = (AvianMath.randomFloat() * 3f) + 2f;
			x = (AvianMath.randomFloat() * AvianApp.getWidth());
			y = -size;
			xv = (AvianMath.randomFloat() - .5f) / 2f;
			yv = (AvianMath.randomFloat() + 1) / 2f;
		}

	}

}
