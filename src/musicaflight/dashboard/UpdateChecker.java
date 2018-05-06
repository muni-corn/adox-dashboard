
package musicaflight.dashboard;

import java.io.*;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import musicaflight.avianutils.*;

public class UpdateChecker {

	private static enum Status {
		CHECKING,
		ERROR,
		AVAILABLE,
		NO_UPDATE,
		DOWNLOADING,
		FINISHED;
	}

	static Status status = Status.CHECKING;
	static AvianFileDownloader textChecker = new AvianFileDownloader("https://www.dropbox.com/s/4sbv30xag039r1k/adoxVersion.txt?dl=1");
	static AvianFileDownloader updateDownloader;
	static AvianFileDownloader installerDownloader = new AvianFileDownloader("C:\\Avian\\tmp\\install.jar", "https://www.dropbox.com/s/kokn0vdwxq3v7v5/Installer.jar?dl=1");

	public static Thread checkThread;
	public static Thread updateThread;
	public static Thread installThread;

	private static String error;

	static float x = AvianApp.getWidth();
	static float y = -25f;

	public UpdateChecker() {
	}

	public static boolean available() {
		return status == Status.AVAILABLE;
	}

	public static boolean downloading() {
		return status == Status.DOWNLOADING;
	}

	public static boolean checking() {
		return status.equals(Status.CHECKING);
	}

	public static boolean upToDate() {
		return status == Status.NO_UPDATE;
	}

	public static void checkForUpdate() {
		if (status != Status.DOWNLOADING && status != Status.AVAILABLE)
			status = Status.CHECKING;
	}

	public static void downloadUpdate() {
		if (status != Status.DOWNLOADING) {
			status = Status.DOWNLOADING;
			updateThread.start();
			installThread.start();
		}
	}

	public static String getRunningJAR() {
		try {
			return new File(UpdateChecker.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	public static String getRunningJARFolder() {
		try {
			return new File(UpdateChecker.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getCanonicalPath();
		} catch (IOException | URISyntaxException e) {
			return null;
		}
	}

	static boolean getURL = true;
	static float seconds;

	public static boolean isWorking() {
		return status == Status.AVAILABLE || status == Status.CHECKING || status == Status.DOWNLOADING;
	}

	static float progressX;

	public static String getError() {
		if (textChecker.getException() == null)
			return error;
		String e = textChecker.getException().toString();
		return error = e.substring(0, e.indexOf(":"));
	}

	public static void logic() {
		if (status == Status.AVAILABLE && !Dashboard.panel) {
			y = (AvianMath.glide(y, 0, 15f));
			x--;
		} else {
			y = (AvianMath.glide(y, -25f, 15f));
			if (y <= -24) {
				x = AvianApp.getWidth();
			}
		}
		seconds += 0.01f;
		if (updateDownloader != null && updateDownloader.isFinished())
			status = Status.FINISHED;
		switch (status) {
			case CHECKING:
				progressX++;
				if (progressX > AvianApp.getWidth())
					progressX = -AvianApp.getWidth() / 4;
				if (checkThread == null) {
					seconds = 0;
					error = null;
					textChecker.initialize();
					checkThread = new Thread(textChecker, "Avian Update Checker");
					checkThread.start();
				}
				if (seconds >= 30f) {
					status = Status.ERROR;
					error = "Request timed out.";
				}
				if (textChecker.isFinished()) {
					if (textChecker.getFileLines() != null) {
						if (!(textChecker.getFileLines()[0].equals(AvianApp.getVersion()))) {
							versionAvailable = textChecker.getFileLines()[0];
							status = Status.AVAILABLE;
						} else
							status = Status.NO_UPDATE;
					} else {
						status = Status.ERROR;
						error = textChecker.getException().toString();
					}
				}
				break;
			case AVAILABLE:
				error = null;

				if (getURL) {
					updateDownloader = new AvianFileDownloader("C:\\Avian\\tmp\\AvianUpdate.jar", "https://www.dropbox.com/s/jetuoljmzs6rld6/AvianUpdate.jar?dl=1", false);
					updateThread = new Thread(updateDownloader, "Avian Update Downloader");
					installThread = new Thread(installerDownloader, "Avian Installer Downloader");
					getURL = false;
				}
				break;

			case DOWNLOADING:
				error = null;

				break;
			case FINISHED:
				error = null;

				try {
					BufferedWriter w = new BufferedWriter(new FileWriter("C:\\Avian\\tmp\\jarfile.updateconfig"));
					w.write(getRunningJAR());
					w.close();

					Runtime.getRuntime().exec("java -jar \"" + "C:/Avian/tmp/install.jar" + "\"");
					AvianApp.close();
				} catch (IOException e) {
					error = e.toString();
					status = Status.ERROR;
				}
				break;
			case NO_UPDATE:
				if (checkThread != null)
					checkThread = null;
				if (updateThread != null)
					updateThread = null;
				if (installThread != null)
					installThread = null;
				break;
			case ERROR:
				if (checkThread != null)
					checkThread = null;
				if (updateThread != null)
					updateThread = null;
				if (installThread != null)
					installThread = null;
				break;
			default:
				break;
		}
	}

	static String versionAvailable;

	public static String getAvailableVersion() {
		return versionAvailable;
	}

	@SuppressWarnings("deprecation")
	public static void render() {
		if (status != Status.AVAILABLE)
			return;
		float width = Fonts.Vegur_ExtraSmall.getWidth("Version \"" + getAvailableVersion() + "\" is available. To download and install, go to Settings and click \"Check for update\".");
		if (AvianApp.getWidth() < width) {
			Fonts.Vegur_ExtraSmall.drawString("Version \"" + getAvailableVersion() + "\" is available. To download and install, go to Settings and click \"Check for update\".", x, y + 20, AvianColor.white(Dashboard.headerTextAlpha * 255f), AvianFont.ALIGN_LEFT);
			if (x < -width)
				x = AvianApp.getWidth();
		} else {
			Fonts.Vegur_ExtraSmall.drawString("Version \"" + getAvailableVersion() + "\" is available. To download and install, go to Settings and click \"Check for update\".", y + 20, AvianColor.white(Dashboard.headerTextAlpha * 255f), AvianFont.ALIGN_BOTTOM);
		}
	}

	static DecimalFormat df = new DecimalFormat("0.#");
	static DecimalFormat df2 = new DecimalFormat("0");

	public static String getRemainingTime() {
		if (updateDownloader.isSaving() && installerDownloader.isSaving()) {
			long bytesRemaining = updateDownloader.getOnlineFileSize() - updateDownloader.getBytesDownloaded();
			bytesRemaining += installerDownloader.getOnlineFileSize() - installerDownloader.getBytesDownloaded();

			double speed = updateDownloader.getBytesDownloaded() / (System.currentTimeMillis() - updateDownloader.getDownloadBeginTime());
			speed += installerDownloader.getBytesDownloaded() / (System.currentTimeMillis() - installerDownloader.getDownloadBeginTime());
			int millis = (int) (bytesRemaining / speed);

			if (millis >= 60 * 60 * 1000) {
				String time = df.format(millis / (60.0 * 60.0 * 1000.0));
				return time + (time.equals("1") ? " hour" : " hours") + " remaining";
			} else if (millis >= 60 * 1000) {
				String time = df.format(millis / (60.0 * 1000.0));
				return time + (time.equals("1") ? " minute" : " minutes") + " remaining";
			} else {
				String time = df2.format(millis / (1000.0));
				return time + (time.equals("1") ? " second" : " seconds") + " remaining";
			}
		}
		return "Downloading...";
	}

	public static float getProgress() {
		return ((float) updateDownloader.getBytesDownloaded() + (float) installerDownloader.getBytesDownloaded()) / ((float) updateDownloader.getOnlineFileSize() + (float) installerDownloader.getOnlineFileSize());
	}
}
