public class FileParser {

	private static final Object START = null;
	private BufferedReader reader;
	private int sequenceLength;

	final byte A = 00;
	final byte T = 11;
	final byte C = 01;
	final byte G = 10;
	/**
	 * Constructor moves the buffered reader to the beginning of the genetic data.
	 * @param fileName
	 * @param sequence length
	 */
	public FileParser(String fileName,int sequence) {
		sequenceLength = sequence;

		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			boolean reading = true;
			String line;
			String word;
			String delim = " ";
			StringTokenizer token;
			while(reading){
				if((line = br.readLine()) == null){
					throw new IOException();
				}
				token = new StringTokenizer(line,delim);
				while(token.hasMoreTokens() && reading){
					word = token.nextToken();
					if(word.equals(START)){
						reading = false;
					}
				}
			}
			reader = br;
		} catch (IOException e) {
			
			System.out.println("Unable to process file " + fileName);
			e.printStackTrace();
		}
	}
	/**
	 * Forms a long representing a sequence of genetic data.
	 *
	 * @return sequence of genetic data.
	 * @throws IOException if the reader fails
	 */
	public long getSequence() throws IOException{
		long sequence = 0;
		String line;
		if((line = reader.readLine()) == null){
			throw new IOException();
		}
		String[] word = line.split("//s+");
		int index = 0;
		while(word[index] != "n" || word[index] != "N" || word[index] != null){
			for(int i = 0; i < word[0].length(); i++){
				if(word[0].charAt(i) == 'A' || word[0].charAt(i) == 'a'){
					sequence = sequence << 2;
					sequence = sequence | A;
				} else if(word[0].charAt(i) == 'C' || word[0].charAt(i) == 'c'){
					sequence = sequence << 2;
					sequence = sequence | C;
				} else if(word[0].charAt(i) == 'T' || word[0].charAt(i) == 't'){
					sequence = sequence << 2;
					sequence = sequence | T;
				} else if(word[0].charAt(i) == 'G' || word[0].charAt(i) == 'g'){
					sequence = sequence << 2;
					sequence = sequence | G;
				} else {

				}
			}
			index++;
		}

		return sequence;

	}

}
