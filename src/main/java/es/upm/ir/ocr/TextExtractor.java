package es.upm.ir.ocr;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;

public class TextExtractor {

	static TessBaseAPI api = new TessBaseAPI();

	public static void main(String[] args) {
		// Initialize tesseract-ocr with English, without specifying tessdata
		// path
		if (api.Init(".", "ENG") != 0) {
			System.err.println("Could not initialize tesseract.");
			System.exit(1);
		}

		String inputPath = null;
		String outputPath = null;
		if (args.length < 1) {
			inputPath = "input";
			outputPath = "output";
		} else {
			inputPath = args[0];
			outputPath = args[1];
		}
		File[] files = FileUtils.getAllFilesTifFiles(inputPath);
		for (File f : files) {
			try {
				processImage(f, outputPath);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		api.End();
		System.exit(0);
	}

	public static void processImage(File file, String exitFolder) throws Exception {
		BytePointer outText;

		PIX image = pixRead(file.getAbsolutePath());
		api.SetImage(image);
		// left;top;width;height
		api.SetRectangle(150, 350, 1550, 2250);

		// get output text from tesseract
		outText = api.GetUTF8Text();
		// from byte to actual string
		String generatedText = outText.getString();

		String fileName = file.getName().split(".tif")[0];
		System.out.println(generatedText);
		createTextFile(generatedText, fileName, exitFolder);

		outText.deallocate();
		pixDestroy(image);
	}

	public static void createTextFile(String content, String fileName, String outputFolder) throws Exception {
		PrintWriter writer = new PrintWriter(outputFolder + "\\" + fileName + ".txt", "UTF-8");
		writer.println(content);
		writer.close();
		System.out.println("Successfully save recognized text");
	}

}
