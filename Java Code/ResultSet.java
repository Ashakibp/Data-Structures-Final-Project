import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;

import java.util.ArrayList;

/**
 * The is the capsule in which the API lets the user know if a process went through or not, or returns selected values
 *
 * Created by Aaron on 4/19/17.
 */
public class ResultSet {
    private ArrayList<ColumnDescription> descriptions;
    private ArrayList<Row> Table;

     ResultSet(Selector s) {
        descriptions = s.getFinalTable().getDescriptions();
        Table = (ArrayList<Row>) s.getFinalTable().getTable().clone();
    }
     ResultSet(Boolean wasAccepted){
        Table = new ArrayList<>();
        Row r = new Row();
        if(wasAccepted){
            r.add(new Cell(true));
            Table.add(r);
        }
        else{
            r.add(new Cell(false));
        }
        Table.add(r);
    }

     ResultSet(Boolean wasAccepted, Exception e){
        Table = new ArrayList<>();
        Row r = new Row();
        if(wasAccepted){
            r.add(new Cell(true));
            Table.add(r);
        }
        else{
            r.add(new Cell(false));
            r.add(new Cell(e.getMessage()));
        }
        Table.add(r);
    }

     ResultSet(SqlTable table){
         Table = new ArrayList<>();
         descriptions = table.getDescriptions();
     }

    /**
     * For debugging
     * @return
     */
    @Override
    public String toString(){
        String s = " ";
        if(descriptions != null){
            for(ColumnDescription desc : descriptions){
                s += desc.getColumnName() + " ";
            }
            s += "\n";
        }
        for(Row r : Table){
            for(Object c : r.data){
                Cell cell = (Cell) c;
                if(cell.getValue() != null){
                    s += cell.getValue() + " ";
                }
                else{
                    s += "NULL" + " ";
                }
            }
            s += " \n";
        }
        return s;
    }

    public ArrayList<ColumnDescription> getDescriptions() {
        return descriptions;
    }

    public ArrayList<Row> getTable() {
        return Table;
    }
}
