
package musicaflight.dashboard;

import java.io.File;
import java.io.IOException;

import musicaflight.avianutils.AvianDataFile;
import musicaflight.avianutils.AvianImage;
import musicaflight.dashboard.games.MiniBlockery;
import musicaflight.dashboard.wallpaper.Wallpaper;

public class Data {

	public static File userRoot = new File("C:/Avian/" + System.getProperty("user.name") + "/dashboard/");

	public static void saveData() {
		AvianDataFile adf = new AvianDataFile("C:/Avian/" + System.getProperty("user.name") + "/dashboard/data.dashboard");

		for (int i = 0; i < MusicPlayer.songFiles.size(); i++) {
			adf.setElement("song" + i, MusicPlayer.songFiles.get(i));
		}
		for (int i = 0; i < Dashboard.elements.size(); i++) {
			Element e = Dashboard.elements.get(i);
			adf.setElement("element" + i, e.getName());
			if (Dashboard.elements.get(i) instanceof Countdown) {
				adf.setElement("countdown" + i, ((Countdown) e).eventName + "::" + ((Countdown) e).endDate.getTimeInMillis());
			}
		}

		for (int i = 0; i < Wallpaper.papers.size(); i++) {
			adf.setElement("wallpaper" + i, Wallpaper.names.get(i) + "::" + Wallpaper.papers.get(i).getFile() + "::" + Wallpaper.blurpapers.get(i).getFile());
		}

		adf.setElement("blurWallpaper", Wallpaper.blur);
		adf.setElement("wallpaperSlideshowInterval", Wallpaper.intervalInSeconds);

		int money = 0;
		for (int i = 0; i < Dashboard.elements.size(); i++) {
			Element e = Dashboard.elements.get(i);
			if (e instanceof GameScreen)
				money += ((GameScreen) e).getTableMoney();

		}

		adf.setElement("money", GameScreen.money + GameScreen.payout + money);
		adf.setElement("blockery", MiniBlockery.totalBlocks + "::" + MiniBlockery.bkit.owned() + "::" + MiniBlockery.cott.owned() + "::" + MiniBlockery.fact.owned() + "::" + MiniBlockery.mine.owned() + "::" + MiniBlockery.powh.owned() + "::" + MiniBlockery.mpow.owned());
		adf.setElement("darker", Dashboard.darkerElements);
		adf.setElement("clockShowOnStartup", Clock.showOnStartup);
		adf.setElement("clockShowSeconds", Clock.showSecondHand);
		adf.setElement("clockSmooth", Clock.smoothMovement);
		adf.setElement("clockShowHourMarks", Clock.showHours);
		adf.setElement("clockShowMinuteMarks", Clock.showMinutes);
		adf.flushElements();
	}

	public static void loadData() {
		File saveFile = new File("C:/Avian/" + System.getProperty("user.name") + "/dashboard/data.dashboard");
		if (!saveFile.exists())
			return;

		AvianDataFile adf = null;
		try {
			adf = new AvianDataFile(saveFile.getCanonicalPath());
		} catch (IOException e1) {
			return;
		}

		Dashboard.darkerElements = adf.retrieveElementBoolean("darker");

		Clock.showOnStartup = Dashboard.clock = adf.retrieveElementBoolean("clockShowOnStartup");
		Clock.showSecondHand = adf.retrieveElementBoolean("clockShowSeconds");
		Clock.smoothMovement = adf.retrieveElementBoolean("clockSmooth");
		Clock.showHours = adf.retrieveElementBoolean("clockShowHourMarks");
		Clock.showMinutes = adf.retrieveElementBoolean("clockShowMinuteMarks");

		MusicPlayer.songFiles.clear();
		String nextElement;
		for (int i = 0; (nextElement = adf.retrieveElement("song" + i)) != null; i++) {
			if (new File(nextElement).exists())
				MusicPlayer.songFiles.add(nextElement);
		}
		Dashboard.elements.clear();
		for (int i = 0; (nextElement = adf.retrieveElement("element" + i)) != null; i++) {
			for (int j = 0; j < Dashboard.availableElements.size(); j++) {
				if (Dashboard.availableElements.get(j).getName().equals(nextElement)) {
					Element e = Dashboard.availableElements.get(j).create();
					if (e != null && e instanceof Countdown) {
						String countdown = adf.retrieveElement("countdown" + i);
						if (countdown != null) {
							String[] splits = countdown.split("::");
							if (splits.length > 1) {
								((Countdown) e).eventName = splits[0];
								((Countdown) e).sb = new StringBuilder(((Countdown) e).eventName);
								((Countdown) e).endDate.setTimeInMillis(Long.parseLong(splits[1]));
							}
						}
					}
					if (e != null) {
						if (e.onlyOne) {
							Dashboard.elements.add(Dashboard.availableElements.remove(j));
						} else {
							Dashboard.elements.add(e);
						}
					}
					break;
				}
			}
		}
		GameScreen.money = adf.retrieveElementLong("money");

		String[] blockery = adf.retrieveElement("blockery").split("::");
		MiniBlockery.totalBlocks = Double.parseDouble(blockery[0]);
		MiniBlockery.bkit.owned(Integer.parseInt(blockery[1]));
		MiniBlockery.cott.owned(Integer.parseInt(blockery[2]));
		MiniBlockery.fact.owned(Integer.parseInt(blockery[3]));
		MiniBlockery.mine.owned(Integer.parseInt(blockery[4]));
		MiniBlockery.powh.owned(Integer.parseInt(blockery[5]));
		MiniBlockery.mpow.owned(Integer.parseInt(blockery[6]));

		for (int i = 0; (nextElement = adf.retrieveElement("wallpaper" + i)) != null; i++) {
			String[] split = nextElement.split("::");

			if (new File(split[1]).exists()) {
				Wallpaper.blurpapers.add(new AvianImage(split[2]));
				Wallpaper.papers.add(new AvianImage(split[1]));
				Wallpaper.names.add(split[0]);
				Wallpaper.blur = adf.retrieveElementBoolean("blurWallpaper");
				Wallpaper.intervalInSeconds = (int) (Wallpaper.currentSeconds = adf.retrieveElementInt("wallpaperSlideshowInterval"));
			}
		}

	}
}
