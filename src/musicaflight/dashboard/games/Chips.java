
package musicaflight.dashboard.games;

import static org.lwjgl.opengl.GL11.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

import musicaflight.avianutils.*;
import musicaflight.dashboard.*;

public class Chips {

	public enum ChipColor {
		WHITE(
				1),
		RED(
				5),
		BLUE(
				10),
		GREEN(
				25),
		BLACK(
				100);
		private int value;

		ChipColor(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static class Chip {

		boolean onTable;
		boolean stolen;

		ChipColor color;

		Chip(ChipColor type) {
			color = type;
			x = GameScreen.width / 2f;
			y = -30;
			tableX = AvianMath.randomFloat() - .5f;
			tableY = GameScreen.height / 2f + AvianMath.randomInt((int) (GameScreen.height / 4)) - GameScreen.height / 8f;
			onTable = true;
		}

		Chip(ChipColor type, boolean putOnTable) {
			this(type);
			this.onTable = putOnTable;
		}

		public void putOnTable() {
			if (!stolen)
				onTable = true;
		}

		public void collect() {
			if (!stolen)
				onTable = false;
		}

		public void stolen() {
			onTable = false;
			stolen = true;
		}

		float x, y, tableX, tableY;

		public void logic(int stack) {
			if (!stolen) {
				if (onTable) {
					x = AvianMath.glide(x, GameScreen.width / 2f - (tableX * (GameScreen.width / 4f)), 10f);
					y = AvianMath.glide(y, tableY, 10f);
				} else {
					switch (color) {
						case WHITE:
							x = AvianMath.glide(x, GameScreen.width / 2f - 100f, 10f);
							break;
						case RED:
							x = AvianMath.glide(x, GameScreen.width / 2f - 50f, 10f);
							break;
						case BLUE:
							x = AvianMath.glide(x, GameScreen.width / 2f, 10f);
							break;
						case GREEN:
							x = AvianMath.glide(x, GameScreen.width / 2f + 50f, 10f);
							break;
						case BLACK:
							x = AvianMath.glide(x, GameScreen.width / 2f + 100f, 10f);
							break;
					}
					y = AvianMath.glide(y, 250 - (stack > 50 ? 50 : stack) + Images.chips.getCroppedHeight() / 2, 10f);
				}
			} else {
				x = AvianMath.glide(x, GameScreen.width / 2f, 10f);
				y = AvianMath.glide(y, -30f, 10f);
			}
		}

		public void render() {
			switch (color) {
				case WHITE:
					Images.chips.crop(0f, 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case RED:
					Images.chips.crop(Images.chips.getWidth() * (1f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case BLUE:
					Images.chips.crop(Images.chips.getWidth() * (2f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case GREEN:
					Images.chips.crop(Images.chips.getWidth() * (3f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case BLACK:
					Images.chips.crop(Images.chips.getWidth() * (4f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
			}
			Images.chips.render(x - Images.chips.getCroppedWidth() / 2, y - Images.chips.getCroppedHeight() / 2);

		}

		void render(float yOffset) {
			switch (color) {
				case WHITE:
					Images.chips.crop(0f, 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case RED:
					Images.chips.crop(Images.chips.getWidth() * (1f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case BLUE:
					Images.chips.crop(Images.chips.getWidth() * (2f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case GREEN:
					Images.chips.crop(Images.chips.getWidth() * (3f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
				case BLACK:
					Images.chips.crop(Images.chips.getWidth() * (4f / 5f), 0, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
					break;
			}
			Images.chips.render(x - Images.chips.getCroppedWidth() / 2, y - Images.chips.getCroppedHeight() / 2 + yOffset);

		}

		public long getValue() {
			return color.getValue();
		}

		public ChipColor getColor() {
			return color;
		}
	}

	public static class ChipStacks {

		public ChipStacks() {
		}

		Dealer dealer = new Dealer();

		private boolean hide;
		private boolean cashClick;
		private float cashInY;
		private float cashAlpha;
		private boolean cashRequest;
		private float y;

		Stack<Chip> white = new Stack<Chip>();
		Stack<Chip> red = new Stack<Chip>();
		Stack<Chip> blue = new Stack<Chip>();
		Stack<Chip> green = new Stack<Chip>();
		Stack<Chip> black = new Stack<Chip>();

		public long getTotalValue() {
			return white.size() * ChipColor.WHITE.getValue() + red.size() * ChipColor.RED.getValue() + blue.size() * ChipColor.BLUE.getValue() + green.size() * ChipColor.GREEN.getValue() + black.size() * ChipColor.BLACK.getValue();
		}

		public void hide() {
			hide = true;
		}

		public void toggleHidden() {
			hide = !hide;
		}

		public void show() {
			hide = false;
		}

		public boolean click(float mx, float my) {
			if (hide)
				return false;
			boolean topBound = my > 200;
			boolean leftBound = mx >= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 100f;
			boolean rightBound = mx <= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 100f + Images.chips.getCroppedWidth();
			if (my > 270 && leftBound && rightBound && getTotalValue() > 0) {
				cashClick = true;
			}
			return (topBound && leftBound && rightBound) || cashClick;
		}

		public Chip getClickedChip(float mx, float my) {
			if (hide)
				return null;

			if (my > 200 && my < 270) {
				if (mx >= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 100f && mx <= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 100f + Images.chips.getCroppedWidth()) {
					return remove(ChipColor.BLACK);
				}
				if (mx >= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 50f && mx <= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 50f + Images.chips.getCroppedWidth()) {
					return remove(ChipColor.GREEN);
				}
				if (mx >= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f && mx <= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + Images.chips.getCroppedWidth()) {
					return remove(ChipColor.BLUE);
				}
				if (mx >= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 50f && mx <= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 50f + Images.chips.getCroppedWidth()) {
					return remove(ChipColor.RED);
				}
				if (mx >= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 100f && mx <= GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 100f + Images.chips.getCroppedWidth()) {
					return remove(ChipColor.WHITE);
				}
			}
			return null;
		}

		public void add(Chip... chips) {
			if (chips == null)
				return;
			for (int i = 0; i < chips.length; i++) {
				if (chips[i] == null)
					continue;
				chips[i].collect();
				switch (chips[i].getColor()) {
					case BLACK:
						black.push(chips[i]);
						break;
					case BLUE:
						blue.push(chips[i]);
						break;
					case GREEN:
						green.push(chips[i]);
						break;
					case RED:
						red.push(chips[i]);
						break;
					case WHITE:
						white.push(chips[i]);
						break;
				}
			}
		}

		public void add(int money) {
			add(money, true);
		}

		private void add(int money, boolean splitFives) {
			while (money > ChipColor.BLACK.getValue()) {
				add(new Chip(ChipColor.BLACK));
				money -= ChipColor.BLACK.getValue();
			}
			while (money > ChipColor.GREEN.getValue()) {
				add(new Chip(ChipColor.GREEN));
				money -= ChipColor.GREEN.getValue();
			}
			while (money > ChipColor.BLUE.getValue()) {
				add(new Chip(ChipColor.BLUE));
				money -= ChipColor.BLUE.getValue();
			}
			if (splitFives) {
				while (money > ChipColor.RED.getValue()) {
					add(new Chip(ChipColor.RED));
					money -= ChipColor.RED.getValue();
				}
			} else {

				while (money >= ChipColor.RED.getValue()) {
					add(new Chip(ChipColor.RED));
					money -= ChipColor.RED.getValue();
				}
			}
			while (money >= ChipColor.WHITE.getValue()) {
				add(new Chip(ChipColor.WHITE));
				money -= ChipColor.WHITE.getValue();
			}
		}

		public Chip remove(ChipColor color) {
			switch (color) {
				case BLACK:
					if (black.size() <= 0)
						retrieveChip(ChipColor.BLACK);
					else
						return (black.pop());
					break;
				case BLUE:
					if (blue.size() <= 0)
						retrieveChip(ChipColor.BLUE);
					else
						return (blue.pop());
					break;
				case GREEN:
					if (green.size() <= 0)
						retrieveChip(ChipColor.GREEN);
					else
						return (green.pop());
					break;
				case WHITE:
					if (white.size() <= 0)
						retrieveChip(ChipColor.WHITE);
					else
						return (white.pop());
					break;
				case RED:
					if (red.size() <= 0)
						retrieveChip(ChipColor.RED);
					else
						return (red.pop());
					break;
			}
			return null;
		}

		//TODO
		private void retrieveChip(ChipColor to) {
			switch (to) {
				case BLUE:
					if (green.size() > 0) {
						dealer.takeChips(green.pop());
						add(ChipColor.GREEN.getValue(), false);
					} else if (black.size() > 0) {
						dealer.takeChips(black.pop());
						add(ChipColor.BLACK.getValue(), false);
					} else if (white.size() * ChipColor.WHITE.getValue() + red.size() * ChipColor.RED.getValue() >= ChipColor.BLUE.getValue()) {
						int value = 0;
						while (value < ChipColor.BLUE.getValue()) {
							if (red.size() > 0) {
								dealer.takeChips(red.pop());
								value += ChipColor.RED.getValue();
							} else {
								dealer.takeChips(white.pop());
								value += ChipColor.WHITE.getValue();
							}
						}
						add(new Chip(ChipColor.BLUE));
					}
					break;
				case BLACK:
					if (white.size() * ChipColor.WHITE.getValue() + red.size() * ChipColor.RED.getValue() + blue.size() * ChipColor.BLUE.getValue() + green.size() * ChipColor.GREEN.getValue() >= ChipColor.BLACK.getValue()) {
						int value = 0;
						while (value < ChipColor.BLACK.getValue()) {
							if (green.size() > 0) {
								dealer.takeChips(green.pop());
								value += ChipColor.GREEN.getValue();
							} else if (blue.size() > 0) {
								dealer.takeChips(blue.pop());
								value += ChipColor.BLUE.getValue();
							} else if (red.size() > 0) {
								dealer.takeChips(red.pop());
								value += ChipColor.RED.getValue();
							} else {
								dealer.takeChips(white.pop());
								value += ChipColor.WHITE.getValue();
							}
						}
						add(new Chip(ChipColor.BLACK));
					}
					break;
				case GREEN:
					// Check to see if higher-valued chips can be split up to satisfy the requested chip.
					// If not, check to see if lower-value chips can add up to the requested chip.
					if (black.size() > 0) {
						dealer.takeChips(black.pop());
						add(ChipColor.BLACK.getValue(), false);
					} else if (white.size() * ChipColor.WHITE.getValue() + red.size() * ChipColor.RED.getValue() + blue.size() * ChipColor.BLUE.getValue() >= ChipColor.GREEN.getValue()) {

						// Start with the highest-valued chip 
						// and remove chips until the removed value 
						// equals the value of the requested chip.

						int value = 0;
						while (value < ChipColor.GREEN.getValue()) {
							if (blue.size() > 0) {
								dealer.takeChips(blue.pop());
								value += ChipColor.BLUE.getValue();
							} else if (red.size() > 0) {
								dealer.takeChips(red.pop());
								value += ChipColor.RED.getValue();
							} else {
								dealer.takeChips(white.pop());
								value += ChipColor.WHITE.getValue();
							}
						}
						add(new Chip(ChipColor.GREEN));
					}
					break;
				case RED:
					if (blue.size() > 0) {
						dealer.takeChips(blue.pop());
						add(ChipColor.BLUE.getValue(), false);
					} else if (green.size() > 0) {
						dealer.takeChips(green.pop());
						add(ChipColor.GREEN.getValue(), false);
					} else if (black.size() > 0) {
						dealer.takeChips(black.pop());
						add(ChipColor.BLACK.getValue(), false);
					} else if (white.size() * ChipColor.WHITE.getValue() >= ChipColor.RED.getValue()) {
						int value = 0;
						while (value < ChipColor.RED.getValue()) {
							dealer.takeChips(white.pop());
							value += ChipColor.WHITE.getValue();
						}
						add(new Chip(ChipColor.RED));
					}
					break;
				case WHITE:
					if (red.size() > 0) {
						dealer.takeChips(red.pop());
						add(ChipColor.RED.getValue());
					} else if (blue.size() > 0) {
						dealer.takeChips(blue.pop());
						add(ChipColor.BLUE.getValue());
					} else if (green.size() > 0) {
						dealer.takeChips(green.pop());
						add(ChipColor.GREEN.getValue());
					} else if (black.size() > 0) {
						dealer.takeChips(black.pop());
						add(ChipColor.BLACK.getValue());
					}
					break;
				default:
					break;
			}
		}

		float totalValue;

		public void logic() {
			if (!AvianInput.isMouseButtonDown(0)) {
				if (cashClick && Game.my < Game.height / 2)
					cashRequest = true;
				cashClick = false;
			}

			totalValue = AvianMath.glide(totalValue, getTotalValue(), 5f);

			for (int i = 0; i < white.size(); i++)
				white.get(i).logic(i);

			for (int i = 0; i < red.size(); i++)
				red.get(i).logic(i);

			for (int i = 0; i < blue.size(); i++)
				blue.get(i).logic(i);

			for (int i = 0; i < green.size(); i++)
				green.get(i).logic(i);

			for (int i = 0; i < black.size(); i++)
				black.get(i).logic(i);

			dealer.logic();

			if (cashClick) {
				cashInY = AvianMath.glide(cashInY, Game.my < Game.height / 2 ? Game.my : Game.my / 2 + Game.height / 4, 10f);
				r.setW(AvianMath.glide(r.getW(), Game.width, 10f));
				if (Game.my < Game.height / 2) {
					cashAlpha = AvianMath.glide(cashAlpha, 1, 10f);
				} else {
					cashAlpha = AvianMath.glide(cashAlpha, .5f, 10f);
				}
			} else {
				cashInY = AvianMath.glide(cashInY, Game.height + 30, 10f);
				r.setW(AvianMath.glide(r.getW(), 0, 10f));
				cashAlpha = AvianMath.glide(cashAlpha, 0f, 10f);
			}

			if (hide) {
				y = AvianMath.glide(y, 100, 10f);
			} else {
				y = AvianMath.glide(y, 0, 10f);
			}

		}

		AvianCircle c = new AvianCircle();

		AvianRectangle r = new AvianRectangle();

		static DecimalFormat df = new DecimalFormat("#,##0");

		public void render() {

			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glEnableClientState(GL_COLOR_ARRAY);

			Images.chips.crop(0, Images.chips.getHeight() / 2f, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
			Images.chips.render(GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 100f, 250 + y);

			Images.chips.crop(Images.chips.getWidth() * (1f / 5f), Images.chips.getHeight() / 2f, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
			Images.chips.render(GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f - 50f, 250 + y);

			Images.chips.crop(Images.chips.getWidth() * (2f / 5f), Images.chips.getHeight() / 2f, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
			Images.chips.render(GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f, 250 + y);

			Images.chips.crop(Images.chips.getWidth() * (3f / 5f), Images.chips.getHeight() / 2f, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
			Images.chips.render(GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 50f, 250 + y);

			Images.chips.crop(Images.chips.getWidth() * (4f / 5f), Images.chips.getHeight() / 2f, Images.chips.getWidth() / 5f, Images.chips.getHeight() / 2f);
			Images.chips.render(GameScreen.width / 2f - Images.chips.getCroppedWidth() / 2f + 100f, 250 + y);

			for (int i = 0; i < white.size(); i++)
				if (i < 50 || i >= white.size() - 2)
					white.get(i).render(y);

			for (int i = 0; i < red.size(); i++)
				if (i < 50 || i >= red.size() - 2)
					red.get(i).render(y);

			for (int i = 0; i < blue.size(); i++)
				if (i < 50 || i >= blue.size() - 2)
					blue.get(i).render(y);

			for (int i = 0; i < green.size(); i++)
				if (i < 50 || i >= green.size() - 2)
					green.get(i).render(y);

			for (int i = 0; i < black.size(); i++) {
				if (i < 50 || i >= black.size() - 2)
					black.get(i).render(y);
			}

			r.setX(Game.width / 2 - r.getW() / 2);
			r.setY(cashInY < Game.height / 2f ? cashInY - 15 : Game.height / 2 - 15);
			r.setH(30);
			r.render(AvianColor.white(cashAlpha * 50f));

			glDisableClientState(GL_VERTEX_ARRAY);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_COLOR_ARRAY);

			dealer.render();

			Fonts.Vegur_ExtraSmall.drawString(Game.my < Game.height / 2 ? "Release to cash in" : "Pull up to cash in", (Game.width / 2), (cashInY), AvianColor.white(cashAlpha * 255f), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
			Fonts.DroidSansMonoSmall.drawString(df.format(Math.round(totalValue)), (Game.width / 2), (285 + y), AvianColor.white(255f), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

		}

		public boolean cashInRequested() {
			if (cashRequest) {
				cashRequest = false;
				cashClick = false;
				return true;
			}
			return false;
		}

		public long cashIn() {
			long tot = (black.size() * ChipColor.BLACK.getValue()) + (green.size() * ChipColor.GREEN.getValue()) + (blue.size() * ChipColor.BLUE.getValue()) + (red.size() * ChipColor.RED.getValue()) + (white.size() * ChipColor.WHITE.getValue());
			ArrayList<Chip> chips = new ArrayList<Chip>();
			chips.addAll(white);
			chips.addAll(red);
			chips.addAll(blue);
			chips.addAll(green);
			chips.addAll(black);
			white.clear();
			red.clear();
			blue.clear();
			green.clear();
			black.clear();
			dealer.takeChips(chips.toArray(new Chip[chips.size()]));
			return tot;
		}
	}

	public static class Dealer {
		public Dealer() {

		}

		ArrayList<Chip> chips = new ArrayList<Chip>();

		public void takeChips(Chip... c) {
			if (c == null)
				return;

			for (int i = 0; i < c.length; i++) {
				c[i].stolen();
				this.chips.add(c[i]);
			}
		}

		public void logic() {
			for (int i = 0; i < chips.size(); i++)
				chips.get(i).logic(0);
		}

		public void render() {
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glEnableClientState(GL_COLOR_ARRAY);

			for (int i = 0; i < chips.size(); i++)
				if (chips.get(i).y > -20)
					chips.get(i).render();

			glDisableClientState(GL_VERTEX_ARRAY);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_COLOR_ARRAY);

		}
	}

	public static class ChipPile {

		public ChipPile() {

		}

		ArrayList<Chip> chips = new ArrayList<Chip>();

		public void add(Chip... c) {
			if (c == null)
				return;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == null)
					continue;
				c[i].putOnTable();
				this.chips.add(c[i]);
			}
		}

		public Chip[] removeAll() {
			Chip[] export = chips.toArray(new Chip[chips.size()]);
			chips.clear();
			return export;
		}

		public long getTotalValue() {
			long total = 0;
			for (int i = 0; i < chips.size(); i++) {
				total += chips.get(i).getValue();
			}
			return total;
		}

		public void logic() {
			for (int i = 0; i < chips.size(); i++)
				chips.get(i).logic(0);
		}

		public void render() {
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glEnableClientState(GL_COLOR_ARRAY);

			for (int i = 0; i < chips.size(); i++)
				chips.get(i).render();

			glDisableClientState(GL_VERTEX_ARRAY);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_COLOR_ARRAY);
		}

		public void add(long money, boolean splitFives) {
			while (money > ChipColor.BLACK.getValue()) {
				add(new Chip(ChipColor.BLACK));
				money -= ChipColor.BLACK.getValue();
			}
			while (money > ChipColor.GREEN.getValue()) {
				add(new Chip(ChipColor.GREEN));
				money -= ChipColor.GREEN.getValue();
			}
			while (money > ChipColor.BLUE.getValue()) {
				add(new Chip(ChipColor.BLUE));
				money -= ChipColor.BLUE.getValue();
			}
			if (splitFives) {
				while (money > ChipColor.RED.getValue()) {
					add(new Chip(ChipColor.RED));
					money -= ChipColor.RED.getValue();
				}
			} else {

				while (money >= ChipColor.RED.getValue()) {
					add(new Chip(ChipColor.RED));
					money -= ChipColor.RED.getValue();
				}
			}
			while (money >= ChipColor.WHITE.getValue()) {
				add(new Chip(ChipColor.WHITE));
				money -= ChipColor.WHITE.getValue();
			}
		}
	}
}
