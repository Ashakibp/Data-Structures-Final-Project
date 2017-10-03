import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;

import java.util.ArrayList;

/**
 * Created by Aaron on 4/20/17.
 */
public class Row <K, V> {
    ArrayList<Cell> data;
    int size;

    public Row() {
        data = new ArrayList<>();
        size = data.size();
    }

    public void set(Cell toAdd, int where) {
        data.set(where, toAdd);
    }

    public Cell getCell(int Column) {
        return data.get(Column);
    }

    public void add(V toAdd, int where) {
        data.add(where, new Cell<>(toAdd));
        return;
    }

    public void add(Cell c){
        data.add(c);
    }

    public void add(Cell c, int where) {
        data.add(where, c);
    }

    public void removeColumn(int index) {
        data.remove(index);
    }


    public boolean equals(Row r) {

        for (int i = 0; i < data.size(); i++) {
            Cell ourCell = data.get(i);
            Cell theirCell = (Cell) r.data.get(i);
            try {
                if (ourCell.compareTo(theirCell.getValue()) != 0) {
                    return false;
                }
                else {
                    continue;
                }
            }
            catch (Exception e){
                if(ourCell.getValue() == null && theirCell.getValue() == null){
                    continue;
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }
}
