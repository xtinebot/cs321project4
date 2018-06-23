import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
/**
 * BTree Class
 * @author KeatonGillihan
 *
 */

public class BTree {

	private int root;  // root of the BTree
	private int degree = 1; // degree of BTree
	private int height = 0;
	private int currentOffset;
	private Cache<BTreeNode> cache;
	private int sequence;
	private RandomAccessFile raf;
	private int offsetJump;
	private BTreeNode r;
/**
 * constructs BTree with Cache
 * @param degree
 * @param cacheSize
 * @param sequence
 * @param fileName
 */
	public BTree(int degree,int cacheSize,int sequence, String fileName){
			ByteBuffer bb= ByteBuffer.allocate(16);
			this.degree=degree;
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
				r=new BTreeNode(root, 0, true,new int[2*degree],new TreeObject[2*degree-1]);
				currentOffset=root+offsetJump;
				nodeWrite(r);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	/**
	 * constructs BTree without cache
	 * @param degree
	 * @param sequence
	 * @param fileName
	 */
	public BTree(int degree,int sequence, String fileName){
		this.degree=degree;
		offsetJump=13+(degree*2-1)*16;
		this.sequence=sequence;
		try {
			ByteBuffer.allocate(16);
			raf=new RandomAccessFile(fileName+".btree.data."+sequence+"."+degree,"rw");
			raf.setLength(4096);
			raf.writeInt(root);
			raf.writeInt(height);
			raf.writeInt(degree);
			raf.writeInt(sequence);
			root=(int) raf.getFilePointer();
			r=new BTreeNode(root, 0, true,new int[2*degree],new TreeObject[2*degree-1]);
			currentOffset=root+offsetJump;
			nodeWrite(r);
			
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
			raf.seek(0);
			this.root = raf.readInt();
			this.height = raf.readInt(); // check to make sure this is right order
			this.degree = raf.readInt();
			this.sequence = raf.readInt();
			offsetJump=13+(degree*2-1)*16;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * @return location of root in file
	 */
	public int getRoot() {
		return root;
	}
	
	/**
	 *sets location of root in file
	 * @param root
	 */
	public void setRoot(int root) {
		this.root = root;
	}
	/**
	 * @return length of sequence
	 */
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
				DNAandFreq.append(traverse(nodeRead(node.children[k])));
			}
			DNAandFreq.append(keyToString(node.objects[k].getKey()).toLowerCase()); 
			DNAandFreq.append(": " + node.objects[k].getFreq() + "\n");
		}
		if (!node.isLeaf()) {
			DNAandFreq.append(traverse(nodeRead(node.children[k])));
		}
		return DNAandFreq.toString();
	}
	/**
	 * converts long key to DNA string
	 * @param DNA
	 * @return
	 */
	public String keyToString(long DNA) {
		
		String stringSeq = Long.toBinaryString(DNA);
		if (stringSeq.length() % 2 != 0) {
			stringSeq = "0" + stringSeq;
		}
		int diff = sequence - stringSeq.length()/2;
		for (int i = 0; i < diff; i++) {
			stringSeq = "00" + stringSeq;
		}
		String retDNA = "";
		
		for(int i = 0; i < stringSeq.length(); i+=2) {
			if ((stringSeq.charAt(i) == '0') && (stringSeq.charAt(i+1) == '0')) {
				retDNA =  retDNA + "a";
			} else if ((stringSeq.charAt(i) == '0') && (stringSeq.charAt(i+1) == '1')) {
				retDNA = retDNA + "c";
			} else if ((stringSeq.charAt(i) == '1') && (stringSeq.charAt(i+1) == '0')) {
				retDNA = retDNA + "g";
			} else if ((stringSeq.charAt(i) == '1') && (stringSeq.charAt(i+1) == '1')) {
				retDNA = retDNA + "t";
			}
		}
		
		return retDNA;
	}
	/**
	 * searches for object
	 * @param node
	 * @param key
	 * @return
	 */
	private TreeObject searches(int node,long key ){
		BTreeNode point=nodeRead(node);
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
	/**
	 * reads node from cache or file
	 * @param offset
	 * @return
	 */
	private BTreeNode nodeRead(int offset) {
		if(cache!=null) {
			BTreeNode get=new BTreeNode(offset);
			get=cache.getObject(get);
			if(get==null) {
				get=diskRead(offset);
				nodeWrite(get);
			}
			return get;
		}else {
			return diskRead(offset);
		}
		
		
	}
	/**
	 * reads node from file
	 * @param offset
	 * @return
	 */
	private BTreeNode diskRead(int offset) {
		BTreeNode node = null;
		TreeObject[] objects;
		long key;
		int freq;
		boolean l=true;
		int[]children;
		try {
			ByteBuffer bb= ByteBuffer.allocate(offsetJump*2);
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
	/**
	 * write node to cache or file
	 * @param node
	 */
	private void nodeWrite(BTreeNode node) {
		if (cache!=null) {
			cache.removeObject(node);
			BTreeNode check =cache.addObject(node);
			if(check!=null) {
				diskWrite(check);
			}
		}else {
			diskWrite(node);
		}
	}
	/**
	 * write node to file 
	 * @param node
	 */
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
	 * the new TreeObject into the tree.
	 * @param key
	 */
	public void insert(long key){
		boolean done=false;
		BTreeNode current=r;
		BTreeNode next;
		int childIndex;
		int child;
		if(current.n==2*degree-1) {
			int[] temp=new int[2*degree];
			temp[0]=root;
			diskWrite(r);
			r=new BTreeNode(currentOffset,0,false,temp,new TreeObject[2*degree-1]);
			root=currentOffset;
			currentOffset+=offsetJump;
			splitChild(r,0,current);
			current=r;
			height++;
			try {
				raf.seek(0);
				raf.writeInt(root);
				raf.writeInt(height);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(!done) {
			childIndex=current.add(key);
			if(childIndex>-1) {
				child=current.children[childIndex];
				next=nodeRead(child);
				if(next.n==2*degree-1){
					TreeObject mid =splitChild(current,childIndex,next);
					
					if(mid.getKey()<key) {
						nodeWrite(next);
						next=nodeRead(current.children[childIndex+1]);
					}else if(mid.getKey()==key){
						next=current;
					}
				}
				current=next;
			}else {
				done=true;
			}	
		}
	}
	/**
	 * splits the child node and inserts the middle object into the parent node
	 * @param parent
	 * @param childIndex
	 * @param child
	 * @return
	 */
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
		nodeWrite(newChild);
		nodeWrite(child);
		currentOffset+=offsetJump;
		return mid;
	}
	/**
	 * @return string
	 */
	public String toString() {
		return "root="+root+"degree="+degree;
		
	}
	/**
	 * dumps cache to file
	 */
	public void finish() {
		if(cache!=null) {
			while(!cache.isEmpty()) {
				diskWrite(cache.remove());
			}
		}
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

		/**
		 * constructs a BTreeNode
		 * @param offset
		 * @param n
		 * @param leaf
		 * @param children
		 * @param objects
		 */
		public BTreeNode(int offset,int n,boolean leaf,int[] children,TreeObject[] objects){
				this.offset=offset;
				this.n=n;
				this.children=children;
				this.objects=objects;
				this.leaf=leaf;
			
			
		}
		/**
		 * constructs an empty BTree Node
		 * @param offset
		 */
		public BTreeNode(int offset){
			this.offset=offset;
		}
		/**
		 * @return number of children
		 */
		public int numChildren() {
			int c;
			if(leaf) {
				c=0;
			}else {
				c=n+1;
			}
			return c;
		}
		/**
		 * inserts child into a node
		 * @param childIndex
		 * @param newChild
		 */
		public void addChild(int childIndex, int newChild) {
			if(children[childIndex]!=0) {
				for(int i=numChildren()-1;i>childIndex;i--) {
					children[i]=children[i-1];
				}
			}
			children[childIndex]=newChild;
			nodeWrite(this);
		}
		/**
		 * adds an object to a non leaf node
		 * @param object
		 */
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
			nodeWrite(this);
		}
		/**
		 * adds an object to a leaf
		 * @param key
		 * @return if not a leaf returns index containing the appropriate child pointer
		 */
		public int add(long key){
			int search= searchInd(key,0,n);
			if(n==0) {
				objects[0]=new TreeObject(key);
				n++;
				search=-1;
				nodeWrite(this);
			}else if(objects[search]!=null&&objects[search].getKey()==key) {
				objects[search].incrFreq();
				search=-1;
				nodeWrite(this);
			}else if(isLeaf()){
				for(int i=n;i>search;i--) {
						objects[i]=objects[i-1];
				}
				objects[search]=new TreeObject(key);
				search=-1;
				n++;
				nodeWrite(this);
			}else if(objects[search]!=null&&objects[search].getKey()<key){
				search++;
			}
			return search;	
		}
		/**
		 * @return value of leaf boolean
		 */
		private boolean isLeaf() {
			return leaf==true;
		}
		/**
		 * searches for where an object should be inserted
		 * @param key
		 * @param start
		 * @param end
		 * @return
		 */
		public int searchInd(long key,int start, int end) {
			int mid=(end+start)/2;
			if(start==end) {
				return 0;
			}else if(objects[mid].getKey()==key) {
				return mid;
			}
				else if(objects[end-1].getKey()<key) {
				return end;
			}else if(objects[mid].getKey()<=key){
				mid=searchInd(key,mid,end);
			}else {
				mid=searchInd(key,start,mid);
			}
			
			return mid;
		}
		/**
		 * @return String
		 */
		public String toString(){
			StringBuilder string= new StringBuilder();
			string.append("isLeaf: "+leaf+" number of objects: "+n+"\n");
			for(int i=0;i<n;i++) {
				string.append(objects[i].toString()+"\n");
			}
			return string.toString();
		}
		/**
		 * Overrides Object.equals
		 */
		public boolean equals(Object i) {
			BTreeNode k=(BTreeNode) i;
			return k.offset==this.offset;
			
		}
	}
}