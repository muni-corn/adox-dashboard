
package musicaflight.dashboard;

import static org.lwjgl.opengl.GL11.*;

import java.util.Set;

import musicaflight.avianutils.*;

public class MainClass extends AvianApp {

	public static enum Screen {
		MAIN,
	}

	public static Screen screen = Screen.MAIN;

	private static Dashboard dashboard;

	public static Dashboard getDash() {
		return dashboard;
	}

	public void construct() {
		dashboard = new Dashboard();
		Data.loadData();
	}

	public void setupGL() {
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_STENCIL_TEST);
		glShadeModel(GL_SMOOTH);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);

		glLightModelfv(GL_LIGHT_MODEL_AMBIENT, AvianUtils.asFlippedFloatBuffer(0f, 0f, 0f, 1f));

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	
	
	public void keyboard() {
		if (AvianInput.isKeyDown(AvianInput.KEY_F5)) {
			AvianApp.newNotification("Random number", "" + AvianMath.randomFloat(), 0, false);
		}
		if (AvianInput.isKeyDown(AvianInput.KEY_SPACE))
			if (MusicPlayer.music.isPlaying())
				MusicPlayer.music.pause();
			else
				MusicPlayer.music.play();

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);

		for (Thread t : threads) {
			if (t.getName().equals("threadUtil"))
				return;
		}

		dashboard.keyboard();
	}

	float mouseX, mouseY;

	public void mouse() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);

		for (Thread t : threads) {
			if (t.getName().equals("threadUtil"))
				return;
		}

		mouseX = (AvianInput.getMouseX()) - (getWidth() / 2);
		mouseY = (getHeight() - AvianInput.getMouseY()) - (getHeight() / 2);
		dashboard.mouse();
	}

	public void logic() {
		keyboard();
		mouse();
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);

		for (Thread t : threads) {
			if (t.getName().equals("threadUtil"))
				return;
		}

		dashboard.logic();
	}

	public void render() {
		getCam().applyTranslations();

		getCam().applyAvianOrthoMatrix();

		dashboard.render();
	}

	public String customTitle() {
		return "Avian Dashboard";
	}

	public void checkAudio() {
	}

	public static void main(String[] args) {
		MainClass main = new MainClass();
		main.setSize(600, 700);
		main.setResizable(true);
		main.setUndecorated(false);
		main.setAppNameAndVersion("Avian Dashboard", "Specimen 1");
		//		main.setIcons("/res/photos/icon16.png", "/res/photos/icon32.png");
		addShutdownTask(new ShutdownTask() {
			public void run() {
				Dashboard.destroy();
			}
		});
		main.addFontBank(new Fonts());
		main.addImageBank(new Images());
		main.start();
	}

}
