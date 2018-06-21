import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class ParseGBKFile {

	private boolean foundDNA;
	private BufferedReader reader;

    /**
     * CONSTRUCTOR
     * 
     * @param fileName      name of Gene Bank file to parse
     * @throws IOException 
     */
    public ParseGBKFile(File GBKfile) throws IOException {
    		reader = new BufferedReader(new FileReader(GBKfile));
    }

    
    /**
     * Moves to the beginning of the next DNA sequence in the desired file
     * 
     * @throws IOException
     */
    public boolean findDNA() throws IOException {
    		String nextLine;
    		foundDNA = false;
    		while ( ((nextLine = reader.readLine()) != null) && !foundDNA) {
    			if (nextLine.contains("ORIGIN")) {
    				foundDNA = true;
    			}
    		} // next iteration of readLine() will give beginning of DNA seq
    		return foundDNA;
    }
    		
    	public String parseDNA() throws IOException {
    		
    		String wholeSequence = "";
		boolean endDNA = false;
		
		
		while(!endDNA) {
    			char nextChar = (char) reader.read();
		    	nextChar = Character.toUpperCase(nextChar);
		    	if (nextChar == 'A') {
		    		wholeSequence += "A";
			} else if (nextChar == 'C') {
				wholeSequence += "C";
			} else if (nextChar == 'G') {
				wholeSequence += "G";
			} else if (nextChar == 'T') {
				wholeSequence += "T";
			} else if (nextChar == 'N') {
				wholeSequence += "N";
			} else if (nextChar == '/') {
			    	endDNA = true;
			}
		}
    		return wholeSequence;
    	}

    	/**
    	 * Converts a DNA string to a long, which will be the key
    	 * value for the TreeObject
    	 * 
    	 * @param DNA	a String representation of the DNA string
    	 * @return		a converted long representation for the key
    	 */
	public long stringToKey(String DNA) {
		
		Long longSeq;
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
	
	
//	public long keyToString(Long DNA, int keyLength) {
//		
//		String stringSeq = DNA.toString();
//		int diff = keyLength - stringSeq.length()/2;
//		for (int i = 0; i < diff; i++) {
//			stringSeq = "00" + stringSeq;
//		}
//		
//		for(int i = 0; i < stringSeq.length()/2; i+=2) {
//			if ((stringSeq.charAt(i) == '0') && (stringSeq.charAt(i+1) == '0')) {
//				stringSeq = stringSeq.replace("00", "A");
//			} else if ((stringSeq.charAt(i) == '0') && (stringSeq.charAt(i+1) == '1')) {
//				stringSeq = stringSeq.replace("01", "C");
//			} else if ((stringSeq.charAt(i) == '1') && (stringSeq.charAt(i+1) == '0')) {
//				stringSeq = stringSeq.replace("10", "G");
//			} else if ((stringSeq.charAt(i) == '1') && (stringSeq.charAt(i+1) == '1')) {
//				stringSeq = stringSeq.replace("11", "T");
//			}
//		}
//		
//		
//		long longSeq = Long.parseLong(stringSeq);
//		return longSeq;
//	}






}