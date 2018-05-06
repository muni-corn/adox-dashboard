
package musicaflight.dashboard.wallpaper;

public abstract class WallpaperEffect {

	boolean usesBackground;

	public WallpaperEffect(boolean usesBackground) {
		this.usesBackground = usesBackground;
	}

	public abstract void logic();

	public abstract void render();
	
	public abstract String getName();
}
