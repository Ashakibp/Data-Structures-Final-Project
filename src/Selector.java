import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import java.util.*;

/**
 * Created by Aaron on 4/13/17.
 */
public class Selector<V>{
    private ColumnID[] columnNames;
    private boolean distinct;
    private Condition where;
    private Map<ColumnID,SelectQuery.FunctionInstance> functionMap;
    private SelectQuery.OrderBy[] orderBys;
    private SqlTable finalTable;

    public Selector(SelectQuery select, SqlTable table) {
        this.columnNames = select.getSelectedColumnNames();
        this.distinct = select.isDistinct();
        this.where = select.getWhereCondition();
        this.functionMap = select.getFunctionMap();
        this.orderBys = select.getOrderBys();
        this.finalTable = table;
    }

    public void run(){
        if(where != null) {
            finalTable.runConditions(where);
        }
        if(finalTable.getSize() == 0){
            return;
        }
        if(functionMap.size() == 0) {
            if(finalTable.getSize() > 1) {
                if (orderBys.length >= 1) {
                    doOrderBys(orderBys);
                }
            }
        }
        setupColumns();
        if(distinct){
            doDistinct();
        }
        if(functionMap.size() != 0){
            Row r = executeFunctions();
            finalTable.wipe();
            finalTable.add(r);
        }
    }

    /**
     * This is too eliminate all the columns not relevant to our select query
     */
    public void setupColumns(){
        //If we don't need to remove anything
        if(columnNames[0].getColumnName() == "*"){
            return;
        }
        ArrayList<ColumnDescription> descriptions = finalTable.getDescriptions();
        Iterator<ColumnDescription> iterator = descriptions.iterator();
        while(iterator.hasNext()){
            ColumnDescription cd = iterator.next();
            if(!columnExists(cd)){
                finalTable.removeColumn(cd.getColumnName());
                iterator.remove();
            }
        }
    }

    public boolean columnExists(ColumnDescription desc){
        ArrayList<ColumnID> ids =  new ArrayList<>(Arrays.asList(columnNames));
        for(ColumnID id : ids){
            if(desc.getColumnName().equals(id.getColumnName())){
                return true;
            }
        }
        return false;
    }

    public void doDistinct(){
    SqlTable x = new SqlTable(finalTable);
    x.wipe();
    ArrayList<Row> rows = finalTable.getTable();
    for(Row row : rows) {
        if (!x.contains(row)) {
            x.addRow(row);
        }
    }
    finalTable = x;
    }

    public SqlTable doDistinctByColumn(int columnName){
        SqlTable start = new SqlTable(finalTable);
        start.wipe();
        ArrayList<Row> rows = (ArrayList<Row>) finalTable.getTable().clone();
        for(Row row : rows) {
            if (!start.contains(columnName, row.getCell(columnName))) {
                start.addRow(row);
            }
        }
        return start;
    }

    //First we do the first order by, then we divide into tiers
    public void doOrderBys(SelectQuery.OrderBy[] orderBys){
        int x = 0;
        SelectQuery.OrderBy first = orderBys[x];
        sortByColumn(first.getColumnID(), first.isAscending(), 0,finalTable.getSize());
        ArrayList<PairValue> tiers = finalTable.splitToTiers(finalTable.getColumnIndex(first.getColumnID().getColumnName()),finalTable.getTable());
        if(orderBys.length >= 1) {
            for (x = 1; x < orderBys.length; x++) {
            for(PairValue tier : tiers){
                int start = (int) tier.getKey();
                int end = (int) tier.getValue();
                    sortByColumn(orderBys[x].getColumnID(), orderBys[x].isAscending(), start, end);
            }
            tiers = finalTable.splitToTiers(finalTable.getColumnIndex(orderBys[x].getColumnID().getColumnName()),finalTable.getTable());
            }
        }
    }

    //This is extremely memory intensive. I will find a better way of doing this if I can.
    public void sortByColumn(ColumnID column, Boolean isAsc, int from, int till){
        ArrayList table = finalTable.getTable();
        int range = till;
        int start = from;
        int columnIndex = finalTable.getColumnIndex(column.getColumnName());
        while (start < range){
            int rowIndex = 0;
        if(isAsc){
            rowIndex = finalTable.getMaxInRange(columnIndex, start, range, true);
        }
        else{
            rowIndex = finalTable.getMinInRange(columnIndex, start, range, true);
        }
        Collections.swap(table, rowIndex, start);
        start ++;
        }
    }

    public SqlTable getFinalTable() {
        return finalTable;
    }

    /**
     * Will create a row with all the function results.
     * @return
     */
    public Row executeFunctions(){
        Row r = new Row();
        for(ColumnID pair : columnNames) {
            if (functionMap.keySet().contains(pair)) {
                SqlTable workTable = new SqlTable(finalTable);
                SelectQuery.FunctionInstance toDo = functionMap.get(pair);
                int index = workTable.getColumnIndex(pair.getColumnName());
                if(toDo.isDistinct){
                    workTable = doDistinctByColumn(index);
                }

                if (toDo.function == SelectQuery.FunctionName.AVG) {
                    r.add(new Cell(workTable.getAverage(index)));
                }
                else if (toDo.function == SelectQuery.FunctionName.MAX) {
                    int functionIndex = (workTable.getMaxInRange(index, 0, workTable.getSize(), false));
                    r.add(new Cell(workTable.getRow(functionIndex).getCell(index).getValue()));
                }
                else if (toDo.function == SelectQuery.FunctionName.MIN) {
                    int functionIndex = (workTable.getMinInRange(index, 0, workTable.getSize(), false));
                    r.add(new Cell(workTable.getRow(functionIndex).getCell(index).getValue()));
                }
                else if (toDo.function == SelectQuery.FunctionName.COUNT){
                    r.add(new Cell(workTable.getSize()));
                }
                else if(toDo.function == SelectQuery.FunctionName.SUM){
                    r.add(new Cell(workTable.getSum(index)));
                }
            }
        }
        return r;
    }
}
