import org.jetbrains.annotations.NotNull;

/**
 * Created by Aaron on 4/20/17.
 */
public class Cell <V, T> implements Comparable<T> {
    V value;

    public Cell(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull T o) {
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
