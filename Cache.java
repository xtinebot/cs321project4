import java.util.LinkedList;
/**
 * This class is used to create a cache object.
 * @author keatongillihan
 * @param <T>
 */
public class Cache<T> {
	private LinkedList<T> list;
	private int size;
	/**
	 * the constructor for the Cache class.
	 * Creates a cache using a linked list
	 * @param size
	 */
	public Cache(int size) {
		list = new LinkedList<T>();
		this.size=size;
	}
	/**
	 * This method is used to search the cache for an object.
	 * After the search is finished the searched for object
	 * is moved to the front of the cache.
	 * @param object
	 * @return the matching object from the cache if it is found.
	 * If it is not found it returns null
	 */
	public T getObject(T object) {
		int index = list.indexOf(object);
		T found= null;
		if (index>=0) {
			found=list.remove(index);
			list.addFirst(found);
		}
		return found;
	}
	/**
	 * Adds an object to the front of the cache.
	 * If adding the object would cause the cache to become
	 * larger than size the last object is removed from the cache.
	 * @param object
	 */
	public T addObject(T object) {
		T it =null;
		if(list.size()==size){
			it= list.removeLast();
		}
		list.addFirst(object);
		return it;
	}
	/**
	 * removes an object from the cache.
	 * @param object
	 */
	public void removeObject(T object) {
		list.remove(object);
	}
	
	public T remove() {
		return list.removeFirst();
		
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	/**
	 * empties the cache.
	 */
	public void clearCache() {
		list.clear();
	}
}
