
package musicaflight.dashboard;

import static org.lwjgl.opengl.GL11.*;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

import musicaflight.avianutils.*;
import musicaflight.dashboard.games.*;

public class GameScreen extends Element {

	static DecimalFormat df = new DecimalFormat("#,##0");
	static DecimalFormat df2 = new DecimalFormat("00");

	public Game game = null;
	public static boolean paused = true;

	public static long payout;
	private long counter;

	int slotHighlight;
	float sHY;
	float sHH;
	float gb = 255;

	boolean dropMoney;
	int droppedMoney;
	float dropSinIn;

	public static long money = 25;

	AvianColor color = new AvianColor(0, 0, 0, 0);

	ItemList<Game> games = new ItemList<Game>(false, true) {

		@Override
		public void selectionRequested(int item) {
			game = games.get(item);
			Dashboard.closePanel();
		}

		@Override
		public void reset() {
			game = null;
			Dashboard.closePanel();
		}

		@Override
		public String itemName(int item) {
			return games.get(item).getName();
		}

		@Override
		public void deletionRequested(int item) {

		}

		@Override
		public void additionRequested(String filepath) {

		}
	};

	public long getTableMoney() {
		long value = 0;
		for (int i = 0; i < games.size(); i++) {
			Game g = games.get(i);
			if (g instanceof CasinoGame)
				value += (((CasinoGame) g).getMoneyValue());
		}
		return value;
	}

	public GameScreen() {
		super(300, false);
		games.add(new MiniBlockery());
		games.add(new SlotMachine());
		games.add(new Blackjack());
	}

	@Override
	public void contentKeyboard() {
		if (hasFocus && game != null)
			game.keyboard();
	}

	@Override
	public void contentMouse() {
		if (!hasFocus)
			paused = true;
		else if (game != null) {
			if (AvianInput.isMouseButtonDown(0)) {
				if (dropMoney) {
					float totalDistance = (float) Math.sqrt((getWidth() - 35f) * (getWidth() - 35f) + (getHeight() - 35f) * (getHeight() - 35f));
					float dragDistance = (float) Math.sqrt((mx - 35f) * (mx - 35f) + (my - 35f) * (my - 35f));
					droppedMoney = (int) ((dragDistance / totalDistance) * money);
					if (droppedMoney < 0)
						droppedMoney = 0;
				} else {
					droppedMoney = 0;
				}
				if (!contentClick) {
					if (mx < 35 && my < 35) {
						dropMoney = true;
					}
				}
				contentClick = true;
			} else if (!AvianInput.isMouseButtonDown(0)) {
				contentClick = false;
				if (dropMoney) {
					if (game != null && game instanceof CasinoGame)
						((CasinoGame) game).moneyDropped(droppedMoney);
					dropMoney = false;
				}
			}
			if (!dropMoney)
				game.mouse(mx, my);
		}

		if (AvianInput.isMouseButtonDown(0) && hasFocus)
			paused = false;
	}

	public static float width;
	public static float height;
	public static boolean focus;

	private int freeCoins;

	int pixelParticles = 50;

	float[] x = new float[pixelParticles];
	float[] y = new float[pixelParticles];
	float[] xv = new float[pixelParticles];
	float[] yv = new float[pixelParticles];
	float[] sizes = new float[pixelParticles];
	float[] sinInputs = new float[pixelParticles];
	float[] fadeSpeeds = new float[pixelParticles];

	AvianRectangle pixelParticle = new AvianRectangle(0, 0, 0, 0);

	@Override
	public void contentLogic() {

		width = getWidth();
		height = getHeight();

		if (dropMoney) {
			if (dropSinIn < 90) {
				dropSinIn += 9f;
			}
		} else {
			if (dropSinIn > 0) {
				dropSinIn -= 9f;
			}
		}

		focus = hasFocus;

		if (game != null)
			game.logic(getWidth(), getHeight());
		else {
			for (int i = 0; i < pixelParticles; i++) {
				if (sinInputs[i] <= 0f || sinInputs[i] >= 180f || x[i] > getWidth() || x[i] < -sizes[i] || y[i] > getHeight() || y[i] < -sizes[i]) {
					sizes[i] = AvianMath.randomFloat() * 90f + 10f;
					x[i] = AvianMath.randomFloat() * getWidth() - sizes[i] / 2f;
					y[i] = AvianMath.randomFloat() * getHeight() - sizes[i] / 2f;
					xv[i] = AvianMath.randomFloat() / 8f - .0625f;
					yv[i] = AvianMath.randomFloat() / 8f - .0625f;
					sinInputs[i] = 0;
					fadeSpeeds[i] = AvianMath.randomFloat() * (9f / 50f);
					sinInputs[i] += fadeSpeeds[i];
				} else {
					sinInputs[i] += fadeSpeeds[i];
					x[i] += xv[i];
					y[i] += yv[i];
				}
			}
		}

		counter++;
		if (payout > 0) {
			for (int i = 0; i < 5 && payout > 0; i++) {
				if (coins.size() < 300)
					coins.add(new Coin());
				if (payout > 300) {
					long increment = payout / 200;
					payout -= increment;
					money += increment;
				} else {
					payout--;
					money++;
				}
			}
		}

		Iterator<Coin> iter = coins.iterator();
		while (iter.hasNext()) {
			Coin c = iter.next();
			c.logic();
			if (c.doneMoving()) {
				fireworks.add(new Firework(c.x, c.y));
				iter.remove();
			}
		}
		Iterator<Firework> iter2 = fireworks.iterator();
		while (iter2.hasNext()) {
			Firework f = iter2.next();
			f.logic();
			if (f.alpha <= 0)
				iter2.remove();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void contentRender() {
		if (game != null) {
			game.render();

		}

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		float mouseX = mx;
		float mouseY = my + 65 - (AvianMath.sin(dropSinIn) * 25f);

		if (game != null) {
			if (game instanceof CasinoGame) {

				if (mouseX + 20 + Fonts.DroidSansMono.getWidth(df.format(droppedMoney)) + 5 > mouseX + 5 + Fonts.Vegur_ExtraSmall.getWidth("Drop...")) {
					if (mouseX + 20 + Fonts.DroidSansMono.getWidth(df.format(droppedMoney)) + 5 > getWidth()) {
						mouseX = getWidth() - 25 - Fonts.DroidSansMono.getWidth(df.format(droppedMoney));
					}
				} else if (mx + 5 + Fonts.Vegur_ExtraSmall.getWidth("Drop...") > getWidth()) {
					mouseX = getWidth() - Fonts.Vegur_ExtraSmall.getWidth("Drop...") - 5;
				}
				if (mouseY + 21 > getHeight()) {
					mouseY = getHeight() - 21;
				}

				if (dropSinIn > 0 && game != null) {
					Fonts.DroidSansMono.drawString(df.format(droppedMoney), (int) (mouseX + 20), (int) (mouseY + 6), AvianColor.white(AvianMath.sin(dropSinIn) * 255f), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
					Fonts.Vegur_ExtraSmall.drawString("Drop...", (int) mouseX, (int) (mouseY - 10), AvianColor.white(), AvianFont.ALIGN_LEFT);
				}

				AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
				Fonts.DroidSansMono.drawString(35, 21, df.format(money), 1f);
				Images.coin.render(15, 15, 1f);
				for (int i = 0; i < coins.size(); i++) {
					coins.get(i).render();
				}
				for (int i = 0; i < fireworks.size(); i++) {
					fireworks.get(i).render();
				}

				if (dropSinIn > 0)
					Images.coin.render(mouseX, mouseY, AvianMath.sin(dropSinIn) * 255f);
			}
		} else {
			for (int i = 0; i < pixelParticles; i++) {
				if (pixelParticle != null) {
					pixelParticle.set(this.x[i], this.y[i], sizes[i], sizes[i]);
					if (pixelParticle.getX() > -sizes[i] && pixelParticle.getX() < width && pixelParticle.getY() > -sizes[i] && pixelParticle.getY() < height)
						pixelParticle.render(AvianColor.white(AvianMath.sin(sinInputs[i]) * 50f));
				}
			}
		}

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);

	}

	@Override
	public void settingsKeyboard() {
	}

	@Override
	public void settingsMouse() {
		float h = ((AvianApp.getHeight() - 120f) / (games.size() + 3f));

		games.mouse(mx, my, settingsClick);

		if (AvianInput.isMouseButtonDown(0)) {
			if (!settingsClick) {
				int i = (int) ((my - 120) / h);
				if (i == 0 && bankrupt()) {
					spinMoney = false;
					awardMoney = true;
					Dashboard.closePanel();
				}
			}
			settingsClick = true;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			settingsClick = false;
		}
	}

	@Override
	public void settingsLogic() {

		games.logic();

		if (spinMoney && counter % 5 == 0)
			freeCoins = AvianMath.randomInt(50) + 1;

		sHY = games.sHY;
		sHH = games.sHH;
	}

	AvianRectangle highlight = new AvianRectangle(0, 0, AvianApp.getWidth(), 30);

	@SuppressWarnings("deprecation")
	@Override
	public void settingsRender() {

		boolean bankrupt = bankrupt();

		games.render();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		if (bankrupt) {
			Fonts.Vegur_ExtraSmall.drawString("Click for more coins...", (int) (120 + sHH / 2f) - 4, AvianColor.white(255), AvianFont.ALIGN_BOTTOM);
			Fonts.DroidSansMono.drawString(df2.format(freeCoins), (int) (AvianApp.getWidth() / 2 - (20f + Fonts.DroidSansMono.getWidth(df.format(money))) / 2) + 20, (int) (120 + sHH / 2) + 11, AvianColor.white(255), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
			Images.coin.render((int) (AvianApp.getWidth() / 2 - (20 + Fonts.DroidSansMono.getWidth("88")) / 2), (int) ((120 + sHH / 2) + 4));
		} else {
			Fonts.DroidSansMono.drawString(df.format(money + payout), (int) (AvianApp.getWidth() / 2f - (20f + Fonts.DroidSansMono.getWidth(df.format(money + payout))) / 2f) + 20, (int) (120 + sHH / 2f) - 1, AvianColor.white(255), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
			Images.coin.render((int) (AvianApp.getWidth() / 2 - (20 + Fonts.DroidSansMono.getWidth(df.format(money + payout))) / 2), (int) ((120 + sHH / 2) - 8));
		}
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);

		if ((255f / 5f) - games.highlightAlpha > 0 && my < 120 + sHH && bankrupt) {
			highlight.set(20, sHY, AvianApp.getWidth() - 40, sHH);
			highlight.render(AvianColor.white((255f / 5f) - games.highlightAlpha));
		}
		//		for (int i = 1; i < games.size() + 3; i++) {
		//
		//			float height = ((float) AvianAppCore.getHeight() - 120f) / ((float) games.size() + 3f);
		//
		//			if (i <= games.size()) {
		//				Fonts.Vegur_Small.renderCenteredString(games.get(i - 1).getName(), (int) (AvianAppCore.getWidth() / 2f - 8f), (int) (120f + (i * height) + (height / 2f)), color.setRGBA(255, gb, gb, 255), true);
		//			} else if (i == games.size() + 1) {
		//				Images.X.render(AvianAppCore.getWidth() / 2f - 8f, 120f + (i * height) + (height / 2f) - 8, color.setRGBA(255, gb, gb, 255));
		//			}
		//		}

	}

	boolean spinMoney = false;
	boolean awardMoney = true;

	private boolean bankrupt() {
		try {
			boolean broke = true;
			for (int i = 0; i < Dashboard.elements.size(); i++) {
				Element e = Dashboard.elements.get(i);
				if (e instanceof GameScreen) {
					GameScreen g = (GameScreen) e;
					if (g.getTableMoney() > 0) {
						broke = false;
						break;
					}
					if (g.game != null &&g.game.getName().equals("Slot Machine")) {
						if (((SlotMachine) g.game).isSpinning()) {
							broke = false;
							break;
						}
					}

				}
			}
			return broke && money <= 0 && payout <= 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void settingsOpened() {
		if (bankrupt()) {
			spinMoney = true;
		}

	}

	@Override
	public void closeSettings() {
		if (awardMoney && bankrupt()) {
			addMoney(freeCoins);
			awardMoney = false;
		}
	}

	private class Coin {

		float x, y, xx, yy;

		public Coin() {
			x = AvianMath.randomFloat() * width;
			y = -20;
			xx = x + (AvianMath.randomFloat() * 20f - 10f);
			yy = AvianMath.randomFloat() * height;
		}

		public boolean doneMoving() {
			if (Math.round(x) == Math.round(xx) && Math.round(y) == Math.round(yy))
				return true;
			else if (sec >= 3f)
				return true;

			else if (x <= -Images.coin.getWidth() || x >= width)
				return true;
			else if (y <= -Images.coin.getHeight() || y >= height)
				return true;
			return false;
		}

		float sec;

		public void logic() {
			sec += .01f;
			x = AvianMath.glide(x, xx, 10f);
			y = AvianMath.glide(y, yy, 10f);
		}

		public void render() {
			Images.coin.render(x, y);
		}
	}

	AvianRectangle particle = new AvianRectangle(0, 0, 4, 4);

	@SuppressWarnings("unused")
	private class Firework {

		int particles = 4;
		float[] xv = new float[particles], yv = new float[particles],
				x = new float[particles], y = new float[particles];
		float alpha;

		public Firework() {
			float xx = AvianMath.randomFloat() * width;
			float yy = AvianMath.randomFloat() * height;
			for (int i = 0; i < particles; i++) {
				this.x[i] = xx;
				this.y[i] = yy;
				xv[i] = AvianMath.randomFloat() * 1f - .5f;
				yv[i] = -AvianMath.randomFloat();
			}
			alpha = 150 + AvianMath.randomFloat() * 105f;
		}

		public Firework(float x, float y) {
			for (int i = 0; i < particles; i++) {
				this.x[i] = x;
				this.y[i] = y;
				xv[i] = AvianMath.randomFloat() * 1f - .5f;
				yv[i] = -AvianMath.randomFloat();
			}
			alpha = 150 + AvianMath.randomFloat() * 105f;
		}

		public void logic() {
			for (int i = 0; i < particles; i++) {
				yv[i] += .015f;
				x[i] += xv[i];
				y[i] += yv[i];
			}
			alpha--;
		}

		public void render() {
			for (int i = 0; i < particles; i++) {
				particle.set(x[i], y[i], 3, 3);
				particle.render(AvianColor.white(alpha));
			}
		}
	}

	LinkedList<Firework> fireworks = new LinkedList<Firework>();
	LinkedList<Coin> coins = new LinkedList<Coin>();

	public static void addMoney(long m) {
		payout += m;
	}

	public static void payMoney(long m) {
		GameScreen.money -= m;
	}

	@Override
	public void destroy() {
		for (int i = 0; i < games.size(); i++) {
			Game g = games.get(i);
			if (g instanceof CasinoGame) {
				addMoney(((CasinoGame) g).getMoneyValue());
				((CasinoGame) g).cashIn();
			}
		}
	}

	@Override
	public String getName() {
		return "Game";
	}

	@Override
	public Element create() {
		return new GameScreen();
	}

}
