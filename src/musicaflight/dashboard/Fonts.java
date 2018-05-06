
package musicaflight.dashboard;

import java.awt.Font;

import musicaflight.avianutils.AvianFont;

public class Fonts implements musicaflight.avianutils.FontBank {

	public static AvianFont Vegur, Vegur_Medium, Vegur_Small, Vegur_ExtraSmall,
			Vegur_Small_BOLD, DroidSansMono, DroidSansMonoSmall, SFDR;

	@Override
	public void initFonts() {
		Vegur = new AvianFont("/res/fonts/Vegur-L 0602.otf", 75);
		Vegur_Medium = new AvianFont("/res/fonts/Vegur-L 0602.otf", 40);
		Vegur_ExtraSmall = new AvianFont("/res/fonts/Vegur-L 0602.otf", 20);
		Vegur_Small = new AvianFont("/res/fonts/Vegur-L 0602.otf", 25);
		Vegur_Small_BOLD = new AvianFont("/res/fonts/Vegur-L 0602.otf", 25, Font.BOLD);
		DroidSansMono = new AvianFont("/res/fonts/DROIDSANSMONO.TTF", 20);
		DroidSansMonoSmall = new AvianFont("/res/fonts/DROIDSANSMONO.TTF", 15);
		SFDR = new AvianFont("/res/fonts/SF Digital Readout Heavy.ttf", 30);
	}

}
