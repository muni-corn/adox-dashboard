
package musicaflight.dashboard;

import java.text.DecimalFormat;

import musicaflight.avianutils.*;

public class Stopwatch extends Element {

	public Stopwatch() {
		super(50, false);
	}

	boolean running = false;
	long milliseconds;
	long last;
	long current;

	@Override
	public void contentKeyboard() {

	}

	@Override
	public void contentMouse() {
		if (!hasFocus)
			return;
		if (AvianInput.isMouseButtonDown(0)) {
			if (!contentClick) {
				if (!running) {
					last = current = System.currentTimeMillis();
					running = true;
				} else {
					running = false;
				}
			}
			contentClick = true;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			contentClick = false;
		}
		if (AvianInput.isMouseButtonDown(1)) {
			if (!contentClick) {
				if (!running) {
					milliseconds = 0;
				}
			}
			contentClick = true;
		}

	}

	@Override
	public void contentLogic() {
		if (running) {
			current = System.currentTimeMillis();
			milliseconds += current - last;
			last = current;
		}
	}

	DecimalFormat f = new DecimalFormat("00");

	@Override
	public void contentRender() {

		int s = (int) ((milliseconds / (1000)) % 60);
		int m = (int) ((milliseconds / (1000 * 60)) % 60);
		int h = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

		Fonts.SFDR.drawString(h + ":" + f.format(m) + ":" + f.format(s) + "." + f.format((milliseconds % 1000) / 10), getWidth() / 2f, getHeight() / 2f, AvianColor.white(running ? 255 : 150), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

	}

	@Override
	public void settingsKeyboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void settingsMouse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void settingsLogic() {
		// TODO Auto-generated method stub

	}

	@Override
	public void settingsRender() {
		// TODO Auto-generated method stub

	}

	@Override
	public void settingsOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {

	}

	@Override
	public String getName() {
		return "Stopwatch";
	}

	@Override
	public Element create() {
		return new Stopwatch();
	}

}
