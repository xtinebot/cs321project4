import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class BTree {
	private final static byte A = 0;
	private final static byte T = 3;
	private final static byte C = 1;
	private final static byte G = 2;

	private int root;  // root of the BTree
	private int degree = 1; // degree of BTree
	private int height = 0;
	private String fileName;
	private File data;
	private int currentOffset;
	private Cache<BTreeNode> cache;
	private int sequence;
	private RandomAccessFile raf;
	private ByteBuffer bb;
	private int offsetJump;
	

	private int blockSize = 4096;

	public BTree(int degree,int cacheSize,int sequence, String fileName){
			this.degree=degree;
			this.fileName=fileName;
			offsetJump=13+(degree*2-1)*16;
			this.cache=new Cache<BTreeNode>(cacheSize);
			this.sequence=sequence;
			try {
				raf=new RandomAccessFile(fileName+".btree.data"+sequence+degree,"rw");
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

	public BTree(int degree,int sequence, String fileName){
		this.degree=degree;
		this.fileName=fileName;
		offsetJump=13+(degree*2-1)*16;
		this.sequence=sequence;
		try {
			raf=new RandomAccessFile(fileName+".btree.data"+sequence+degree,"rw");
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

	private BTreeNode diskRead(int offset) {
		BTreeNode node = null;
		TreeObject[] objects;
		long key;
		int freq;
		int[]children;
		try {
			raf.seek(offset);
			raf.readInt();
			boolean l=raf.readBoolean();
			int n=raf.readInt();
			objects= new TreeObject[2*degree-1];
			children= new int[2*degree];
			for(int i=0;i<n;i++) {
				children[i]=raf.readInt();
				key= raf.readLong();
				freq=raf.readInt();
				objects[i]=new TreeObject(key,freq);
			}
			children[n]=raf.readInt();
			node= new BTreeNode(offset,n,l,children,objects);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
		
	}
	
	private void nodeWrite(BTreeNode node) {
		if (cache!=null) {
			BTreeNode check =cache.addObject(node);
			if(check!=null) {
				diskWrite(check);
			}
		}
	}


	private void diskWrite(BTreeNode node) {
		try {
			raf.seek(node.offset);
			raf.writeInt(node.offset);
			raf.writeBoolean(node.isLeaf());
			raf.writeInt(node.n);
			
			for(int i=0;i<node.n;i++) {
				raf.writeInt(node.children[i]);
				raf.writeLong(node.objects[i].getKey());
				raf.writeInt(node.objects[i].getFreq());
			}
			raf.writeInt(node.children[node.n]);
			
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
		public int offset; // File offset to locate date

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