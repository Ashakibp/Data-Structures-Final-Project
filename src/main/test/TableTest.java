import net.sf.jsqlparser.JSQLParserException;
import org.junit.Before;
import org.junit.Test;


/**
 * Some of these test methods are not complete, and involve printing to the terminal.
 *
 * the table
 *
 * Created by Aaron on 4/9/17.
 */
public class TableTest {
    Main testMain = new Main();


    //This test is to make sure that a table of proper size is created
    public void tableCreationTest() throws Exception{
        String query = "CREATE TABLE YCStudent"
                + "("
                + " BannerID int,"
                + " SSNum int UNIQUE,"
                + " FirstName varchar(255),"
                + " LastName varchar(255) NOT NULL,"
                + " GPA decimal(1,2) DEFAULT 0.0,"
                + " CurrentStudent boolean DEFAULT true,"
                + " Class VARCHAR(255),"
                + " PRIMARY KEY (BannerID)"
                + ");";
        testMain.executeCommand(query);
        SqlTable YCStudent = testMain.findTable("YCStudent");
        assert YCStudent.getWidth() == 7;
        assert testMain.amountOfTables() == 1;
    }


    public void tableCreationTest2() throws Exception{
        String query = "CREATE TABLE CSFall"
                + "("
                + " BannerID int,"
                + " SSNum int UNIQUE,"
                + " FirstName varchar(255),"
                + " LastName varchar(255) NOT NULL,"
                + " GPA decimal(1,2) DEFAULT 0.0,"
                + " PRIMARY KEY (BannerID)"
                + ");";
        testMain.executeCommand(query);
        SqlTable YCStudent = testMain.findTable("CSFall");
        assert YCStudent.getWidth() == 5;
        assert testMain.amountOfTables() == 2;
    }
    /**
     * This will populate the table.
     * @throws Exception
     */
    public void testInsert() throws Exception
    {
        String query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Aaron','Shakibpanah', 'Sophmore',800012345, 800454, 3.8);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yitzie', 'Schienman' , 'Sophmore', 2453345, 43543, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yakov', 'Stern' , 'Senior', 453232453, 25343425, 2.6);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Barack', 'Obama' , 'Sophmore', 25244235, 545665422, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Donald', 'Trump' , 'Junior', 4255342, 2345234, 3.7);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('George', 'Bush' , 'Senior',3254435, 2543525, 3.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yair', 'Lapid' , 'null', 3280832, 4334534, 2.8);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Matan', 'Nomdar' , 'Freshman', 3453454, 24352345, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Samuel', 'Tafara' , 'null', 52342345, 565465, 2.9);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Jonathan', 'Singer' , 'Junior', 43589342, 354234255, 3.4);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Judi', 'Meltzer' , 'Junior', 4352534, 7654723, 3.6);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yehuda', 'Gale' , 'Senior', 8675364, 0678354, 9.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Noah', 'Frankel' , 'Junior', 3875230, 3423798, 3.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Josh', 'Sinker' , 'Senior', 9209435, 42354322, 2.6);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yosef', 'Cohen' , 'null', 542348432, 35249009, 5.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Aaron', 'Landy' , 'null', 25340439, 2342345, 9.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Nir', 'Frankel' , 'Freshman', 45073234, 4322435, 6.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Avi', 'Greenman' , 'null', 34543534, 43243, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Meir', 'Alneck' , 'null', 4354345, 345465, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Steve', 'Jobs' , 'Senior', 345465, 45437545, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Micah', 'Shippel' , 'Junior', 876776, 83723247, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yehuda', 'Bigowski' , 'Sophmore', 93473875, 432474383, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Rick', 'Scott' , 'Senior', 945466, 9335078, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('John', 'Appleseed' , 'Sophmore', 439009054, 9475743, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Jamie', 'Benson' , 'null', 9306739, 8347947, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Shalom', 'Mamon' , 'null', 3923474, 9957349, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Meir', 'Shemesh' , 'null', 9474832, 904575, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Avraham', 'Avinu' , 'null', 943365, 947524, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('David', 'Hamelech' , 'null', 947239034, 23715034, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yosef', 'Hasidik' , 'null', 974328043, 897429832, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Lipa', 'Hasinger' , 'null', 9097547, 094327432, 4.0);");
        testMain.executeCommand(query);

    }

    public void testInsert2() throws JSQLParserException {
        String query = ("INSERT INTO CSFall (FirstName, LastName, BannerID, SSNum, GPA) VALUES ('Aaron','Shakibpanah',800012345, 800454, 3.8);");
        testMain.executeCommand(query);
        query = ("INSERT INTO CSFall (FirstName, LastName, BannerID, SSNum, GPA) VALUES ('Yitzie', 'Schienman' ,2453345, 43543, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO CSFall (FirstName, LastName, BannerID, SSNum, GPA) VALUES ('Yakov', 'Stern' , 453232453, 25343425, 2.6);");
        testMain.executeCommand(query);
        query = ("INSERT INTO CSFall (FirstName, LastName, BannerID, SSNum, GPA) VALUES ('Barack', 'Obama' , 25244235, 545665422, 4.0);");
        testMain.executeCommand(query);
        query = ("INSERT INTO CSFall (FirstName, LastName, BannerID, SSNum, GPA) VALUES ('Donald', 'Trump' , 4255342, 2345234, 3.7);");
    }


    @Before
    public void setupTable() throws Exception {
        tableCreationTest();
        tableCreationTest2();
        testInsert();
        testInsert2();
    }

    /**
     * Certain tests will modify the table, this is for setting the table
     * back to its original state
     * @throws Exception
     */
    @Test
    public void restoreTable() throws Exception {
        SqlTable x = testMain.findTable("YCStudent");
        int size = x.getSize();
        x.wipe(); //Deletes all
        testInsert();
        assert x.getSize() == size;
    }


    /**
     * This test will update the entire table
     * will show that this words by printing the terminal
     * you can also see the result set if you would like, just print it normal
     * as I have overrided toString()
     * @throws JSQLParserException
     */
    @Test
    public void updateTest1() throws JSQLParserException {
        String query = "UPDATE YCStudent SET GPA=3.0,Class='Super Senior';";
        ResultSet r = testMain.executeCommand(query);
        //System.out.println(r);
        SqlTable x = testMain.findTable("YCStudent");
        x.printTable();
    }

    /**
     * This one is a bit simpler. We just check that the avg GPA is 3.0 since
     * We made them all 3.0
     * @throws Exception
     */
    @Test
    public void updateTest2() throws Exception {
        restoreTable(); //This will restore thr table to its original state for further testing
        String query = "UPDATE YCStudent SET GPA=3.0;";
        ResultSet r = testMain.executeCommand(query);
        query = "SELECT AVG(GPA) FROM YCStudent;";
        r = testMain.executeCommand(query);
        assert r.getTable().get(0).getCell(0).value.equals(3.0);
    }


    //Very straight forward, just putting things in that shouldnt go in and making sure the
    //Program catches it. This is for the remainder of update.
    @Test
    public void updateTest3() throws Exception {
        testInsert();
        String query = "UPDATE YCStudent SET GPA=3.333;";
        ResultSet r = testMain.executeCommand(query);
        assert r.getTable().get(0).getCell(0).value.equals(false);//Essentially saying that it didnt go through
    }

    @Test
    public void updateTest4() throws JSQLParserException {
        String query = "UPDATE YCStudent SET BannerID = 1;";;
        ResultSet r = testMain.executeCommand(query);
        assert r.getTable().get(0).getCell(0).value.equals(false);//Essentially saying that it didnt go through
    }

    @Test
    public void updateTest5() throws JSQLParserException {
        String query = "UPDATE YCStudent SET Currentstudent = null;";
        ResultSet r = testMain.executeCommand(query);
        assert r.getTable().get(0).getCell(0).value.equals(false);//Essentially saying that it didnt go through;
    }


    /**
     * Unless we are attempting to get an empty table, or an error, the method I will use to
     * show this works will be though printing to the terminal.
     *
     * There may be some order bys thrown in as well.
     * @throws JSQLParserException
     */
    @Test
    public void selectTest2() throws JSQLParserException {
        String query = "SELECT * FROM YCStudent WHERE GPA > 2.0 ORDER BY Class ASC, GPA ASC;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }
    @Test
    public void selectTest3() throws JSQLParserException {
        String query = "select distinct FirstName,GPA from YCStudent where Class='Sophmore' OR GPA = 4.0 OR GPA = 9.0 OR GPA = 3.8 OR FirstName='Judi' ORDER BY GPA DESC;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    /**
     * Here, we are going to be selecting an empty table
     * It should only print out the column names, but no rows
     * an assert statement to double check that it is empty
     * @throws JSQLParserException
     */
    @Test
    public void selectTest4() throws JSQLParserException {
        String query = "select * from YCStudent where Class='Graduate' ORDER BY GPA DESC;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
        assert r.getTable().size() == 0;
    }

    /**
     * Show function works
     * @throws JSQLParserException
     */
    @Test
    public void selectTest5() throws JSQLParserException {
        String query = "SELECT MAX(GPA) FROM YCStudent;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    /**
     * Show it works with strings too
     * @throws JSQLParserException
     */
    @Test
    public void selectTest6() throws JSQLParserException {
        String query = "SELECT MAX(FirstName) FROM YCStudent;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    /**
     * This one will use the BTree, however I am not sure how to display that it has.
     * Feel free to run it through the debugger/The tree can be printed out and will show that it
     * works
     * @throws JSQLParserException
     */
    @Test
    public void selectTest7() throws JSQLParserException {
        String query = "SELECT MAX(BannerID) FROM YCStudent;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    /**
     * This will also use the Btree
     * @throws JSQLParserException
     */
    @Test
    public void selectTest8() throws JSQLParserException {
        String query = "SELECT * FROM YCStudent WHERE BannerID > 500;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    @Test
    public void selectTest9() throws Exception{
        String query ="SELECT distinct GPA, FirstName FROM YCStudent where CurrentStudent=true or GPA > 4;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    /**
     * Multiple function support.
     * @throws Exception
     */
    @Test
    public void testFunction() throws Exception{
        testInsert();
        String query = "SELECT AVG(GPA), MAX(LastName) FROM YCStudent;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    @Test
    public void testFunction2() throws Exception{
        testInsert();
        String query = "SELECT MAX(GPA), MIN(LastName) FROM YCStudent;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    @Test
    public void testFunction3() throws Exception{
        testInsert();
        String query = "SELECT AVG(BannerID), MIN(FirstName) FROM YCStudent;";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
    }

    /**
     * Creates an Indice of the GPA column and then prints it out.
     *
     * @throws Exception
     */
    @Test
    public void createIndiceAndPrint() throws JSQLParserException {
        String query = "CREATE INDEX GPA on YCStudent (GPA);";
        ResultSet r = testMain.executeCommand(query);
        System.out.println(r);
        SqlTable x = testMain.findTable("YCStudent");
        BTree tree = x.getTree("GPA");
        tree.print();
        //This will only show tree structure, not the values.
    }

    /**
     * Wipe the entire table, and then print to show it is empty
     * also assert statement for double check
     * At the end we restore the table to its previous state.
     * @throws JSQLParserException
     */
    @Test
    public void testDelete1() throws Exception {
        String query = "Delete FROM YCStudent;";
        testMain.executeCommand(query);
        SqlTable x = testMain.findTable("YCStudent");
        x.printTable();
        assert x.getSize() == 0;
        restoreTable();
    }

    /**
     * Will print table to show it has been done.
     * @throws Exception
     */
    @Test
    public void testDelete2() throws Exception{
        String query = "Delete FROM YCStudent Where Class='Freshman';";
        testMain.executeCommand(query);
        SqlTable x = testMain.findTable("YCStudent");
        x.printTable();
        restoreTable();
    }

    @Test
    public void testDelete3() throws Exception{
        String query = "Delete FROM YCStudent Where GPA<3.0;";
        testMain.executeCommand(query);
        SqlTable x = testMain.findTable("YCStudent");
        x.printTable();
        restoreTable();
    }

    @Test
    public void testDelete4() throws Exception{
        String query = "Delete FROM YCStudent Where GPA=3.0;";
        testMain.executeCommand(query);
        SqlTable x = testMain.findTable("YCStudent");
        x.printTable();
        restoreTable();
    }




}
