
package musicaflight.dashboard.games;

import static org.lwjgl.opengl.GL11.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import musicaflight.avianutils.*;
import musicaflight.dashboard.*;
import musicaflight.dashboard.games.MiniBlockery.Block.BlockColor;

public class MiniBlockery extends Game {

	public Block[][] blocks;

	int newBlocks;
	static double bps;

	DecimalFormat format = new DecimalFormat("#,###");
	DecimalFormat format2 = new DecimalFormat("#,###.#");
	DecimalFormat format3 = new DecimalFormat("0.000");

	private long counterTrigger;

	AvianRectangle block = new AvianRectangle(0, 0, 20, 20);
	AvianRectangle gui = new AvianRectangle();

	static AvianRectangle particle = new AvianRectangle();
	static AvianRectangle bg = new AvianRectangle();

	static float yOffset;
	static boolean buyFactories;

	public static double totalBlocks;

	ArrayList<Particle> particles = new ArrayList<Particle>();

	public MiniBlockery() {
		super(false);
		initializeBlocks();
	}

	AvianColor color;

	public static BlockProducer bkit = new BlockProducer(ProducerType.BLOCKKIT);
	public static BlockProducer cott = new BlockProducer(ProducerType.COTTAGE);
	public static BlockProducer fact = new BlockProducer(ProducerType.FACTORY);
	public static BlockProducer mine = new BlockProducer(ProducerType.MINE);
	public static BlockProducer powh = new BlockProducer(ProducerType.POWERHOUSE);
	public static BlockProducer mpow = new BlockProducer(ProducerType.MEGAPOWERHOUSE);

	static BlockProducer[] producers = new BlockProducer[] { bkit, cott, fact,
			mine, powh, mpow };

	int slotOffset;

	protected void initializeBlocks() {
		blocks = new Block[14][12];
		for (int r = 0; r < blocks.length; r++)
			for (int c = 0; c < blocks[r].length; c++)
				blocks[r][c] = new Block(r, c);
	}

	protected void gameKeyboard() {
	}

	boolean contentClick;

	public void gameMouse() {
		for (int i = 0; i < producers.length; i++) {
			producers[i].hover(my);
		}
		if (AvianInput.isMouseButtonDown(0)) {
			if (!contentClick) {

				if (buyFactories) {
					for (int i = 0; i < producers.length; i++) {
						producers[i].clicked(my);
					}
					if (my < 65) {
						buyFactories = false;
					}
				} else {
					beginElimination(mx, my);
					if (my > 235 + 18) {
						buyFactories = true;
					}
				}
			}
			contentClick = true;
		} else if (!AvianInput.isMouseButtonDown(0))
			contentClick = false;

		slotOffset += AvianInput.getScroll();

	}

	public void gameLogic() {

		if (slotOffset > 0)
			slotOffset = 0;
		else if (slotOffset < 5 - producers.length)
			slotOffset = 5 - producers.length;

		bps = 0;
		for (int i = 0; i < producers.length; i++) {
			BlockProducer bp = producers[i];
			bps += bp.owned() * bp.BPS();
		}
		Iterator<Particle> iter = particles.iterator();
		while (iter.hasNext()) {
			Particle p = iter.next();
			p.logic();
			if (p.y > height) {
				iter.remove();
			}
		}

		int blockeries = 0;
		for (int i = 0; i < Dashboard.elements.size(); i++) {
			Element e = Dashboard.elements.get(i);
			if (e instanceof GameScreen) {
				if (((GameScreen) e).game instanceof MiniBlockery) {
					blockeries++;
				}
			}
		}

		if (buyFactories) {
			yOffset = AvianMath.glide(yOffset, -300f, 10f * blockeries);
		} else {
			yOffset = AvianMath.glide(yOffset, 0, 10f * blockeries);
		}

		counterLogic();
		blockLogic();
		itemLogic();
	}

	protected void counterLogic() {
		counterTrigger++;

		if (newBlocks > 0 && counterTrigger % 6 == 1) {
			totalBlocks += (newBlocks / 5) + 1;
			newBlocks -= (newBlocks / 5) + 1;
		}

		int blockeries = 0;
		for (int i = 0; i < Dashboard.elements.size(); i++) {
			Element e = Dashboard.elements.get(i);
			if (e instanceof GameScreen) {
				if (((GameScreen) e).game instanceof MiniBlockery) {
					blockeries++;
				}
			}
		}

		totalBlocks += (bps / (100 * blockeries));
	}

	protected void blockLogic() {
		for (int r = blocks.length - 1; r >= 0; r--)
			for (int c = blocks[0].length - 1; c >= 0; c--)
				if (blocks[r][c] == null)
					if (r > 0) {
						blocks[r][c] = blocks[r - 1][c];
						blocks[r - 1][c] = null;
					} else
						blocks[r][c] = new Block(18f * c + ((width / 2) - 18 * 6) + 2.5f, -50f, r, c);

		for (int r = 0; r < blocks.length; r++)
			for (int c = 0; c < blocks[r].length; c++)
				if (blocks[r][c] != null)
					blocks[r][c].logic(r, c);

	}

	protected void gameRender() {

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		block.setW(13);
		block.setH(13);

		for (int r = 0; r < blocks.length; r++)
			for (int c = 0; c < blocks[r].length; c++)
				if (blocks[r][c] != null) {
					block.setX(blocks[r][c].getX());
					block.setY(blocks[r][c].getY() + Math.round(yOffset));
					float a = 175f;
					if (block.getY() < 50) {
						a = ((block.getY() + 13) / (55f + 13f)) * 75;
						if (a > 75f)
							a = 75f;
					}
					block.render(blocks[r][c].getBlockColor().setA(a));
				}

		for (int i = 0; i < particles.size(); i++) {
			particle.set(particles.get(i).x, particles.get(i).y, 13, 13);
			float a = 175f;
			if (particle.getY() < 55) {
				a = ((particle.getY() + 13) / (55f + 13f)) * 75;
				if (a > 75f)
					a = 75f;
			}
			particle.render(particles.get(i).color.getColor().setA(a));
		}

		for (int i = 0; i < producers.length; i++) {
			BlockProducer bp = producers[i];
			bp.render();
		}

		AvianColor c = AvianColor.white(255);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);

		if (totalBlocks >= 1_000_000_000_000_000_000_000_000.0) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000_000_000_000_000_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("septillion blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else if (totalBlocks >= 1_000_000_000_000_000_000_000.0) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000_000_000_000_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("sextillion blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else if (totalBlocks >= 1_000_000_000_000_000_000.0) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000_000_000_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("quintillion blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else if (totalBlocks >= 1_000_000_000_000_000.0) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000_000_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("quadrillion blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else if (totalBlocks >= 1_000_000_000_000.0) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("trillion blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else if (totalBlocks >= 1_000_000_000) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("billion blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else if (totalBlocks >= 1_000_000) {
			Fonts.SFDR.drawString(format3.format(totalBlocks / 1_000_000.0), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("million blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);

		} else {
			Fonts.SFDR.drawString(format.format(totalBlocks), (width / 2f), 30, c, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("blocks", (width / 2f), 50, c, AvianFont.ALIGN_CENTER);
		}

		Fonts.Vegur_ExtraSmall.drawString(format2.format(bps) + " BPS", (width / 2f), (height - 20), c, AvianFont.ALIGN_CENTER);

	}

	protected void beginElimination(float mouseX, float mouseY) {

		int row = (int) ((mouseY - 55 + (3 * 18)) / 18);
		int col = (int) ((mouseX - ((width / 2) - 18 * 6)) / 18);

		boolean isGreaterThanOne = false;

		if ((row >= 0) && (row <= (blocks.length - 1)) && (col >= 0) && (col <= (blocks[0].length - 1))) {
			if (blocks[row][col] == null)
				return;

			if ((row > 0) && (blocks[row - 1][col] != null) && (blocks[row - 1][col].getBlockColorEnum() == blocks[row][col].getBlockColorEnum()))
				isGreaterThanOne = true;

			if ((row < (blocks.length - 1)) && (blocks[row + 1][col] != null) && (blocks[row + 1][col].getBlockColorEnum() == blocks[row][col].getBlockColorEnum()))
				isGreaterThanOne = true;

			if ((row < (blocks.length - 1)) && (blocks[row + 1][col] != null) && (blocks[row + 1][col].getBlockColorEnum() == blocks[row][col].getBlockColorEnum()))
				isGreaterThanOne = true;

			if ((col > 0) && (blocks[row][col - 1] != null) && (blocks[row][col - 1].getBlockColorEnum() == blocks[row][col].getBlockColorEnum()))
				isGreaterThanOne = true;

			if ((col < (blocks[0].length - 1)) && (blocks[row][col + 1] != null) && (blocks[row][col + 1].getBlockColorEnum() == blocks[row][col].getBlockColorEnum()))
				isGreaterThanOne = true;

			if (isGreaterThanOne)
				eliminateBlocks(row, col, blocks[row][col].getBlockColorEnum());

		}
	}

	protected void eliminateBlocks(int row, int col, BlockColor c) {
		if (row < 0 || row > blocks.length - 1 || col < 0 || col > blocks[0].length - 1 || blocks[row][col] == null)
			return;

		if (blocks[row][col].getBlockColorEnum() == c) {
			//			Block fireworks

			//			if (newerBlocks > 1)
			//				particles.add(new BlockFirework(blocks[row][col].getX() + (AvianAppCore.WIDTH / 2), blocks[row][col].getZ() + (AvianAppCore.HEIGHT / 2), blocks[row][col].getBlockColorEnum(), "+" + newerBlocks));
			//			else
			//				particles.add(new BlockFirework(blocks[row][col].getX() + (AvianAppCore.WIDTH / 2), blocks[row][col].getZ() + (AvianAppCore.HEIGHT / 2), blocks[row][col].getBlockColorEnum(), ""));

			newBlocks += 1;

			particles.add(new Particle(row, col, blocks[row][col].getBlockColorEnum()));
			blocks[row][col] = null;

			eliminateBlocks(row - 1, col, c);
			eliminateBlocks(row + 1, col, c);
			eliminateBlocks(row, col - 1, c);
			eliminateBlocks(row, col + 1, c);
		}
	}

	protected void itemLogic() {
		for (int i = 0; i < producers.length; i++) {
			producers[i].logic();
		}
	}

	protected void resetGame() {
		for (int r = 0; r < blocks.length; r++)
			for (int c = 0; c < blocks[r].length; c++)
				blocks[r][c] = new Block(r, c);

		newBlocks = 0;
		totalBlocks = bps = 0;

		for (int i = 0; i < producers.length; i++) {
			producers[i].reset();
		}

		Data.saveData();
	}

	@Override
	public String getName() {
		return "Mini Blockery";
	}

	private enum ProducerType {
		BLOCKKIT(
				0,
				"Block Kit",
				50,
				.1),
		COTTAGE(
				1,
				"Cottage House",
				200,
				1),
		FACTORY(
				2,
				"Factory",
				1500,
				5),
		MINE(
				3,
				"Blockium Mine",
				8250,
				25),
		POWERHOUSE(
				4,
				"Powerhouse",
				20000,
				75),
		MEGAPOWERHOUSE(
				5,
				"Megapowerhouse",
				250000,
				1000);

		public double initprice, addBPS;
		public String name;
		public int slot;

		ProducerType(int slot, String name, double price, double bps) {
			this.slot = slot;
			this.initprice = price;
			addBPS = bps;
			this.name = name;
		}
	}

	public static class BlockProducer {

		private double price;
		private int owned;

		int slot;

		int ms, s, m, h;
		String time;

		boolean hover = false;

		ProducerType type;

		float y;

		DecimalFormat format = new DecimalFormat("#,##0");
		DecimalFormat format2 = new DecimalFormat("+#,##0.#;-#,##0.#");

		static AvianRectangle highlight = new AvianRectangle();

		float alpha;

		public BlockProducer(ProducerType type) {
			this.type = type;
			slot = type.slot;

			owned = 0;

		}

		double dimSin = 90;

		double hoverSin;
		int imgX, textX, dim;

		final int smoothness = 5;

		public void logic() {
			price = type.initprice * Math.pow(1.3, owned);

			if (hover) {
				alpha = AvianMath.glide(alpha, 1f, 10f);
			} else {
				alpha = AvianMath.glide(alpha, 0f, 10f);
			}
		}

		@SuppressWarnings("unused")
		public void render() {
			String blocksPerSecond = format2.format(type.addBPS) + " BPS";
			String ownage = owned + " owned";

			float selectionHeight = (height - 65 * 2) / producers.length;

			if (alpha > 0) {
				highlight.set(0, yOffset + 365 + (slot * selectionHeight), width, selectionHeight);
				highlight.render(AvianColor.white(alpha * (affordable() ? 50 : 25)));
			}

			Fonts.Vegur_ExtraSmall.drawString(type.name, 20, yOffset + 365 + (slot * selectionHeight) + (selectionHeight / 2), AvianColor.white(affordable() ? 255 : 150), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString("costs " + format.format(price), width / 2.5f, yOffset + 365 + (slot * selectionHeight) + (selectionHeight / 2), AvianColor.white(affordable() ? 255 : 150), AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
			Fonts.Vegur_ExtraSmall.drawString(format2.format(type.addBPS) + " BPS", width - 20f, yOffset + 365 + (slot * selectionHeight) + (selectionHeight / 2), AvianColor.white(affordable() ? 255 : 150), AvianFont.ALIGN_RIGHT, AvianFont.ALIGN_CENTER);

		}

		public boolean affordable() {
			return totalBlocks >= price;
		}

		public int owned() {
			return owned;
		}

		public void owned(int o) {
			owned = o;
		}

		public double BPS() {
			return type.addBPS;
		}

		public boolean hover(float mouseY) {
			float selectionHeight = (height - 65 * 2) / producers.length;

			if (buyFactories && mouseY >= 65 + (slot * selectionHeight) && mouseY <= 65 + (slot * selectionHeight) + (selectionHeight))
				return hover = true;
			return hover = false;
		}

		public boolean clicked(float mouseY) {
			if (hover(mouseY)) {
				if (affordable()) {
					totalBlocks -= price;
					owned++;
					alpha = 0;
				}
				return true;
			}
			return false;
		}

		public void reset() {
			owned = 0;
		}

	}

	class Particle {

		int row, col;
		float x, y, xv, yv;
		BlockColor color;
		int bounces = AvianMath.randomInt(3);

		public Particle(int row, int col, BlockColor color) {
			this.row = row;
			this.col = col;
			this.color = color;
			x = 18f * col + ((width / 2) - 18f * 6f) + 2.5f;
			y = 18f * row + 55f - (3f * 18f);
			xv = AvianMath.randomFloat() * 2.5f - 1.25f;
			yv = -AvianMath.randomFloat() * 3f;
			int randColor = (AvianMath.randomInt(4));
			switch (randColor) {
				case 0:
					color = BlockColor.RED;
					break;
				case 1:
					color = BlockColor.ORANGE;
					break;
				case 2:
					color = BlockColor.GREEN;
					break;
				case 3:
					color = BlockColor.BLUE;
					break;
			}
		}

		public void logic() {
			yv += .15f;
			x += xv;
			y += yv;
			if (y > height - 13 && bounces > 0) {
				y = height - 13;
				yv *= -.5f;
				bounces--;
			}
		}
	}

	static class Block {

		BlockColor color;

		float x, y, Fx, Fy;

		public static enum BlockColor {
			//			RED(
			//					new AvianColor(255, 0, 76, 200)),
			//			ORANGE(
			//					new AvianColor(255, 165, 48, 200)),
			//			GREEN(
			//					new AvianColor(80, 236, 140, 200)),
			//			BLUE(
			//					new AvianColor(17, 124, 255, 200));
			RED(
					new AvianColor(255, 70, 146, 175)),
			ORANGE(
					new AvianColor(255, 235, 118, 175)),
			GREEN(
					new AvianColor(150, 255, 210, 175)),
			BLUE(
					new AvianColor(87, 194, 255, 175));
			private AvianColor color;

			BlockColor(AvianColor color) {
				this.color = color;
			}

			public AvianColor getColor() {
				return color;
			}
		}

		public int row, col;

		public Block(int row, int col) {
			this.row = row;
			this.col = col;
			Fx = x = 18f * col + ((width / 2) - 18f * 6f) + 2.5f;
			Fy = y = 18f * row + 55f - (3f * 18f);
			int randColor = (AvianMath.randomInt(4));
			switch (randColor) {
				case 0:
					color = BlockColor.RED;
					break;
				case 1:
					color = BlockColor.ORANGE;
					break;
				case 2:
					color = BlockColor.GREEN;
					break;
				case 3:
					color = BlockColor.BLUE;
					break;
			}
		}

		public Block(float x, float y, int row, int col) {
			this(row, col);
			Fx = this.x = x;
			Fy = this.y = y;
			velocity = 7.5f;
		}

		public BlockColor getBlockColorEnum() {
			return color;
		}

		public AvianColor getBlockColor() {
			return color.getColor();
		}

		float velocity;

		public void logic(int r, int c) {
			velocity += 15f / 100f;
			Fx = 18f * c + ((width / 2) - 18f * 6f) + 2.5f;
			Fy = 18f * r + 55f - (3f * 18f);
			x = AvianMath.glide(x, Fx, 10);
			y += velocity;
			if (y >= Fy) {
				y = Fy;
				velocity *= -.3f;
			}

		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

	}

}
