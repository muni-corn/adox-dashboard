
package musicaflight.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.*;

import musicaflight.avianutils.*;

public abstract class ItemList<T> {

	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				if (iterator + 1 < items.size())
					return true;
				return false;
			}

			@Override
			public T next() {
				return items.get(iterator++);
			}
		};
	}

	AvianColor color = new AvianColor(0, 0, 0, 0);

	int iterator = -1;

	LinkedList<T> items = new LinkedList<T>();

	boolean fileList, canReset;
	boolean showFiles;
	boolean delete;
	String[] allowedFiles;
	String noFileMessage = "There are no files in this folder.";

	File folder = new File("C:/");

	public ItemList(boolean fileList, boolean canReset) {
		this.fileList = fileList;
		this.canReset = canReset;
		if (fileList) {
			listFilesInFolder(folder);
		}
	}

	int slotOffset;

	float scrollAlpha;
	float scrollSeconds;

	int slotHighlight;
	float sHY;
	float sHH;

	File[] currentDirectory;

	public abstract void selectionRequested(int item);

	public abstract void additionRequested(String filepath);

	public abstract void deletionRequested(int item);

	public abstract void reset();

	public abstract String itemName(int item);

	public void closeFiles() {
		showFiles = false;
	}

	public void setAcceptableFiles(String... extensions) {
		allowedFiles = extensions;
	}

	public void setNoFileMessage(String message) {
		noFileMessage = message;
	}

	float filesTransition = 0;
	boolean highlightOff = false;
	float highlightAlpha;

	public T get(int index) {
		return items.get(index);
	}

	public boolean add(T t) {
		return items.add(t);
	}

	public T remove(int index) {
		return items.remove(index);
	}

	public void clear() {
		items.clear();
	}

	public int size() {
		return items.size();
	}

	public void mouse(float mx, float my, boolean isMouseClicked) {
		float h = ((AvianApp.getHeight() - 120f) / ((float) items.size() + ((fileList || canReset) ? 3 : 2)));
		slotHighlight = (int) ((my - 120) / h);
		if (slotHighlight < 1 || slotHighlight > items.size() + ((fileList || canReset) ? 1 : 0))
			highlightOff = true;
		else
			highlightOff = false;

		if (slotHighlight > 1 + items.size() + (fileList ? 1 : 0))
			slotHighlight = 1 + items.size() + (fileList ? 1 : 0);
		else if (slotHighlight < 0)
			slotHighlight = 0;

		if (Dashboard.dw != 0) {
			scrollAlpha = 150f;
			scrollSeconds = 0;
		}

		if (showFiles)
			slotOffset += Dashboard.dw / 20;

		if (AvianInput.isMouseButtonDown(0)) {
			if (!isMouseClicked) {
				if (!showFiles) {
					int i = (int) ((my - 120) / h);
					if (i == items.size() + 1) {
						if ((delete && canReset && fileList) || (canReset && !fileList)) {
							reset();
						} else if (fileList && !delete) {
							showFiles = true;
							listFilesInFolder(folder);
						}
					} else if (i > 0) {
						if (i <= items.size())
							if (items.size() > 0) {
								if (delete) {
									deletionRequested(i - 1);
									Data.saveData();
								} else
									selectionRequested(i - 1);
							}
					}
				} else {
					if (my > 155) {
						int i = (int) ((my - 150f) / 30f) - slotOffset;
						try {
							listFilesInFolder(currentDirectory[i]);
						} catch (ArrayIndexOutOfBoundsException e) {

						}
					} else if (my < 155 && my > 120 && folder.getParentFile() != null) {
						listFilesInFolder(folder.getParentFile());
					}
				}
			}
			isMouseClicked = true;
		} else if (!AvianInput.isMouseButtonDown(0)) {
			isMouseClicked = false;
		}
	}

	float gb = 255;

	public void logic() {
		delete = (AvianInput.isKeyDown(AvianInput.KEY_DELETE)) && fileList;

		scrollSeconds += .01f;

		if (highlightOff)
			highlightAlpha = AvianMath.glide(highlightAlpha, 0, 15f);
		else
			highlightAlpha = AvianMath.glide(highlightAlpha, (255f / 5f), 3f);

		if (delete && fileList)
			gb = AvianMath.glide(gb, 50f, 5f);
		else
			gb = AvianMath.glide(gb, 255f, 5f);

		if (scrollSeconds > 1f)
			scrollAlpha -= 5f;

		if (fileList) {
			if ((AvianApp.getHeight() - 150) / 30 > currentDirectory.length)
				slotOffset = 0;
			else {
				if (slotOffset < -currentDirectory.length + (AvianApp.getHeight() - 150) / 30 - 1)
					slotOffset = -currentDirectory.length + (AvianApp.getHeight() - 150) / 30 - 1;
				if (slotOffset > 0)
					slotOffset = 0;

			}
			if (showFiles) {
				if (filesTransition < 90)
					filesTransition += 5;
				if (filesTransition > 90)
					filesTransition = 90;
			} else {
				if (filesTransition > 0)
					filesTransition -= 5;
				if (filesTransition < 0)
					filesTransition = 0;

			}
		}
		float h = ((AvianApp.getHeight() - 120f) / ((float) items.size() + ((fileList || canReset) ? 3 : 2)));
		sHY = AvianMath.glide(sHY, 120 + slotHighlight * h, 3f);
		sHH = h;
	}

	static AvianRectangle scroll = new AvianRectangle(0, 0, 3, 0);
	static AvianRectangle highlight = new AvianRectangle(0, 0, AvianApp.getWidth(), 30);
	static AvianRectangle line = new AvianRectangle(20, 0, AvianApp.getWidth() - 40, 1);

	@SuppressWarnings("deprecation")
	public void render() {

		highlight.set(20, sHY, AvianApp.getWidth() - 40, sHH);
		highlight.render(AvianColor.white(AvianMath.cos(filesTransition) * highlightAlpha));

		for (int i = 1; i < items.size() + ((fileList || canReset) ? 3 : 2); i++) {

			float height = (AvianApp.getHeight() - 120f) / ((float) items.size() + ((fileList || canReset) ? 3 : 2));

			line.setX(20);
			line.setY(120 + height * i);
			line.setW(AvianApp.getWidth() - 40);
			line.render(color.setRGBA(255, gb, gb, (AvianMath.cos(filesTransition) * 255f)));
			if (i == items.size() + 1 && (fileList || canReset)) {
				if (canReset && !fileList)
					Images.X.render(AvianApp.getWidth() / 2f - 8f, 120f + (i * height) + (height / 2f) - 8, new float[] {
							1f, 1f, 1f, AvianMath.cos(filesTransition) });
				else if (fileList && delete)
					Images.X.render(AvianApp.getWidth() / 2f - 8f, 120f + (i * height) + (height / 2f) - 8, new float[] {
							1f, gb, gb, AvianMath.cos(filesTransition) });
				else if (fileList)
					Images.plus.render(AvianApp.getWidth() / 2f - 8f, 120f + (i * height) + (height / 2f) - 8, new float[] {
							1f, gb, gb, AvianMath.cos(filesTransition) });
			} else {
				if (i <= items.size()) {
					Fonts.Vegur_Small.drawString(itemName(i - 1), (AvianApp.getWidth() / 2f - 8f), (120f + (i * height) + (height / 2f)), color.setRGBA(255, gb, gb, (AvianMath.cos(filesTransition) * 255f)), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
				}
			}
		}

		if (fileList) {

			if (folder.getParentFile() != null)
				Images.uponelevel.render(20 + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f) - 8, 150 - 16, (int) (AvianMath.sin(filesTransition) * 255f));
			try {
				Fonts.Vegur_Small.drawString(folder.getCanonicalPath(), (int) (40f + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f)), 150, AvianColor.white(AvianMath.sin(filesTransition) * 255f), AvianFont.ALIGN_LEFT);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (currentDirectory.length == 0) {
				Fonts.Vegur_Small.drawString(noFileMessage, (int) ((AvianApp.getWidth() / 2) + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f)), 155 + ((AvianApp.getHeight() - 155) / 2), AvianColor.white(AvianMath.sin(filesTransition) * 255f), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

			} else
				for (int i = 0; i < currentDirectory.length; i++) {
					int newI = i + slotOffset;

					float alpha = AvianMath.sin(filesTransition) * 255f;

					if (newI >= 0) {
						// alpha -= (4f - newI) * (255f / 5f);
						Fonts.Vegur_Small.drawString(currentDirectory[i].getName(), (int) (40f + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f)), 150 + (newI + 1) * 30, AvianColor.white(alpha), AvianFont.ALIGN_LEFT);
						if (currentDirectory[i].isDirectory())
							Images.folder.render(20 + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f) - 8, 150 + (newI + 1) * 30 - 16, (int) alpha);
						else
							Images.file.render(20 + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f) - 8, 150 + (newI + 1) * 30 - 16, (int) alpha);
					}
				}

			line.setX(0);
			line.setY(155);
			line.setW(AvianApp.getWidth());
			line.render(AvianColor.white(AvianMath.sin(filesTransition) * 150f));

			int barsOnScreen = (int) ((AvianApp.getHeight() - 150f) / 30f) - 1;
			int leftovers = currentDirectory.length - barsOnScreen;
			float scrollBarHeight = AvianApp.getHeight() - 150f;
			for (int i = 0; i < leftovers; i++) {
				scrollBarHeight -= 5;
				if (scrollBarHeight < 2) {
					scrollBarHeight = 2;
					break;
				}
			}

			if ((AvianApp.getHeight() - 150) / 30 < currentDirectory.length) {
				scroll.set(AvianApp.getWidth() - 5 + (1f - AvianMath.sin(filesTransition)) * (AvianApp.getWidth() / 8f), 159 + ((AvianApp.getHeight() - 162f - scrollBarHeight) * (-slotOffset) / ((float) currentDirectory.length - (float) barsOnScreen)), 2, scrollBarHeight);
				scroll.render(AvianColor.white(scrollAlpha));
			}
		}

	}

	public String getExtension(String filepath) {
		try {
			String fileName = new File(filepath).getName();
			String[] fileNameSplit = fileName.split("\\.");
			return "." + fileNameSplit[fileNameSplit.length - 1];
		} catch (NullPointerException e) {
			return null;
		}
	}

	public File[] listFilesInFolder(File directory) {
		if (!directory.isDirectory())
			try {
				if (allowedFiles != null) {
					for (int i = 0; i < allowedFiles.length; i++) {
						String s = allowedFiles[i];
						if (getExtension(directory.getCanonicalPath()).equalsIgnoreCase("." + s)) {
							additionRequested(directory.getCanonicalPath());
							return currentDirectory;
						}
					}
				} else {
					additionRequested(directory.getCanonicalPath());
					return currentDirectory;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		scrollAlpha = 150f;
		scrollSeconds = 0;

		Comparator<Object> comp = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				File f1 = (File) o1;
				File f2 = (File) o2;
				if (f1.isDirectory() && !f2.isDirectory())
					return -1;
				else if (!f1.isDirectory() && f2.isDirectory())
					return 1;
				return f1.compareTo(f2);
			}
		};
		File[] files = directory.listFiles();

		LinkedList<File> acceptedFiles = new LinkedList<File>();

		if (files != null) {
			try {
				if (allowedFiles != null) {
					for (int i = 0; i < files.length; i++) {
						File f = files[i];
						fileloop: for (int j = 0; j < allowedFiles.length; j++) {
							String s = allowedFiles[j];
							if (f.isDirectory() || getExtension(f.getCanonicalPath()).equalsIgnoreCase("." + s)) {
								acceptedFiles.add(f);
								break fileloop;
							}
						}
					}
				} else {
					Arrays.sort(files, comp);

					folder = directory;

					slotOffset = 0;

					return currentDirectory = files;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		files = acceptedFiles.toArray(new File[acceptedFiles.size()]);
		Arrays.sort(files, comp);

		folder = directory;

		slotOffset = 0;

		return currentDirectory = files;
	}

}
