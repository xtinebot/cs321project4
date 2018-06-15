import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

public class BTree {
	private BTreeNode root;  // root of the BTree
	private int degree = 1; // degree of BTree
	private int nodes = 1;
	private String fileName;
	private File data;
	private int currentOffset;
	private Cache cache;
	private int sequence;
	private ByteBuffer buffer;
	private RandomAccessFile raf;

	private int blockSize = 4096;

	public BTree(int degree,int cacheSize,int sequence, String fileName){
		try {
			raf = new RandomAccessFile(fileName+".btree.data"+sequence+degree,"rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		root = new BTreeNode(0);
		
	}

	public BTree(int cacheSize, File file){
		
		

	}

	public void test(){
		
	}

	public int getSequence(){
		return sequence;
	}
	/**
	 * Creates a file containing all the sequences with their frequency
	 *
	 */
	public void dumpTree(){
		
	}
	/**
	 * Converts a string to a key
	 * @param word
	 * @return
	 */
	public long strToKey(String word){
		return blockSize;
		
	}

	public String keyToStr(long key){
		return fileName;
		
	}

	private TreeObject searches(BTreeNode root,long key ){
		return null;
		
	}
	/**
	 * Searched for the specified key, returns null if no match is found.
	 * @param key
	 * @return
	 */
	public TreeObject search(long key){
		
	
		return searches(root, key);
	}

	private BTreeNode diskRead(int offset) {
		return root;
		
	}


	private void diskWrite(BTreeNode node) {
		
	}
	/**
	 * Creates a new node and writes it to disk.
	 * @return
	 */
	private BTreeNode allocateNode(){
		return root;
		
		
	}

	/**
	 * Searches if the specified object is contained in the the Tree already and increased the frequency counter otherwise it inserts
	 * the new TreeOvject into the tree.
	 * @param key
	 */
	public void insert(long key){
		BTreeNode current = root;
		int child;
		if(current.isFull()) {
			long temp = split(current);
			current=diskRead(current.getParent());
			current.add(temp);
		}
		child = current.add(key);
		if(child>0) {
			diskWrite(current);
			current= diskRead(child);
		}
	}

	private void insertNonFull(BTreeNode x, TreeObject obj) {
		

	}

	private void splitChild(BTreeNode x, int i,BTreeNode y){
		
	}
	private long split(BTreeNode node) {
		long middle = 0;
		try {
			raf.seek(node.getOffset());
			raf.writeInt(node.getOffset());
			raf.writeInt(node.getParent());
			raf.writeInt(node.getM()/2);
			raf.writeInt(node.getN()/2);
			for(int i=0;i<node.getMaxN()/2;i++) {
				raf.writeInt(node.getChildren()[i]);
				raf.writeLong(node.getObjects().remove().getKey());
			}
			raf.writeInt(node.getChildren()[node.getN()]);
			
			middle=node.getObjects().remove().getKey();
			
			raf.seek(currentOffset);
			raf.writeInt(currentOffset);
			raf.writeInt(node.getParent());
			raf.writeInt(node.getM()/2);
			raf.writeInt(node.getN()/2);
			for(int i=node.getMaxN()/2+1;i<node.getMaxN();i++) {
				raf.writeInt(node.getChildren()[i]);
				raf.writeLong(node.getObjects().remove().getKey());
			}
			raf.writeInt(node.getChildren()[node.getN()]);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return middle;
	}
	////////////////////////////////////////////////
	/**
	 * 	BTree node inner class
	 */
	private class BTreeNode{

		public Boolean leaf;
		private int parent; //File Offset for parent;
		private int offset; // File offset to locate date

		private int m; //number of children
		private int n; // number of keys;
		private int maxN;
	
		private int[] children; // Array that hold the offset for each child index
		public LinkedList<TreeObject> objects;


		public BTreeNode(int parent){
			maxN= 2*degree-1;
			setChildren(new int[2*degree]);
		}
		
		public Boolean getLeaf() {
			return leaf;
		}

		public void setLeaf(Boolean leaf) {
			this.leaf = leaf;
		}

		public int getM() {
			return m;
		}

		public void setM(int m) {
			this.m = m;
		}

		public int getN() {
			return n;
		}

		public void setN(int n) {
			this.n = n;
		}

		public int getMaxN() {
			return maxN;
		}

		public void setMaxN(int maxN) {
			this.maxN = maxN;
		}

		public LinkedList<TreeObject> getObjects() {
			return objects;
		}

		public void setObjects(LinkedList<TreeObject> objects) {
			this.objects = objects;
		}

		public BTreeNode(int parent,LinkedList<TreeObject> objects, int[] children) {
			
		}
		
		public boolean isLeaf() {
			return m==0;
		}
		
		public int add(long key){
			TreeObject check = null;
			boolean stop = false;
			int child=-1;
			ListIterator<TreeObject> lit = objects.listIterator();
			while(lit.hasNext()&&!stop) {
				check=lit.next();
				if(check.getKey()>key) {
					stop=true;
				}
			}
			if(check.getKey()==key){
				check.incrFreq();
			}else if(isLeaf()) {
				TreeObject object = new TreeObject(key);
				lit.previous();
				lit.add(object);
			}else {
				child= children[lit.previousIndex()];
			}
			return child;
		}

		public int[] getChildren() {
			return children;
		}

		public void setChildren(int[] children) {
			this.children = children;
		}

		public int getParent() {
			return parent;
		}

		public void setParent(int parent) {
			this.parent = parent;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}
		
		public Boolean isFull() {
			return maxN==n;
		}
		

	}
}
