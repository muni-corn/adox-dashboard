
package musicaflight.dashboard.games;

import java.text.DecimalFormat;

import musicaflight.avianutils.*;
import musicaflight.dashboard.*;
import musicaflight.dashboard.games.Cards.*;
import musicaflight.dashboard.games.Chips.ChipPile;
import musicaflight.dashboard.games.Chips.ChipStacks;

public class Blackjack extends CasinoGame {

	public Blackjack() {
		super(false);
		deck.shuffle();
	}

	Deck deck = new Deck();

	ChipStacks stacks = new ChipStacks();
	ChipPile table = new ChipPile();

	AvianRectangle r = new AvianRectangle();

	Chips.Dealer d = new Chips.Dealer();

	static enum Shift {
		BET,
		DEAL,
		PLAYER,
		DEALER,
		END_OF_ROUND;
	}

	Shift s = Shift.BET;

	Pile dealer = new Pile() {
		@Override
		public void render() {
			for (int i = 0; i < getCards().size(); i++) {
				getCards().get(i).setX((width / 2 - Images.cards.getCroppedWidth() / 2f) + (i * 25) - ((getCards().size() - 1) * 25) / 2);
				getCards().get(i).setY(height * (3f / 16f) - Images.cards.getCroppedHeight() / 2f);
				getCards().get(i).render();

			}
		}
	};
	Pile player = new Pile() {
		@Override
		public void render() {
			for (int i = 0; i < getCards().size(); i++) {
				getCards().get(i).setX((width / 2 - Images.cards.getCroppedWidth() / 2f) + (i * 25) - ((getCards().size() - 1) * 25) / 2);
				getCards().get(i).setY(height * (13f / 16f) - Images.cards.getCroppedHeight() / 2f);
				getCards().get(i).render();
			}
		}
	};
	Pile discard = new Pile() {
		@Override
		public void render() {
			for (int i = 0; i < getCards().size(); i++) {
				getCards().get(i).setFaceUp(false);
				getCards().get(i).setX(width + 5);
				getCards().get(i).setY(height / 2f - Images.cards.getHeight() / 10f);
				getCards().get(i).render();
			}
		}
	};

	@Override
	protected void gameKeyboard() {

	}

	boolean click;

	boolean dealHover;
	boolean hitHover;
	boolean standHover;

	@Override
	protected void gameMouse() {
		dealHover = table.getTotalValue() > 0 && mx > width / 4 && mx < width * (3f / 4f) && my > height * ((3f / 16f) - (3f / 32f)) && my < height * ((3f / 8f) - (3f / 32f));
		hitHover = mx > width / 16f && mx < width * (5f / 16f) && my > height * (13f / 32f) && my < height * (19f / 32f);
		standHover = mx > width * (11f / 16f) && my > height * (13f / 32f) && mx < width * (15f / 16f) && my < height * (19f / 32f);
		if (AvianInput.isMouseButtonDown(0)) {
			if (!click) {
				switch (s) {
					case BET:
						if (stacks.click(mx, my)) {
							table.add(stacks.getClickedChip(mx, my));
						}
						if (dealHover)
							s = Shift.DEAL;
						break;
					case DEALER:
						break;
					case END_OF_ROUND:
						if (standHover) {
							s = Shift.BET;
							discard.add(false, dealer.removeAll());
							discard.add(false, player.removeAll());
						}
						break;
					case PLAYER:
						if (hitHover)
							hit();
						if (standHover)
							s = Shift.DEALER;
						break;
					case DEAL:
					default:
						break;

				}
			}
			click = true;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			click = false;
		}
		if (AvianInput.isMouseButtonDown(1) && s == Shift.BET) {
			stacks.add(table.removeAll());
		}

	}

	float dealerNumberY, playerNumberY;

	float betAlpha;

	@Override
	protected void gameLogic() {
		if (s != Shift.BET) {
			playerNumberY = AvianMath.glide(playerNumberY, height * (13f / 16f), 10f);
			stacks.hide();
		} else {
			playerNumberY = AvianMath.glide(playerNumberY, height + 10, 10f);
			dealerNumberY = AvianMath.glide(dealerNumberY, -10, 10f);
			stacks.show();
		}

		if (table.getTotalValue() > 0 && s == Shift.BET)
			betAlpha = AvianMath.glide(betAlpha, 255, 10f);
		else
			betAlpha = AvianMath.glide(betAlpha, 0, 10f);

		switch (s) {
			case BET:

				break;
			case DEAL:
				if (dealer.getCards().size() < 2) {
					if (dealer.getCards().size() == player.getCards().size()) {
						dealCard(player, true);
					} else if (player.getCards().size() > dealer.getCards().size()) {
						dealCard(dealer, dealer.getCards().size() == 1);
					}
				} else if (getHandValue(player) == 21) {
					dealer.getCards().get(0).setFaceUp(true);
					if (getHandValue(dealer) != 21) {
						table.add((long) (table.getTotalValue() * 1.5f), false);
					}
					s = Shift.END_OF_ROUND;
				} else if (getHandValue(dealer) == 21) {
					dealer.getCards().get(0).setFaceUp(true);
					d.takeChips(table.removeAll());
					s = Shift.END_OF_ROUND;
				} else {
					s = Shift.PLAYER;
				}
				break;
			case DEALER:
				dealerNumberY = AvianMath.glide(dealerNumberY, height * (3f / 16f), 10f);
				dealer.getCards().get(0).setFaceUp(true);
				if (getHandValue(dealer) < 17) {
					dealCard(dealer, true);
				} else {
					s = Shift.END_OF_ROUND;
					if (getHandValue(player) > 21 || (getHandValue(dealer) > getHandValue(player) && getHandValue(dealer) <= 21)) {
						d.takeChips(table.removeAll());
					} else if (getHandValue(dealer) > 21 || getHandValue(player) > getHandValue(dealer)) {
						table.add(table.getTotalValue(), false);
					}
				}
				break;
			case END_OF_ROUND:
				dealerNumberY = AvianMath.glide(dealerNumberY, height * (3f / 16f), 10f);
				break;
			case PLAYER:
				break;

		}

		deck.setX(width / 2);
		deck.setY(-100);
		deck.logic();
		player.logic();
		dealer.logic();
		discard.logic();
		table.logic();
		stacks.logic();
		d.logic();
		if (stacks.cashInRequested()) {
			GameScreen.addMoney(stacks.cashIn());
		}
	}

	static AvianColor color = new AvianColor(255, 255, 255);

	static DecimalFormat df = new DecimalFormat("#,##0");

	@SuppressWarnings("deprecation")
	@Override
	protected void gameRender() {
		AvianFont f = Fonts.Vegur_Small;

		deck.render();
		dealer.render();

		table.render();
		discard.render();
		stacks.render();
		player.render();

		d.render();

		switch (s) {
			case BET:
				r.set(width / 4, height * (3f / 32f), width / 2, height * (3f / 16f));
				r.render(AvianColor.white(dealHover ? 50 : 25));
				f.drawString("Deal", width / 2f, (height * (3f / 16f)), AvianColor.white(table.getTotalValue() > 0 ? 255 : (255f / 2f)), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
				break;
			case DEALER:
				break;
			case END_OF_ROUND:
				r.set(width * (11f / 16f), height * (13f / 32f), width / 4f, height * (3f / 16f));
				r.render(AvianColor.white(standHover ? 50 : 25));
				f.drawString("Next", (width * (13f / 16f)), (height / 2f), AvianColor.white(255), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
				break;
			case PLAYER:
				r.set(width / 16f, height * (13f / 32f), width / 4f, height * (3f / 16f));
				r.render(AvianColor.white(hitHover ? 50 : 25));
				f.drawString("Hit", (width * (3f / 16f)), (height / 2f), AvianColor.white(255), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
				r.set(width * (11f / 16f), height * (13f / 32f), width / 4f, height * (3f / 16f));
				r.render(AvianColor.white(standHover ? 50 : 25));
				f.drawString("Stand", (width * (13f / 16f)), (height / 2f), AvianColor.white(255), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
				break;
			case DEAL:
				break;
			default:
				break;

		}

		f.drawString(String.valueOf(table.getTotalValue()), (width / 2 - width / 8 - 20), (height / 2), AvianColor.white(betAlpha), AvianFont.ALIGN_RIGHT, AvianFont.ALIGN_CENTER);

		if (s != Shift.BET) {
			if (getHandValue(dealer) == 21) {
				color.setRGBA(0, 176, 255, 255);
			} else if (getHandValue(dealer) > 21) {
				color.setRGBA(255, 50, 50, 255);
			} else {
				color.setRGBA(255, 255, 255, 255);
			}
			f.drawString(String.valueOf(getHandValue(dealer)), ((dealer.getCards().size() > 0) ? ((dealer.getCards().get(dealer.getCards().size() - 1).getX()) + 75) : ((width / 2))), (dealerNumberY), color, AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);

			if (getHandValue(player) == 21) {
				color.setRGBA(0, 176, 255, 255);
			} else if (getHandValue(player) > 21) {
				color.setRGBA(255, 50, 50, 255);
			} else {
				color.setRGBA(255, 255, 255, 255);
			}
			f.drawString(String.valueOf(getHandValue(player)), ((player.getCards().size() > 0) ? ((player.getCards().get(player.getCards().size() - 1).getX()) + 75) : ((width / 2))), (playerNumberY), color, AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		}
	}

	@Override
	public String getName() {
		return "Blackjack";
	}

	@Override
	public void moneyDropped(int droppedMoney) {
		GameScreen.payMoney(droppedMoney);
		if (s == Shift.BET || s == Shift.END_OF_ROUND) {
			table.add(droppedMoney, true);
		}
	}

	@Override
	public void cashIn() {
		table.removeAll();
		stacks.cashIn();
	}

	@Override
	public long getMoneyValue() {
		return stacks.getTotalValue() + table.getTotalValue();
	}

	private int getHandValue(Pile pile) {
		int value = 0;

		for (int i = 0; i < pile.getCards().size(); i++) {
			Card c = pile.getCards().get(i);
			if (c.getRank() == Rank.ACE) {
				if (11 + value <= 21)
					value += 11;
				else
					value += 1;
				continue;
			}
			int nextValue = c.getRank().getValue();
			if (nextValue > 10) {
				nextValue = 10;
			}
			value += nextValue;
		}

		return value;
	}

	void discardCards() {
		discard.add(false, player.removeAll());
		discard.add(false, dealer.removeAll());
	}

	void dealCard(Pile pile, boolean faceUp) {
		Card c = deck.draw();
		if (c == null) {
			deck.add(discard.removeAll());
			deck.shuffle();
			pile.add(faceUp, deck.draw());
		} else {
			pile.add(faceUp, c);
		}
	}

	void hit() {
		if (s == Shift.PLAYER) {
			dealCard(player, true);
			if (getHandValue(player) >= 21) {
				s = Shift.DEALER;
			}
		}
	}

}
