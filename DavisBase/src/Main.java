import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static String prompt = "davisql> ";
    static String version = "v1.0b(example)";
    static String copyright = "©2016 Chris Irwin Davis";
    static boolean isExit = false;
    static PropertiesHelper recordLengthHelper = new PropertiesHelper(Constant.propertiesRecordLength);
    static PropertiesHelper columnTypeHelper = new PropertiesHelper(Constant.propertiesColumnType);
    static PropertiesHelper columnOrdinalHelper = new PropertiesHelper(Constant.propertiesColumnOrdinalPosition);
    static PropertiesHelper columnNotNullHelper = new PropertiesHelper(Constant.propertiesColumnNotNull);
    /*
     * Page size for alll files is 512 bytes by default.
     * You may choose to make it user modifiable
     */
    static long pageSize = 512;

    /*
     *  The Scanner class is used to collect user commands from the prompt
     *  There are many ways to do this. This is just one.
     *
     *  Each time the semicolon (;) delimiter is entered, the userCommand
     *  String is re-populated.
     */
    static Scanner scanner = new Scanner(System.in).useDelimiter(";");

    /**
     * **********************************************************************
     * Main method
     */
    public static void main(String[] args) {

		/* Display the welcome screen */
        splashScreen();

        try {
            createMetaData();



		/* Variable to collect user input from the prompt */
            String userCommand = "";

            while (!isExit) {
                System.out.print(prompt);
            /* toLowerCase() renders command case insensitive */
                userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
                // userCommand = userCommand.replace("\n", "").replace("\r", "");
                parseUserCommand(userCommand);
            }
            System.out.println("Exiting...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void createMetaData() throws FileNotFoundException {
        if (!Table.isTablePresent(Constant.davisbase_columns_file) && !Table.isTablePresent(Constant.davisbase_columns_file)) {
            Table davisbase_tables = new Table(Constant.davisbase_tables_file, 1);
            Table davisbase_columns = new Table(Constant.davisbase_columns_file, 1);
            String createTablesTableString = "CREATE TABLE DAVISBASE_TABLES(ROWID INT PRIMARY KEY , TABLE_NAME TEXT NOT NULL, AVG_LENGTH SMALLINT)";
            String createColumnsTableString = "CREATE TABLE DAVISBASE_COLUMNS(ROWID INT PRIMARY KEY , " +
                    "TABLE_NAME TEXT NOT NULL," +
                    "COLUMN_NAME TEXT NOT NULL," +
                    "DATA_TYPE TEXT NOT NULL, " +
                    "ORDINAL_POSITION TINYINT NOT NULL, " +
                    "COLUMN_KEY TEXT NOT NULL, " +
                    "IS_NULLABLE SHORTTEXT)";
            HashMap<String, ArrayList<String>> davisbase_table = parseCreateString(createTablesTableString, true);
            HashMap<String, ArrayList<String>> davisbase_column = parseCreateString(createColumnsTableString, true);
            updateTablesTable(Constant.davisbase_tables, Integer.parseInt(recordLengthHelper.getProperties(Constant.davisbase_tables.concat(".").concat(Constant.recordLength))));
            updateTablesTable(Constant.davisbase_columns, Integer.parseInt(recordLengthHelper.getProperties(Constant.davisbase_tables.concat(".").concat(Constant.recordLength))));
            updateColumnsTable(Constant.davisbase_tables, davisbase_table);
            updateColumnsTable(Constant.davisbase_columns, davisbase_column);
        } else {
            Table davisbase_tables = new Table(Constant.davisbase_tables_file, 1);
            Table davisbase_columns = new Table(Constant.davisbase_columns_file, 1);

        }


    }

    /** ***********************************************************************
     *  Method definitions
     */

    /**
     * Display the splash screen
     */
    public static void splashScreen() {
        System.out.println(line("-", 80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
        System.out.println("DavisBaseLite Version " + getVersion());
        System.out.println(getCopyright());
        System.out.println("\nType \"help;\" to display supported commands.");
        System.out.println(line("-", 80));
    }

    /**
     * @param s   The String to be repeated
     * @param num The number of time to repeat String s.
     * @return String A String object, which is the String s appended to itself num times.
     */
    public static String line(String s, int num) {
        String a = "";
        for (int i = 0; i < num; i++) {
            a += s;
        }
        return a;
    }

    /**
     * Help: Display supported commands
     */
    public static void help() {
        System.out.println(line("*", 80));
        System.out.println("SUPPORTED COMMANDS");
        System.out.println("All commands below are case insensitive");
        System.out.println();
        System.out.println("\tSELECT * FROM table_name;                        Display all records in the table.");
        System.out.println("\tSELECT * FROM table_name WHERE rowid = <value>;  Display records whose rowid is <id>.");
        System.out.println("\tDROP TABLE table_name;                           Remove table data and its schema.");
        System.out.println("\tVERSION;                                         Show the program version.");
        System.out.println("\tHELP;                                            Show this help information");
        System.out.println("\tEXIT;                                            Exit the program");
        System.out.println();
        System.out.println();
        System.out.println(line("*", 80));
    }

    /**
     * return the DavisBase version
     */
    public static String getVersion() {
        return version;
    }

    public static String getCopyright() {
        return copyright;
    }

    public static void displayVersion() {
        System.out.println("DavisBaseLite Version " + getVersion());
        System.out.println(getCopyright());
    }

    public static void parseUserCommand(String userCommand) throws FileNotFoundException {

		/* commandTokens is an array of Strings that contains one token per array element
		 * The first token can be used to determine the type of command
		 * The other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement */
        // String[] commandTokens = userCommand.split(" ");
        ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));


		/*
		*  This switch handles a very small list of hardcoded commands of known syntax.
		*  You will want to rewrite this method to interpret more complex commands.
		*/
        switch (commandTokens.get(0)) {
            case "select":
                parseQueryString(userCommand);
                break;
            case "drop":
                System.out.println("STUB: Calling your method to drop items");
                dropTable(userCommand);
                break;
            case "create":
                parseCreateString(userCommand, false);
                break;
            case "insert":
                parseInsertString(userCommand);
                break;
            case "help":
                help();
                break;
            case "version":
                displayVersion();
                break;
            case "exit":
                isExit = true;
                break;
            case "quit":
                isExit = true;
            default:
                System.out.println("I didn't understand the command: \"" + userCommand + "\"");
                break;
        }
    }

    private static void parseInsertString(String insertRowString) {
            /*
            INSERT INTO TABLE (ID,NAME,AGE) CUSTOMERS VALUES (1,HelloWorld,3);
            */
        System.out.println("STUB: Calling parseInsertString(String s) to process queries");
        System.out.println("Parsing the string:\"" + insertRowString + "\"");
        insertRowString = insertRowString.toLowerCase();
        boolean insert = true;
        String cols = insertRowString.substring(0, insertRowString.indexOf(")") + 1);
        String vals = insertRowString.substring(insertRowString.indexOf(")") + 1);
        String tableName = vals.trim().split(" ")[0];
        String tableNamefile = tableName + ".tbl";

        Matcher mcols = Pattern.compile("\\((.*?)\\)").matcher(cols);
        Matcher mvals = Pattern.compile("\\((.*?)\\)").matcher(vals);


        cols = mcols.find() ? mcols.group(1).trim() : "";
        vals = mvals.find() ? mvals.group(1).trim() : "";
        String columns[] = cols.split(",");
        String values[] = vals.split(",");
        columns = removeWhiteSpacesInArray(columns);
        values = removeWhiteSpacesInArray(values);
        Table table = null;
        Set colNames = columnOrdinalHelper.getKeySet(tableName);
        HashSet<String> colNullVals = new HashSet<>();
        try {
            table = new Table(tableNamefile);
            // to perform the order of the insert based on the ordinal position
            TreeMap<String, String> colOrder = new TreeMap<>();
            // to map the colunm with data
            HashMap<String, String> colVals = new HashMap<>();

            for (int i = 0; i < columns.length; i++) {
                //preserving the order of the columns as given to ordinal positions
                colOrder.put(columnOrdinalHelper.getProperties(tableName.concat(".").concat(columns[i])), columns[i]);
                //mappng column name wth value
                colVals.put(columns[i], values[i]);
            }
            long pos = checkIfTablePageHasSpace(tableNamefile, Integer.parseInt(recordLengthHelper.getProperties(tableName.concat(".").concat(Constant.recordLength))));
            if (pos != -1) {
                long indexTowrite = pos;
                int noOfColumns = Integer.parseInt(recordLengthHelper.getProperties(tableName.concat(".").concat(Constant.numberOfColumns)));

                for (Object s : colNames) {
                    if (!colOrder.containsValue(String.valueOf(s).substring(String.valueOf(s).indexOf('.')+1))){
                        colNullVals.add(String.valueOf(s));
                    }
                }
//
//
//                }

                for(String s : colNullVals){
                    if(columnNotNullHelper.getProperties(s)!=null){
                        System.out.println("Column cannot be null : "+s);
                        insert = false;
                    }
                    break;
                }

                for (int i = 1; i <= noOfColumns; i++) {
                    if (colOrder.containsKey(String.valueOf(i)) && insert) {
                        pos = RecordFormat.writeRecordFormat(columnTypeHelper.getProperties(tableName.concat(".").concat(colOrder.get(String.valueOf(i)))), table, pos, colVals.get(colOrder.get(String.valueOf(i))));
                        colNames.remove(tableName.concat(".").concat(colOrder.get(String.valueOf(i))));
                    }
                }
                Iterator it = colNames.iterator();
                while (it.hasNext() && insert) {
                    String colName = (String) it.next();
                    String nullValue = String.valueOf(RecordFormat.getRecordFormat(columnTypeHelper.getProperties(colName)));
                    pos = RecordFormat.writeRecordFormat(nullValue, table, pos, null);
                }
                table.page.updateArrOfRecLocInPageHeader((short) indexTowrite);
                table.page.updateNoOfRecInPageHeader();
                table.page.updateStartOfContent((short) indexTowrite);
            } else {
                //TODO: Splitting
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String[] removeWhiteSpacesInArray(String[] columns) {
        ArrayList<String> toreturn = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            if (!columns[i].equals(" ")) {
                toreturn.add(columns[i].trim());
            }

        }
        return toreturn.toArray(new String[toreturn.size()]);
    }


    /**
     * Stub method for dropping tables
     *
     * @param dropTableString is a String of the user input
     */
    public static void dropTable(String dropTableString) {
        System.out.println("STUB: Calling parseQueryString(String s) to process queries");
        System.out.println("Parsing the string:\"" + dropTableString + "\"");
        String dropTableName = dropTableString.replaceAll(".* ", "").concat(".tbl");
        System.out.println("TableName : " + dropTableName);
        try {
            Table table = new Table(dropTableName);
            table.page.close();
            File file = new File(dropTableName);
//                table.page.setLength(0);
            //TODO : update tables table and columnstable and property files

            if (file.exists())
                file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stub method for executing queries
     *
     * @param queryString is a String of the user input
     */
    public static void parseQueryString(String queryString) {

        System.out.println("STUB: Calling parseQueryString(String s) to process queries");
        System.out.println("Parsing the string:\"" + queryString + "\"");
        queryString =  queryString.toLowerCase();
        String colName = queryString.substring(0,queryString.indexOf("from")+5).trim();
        String colNames[] = removeWhiteSpacesInArray(colName.split(" "));
        ArrayList<String> queryStringList = new ArrayList<String>(Arrays.asList(colNames));
        queryStringList.remove("select");
        queryStringList.remove("from");
        String condition = "";
        String keyValueCond[] = new String[]{};
        String tableName = "";
        if(queryString.contains("where")) {
            tableName = queryString.substring(queryString.indexOf("from") + 5, queryString.indexOf("where")).trim();
            condition = queryString.substring(queryString.indexOf("where")+6, queryString.length()).trim();
            keyValueCond = removeWhiteSpacesInArray(condition.split(" "));
        }else
            tableName = queryString.substring(queryString.indexOf("from")+5).trim();
        try {
            Table table = new Table(tableName.concat(".tbl"));
            int noOfRecords = table.page.getNoOfRecords();
            long pos = table.page.getStartofContent();
            TreeMap<String, String> colOrder = columnOrdinalHelper.getColumnsInOrdinalPositionOrder(tableName);
            int recordLength = Integer.parseInt(recordLengthHelper.getProperties(tableName.concat(".").concat(Constant.recordLength)));
            if(keyValueCond.length>0){

            }else{
                Iterator it = colOrder.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry<String, String> entryPair = (Map.Entry<String, String>) it.next();
//                    ReadResult<Object> readResult = table.page.readIntasByte(pos);
//                    System.out.println(readResult.getT());
                    ReadResult<Object> readResult = RecordFormat.readRecordFormat(columnTypeHelper.getProperties(entryPair.getValue()),table,pos);
                    System.out.println("RESULT COLUMN NAME : "+entryPair.getValue()+"  Value : "+readResult.getT());

                }

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stub method for creating new tables
     *
     * @param createTableString is a String of the user input
     */
    public static HashMap<String, ArrayList<String>> parseCreateString(String createTableString, boolean metadata) throws FileNotFoundException {

            /*
            CREATE TABLE CUSTOMERS( ID INT PRIMARY KEY,NAME TEXT NOT NULL,AGE INT);
            */

        System.out.println("STUB: Calling your method to create a table");
        System.out.println("Parsing the string:\"" + createTableString + "\"");
        createTableString = createTableString.toLowerCase();
        String tablename = createTableString.substring(0, createTableString.indexOf("(")).split(" ")[2].trim();
        String tablenamefile = tablename + ".tbl";
        Table newTable = new Table(tablenamefile, Constant.leafNodeType);
        HashMap<String, ArrayList<String>> columndata = new HashMap<>();
        TreeMap<Integer, String> columnOrdinalPosition = new TreeMap<>();
        int record_length = 0;
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(createTableString);
        while (m.find()) {
            String cols = m.group(1);
            String singlecol[] = cols.split(",");
            ArrayList<String> colname;
            int ordinalPosition = 1;
            for (int i = singlecol.length - 1; i >= 0; i--) {


                colname = new ArrayList<>();
                singlecol[i] = singlecol[i].trim();
                String colNameType[] = singlecol[i].split(" ");
                colNameType = removeWhiteSpacesInArray(colNameType);
                //columntype
                colname.add(0, colNameType[1]);

                columnTypeHelper.setProperties(tablename.concat(".").concat(colNameType[0]), colNameType[1]);
                record_length = record_length + RecordFormat.getRecordFormat(colNameType[1]);
                colname.add(1, "yes");
                //ordinaltype
                colname.add(2, String.valueOf(++ordinalPosition));
                columnOrdinalPosition.put(ordinalPosition, tablename.concat(".").concat(colNameType[0]));
                if (colNameType.length == 4) {
                    if (colNameType[2].equals("primary")) {
                        colname.set(1, "pri");
                        colname.set(2, String.valueOf(1));
                        columnOrdinalPosition.remove(ordinalPosition);
                        columnOrdinalPosition.put(1, tablename.concat(".").concat(colNameType[0]));
                        --ordinalPosition;
                    } else
                        colname.set(1, "no");
                    columnNotNullHelper.setProperties(tablename.concat(".").concat(colNameType[0]), "NOT NULL");
                }
                columndata.put(colNameType[0], colname);
            }

        }

        Iterator it = columnOrdinalPosition.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            columnOrdinalHelper.setProperties(String.valueOf(pair.getValue()), String.valueOf(pair.getKey()));
        }
        recordLengthHelper.setProperties(tablename.concat(".").concat(Constant.recordLength), String.valueOf(record_length));
        recordLengthHelper.setProperties(tablename.concat(".").concat(Constant.numberOfColumns), String.valueOf(columnOrdinalPosition.size()));
        if (!metadata) {
            updateTablesTable(tablename, record_length);
            updateColumnsTable(tablename, columndata);
        }
        return columndata;

    }

    private static void updateColumnsTable(String tablename, HashMap<String, ArrayList<String>> columndata) {
        try {
            Table davisbase_columns = new Table(Constant.davisbase_columns_file);
            Iterator it = columndata.entrySet().iterator();
            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();
                String attribute = (String) pair.getKey();
                ArrayList<String> values = (ArrayList<String>) pair.getValue();

                //insert rowId
                int rowId = davisbase_columns.page.getNoOfRecords();
                String isPrimary = values.get(1).equals("pri") ? "yes" : "no";
                String isNullable = values.get(1).equals("yes") ? "yes" : "no";
                String insertString = "INSERT INTO TABLE(ROWID, TABLE_NAME, COLUMN_NAME, DATA_TYPE, ORDINAL_POSITION, COLUMN_KEY, IS_NULLABLE) DAVISBASE_COLUMNS (" + ++rowId + "," + tablename + "," + attribute + "," + values.get(0) + "," + values.get(2) +"," + isPrimary + "," + isNullable + ")";

                parseInsertString(insertString);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long checkIfTablePageHasSpace(String tablenamefile, int record_length) {
        long pos = -1;
        Table table = null;
        try {
            table = new Table(tablenamefile);
            long firstAvailable = table.page.getFirstAvailableFreeByte();
            long startofContent = table.page.getStartofContent();
            long indexTowrite = startofContent - record_length;
            if (indexTowrite > firstAvailable)
                pos = indexTowrite;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pos;

    }

    private static void updateTablesTable(String tablename, int record_length) {
        try {
            Table davisbase_tables = new Table(Constant.davisbase_tables_file);
            int rowId = davisbase_tables.page.getNoOfRecords();
            String insertString = "INSERT INTO TABLE (ROWID,TABLE_NAME,AVG_LENGTH) DAVISBASE_TABLES (" + ++rowId + "," + tablename + "," + record_length + ")";
            parseInsertString(insertString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
private static void updateTablesTable(String tablename, int record_length) {
        try {
            Table davisbase_tables = new Table(Constant.davisbase_tables_file);
//            long indexTowrite = checkIfTablePageHasSpace(Constant.davisbase_tables_file,record_length);
//            long pos = indexTowrite;
//            long pos = 0;
//            if(indexTowrite!=-1){
                  */
/*
                        * INSERT INTO TABLE (ID,NAME,AGE) CUSTOMERS VALUES (1,HelloWorld,3);
                        *"CREATE TABLE DAVISBASE_TABLES(ROWID INT PRIMARY KEY , TABLE_NAME TEXT NOT NULL, AVG_LENGTH SMALLINT)";
                        * *//*

                //TODO: to get from table and get column headersfrom column table update row count
                int rowId = davisbase_tables.page.getNoOfRecords();
                String insertString = "INSERT INTO TABLE (ROWID,TABLE_NAME,AVG_LENGTH) DAVISBASE_TABLES ("+ ++rowId +","+tablename+","+record_length+")";

                parseInsertString(insertString);
//
//            }else{
                //TODO: split the page
//                System.out.println("PAGE SPLITTING");
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/

//insert rowId
              /*  pos = davisbase_tables.page.writeIntasByte(rowId+1,indexTowrite);
//                //insert table name
                pos = davisbase_tables.page.writeStringasByte(tablename,davisbase_tables.page.getFilePointer());
//
                pos = davisbase_tables.page.writeIntasByte(0,davisbase_tables.page.getFilePointer());
                //insert recordlength
/////                davisbase_tables.page.seek(pos);
                pos =davisbase_tables.page.writeShortasByte((short) Integer.parseInt(recordLengthHelper.getProperties(tablename.concat(".").concat(Constant.recordLength))),davisbase_tables.page.getFilePointer());
                davisbase_tables.page.updateArrOfRecLocInPageHeader((short)indexTowrite);
                davisbase_tables.page.updateNoOfRecInPageHeader();
                davisbase_tables.page.updateStartOfContent((short) indexTowrite);*/

              /*private static void updateColumnsTable(String tablename, HashMap<String, ArrayList<String>> columndata) {
            try {
                Table davisbase_columns = new Table(Constant.davisbase_columns_file);

                long pos, indexTowrite = 0;
                Iterator it = columndata.entrySet().iterator();
                while (it.hasNext()){
                    *//*
                    * "CREATE TABLE DAVISBASE_COLUMNS(ROWID INT PRIMARY KEY , " +
                        "TABLE_NAME TEXT NOT NULL," +
                        "COLUMN_NAME TEXT NOT NULL," +
                        "DATA_TYPE SHORTTEXT NOT NULL, " +
                        "ORDINAL_POSITION TINYINT NOT NULL, " +
                        "COLUMN_KEY SHORTTEXT NOT NULL, " +
                        "IS_NULLABLE SHORTTEXT)";
                    * *//*
                    Map.Entry pair = (Map.Entry) it.next();
                    String attribute = (String) pair.getKey();
                    indexTowrite = checkIfTablePageHasSpace(Constant.davisbase_columns_file,Integer.parseInt(recordLengthHelper.getProperties(tablename.concat(".").concat(Constant.recordLength))));
                    pos = indexTowrite;
                    if(pos!=-1){


                        ArrayList<String> values = (ArrayList<String>) pair.getValue();

                            //insert rowId
                        int rowId = davisbase_columns.page.getNoOfRecords();
                        String isPrimary = values.get(1).equals("pri")?"yes":"no";
                        String isNullable = values.get(1).equals("yes")?"yes":"no";
                        String insertString = "INSERT INTO TABLE(ROWID, TABLE_NAME, COLUMN_NAME, DATA_TYPE, ORDINAL_POSITION, COLUMN_KEY, IS_NULLABLE)" +
                                " DAVISBASE_COLUMNS ("+ ++rowId +","+tablename+","+attribute+","+values.get(0)+","+values.get(2)+
                                ","+ isPrimary + ","+isNullable+")";
                        parseInsertString(insertString);
                      *//*
                        pos = davisbase_columns.page.writeIntasByte(rowId + 1, pos);
                            //insert table name
                        davisbase_columns.page.seek(pos);
                        pos = davisbase_columns.page.writeStringasByte(tablename,davisbase_columns.page.getFilePointer());
                        //insert column name
                        pos = davisbase_columns.page.writeStringasByte(attribute,davisbase_columns.page.getFilePointer());
                        //insert column type
                        pos = davisbase_columns.page.writeShortStringasByte(values.get(0),davisbase_columns.page.getFilePointer());
                        //insert ordinalPosition
                        pos = davisbase_columns.page.writeTinyIntasByte(values.get(2).getBytes(),pos);
                        // insert pri key
                        pos = davisbase_columns.page.writeShortStringasByte(values.get(1).equals("pri")?"yes":"no",davisbase_columns.page.getFilePointer());
                        //is nullable
                        pos = davisbase_columns.page.writeShortStringasByte(values.get(1).equals("yes")?"yes":"no",davisbase_columns.page.getFilePointer());
                        davisbase_columns.page.updateArrOfRecLocInPageHeader((short)indexTowrite);
                        davisbase_columns.page.updateNoOfRecInPageHeader();
                        davisbase_columns.page.updateStartOfContent((short) indexTowrite);
*//*
                    }
                    else{
                        System.out.println("PageSplitting needed : "+attribute);
                    }
//                    it.remove();
                }
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }*/