import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.util.Scanner;

public class GeneBankSearch {
	
	static boolean hasCache;
	static int cacheSize;
	static String bTreeFilename;
	static File bTreeFile;
	static String queryFilename;
	static File queryFile;
	static int debugLevel;
	static BufferedReader brBTree;
	static Scanner scanBTree;
	static Scanner scanQuery;
	static BTree bTree;
	

	public static void main(String[] args) throws FileNotFoundException {
		// args <0/1 no/with cache> <btree file> <query file> [<cache size>] [<debug level>]

		/* Usage output */
		if (args.length < 3 || args.length > 5) {
			System.err.println("USAGE: $ java GeneBankSearch <0/1(no/with Cache)> "
					+ "<btree file> <query file> [<cache size>] [<debug level>]");
			System.err.println("<debug level>: 0 to print basic messages to console via stderr,"
					+ " 1 to print text file with DNA strings and frequencies\n");
			System.exit(1);
		}
		
		
		
		/* Set whether has cache or not */
		if (args[0].equals("0")) {
			hasCache = false;
		} else if (args[0].equals("1")) {
			hasCache = true;
			/* Set cache size */
			if (args[3] != null) {
				cacheSize = Integer.parseInt(args[3]);
			}
		} else {
			throw new IllegalArgumentException("Illegal argument, first argument must be 0 or 1");
		}
		
		/* Set BTree file to use */
		bTreeFilename = args[1];
		bTree = new BTree(bTreeFilename);
		
		
		/* Set Query file to use */
		queryFilename = args[2];
		queryFile = new File(queryFilename);
		scanQuery = new Scanner(queryFile);
		
		/**
		 * Algorithm for next steps:
		 * while there is still another token in queryFile
		 * 	scan for next token (String) in queryFile
		 * 	print that String to stdout
		 * 	search for the TreeObject containing that String in the bTreeFile
		 * 		if it exists, return the frequency to stdout
		 * 		else return 0
		 */
		
		String tokenToQuery;
		long key;
		TreeObject obj;
		int freq;
		while ((tokenToQuery = scanQuery.next()) != null) {
			System.out.print(tokenToQuery + "\t\t"); // print token to stdout
			key = stringToKey(tokenToQuery);// convert token to long for search in BTree
			obj = bTree.search(key);
			if (obj != null) { // if (find tokenToQuery in BTree)
				freq = obj.getFreq();
				System.out.println(freq);
			} else {
				System.out.println("0");
			}
		}
		
	}
	
	private static long stringToKey(String DNA) {
		
		long longSeq;
		for(int i = 0; i < DNA.length(); i++) {
			if (DNA.charAt(i) == 'A') {
				DNA = DNA.replace("A", "00");
			} else if (DNA.charAt(i) == 'C') {
				DNA = DNA.replace("C", "01");
			} else if (DNA.charAt(i) == 'G') {
				DNA = DNA.replace("G", "10");
			} else if (DNA.charAt(i) == 'T') {
				DNA = DNA.replace("T", "11");
			}
		}
		
		longSeq = Long.parseLong(DNA);
		return longSeq;
	}

}
