package firsttest;
/**
 * 
 * A simple kv interface. 
 * Implementations should be backed by different versions of the 
 * JDK's implementations available in JDK. 
 * @author Valerio Schiavoni
 * Modified by Andres Felipe Dorado
 *
 */
public interface SimpleKV {
	public void put(Object k, Object v, Object lru);
	public Object get(Object k);
        public Object remove(Object k);
        public Object getAll();
        public int getSize();
        public boolean containsKey(Object k);

        // Remove methods when MAX length is achieved
        public void removeMax_Random();
        public void removeMax_LRU();
}