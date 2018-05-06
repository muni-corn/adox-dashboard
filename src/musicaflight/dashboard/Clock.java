
package musicaflight.dashboard;

import static org.lwjgl.opengl.GL11.*;

import java.util.Calendar;

import musicaflight.avianutils.*;

public class Clock {

	static AvianCircle c = new AvianCircle();

	static float inactivity;

	static boolean showMinutes, showHours, showSecondHand = true;

	static boolean showOnStartup = true;

	static float seconds;

	static boolean smoothMovement = true;

	static Calendar t;

	public static void logic() {
		inactivity += .01f;

		t = Dashboard.getCurrentCalendar();

		//		if (AvianInput.getMouseDX() != 0 || AvianInput.getMouseDY() != 0 || !Dashboard.clock)
		inactivity = 0;

		if (!Settings.deleting) {
			if (inactivity > 5f) {
				Dashboard.headerTextAlpha = AvianMath.glide(Dashboard.headerTextAlpha, 0, 30f);
			} else {
				Dashboard.headerTextAlpha = AvianMath.glide(Dashboard.headerTextAlpha, 1f, 10f);
			}
		}

		if (smoothMovement || !showSecondHand)
			seconds = t.get(Calendar.SECOND) + (t.get(Calendar.MILLISECOND) / 1000f);
		else
			seconds = t.get(Calendar.SECOND);

	}

	static AvianLine l = new AvianLine();

	static float centerSize;

	public static void render() {
		t = Dashboard.getCurrentCalendar();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		float dashboardOffset = Dashboard.yOffset - AvianApp.getHeight() + 120;
		float alpha = 255f * ((Dashboard.yOffset - 50) / (AvianApp.getHeight() - 170));

		if (showSecondHand) {
			glLineWidth(1f);

			float secLength = AvianApp.getHeight() / 7f;
			float secInput = (seconds / 60f) * 360f;

			l.set(AvianApp.getWidth() / 2, AvianApp.getHeight() / 2 + dashboardOffset, (AvianApp.getWidth() / 2) + AvianMath.sin(secInput) * secLength, (AvianApp.getHeight() / 2) + -AvianMath.cos(secInput) * secLength + dashboardOffset);
			l.render(alpha);
		}

		float handWidth = 2f;

		glLineWidth(handWidth);

		float minLength = AvianApp.getHeight() / 5f;
		float minutes = t.get(Calendar.MINUTE) + (seconds / 60f);
		float minInput = (minutes / 60f) * 360f;
		l.set(AvianApp.getWidth() / 2, AvianApp.getHeight() / 2 + dashboardOffset, (AvianApp.getWidth() / 2) + AvianMath.sin(minInput) * minLength, (AvianApp.getHeight() / 2) + -AvianMath.cos(minInput) * minLength + dashboardOffset);
		l.render(alpha);

		float hrLength = AvianApp.getHeight() / 9f;
		float hours = t.get(Calendar.HOUR) + (t.get(Calendar.MINUTE) / 60f);
		float hrInput = (hours / 12f) * 360f;
		l.set(AvianApp.getWidth() / 2, AvianApp.getHeight() / 2 + dashboardOffset, (AvianApp.getWidth() / 2) + AvianMath.sin(hrInput) * hrLength, (AvianApp.getHeight() / 2) + -AvianMath.cos(hrInput) * hrLength + dashboardOffset);
		l.render(alpha);

		c.set(AvianApp.getWidth() / 2, AvianApp.getHeight() / 2 + dashboardOffset, handWidth);
		c.render(true, alpha);

		if (showHours) {
			for (int i = 0; i < 60; i++) {
				if (i % 5 != 0 && !showMinutes)
					continue;
				int angle = i * (360 / 60);
				if (i % 5 == 0)
					c.set((AvianApp.getWidth() / 2) + AvianMath.sin(angle) * (AvianApp.getHeight() / 4), (AvianApp.getHeight() / 2) + AvianMath.cos(angle) * (AvianApp.getHeight() / 4) + dashboardOffset, handWidth * 3);
				else if (showMinutes) {
					c.set((AvianApp.getWidth() / 2) + AvianMath.sin(angle) * (AvianApp.getHeight() / 4), (AvianApp.getHeight() / 2) + AvianMath.cos(angle) * (AvianApp.getHeight() / 4) + dashboardOffset, handWidth * 1);

				}
				c.render(true, alpha);
			}
		}

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}
}
