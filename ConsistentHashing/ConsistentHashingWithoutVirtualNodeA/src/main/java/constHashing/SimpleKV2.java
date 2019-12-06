package constHashing;
/**
 *
 * A simple kv interface.
 * Implementations should be backed by different versions of the
 * JDK's implementations available in JDK.
 * @author Valerio Schiavoni
 * Modified by Andres Felipe Dorado
 *
 */
public interface SimpleKV2<K, V> {
    V getLRU(K key, Integer hash);
    void putLRU(K key, V value, Integer hash);
}