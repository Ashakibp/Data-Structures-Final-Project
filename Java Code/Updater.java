import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Aaron on 5/14/17.
 */
public class Updater {
    HashSet<Row> rowsToUpdate;
    SqlTable table;
    Condition where;
    ArrayList<PairValue> valPairs;

    public Updater(SqlTable table, UpdateQuery updateData) {
        this.table = table;
        this.where = updateData.getWhereCondition();
        this.valPairs = new ArrayList<>();
        this.rowsToUpdate = new HashSet<>();
        parseValues(updateData.getColumnValuePairs());
    }

    void run(){
        if(where == null){
            rowsToUpdate.addAll(table.getTable());
        }
        else {
            rowsToUpdate = table.runConditions(rowsToUpdate, where);
        }
        for(PairValue pairValue : valPairs){
            checkConstraints(pairValue);
        }
        swapToIndex();
        inputValues();
    }

    void parseValues(ColumnValuePair[] pairs){
        for(ColumnValuePair pair : pairs){
            ColumnDescription desc = table.getColumn(pair.getColumnID().getColumnName());
            //For now, we let null values pass through and catch them later on, if the parser gets a null value it could
            //Be problematic.
            if(pair.getValue().equals(null) || pair.getValue().toLowerCase().equals("null") || pair.getValue().equals("")){
                PairValue pairValue = new PairValue(desc, null);
                valPairs.add(pairValue);
                continue;
            }
            PairValue pairValue = new PairValue(desc, table.checkAndCast(pair.getValue(), desc));
            valPairs.add(pairValue);
        }
    }

    boolean checkConstraints(PairValue pair){
        ColumnDescription desc = (ColumnDescription) pair.getKey();
        if(desc.isUnique() || table.getPrimaryKeyColumn().getColumnName().equals(desc.getColumnName())){
            //If this column can only take unique values and we're trying to add that to more than one row,
            //Throw an exception
            if(rowsToUpdate.size() > 1){
                throw new IllegalArgumentException(desc.getColumnName() + " is Unique, and several rows cannot be updated with the same value");
            }
            //Then we check if this column
            else if(!table.checkUnique(pair.getValue(), desc)){
                throw new IllegalArgumentException(desc.getColumnName() + " is unique and already contains this value");
            }
        }
        if(pair.getValue() == null){
            if(desc.isNotNull()){
                return true;
            }
            else{
                return false;
            }
        }
        if(!table.checkConstraints(pair.getValue(), desc)){
            throw new IllegalArgumentException (desc.getColumnName() + " does not meet constraints with value: " + pair.getValue().toString());
        }
        return true;
    }

    void swapToIndex(){
        for(PairValue pair : valPairs){
            ColumnDescription desc = (ColumnDescription) pair.getKey();
            int x = table.getColumnIndex(desc.getColumnName());
            if(x == - 1){
                throw new IllegalArgumentException("Column " + desc.getColumnName() + " does not exist!");
            }
            else{
                pair.key = x;
            }
        }
    }

    void inputValues(){
        for(Row r : rowsToUpdate){
            table.removeFromIndices(r);
            for(PairValue pair : valPairs){
                r.getCell((Integer) pair.getKey()).value = pair.getValue();
            }
            table.updateIndices(r);
        }
    }




}
