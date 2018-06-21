package hr.fer.zemris.java.hw16.trazilica.makers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import hr.fer.zemris.java.hw16.trazilica.util.Util;

/**
 * Class extends {@link SimpleFileVisitor} and overrides method for file
 * visiting.<br>
 * During file visiting,method collects all unique words
 * 
 * @author Mihael
 *
 */
public class VocabularyMaker extends SimpleFileVisitor<Path> {
	/**
	 * Set of all unique words
	 */
	private Set<String> vocabulary;
	/**
	 * List contains all stop words in language
	 */
	private List<String> stopWords;

	/**
	 * Constructor initializes new folder walker which finds all words
	 * 
	 * @param path
	 *            - path to folder with documents
	 * @param stopWords
	 * @throws IllegalArgumentException
	 *             - if given path is not path to folder
	 * @throws NullPointerException
	 *             - if list argument or path are null
	 */
	public VocabularyMaker(Path path, List<String> stopWords) {
		Objects.requireNonNull(path, "Path to folder cannot be null!");
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Given path must be folder!");
		}
		this.stopWords = Objects.requireNonNull(stopWords, "List with stop words cannot be null!s");
		this.vocabulary = new LinkedHashSet<>();
	}

	/**
	 * Method passes trough every file in folder and collects all words
	 * 
	 * @param arg0
	 *            - path to file
	 */
	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
		for (String string : Util.readLines(arg0)) {
			if (string.length() != 0 && !stopWords.contains(string)) {
				vocabulary.add(string);
			}
		}

		return FileVisitResult.CONTINUE;
	}

	/**
	 * Method returns list of founded words
	 * 
	 * @return list of founded words
	 */
	public List<String> getVocabulary() {
		return new ArrayList<>(vocabulary);
	}

}
