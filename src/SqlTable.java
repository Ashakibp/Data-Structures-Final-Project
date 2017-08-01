import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import org.jetbrains.annotations.Nullable;
import java.util.*;

/**
 * Created by Aaron on 4/20/17.
 * There are multiple selectors, one for creation, and one for doing a deep clone.
 *
 * to do - fix double cloning - cloning once in constuctor and once after cloning due to
 * bug when selecting from tree. Stop cloning in constructor and do it only in select unless where
 * is null then do it in constructor.
 *
 * Table is built of Rows, which are made up of cells.
 *
 */
public class SqlTable <V> {

    private ArrayList<Row> Table;
    private ArrayList<ColumnDescription> descriptions;
    private String name;
    private ColumnDescription primaryKeyColumn;
    private int width;
    private ArrayList<BTree> treeTable;
    private PairValue[] operations;


     SqlTable(CreateTableQuery n) {
        this.Table = new ArrayList<>();
        this.descriptions = new ArrayList<>(Arrays.asList(n.getColumnDescriptions()));
        this.width = descriptions.size();
        this.name = n.getTableName();
        this.primaryKeyColumn = n.getPrimaryKeyColumn();
        this.operations = new PairValue[width];
        this.treeTable = new ArrayList<>();
        addPrimaryKey();
    }
     SqlTable(SqlTable tableToGet) {
        this.descriptions = (ArrayList<ColumnDescription>) tableToGet.getDescriptions().clone();
        this.name = tableToGet.getName();
        this.primaryKeyColumn = tableToGet.getPrimaryKeyColumn();
        this.width = tableToGet.getWidth();
        this.Table = (ArrayList<Row>) tableToGet.cloneTable();
        this.treeTable = (ArrayList<BTree>) tableToGet.getTreeTable().clone();
    }
    /**
     * Returns the holder of the BTree
     * @return
     */
    public ArrayList<BTree> getTreeTable() {
        return treeTable;
    }

    /**
     * Makes sure every columnID in the column value pairs coresponds to a column
     * @param info
     * @param columnName
     * @return
     */
     ColumnValuePair findPair(ColumnValuePair[] info, String columnName) {
        for (ColumnValuePair pair : info) {
            if (pair.getColumnID().getColumnName().equals(columnName)) {
                return pair;
            }
        }
        return null;
    }

    /**
     * Follows ACID
     * Essentially checks through everything a variety of times
     * puts it all into PairValues which are then assesed one more
     * time before added to the table.
     * @param infoArray
     * @throws IllegalArgumentException
     */
     void add(ColumnValuePair[] infoArray) throws IllegalArgumentException {
        //First thing we check is that every ColumnValue pair matches a column in the database.
        //Actually other way around
        for (ColumnDescription toAdd : descriptions) {
            ColumnValuePair pair = findPair(infoArray, toAdd.getColumnName());
            int columnIndex = descriptions.indexOf(toAdd);
            //Meaning it was not included, first we check default, then we check null, then we throw exceptions
            if (pair == null) {
                if (toAdd.getHasDefault() && !toAdd.equals(primaryKeyColumn)) {
                    operations[columnIndex] = (new PairValue(columnIndex, checkAndCast(toAdd.getDefaultValue(), toAdd)));
                    continue;
                } else if (toAdd.isNotNull() && !toAdd.equals(primaryKeyColumn)) {
                    operations[columnIndex] = (new PairValue(columnIndex, null));
                    continue;
                } else {
                    operations = new PairValue[width];
                    throw new IllegalArgumentException("Error with null/default queries with column " + toAdd.getColumnName() + getName());
                }
            }
            V AddValue = checkAndCast(pair.getValue(), toAdd);
            //Honestly I wasn't sure how this would come out
            if (AddValue.toString().toLowerCase().equals("'null'") || AddValue == null) {
                if (!toAdd.isNotNull() && !toAdd.equals(primaryKeyColumn)) {
                    operations[columnIndex] = (new PairValue(columnIndex, null));
                    continue;
                } else {
                    throw new IllegalArgumentException("Column " + toAdd.getColumnName() + " can not be NULL");
                }
            }

            if (!checkConstraints(AddValue, toAdd)) {
                operations = new PairValue[width];
                throw new IllegalArgumentException("Value for column " + pair.getColumnID().getColumnName() + " does not meet constraints");
            }
            if (!checkUnique(AddValue, toAdd)) {
                operations = new PairValue[width];
                throw new IllegalArgumentException("Value for column " + pair.getColumnID().getColumnName() + " is not unique and must be unique");
            }
            operations[columnIndex] = (new PairValue(columnIndex, AddValue));

            if (toAdd == null) {
                operations = new PairValue[width];
                throw new IllegalArgumentException("Column " + pair.getColumnID().getColumnName() + " Does not exist");
            }
        }
        addOperations();
    }

    /**
     * Returns a column via index number
     * @param index
     * @return
     */
     ColumnDescription getColumn(int index){
        return descriptions.get(index);
    }


    /**
     * Once we have added all the pair values to a single data structure and checked them all,
     * this method finishes the process and calls on the method to update the BTrees
     * Of course we do one last check to make sure there are enough pairValues to make a complete row
     */
    private void addOperations() {
        if (operations.length == width) {
            Row toAdd = new Row();
            for (PairValue p : operations) {
                toAdd.add(p.getValue(), (Integer) p.getKey());
            }
            Table.add(toAdd);
            operations = new PairValue[width];
            updateIndices(toAdd);
        } else {
            throw new IllegalArgumentException("Missing values for table " + getName());
        }
    }

    /**
     *Returns a tree from the arraylist based on the Column Name
     * @param columnName
     * @return
     */
     BTree getTree(String columnName){
        for(BTree tree : treeTable){
            if(tree.getName().equals(columnName)){
                return tree;
            }
        }
        return null;
    }

    /**
     * Updates all the Btrees, this is when a new value has been added to the database
     */
     void updateIndices(Row toAdd){
        Row r = toAdd;
        for(ColumnDescription column : descriptions){
            BTree t = getTree(column.getColumnName());
            if(t != null){
                V value = (V) r.getCell(getColumnIndex(column.getColumnName())).getValue();
                t.put(value, r);
            }
        }
    }

    /**
     * Updates all the Btrees, this is when a row has been updated/removed in the database
     */
     void removeFromIndices(Row toRemove){
        Row r = toRemove;
        for(ColumnDescription column : descriptions){
            BTree t = getTree(column.getColumnName());
            if(t != null){
                V value = (V) r.getCell(getColumnIndex(column.getColumnName())).getValue();
                t.remove(value, r);
            }
        }
    }



    /**
     * Will find a column based on name
     * @param columnName
     * @return
     * @throws IllegalArgumentException
     */
     ColumnDescription getColumn(String columnName) throws IllegalArgumentException {
        for (ColumnDescription column : descriptions) {
            if (column.getColumnName().equals(columnName)) {
                return column;
            }
        }
        throw new IllegalArgumentException("Column " + columnName + " does not exist!");
    }

    /**
     * Checks if a value meets all constraints for that column
     * @param value
     * @param column
     * @return
     */
     boolean checkConstraints(V value, ColumnDescription column) {
        ColumnDescription.DataType type = column.getColumnType();
        Boolean isValid = false;
        try {
            switch (type) {
                case INT:
                    isValid = true;
                    break;
                case BOOLEAN:
                    isValid = true;
                    break;
                case DECIMAL:
                    String checker = "";
                    checker += value;
                    int decimal = checker.lastIndexOf(".");
                    String beforeDecimal = checker.substring(0, decimal);
                    String afterDecimal = checker.substring(decimal + 1);
                    int beforeDecimalLength = beforeDecimal.length();
                    int afterDecimalLength = afterDecimal.length();

                    if (beforeDecimalLength == afterDecimalLength) {
                        if (beforeDecimalLength <= column.getWholeNumberLength() && afterDecimalLength <= column.getFractionLength()) {
                            isValid = true;
                        }
                    }
                    break;
                case VARCHAR:
                    if (column.getVarCharLength() > 0 && value.toString().length() > column.getVarCharLength()) {
                        isValid = false;
                    } else {
                        isValid = true;
                    }
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid type for column " + column.getColumnName() + " in table " + getName());
        }
        return isValid;
    }

    /**
     * Checks if a value is needs to be unique/is
     * @param value
     * @param column
     * @return
     */
     boolean checkUnique(V value, ColumnDescription column) {
        if (column.isUnique() || primaryKeyColumn.equals(column)) {
            if (contains(column, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a column already contains that value
     * @param column
     * @param value
     * @return
     */
     boolean contains(ColumnDescription column, V value) {
        int index = descriptions.indexOf(column);
        for (Row r : Table) {
            if (r.getCell(index).compareTo(value) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Same as the one above, but for cells
     * @param column
     * @param value
     * @return
     */
     boolean contains(int column, Cell value) {
        for (Row r : Table) {
            if (r.getCell(column).compareTo(value.getValue()) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses, and makes sure values follow to data types
     * @param value
     * @param column
     * @return
     */
    V checkAndCast(String value, ColumnDescription column) {
        ColumnDescription.DataType type = column.getColumnType();
        Object toReturn = null;
        try {
            switch (type) {
                case INT:
                    toReturn = Integer.parseInt(value);
                    break;
                case BOOLEAN:
                    if (value.toLowerCase().equals("true")) {
                        toReturn = true;
                    } else if (value.toLowerCase().equals("false")) {
                        toReturn = false;
                    } else {
                        throw new IllegalArgumentException("Error in column " + column.getColumnName() + " Boolean values must be either True or False");
                    }
                    break;
                case DECIMAL:
                    toReturn = Double.parseDouble(value);
                    break;
                case VARCHAR:
                    toReturn = value;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid type for column " + column.getColumnName() + " in table " + getName());
        }
        return (V) toReturn;
    }

    /**
     * for updating the primary key
     * @param primaryKeyColumn
     */
     void setPrimaryKeyColumn(ColumnDescription primaryKeyColumn) {
        this.primaryKeyColumn = primaryKeyColumn;
    }

     String getName() {
        return name;
    }

     int getSize() {
        return Table.size();
    }

     int getWidth() {
        return width;
    }


    /**
     * Allows you to remove a row from the table via
     * rowNumber
     * @param rowNumber
     */
     void removeRow(int rowNumber) {
        Iterator<Row> rowIterator = this.Table.iterator();
        while (rowIterator.hasNext()) {
            Row rowToChange = rowIterator.next();
            if(getRowIndex(rowToChange) == rowNumber){
                for(ColumnDescription description : descriptions){
                    BTree t = getTree(description.getColumnName());
                    if(t != null){
                        deleteTreeRow(rowToChange, t);
                    }
                }
                rowIterator.remove();
            }
        }
    }

    /**
     * For deleting a lot of rows, takes an entire hashset as argument
     * Will delete all rows that hashset references too
     * @param set
     */
     void removeRows(HashSet<Row> set){
        Iterator<Row> rowIterator = this.Table.iterator();
        while(rowIterator.hasNext()){
            Row r = rowIterator.next();
            if(set.contains(r)){
                removeFromIndices(r);
                rowIterator.remove();
            }
        }
    }
    /**
     * Strictly for debugging, I know I know - this should be called "toString"
     */
     void printTable() {
        for (ColumnDescription desc : descriptions) {
            System.out.print(desc.getColumnName() + " ");
        }
        for (Row r : Table) {
            System.out.println();
            for (Object cell : r.data) {
                Cell thisCell = (Cell) cell;
                try {
                    System.out.print(" " + thisCell.getValue().toString() + " ");
                } catch (NullPointerException N) {
                    System.out.print(" NULL ");
                    continue;
                }
            }
        }
    }

    /**
     * For selecting, sometimes you need to remove a column
     * @param ColumnName
     */
     void removeColumn(String ColumnName) {
        int columnIndex = descriptions.indexOf(getColumn(ColumnName));
        Iterator<Row> rowIterator = this.Table.iterator();
        while (rowIterator.hasNext()) {
            Row rowToChange = rowIterator.next();
            rowToChange.removeColumn(columnIndex);
        }
    }


    /**
     * Allows you to remove indice
     * @param columnName
     */
     void removeTree(String columnName){
        Iterator<BTree> treeIterator = this.treeTable.iterator();
        while (treeIterator.hasNext()){
            BTree tree = treeIterator.next();
            if(tree.getName().equals(columnName)){
                treeIterator.remove();
            }
        }
    }

    private Integer getAverageInt(int columnIndex) {
        Integer i = 0;
        for (Row r : Table) {
            Integer toAdd = (Integer) r.getCell(columnIndex).getValue();
            try {
                i += toAdd;
            } catch (NullPointerException e) {
                continue;
            }
        }
        i = i / ((Table.size()));
        return i;
    }

     private Double getAverageDouble(int columnIndex) {
        Double i = 0.0;
        for (Row r : Table) {
            try {
                Double toAdd = (Double) r.getCell(columnIndex).getValue();
                i += toAdd;
            } catch (NullPointerException e) {
                continue;
            }
        }
        i = i / ((Table.size()));
        return i;
    }

    /**
     * Starts at a certain point so it can be used for sorting as well
     * @param columnIndex
     * @param startingPoint
     * @return
     */
     Integer getMaxInRange(int columnIndex, int startingPoint, int endingPoint, boolean wNull) {
        Row g = Table.get(startingPoint);
        Cell i = g.getCell(columnIndex);
        for (int u = startingPoint; u < endingPoint; u++) {
            try {
                Cell compare = Table.get(u).getCell(columnIndex);
                if (i.compareTo(compare.getValue()) < 0) {
                    g = Table.get(u);
                    i = compare;
                }
            }
            //The compareTo Method will throw errors if something is null
            catch (Exception e) {
                if(wNull) {
                    g = Table.get(u);
                    i = Table.get(u).getCell(columnIndex);
                }
                else{
                    continue;
                }
            }
        }
        return Table.indexOf(g);
    }

     Integer getMinInRange(int columnIndex, int startingPoint, int endingPoint, boolean wNull) {
        Row g = Table.get(startingPoint);
        Cell i = g.getCell(columnIndex);
        for (int u = startingPoint; u < endingPoint; u++) {
            try {
                Cell compare = Table.get(u).getCell(columnIndex);
                if (i.compareTo(compare.getValue()) > 0) {
                    g = Table.get(u);
                    i = compare;
                }
            }
            catch (NullPointerException e) {
                if(wNull) {
                    g = Table.get(u);
                    i = Table.get(u).getCell(columnIndex);
                }
                else{
                    continue;
                }
            }
        }
        return Table.indexOf(g);
    }

     private int getSumInt(int columnIndex) {
        Integer i = 0;
        for (Row r : Table) {
            try {
                Integer toAdd = (Integer) r.getCell(columnIndex).getValue();
                i += toAdd;
            } catch (NullPointerException e) {
                continue;
            }
        }
        return i;
    }

     private double getSumDouble(int columnIndex) {
        Double i = 0.0;
        for (Row r : Table) {
            try {
                Double toAdd = (Double) r.getCell(columnIndex).getValue();
                i += toAdd;
            } catch (NullPointerException e) {
                continue;
            }
        }
            return i;
        }

     ArrayList<Row> getTable() {
        return Table;
    }

     void setTable(ArrayList<Row> table) {
        Table = table;
    }

     int getRowIndex(Row r){
        if(Table.contains(r)){
            return (Table.indexOf(r));
        }
        return -1;
    }
     ArrayList<ColumnDescription> getDescriptions() {
        return descriptions;
    }

     ColumnDescription getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

     void setName(String name) {
        this.name = name;
    }

     void wipe(){
        Table.clear();
        treeTable.clear();
        addPrimaryKey();
    }

     Row getRow(int row){
        return Table.get(row);
    }

     int getColumnIndex(String columnName){
        for (ColumnDescription description : descriptions){
            if(description.getColumnName().equals(columnName)){
                return(descriptions.indexOf(description));
            }
        }
        throw new IllegalArgumentException("Column " + columnName + " does not exist!");
    }
    /**
     * This is for orderBys - essentially it splits the ArrayList into tiers of pair values -
     * Which I define as a group of rows with a common value from the order by before - After being ordered by that value
     * We continue to break them down and do order bys until we either run out or order-bys or
     * all the rows become "distinct" after this, we join all the lists together and replace that with
     * the table
     *
     * @param ColumnIndex
     * @return
     */
     ArrayList<PairValue> splitToTiers(int ColumnIndex, ArrayList<Row> ourTable){
        ArrayList<PairValue> tierList = new ArrayList<>();
        int startingPoint = 0;
        V c = (V) Table.get(startingPoint).getCell(ColumnIndex).getValue();
        //Due to a change, we first need to do all the NULL values
        if(c == null){
            for(Row x : ourTable){
                V val = (V) x.getCell(ColumnIndex).getValue();
                if(val == null){
                    continue;
                }
                else{
                    int rowFind = getRowIndex(x);
                    tierList.add(new PairValue(startingPoint, rowFind));
                    startingPoint = rowFind;
                    break;
                }
            }
        }
        for(Row r : ourTable){
            Cell cmp = Table.get(startingPoint).getCell(ColumnIndex);
            try{
                int x = cmp.compareTo(r.getCell(ColumnIndex).getValue());
                if(x == 0){
                    continue;
                }
                else{
                    int rowIndex = getRowIndex(r);
                    tierList.add(new PairValue(startingPoint, rowIndex));
                    startingPoint = rowIndex;
                    continue;
                }
            }
            catch (Exception e ){
                continue;
            }
        }
        tierList.add(new PairValue(startingPoint, ourTable.size()));
        return tierList;
    }



     boolean contains(Row x){
        for(Row r : Table){
            if(x.equals(r)){
                return true;
            }
        }
        return false;
    }

     void addRow(Row r){
        Table.add(r);
    }

    /**
     * Adds the primary key indices
     */
    void addPrimaryKey(){
        treeTable.add(new BTree(primaryKeyColumn.getColumnType().toString(), 6, primaryKeyColumn.getColumnName()));
    }
     void createIndice(String columnName){
        ColumnDescription disc = getColumn(columnName);
        createIndice(disc);
    }
     void createIndice(ColumnDescription column){
        BTree tree = new BTree(column.getColumnType().toString(), 6, column.getColumnName());
        int cell = getColumnIndex(column.getColumnName());
        for(Row r : Table){
            Cell c = r.getCell(cell);
            tree.put(c.getValue(), r);
        }
        treeTable.add(tree);
    }
    //Due to the nature of this tree, you must only be able to remove with key AND value.
     void deleteTreeRow(Row r, BTree tree){
        int index = getColumnIndex(tree.getName());
        Cell c = r.getCell(index);
        tree.remove(c.getValue(), r);
    }
     void moveColumn(String columnName, int index){
       int place = getColumnIndex(columnName);
       if(place == index){
           return;
       }
        Collections.swap(descriptions, place, index);
        for(Row r : Table){
            Collections.swap(r.data, place, index);
        }
    }

    /**
     * this calls on all the recursive methods that do the conditions
     *
     *We get all rows that meet some part of the condition, and them run them through the entire condition.
     *This Replaces the table with the new dataset, so it is only good for selecting, because in selectiing,
     * we would be using a clone for returning in result set
     *
     * @param where
     */
     void runConditions(Condition where){
        HashSet<Row> rows = new HashSet<>();
        rows = this.getRows(rows, where);
        rows = this.cloneTable(rows);
        this.wipe();
        for(Row r : rows){
            try {
                r = doCondition(this, r, where);
            }
            catch (Exception e) {
                continue;
            }
            if(r != null){
                Table.add(r);
            }
        }
    }

    /**
     * This
     * @param rows
     * @param where
     * @return
     */
    HashSet<Row> runConditions(HashSet<Row> rows, Condition where){
        rows = getRows(rows, where);
        HashSet<Row> newRows = new HashSet<>();
        for(Row r : rows){
            try {
                r = doCondition(this, r, where);
            }
            catch (Exception e) {
                continue;
            }
            if(r != null){
                newRows.add(r);
            }
        }
        return newRows;
    }


    /**
     * This essentially takes in a hashset and a condition
     * and puts together all the possible rows from every part
     * of the hashset for select.
     * @param set
     * @param where
     * @return
     */
    private HashSet<Row> getRows(HashSet<Row> set, Condition where){
        if(where.getLeftOperand() instanceof  Condition){
            getRows(set, (Condition) where.getLeftOperand());
        }
        if(where.getRightOperand() instanceof Condition){
            getRows(set, (Condition) where.getRightOperand());
        }
        else{
            set.addAll(getAllPossibleRows(where));
        }
        return set;
    }

    /**
     * This will bundle up all rows that have a possibility in the where clause
     * after that we will run each row through the clauses
     * If the row passes through, then we added it to our data structure of
     * selected rows
     *
     * HashSet works in this situation without overriding equals because we are
     * dealing with references and are trying to weed out duplicate references.
     * Since duplicate references share the same hashcode, I found this data structure
     * to be ideal without taking the usaul steps to make it effective ie Overriding Equal && Hashcode
     *
     * @param c
     * @return
     */
    private HashSet<Row> getAllPossibleRows(Condition c){
        HashSet<Row> list = new HashSet<>();
        ColumnID id = (ColumnID) c.getLeftOperand();
        int index = getColumnIndex(id.getColumnName());
        ColumnDescription desc = descriptions.get(index);
        V val = checkAndCast((String) c.getRightOperand().toString(), desc);
        BTree t = getTree(desc.getColumnName());
        switch (c.getOperator()){

            ///This is the only case where I am checking the BTree
            case EQUALS:
                if(t != null){
                    LinkedList<Row> e = t.get(val);
                    for(Row r : e){
                        list.add(r);
                    }
                }
                else{
                    for(Row r : Table){
                        if(r.getCell(index).compareTo(val) == 0 && r.getCell(index).value != null){
                            list.add(r);
                        }
                    }
                }

                break;

            case GREATER_THAN_OR_EQUALS:
                if(t != null){
                    list.addAll(t.select(val, c));
                }
                else {
                    for (Row r : Table) {
                        if (r.getCell(index).compareTo(val) >= 0 && r.getCell(index).value != null) {
                            list.add(r);
                        }
                    }
                }
            break;

            case lESS_THAN_OR_EQUALS:
                if(t != null){
                    list.addAll(t.select(val, c));
                }
                else {
                    for (Row r : Table) {
                        if (r.getCell(index).compareTo(val) <= 0 && r.getCell(index).value != null) {
                            list.add(r);
                        }
                    }
                }
            break;

                //While you did not explicitly ask us to take from the tree in this scenario,
                //If I have enough time when I come back around for review I will.
            case NOT_EQUALS:
                for(Row r : Table){
                    if(r.getCell(index).compareTo(val) != 0 && r.getCell(index).value != null){
                        list.add(r);
                    }
                }
             break;


            case LESS_THAN:
                if(t != null){
                    list.addAll(t.select(val, c));
                }
                else {
                    for (Row r : Table) {
                        if (r.getCell(index).compareTo(val) < 0 && r.getCell(index).value != null) {
                            list.add(r);
                        }
                    }
                }
            break;

            case GREATER_THAN:
                if(t != null){
                    list.addAll(t.select(val, c));
                }
                else {
                    for (Row r : Table) {
                        if (r.getCell(index).compareTo(val) > 0 && r.getCell(index).value != null) {
                            list.add(r);
                        }
                    }
                }
            break;

        }
        return list;
    }

    /**
     * If it returns null, row did not pass the constraints.
     * Takes a table as an argument because we may need to clone the table and use the clone
     * to protect the rows - Have a dataset we can mess up without worrying about our original
     * data.
     * @param ourTable
     * @param r
     * @param c
     * @return
     */
    @Nullable
    private Row doCondition(SqlTable ourTable, Row r, Condition c){

        switch (c.getOperator()) {
            case OR:
                if (c.getLeftOperand() instanceof Condition || c.getRightOperand() instanceof Condition) {
                    Row rowOne = doCondition(ourTable, r, (Condition) c.getLeftOperand());
                    Row rowTwo = doCondition(ourTable, r, (Condition) c.getRightOperand());
                    if (rowOne != null || rowTwo != null) {
                        return r;
                    }
                }
                break;

            case AND:
                if (c.getLeftOperand() instanceof Condition || c.getRightOperand() instanceof Condition) {
                    Row rowOne = doCondition(ourTable, r, (Condition) c.getLeftOperand());
                    Row rowTwo = doCondition(ourTable, r, (Condition) c.getRightOperand());
                    if (rowOne != null && rowTwo != null) {
                        return r;
                    }
                }
                break;

                case EQUALS:
                    ColumnID id = (ColumnID) c.getLeftOperand();
                    ColumnID columnID = (ColumnID) c.getLeftOperand();
                    int index = ourTable.getColumnIndex(columnID.getColumnName());
                    Object operand = ourTable.checkAndCast((String) c.getRightOperand().toString(), ourTable.getColumn(index));
                    if (r.getCell(index).compareTo(operand) == 0 && r.getCell(index).value != null) {
                        return r;
                    }
                    break;


                case GREATER_THAN:
                    id = (ColumnID) c.getLeftOperand();
                    columnID = (ColumnID) c.getLeftOperand();
                    index = ourTable.getColumnIndex(columnID.getColumnName());
                    operand = ourTable.checkAndCast((String) c.getRightOperand().toString(), ourTable.getColumn(index));
                    if (r.getCell(index).compareTo(operand) > 0 && r.getCell(index).value != null) {
                        return r;
                    }
                    break;


                case LESS_THAN:
                    id = (ColumnID) c.getLeftOperand();
                    columnID = (ColumnID) c.getLeftOperand();
                    index = ourTable.getColumnIndex(columnID.getColumnName());
                    operand = ourTable.checkAndCast((String) c.getRightOperand().toString(), ourTable.getColumn(index));
                    if (r.getCell(index).compareTo(operand) < 0 && r.getCell(index).value != null) {
                        return r;
                    }
                    break;

                case NOT_EQUALS:
                    id = (ColumnID) c.getLeftOperand();
                    columnID = (ColumnID) c.getLeftOperand();
                    index = ourTable.getColumnIndex(columnID.getColumnName());
                    operand = ourTable.checkAndCast((String) c.getRightOperand().toString(), ourTable.getColumn(index));
                    if (r.getCell(index).compareTo(operand) != 0 && r.getCell(index).value != null) {
                        return r;
                    }
                    break;

                case lESS_THAN_OR_EQUALS:
                    id = (ColumnID) c.getLeftOperand();
                    columnID = (ColumnID) c.getLeftOperand();
                    index = ourTable.getColumnIndex(columnID.getColumnName());
                    operand = ourTable.checkAndCast((String) c.getRightOperand().toString(), ourTable.getColumn(index));
                    if (r.getCell(index).compareTo(operand) <= 0 && r.getCell(index).value != null) {
                        return r;
                    }

                    break;

                case GREATER_THAN_OR_EQUALS:
                    id = (ColumnID) c.getLeftOperand();
                    columnID = (ColumnID) c.getLeftOperand();
                    index = ourTable.getColumnIndex(columnID.getColumnName());
                    operand = ourTable.checkAndCast((String) c.getRightOperand().toString(), ourTable.getColumn(index));
                    if (r.getCell(index).compareTo(operand) >= 0 && r.getCell(index).value != null) {
                        return r;
                    }

                    break;


            }
    return null;
    }

    public void add(Row r){
        Table.add(r);
    }

    /**
     * Helper method for simplifying types when making calls
     * @param columnIndex
     * @return
     */
     V getAverage(int columnIndex){
        ColumnDescription x = descriptions.get(columnIndex);

        switch (x.getColumnType()){

            case INT:
                return (V) getAverageInt(columnIndex);

            case DECIMAL:
                return (V) getAverageDouble(columnIndex);
        }
        throw new IllegalArgumentException("Illegal Type being asked for");
    }

    /**
     * Same as the one on top
     * @param columnIndex
     * @return
     */
     V getSum(int columnIndex){
        ColumnDescription x = descriptions.get(columnIndex);
        switch (x.getColumnType()){

            case INT:
                return (V) (Integer) getSumInt(columnIndex);

            case DECIMAL:
                return (V) (Double) getSumDouble(columnIndex);
        }
        throw new IllegalArgumentException("Illegal Type being asked for");
    }


    /**
     * Takes a delete Query as an argument
     * @param toDelete
     */
    void doDelete(DeleteQuery toDelete){
        HashSet<Row> set = new HashSet();
        if(toDelete.getWhereCondition() == null){
            this.wipe();
            return;
        }
        set = getRows(set,toDelete.getWhereCondition());
        for(Row r : set){
            r = doCondition(this, r, toDelete.getWhereCondition());
        }
        removeRows(set);
    }

    /**
     * For deep cloning the table for select
     * It is absolutely paramount not to let the select clause, and the
     * rows it uses to modify the table in any way/shape/form
     * That is why we do a deep clone.
     * @return
     */
    ArrayList<Row> cloneTable(){
        ArrayList<Row> r = new ArrayList<>();
        for(Row row : this.Table){
            Row x = new Row();
            for(Object c : row.data){
                Cell e = (Cell) c;
                x.add(new Cell(e.value));
            }
            r.add(x);
        }
        return r;
    }

    /**
     * For deep cloning when selecting from tree (I was getting bugs because I hadnt cloned their references
     * @param s
     * @return
     */
    HashSet<Row> cloneTable(HashSet<Row> s){
        HashSet<Row> r = new HashSet<>();
        for(Row row : s){
            Row x = new Row();
            for(Object c : row.data){
                Cell e = (Cell) c;
                x.add(new Cell(e.value));
            }
            r.add(x);
        }
        return r;
    }





}
