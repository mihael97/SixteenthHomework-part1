package hr.fer.zemris.java.hw16.trazilica.makers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import hr.fer.zemris.java.hw16.trazilica.util.Util;
import hr.fer.zemris.java.hw16.trazilica.util.Vector3;
import static java.lang.Math.log10;

/**
 * Class extends {@link SimpleFileVisitor} and has implementation for
 * <code>TF-IDF</code> analyze on documents
 * 
 * @author Mihael
 *
 */
public class DocumentMaker extends SimpleFileVisitor<Path> {
	/**
	 * List of all words in documents
	 */
	private List<String> dictionary;
	/**
	 * Map which contains final results
	 */
	private Map<Path, Vector3> results;
	/**
	 * IDF vector
	 */
	private Vector3 idf;

	/**
	 * Constructor initializes new document visitor
	 * @param dictionary
	 *            - list of all words
	 */
	public DocumentMaker(List<String> dictionary) {
		this.dictionary = Objects.requireNonNull(dictionary);
		this.results = new LinkedHashMap<>();
	}

	/**
	 * Method goes through all files and records number of appearances of word
	 * 
	 * @param path
	 *            - path to file
	 * @param arg1
	 *            - file attributes
	 * 
	 * @return {@link FileVisitResult},always {@link FileVisitResult#CONTINUE}
	 * @throws IOException
	 *             - exception during file reading
	 */
	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes arg1) throws IOException {
		double[] frequency = new double[dictionary.size()];

		List<String> document = Util.readLines(path);

		for (String string : document) {
			string = string.toUpperCase().trim();
			if (dictionary.contains(string)) {
				int index = dictionary.indexOf(string);

				frequency[index]++;
			}
		}

		results.put(path, new Vector3(frequency)); // tf vector
		return FileVisitResult.CONTINUE;
	}

	/**
	 * Method returns map where key is path to file and value is <code>TF-IDF</code>
	 * vector
	 * 
	 * @return map about documents
	 */
	public Map<Path, Vector3> getDocuments() {
		prepareResults();
		return results;
	}

	/**
	 * Method prepares results for sending
	 */
	private void prepareResults() {

		calculateIDF();

		for (Map.Entry<Path, Vector3> map : results.entrySet()) {
			map.setValue(Vector3.multiply(idf.toArray(), map.getValue().toArray()));
		}
	}

	/**
	 * Method calculates IDF vector<br>
	 * IDF vector is calculated with formula<br>
	 * 
	 * <code>|D|/|d|</code><br>
	 * where <code>|D|</code> is number of files and <code>|d|</code> is number of
	 * documents where word appears
	 * 
	 * @return IDF vector
	 */
	private void calculateIDF() {
		double[] coeff = new double[dictionary.size()];

		for (int i = 0, size = results.size(), length = dictionary.size(); i < length; i++) {
			coeff[i] = log10((double) (size / frequencyOfWord(dictionary.get(i))));
		}

		this.idf = new Vector3(coeff);
	}

	/**
	 * Method calculates in how many documents word appears
	 * 
	 * @param word
	 *            - word
	 * @return number of documents where word appears
	 */
	private int frequencyOfWord(String word) {
		int number = 0;
		int index = dictionary.indexOf(word);

		for (Map.Entry<Path, Vector3> map : results.entrySet()) {
			if (map.getValue().toArray()[index] != 0) {
				number++;
			}
		}

		return number;
	}

	/**
	 * Method returns created IDF vector
	 * 
	 * @return IDF vector
	 */
	public Vector3 getIDF() {
		return idf;
	}
}
