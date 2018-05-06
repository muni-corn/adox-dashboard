
package musicaflight.dashboard;

import java.io.File;
import java.text.DecimalFormat;

import musicaflight.avianutils.*;

public class MusicPlayer extends Element {

	public static AvianSound music;

	boolean changerHover;

	String musicName;

	static boolean looping = false;

	boolean shuffle = false;

	boolean lpsHover = false;
	float lpsAlpha = 0;
	int lpsSelection;

	static ItemList<String> songFiles = new ItemList<String>(true, true) {

		@Override
		public void reset() {
			music.destroy();
			music = new AvianSound();
			Dashboard.closePanel();
		}

		@Override
		public void selectionRequested(int item) {
			changeSongs(songFiles.get(item));
		}

		@Override
		public void additionRequested(String filepath) {
			for (int i = 0; i < songFiles.size(); i++) {
				String s = songFiles.get(i);
				if (getSongName(s).equals(getSongName(filepath))) {
					changeSongs(s);
					return;
				}
			}

			String[] splitString = filepath.split("\\.");

			if (!splitString[splitString.length - 1].equalsIgnoreCase("wav")) {
				converter = new ThreadUtility("Converting \"" + getSongName(filepath) + "\"...", filepath, "true") {

					boolean changeSong = Boolean.parseBoolean(args[1]);

					@Override
					public void task() {
						args[0] = convertSong(args[0]);
					}

					public void whenFinished() {
						songFiles.add(args[0]);

						if (changeSong)
							changeSongs(args[0]);
						Data.saveData();
					}
				};
				converter.start();
			} else {
				songFiles.add(filepath);
				changeSongs(filepath);
			}

			songFiles.closeFiles();

		}

		@Override
		public void deletionRequested(int item) {
			File f;
			if ((f = new File("C:/Avian/" + System.getProperty("user.name") + "/dashboard/music/" + getSongName(songFiles.get(item)) + ".wav")).exists()) {
				f.delete();
			}
			songFiles.remove(item);
		}

		@Override
		public String itemName(int item) {
			return getSongName(songFiles.get(item));
		}

	};

	public MusicPlayer() {
		super(80, true);
		if (music == null)
			music = new AvianSound();
		songFiles.setAcceptableFiles("wav", "mp3", "m4a");
		songFiles.setNoFileMessage("There is no music in this folder.");
	}

	static ThreadUtility converter;
	private static ThreadUtility songChanger;

	boolean addSong = false;

	public void contentMouse() {

		changerHover = hasFocus && my > 50 && mx > 0 && mx < getWidth() * .4f;
		if (lpsHover = hasFocus && my > 50 && mx > getWidth() * .4f && mx < getWidth() * .8f)
			lpsSelection = (int) (((mx - (getWidth() * .4f)) / (getWidth() * .4f)) * 3f);
		if (lpsSelection < 0) {
			lpsSelection = 0;
		} else if (lpsSelection > 2)
			lpsSelection = 2;
		if (!Dashboard.panel) {
			if (AvianInput.isMouseButtonDown(0)) {
				if (mx > 0 && mx < AvianApp.getWidth() - 40 && my > 0 && my < seek.getH() && music.isInitialized())
					music.setPosition((mx) / ((float) AvianApp.getWidth() - 40) * music.getDuration());
				if (!contentClick) {
					if (changerHover)
						settings();
					if (lpsHover) {
						lpsAlpha = 5;
						switch (lpsSelection) {
							case 0:
								looping = !looping;
								music.setLooping(looping);
								break;
							case 1:
								if (MusicPlayer.music.isPlaying())
									MusicPlayer.music.pause();
								else
									MusicPlayer.music.play();
								break;
							case 2:
								shuffle = !shuffle;
								break;
						}
					}
				}

				contentClick = true;
			} else if (!AvianInput.isMouseButtonDown(0)) {
				contentClick = false;
			}
		}
	}

	@Override
	public void contentKeyboard() {

	}

	public void settingsMouse() {
		songFiles.mouse(mx, my, settingsClick);
		if (AvianInput.isMouseButtonDown(0) && !settingsClick) {
			settingsClick = true;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			settingsClick = false;
		}
	}

	@Override
	public void settingsKeyboard() {
	}

	int seekBarCosIn;

	AvianRectangle seek = new AvianRectangle(0, 200, 0, 50),
			hover = new AvianRectangle(),
			lps = new AvianRectangle(0, 50, getWidth() * 4f / 3, 30);

	float hoverW;

	float count, count1, count2, count3, count4, count5;

	float timePercentage;

	float musicVolume = 1f;

	public void contentLogic() {

		musicVolume = AvianMath.glide(musicVolume, Dashboard.settings ? (1f - ((Settings.milliseconds / 500))) : 1f, 20f);

		music.setVolume(musicVolume);

		lps.set(AvianMath.glide(lps.getX(), (getWidth() * .4f) + lpsSelection * (getWidth() * .4f) / 3f, 5f), 50, getWidth() * (2f / 15f), 30);

		if (lpsHover) {
			if (lpsAlpha < 50)
				lpsAlpha += 3;
		} else {
			if (lpsAlpha > 0)
				lpsAlpha -= 2;
		}

		if (!music.isInitialized()) {
			count++;
			if (count > 100) {
				count -= 100;
				count1 = AvianMath.randomInt(10);
				count2 = AvianMath.randomInt(10);
				count3 = AvianMath.randomInt(10);
				count4 = AvianMath.randomInt(10);
				count5 = AvianMath.randomInt(10);
			}
		} else {
			timePercentage = music.getPosition() / music.getDuration();
		}

		seek.setW(AvianMath.glide(seek.getW(), (AvianApp.getWidth() - 40f) * (timePercentage), 5f));

		if (music.isPaused() || music.isStopped()) {
			seekBarCosIn++;
			seekBarCosIn %= 360;
		} else {
			if (seekBarCosIn > 180)
				seekBarCosIn -= 360;

			if (seekBarCosIn <= 3 && seekBarCosIn >= -3)
				seekBarCosIn = 0;
			else
				seekBarCosIn = seekBarCosIn < 0 ? seekBarCosIn + 3 : (seekBarCosIn > 0 ? seekBarCosIn - 3 : 0);
		}

		if (changerHover)
			hoverW = AvianMath.glide(hoverW, 255f / 5f, 10f);
		else
			hoverW = AvianMath.glide(hoverW, 0, 10f);
	}

	public void settingsLogic() {
		songFiles.logic();
	}

	DecimalFormat f2 = new DecimalFormat("00");

	float loopX, imgY, playX, shuffleX;

	public void contentRender() {

		float seekBarCos = AvianMath.cos(seekBarCosIn);

		loopX = (getWidth() * .4f) + ((getWidth() * .4f) * (1f / 6f)) - 8;
		playX = (getWidth() * .4f) + ((getWidth() * .4f) * (3f / 6f)) - 8;
		shuffleX = (getWidth() * .4f) + ((getWidth() * .4f) * (5f / 6f)) - 8;
		imgY = 50 + 15 - 8;

		lps.render(AvianColor.white(lpsAlpha));

		Images.loop.render(loopX, imgY, looping ? 255 : 50);
		Images.shuffle.render(shuffleX, imgY, shuffle ? 255 : 50);

		if (music.isInitialized()) {
			if (music.isPlaying())
				Images.pause.render(playX, imgY);
			else if (music.isInitialized())
				Images.play.render(playX, imgY);
		} else {
			Images.play.render(playX, imgY, 50);
		}
		seek.setH(50);
		seek.setY(0);

		if (!music.isInitialized()) {
			seek.setX(count1 * ((AvianApp.getWidth() - 40f) / 10f));
			seek.setW((AvianApp.getWidth() - 40f) / 10f);
			seek.render(AvianColor.white(seekBarCos * 15f + 35f));
			seek.setX(count2 * ((AvianApp.getWidth() - 40f) / 10f));
			seek.setW((AvianApp.getWidth() - 40f) / 10f);
			seek.render(AvianColor.white(seekBarCos * 15f + 35f));
			seek.setX(count3 * ((AvianApp.getWidth() - 40f) / 10f));
			seek.setW((AvianApp.getWidth() - 40f) / 10f);
			seek.render(AvianColor.white(seekBarCos * 15f + 35f));
			seek.setX(count4 * ((AvianApp.getWidth() - 40f) / 10f));
			seek.setW((AvianApp.getWidth() - 40f) / 10f);
			seek.render(AvianColor.white(seekBarCos * 15f + 35f));
			seek.setX(count5 * ((AvianApp.getWidth() - 40f) / 10f));
			seek.setW((AvianApp.getWidth() - 40f) / 10f);
		} else {
			seek.setX(0);
		}

		seek.render(AvianColor.white(seekBarCos * 15f + 35f));

		hover.set(0, 50f, (AvianApp.getWidth() - 40f) * .4f, 30f);
		hover.render(AvianColor.white(hoverW));

		int sTotal = (int) music.getPosition();
		int s = sTotal % 60;
		int m = (sTotal / 60);

		int sTotalLeft = (int) (music.getDuration() - music.getPosition());
		int sLeft = sTotalLeft % 60;
		int mLeft = (sTotalLeft / 60);

		musicName = "Select music...";

		String newName = getSongName();
		if (newName != null)
			musicName = newName;

		if (Fonts.Vegur_Small.getWidth(musicName) > getWidth() * .4f) {
			int foo = musicName.length();
			do {
				musicName = musicName.substring(0, foo - 1) + "...";
				foo--;
			} while (Fonts.Vegur_Small.getWidth(musicName) > getWidth() * .4f && foo > 0);
		}

		float timeWidth = Fonts.Vegur_Small.getWidth(m + ":" + f2.format(s));
		if (music.isInitialized()) {
			Fonts.Vegur_Small.drawString(m + ":" + f2.format(s), (int) (timePercentage * getWidth() - (timePercentage * timeWidth)), 33, AvianColor.white(seekBarCos * 100 + 155), AvianFont.ALIGN_LEFT);
			Fonts.Vegur_Small.drawString("-" + mLeft + ":" + f2.format(sLeft), (int) (getWidth()) - 5, 75, AvianColor.white(seekBarCos * 100 + 155), AvianFont.ALIGN_RIGHT);
		}
		Fonts.Vegur_Small.drawString(musicName, 5, 75, AvianColor.white(255 - hoverW * 5), AvianFont.ALIGN_LEFT);
		Fonts.Vegur_Small.drawString("Select music...", 5, 75, AvianColor.white(hoverW * 5), AvianFont.ALIGN_LEFT);

	}

	@Override
	public void settingsRender() {
		songFiles.render();

		if (songChanger != null) {
			songChanger.render();
			if (!songChanger.isWorking()) {
				songChanger = null;
			}
		}
		if (converter != null) {
			converter.render();
			if (!converter.isWorking()) {
				converter = null;
			}
		}
	}

	static void changeSongs(String filepath) {
		songChanger = new ThreadUtility("Changing songs...", filepath) {

			@Override
			public void task() {
				String path = args[0];

				String[] splitString = path.split("\\.");
				if (!splitString[splitString.length - 1].equalsIgnoreCase("wav"))
					path = convertSong(path);

//								music.changeAudioFile(filepath);
				music=new AvianSound(path, looping);
			}

			public void whenFinished() {
				songFiles.closeFiles();
				Dashboard.closePanel();
				Data.saveData();
			}

		};
		songChanger.start();
	}

	static String convertSong(String filepath) {
		File f = new File(filepath);
		String fileName = f.getName();
		String[] fileNameSplit = fileName.split("\\.");
		String fileExtension = "." + fileNameSplit[fileNameSplit.length - 1];

		String name = fileName.substring(0, fileName.length() - fileExtension.length());

		WavConverter.setUpConversion(filepath, "C:/Avian/" + System.getProperty("user.name") + "/dashboard/music/" + name + ".wav");
		filepath = "C:/Avian/" + System.getProperty("user.name") + "/dashboard/music/" + name + ".wav";
		WavConverter.convert();

		return filepath;
	}

	public String getSongName() {
		try {
			String musicFileName = music.getFile().getName();
			String[] musicFileNameSplit = musicFileName.split("\\.");
			String musicFileExtension = "." + musicFileNameSplit[musicFileNameSplit.length - 1];

			return musicFileName.substring(0, musicFileName.length() - musicFileExtension.length());
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getSongName(String filepath) {
		try {
			String fileExtension = getExtension(filepath);
			String fileName = new File(filepath).getName();

			return fileName.substring(0, fileName.length() - fileExtension.length());
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getExtension(String filepath) {
		try {
			String fileName = new File(filepath).getName();
			String[] fileNameSplit = fileName.split("\\.");
			return "." + fileNameSplit[fileNameSplit.length - 1];
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public void settingsOpened() {
	}

	@Override
	public void closeSettings() {
		songFiles.closeFiles();
	}

	@Override
	public void destroy() {
		music.stop();
	}

	public String getName() {
		return "Music Player";
	}

	@Override
	public Element create() {
		return new MusicPlayer();
	}

}
