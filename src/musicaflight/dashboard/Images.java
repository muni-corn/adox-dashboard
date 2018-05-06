
package musicaflight.dashboard;

import musicaflight.avianutils.AvianImage;
import musicaflight.avianutils.ImageBank;

public class Images implements ImageBank {

	public static AvianImage slots, coin, cards, chips, exchange, cashin;

	public static AvianImage shadow, shadowLeftCorner, shadowRightCorner, gear,
			clock, maximize, restore, X, plus, folder, file, uponelevel, pg,
			wallpaperIcon, loop, shuffle, play, pause, scrub, unchecked_box,
			checked_box, swap;

	@Override
	public void initImages() {
		shadow = new AvianImage("/res/photos/shadow.png");
		shadowLeftCorner = new AvianImage("/res/photos/shadowCorner.png");
		shadowRightCorner = new AvianImage("/res/photos/shadowCornerFlip.png");
		gear = new AvianImage("/res/photos/gear.png");
		clock = new AvianImage("/res/photos/clock.png");
		maximize = new AvianImage("/res/photos/maximize.png");
		restore = new AvianImage("/res/photos/restore.png");
		X = new AvianImage("/res/photos/x.png");
		plus = new AvianImage("/res/photos/plus.png");
		folder = new AvianImage("/res/photos/folder.png");
		file = new AvianImage("/res/photos/file.png");
		uponelevel = new AvianImage("/res/photos/uponelevel.png");
		pg = new AvianImage("/res/photos/parallelogram.png");
		wallpaperIcon = new AvianImage("/res/photos/wallpaper.png");
		loop = new AvianImage("/res/photos/loop.png");
		shuffle = new AvianImage("/res/photos/shuffle.png");
		play = new AvianImage("/res/photos/play.png");
		pause = new AvianImage("/res/photos/pause.png");
		scrub = new AvianImage("/res/photos/verticalScrub.png");
		unchecked_box = new AvianImage("/res/photos/uncheckedbox.png");
		checked_box = new AvianImage("/res/photos/checkedbox.png");
		swap = new AvianImage("/res/photos/swap.png");

		coin = new AvianImage("/res/photos/coin.png");
		slots = new AvianImage("/res/photos/slots.png");
		cards = new AvianImage("/res/photos/cards.png");
		chips = new AvianImage("/res/photos/chips.png");
		exchange = new AvianImage("/res/photos/exchange.png");
		cashin = new AvianImage("/res/photos/cashIn.png");
	}
}
