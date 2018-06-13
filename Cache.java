import java.util.LinkedList;

/**
 * A cache class
 * 
 * CS 321 Summer 2018
 * 
 * @author Christine Chang
 *
 */

public class Cache<T> extends LinkedList<T> {

	private static final long serialVersionUID = 1L;
	int desiredSize;
	
	/*
	 * Constructor
	 * 		creates cache of desired size
	 * 
	 * @ param size	desired size of cache
	 */
	public Cache(int size) {
		desiredSize = size;
		
		/* Create the empty cache */
		for(int i = 0; i < desiredSize; i++) {
			addLast(null);
		}
	}
	
	/**
	 * Adds object at top of cache, maintains desired size
	 * 
	 * @param element  the object to add to the cache
	 */
	public void addObject(T element) {
		addFirst(element);
		/* Ensure that desired size of cache is maintained */
		if (size() > desiredSize) {
			removeLast();
		}
		
	}
	
	/**
	 * Returns next object
	 * 
	 * @return  object at front of cache
	 */
	public T getObject() {
		return this.getObject();
	}
	
	/**
	 * Removes the desired object from cache
	 * 
	 * @param element  object to be removed
	 */
	public void removeObject(T element) {
		this.remove(element);
	}
	
	/**
	 * Moves desired object to top of cache
	 * 
	 * @param element  object to be moved
	 */
	public void moveObject(T element) {
		this.remove(element);
		this.addFirst(element);
	}
	
	/**
	 * Clears all objects from the cache
	 */
	public void clearCache() {
		for(int i = 0; i < getDesiredSize(); i++) {
			this.set(i, null);
		}
	}
	
	
	/** 
	 * Returns desired size of cache
	 * 
	 * @return  desired size of cache
	 */
	public int getDesiredSize() {
		return desiredSize;
	}
	
}
