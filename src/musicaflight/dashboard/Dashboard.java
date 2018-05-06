
package musicaflight.dashboard;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import musicaflight.avianutils.*;
import musicaflight.dashboard.wallpaper.Wallpaper;

public class Dashboard {

	static float mx;
	static float my;
	AvianCircle ripple = new AvianCircle();
	boolean mouseClicked = false;

	static boolean safeShutdown;
	static boolean startupWarning;

	static float yOffset;
	static float finalOffset;

	public static boolean intro = true;
	float introSeconds = 0;

	static boolean panel;
	public static float traySinIn = -90;

	public static boolean settings;

	static boolean clock = true;
	static float headerTextAlpha = 1f;
	static boolean headerOptions = false;
	float optionsSinIn;

	static float greeting = 0;
	static float greetVel = 0;

	static Calendar c;

	float saveSeconds;

	static boolean darkerElements = false;

	int lastWidth, lastHeight, lastX, lastY;
	boolean maximized = false;

	static AvianRectangle highlight = new AvianRectangle();
	int selection;
	float selectionX;

	public static ArrayList<Element> elements = new ArrayList<Element>();
	static boolean addElement;
	static boolean deleteElement;
	static float deleteColor;

	static ItemList<Element> availableElements = new ItemList<Element>(false, false) {

		@Override
		public void selectionRequested(int item) {
			Element e = availableElements.get(item).create();
			if (e.onlyOne) {
				elements.add(availableElements.remove(item));
			} else {
				elements.add(e);
			}
			Data.saveData();
			closePanel();
		}

		@Override
		public void additionRequested(String filepath) {
		}

		@Override
		public void deletionRequested(int item) {
		}

		@Override
		public void reset() {
		}

		@Override
		public String itemName(int item) {
			return this.get(item).getName();
		}
	};

	public Dashboard() {
		availableElements.add(new MusicPlayer());
		availableElements.add(new Stopwatch());
		availableElements.add(new Countdown());
		availableElements.add(new GameScreen());
		
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
				if (!panel&&!clock)finalOffset+=count*20f;
				
			}
			
		});
	}

	public static int dw;
	Element deletedElement;

	public void mouse() {
		if (UpdateChecker.downloading())
			return;

		if (!intro) {
			dw = (int) (AvianInput.getScroll() * 20f);

			mx = AvianInput.getMouseX();
			my = (AvianInput.getMouseY());

			if (AvianInput.isMouseButtonDown(0) && !mouseClicked)
				ripple.set(mx, my, 0);

			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				e.mouse();
			}

			if (AvianInput.isMouseButtonDown(1) || (AvianInput.isMouseButtonDown(0) && (my < 120) && !mouseClicked)) {
				closePanel();
				if (AvianInput.isMouseButtonDown(1))
					clock = false;
			}

			if (!panel) {

				if (my > 170) {
					selection = 0;
					headerOptions = false;
				} else if (headerOptions) {
					selection = (int) ((mx / AvianApp.getWidth()) * 4f);
				}

				if (AvianInput.isMouseButtonDown(AvianInput.MOUSE_LEFT)) {
					if (!mouseClicked) {
						if (deleteElement) {
							for (int i = 0; i < elements.size(); i++) {
								if (elements.get(i).hasFocus) {
									if (elements.get(i).onlyOne) {
										availableElements.add(elements.get(i).create());
									}
									elements.get(i).delete = true;
									elements.get(i).destroy();
									deletedElement = elements.remove(i);
									Data.saveData();
									break;
								}
							}
						} else {
							if (headerOptions && my < 120) {
								switch (selection) {
									case 0:
										clock = true;
										break;
									case 1:
										openPanel();
										settings = true;
										Wallpaper.settingsClick = true;
										Settings.mouseClicked = true;

										break;
									case 2:
										if (!maximized) {
											AvianApp.setFullscreen(true);
										} else {
											AvianApp.setFullscreen(false);
										}
										break;
									case 3:
										AvianApp.close();
										break;
								}
								headerOptions = false;
								selection = 0;
							} else if ((addElementDrawer ? (my < 170 && my > 50) : (my < 120)) && !clock) {
								headerOptions = true;
							} else if (addElementDrawer && my < 50 && !clock) {
								addElement = true;
								openPanel();
							}
						}
					}
					mouseClicked = true;
				} else if (!AvianInput.isMouseButtonDown(0)) {
					mouseClicked = false;
				}

			} else {
				if (settings) {
					Settings.mouse();
				}
				if (addElement)
					availableElements.mouse(mx, my, mouseClicked);
				if (AvianInput.isMouseButtonDown(0)) {
					mouseClicked = true;
				} else if (!AvianInput.isMouseButtonDown(0)) {
					mouseClicked = false;
				}
			}
		}

	}

	public void keyboard() {
		if (UpdateChecker.downloading())
			return;
		if (AvianInput.isKeyDown(AvianInput.KEY_ENTER))
			Dashboard.closePanel();
		deleteElement = AvianInput.isKeyDown(AvianInput.KEY_DELETE) && !panel && !clock && !headerOptions && elements.size() > 0;
		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			e.keyboard();
		}
	}

	boolean addElementDrawer;

	public void logic() {

		UpdateChecker.logic();
		Wallpaper.wallpaperLogic();

		c = new GregorianCalendar();

		saveSeconds += .01f;

		if (saveSeconds >= 30f) {
			Data.saveData();
			saveSeconds -= 30f;
		}

		if (!intro) {
			Clock.logic();
			Settings.logic();

			if (panel) {
				availableElements.logic();
			} else
				Settings.nextScreen = false;

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			double width = screenSize.getWidth();
			double height = screenSize.getHeight();

			maximized = AvianApp.getWidth() >= width && AvianApp.getHeight() >= height;

			selectionX = AvianMath.glide(selectionX, selection * (AvianApp.getWidth() / 4f), 10f);

			addElementDrawer = finalOffset > 25 && !panel && !headerOptions && dw >= 0;

			if (Settings.deleting)
				headerTextAlpha = Wallpaper.wallAlpha;

			if (deleteElement) {
				deleteColor = AvianMath.glide(deleteColor, 1f, 15f);
			} else {
				deleteColor = AvianMath.glide(deleteColor, 0f, 15f);
			}

			int elementsHeight = 20 - AvianApp.getHeight() + 120;

			for (int i = 0; i < elements.size(); i++) {
				elementsHeight += 20 + elements.get(i).getHeight();
			}

			if (!clock) {
				if (finalOffset > 0) {
					if (Element.grabClick)
						finalOffset = AvianMath.glide(finalOffset, 0, 5f);
					else
						finalOffset = AvianMath.glide(finalOffset, addElementDrawer ? 50 : 0, 30f);
				}

				if (elementsHeight < 0 && finalOffset < 0) {
					finalOffset = AvianMath.glide(finalOffset, 0, 5f);
				} else if (elementsHeight > 0 && finalOffset < -elementsHeight) {
					finalOffset = AvianMath.glide(finalOffset, -elementsHeight, 5f);
				}
			} else {
				finalOffset = AvianMath.glide(finalOffset, AvianApp.getHeight() - 120, 30f);
			}

			yOffset = AvianMath.glide(yOffset, finalOffset, 5f);

			if (panel) {
				if (traySinIn < 90)
					traySinIn += 5;
			} else {
				if (traySinIn > -90)
					traySinIn -= 5;
			}

			if (traySinIn <= -90) {
				settings = false;
				addElement = false;
			}
			if (headerOptions) {
				if (optionsSinIn < 90)
					optionsSinIn += 4;
			} else {
				if (optionsSinIn > 0)
					optionsSinIn -= 4;
			}
			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				e.logic(i);
				if (e.grabbed) {
					if (i < elements.size() - 1) {
						Element lower = elements.get(i + 1);
						if (e.getCurrentY() + e.getHeight() / 2f > lower.getY() + lower.getHeight() / 2f)
							Collections.swap(elements, i, i + 1);
					}
					if (i > 0) {
						Element upper = elements.get(i - 1);
						if (e.getCurrentY() + e.getHeight() / 2f < upper.getY() + upper.getHeight() / 2f)
							Collections.swap(elements, i, i - 1);
					}
				}
			}
			if (deletedElement != null && deletedElement.getY() < AvianApp.getHeight()) {
				deletedElement.logic(0);
			}

			ripple.setDiameter(AvianMath.glide(ripple.getDiameter(), (255f / 4f), 20f));

		} else {
			ripple.setDiameter(255f / 4f);
			finalOffset = yOffset = AvianApp.getHeight() * 2;
			panel = false;
			headerOptions = false;
			optionsSinIn = 0;
			traySinIn = -90;
			introSeconds += .01;
			if (introSeconds >= 3f)
				greeting = AvianMath.glide(greeting, 1f, 30f);
			if (greeting > .9f && introSeconds > 5f) {
				greetVel += (greetVel / 20f) + .0003f;
				greeting += greetVel;
			}
			if (greeting >= 2) {
				intro = false;
			}
		}
	}

	SimpleDateFormat hour = new SimpleDateFormat("h");
	SimpleDateFormat minute = new SimpleDateFormat("mm");
	SimpleDateFormat second = new SimpleDateFormat("ss");
	SimpleDateFormat ms = new SimpleDateFormat("SSS");
	SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE");
	SimpleDateFormat date = new SimpleDateFormat("MMMM d, y");

	static AvianRectangle header = new AvianRectangle(0, 0, AvianApp.getWidth(), 120);

	static DecimalFormat f = new DecimalFormat("0.00");

	static AvianRectangle r = new AvianRectangle(0, 0, 200, 200);

	static AvianCircle circle = new AvianCircle(mx, my, 0);

	static AvianLine l = new AvianLine();

	public void render() {

		AvianFont v = Fonts.Vegur;
		AvianFont vs = Fonts.Vegur_Small;

		header.setW(AvianApp.getWidth());
		if (traySinIn > -90)
			header.setH((120f + (yOffset > 0 ? yOffset : 0)) + ((AvianMath.sin(traySinIn) + 1f) / 2f) * (AvianApp.getHeight() - (120f)));
		else
			header.setH(yOffset > 0 ? 120 + yOffset : 120f);

		// if (AvianInput.isButtonDown(1)) {
		// header.setH(my);
		// }

		Wallpaper.render();

		header.setY(0);
		header.render(AvianColor.get(deleteColor * 100f, deleteColor * 10f, deleteColor * 10f, (100 + (darkerElements ? 75 : 0))));

		for (int i = 0; i < Math.ceil(AvianApp.getWidth() / 256f); i++)
			Images.shadow.render(i * Images.shadow.getWidth(), header.getH());

		float dateText = AvianMath.cos(optionsSinIn);
		float optionsText = AvianMath.sin(optionsSinIn);
		float textY = ((dateText * 100f) + (yOffset > 0 ? yOffset : 0)) - 10;
		float optionsY = ((optionsText * 100f) + (yOffset > 0 ? yOffset : 0)) - 10;
		float dateAlpha = (dateText * 255f) * headerTextAlpha;
		float optionsAlpha = headerTextAlpha * (255f * headerTextAlpha - dateAlpha);

		header.setY(-120);
		if (!clock && !panel) {
			Images.plus.render(AvianApp.getWidth() / 2 - 8, textY - 122, finalOffset > 50 ? 255f - (((yOffset - 50f) / 50f) * 255f) : 255);
			r.set(0, (textY - 140 > 0 ? 0 : textY - 140), AvianApp.getWidth(), (textY - 140 > 0 ? textY - 90 : 50));
			r.render(AvianColor.white((finalOffset > 50 ? 50 - (((yOffset - 50f) / 50f) * 25) : 25) * ((1 - ((my - 50) / 120f)) > 1 ? 1 : ((1 - ((my - 50) / 120f)) < 0 ? 0 : (1 - ((my - 50) / 120f))))));
		}
		r.set(0, 0, AvianApp.getWidth(), 120);
		r.render(AvianColor.black(50f * ((AvianMath.sin(traySinIn) + 1f) / 2f) + (darkerElements ? 75 * ((AvianMath.sin(traySinIn) + 1f) / 2f) : 0)));
		AvianFont.setHorizontalAlignment(AvianFont.ALIGN_LEFT);
		v.drawString(20, textY, hour.format(getCurrentCalendar().getTime()), dateAlpha/255f);
		float colonAlpha = (float) Math.pow(Math.abs(AvianMath.cos((Float.parseFloat(ms.format(getCurrentCalendar().getTime())) / 1000f) * 180f)), 50) * 205f + 50f;

		v.drawString(20 + v.getWidth(hour.format(getCurrentCalendar().getTime())), textY, ":", (colonAlpha - 255 + dateAlpha) / 255f);
		v.drawString(20 + v.getWidth(hour.format(getCurrentCalendar().getTime())) + v.getWidth(":"), textY, minute.format(getCurrentCalendar().getTime()), (dateAlpha) / 255f);

		AvianFont.setHorizontalAlignment(AvianFont.ALIGN_RIGHT);
		vs.drawString(AvianApp.getWidth() - 20, textY - 30, dayOfWeek.format(getCurrentCalendar().getTime()), dateAlpha/255f);
		vs.drawString(AvianApp.getWidth() - 20, textY, getToday(), (dateAlpha) / 255f);

		// Options
		if (!clock) {
			highlight.set(selectionX, 0, AvianApp.getWidth() / 4f, yOffset > 0 ? 120 + yOffset : 120f);
			highlight.render(optionsAlpha / (5*255f));

			AvianFont.setAlignment(AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
			vs.drawString(AvianApp.getWidth() / 8, optionsY - 10, "Clock", (optionsAlpha) / 255f);
			vs.drawString((AvianApp.getWidth() * (3f / 8f)), optionsY - 10, "Settings", (optionsAlpha) / 255f);
			Images.gear.render(AvianApp.getWidth() * (3f / 8f) - 8, optionsY - 40, optionsAlpha / 255f);

			if (!maximized) {
				vs.drawString((AvianApp.getWidth() * (5f / 8f)), optionsY - 10, "Fullscreen", (optionsAlpha) / 255f);
				Images.maximize.render(AvianApp.getWidth() * (5f / 8f) - 8, optionsY - 40, optionsAlpha / 255f);
			} else {
				vs.drawString((AvianApp.getWidth() * (5f / 8f)), optionsY - 10, "Restore", (optionsAlpha) / 255f);
				Images.restore.render(AvianApp.getWidth() * (5f / 8f) - 8, optionsY - 40, optionsAlpha / 255f);
			}
			vs.drawString((AvianApp.getWidth() * (7f / 8f)), optionsY - 10, "Exit", (optionsAlpha) / 255f);

			Images.clock.render(AvianApp.getWidth() / 8 - 8, optionsY - 40, optionsAlpha / 255f);
			Images.X.render(AvianApp.getWidth() * (7f / 8f) - 8, optionsY - 40, optionsAlpha / 255f);
		}
		if (elements.size() <= 0) {
			AvianFont.setVerticalAlignment(AvianFont.ALIGN_BOTTOM);
			if (traySinIn < 90 && !panel)
				vs.drawString((header.getH() + 50f), "Scroll up to add items to your dashboard.", (255f * ((50 - finalOffset) / 50f)) / 255f);
		}

		Clock.render();

		glEnable(GL_STENCIL_TEST);

		if (deletedElement != null && deletedElement.getY() < AvianApp.getHeight())
			deletedElement.render();

		if (elements.size() > 0 && traySinIn < 90) {

			Element grabbedElement = null;

			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				if (!e.grabbed)
					e.render();
				else
					grabbedElement = e;
			}

			if (grabbedElement != null)
				grabbedElement.render();

		}

		if (traySinIn > -90) {

			glColorMask(false, false, false, false);
			glDepthMask(false);
			glStencilFunc(GL_NEVER, 1, 0xFF);
			glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP); // render 1s on test fail (always)

			// render stencil pattern
			glStencilMask(0xFF);
			glClear(GL_STENCIL_BUFFER_BIT);

			r.set(0, 120, AvianApp.getWidth(), header.getH() - 120);
			r.render(AvianColor.black(0));

			glColorMask(true, true, true, true);
			glDepthMask(true);
			glStencilMask(0x00);

			glStencilFunc(GL_EQUAL, 1, 0xFF);

			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				e.renderSettings();
			}
			if (addElement) {
				availableElements.render();
			} else if (settings) {
				Settings.render();
			}

			glStencilFunc(GL_EQUAL, 0, 0xFF);
		}

		glDisable(GL_STENCIL_TEST);

		if (intro) {
			AvianFont.setVerticalAlignment(AvianFont.ALIGN_CENTER);
			Fonts.Vegur_Medium.drawString((AvianApp.getHeight() / 2) + ((1f - greeting) * (AvianApp.getHeight() / 4f)), getGreeting(), AvianMath.sin(greeting * 90f));
			l.set((AvianApp.getWidth() / 2) - (greeting * (AvianApp.getWidth() / 4)), (AvianApp.getHeight() / 2) + ((1f - greeting) * (AvianApp.getHeight() / 4)) + 30, (AvianApp.getWidth() / 2) + (greeting * (AvianApp.getWidth() / 4)), (AvianApp.getHeight() / 2) + ((1f - greeting) * (AvianApp.getHeight() / 4) + 30));
			l.render(AvianMath.sin(greeting * 90f));
		}

		UpdateChecker.render();

		ripple.render(false, 1f, 1f, 1f, (255 - ripple.getDiameter() * 4) / 255f);

	}

	public static void openPanel() {
		panel = true;
	}

	public static void closePanel() {
		panel = false;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).settings) {
				elements.get(i).closeSettings();
				break;
			}
		}
		if (settings)
			Settings.cleanUpSettings();
	}

	static SimpleDateFormat month = new SimpleDateFormat("M");
	static SimpleDateFormat fullMonth = new SimpleDateFormat("MMMM");
	static SimpleDateFormat day = new SimpleDateFormat("d");
	static SimpleDateFormat year = new SimpleDateFormat("y");
	static SimpleDateFormat week = new SimpleDateFormat("W");
	static SimpleDateFormat dayOfWeekNum = new SimpleDateFormat("u");

	public static boolean deleteData;

	private String getToday() {
		String y = Dashboard.year.format(getCurrentCalendar().getTime());

		int monthAsInt = c.get(Calendar.MONTH) + 1;
		int dayAsInt = c.get(Calendar.DAY_OF_MONTH);

		String d = fullMonth.format(getCurrentCalendar().getTime()) + " " + day.format(getCurrentCalendar().getTime());

		switch (monthAsInt) {
			case 1:
				switch (dayAsInt) {
					case 1:
						d = "Happy New Year";
				}
				break;
			case 2:
				switch (dayAsInt) {
					case 14:
						d = "Valentine's Day";
				}
				break;
			case 3:
				switch (dayAsInt) {
					case 17:
						d = "St. Patrick's Day";
				}
				break;
			case 5:
				if (c.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 2 && c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					d = "Mother's Day";
				break;
			case 6:
				if (c.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 3 && c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					d = "Father's Day";
				break;
			case 7:
				switch (dayAsInt) {
					case 4:
						d = "Independence Day";
				}
				break;
			case 10:
				switch (dayAsInt) {
					case 31:
						d = "Happy Halloween";
						break;
				}
				break;
			case 11:
				if (c.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 4 && c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
					d = "Happy Thanksgiving";
				break;
			case 12:
				switch (dayAsInt) {
					case 24:
						d = "Christmas Eve";
						break;
					case 25:
						d = "Merry Christmas";
						break;
					case 31:
						d = "New Year's Eve";
						break;
				}
				break;
		}

		return d + ", " + y;

	}

	@SuppressWarnings("unused")
	private String getGreeting() {
		String d = fullMonth.format(getCurrentCalendar().getTime()) + " " + day.format(getCurrentCalendar().getTime());
		String y = Dashboard.year.format(getCurrentCalendar().getTime());

		//		if (getToday().equals(date + ", " + year)) {
		int h = c.get(Calendar.HOUR_OF_DAY);

		if (h > 0 && h < 12)
			return "Good Morning";
		else if (h >= 12 && h < 12 + 5)
			return "Good Afternoon";
		else if (h >= 12 + 5 && (h <= 24 || h == 0))
			return "Good Evening";

		//		} else {
		//			return getToday().substring(0, (date + ", ").length() - 1);
		//		}
		return "Welcome";
	}

	public static void destroy() {
		if (deleteData) {
			deleteData(new File("C:/Adox/" + System.getProperty("user.name") + "/dashboard/"));
			return;
		}
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).destroy();
		safeShutdown = true;
		Data.saveData();
	}

	private static void deleteData(File file) {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			File[] ff = file.listFiles();
			for (int i = 0; i < ff.length; i++)
				deleteData(ff[i]);
		}
		try {
			if (file.delete())
				System.out.println("Successfully deleted: " + file.getCanonicalPath());
			else {
				System.err.println("Failed to delete: " + file.getCanonicalPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Calendar getCurrentCalendar() {
		return c == null ? c = Calendar.getInstance() : c;
	}
}
