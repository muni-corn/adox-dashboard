
package musicaflight.dashboard;

import musicaflight.avianutils.*;

public abstract class ThreadUtility implements Runnable {

	Thread thread = null;

	protected String[] args;

	String message;

	static boolean threadInProgress;

	static AvianRectangle bg = new AvianRectangle(0, 0, 0, 0);

	boolean threadWorking;

	public ThreadUtility(String message, String... args) {
		this.args = args;
		this.message = message;
	}

	public void start() {
		threadWorking = true;
		thread = new Thread(this, "threadUtil");
		thread.start();
	}

	public void destroyThread() {
		thread = null;
		threadWorking = false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public abstract void task();

	public abstract void whenFinished();

	@Override
	public void run() {
		task();
		destroyThread();
		whenFinished();

	}

	public boolean isWorking() {
		return threadWorking;
	}

	float x;

	@SuppressWarnings("deprecation")
	public void render() {
		if (!isWorking())
			return;
		float dt = AvianApp.getDeltaRenderTime();
		x += 10f * dt;
		x %= 8f;

		//8.2f % 8f == .2f 

		bg.set(0, 0, AvianApp.getWidth(), AvianApp.getHeight());
		bg.render(AvianColor.black(150));

		Fonts.Vegur_Small.drawString(message, 10, AvianApp.getHeight() - 20, AvianColor.white(255), AvianFont.ALIGN_LEFT);

		for (int i = -1; i <= AvianApp.getWidth() / 8; i++) {
			Images.pg.render(i * 8 + x, AvianApp.getHeight() - 8);
		}
	}

}