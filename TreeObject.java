public class TreeObject{

	private int frequency;
	private long key;

	public TreeObject(long key){
		this.key=key;
		frequency = 1;
	}

	public void increment(){
		frequency++;
	}

	/**
	 * Returns 0 if equal, -1 if less, or 1 if greater.
	 * @param num
	 * @return
	 */
	
	public int equal(TreeObject obj){
		if(obj == null){
			return -1;
		}

		if(key == obj.getKey()){
			return 0;
		}else if(key < obj.getKey()){
			return -1;
		} else {
			return 1;
		}
	}

	public long getKey(){
		return key;
	}

	public String toString(){
		return key + "";

	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
