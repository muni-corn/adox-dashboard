
package musicaflight.dashboard;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;

public class WavConverter {

	private static String sourceFileName;
	private static String destFileName;

	public static void setUpConversion(String sourceFileName, String destFileName) {
		WavConverter.sourceFileName = sourceFileName;
		WavConverter.destFileName = destFileName;
	}

	public static void convert() {
		File source = new File(sourceFileName);
		File target = new File(destFileName);
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("pcm_s16le");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("wav");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		try {
			encoder.encode(source, target, attrs);
		} catch (IllegalArgumentException | EncoderException e) {
			e.printStackTrace();
		}
	}

}
