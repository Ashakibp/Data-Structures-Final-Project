import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Aaron on 4/20/17.
 */
public class PairValue <K, V> implements Comparable<K>{
    K key;
    private V value;

    public PairValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull K o) {
        if (value instanceof Integer) {
            Integer ourVal = (Integer) value;
            Integer otherVal = (Integer) o;
            return ourVal.compareTo(otherVal);
        }
        else if (value instanceof String) {
            String ourVal = (String) value;
            String otherVal = (String) o;
            return ourVal.compareTo(otherVal);
        }
        else if (value instanceof Double) {
            Double ourVal = (Double) value;
            Double otherVal = (Double) o;
            return ourVal.compareTo(otherVal);
        }
        return 0;
    }


}
