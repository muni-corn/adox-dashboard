
package musicaflight.dashboard;

import static org.lwjgl.opengl.GL11.*;

import musicaflight.avianutils.*;

public abstract class Element {

	float height;
	private float y, finalY;
	private float yVel;
	static float mx, my;

	boolean hoverGear;
	boolean hoverGrab;
	float gearX;
	float grabX;

	boolean contentClick;
	boolean settingsClick;

	boolean settings;

	boolean hasFocus;
	boolean onlyOne;

	boolean delete;

	public Element(float h, boolean oneAtATime) {
		height = h;
		this.onlyOne = oneAtATime;
	}

	private static transient AvianRectangle bg = new AvianRectangle(20, 0, AvianApp.getWidth() - 40, 0);

	public float getWidth() {
		return bg.getW();
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float h) {
		height = h;
	}

	public final void keyboard() {
		if (Dashboard.deleteElement || delete) {
			grabClick = false;
			grabbed = false;
			return;
		}
		if (Dashboard.panel) {
			if (settings)
				settingsKeyboard();
		} else if (Dashboard.traySinIn <= -90 && hasFocus)
			contentKeyboard();
	}

	static boolean grabClick;
	boolean grabbed;
	float grabAlpha = 0f;
	float startY;

	public final void mouse() {
		mx = AvianInput.getMouseX();
		my = (AvianInput.getMouseY());

		hasFocus = (my > y) && (my < (y + height)) && (mx > 20) && (mx < AvianApp.getWidth() - 20);

		if (Dashboard.deleteElement || delete)
			return;

		if (AvianInput.isMouseButtonDown(0) && !grabClick && hoverGrab) {
			grabbed = true;
			grabClick = true;
			startY = my;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			grabClick = false;
			grabbed = false;
		}
		if (!grabClick) {

			hoverGear = (mx < 20) && (my > y) && (my < (y + height));
			hoverGrab = (mx > AvianApp.getWidth() - 20) && (my > y) && (my < (y + height));

			if (hoverGear && AvianInput.isMouseButtonDown(0) && !Dashboard.panel)
				settings();

			if (Dashboard.panel) {
				if (settings && Dashboard.traySinIn >= 90) {
					settingsMouse();
				}
			} else if (my > 120 && !Dashboard.panel) {
				mx = AvianInput.getMouseX() - 20;
				my = (int) (AvianInput.getMouseY()) - (int) y;
				contentMouse();
			}
		}

	}

	public boolean hasFocus(int mouseX, int mouseY) {
		return (mouseY > y) && (mouseY < (y + height)) && (mouseX > 20) && (mouseX < AvianApp.getWidth() - 20);
	}

	public abstract void contentKeyboard();

	public abstract void contentMouse();

	public abstract void contentLogic();

	public abstract void contentRender();

	public abstract void settingsKeyboard();

	public abstract void settingsMouse();

	public abstract void settingsLogic();

	public abstract void settingsRender();

	public abstract void settingsOpened();

	public abstract void closeSettings();

	public abstract void destroy();

	protected final void settings() {
		settingsOpened();
		Dashboard.openPanel();
		settings = true;
		settingsClick = true;
	}

	public float getPosition(int slot) {
		float position = 140 + Dashboard.finalOffset;
		for (int i = 0; i < slot; i++) {
			position += 20 + Dashboard.elements.get(i).height;
		}
		return position;
	}

	public final void logic(int slot) {

		if (Dashboard.settings)
			settings = false;

		if (delete) {
			yVel += 2;
			y += yVel;
			shadowAlpha = AvianMath.glide(shadowAlpha, 0f, 10f);
			grabAlpha = AvianMath.glide(grabAlpha, -50f, 10f);
		} else {

			mx = AvianInput.getMouseX();
			my = (AvianInput.getMouseY());

			if (Dashboard.traySinIn <= -90) {
				settings = false;
			}

			if (grabClick) {
				if (grabbed) {
					if (my < 135) {
						Dashboard.finalOffset += 3;
					} else if (my > AvianApp.getHeight() - 15) {
						Dashboard.finalOffset -= 3;
					}
				} else
					shadowAlpha = AvianMath.glide(shadowAlpha, 0f, 10f);
				if (grabbed)
					grabAlpha = AvianMath.glide(grabAlpha, 50f, 10f);
				else
					grabAlpha = AvianMath.glide(grabAlpha, -50f, 10f);
			} else {
				shadowAlpha = AvianMath.glide(shadowAlpha, 255f, 10f);
				grabAlpha = AvianMath.glide(grabAlpha, 0f, 10f);

			}

			finalY = getPosition(slot);
			if (!grabbed)
				y = AvianMath.glide(y, finalY, 5f);
			else {
				y += my - startY;
				startY = my;
			}
			mx = AvianInput.getMouseX() - 20;
			my = (int) (AvianInput.getMouseY()) - (int) y;
			if (hoverGear && !Dashboard.deleteElement)
				gearX = AvianMath.glide(gearX, 2f, 5f);
			else
				gearX = AvianMath.glide(gearX, -16f, 15f);
			if (hoverGrab && !Dashboard.deleteElement)
				grabX = AvianMath.glide(grabX, 2f, 5f);
			else
				grabX = AvianMath.glide(grabX, 20, 15f);

			if (grabClick)
				if (grabbed)
					grabAlpha = AvianMath.glide(grabAlpha, 75f, 10f);
				else
					grabAlpha = AvianMath.glide(grabAlpha, 0, 10f);

			contentLogic();

			mx = AvianInput.getMouseX();
			my = (AvianInput.getMouseY());

			settingsLogic();
		}
	}

	static AvianRectangle mask = new AvianRectangle();

	/** Not to be confused with the abstract method settingsRender(). This method
	 * simply calls settingsRender() only if the current panel screen matches
	 * the one associated with this Element. */

	public final void renderSettings() {
		mx = AvianInput.getMouseX();
		my = (AvianInput.getMouseY());

		if (settings)
			settingsRender();
	}

	static AvianColor bgColor = new AvianColor(0, 0, 0, 50);

	public final void render() {

		glColorMask(false, false, false, false);
		glDepthMask(false);
		glStencilFunc(GL_NEVER, 1, 0xFF);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);

		// draw stencil pattern
		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);

		float maskHeight = height;
		if ((y + height) > Dashboard.header.getH())
			if (y < Dashboard.header.getH()) {
				maskHeight = height + (y - Dashboard.header.getH());
				if (maskHeight < 0)
					maskHeight = 0;
				mask.set(20, Dashboard.header.getH(), AvianApp.getWidth() - 40f, maskHeight);
				mask.render(AvianColor.white(0));
			} else {
				mask.set(20, y, AvianApp.getWidth() - 40, height);
				mask.render(AvianColor.white(0));
			}

		mx = AvianInput.getMouseX() - 20;
		my = (int) (AvianInput.getMouseY()) - (int) y;

		glColorMask(true, true, true, true);
		glDepthMask(true);
		glStencilMask(0x00);

		glStencilFunc(GL_EQUAL, 1, 0xFF);

		bg.setH(height);
		bg.setY(y);
		bg.setW(AvianApp.getWidth() - 40);

		bg.render(bgColor.setRGBA(Dashboard.deleteColor * 100f, Dashboard.deleteColor * 10f, Dashboard.deleteColor * 10f, grabAlpha + 75f + (Dashboard.darkerElements ? 75 : 0)));

		glPushMatrix();

		glLoadIdentity();
		glTranslatef(20, y, 0);

		contentRender();

		glPopMatrix();

		glColorMask(false, false, false, false);
		glDepthMask(false);
		glStencilFunc(GL_NEVER, 1, 0xFF);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);

		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);

		if ((y + height + 20) > Dashboard.header.getH())
			if (y < Dashboard.header.getH()) {
				maskHeight = height + (y - Dashboard.header.getH());
				if (maskHeight < 0)
					maskHeight = 0;
				mask.set(0, Dashboard.header.getH(), 20, maskHeight);
				mask.render(AvianColor.white(0));
				mask.set(AvianApp.getWidth() - 20, Dashboard.header.getH(), 20, maskHeight);
				mask.render(AvianColor.white(0));

				float shadowHeight = (y + height + 20) - Dashboard.header.getH();
				if (shadowHeight > 20)
					shadowHeight = 20;
				mask.set(0, y + height + (20 - shadowHeight), AvianApp.getWidth(), shadowHeight);
				mask.render(AvianColor.white(0));
			} else {
				mask.set(0, y, 20, maskHeight);
				mask.render(AvianColor.white(0));
				mask.set(AvianApp.getWidth() - 20, y, 20, maskHeight);
				mask.render(AvianColor.white(0));
				mask.set(0, y + height, AvianApp.getWidth(), 20);
				mask.render(AvianColor.white(0));
			}

		glColorMask(true, true, true, true);
		glDepthMask(true);
		glStencilMask(0x00);

		glStencilFunc(GL_EQUAL, 1, 0xFF);

		Images.gear.render(gearX, (y + (height / 2)) - 8, (int) (((gearX + 8f) / 10f) * 255f));
		Images.scrub.render(grabX + AvianApp.getWidth() - 20, (y + (height / 2)) - 8, 255 - (int) (((grabX - 2f) / 18f) * 255f));
		shadow.set(20, y, AvianApp.getWidth() - 40f, height);
		shadow.render(shadowAlpha);

	}

	static AvianShadow shadow = new AvianShadow();
	float shadowAlpha = 255;

	public float getY() {
		return finalY;
	}

	public float getCurrentY() {
		return y;
	}

	public abstract String getName();

	public abstract Element create();

}
