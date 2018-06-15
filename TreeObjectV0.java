
public class TreeObject {
	
	
	private long DNAseq;
	private int freq;
	
	/**
	 * CONSTRUCTOR for TreeObject
	 * 
	 * @param sequence	a long that is the key to this object
	 * 					converted from the DNA sequence
	 */
	public TreeObject(long sequence) {
		DNAseq = sequence;
		freq = 1;
	}
	
	/**
	 * Increments frequency by one
	 * 
	 */
	public void incrFreq() {
		freq++;
	}
	
	/**
	 * Returns the frequency of this key
	 * @return freq
	 */
	public int getFreq() {
		return freq;
	}
	
	/**
	 * Returns the DNA sequence key in this TreeObject
	 * @return DNAseq
	 */
	public long getSeq() {
		return DNAseq;
	}
	
	
	public String toString() {
		//convert DNAseq to string from long
		
		return "/*DNA*/\t\t"+this.getFreq();
	}


}
