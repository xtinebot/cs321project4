public class Cache {

		private LinkedList<BTreeNode> ll;
		private int cacheSize;

		/**
		 * creates a linkedlist object and sets the cache size.
		 * @param size
		 */
		public Cache(int size){
			ll = new LinkedList<BTreeNode>();
			cacheSize = size;
		}


		/**
		 * Searches through the cache and returns a reference to the object.
		 * returns null if no node is found. Move the node to the front of the list otherwise.
		 * @param str number to be found.
		 */
		public BTreeNode getObject(int  nodeOffset){
			ListIterator<BTreeNode> iter = ll.listIterator();
			while(iter.hasNext()){
				BTreeNode node = iter.next();
				if(node.offset == nodeOffset ){
					moveToFront(node);
					return node;
				}
			}
			return null;
		}

		/**
		 * Adds an BTreeNode to the cache
		 * @param num to be added.
		 */
		public void addObject(BTreeNode node){
			if(cacheSize == 0){
				return;
			}
			if(ll.size() < cacheSize){
				ll.addFirst(node);
			} else {
				ll.removeLast();
				ll.addFirst(node);
			}
		}

		/**
		 * Removes specified string.
		 * @param num to be removed.
		 */
		public void removeObject(BTreeNode node){
			if(cacheSize == 0){
				return;
			}
			ll.remove(node);
		}
		/**
		 * Takes a reference to an item in the list and moves it to the front.
		 * @param str to be moved to the front.
		 */
		public void moveToFront(BTreeNode node){
			if(cacheSize == 0){
				return;
			}
			ll.remove(node);
			ll.addFirst(node);
		}

		/**
		 * Clears the cache.
		 */
		public void clearCache(){

			ll.clear();
		}

		/**
		 * Prints a string that representsthe cache.
		 */
		public void PrintString(){
			if(cacheSize == 0){
				return;
			}
			ListIterator<BTreeNode> iter = ll.listIterator();

			while(iter.hasNext()){
				System.out.print(iter.next());
			}

			System.out.println();

		}
	}
