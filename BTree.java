
public class BTree {

	private int size; // size of BTree
	
	public BTree() {
		
	}
	
	
	public int getSize() {
		return size;
	}
	
	/**
	 * Converts a string to a key
	 * @param word
	 * @return
	 */
	public long stringToKey(String word){
		long numb = 0;
		for(int i = 0; i < word.length(); i++){
				if(word.charAt(i) == 'A' || word.charAt(i) == 'a'){
					numb = numb << 2;
					numb = numb | A;
				} else if(word.charAt(i) == 'C' || word.charAt(i) == 'c'){
					numb = numb << 2;
					numb = numb | C;
				} else if(word.charAt(i) == 'T' || word.charAt(i) == 't'){
					numb = numb << 2; // bitwise operation
					numb = numb | T; // bitwise operation
				} else if(word.charAt(i) == 'G' || word.charAt(i) == 'g'){
					numb = numb << 2;
					numb = numb | G;
				} else {

				}
		}

		return numb;
	}
	// Convert a key to a string
	public String keyToString(long key){
		StringBuilder str = new StringBuilder();
		Stack<Character> stack = new Stack<Character>();
		long mask = 3;
		long temp;
		for(int i = 0; i < sequence;i++){
			temp = (key & mask);
			if(temp  == A){
				stack.push('A');
			} else if(temp  == C){
				stack.push('C');
			} else if(temp  == T){
				stack.push('T');
			} else if(temp  == G){
				stack.push('G');
			} else {

			}
			key = key >> 2;
		}
		while(!stack.isEmpty()){
			str.append(stack.pop());
		}
		return str.toString();
	}
	
}
