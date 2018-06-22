import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Driver class for Cache project
 * 
 * CS 321 Summer 2018
 * 
 * @author Christine Chang
 *
 */
public class Test {

	private static int numCache;
	private static int[] sizeCache;
	private static String fileName;
	private Cache<String> cache1;
	private Cache<String> cache2;
	private File testFile;
	private int numHits; // total number of hits
	private int numHits1; // number of hits in cache 1
	private int numHits2; // number of hits in cache 2
	private int numRefs; // number of times the cache was searched
	
	
	public static void main(String[] args) {
		/* Ensure correct number of arguments */
		if (args.length < 3 || args.length > 4) {
			printUsage();
			System.exit(1);
		}
		
		/* Parse Input */
		/* Determine if 1-level or 2-level cache
		/* Assign cache size
		/* Take test file name */
		try {
			numCache = Integer.parseInt(args[0]);
			if (numCache == 1) {
				sizeCache = new int[1];
				sizeCache[0] = Integer.parseInt(args[1]);
				fileName = args[2];
			} else if (numCache == 2) {
				sizeCache = new int[2];
				sizeCache[0] = Integer.parseInt(args[1]);
				sizeCache[1] = Integer.parseInt(args[2]);
				fileName = args[3];
			}
		} catch (Exception e) {
			printUsage();
			System.exit(1);
		}
		
		try {
			new Test(sizeCache, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}		

	}
	
	/** 
	 * Print instructions for running 
	 * the driver class from the command line 
	 */
	private static void printUsage() {
		System.out.println("Command line input should take the form:");
		System.out.println("$ java Test 1 <cache size> <input textfile name>");
		System.out.println("or");
		System.out.println("$ java Test 2 <1st level cache size> <2nd level cache size> <input textfile name>");
	}
	
	/**
	 * Private class for testing cache
	 * Called in main method
	 * 
	 * @param cacheSize	an int array containing the desired size
	 * 					of each cache; two elements if a two-level
	 * 					cache, or one element if a one-level cache
	 * @param filename	the name of the desired test file
	 */
	private Test(int[] cacheSize, String filename) throws FileNotFoundException {
		
		/* For testing a one-level cache */
		
		if(cacheSize.length == 1) {
			cache1 = new Cache<String>(cacheSize[0]);
			System.out.println("One-level cache with " + cacheSize[0] + " entries created.");
			System.out.print("Processing. ");
			numRefs = 0;
			numHits = 0;
			 
			testFile = new File(fileName);
				
			Scanner scan = new Scanner(testFile);
			String temp;
			int visualProgress = 0;
			
			while(scan.hasNextLine()) {
				visualProgress++;
				if (visualProgress % 10000 == 0) {
					System.out.print(". ");
				}
				temp = scan.nextLine();
				StringTokenizer st = new StringTokenizer(temp);
				while(st.hasMoreTokens()) {
					numRefs++;
					String next = st.nextToken();
					if (cache1.contains(next)) {
						numHits++;
						cache1.moveObject(next);
					} else {
						cache1.addObject(next);
					}
				}
			}
				
			scan.close();
			System.out.println();
				
			System.out.println("RESULTS");
			System.out.println("Number of global references: " + numRefs);
			System.out.println("Number of global cache hits: " + numHits);
			System.out.print("Global hit ratio:            " + ((double)numHits/numRefs));
				
				
		/* For testing a two-level cache */
		} else {
			cache1 = new Cache<String>(cacheSize[0]);
			cache2 = new Cache<String>(cacheSize[1]);
			System.out.println("Two-level cache created.");
			System.out.println("First level: "+ cacheSize[0] + " entries");
			System.out.println("Second level: "+ cacheSize[1] + " entries");
			System.out.print("Processing. ");
			numRefs = 0;
			numHits1 = 0;
			numHits2 = 0;
			numHits = numHits1 + numHits2;
			
			testFile = new File(fileName);
			
			Scanner scan = new Scanner(testFile);
			String temp;
			int visualProgress = 0;
			 
			while(scan.hasNextLine()) {
				visualProgress++;
				if (visualProgress % 10000 == 0) {
					System.out.print(". ");
				}
				temp = scan.nextLine();
				StringTokenizer st = new StringTokenizer(temp);
				while(st.hasMoreTokens()) {
					numRefs++;
					String next = st.nextToken();
					if (cache1.contains(next)) {// if in cache 1
						numHits1++;
						// move to top of cache 1
						cache1.moveObject(next);
						// move to top of cache 2
						cache2.moveObject(next);
					} else if (cache2.contains(next)) {// else if in cache 2
						numHits2++;
						// move to top of cache 2
						cache2.moveObject(next);
						// add to top of cache 1
						cache1.addObject(next);
					} else { //not in either cache
						// add to top of cache 1
						cache1.addObject(next);
						// add to top of cache 2
						cache2.addObject(next);
					}
						
				}
			}
			scan.close();
			System.out.println();
			
			numHits = numHits1 + numHits2;
			
			System.out.println("RESULTS");
			System.out.println("Number of global references: " + numRefs);
			System.out.println("Number of global cache hits: " + numHits);
			System.out.println("Global hit ratio:             " + ((double)numHits/numRefs) + "\n");
			
			System.out.println("Number of 1st level references: " + numRefs);
			System.out.println("Number of 1st level cache hits: " + numHits1);
			System.out.println("1st level cache hit ratio:        " + ((double)numHits1/numRefs) + "\n");
			
			System.out.println("Number of 2nd level references: " + (numRefs - numHits1));
			System.out.println("Number of 2nd level cache hits: " + numHits2);
			System.out.println("2nd level cache hit ratio:       " + ((double)numHits2/(numRefs-numHits1)) + "\n");
			
		}
		
		
	}	

}
