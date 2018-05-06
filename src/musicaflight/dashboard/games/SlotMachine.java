
package musicaflight.dashboard.games;

import musicaflight.avianutils.*;
import musicaflight.dashboard.*;

public class SlotMachine extends CasinoGame {

	public SlotMachine() {
		super(false);
	}

	public String getName() {
		return "Slot Machine";
	}

	int bet;

	private boolean spinning;

	public boolean isSpinning() {
		return spinning;
	}

	Slot[] reelOne = new Slot[5];
	float reelOneVel;
	float reelOnePos;

	Slot[] reelTwo = new Slot[5];
	float reelTwoVel;
	float reelTwoPos;

	Slot[] reelThr = new Slot[5];
	float reelThrVel;
	float reelThrPos;

	private static enum Slot {
		CANDY,
		LAMP,
		TELEPHONE,
		GOLD,
		DIAMOND,
		BAR;
	}

	boolean clicked;

	public void gameKeyboard() {

	}

	public void gameMouse() {
		if (AvianInput.isMouseButtonDown(0) && reelOneVel == 0 && reelTwoVel == 0 && reelThrVel == 0 && bet > 0 && GameScreen.money > 0) {
			reelOneVel += 4;
			reelTwoVel += 5;
			reelThrVel += 6;
			spinning = true;
			GameScreen.money -= bet;
		}
		if (AvianInput.isMouseButtonDown(1) && !clicked && !spinning) {
			if (GameScreen.money > 0)
				bet++;
			if (bet > 5 || bet > GameScreen.money)
				bet = 0;
			clicked = true;
		} else if (!AvianInput.isMouseButtonDown(1))
			clicked = false;
	}

	/* 
	 *	CAND					2
	 *
	 *	CAND	CAND			5
	 *
	 *	LAMP	LAMP	BAR 	10
	 *
	 *	LAMP	LAMP	LAMP 	10
	 *
	 *	PHON	PHON	BAR 	14
	 *	
	 *	PHON	PHON	PHON 	14
	 *
	 *	GOLD	GOLD	BAR 	18
	 *
	 *	GOLD	GOLD	GOLD 	18
	 *
	 *	BAR		BAR		BAR		100
	 *
	 *	DIAM	DIAM	DIAM	200
	 */

	private void payMoney(Slot... payline) {
		if (payline[0] == Slot.CANDY) {
			if (payline[1] == Slot.CANDY)
				GameScreen.addMoney(5);
			else
				GameScreen.addMoney(2);
		} else if (payline[0] == Slot.LAMP && payline[1] == Slot.LAMP) {
			if (payline[2] == Slot.BAR || payline[2] == Slot.LAMP)
				GameScreen.addMoney(10);
		} else if (payline[0] == Slot.TELEPHONE && payline[1] == Slot.TELEPHONE) {
			if (payline[2] == Slot.BAR || payline[2] == Slot.TELEPHONE)
				GameScreen.addMoney(14);
		} else if (payline[0] == Slot.GOLD && payline[1] == Slot.GOLD) {
			if (payline[2] == Slot.BAR || payline[2] == Slot.GOLD)
				GameScreen.addMoney(18);
		} else if (payline[0] == Slot.BAR && payline[1] == Slot.BAR && payline[2] == Slot.BAR) {
			GameScreen.addMoney(100);
		} else if (payline[0] == Slot.DIAMOND && payline[1] == Slot.DIAMOND && payline[2] == Slot.DIAMOND) {
			GameScreen.addMoney(200);
		}
	}

	public void gameLogic() {
		if (bet > GameScreen.money && !spinning)
			bet = (int) GameScreen.money;

		if (errorAlpha > 0) {
			errorAlpha -= 1;
		}

		// Reel One
		reelOneVel -= .02;
		if (reelOneVel < 0f)
			reelOneVel = 0f;
		reelOnePos += reelOneVel;

		if (reelOnePos > 180f / reelOne.length) {
			reelOnePos -= 180f / reelOne.length;
			for (int i = 4; i > 0; i--)
				reelOne[i] = reelOne[i - 1];
			reelOne[0] = null;
		}

		for (int i = 0; i < reelOne.length; i++)
			if (reelOne[i] == null) {
				int random = AvianMath.randomInt(100) + 1;
				if (random <= 32) {
					reelOne[i] = Slot.LAMP;
				} else if (random <= 32 + 29) {
					reelOne[i] = Slot.TELEPHONE;
				} else if (random <= 32 + 29 + 17) {
					reelOne[i] = Slot.BAR;
				} else if (random <= 32 + 29 + 17 + 7) {
					reelOne[i] = Slot.DIAMOND;
				} else if (random <= 32 + 29 + 17 + 7 + 9) {
					reelOne[i] = Slot.GOLD;
				} else if (random <= 32 + 29 + 17 + 7 + 9 + 6) {
					reelOne[i] = Slot.CANDY;
				}
			}

		if (reelOneVel == 0f) {
			reelOnePos = AvianMath.glide(reelOnePos, -2 * (180 / reelOne.length) + 90, 5f);
		}

		// Reel Two
		reelTwoVel -= .02;
		if (reelTwoVel < 0f)
			reelTwoVel = 0f;
		reelTwoPos += reelTwoVel;

		if (reelTwoPos > 180f / reelTwo.length) {
			reelTwoPos -= 180f / reelTwo.length;
			for (int i = 4; i > 0; i--)
				reelTwo[i] = reelTwo[i - 1];
			reelTwo[0] = null;
		}

		for (int i = 0; i < reelTwo.length; i++)
			if (reelTwo[i] == null) {
				int random = AvianMath.randomInt(100) + 1;
				if (random <= 16) {
					reelTwo[i] = Slot.LAMP;
				} else if (random <= 16 + 19) {
					reelTwo[i] = Slot.TELEPHONE;
				} else if (random <= 16 + 19 + 9) {
					reelTwo[i] = Slot.BAR;
				} else if (random <= 16 + 19 + 9 + 8) {
					reelTwo[i] = Slot.DIAMOND;
				} else if (random <= 16 + 19 + 9 + 8 + 25) {
					reelTwo[i] = Slot.GOLD;
				} else if (random <= 16 + 19 + 9 + 8 + 25 + 23) {
					reelTwo[i] = Slot.CANDY;
				}
			}

		if (reelTwoVel == 0f) {
			reelTwoPos = AvianMath.glide(reelTwoPos, -2 * (180 / reelTwo.length) + 90, 5f);
		}

		// Reel Three
		reelThrVel -= .02;
		if (reelThrVel < 0f)
			reelThrVel = 0f;
		reelThrPos += reelThrVel;

		if (reelThrPos > 180f / reelThr.length) {
			reelThrPos -= 180f / reelThr.length;
			for (int i = 4; i > 0; i--)
				reelThr[i] = reelThr[i - 1];
			reelThr[0] = null;
		}

		for (int i = 0; i < reelThr.length; i++)
			if (reelThr[i] == null) {
				int random = AvianMath.randomInt(100) + 1;
				if (random <= 29) {
					reelThr[i] = Slot.LAMP;
				} else if (random <= 29 + 12) {
					reelThr[i] = Slot.TELEPHONE;
				} else if (random <= 29 + 12 + 9) {
					reelThr[i] = Slot.BAR;
				} else if (random <= 29 + 12 + 9 + 8) {
					reelThr[i] = Slot.DIAMOND;
				} else if (random <= 29 + 12 + 9 + 8 + 42) {
					reelThr[i] = Slot.GOLD;
				}
			}

		if (reelThrVel == 0f) {
			reelThrPos = AvianMath.glide(reelThrPos, -2 * (180 / reelThr.length) + 90, 5f);
		}

		if (reelOneVel == 0f && reelTwoVel == 0f && reelThrVel == 0f && spinning) {
			if (bet >= 1)
				payMoney(reelOne[2], reelTwo[2], reelThr[2]);
			if (bet >= 2)
				payMoney(reelOne[1], reelTwo[1], reelThr[1]);
			if (bet >= 3)
				payMoney(reelOne[3], reelTwo[3], reelThr[3]);
			if (bet >= 4)
				payMoney(reelOne[1], reelTwo[2], reelThr[3]);
			if (bet >= 5)
				payMoney(reelOne[3], reelTwo[2], reelThr[1]);
			spinning = false;
			bet = 0;
		}
	}

	float errorAlpha;

	public void gameRender() {

		Fonts.Vegur_ExtraSmall.drawString(spinning ? "Spinning..." : "Use the right mouse button to bet coins.", (width / 2f), 40, AvianColor.white(errorAlpha), AvianFont.ALIGN_DEFAULT);
		if (GameScreen.money > 0 || GameScreen.payout > 0 || spinning)
			Fonts.DroidSansMonoSmall.drawString(spinning ? bet + "" : bet + "?", (width / 2f), 20, AvianColor.white(spinning ? 100 : 255), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
		else
			Fonts.DroidSansMonoSmall.drawString("No money :(", (width / 2f), 20, AvianColor.white(100), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

		float wheelSize = 200f;
		float iconSize = 24f;

		for (int i = 0; i < reelOne.length; i++) {
			float input = reelOnePos + i * (180f / reelOne.length);
			input %= 360;
			if (input < 0 || input > 180)
				continue;
			float y = (((AvianMath.cos(input) + 1f) / 2f) * wheelSize);
			float h = AvianMath.sin(input) * iconSize;
			float alpha = AvianMath.sin(input) * 255f;
			if (reelOne[i] != null)
				switch (reelOne[i]) {
					case BAR:
						Images.slots.crop(72, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case CANDY:
						Images.slots.crop(0, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case DIAMOND:
						Images.slots.crop(120, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case GOLD:
						Images.slots.crop(96, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case LAMP:
						Images.slots.crop(iconSize, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case TELEPHONE:
						Images.slots.crop(48, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					default:
						break;
				}
		}
		for (int i = 0; i < reelTwo.length; i++) {
			float input = reelTwoPos + i * (180f / reelTwo.length);
			input %= 360;
			if (input < 0 || input > 180)
				continue;
			float y = (((AvianMath.cos(input) + 1f) / 2f) * wheelSize);
			float h = AvianMath.sin(input) * iconSize;
			float alpha = AvianMath.sin(input) * 255f;
			if (reelTwo[i] != null)
				switch (reelTwo[i]) {
					case BAR:
						Images.slots.crop(72, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case CANDY:
						Images.slots.crop(0, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case DIAMOND:
						Images.slots.crop(120, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case GOLD:
						Images.slots.crop(96, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case LAMP:
						Images.slots.crop(iconSize, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case TELEPHONE:
						Images.slots.crop(48, 0, iconSize, iconSize);
						Images.slots.render(width / 2 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					default:
						break;
				}
		}
		for (int i = 0; i < reelThr.length; i++) {
			float input = reelThrPos + i * (180f / reelThr.length);
			input %= 360;
			if (input < 0 || input > 180)
				continue;
			float y = (((AvianMath.cos(input) + 1f) / 2f) * wheelSize);
			float h = AvianMath.sin(input) * iconSize;
			float alpha = AvianMath.sin(input) * 255f;
			if (reelThr[i] != null)
				switch (reelThr[i]) {
					case BAR:
						Images.slots.crop(72, 0, iconSize, iconSize);
						Images.slots.render(width / 2 + 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case CANDY:
						Images.slots.crop(0, 0, iconSize, iconSize);
						Images.slots.render(width / 2 + 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case DIAMOND:
						Images.slots.crop(120, 0, iconSize, iconSize);
						Images.slots.render(width / 2 + 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case GOLD:
						Images.slots.crop(96, 0, iconSize, iconSize);
						Images.slots.render(width / 2 + 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case LAMP:
						Images.slots.crop(iconSize, 0, iconSize, iconSize);
						Images.slots.render(width / 2 + 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					case TELEPHONE:
						Images.slots.crop(48, 0, iconSize, iconSize);
						Images.slots.render(width / 2 + 100 - iconSize / 2, wheelSize - y + 50, iconSize, h, GameScreen.money == 0 && GameScreen.payout == 0 && !spinning ? alpha / 2f : alpha);
						break;
					default:
						break;
				}
		}
	}

	@Override
	public void moneyDropped(int droppedMoney) {
		errorAlpha = 255;
	}

	@Override
	public long getMoneyValue() {
		return 0;
	}

	@Override
	public void cashIn() {

	}

}
