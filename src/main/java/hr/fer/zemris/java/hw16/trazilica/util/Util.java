package hr.fer.zemris.java.hw16.trazilica.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Class contains methods for reading from file
 * 
 * @author Mihael
 *
 */
public abstract class Util {
	/**
	 * Method reads file context<br>
	 * Method split lines on every non-alphabetic character
	 * 
	 * @param path
	 *            - path to file
	 * @return list of all words
	 * @throws IOException
	 *             - if exception during reading appears
	 */
	public static List<String> readLines(Path path) throws IOException {
		String document = readFile(path).replaceAll("\\P{L}+", " ").trim().toUpperCase();

		return Arrays.asList(document.split(" "));
	}

	/**
	 * Method reads file from disc
	 * 
	 * @param path
	 *            - path to file
	 * @return file's context
	 * @throws IOException
	 *             - if exception during reading appears
	 */
	public static String readFile(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}
}
