package es.upm.ir.ocr;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

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
		//set default paths
		if (args.length < 1) {
			inputPath = "input";
			outputPath = "output";
		} else {
			// set paths to cmd arguments if exists
			inputPath = args[0];
			outputPath = args[1];
		}
		//read all existing tiff files
		File[] files = FileUtils.getAllFilesTifFiles(inputPath);
		for (File f : files) {
			try {
				//for each image, process the image with tesseract
				processImage(f, outputPath);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		api.End();
		System.exit(0);
	}

	public static void processImage(File file, String exitFolder) throws Exception {
		//load the image into memory
		PIX image = pixRead(file.getAbsolutePath());
		//extract text and insert into hashmap
		HashMap<String, String> generatedText = new HashMap<String, String>();
		generatedText.put("1: Headline", getTextFromCoordinates(image, 150, 330, 1550, 550));
		generatedText.put("2: ColumnLeft", getTextFromCoordinates(image, 140, 950, 1040, 1974));
		generatedText.put("3: ColumnRight", getTextFromCoordinates(image, 1185, 950, 1040, 1974));
		String fileName = file.getName().split(".tif")[0];
		//create text file from hashmap
		createTextFile(generatedText, fileName, exitFolder);

		pixDestroy(image);
	}

	public static void createTextFile(HashMap<String, String> generatedText, String fileName, String outputFolder)
			throws Exception {
		PrintWriter writer = new PrintWriter(outputFolder + "\\" + fileName + ".txt", "UTF-8");
		for (Entry<String, String> item : generatedText.entrySet()) {
			writer.println(item.getKey());
			writer.println(item.getValue());
		}

		writer.close();
		System.out.println("Successfully save recognized text");
	}

	private static String getTextFromCoordinates(PIX image, int left, int top, int width, int height) {
		BytePointer outText;

		api.SetImage(image);
		// left;top;width;height
		api.SetRectangle(left, top, width, height);
		// get output text from tesseract
		outText = api.GetUTF8Text();
		outText.deallocate();
		return outText.getString();
	}

}
