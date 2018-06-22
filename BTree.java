import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;


public class BTree {

	private int root;  // root of the BTree
	private int degree = 1; // degree of BTree
	private int height = 0;
	private String fileName;
	private File data;
	private int currentOffset;
	private Cache<BTreeNode> cache;
	private int sequence;
	private RandomAccessFile raf;
	private int offsetJump;
	int sum = 0;

	private int blockSize = 4096;

	public BTree(int degree,int cacheSize,int sequence, String fileName){
			ByteBuffer bb= ByteBuffer.allocate(16);
			this.degree=degree;
			this.fileName=fileName;
			offsetJump=13+(degree*2-1)*16;
			this.cache=new Cache<BTreeNode>(cacheSize);
			this.sequence=sequence;
			try {
				raf=new RandomAccessFile(fileName+".btree.data."+sequence+"."+degree,"rw");
				
				raf.setLength(4096);
				bb.putInt(root);
				bb.putInt(height);
				bb.putInt(degree);
				bb.putInt(sequence);
				bb.flip();
				byte[] data= new byte[bb.limit()];
				bb.get(data);
				raf.write(data);
				root=(int) raf.getFilePointer();
				BTreeNode theRoot=new BTreeNode(root, 0, true,new int[2*degree],new TreeObject[2*degree-1]);
				currentOffset=root+offsetJump;
				diskWrite(theRoot);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}

	public BTree(int degree,int sequence, String fileName){
		this.degree=degree;
		this.fileName=fileName;
		offsetJump=13+(degree*2-1)*16;
		this.sequence=sequence;
		try {
			ByteBuffer.allocate(16);
			raf=new RandomAccessFile(fileName+".btree.data."+sequence+"."+degree,"rw");
			raf.setLength(4096);
			raf.writeInt(root);
			raf.writeInt(degree);
			raf.writeInt(height);
			raf.writeInt(sequence);
			root=(int) raf.getFilePointer();
			BTreeNode theRoot=new BTreeNode(root, 0, true,new int[2*degree],new TreeObject[2*degree-1]);
			currentOffset=root+offsetJump;
			diskWrite(theRoot);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * Constructor for creating tree from pre-made file
	 * Used for running GeneBankSearch
	 * 
	 * @param fileName	the name of the file that contains
	 * 					B-Tree data
	 * @throws FileNotFound 
	 */
	public BTree(String fileName) {
		try {
			raf = new RandomAccessFile(fileName, "r");
			this.root = raf.readInt();
			this.degree = raf.readInt(); // check to make sure this is right order
			this.height = raf.readInt();
			this.sequence = raf.readInt();
			offsetJump=13+(degree*2-1)*16;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public void test(int i){
		BTreeNode current=diskRead(i);
		System.out.println(current.toString());
		if(!current.isLeaf()){
			for(int j=0;j<current.n+1;j++) {
				test(current.children[j]);
			}
		}
	}

	public int getRoot() {
		return root;
	}

	public void setRoot(int root) {
		this.root = root;
	}

	public int getSequence(){
		return sequence;
	}
	
	/**
	 * Creates a file containing all the sequences with their frequency
	 *
	 */
	public void dumpTree(){
		File dump = new File("dump");
		FileWriter fw;
		BufferedWriter out;
			try {
				if(!dump.exists()){
					dump.createNewFile();
				}
				fw = new FileWriter(dump.getAbsolutePath());
				out = new BufferedWriter(fw);
				
				raf.seek(0);
				BTreeNode r = diskRead(raf.readInt()); // this needs another look
				out.write(traverse(r));
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public String traverse(BTreeNode node) {
		StringBuilder DNAandFreq = new StringBuilder();
		int k;
		
		for (k = 0; k < node.n; k++) {
			if (!node.isLeaf()) {
				DNAandFreq.append(traverse(diskRead(node.children[k])));
			}
			DNAandFreq.append(keyToString(node.objects[k].getKey()).toLowerCase()); 
			DNAandFreq.append(":\t" + node.objects[k].getFreq() + "\n");
			sum += node.objects[k].getFreq();
		}
		if (!node.isLeaf()) {
			DNAandFreq.append(traverse(diskRead(node.children[k])));
		}
//		System.out.println("SUM = " + sum);
		return DNAandFreq.toString();
	}
	
	public String keyToString(long DNA) {
		
		String stringSeq = Long.toBinaryString(DNA);
		if (stringSeq.length() % 2 != 0) {
			stringSeq = "0" + stringSeq;
		}
		int diff = sequence - stringSeq.length()/2;
		for (int i = 0; i < diff; i++) {
			stringSeq = "00" + stringSeq;
		}
//		System.out.println(stringSeq);
		String retDNA = "";
		
		for(int i = 0; i < stringSeq.length(); i+=2) {
			if ((stringSeq.charAt(i) == '0') && (stringSeq.charAt(i+1) == '0')) {
				retDNA =  retDNA + "A";
			} else if ((stringSeq.charAt(i) == '0') && (stringSeq.charAt(i+1) == '1')) {
				retDNA = retDNA + "C";
			} else if ((stringSeq.charAt(i) == '1') && (stringSeq.charAt(i+1) == '0')) {
				retDNA = retDNA + "G";
			} else if ((stringSeq.charAt(i) == '1') && (stringSeq.charAt(i+1) == '1')) {
				retDNA = retDNA + "T";
			}
		}
		
		return retDNA;
	}
	
	
	private TreeObject searches(int node,long key ){
		BTreeNode point=diskRead(node);
//		int index = point.searchInd(key, 0, point.n);
//		if(index<0) {
//			return null;
//		}
//		if(point.objects[index]!=null&&point.objects[index].getKey()==key) {
//			return point.objects[index];
//		}
		int i=0;
		while(i<point.n&&key>point.objects[i].getKey()) {
			i++;
		}
		if(i<point.n&&key==point.objects[i].getKey()) {
			return point.objects[i];
		}if(point.isLeaf()) {
			return null;
		}else {
			return searches(point.children[i],key);
		}
				
	}
	/**
	 * Searched for the specified key, returns null if no match is found.
	 * @param key
	 * @return
	 */
	public TreeObject search(long key){
		
	
		return searches(root, key);
	}	
	
	private BTreeNode nodeRead(int offset) {
		if(cache!=null) {
//			cache.moveObject();
		}
		return null;
		
	}

	private BTreeNode diskRead(int offset) {
		BTreeNode node = null;
		TreeObject[] objects;
		long key;
		int freq;
		boolean l=true;
		int[]children;
		try {
			ByteBuffer bb= ByteBuffer.allocate(offsetJump);
			byte[] data=new byte[offsetJump];
			raf.seek(offset);
			raf.read(data);
			bb.put(data);
			bb.flip();
			
			bb.getInt();
			if(bb.get()==0) {
				l=false;
			}
			int n=bb.getInt();
			objects= new TreeObject[2*degree-1];
			children= new int[2*degree];
			for(int i=0;i<n;i++) {
				children[i]=bb.getInt();
				key= bb.getLong();
				freq=bb.getInt();
				objects[i]=new TreeObject(key,freq);
			}
			children[n]=bb.getInt();
			node= new BTreeNode(offset,n,l,children,objects);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
		
	}
	
//	private void nodeWrite(BTreeNode node) {
//		if (cache!=null) {
//			cache.remove(node);
//			BTreeNode check =cache.addObject(node);
//			if(check!=null) {
//				diskWrite(check);
//			}
//		}else {
//			diskWrite(node);
//		}
//	}


	private void diskWrite(BTreeNode node) {
		try {
			ByteBuffer bb= ByteBuffer.allocate(offsetJump);
			byte bool=0;
			raf.seek(node.offset);
			bb.putInt(node.offset);
			if(node.isLeaf()) {
				bool=1;
			}
			bb.put(bool);
			bb.putInt(node.n);
			
			for(int i=0;i<node.n;i++) {
				bb.putInt(node.children[i]);
				bb.putLong(node.objects[i].getKey());
				bb.putInt(node.objects[i].getFreq());
			}
			bb.putInt(node.children[node.n]);
			bb.flip();
			byte[] data=new byte[bb.limit()];
			bb.get(data);
			raf.write(data);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Searches if the specified object is contained in the the Tree already and increased the frequency counter otherwise it inserts
	 * the new TreeOvject into the tree.
	 * @param key
	 * @throws Exception 
	 */
	public void insert(long key) throws Exception{
		boolean done=false;
		BTreeNode current=diskRead(root);
		BTreeNode next;
		int childIndex;
		int child;
		if(current.n==2*degree-1) {
			int[] temp=new int[2*degree];
			temp[0]=root;
			BTreeNode newRoot=new BTreeNode(currentOffset,0,false,temp,new TreeObject[2*degree-1]);
			root=currentOffset;
			currentOffset+=offsetJump;
			splitChild(newRoot,0,current);
			current=newRoot;
			height++;
			raf.seek(0);
			raf.writeInt(root);
			raf.writeInt(height);
		}
		while(!done) {
			childIndex=current.add(key);
			if(childIndex>-1) {
				child=current.children[childIndex];
				next=diskRead(child);
				if(next.n==2*degree-1){
					TreeObject mid =splitChild(current,childIndex,next);
					if(mid.getKey()<key) {
						diskWrite(next);
						next=diskRead(current.children[childIndex+1]);
					}
				}
				current=next;
			}else {
				done=true;
			}	
		}
	}
	

	private TreeObject splitChild(BTreeNode parent, int childIndex,BTreeNode child){
		TreeObject mid =child.objects[child.n/2];
		parent.addInner(mid);
		TreeObject[] objects = new TreeObject[2*degree-1];
		int j = 0;
		int[] children = new int[2*degree];
			for(int i= child.n/2+1;i<child.n;i++) {
				objects[j]=child.objects[i];
				children[j]=child.children[i];
				j++;
			}
		children[j]=child.children[child.n];
		BTreeNode newChild = new BTreeNode(currentOffset,degree-1,child.leaf,children,objects);
		child.n=degree-1;
		parent.addChild(childIndex+1, newChild.offset);
		diskWrite(newChild);
		diskWrite(child);
		currentOffset+=offsetJump;
		return mid;
	}
	////////////////////////////////////////////////
	/**
	 * 	BTree node inner class
	 */
	private class BTreeNode{

		public Boolean leaf;
		public int offset; // File offset to locate data

		public int n; // number of keys;
		
	
		public int[] children; // Array that hold the offset for each child index
		public  TreeObject[] objects;


		public BTreeNode(int offset,int n,boolean leaf,int[] children,TreeObject[] objects){
				this.offset=offset;
				this.n=n;
				this.children=children;
				this.objects=objects;
				this.leaf=leaf;
			
			
		}
		
		public int numChildren() {
			int c;
			if(leaf) {
				c=0;
			}else {
				c=n+1;
			}
			return c;
		}
		
		public void addChild(int childIndex, int newChild) {
			if(children[childIndex]!=0) {
				for(int i=numChildren()-1;i>childIndex;i--) {
					children[i]=children[i-1];
				}
			}
			children[childIndex]=newChild;
			diskWrite(this);
		}

		public void addInner(TreeObject object) {
			int search= searchInd(object.getKey(),0,n);
			if(n==0) {
				objects[search]=object;
				n++;
			}else if(objects[search]!=null&&objects[search].getKey()==object.getKey()) {
				objects[search].incrFreq();
			}else{
				if(search!=n) {
					for(int i=n;i>search;i--) {
						objects[i]=objects[i-1];
					}
				}
				objects[search]=object;
				n++;
			}
			diskWrite(this);
		}

		public int add(long key) throws Exception {
			int search= searchInd(key,0,n);
			if(n==0) {
				objects[0]=new TreeObject(key);
				n++;
				search=-1;
			}else if(objects[search]!=null&&objects[search].getKey()==key) {
				objects[search].incrFreq();
				search=-1;
			}else if(isLeaf()){
				for(int i=n;i>search;i--) {
						objects[i]=objects[i-1];
				}
				objects[search]=new TreeObject(key);
				search=-1;
				n++;
			}else if(objects[search]!=null&&objects[search].getKey()<key){
				search++;
			}
			diskWrite(this);
			if(search>-1&&children[search]==0) {
				throw new Exception();
			}
			
			return search;	
		}
		private boolean isLeaf() {
			return leaf==true;
		}

		public int searchInd(long key,int start, int end) {
//			int current= (end-start)/2;
//			if(n==0) {
//				current=0;
//			}else if(objects[end-1].getKey()<key) {
//				current=end;
//			}else if(start==end-1) {
//				current=start;
//			}else if(objects[current].getKey()<=key){
//				current=searchInd(key,current,end);
//			}else {
//				current=searchInd(key,start,current);
//			}
//			return current;
			int i=0;
			while(i<n&&key>objects[i].getKey()) {
				i++;
			}
			return i;
		}
		
		public TreeObject search(long key,int start, int end) {
			int current= (end-start)/2;
			TreeObject found;
			if(n==0) {
				return null;
			}else if(key==objects[current].getKey()) {
				return objects[current];
			}else if(start==end-1){
				return null;
			}else if(objects[current].getKey()>=key){
				found=search(key,current,end);
			}else {
				found=search(key,start,current);
			}
			return found;
		}
		
		public String toString(){
			StringBuilder string= new StringBuilder();
			string.append("isLeaf: "+leaf+" number of objects: "+n+"\n");
			for(int i=0;i<n;i++) {
				string.append(objects[i].toString()+"\n");
			}
			return string.toString();
		}
	}
}