package hr.fer.zemris.java.hw16.trazilica.util;

import java.nio.file.Path;

/**
 * Class represents structure which contains results of documents analyze
 * 
 * @author Mihael
 *
 */
public class Record {
	/**
	 * Path to file
	 */
	private Path path;

	/**
	 * Similarity between two vectors
	 */
	private double value;

	/**
	 * Constructor creates new Record instance for file with given path
	 * 
	 * @param path
	 *            - path to file
	 * @param value
	 *            - file's similarity
	 */
	public Record(Path path, double value) {
		super();
		this.path = path;
		this.value = value;
	}

	/**
	 * Method returns file path
	 * 
	 * @return file path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * Method returns value
	 * 
	 * @return file's value
	 */
	public double getValue() {
		return value;
	}

}
