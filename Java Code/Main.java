import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import net.sf.jsqlparser.JSQLParserException;
import java.util.ArrayList;

/**
 * Created by Aaron on 4/6/17.
 */

public class Main {
    static ArrayList<SqlTable> tableHolder;
    SQLParser parser;

    public Main() {
        parser = new SQLParser();
        tableHolder = new ArrayList<>();
    }

    /**
     * Since this parser returns objects, we work based on the objects it returns to
     * us. So the first step is figuring out which objects are being returned to us.
     * @param query
     * @throws JSQLParserException
     */
    public ResultSet executeCommand(String query) throws JSQLParserException {
       SQLQuery result = parser.parse(query);

        if(result instanceof CreateTableQuery){
            CreateTableQuery tableInfo = (CreateTableQuery) result;
            //If someone tries to name a table something that already exists, we need to stop them.
            try{
                findTable(tableInfo.getTableName());
                return new ResultSet(false, new IllegalArgumentException("Table " + tableInfo.getTableName() + " already exists!"));
            }
            //If we caught an exception, then we can proceed.
            catch (Exception e) {
                addTable(new SqlTable(tableInfo));
                return new ResultSet(findTable(tableInfo.getTableName()));
            }
        }

        if(result instanceof CreateIndexQuery){
            try {
                CreateIndexQuery indexQuery = (CreateIndexQuery) result;
                SqlTable workTable = findTable(indexQuery.getTableName());
                workTable.createIndice(indexQuery.getColumnName());
                return new ResultSet(true);
            }
            catch (Exception e){
                return new ResultSet(false, e);
            }

        }

        if(result instanceof DeleteQuery){
            try{
                DeleteQuery toDelete = (DeleteQuery) result;
                SqlTable toWork = findTable(toDelete.getTableName());
                toWork.doDelete(toDelete);
                return new ResultSet(true);
            }
            catch (Exception e){
                return new ResultSet(false, e);
            }
        }
        if(result instanceof SelectQuery){
            try {
                SelectQuery querySelect = (SelectQuery) result;
                Selector select = new Selector(querySelect, new SqlTable(findTable(querySelect.getFromTableNames()[0])));
                select.run();
                return new ResultSet(select);
            }
            catch (Exception e){
                return new ResultSet(false, e);
            }

        }
        if(result instanceof InsertQuery){
            try {
                InsertQuery tableInfo = (InsertQuery) result;
                SqlTable tableToAdd = findTable(tableInfo.getTableName());
                tableToAdd.add(tableInfo.getColumnValuePairs());
                return new ResultSet(true);
            }
            catch (Exception e){
                return new ResultSet(false, e);

            }
        }
        if(result instanceof UpdateQuery){
            try {
                UpdateQuery update = (UpdateQuery) result;
                SqlTable toUpdate = findTable(update.getTableName());
                Updater up = new Updater(toUpdate, update);
                up.run();
                return new ResultSet(true);
            }
            catch (Exception e){
                return new ResultSet(false, e);
            }
        }
        return new ResultSet(false);
    }

    static SqlTable findTable(String tableName) throws IllegalArgumentException{
        for(SqlTable t : tableHolder){
            if(t.getName().equals(tableName)){
                return t;
            }
        }
        throw new IllegalArgumentException("Table " + tableName + " does not exist");
    }

    int amountOfTables() {
        return tableHolder.size();
    }

    void addTable(SqlTable tableToAdd){
        tableHolder.add(tableToAdd);
    }


}
