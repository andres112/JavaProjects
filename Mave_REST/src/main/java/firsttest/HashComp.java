package firsttest;

import java.util.Comparator;

public class HashComp implements Comparator<Key> {
    @Override
    public int compare(Key key1, Key key2) {
        return key1.getId().compareTo(key2.getId());
    }
}
