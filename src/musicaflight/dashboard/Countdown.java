
package musicaflight.dashboard;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import musicaflight.avianutils.*;

public class Countdown extends Element {

	public Countdown() {
		super(175, false);
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {
				
			}

			@Override
			public void release(int button, float x, float y) {
				
			}

			@Override
			public void move(float x, float y) {
				
			}

			@Override
			public void scroll(int count) {
				if(!settings)return;
				int selection = (int) (((my - 120f) / (AvianApp.getHeight() - 120f)) * 6f);
				switch (selection) {
					case 1:
						monthOff += count;
						break;
					case 2:
						dayOff += count;
						break;
					case 3:
						yearNum -= count;
						break;
				}
				
			}
			
		});
	}

	Calendar endDate = new GregorianCalendar();
	String eventName;

	@Override
	public void contentKeyboard() {

	}

	@Override
	public void contentMouse() {

	}

	float spacing = 5f;

	@Override
	public void contentLogic() {
		sec.logic();
		min.logic();
		hrs.logic();
		dys.logic();
	}

	AvianTextField keyboard = new AvianTextField();

	boolean passed;

	@Override
	public void contentRender() {

		AvianFont f = Fonts.Vegur_ExtraSmall;

		long total = endDate.getTimeInMillis() - Dashboard.getCurrentCalendar().getTimeInMillis();

		passed = total < 0;
		if (passed)
			total *= -1;

		int s = (int) ((total / (1000)) % 60);
		int m = (int) ((total / (1000 * 60)) % 60);
		int h = (int) ((total / (1000 * 60 * 60)) % 24);
		int d = (int) ((total / (1000 * 60 * 60 * 24)));

		sec.set(s);
		min.set(m);
		hrs.set(h);
		dys.set(d);

		AvianFont.setHorizontalAlignment(AvianFont.ALIGN_LEFT);
		AvianFont.setVerticalAlignment(AvianFont.ALIGN_CENTER);

		f.drawString((getWidth() / 2) + 5, (getHeight() * (4f / 6f)) + 2, s != 1 ? "seconds" : "second", passed ? 200f / 255f : 1f);

		f.drawString((getWidth() / 2) + 5, (getHeight() * (3f / 6f)) + 2, m != 1 ? "minutes" : "minute", passed ? 200f / 255f : 1f);

		f.drawString((getWidth() / 2) + 5, (getHeight() * (2f / 6f)) + 2, h != 1 ? "hours" : "hour", passed ? 200f / 255f : 1f);

		f.drawString((getWidth() / 2) + 5, (getHeight() * (1f / 6f)) + 2, d != 1 ? "days" : "day", passed ? 200f / 255f : 1f);

		sec.render((getHeight() * (4f / 6f)));
		min.render((getHeight() * (3f / 6f)));
		hrs.render((getHeight() * (2f / 6f)));
		dys.render((getHeight() * (1f / 6f)));

		AvianFont.setHorizontalAlignment(AvianFont.ALIGN_CENTER);
		f.drawString((getWidth() / 2) + 5, (getHeight() * (5f / 6f)), (passed ? "since " : "until ") + ((eventName == null || eventName.equals("null") || eventName.trim().equals("")) ? sdf.format(endDate.getTime()) : eventName.trim()), passed ? 200f / 255f : 1f);

	}

	Number sec = new Number(0);
	Number min = new Number(0);
	Number hrs = new Number(0);
	Number dys = new Number(0);

	static SimpleDateFormat sdf = new SimpleDateFormat("MMM d, y h:mm a");
	static SimpleDateFormat month = new SimpleDateFormat("MMM");
	static SimpleDateFormat day = new SimpleDateFormat("d");
	static SimpleDateFormat year = new SimpleDateFormat("y");
	static SimpleDateFormat hour = new SimpleDateFormat("h aa");
	static SimpleDateFormat minute = new SimpleDateFormat("m");

	@Override
	public void settingsKeyboard() {
		//		if (keyboard.process() == null)
		caretSin = 90;

		eventName = sb.toString();
	}

	int monthOff;
	float monthX;
	int dayOff;
	float dayX;
	int yearNum = new GregorianCalendar().get(Calendar.YEAR);
	int hourOff;
	float hourX;
	int minuteOff;
	float minuteX;

	
	
	@Override
	public void settingsMouse() {
		
	}

	@Override
	public void settingsLogic() {
		if (monthOff < -11) {
			monthOff = -11;
		}
		if (monthOff > 0) {
			monthOff = 0;
		}

		calendar.set(Calendar.MONTH, -monthOff);
		int foo = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		if (dayOff < -foo + 1) {
			dayOff = -foo + 1;
		}
		if (dayOff > 0) {
			dayOff = 0;
		}

		caretSin++;
		caretSin %= 180;

		monthX = AvianMath.glide(monthX, monthOff * (AvianApp.getWidth() / spacing), 10f);
		dayX = AvianMath.glide(dayX, dayOff * (AvianApp.getWidth() / spacing), 10f);

	}

	static Calendar calendar = new GregorianCalendar();

	static AvianRectangle r = new AvianRectangle();

	StringBuilder sb = new StringBuilder();

	float caretSin;

	@Override
	public void settingsRender() {

		AvianFont f = Fonts.Vegur_Small;

		//		r.set((AvianApp.getWidth() / 2f - f.getWidth(sb.toString()) / 2) + f.getWidth(keyboard.getStringToCaret()), (120f + ((AvianApp.getHeight() - 120) * (1f / 12f))) - (50 / 2), 2, 50);
		r.render(AvianColor.white(AvianMath.sin(caretSin) * 100f));
		AvianFont.setVerticalAlignment(AvianFont.ALIGN_CENTER);
		f.drawString((120f + ((AvianApp.getHeight() - 120) * (1f / 12f))), sb.toString());

		calendar.set(Calendar.YEAR, yearNum);

		r.set(AvianApp.getWidth() / 2f - (AvianApp.getWidth() / (spacing * 2f)), (120f + ((AvianApp.getHeight() - 120) * (3f / 12f))) - (50 / 2), AvianApp.getWidth() / spacing, 50);
		r.render(AvianColor.white(100));
		for (int i = 0; i < 12; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, i);
			f.drawString((AvianApp.getWidth() / 2 + (i * (AvianApp.getWidth() / spacing)) + monthX), (120f + ((AvianApp.getHeight() - 120) * (3f / 12f))), month.format(calendar.getTime()));
		}
		r.set(AvianApp.getWidth() / 2f - (AvianApp.getWidth() / (spacing * 2f)), (120f + ((AvianApp.getHeight() - 120) * (5f / 12f))) - (50 / 2), AvianApp.getWidth() / spacing, 50);
		r.render(AvianColor.white(100));
		calendar.set(Calendar.MONTH, -monthOff);
		for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
			calendar.set(Calendar.DAY_OF_MONTH, i + 1);
			f.drawString((AvianApp.getWidth() / 2 + (i * (AvianApp.getWidth() / spacing)) + dayX), (120f + ((AvianApp.getHeight() - 120) * (5f / 12f))), day.format(calendar.getTime()));
		}
		r.set(AvianApp.getWidth() / 2f - (AvianApp.getWidth() / (spacing * 2f)), (120f + ((AvianApp.getHeight() - 120) * (7f / 12f))) - (50 / 2), AvianApp.getWidth() / spacing, 50);
		r.render(AvianColor.white(100));
		f.drawString(AvianApp.getWidth() / 2, (120f + ((AvianApp.getHeight() - 120) * (7f / 12f))), year.format(calendar.getTime()));

	}

	@Override
	public void settingsOpened() {
		//		keyboard.setStringBuilder(sb);
		eventName = sb.toString();
		//		keyboard.setCaret(eventName);
		monthOff = -endDate.get(Calendar.MONTH);
		dayOff = -endDate.get(Calendar.DAY_OF_MONTH) + 1;
		yearNum = endDate.get(Calendar.YEAR);
	}

	@Override
	public void closeSettings() {
		endDate.clear();
		endDate.set(Calendar.YEAR, yearNum);
		endDate.set(Calendar.MONTH, -monthOff);
		endDate.set(Calendar.DAY_OF_MONTH, -dayOff + 1);
		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
	}

	@Override
	public void destroy() {

	}

	@Override
	public String getName() {
		return "Countdown";
	}

	@Override
	public Element create() {
		return new Countdown();
	}

	private class Number {

		final DecimalFormat d = new DecimalFormat("0000000000");

		int[] nums = new int[10];
		int[] olds = new int[10];

		float[] y = new float[10];

		Number(int num) {
			String number = String.valueOf(num);
			for (int i = 0; i < number.length(); i++) {
				nums[i] = (Integer.parseInt(String.valueOf(number.charAt(i))));
				y[i] = 0f;
			}
		}

		void logic() {
			for (int i = 0; i < y.length; i++) {
				y[i] = AvianMath.glide(y[i], 0, 10f);
			}
		}

		void set(int num) {
			String number = d.format(num);
			for (int i = 0; i < number.length(); i++) {
				if (nums[i] != Integer.parseInt(String.valueOf(number.charAt(i)))) {
					olds[i] = nums[i];
					nums[i] = Integer.parseInt(String.valueOf(number.charAt(i)));
					y[i] = -20f;
				}
			}
		}

		void render(float yy) {
			AvianFont.setAlignment(AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
			for (int i = 0; i < olds.length; i++) {
				Fonts.Vegur_Small.drawString((getWidth() / 2f) - ((olds.length - 1) - i) * 13 - 10, (yy + y[i] + 20), String.valueOf(olds[i]), ((passed ? 200 : 255) - ((passed ? 200 : 255) * ((y[i] + 20f) / 20f))) / 255f);
			}
			for (int i = 0; i < nums.length; i++) {
				boolean placeholder = true;
				if (nums[i] == 0 && i < nums.length - 1) {
					for (int j = i - 1; j > 0; j--) {
						if (nums[j] != 0)
							placeholder = false;
					}
				} else
					placeholder = false;
				if (!placeholder) {
					Fonts.Vegur_Small.drawString((getWidth() / 2f) - ((nums.length - 1) - i) * 13 - 10, (yy + y[i]), String.valueOf(nums[i]), ((passed ? 200 : 255) * ((y[i] + 20f) / 20f)) / 255f);
				}
			}
		}
	}

}
