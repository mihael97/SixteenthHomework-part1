package hr.fer.zemris.java.hw16.trazilica;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import hr.fer.zemris.java.hw16.trazilica.makers.DocumentMaker;
import hr.fer.zemris.java.hw16.trazilica.makers.VocabularyMaker;
import hr.fer.zemris.java.hw16.trazilica.util.Record;
import hr.fer.zemris.java.hw16.trazilica.util.Util;
import hr.fer.zemris.java.hw16.trazilica.util.Vector3;

/**
 * Class implements console user interface for communicating with user and
 * showing results of document comparing
 * 
 * @author Mihael
 *
 */
public class Konzola {

	/**
	 * List contains all words on documents
	 */
	private static List<String> dictionary;

	/**
	 * List contains all <code>stopping</code> words in language
	 */
	private static Set<String> stopWords;

	/**
	 * List contains results of last <code>query</code> command
	 */
	private static List<Record> results;

	/**
	 * Map stores path to file with file's <code>tfidf</code>
	 */
	private static Map<Path, Vector3> documents;

	/**
	 * IDF vector for documents
	 */
	private static Vector3 idfVector;

	/**
	 * Path to file where data with dictionary is stored
	 */
	private static final String DICTIONARY_FILE = "src/main/resources/hrvatski_stoprijeci.txt";

	/**
	 * Constant which represents how small similarity must be to be treated like
	 * zero
	 */
	private static final Double CONSTANT = Math.pow(10, -6);

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
			e.printStackTrace();
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
		List<String> list = Files.readAllLines(Paths.get(DICTIONARY_FILE));
		stopWords = new LinkedHashSet<>();

		for (String string : list) {
			string = string.toUpperCase().trim();
			stopWords.add(string);
		}

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

	/**
	 * Method passes through all documents and creates their vectors
	 * 
	 * @param path
	 *            - path to file
	 * @throws IOException
	 *             - if exception during passing appears
	 */
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

				String list = Util.readFile(result.getPath());

				System.out.println("Document: " + result.getPath());
				System.out.println("------------------------------");
				System.out.println(result.getPath().getFileName().toString());
				System.out.println();
				System.out.println(list);
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
			for (int i = 0, lenght = 10; i < lenght; i++) {
				Record record = results.get(i);

				if (Double.compare(record.getValue(), CONSTANT) < 0)
					break;

				System.out
						.println("[" + i + "] (" + String.format("%.4f", record.getValue()) + ") " + record.getPath());
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

		List<String> pomList = Arrays.asList(command.split(" "));

		System.out.print("Query is: [");
		StringBuilder builder = new StringBuilder();
		List<String> list = new ArrayList<>();

		for (String str : pomList) {
			str = str.toUpperCase().trim();
			if (!dictionary.contains(str))
				continue;

			list.add(str);
			builder.append(", " + str);
		}

		System.out.print(builder.toString().substring(2));

		System.out.println("]");
		double[] array = new double[dictionary.size()];

		for (String string : list) {
			array[dictionary.indexOf(string.toUpperCase().trim())]++;
		}

		Vector3 vector = Vector3.multiply(array, idfVector.toArray());

		calculate(vector);
	}

	/**
	 * Method calculates similarity between all documents and document made by given
	 * query. After that,results list is sorted by similarity
	 * 
	 * @param vector
	 *            - vector made by given query
	 */
	private static void calculate(Vector3 vector) {
		results = new ArrayList<>();

		for (Map.Entry<Path, Vector3> map : documents.entrySet()) {
			results.add(new Record(map.getKey(), vector.cosAngle(map.getValue())));
		}

		Collections.sort(results, new Comparator<Record>() {

			@Override
			public int compare(Record first, Record second) {
				return Double.compare(second.getValue(), first.getValue());
			}
		});

		printResults();
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
