import java.io.File;

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
	
	
	public static void main(String[] args) {
		
		// args: <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]
		
		/* Usage output */
		if (args.length < 4 || args.length > 6) {
			System.out.println("USAGE: $ java GeneBankCreateBTree <0/1(no/with Cache)> "
					+ "<degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
			System.out.println("<degree>: choose 0 for optimal degree");
			System.out.println("<sequence length>: from 1 to 31, inclusive");
			System.out.println("<debug level>: 0 to print basic messages to console via stderr,"
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
		
		/* Set degree of BTree */
		degree = Integer.parseInt(args[1]);
		
		/* Set GBK File to parse */
		fileName = args[2];
		GBKFile = new File(fileName);
		
		/* Set sequence length to parse */
		seqLength = Integer.parseInt(args[3]);
		
		/* Set cache size */
		if (args.length > 4) {
			cacheSize = Integer.parseInt(args[4]);
		} else {
			cacheSize = 500;
		}

		/* Set debug level */
		if (args.length > 5) {
			debugLevel = Integer.parseInt(args[5]);
		} else {
			debugLevel = 0;
		}
		
		
		tree = new BTree(/* parameters*/);
		

	}

}
