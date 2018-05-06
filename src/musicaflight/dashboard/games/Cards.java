
package musicaflight.dashboard.games;

import java.util.ArrayList;
import java.util.Collections;

import musicaflight.avianutils.AvianMath;
import musicaflight.dashboard.Images;

public class Cards {

	protected static enum Suit {
		HEARTS,
		DIAMONDS,
		CLUBS,
		SPADES;

		public static Suit fromInt(int i) {
			return Suit.values()[i];
		}
	}

	protected static enum Rank {
		ACE(
				1),
		TWO(
				2),
		THREE(
				3),
		FOUR(
				4),
		FIVE(
				5),
		SIX(
				6),
		SEVEN(
				7),
		EIGHT(
				8),
		NINE(
				9),
		TEN(
				10),
		JACK(
				11),
		QUEEN(
				12),
		KING(
				13);

		int value;

		Rank(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Rank fromInt(int i) {
			return Rank.values()[i];
		}
	}

	public static class Deck {

		ArrayList<Card> cards = new ArrayList<Card>();

		public Deck() {
			for (int i = 0; i < Suit.values().length; i++) {
				for (int j = 0; j < Rank.values().length; j++) {
					cards.add(new Card(Rank.fromInt(j), Suit.fromInt(i), false));
				}
			}
		}

		public Card draw() {
			if (cards.size() > 0)
				return cards.remove(0);
			return null;
		}

		public void shuffle() {
			Collections.shuffle(cards);
		}

		public Card[] remove(Suit suit) {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			for (int i = 0; i < cards.size(); i++)
				if (cards.get(i).getSuit() == suit)
					removedCards.add(cards.remove(i));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public Card[] remove(Rank rank) {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			for (int i = 0; i < cards.size(); i++)
				if (cards.get(i).getRank() == rank)
					removedCards.add(cards.remove(i));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public Card[] remove(Suit suit, Rank rank) {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			for (int i = 0; i < cards.size(); i++)
				if (cards.get(i).getSuit() == suit && cards.get(i).getRank() == rank)
					removedCards.add(cards.remove(i));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public void add(Card... c) {
			for (int i = 0; i < c.length; i++) {
				c[i].setFaceUp(false);
				this.cards.add(c[i]);
			}
		}

		public void setX(float x) {
			this.x = x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public void logic() {
			for (int i = 0; i < cards.size(); i++) {
				cards.get(i).logic();
			}
		}

		float x, y;

		public void render() {
			for (int i = cards.size() - 1; i >= 0; i--) {
				cards.get(i).setX(x);
				cards.get(i).setY(y - (cards.size() - i) / 10f);
				cards.get(i).render();
			}
		}
	}

	public static abstract class Pile {

		private ArrayList<Card> cards = new ArrayList<Card>();

		public Pile() {
		}

		public void add(boolean faceUp, Card... c) {
			if (c == null)
				return;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == null)
					continue;
				c[i].setFaceUp(faceUp);
				this.cards.add(c[i]);
			}
		}

		public Card[] remove(Suit suit) {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			for (int i = 0; i < cards.size(); i++)
				if (cards.get(i).getSuit() == suit)
					removedCards.add(cards.remove(i));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public Card[] remove(Rank rank) {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			for (int i = 0; i < cards.size(); i++)
				if (cards.get(i).getRank() == rank)
					removedCards.add(cards.remove(i));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public Card[] remove(Suit suit, Rank rank) {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			for (int i = 0; i < cards.size(); i++)
				if (cards.get(i).getSuit() == suit && cards.get(i).getRank() == rank)
					removedCards.add(cards.remove(i));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public Card[] removeAll() {
			ArrayList<Card> removedCards = new ArrayList<Card>();

			while (cards.size() > 0)
				removedCards.add(cards.remove(0));

			return removedCards.toArray(new Card[removedCards.size()]);
		}

		public ArrayList<Card> getCards() {
			return cards;
		}

		public void logic() {
			for (int i = 0; i < cards.size(); i++) {
				cards.get(i).logic();
			}
		}

		public abstract void render();

	}

	public static class Card {

		private Suit suit;
		private Rank rank;

		private boolean faceUp;
		private float flip = 0f;

		private float x, y, fx, fy;

		public float getX() {
			return fx;
		}

		public float getY() {
			return fy;
		}

		public void setX(float x) {
			this.fx = x;
		}

		public void setY(float y) {
			this.fy = y;
		}

		public void snapTo(float xx, float yy) {
			this.x = fx = xx;
			this.y = fy = yy;
		}

		public Card(Rank rank, Suit suit) {
			this.suit = suit;
			this.rank = rank;
		}

		public Card(Rank rank, Suit suit, boolean faceUp) {
			this(rank, suit);
			this.faceUp = faceUp;
		}

		public Suit getSuit() {
			return suit;
		}

		public Rank getRank() {
			return rank;
		}

		public void setFaceUp(boolean faceUp) {
			this.faceUp = faceUp;
		}

		public void logic() {
			x = AvianMath.glide(x, fx, 10f);
			y = AvianMath.glide(y, fy, 10f);
			if (faceUp)
				flip = AvianMath.glide(flip, 1f, 15f);
			else
				flip = AvianMath.glide(flip, 0f, 15f);

		}

		public void render() {
			if (flip < .5f) {
				Images.cards.crop(0, Images.cards.getHeight() * (.8f), (Images.cards.getWidth() / 13f), Images.cards.getHeight() / 5f);
			} else {
				float cropY = 0;
				switch (suit) {
					case CLUBS:
						cropY = Images.cards.getHeight() * (2f / 5f);
						break;
					case DIAMONDS:
						cropY = Images.cards.getHeight() * (1f / 5f);
						break;
					case HEARTS:
						cropY = 0;
						break;
					case SPADES:
						cropY = Images.cards.getHeight() * (3f / 5f);
						break;
				}
				Images.cards.crop(Images.cards.getWidth() * ((rank.getValue() - 1) / 13f), cropY, (Images.cards.getWidth() / 13f), Images.cards.getHeight() / 5f);
			}
			Images.cards.render(x + ((Images.cards.getWidth() / 13f) * (1f - Math.abs(flip * 2f - 1f))) / 2f, y, (Images.cards.getWidth() / 13f) * Math.abs(flip * 2f - 1f), Images.cards.getHeight() / 5f);
		}

	}
}
