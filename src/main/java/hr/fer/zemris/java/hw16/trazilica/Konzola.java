package hr.fer.zemris.java.hw16.trazilica;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import hr.fer.zemris.java.hw16.trazilica.makers.DocumentMaker;
import hr.fer.zemris.java.hw16.trazilica.makers.VocabularyMaker;
import hr.fer.zemris.java.hw16.trazilica.util.Record;
import hr.fer.zemris.java.hw16.trazilica.util.Util;
import hr.fer.zemris.java.hw16.trazilica.util.Vector3;

public class Konzola {

	/**
	 * List contains all words on documents
	 */
	private static List<String> dictionary;

	/**
	 * List contains all <code>stopping</code> words in language
	 */
	private static List<String> stopWords;

	/**
	 * List contains results of last <code>query</code> command
	 */
	private static List<Record> results;

	private static Map<Path, Vector3> documents;

	private static Vector3 idfVector;

	/**
	 * Path to file where data with dictionary is stored
	 */
	private static final String DICTIONARY_FILE = "src/main/resources/hrvatski_stoprijeci.txt";

	/**
	 * Main program
	 * 
	 * @param args
	 *            - must contain one argument which is path to files with text
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Wrong number of arguments. Should be 1 but is " + args.length);
			return;
		}

		try {
			initialize(Paths.get(args[0]));
			process();
		} catch (Exception e) {
			System.exit(1);
		}
	}

	/**
	 * Method initializes dictionary with data stored in disc file
	 * 
	 * @param path
	 *            - path to file on disc
	 * 
	 * @throws IOException
	 *             - if exception during reading happens
	 * 
	 */
	private static void initialize(Path path) throws IOException {
		stopWords = Files.readAllLines(Paths.get(DICTIONARY_FILE));
		documents = new LinkedHashMap<>();
		prepareDocuments(path);
	}

	/**
	 * Method prepares documents for analyze
	 * 
	 * @param path
	 *            - path to files
	 * @throws IOException
	 *             - if exception during file visiting appears
	 */
	private static void prepareDocuments(Path path) throws IOException {
		getVocabulary(path);
		createsDocuments(path);
	}

	private static void createsDocuments(Path path) throws IOException {
		DocumentMaker maker = new DocumentMaker(path, dictionary);

		Files.walkFileTree(path, maker);

		documents = maker.getDocuments();
		idfVector = maker.getIDF();
	}

	/**
	 * Method creates class for file visiting and executes file tree walking
	 * 
	 * @param path
	 *            - path to file
	 * @throws IOException
	 *             - if exception during walking appears
	 */
	private static void getVocabulary(Path path) throws IOException {
		VocabularyMaker maker = new VocabularyMaker(path, stopWords);

		Files.walkFileTree(path, maker);

		dictionary = maker.getVocabulary();
	}

	/**
	 * Method represents shell which asks user to enter command and,if command is
	 * supported,executes commands
	 */
	private static void process() {
		System.out.println("Dictionary length is " + dictionary.size());
		System.out.println();

		try (Scanner sc = new Scanner(System.in)) {
			while (true) {
				System.out.print("Enter command> ");
				String command = checkCommand(sc.nextLine().trim());

				if (command != null) {
					if (command.startsWith("query")) {
						queryExecute(command);
					} else if (command.startsWith("exit")) {
						break;
					} else if (command.equals("results")) {
						printResults();
					} else if (command.startsWith("type")) {
						typeExecute(command);
					}
				} else {
					System.out.println("Unsupported command!");
				}

			}

			System.out.println("Goodbye!");
		}

	}

	/**
	 * Method prints context of file on given position. Method will show results if
	 * 'query' is already executed,otherwise it will print appropriate message
	 * 
	 * @param command
	 *            - user's command
	 */
	private static void typeExecute(String command) {
		if (results != null) {
			try {
				command = command.substring(4);

				int value = Integer.parseInt(command.trim());
				Record result = results.get(value);

				List<String> list = Util.readLines(result.getPath());

				System.out.println("Document: " + result.getPath());
				System.out.println("------------------------------");
				System.out.println(result.getPath().getFileName().toString());
				System.out.println();
				list.forEach(e -> System.out.print(e));
				System.out.println("\n");
				System.out.println("------------------------------");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Method prints results of last query
	 */
	private static void printResults() {
		if (results != null) {
			for (int i = 0, lenght = results.size(); i < lenght; i++) {
				Record record = results.get(i);
				System.out.println("[" + (i + 1) + "] (" + record.getValue() + ") " + record.getPath());
			}
		} else {
			System.out.println("Before results showing,you must execute 'query' command");
		}
	}

	/**
	 * Method represents implementation of <code>query</code> command
	 * 
	 * @param command
	 *            - command
	 */
	private static void queryExecute(String command) {
		command = command.substring(5);

		List<String> list = Arrays.asList(command.split(" "));
		list.retainAll(dictionary);
	}

	/**
	 * Method returns line with command and arguments if program supports command
	 * processing
	 * 
	 * @param line
	 *            - line
	 * @return command if program supports command executing,otherwise
	 *         <code>null</code>
	 */
	private static String checkCommand(String line) {
		if (line == null || line.length() == 0)
			return null;

		line = line.trim();

		if (line.startsWith("query") || line.startsWith("exit") || line.startsWith("results")
				|| line.startsWith("type")) {
			return line;
		}

		return null;
	}
}
