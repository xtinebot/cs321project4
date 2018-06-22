//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;

/**
 * Driver class for creating BTree from GBK file
 * 
 * @author Christine Chang, Keaton Gillihan, Koutam Haidara
 *
 */
public class GeneBankCreateBTree {

	static boolean hasCache;
	static int cacheSize;
	static int degree;
	static String fileName;
	static File GBKFile;
	static int seqLength;
	static int debugLevel;
	static BTree tree;
	static FileWriter fw;
	static BufferedWriter bw;
	
	
	public static void main(String[] args) throws IOException {
		
		// args: <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]
		
		/* Usage output */
		if (args.length < 4 || args.length > 6) {
			System.err.println("USAGE: $ java GeneBankCreateBTree <0/1(no/with Cache)> "
					+ "<degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
			System.err.println("<degree>: choose 0 for optimal degree");
			System.err.println("<sequence length>: from 1 to 31, inclusive");
			System.err.println("<debug level>: 0 to print basic messages to console via stderr,"
					+ " 1 to print text file with DNA strings and frequencies\n");
			System.exit(1);
		}
		
		/* Set whether has cache or not */
		if (args[0].equals("0")) {
			hasCache = false;
		} else if (args[0].equals("1")) {
			hasCache = true;
		} else {
			throw new IllegalArgumentException("Illegal argument, first argument must be 0 or 1");
		}
		
		/* Set degree of BTree, set to ## if degree of 0 input via console */
		degree = Integer.parseInt(args[1]);
		if (degree == 0) {
			degree = 3; // got 127 based on calcs??
		}
		
		/* Set GBK File to parse */
		fileName = args[2];
		GBKFile = new File(fileName);
		
		/* Set sequence length to parse */
		seqLength = Integer.parseInt(args[3]);
		
		/* Set cache size and debug level */
		if (hasCache == true) { // if has cache, size must be input parameter
			cacheSize = Integer.parseInt(args[4]);
		} else if (args.length > 4) { // if no cache and has another argument, arg is debug level
			debugLevel = Integer.parseInt(args[4]);
		} else { // else default debug level set to 0
			debugLevel = 0;
		}
		
		
		/* Initialize the B-Tree with or without cache */
		if (hasCache) {
			tree = new BTree(degree, cacheSize, seqLength, fileName);
		} else {
			tree = new BTree(degree, seqLength, fileName);			
		}
		
		/* Parse GBK file into one DNA string */
		ParseGBKFile parser = new ParseGBKFile(GBKFile);
		while (parser.findDNA()) {
			String wholeDNASequence = parser.parseDNA(); // get whole DNA string
			/* Add sequences of desired length to the tree */
			for (int i = 0; i < wholeDNASequence.length() - seqLength; i++) {
				String keySequence = wholeDNASequence.substring(i, i + seqLength);
				System.out.print(keySequence + "\t");
				long key = parser.stringToKey(keySequence);
				tree.insert(key);
				//System.out.println(key);
			}
		}
		
		/* Print dump file */
		if (debugLevel == 1) {
			tree.dumpTree();
		}
		
		

	}

}