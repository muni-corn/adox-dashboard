
package musicaflight.dashboard.wallpaper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import javax.imageio.ImageIO;

import musicaflight.avianutils.*;
import musicaflight.dashboard.*;

public class Wallpaper {

	boolean changerHover;

	String musicName;

	boolean looping = false;

	static int slotOffset;

	static float scrollAlpha;

	static File folder = new File("C:/");

	static ThreadUtility changer;

	static AvianRectangle scroll = new AvianRectangle(0, 0, 3, 0);

	static File[] currentDirectory = listImagesInFolder(folder);

	float height;
	int slot;
	float y;

	static float mx, my;

	boolean hoverGear = false;
	float gearX;

	boolean contentClick = false;
	public static boolean settingsClick = false;

	boolean hasFocus;

	static float scrollSeconds;

	static WallpaperEffect effect;

	public static int intervalInSeconds;
	public static float currentSeconds;
	public static int currentPaper = -1;
	public static ArrayList<String> names = new ArrayList<String>();
	public static ArrayList<AvianImage> papers = new ArrayList<AvianImage>();
	public static ArrayList<AvianImage> blurpapers = new ArrayList<AvianImage>();
	public static int changePaper = -1;
	public static AvianImage newPaper, oldPaper;
	public static float newPaperSin;

	public static enum WallpaperSettings {
		BUBBLES,
		LINES,
		RIPPLES,
		DAYLIGHT,
		CUSTOM;
	}

	static {
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void release(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void move(float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void scroll(int count) {
				scrollAlpha = 150f;
				scrollSeconds = 0;
				slotOffset += count * 30;

				if (slotHighlight == 1 && papers.size() > 1) {
					if (intervalInSeconds >= 60) {
						System.out.println("hello");
						if (count > 0) {
							if (intervalInSeconds >= 3600) {
								intervalInSeconds += (count) * 3600;
							} else if (intervalInSeconds >= 300) {
								intervalInSeconds += (count) * 300;
							} else if (intervalInSeconds >= 60) {
								intervalInSeconds += (count) * 60;
							}
						} else if (count < 0) {
							if (intervalInSeconds >= 3600) {
								if (intervalInSeconds == 3600) {
									intervalInSeconds += (count) * 300;
								} else {
									intervalInSeconds += (count) * 3600;
								}
							} else if (intervalInSeconds >= 300) {
								if (intervalInSeconds == 300) {
									intervalInSeconds += (count) * 60;
								} else {
									intervalInSeconds += (count) * 300;
								}
							} else if (intervalInSeconds >= 60) {
								if (intervalInSeconds == 60) {
									intervalInSeconds += (count) * 5;
								} else {
									intervalInSeconds += (count) * 60;
								}
							}
						}
					} else {
						System.out.println("hi");
						intervalInSeconds += (count) * 5;
					}
				}
			}

		});
	}

	public static void settingsMouse() {

		mx = AvianInput.getMouseX();
		my = AvianInput.getMouseY();

		if (fileBrowser) {

			if (AvianInput.isMouseButtonDown(1) || (AvianInput.isMouseButtonDown(0) && my < 120 && !settingsClick))
				Dashboard.closePanel();

			if (AvianInput.isMouseButtonDown(0)) {
				if (!settingsClick) {
					if (my > 185) {
						int i = (int) ((my - 180f) / 30f) - slotOffset;
						try {
							listImagesInFolder(currentDirectory[i]);
						} catch (ArrayIndexOutOfBoundsException e) {

						}
					} else if (my < 185 && my > 150 && folder.getParentFile() != null) {
						listImagesInFolder(folder.getParentFile());
					} else if (my < 150 && my > 120 && mx < AvianApp.getWidth() / 2) {
						listImagesInFolder(new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Microsoft\\Windows\\Themes\\TranscodedWallpaper.jpg"));
					}
				}
				settingsClick = true;
			} else if (!AvianInput.isMouseButtonDown(0)) {
				settingsClick = false;
			}

		} else if (changeImages) {

			float h = ((AvianApp.getHeight() - 120f) / (names.size() + (names.size() <= 1 ? 3f : 4f)));
			slotHighlight = (int) ((my - 120) / h);

			if (slotHighlight > names.size() + (names.size() <= 1 ? 2f : 3f))
				slotHighlight = names.size() + (names.size() <= 1 ? 2 : 3);
			else if (slotHighlight < 0)
				slotHighlight = 0;

			if (slotHighlight < 1 || slotHighlight > names.size() + (names.size() <= 1 ? 3f : 4f) - 2)
				highlightOff = true;
			else
				highlightOff = false;

			if (slotHighlight == 1 && papers.size() > 1) {

			} else {
				if (AvianInput.isMouseButtonDown(0)) {
					if (!settingsClick) {
						if (AvianInput.isMouseButtonDown(0) && slotHighlight == names.size() + (names.size() <= 1 ? 1f : 2f)) {
							if (!delete)
								fileBrowser = true;
							else {
								for (int i = papers.size() - 1; i > 0; i--) {
									papers.get(i).destroy();
									papers.remove(i);
									blurpapers.get(i).destroy();
									blurpapers.remove(i);
									names.remove(i);
								}
							}
						} else if (slotHighlight - (names.size() <= 1 ? 1f : 2f) < papers.size() && slotHighlight - (names.size() <= 1 ? 1f : 2f) >= 0) {
							//							System.out.println(slotHighlight - (names.size() <= 1 ? 1 : 2));
						}
					}
					settingsClick = true;
				} else if (!AvianInput.isMouseButtonDown(0)) {
					settingsClick = false;
				}
			}
		}
	}

	public static boolean changeImages;
	public static float imagesTransition = 0;
	public static boolean fileBrowser = false;
	public static float filesTransition = 0;
	static float transition;
	public static float wallAlpha;

	public static void blurWallpaper(int i) {
		AvianImage ai = papers.get(i);

		ai.gaussianBlur(40);
		String filepath = null;
		try {
			ImageIO.write(ai.getBufferedImage(), "png", new File(filepath = "C:\\Avian\\" + System.getProperty("user.name") + "\\dashboard\\wallpapers\\blurWallpaper" + i + ".png"));
		} catch (IOException e) {}
		blurpapers.set(i, new AvianImage(filepath));
		ai.gaussianBlur(0);
	}

	public static void wallpaperLogic() {

		if (newPaperSin < 90f) {
			newPaperSin += .3f;
		}
		if (newPaperSin > 90f)
			newPaperSin = 90f;

		if (effect != null)
			effect.logic();

		for (int i = 0; i < papers.size(); i++) {
			if (blurpapers.get(i) == null || blurpapers.get(i).getImageFilepath() == null || blurpapers.get(i).getImageFilepath() == "") {
				blurWallpaper(i);
			}
		}
		if (intervalInSeconds < 5) {
			intervalInSeconds = 5;
		}

		if (!Dashboard.intro) {
			currentSeconds += .01f;
		}
		if (papers.size() > 0 && (currentPaper == -1 || (currentSeconds >= intervalInSeconds))) {
			currentSeconds = 0;
			rotate();
		}

		wallAlpha = AvianMath.glide(wallAlpha, Dashboard.settings ? (1f - ((Settings.milliseconds / 500))) : 1f, 20f);
	}

	public static void rotate() {
		if (papers.size() == 0) {
			currentPaper = -1;
			return;
		} else if (papers.size() == 1) {
			if (currentPaper != 0) {
				newPaperSin = 0f;
				currentPaper = 0;
				if (blur)
					newPaper = blurpapers.get(currentPaper);
				else
					newPaper = papers.get(currentPaper);
			}
			return;
		}
		newPaperSin = 0f;
		currentPaper++;
		if (currentPaper >= papers.size()) {
			currentPaper = 0;
		}

		oldPaper = newPaper;
		if (blur)
			newPaper = blurpapers.get(currentPaper);
		else
			newPaper = papers.get(currentPaper);

	}

	static float arcEnd, barAlpha;

	static float gb = 255;
	static int slotHighlight;
	static float sHY;
	static float sHH;
	static boolean delete;
	static boolean highlightOff = false;
	static float highlightAlpha;

	public static void settingsLogic(float trans) {
		transition = trans;

		scrollSeconds += .01f;

		if (scrollSeconds > 1f)
			scrollAlpha -= 5f;

		if ((AvianApp.getHeight() - 150) / 30 > currentDirectory.length)
			slotOffset = 0;
		else {
			if (slotOffset < -currentDirectory.length + (AvianApp.getHeight() - 150) / 30 - 1)
				slotOffset = -currentDirectory.length + (AvianApp.getHeight() - 150) / 30 - 1;
			if (slotOffset > 0)
				slotOffset = 0;

		}

		delete = (AvianInput.isKeyDown(AvianInput.KEY_DELETE));

		scrollSeconds += .01f;

		if (highlightOff)
			highlightAlpha = AvianMath.glide(highlightAlpha, 0, 15f);
		else
			highlightAlpha = AvianMath.glide(highlightAlpha, (255f / 5f), 3f);

		if (delete)
			gb = AvianMath.glide(gb, 50f, 5f);
		else
			gb = AvianMath.glide(gb, 255f, 5f);

		float h = ((AvianApp.getHeight() - 120f) / (names.size() + (names.size() <= 1 ? 3f : 4f)));
		sHY = AvianMath.glide(sHY, 120 + slotHighlight * h, 3f);
		sHH = h;

		if (fileBrowser) {
			if (filesTransition < 90)
				filesTransition += 5;
			if (filesTransition > 90)
				filesTransition = 90;
		} else {
			if (filesTransition > 0)
				filesTransition -= 5;
			if (filesTransition < 0)
				filesTransition = 0;

		}
		if (changeImages) {

			if (imagesTransition < 90)
				imagesTransition += 5;
			if (imagesTransition > 90)
				imagesTransition = 90;
		} else {
			if (imagesTransition > 0)
				imagesTransition -= 5;
			if (imagesTransition < 0)
				imagesTransition = 0;

		}

		arcEnd = /*AvianMath.glide(arcEnd, */360f - ((currentSeconds / intervalInSeconds) * 360f)/*, 10f)*/;
		barAlpha = AvianMath.glide(barAlpha, (1f - (currentSeconds / intervalInSeconds)) * 50f, 10f);
	}

	static AvianRectangle line = new AvianRectangle(20, 0, AvianApp.getWidth() - 40, 1);
	static AvianRectangle black = new AvianRectangle(0, 0, AvianApp.getWidth(), AvianApp.getHeight());

	static AvianArc arc = new AvianArc();
	static AvianRectangle highlight = new AvianRectangle(0, 0, 0, 0);

	static DecimalFormat d = new DecimalFormat("0");

	@SuppressWarnings("deprecation")
	public static void settingsRender() {
		float oneEighth = (AvianApp.getWidth() / 8f);
		float sin = AvianMath.sin(transition);
		float imgSin = AvianMath.sin(imagesTransition);
		float fileSin = AvianMath.sin(filesTransition);
		float offset = (1f - sin) * oneEighth;
		float imgOffset = offset + (1f - imgSin) * oneEighth;
		float filesOffset = offset + imgOffset + (1f - fileSin) * oneEighth;

		float firstMenuAlpha = sin * AvianMath.cos(filesTransition) * AvianMath.cos(imagesTransition) * 255f;

		float imgAlphaMultiplier = sin * imgSin * AvianMath.cos(filesTransition);
		float imgAlpha = imgAlphaMultiplier * 255f;

		float filesAlphaMultiplier = sin * imgSin * fileSin;
		float filesAlpha = filesAlphaMultiplier * 255f;

		AvianFont vs = Fonts.Vegur_Small;

		vs.drawString("Change images", 20f + offset, 120f + (AvianApp.getHeight() - 120) * (1f / 6f), AvianColor.white(firstMenuAlpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		vs.drawString("Blur wallpaper", 20f + offset, 120f + (AvianApp.getHeight() - 120) * (3f / 6f), AvianColor.white(firstMenuAlpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		vs.drawString("Change effect", 20f + offset, 120f + (AvianApp.getHeight() - 120) * (5f / 6f), AvianColor.white(firstMenuAlpha), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);

		if (blur)
			Images.checked_box.render(AvianApp.getWidth() - 20 - Images.checked_box.getWidth() + offset, ((120f + (AvianApp.getHeight() - 120) * (3f / 6f)) - Images.checked_box.getHeight() / 2f), firstMenuAlpha);
		else
			Images.unchecked_box.render(AvianApp.getWidth() - 20 - Images.unchecked_box.getWidth() + offset, ((120f + (AvianApp.getHeight() - 120) * (3f / 6f)) - Images.unchecked_box.getHeight() / 2f), firstMenuAlpha);

		if (imagesTransition > 0) {

			int n = Settings.numberOfOptions = papers.size() + (papers.size() > 1 ? 4 : 2);

			highlight.set(20, sHY, AvianApp.getWidth() - 40, sHH);
			highlight.render(AvianColor.white(imgAlphaMultiplier * highlightAlpha));

			if (papers.size() > 1) {
				highlight.set(20 + imgOffset, 120 + (2 + currentPaper) * ((AvianApp.getHeight() - 120f) / n), 20, (AvianApp.getHeight() - 120f) / n);
				highlight.render(AvianColor.white(barAlpha * imgAlphaMultiplier));
				highlight.set(AvianApp.getWidth() - 40 + imgOffset, 120 + (2 + currentPaper) * ((AvianApp.getHeight() - 120f) / n), 20, (AvianApp.getHeight() - 120f) / n);
				highlight.render(AvianColor.white(barAlpha * imgAlphaMultiplier));
			}
			for (int i = 1; i < names.size() + (names.size() <= 1 ? 3f : 4f); i++) {

				float height = ((AvianApp.getHeight() - 120f) / (names.size() + (names.size() <= 1 ? 3f : 4f)));

				line.setX(20 + imgOffset);
				line.setY(120 + height * i);
				line.setW(AvianApp.getWidth() - 40);
				line.render(AvianColor.get(255, gb, gb, imgAlpha));
				if (i == 1 && names.size() > 1) {
					Fonts.Vegur_Small.drawString("Change wallpaper every...", AvianApp.getWidth() / 4 + imgOffset, (120f + (i * height) + (height / 2f)), AvianColor.white(imgAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

					String time = "???", value = "???";
					if (intervalInSeconds < 60) {
						time = "seconds";
						value = String.valueOf(intervalInSeconds);
					} else if (intervalInSeconds >= 60 && intervalInSeconds / 60 <= 59) {
						time = intervalInSeconds / 60 != 1 ? "minutes" : "minute";
						value = String.valueOf(intervalInSeconds / 60);
					} else if (intervalInSeconds / 60 >= 60) {
						time = intervalInSeconds / 3600 != 1 ? "hours" : "hour";
						value = String.valueOf(intervalInSeconds / 3600);
					}

					Fonts.Vegur_Small.drawString(value, AvianApp.getWidth() * (5f / 8f) + imgOffset, (120f + (i * height) + (height / 2f)), AvianColor.white(imgAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

					arc.set(AvianApp.getWidth() * (5f / 8f) + imgOffset, (120f + ((i) * height) + (height / 2f)), ((AvianApp.getHeight() - 120f) / n) / 4, 0, arcEnd);
					arc.render((imgAlpha * (100f / 255f)) / 255f);

					Fonts.Vegur_Small.drawString(time, AvianApp.getWidth() * (7f / 8f) + imgOffset, (120f + (i * height) + (height / 2f)), AvianColor.white(imgAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

				} else if (i == names.size() + (names.size() <= 1 ? 1 : 2)) {
					if (delete)
						Images.X.render(AvianApp.getWidth() / 2f - 8f + imgOffset, 120f + (i * height) + (height / 2f) - 8, new float[] {
								1f, gb / 255f, gb / 255f,
								AvianMath.cos(filesTransition) });
					else
						Images.plus.render(AvianApp.getWidth() / 2f - 8f + imgOffset, 120f + (i * height) + (height / 2f) - 8, new float[] {
								1f, gb / 255f, gb / 255f,
								AvianMath.cos(filesTransition) });
				}
				if (names.size() > 1) {
					if (i - 1 < names.size() && i - 1 >= 0) {
						Fonts.Vegur_Small.drawString(names.get(i - 1), (AvianApp.getWidth() / 2f - 8f) + imgOffset, (120f + ((i + 1) * height) + (height / 2f)), AvianColor.get(255, gb, gb, (int) (AvianMath.cos(filesTransition) * 255f)), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
					}
				} else if (names.size() > 0 && i == 1) {
					Fonts.Vegur_Small.drawString(names.get(0), (AvianApp.getWidth() / 2f - 8f) + imgOffset, (120f + (i * height) + (height / 2f)), AvianColor.get(255, gb, gb, (int) (AvianMath.cos(filesTransition) * 255f)), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
				}

			}

		}

		if (filesTransition > 0) {
			if (my < 150 && my > 120) {
				highlight.set(0, 120, AvianApp.getWidth(), 30);
				highlight.render(AvianColor.white(50));
			}
			vs.drawString("Fetch desktop wallpaper", AvianApp.getWidth() / 2 + (int) (filesOffset), 145, AvianColor.white(filesAlpha), AvianFont.ALIGN_CENTER);

			if (folder.getParentFile() != null)
				Images.uponelevel.render(12 + (int) (filesOffset), 180 - 16, filesAlpha);
			try {
				vs.drawString(folder.getCanonicalPath(), 40 + (int) (filesOffset), 180, AvianColor.white(filesAlpha), AvianFont.ALIGN_LEFT);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (currentDirectory.length == 0) {
				vs.drawString("There are no image files in this folder.", (int) (AvianApp.getWidth() / 2 + filesOffset), 185 + ((AvianApp.getHeight() - 155) / 2), AvianColor.white(filesAlpha), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
			} else
				for (int i = 0; i < currentDirectory.length; i++) {
					int newI = i + slotOffset;

					if (newI >= 0) {
						// alpha -= (4f - newI) * (255f / 5f);
						vs.drawString(currentDirectory[i].getName(), 40 + (int) (filesOffset), 180 + (newI + 1) * 30, AvianColor.white(filesAlpha), AvianFont.ALIGN_LEFT);
						if (currentDirectory[i].isDirectory())
							Images.folder.render(12 + filesOffset, 180 + (newI + 1) * 30 - 16, filesAlpha);
						else
							Images.file.render(12 + filesOffset, 180 + (newI + 1) * 30 - 16, filesAlpha);
					}
				}

			line.setX((int) (filesOffset));
			line.setW(AvianApp.getWidth());
			line.setY(150);
			line.render(AvianColor.white(150f * filesAlphaMultiplier));
			line.setY(185);
			line.render(AvianColor.white(150f * filesAlphaMultiplier));

			int barsOnScreen = (int) ((AvianApp.getHeight() - 150f) / 30f) - 1;
			int leftovers = currentDirectory.length - barsOnScreen;
			float scrollBarHeight = AvianApp.getHeight() - 192f;
			for (int i = 0; i < leftovers; i++) {
				scrollBarHeight -= 5;
				if (scrollBarHeight < 2) {
					scrollBarHeight = 2;
					break;
				}
			}

			if (barsOnScreen < currentDirectory.length) {
				scroll.set(AvianApp.getWidth() - 5, 189 + (int) (filesOffset) + ((AvianApp.getHeight() - 192f - scrollBarHeight) * (-slotOffset) / ((float) currentDirectory.length - (float) barsOnScreen)), 2, scrollBarHeight);
				scroll.render(AvianColor.white(scrollAlpha * filesAlphaMultiplier));
			}

			if (changer != null) {
				changer.render();
				if (!changer.isWorking()) {
					changer = null;
				}
			}
		}
	}

	static AvianRectangle missingBackground = new AvianRectangle();
	static AvianColor color = new AvianColor(AvianMath.randomInt(255), AvianMath.randomInt(255), AvianMath.randomInt(255));

	public static boolean blur = true;

	public static void render() {
		if (effect == null || effect.usesBackground) {
			if (newPaper != null) {

				if (oldPaper != null)
					if ((float) AvianApp.getWidth() / (float) AvianApp.getHeight() < oldPaper.getImageAspectRatio())
						oldPaper.render(AvianApp.getWidth() / 2 - (AvianApp.getHeight() * oldPaper.getImageAspectRatio()) / 2, 0, AvianApp.getHeight() * oldPaper.getImageAspectRatio(), AvianApp.getHeight(), wallAlpha * AvianMath.cos(newPaperSin));
					else
						oldPaper.render(0, AvianApp.getHeight() / 2 - (AvianApp.getWidth() / oldPaper.getImageAspectRatio()) / 2, AvianApp.getWidth(), AvianApp.getWidth() / oldPaper.getImageAspectRatio(), wallAlpha * AvianMath.cos(newPaperSin));

				if ((float) AvianApp.getWidth() / (float) AvianApp.getHeight() < newPaper.getImageAspectRatio())
					newPaper.render(AvianApp.getWidth() / 2 - (AvianApp.getHeight() * newPaper.getImageAspectRatio()) / 2, 0, AvianApp.getHeight() * newPaper.getImageAspectRatio(), AvianApp.getHeight(), wallAlpha * AvianMath.sin(newPaperSin));
				else
					newPaper.render(0, AvianApp.getHeight() / 2 - (AvianApp.getWidth() / newPaper.getImageAspectRatio()) / 2, AvianApp.getWidth(), AvianApp.getWidth() / newPaper.getImageAspectRatio(), wallAlpha * AvianMath.sin(newPaperSin));

			} else {
				missingBackground.set(0, 0, AvianApp.getWidth(), AvianApp.getHeight());
				missingBackground.render(color.setA(wallAlpha * 255f));
			}
		}
		if (effect != null)
			effect.render();
	}

	private static File[] listImagesInFolder(File directory) {
		if (!directory.isDirectory())
			try {
				if (getExtension(directory.getCanonicalPath()).equalsIgnoreCase(".png") || getExtension(directory.getCanonicalPath()).equalsIgnoreCase(".jpg") || getExtension(directory.getCanonicalPath()).equalsIgnoreCase(".jpeg")) {
					changeBackground(directory.getCanonicalPath());
					return currentDirectory;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		scrollAlpha = 150f;
		scrollSeconds = 0;

		Comparator<Object> comp = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				File f1 = (File) o1;
				File f2 = (File) o2;
				if (f1.isDirectory() && !f2.isDirectory())
					return -1;
				else if (!f1.isDirectory() && f2.isDirectory())
					return 1;
				return f1.compareTo(f2);
			}
		};
		File[] files = directory.listFiles();

		LinkedList<File> imageFiles = new LinkedList<File>();

		if (files != null) {
			try {
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					if (f.isDirectory() || getExtension(f.getCanonicalPath()).equalsIgnoreCase(".png") || getExtension(f.getCanonicalPath()).equalsIgnoreCase(".jpg") || getExtension(f.getCanonicalPath()).equalsIgnoreCase(".jpeg")) {
						imageFiles.add(f);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		files = imageFiles.toArray(new File[imageFiles.size()]);
		Arrays.sort(files, comp);

		folder = directory;

		slotOffset = 0;

		return currentDirectory = files;
	}

	private static String getExtension(String filepath) {
		try {
			String fileName = new File(filepath).getName();
			String[] fileNameSplit = fileName.split("\\.");
			return "." + fileNameSplit[fileNameSplit.length - 1];
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static void changeBackground(String filepath) {
		changer = new ThreadUtility("One moment...", filepath) {
			@Override
			public void task() {
				color = AvianColor.black(255);
				String originalFilepath = args[0];

				File f = new File(originalFilepath);
				if (!f.exists())
					return;

				String path = args[0];
				String[] splitString = originalFilepath.split("\\.");
				if (!splitString[splitString.length - 1].equalsIgnoreCase("png"))
					path = convertImage(originalFilepath);

				if (changePaper <= -1) {

					String name = (f.getName());

					names.add(name.substring(0, name.lastIndexOf('.')));
					papers.add(new AvianImage(path));
					blurpapers.add(null);
					blurWallpaper(papers.size()-1);
				} else if (changePaper < papers.size()) {
					//					papers.get(changePaper).changeImage(filepath);
				}
				changePaper = -1;
			}

			public void whenFinished() {
				fileBrowser = false;
				Data.saveData();
			}

		};
		changer.start();

	}

	static String convertImage(String filepath) {
		try {

			int num = 0;
			while (new File("C:/Avian/" + System.getProperty("user.name") + "/dashboard/wallpapers/transcodedWallpaper" + num + ".png").exists()) {
				num++;
			}

			BufferedImage bufferedImage = ImageIO.read(new File(filepath));
			File f = new File(filepath = "C:\\Avian\\" + System.getProperty("user.name") + "\\dashboard\\wallpapers\\transcodedWallpaper" + num + ".png");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			ImageIO.write(bufferedImage, "png", f);

			return filepath;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

}
