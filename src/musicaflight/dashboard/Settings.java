
package musicaflight.dashboard;

import java.io.File;
import java.text.DecimalFormat;

import musicaflight.avianutils.*;
import musicaflight.dashboard.wallpaper.Wallpaper;

public class Settings {

	static boolean mouseClicked;

	public static int selection = 0;
	static float highlightY;

	static AvianRectangle r = new AvianRectangle();

	static float transition, deleteTransition;
	static boolean nextScreen;

	public static enum Screen {
		UPDATE,
		WALLPAPER,
		DATA,
		CLOCK;
	}

	static Screen s = Screen.UPDATE;

	static float mx, my;
	public static int numberOfOptions = 5;

	public static void mouse() {
		mx = AvianInput.getMouseX();
		my = AvianInput.getMouseY();

		selection = (int) (((my - 120f) / (AvianApp.getHeight() - 120f)) * numberOfOptions);

		if (selection < 0)
			selection = 0;

		if (nextScreen) {
			switch (s) {
				case DATA:
					break;
				case UPDATE:
					break;
				case WALLPAPER:
					Wallpaper.settingsMouse();
					break;
				case CLOCK:
					break;
				default:
					break;
			}
		}

		if (AvianInput.isMouseButtonDown(0)) {
			if (!mouseClicked) {
				if (!nextScreen) {
					switch (selection) {
						case 0:
							s = Screen.UPDATE;
							numberOfOptions = 2;
							UpdateChecker.checkForUpdate();
							nextScreen = true;
							break;
						case 1:
							Dashboard.darkerElements = !Dashboard.darkerElements;
							break;
						case 2:
							s = Screen.WALLPAPER;
							numberOfOptions = 3;
							nextScreen = true;
							break;
						case 3:
							s = Screen.CLOCK;
							numberOfOptions = 5;
							nextScreen = true;
							break;
						case 4:
							s = Screen.DATA;
							numberOfOptions = 3;
							nextScreen = true;
							break;
					}
				} else {
					switch (s) {
						case DATA:
							if (deleting) {
								deleting = false;
								askDeletion = false;
								nextScreen = false;
							} else if (askDeletion) {
								switch (selection) {
									case 1:
										if (!deleting) {
											askDeletion = false;
										}
										break;
									case 2:
										if (!deleting)
											deleting = true;
										break;
								}
							} else {
								if (selection == 1) {
									askDeletion = true;
								}
							}
							break;
						case UPDATE:
							if (UpdateChecker.available()) {
								UpdateChecker.downloadUpdate();
							} else if (UpdateChecker.getError() != null) {
								UpdateChecker.checkForUpdate();
							}
							break;
						case WALLPAPER:
							if (!Wallpaper.changeImages) {
								switch (selection) {
									case 0:
										Wallpaper.changeImages = true;
										break;
									case 1:
										Wallpaper.blur = !Wallpaper.blur;
										if (Wallpaper.blur)
											Wallpaper.newPaper = Wallpaper.blurpapers.get(Wallpaper.currentPaper);
										else
											Wallpaper.newPaper = Wallpaper.papers.get(Wallpaper.currentPaper);
										break;
								}
							}
							break;
						case CLOCK:
							switch (selection) {
								case 0:
									Clock.showOnStartup = !Clock.showOnStartup;
									break;
								case 1:
									Clock.showSecondHand = !Clock.showSecondHand;
									break;
								case 2:
									Clock.smoothMovement = !Clock.smoothMovement;
									break;
								case 3:
									Clock.showHours = !Clock.showHours;
									break;
								case 4:
									Clock.showMinutes = !Clock.showMinutes;
									break;
							}
							break;
						default:
							break;

					}
				}
			}
			mouseClicked = true;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			mouseClicked = false;
		}
	}

	public static void logic() {
		if (!nextScreen)
			numberOfOptions = 5;

		Wallpaper.settingsLogic(transition);

		if (nextScreen) {

			switch (s) {
				case DATA:
					if (!deleting && !askDeletion) {
						numberOfOptions = 2;
						selection = 1;
						break;
					}
					if (deleting) {
						milliseconds++;
						if (milliseconds > 1000) {
							Dashboard.deleteData = true;
							AvianApp.close();
						}
						if (selection < 2)
							selection = 2;
					} else if (askDeletion) {
						numberOfOptions = 3;
						if (selection < 1)
							selection = 1;
					}
					break;
				case UPDATE:
					selection = 1;
					break;
				case WALLPAPER:
					break;
				case CLOCK:
					break;
				default:
					break;

			}

			if (transition < 90)
				transition += 5;
			if (transition > 90)
				transition = 90;
			if (askDeletion || deleting) {
				if (deleteTransition < 90)
					deleteTransition += 5;
				if (deleteTransition > 90)
					deleteTransition = 90;
			} else {
				if (deleteTransition > 0)
					deleteTransition -= 5;
				if (deleteTransition < 0)
					deleteTransition = 0;
			}
		} else {
			milliseconds = 0;
			if (transition > 0)
				transition -= 5;
			if (transition < 0)
				transition = 0;
			if (deleteTransition > 0)
				deleteTransition -= 5;
			if (deleteTransition < 0)
				deleteTransition = 0;

		}
		if (selection > numberOfOptions - 1)
			selection = numberOfOptions - 1;
		highlightY = AvianMath.glide(highlightY, 120 + (selection * ((AvianApp.getHeight() - 120f) / numberOfOptions)), 10f);
	}

	static AvianCircle c2 = new AvianCircle();
	static AvianArc a = new AvianArc();
	static boolean deleting, askDeletion;
	public static float milliseconds;
	static DecimalFormat d = new DecimalFormat("0");
	static DecimalFormat d2 = new DecimalFormat("0.##");

	@SuppressWarnings("deprecation")
	public static void render() {
		AvianFont v = Fonts.Vegur_Small;

		r.set(0, highlightY, AvianApp.getWidth(), (AvianApp.getHeight() - 120f) / numberOfOptions);

		float alpha = AvianMath.cos(transition) * 255f;

		if (transition == 0)
			r.render(AvianColor.white(50));

		v.drawString("Check for update", 20, (int) (120f + (AvianApp.getHeight() - 120) * (1f / 10f)), AvianColor.white(alpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		v.drawString("Darken elements", 20, (int) (120f + (AvianApp.getHeight() - 120) * (3f / 10f)), AvianColor.white(alpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		if (Dashboard.darkerElements)
			Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth(), (int) ((120f + (AvianApp.getHeight() - 120) * (3f / 10f)) - Images.checked_box.getHeight() / 2f), alpha);
		else
			Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth(), (int) ((120f + (AvianApp.getHeight() - 120) * (3f / 10f)) - Images.unchecked_box.getHeight() / 2f), alpha);
		v.drawString("Wallpaper", 20, (int) (120f + (AvianApp.getHeight() - 120) * (5f / 10f)), AvianColor.white(alpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		v.drawString("Clock", 20, (int) (120f + (AvianApp.getHeight() - 120) * (7f / 10f)), AvianColor.white(alpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		v.drawString("Data", 20, (int) (120f + (AvianApp.getHeight() - 120) * (9f / 10f)), AvianColor.white(alpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);

		float o = (1f - AvianMath.sin(transition)) * (AvianApp.getWidth() / 8f);

		float thisAlpha = AvianMath.sin(transition) * 255f;

		if (transition > 0) {
			switch (s) {
				case CLOCK:
					r.render(AvianColor.white(50));

					v.drawString("Show on startup", 20 + (int) o, (int) (120f + (AvianApp.getHeight() - 120) * (1f / 10f)), AvianColor.white(thisAlpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
					if (Clock.showOnStartup)
						Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (1f / 10f)) - Images.checked_box.getHeight() / 2f), thisAlpha);
					else
						Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (1f / 10f)) - Images.unchecked_box.getHeight() / 2f), thisAlpha);

					v.drawString("Show second hand", 20 + (int) o, (int) (120f + (AvianApp.getHeight() - 120) * (3f / 10f)), AvianColor.white(thisAlpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
					if (Clock.showSecondHand)
						Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (3f / 10f)) - Images.checked_box.getHeight() / 2f), thisAlpha);
					else
						Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (3f / 10f)) - Images.unchecked_box.getHeight() / 2f), thisAlpha);

					v.drawString("Smooth movement", 20 + (int) o, (int) (120f + (AvianApp.getHeight() - 120) * (5f / 10f)), AvianColor.white(Clock.showSecondHand ? thisAlpha : thisAlpha / 3), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
					if (Clock.smoothMovement)
						Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (5f / 10f)) - Images.checked_box.getHeight() / 2f), Clock.showSecondHand ? thisAlpha : thisAlpha / 3);
					else
						Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (5f / 10f)) - Images.unchecked_box.getHeight() / 2f), Clock.showSecondHand ? thisAlpha : thisAlpha / 3);

					v.drawString("Show hour marks", 20 + (int) o, (int) (120f + (AvianApp.getHeight() - 120) * (7f / 10f)), AvianColor.white(thisAlpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
					if (Clock.showHours)
						Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (7f / 10f)) - Images.checked_box.getHeight() / 2f), thisAlpha);
					else
						Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (7f / 10f)) - Images.unchecked_box.getHeight() / 2f), thisAlpha);

					v.drawString("Show minute marks", 20 + (int) o, (int) (120f + (AvianApp.getHeight() - 120) * (9f / 10f)), AvianColor.white(Clock.showHours ? thisAlpha : thisAlpha / 3), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
					if (Clock.showMinutes)
						Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (9f / 10f)) - Images.checked_box.getHeight() / 2f), Clock.showHours ? thisAlpha : thisAlpha / 3);
					else
						Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth() + o, (int) ((120f + (AvianApp.getHeight() - 120) * (9f / 10f)) - Images.unchecked_box.getHeight() / 2f), Clock.showHours ? thisAlpha : thisAlpha / 3);

					break;
				case DATA:
					r.render(AvianColor.white(50));
					float deleteOffset = o + (1f - AvianMath.sin(deleteTransition)) * (AvianApp.getWidth() / 8f);
					float deleteAlpha = thisAlpha * AvianMath.sin(deleteTransition);

					long size = userFolderSize();
					String bytes = d2.format(size) + " bytes";

					if (size > 1000000000f) {
						bytes = d2.format(size / 1000000000f) + " GB";
					} else if (size > 1000000f) {
						bytes = d2.format(size / 1000000f) + " MB";
					} else if (size > 1000f) {
						bytes = d2.format(size / 1000f) + " KB";
					}

					v.drawString("Your data is using up " + bytes + " on your computer.", (AvianApp.getWidth() / 2 + o), (120 + ((AvianApp.getHeight() - 120f) / 4f)), AvianColor.white((1f - (deleteAlpha / 255f)) * thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					v.drawString("Delete all data", (AvianApp.getWidth() / 2 + o), (120 + ((AvianApp.getHeight() - 120f) * (3f / 4f))), AvianColor.white((1f - (deleteAlpha / 255f)) * thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

					if (deleting) {
						//						c2.setXYD(AvianAppCore.getWidth() / 2, 120 + (AvianAppCore.getHeight() - 120) / 2, 50);
						//						c2.render(AvianColor.white(50), false);
						a.set(AvianApp.getWidth() / 2, 120 + (AvianApp.getHeight() - 120) / 2, 25, (((milliseconds % 100) / 100f) * 360f) - (((1000f - milliseconds) / 1000f) * 360f), ((milliseconds % 100) / 100) * 360);
						a.render(1f);
						v.drawString("Confirming deletion...", (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) / 6f)), AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						Fonts.Vegur_ExtraSmall.drawString("Avian Dashboard will close after your data has been erased.", (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) / 6f)) + 30, AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						v.drawString("" + ((9 - ((int) milliseconds > 999 ? 999 : (int) milliseconds) / 100)), (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) * (3f / 6f))), AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						v.drawString("Click to cancel.", (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) * (5f / 6f))), AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					} else {
						v.drawString("Delete all of your data?", (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) / 6f)), AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						v.drawString("No", (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) * (3f / 6f))), AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						v.drawString("Yes", (AvianApp.getWidth() / 2 + deleteOffset), (120 + ((AvianApp.getHeight() - 120f) * (5f / 6f))), AvianColor.white(deleteAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					}
					break;
				case UPDATE:
					if (UpdateChecker.available()) {
						r.render(AvianColor.white(50));
						v.drawString(UpdateChecker.getAvailableVersion() + " is available.", AvianApp.getWidth() / 2 + (int) (o), 120 + (AvianApp.getHeight() - 120) / 4, AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						v.drawString("Download and Install", AvianApp.getWidth() / 2 + (o), (120 + (AvianApp.getHeight() - 120) * (3f / 4f)), AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					} else if (UpdateChecker.downloading()) {
						v.drawString("Downloading " + UpdateChecker.getAvailableVersion() + "...", AvianApp.getWidth() / 2 + (int) (o), 120 + (AvianApp.getHeight() - 120) / 4, AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						r.set(20 + o, 120 + (AvianApp.getHeight() - 120) * (3f / 4f), AvianApp.getWidth() - 40, 2);
						r.render(AvianColor.black(AvianMath.sin(transition) * 150));
						r.set(20 + o, 120 + (AvianApp.getHeight() - 120) * (3f / 4f), (AvianApp.getWidth() - 40) * UpdateChecker.getProgress(), 2);
						r.render(AvianColor.white(thisAlpha));
					} else if (UpdateChecker.checking()) {
						v.drawString("Checking for an update...", AvianApp.getWidth() / 2 + (o), (120 + (AvianApp.getHeight() - 120) / 2f), AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					} else if (UpdateChecker.upToDate()) {
						v.drawString("Your dashboard is up to date.", AvianApp.getWidth() / 2 + (o), (120 + (AvianApp.getHeight() - 120) / 2f), AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					} else if (UpdateChecker.getError() != null) {
						r.render(AvianColor.white(50));
						v.drawString("An error occurred while checking for an update.", AvianApp.getWidth() / 2 + (o), (120 + (AvianApp.getHeight() - 120) / 4f), AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						Fonts.Vegur_ExtraSmall.drawString(UpdateChecker.getError(), AvianApp.getWidth() / 2 + (o), (150 + (AvianApp.getHeight() - 120) / 4f), AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
						Fonts.Vegur_ExtraSmall.drawString("Try checking your Internet connection, then click to try again.", AvianApp.getWidth() / 2 + (o), (120 + (AvianApp.getHeight() - 120) * (3f / 4f)), AvianColor.white(thisAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					}
					break;
				case WALLPAPER:
					r.render(AvianColor.white(50 * (1f - AvianMath.sin(Wallpaper.imagesTransition))));
					Wallpaper.settingsRender();
					break;
				default:
					break;

			}
		}
	}

	public static void cleanUpSettings() {
		nextScreen = false;
		Wallpaper.fileBrowser = false;
		Wallpaper.changeImages = false;
		numberOfOptions = 5;
		deleting = false;
		askDeletion = false;
		milliseconds = 0;
	}

	private static long userFolderSize() {
		File directory = Data.userRoot;
		long length = 0;
		for (int i = 0; i < directory.listFiles().length; i++) {
			File file = directory.listFiles()[i];
			if (file.isDirectory())
				length += folderSize(file);
			else
				length += file.length();
		}
		return length;
	}

	private static long folderSize(File dir) {
		long length = 0;
		for (int i = 0; i < dir.listFiles().length; i++) {
			File file = dir.listFiles()[i];
			if (file.isDirectory())
				length += folderSize(file);
			else
				length += file.length();
		}
		return length;
	}
}
