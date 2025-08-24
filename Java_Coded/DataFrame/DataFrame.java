package DataFrame;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Random;

import MachineLearningExceptions.*;
import java.io.BufferedReader;

/**
 * A Class mimicing the DataFrames present in R and Python.
 * 
 * Each DataFrame comprises of a/several Series class(es), representing as the column, where each element 
 * in a series represents a partial record
 *  
 * For Visualization:
 * Index | Series 1 | Series 2 |
 *   0   |  Pencil  |   10.00  |
 *   1   | Ballpen  |   20.00  |
 * 
 * It is stored as an array:
 * Series 1 : ["Pencil", "Ballpen"]
 * Series 2 : [10.00, 20.00]
 */
public class DataFrame {
    /**
     * Count the number of columns
     * Count the number of records
     * Capture the DataType for each row
     */

     // The columns used to store the data.
    @SuppressWarnings("rawtypes")
    private Series[] columns;

    // Dimensions of the DataFrame
    private int columnSize;
    private int rowSize;

    // Seed used for methods relying on randomization (i.e., split())
    private int seed;

    public static void main(String [] args){
        // DataFrame df = new DataFrame("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv");
        DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine Learning\\Datasets\\Iris.csv");
        System.out.println(df.getInfo());
        System.out.println(df);
        // System.out.println(df.getHead());
        // System.out.println(df.select("Id", "SepalLengthCm", "PetalLengthCm", "Species")
        //                         .getHead());
        // System.out.println(df.loc("Id", "SepalLengthCm").getHead());
        System.out.println(df.iloc(0, 0, 3, 2).getHead());
    }

    /**
     * Makes a copy of the passed DataFrame
     * @param df
     */
    public DataFrame(DataFrame df){
        int [] shape = df.getShape();
        if (shape[0] < 1 || shape[1] < 1)
            throw new IllegalArgumentException("Empty DataFrame was used.");
        this.rowSize = shape[0];
        this.columnSize = shape[1];
        this.columns = duplicateColumns(df.getColumns());
        this.seed = df.seed;
    }

    public DataFrame(String pathToFile){
        File file = new File(pathToFile);
        prepareDimensions(file);
        prepareFileData(file);
        this.seed = -1;
    }

    private DataFrame(Series<?>[] seriesArray){
        if (seriesArray == null)
            throw new NullPointerException("Cannot access Series as \"seriesArray\" is null.");
        
        int rowSize = seriesArray[0].getSize();
        for (int i = 1; i < seriesArray.length; i++){
            if (seriesArray[i].getSize() != rowSize)
                throw new IllegalArgumentException("Invalid Series \"" + seriesArray[i].getName() + "\" as it has " + seriesArray[i].getSize() + " rows than the supposed " + rowSize + ".");
        }

        this.rowSize = seriesArray[0].getSize();
        this.columnSize = seriesArray.length;
        this.columns = duplicateColumns(seriesArray);
        this.seed = -1;
    }

// ===================================================================================================================================
//  INSTANTIATING THE DATA FRAME

    /**
     * Prepares the row and column size taken from the csv file
     * This is made for the instantiation of the columns, and their rows.
     * @param file
     */
    private void prepareDimensions(File file){
        try {
            if (file == null){
                setColumnSize(0);
                setRowSize(0);
                return;
            }

            // Prepares the file for reading
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            String columnNames = br.readLine();
            if (columnNames == null){
                setColumnSize(0);
                setRowSize(0);
                br.close();
                return;
            }

            // Prepares the columns
            setColumnSize(columnNames.split(",").length);

            int rows = 0;
            while (br.readLine() != null)
                rows++;
            setRowSize(rows);

            br.close();
        } catch (Exception e){
            System.err.println(e);
        }
    }

    /**
     * The method that fills in the records for each Series based on the given csv file.
     * This method assumes that the file is not null and is existing.
     * @param file
     */
    @SuppressWarnings("unchecked")
    private void prepareFileData(File file){
        try {
            // PART 1: Prepares the file readers
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            // PART 3: Create the columns
            createColumns(file);

            // Enter the records
            br.readLine(); // Skips the line containing the column names
            for (int i = 0; i < this.rowSize; i++){
                String [] row = br.readLine().split(",");
                for (int j = 0; j < this.columnSize; j++){
                    handleAddItem(this.columns[j], row[j]);
                }
            }

            br.close();
        } catch (Exception e){
            System.err.println("ERROR: prepareFileData() \n" + e + " " + e.getStackTrace()[0].getMethodName() + " " + e.getMessage() + "\n");
        }
    }

    /**
     * Creates the columns for our DataFrame, done by instantiating the Series array.
     * @param file The file containing the data to be read.
     */
    private void createColumns(File file){
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            // Instantiate the columns
            this.columns = new Series[this.columnSize];

            // Get the column names
            String [] columnNames = br.readLine().split(",");
            String [] firstRow = br.readLine().split(",");

            for (int i = 0; i < this.columnSize; i++){
                String type = getType(firstRow[i]);

                if (type == "Unknown"){
                    br.close();
                    throw new IllegalArgumentException("Undefinable Data Type: " + firstRow[i]);
                } else if (type == "LocalDate")
                    this.columns[i] = new Series<LocalDate>(type, this.rowSize, columnNames[i]);
                else if (type == "Float")
                    this.columns[i] = new Series<Float>(type, this.rowSize, columnNames[i]);
                else if (type == "Double")
                    this.columns[i] = new Series<Double>(type, this.rowSize, columnNames[i]);
                else if (type == "Short") 
                    this.columns[i] = new Series<Short>(type, this.rowSize, columnNames[i]);
                else if (type == "Integer") 
                    this.columns[i] = new Series<Integer>(type, this.rowSize, columnNames[i]);
                else if (type == "Long") 
                    this.columns[i] = new Series<Long>(type, this.rowSize, columnNames[i]);
                else if (type == "Boolean")
                    this.columns[i] = new Series<Boolean>(type, this.rowSize, columnNames[i]);
                else if (type == "Character")
                    this.columns[i] = new Series<Character>(type, this.rowSize, columnNames[i]);
                else if (type == "String")
                    this.columns[i] = new Series<String>(type, this.rowSize, columnNames[i]);
            }

            br.close();
        } catch (Exception e){
            System.err.println(e);
        }
    }

    private void createColumns(Series<?> [] otherColumns, int otherRowSize){
        for (int i = 0; i < this.columns.length; i++){
            String type = this.columns[i].getType();
            String colName = this.columns[i].getName();

            if (type == "Unknown"){
                throw new IllegalArgumentException("Undefinable Data Type: " + type);
            } else if (type == "LocalDate")
                otherColumns[i] = new Series<LocalDate>(type, otherRowSize, colName);
            else if (type == "Float")
                otherColumns[i] = new Series<Float>(type, otherRowSize, colName);
            else if (type == "Double")
                otherColumns[i] = new Series<Double>(type, otherRowSize, colName);
            else if (type == "Short") 
                otherColumns[i] = new Series<Short>(type, otherRowSize, colName);
            else if (type == "Integer") 
                otherColumns[i] = new Series<Integer>(type, otherRowSize, colName);
            else if (type == "Long") 
                otherColumns[i] = new Series<Long>(type, otherRowSize, colName);
            else if (type == "Boolean")
                otherColumns[i] = new Series<Boolean>(type, otherRowSize, colName);
            else if (type == "Character")
                otherColumns[i] = new Series<Character>(type, otherRowSize, colName);
            else if (type == "String")
                otherColumns[i] = new Series<String>(type, otherRowSize, colName);
        }
    }

    /**
     * Gets the object's data type given a string. Similar to a JSON object being transformed into a variable
     * @param data
     * @return String representing its data type (LocalDate, Float, Integer, Boolean, String)
     */
    private String getType(String data){
        try {
            LocalDate.parse(data);
            return "LocalDate";
        } catch (Exception e){}

        try {
            if (!data.contains("."))
                throw new IllegalArgumentException("");
            Float.parseFloat(data);
            return "Float";
        } catch (Exception e){}

        try {
            if (!data.contains("."))
                throw new IllegalArgumentException("");
            Double.parseDouble(data);
            return "Double";
        } catch (Exception e){}

        try {
            Integer.parseInt(data);
            return "Integer";
        } catch (Exception e){}

        try {
            Short.parseShort(data);
            return "Short";
        } catch (Exception e){}

        try {
            Integer.parseInt(data);
            return "Integer";
        } catch (Exception e){}

        try {
            if (data.equals("True") || data.equals("true")){
                Boolean.parseBoolean(data);
                return "Boolean";
            }
        } catch (Exception e){}

        return (data.length() == 1)? "Character" : "String";
    }
    
// ===================================================================================================================================
//  SERIES DUPLICATION

    /**
     * Creates a deep copy of a DataFrame
     * @param otherCols The Series or columns to be copied
     * @return A separate and independently editable Series.
     */
    private Series<?>[] duplicateColumns(Series<?>[] otherCols){
        Series<?>[] copy = new Series[otherCols.length];

        for (int i = 0; i < otherCols.length; i++){
            copy[i] = new Series<>(otherCols[i]);
        }

        return copy;
    }

// ===================================================================================================================================
//  SETTERS

    private void setRowSize(int rowSize){
        this.rowSize = rowSize;
    }

    private void setColumnSize(int columnSize){
        this.columnSize = columnSize;
    }

    public void setSeed(int seed){
        if (seed == -1)
            throw new IllegalArgumentException("Seed must be a positive non-zero number.");
        this.seed = seed;
    }

// ===================================================================================================================================
//  SUBSETTING

    public Series<?> select(String colName){
        return select(new String[] {colName}).getColumns()[0];
    }

    /**
     * Selects the columns from a DataFrame
     * 
     * @param colNames List of column names to be selected
     * @return A new DataFrame composing of only the selected columns. 
     *         Note that the order of the Strings affect the column order.
     */
    public DataFrame select(String... colNames){
        Series<?>[] seriesArray = new Series[colNames.length];

        // Adds the DataFrames columns to a HashSet
        Hashtable<String, Series<?>> set = new Hashtable<>();
        for (int i = 0; i < this.columnSize; i++)
            set.put(this.columns[i].getName(), this.columns[i]);
        
        // Iterate over the HashSet if the columns are found there
        for (int i = 0; i < colNames.length; i++){
            Series<?> result = set.getOrDefault(colNames[i], null);
            if (result == null)
                throw new UnknownColumnException("The column \"" + colNames[i] + "\" doesn't exist in the DataFrame.");
            seriesArray[i] = new Series<>(result);
        }
        
        // Instantiate and return the new DataFrame
        return new DataFrame(seriesArray);
    }

    /**
     * Splits the DataFrame to return the selected columns from the startCol till the endCol column
     * 
     * Example: 
     * The DataFrame "df" contains the following columns in this order: Id, Name, Number, Address
     * df.loc("Id", "Number"); // Returns a DataFrame containing the Id, Name, and Number columns.
     * 
     * @param startCol The starting column
     * @param endCol The last column to be selected in the DataFrame
     * @return
     */
    public DataFrame loc(String startCol, String endCol){
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < this.columns.length; i++){
            if (startIndex == -1 && this.columns[i].getName().equals(startCol))
                startIndex = i;
            
            if (startIndex != 1 && this.columns[i].getName().equals(endCol)){
                endIndex = i;
                break;
            }
        }

        if (startIndex == -1)
            throw new UnknownColumnException("The column \"" + startCol + "\" doesn't exist in the DataFrame.");
        else if (endIndex == -1)
            throw new UnknownColumnException("The column \"" + endCol + "\" doesn't exist in the DataFrame.");

        String [] list = new String[endIndex - startIndex + 1];
        for (int i = startIndex; i <= endIndex; i++)
            list[i - startIndex] = this.columns[i].getName();

        return select(list);
    }

    public Series<?> iloc(int rowIndex, int columnIndex){
        if (rowIndex >= this.rowSize || columnIndex >= this.columnSize)
            throw new IllegalArgumentException("The parameters \"row\" or \"column\" must be within the dimension of the DataFrame.");
        
        return new Series<>(this.columns[columnIndex].getIndex(rowIndex));
    }

    /**
     * Splicing the DataFrame by selecting the start, end 
     * @param startRow
     * @param endRow
     * @param startCol
     * @param endCol
     * @return
     */
    public DataFrame iloc(int startRow, int startCol, int endRow, int endCol){
        if (startCol > endCol)
            throw new IllegalArgumentException("The parameter \"startCol\" must be less than or equal to the \"endCol\" parameter");
        else if (startRow > endRow)
            throw new IllegalArgumentException("The parameter \"startRow\" must be less than or equal to the \"endRow\" parameter");
        else if (startRow < 0 || endRow < 0)
            throw new IllegalArgumentException("The parameter \"startRow\" and \"endRow\" must be a positive number");
        else if (endRow > this.rowSize || endCol > this.columnSize)
            throw new IllegalArgumentException("The parameter \"endRow\" and \"endCol\" must be a within the DataFrame's dimensions.");
        
        String [] colNames = new String[endCol - startCol + 1];
        for (int i = startCol; i <= endCol; i++)
            colNames[i - startCol] = this.columns[i].getName();

        Series<?>[] newColumns = new Series[endCol - startCol + 1];
        for (int i = startCol; i <= endCol; i++)
            newColumns[i] = this.columns[i].getIndex(startRow, endRow);

        return new DataFrame(newColumns).select(colNames);
    }

// ===================================================================================================================================
//  GETTERS

    public int getSeed(){
        return this.seed;
    }

    private Series<?>[] getColumns(){
        return duplicateColumns(this.columns);
    }

    public String getColumnNames(){
        String resultString = "[ ";

        for (int i = 0; i < this.columns.length; i++){
            resultString += this.columns[i].getName() + " ";

            if (i != this.columns.length - 1)
                resultString += ", ";
        }

        return resultString += "]";
    }

    public String getHead(){
        return getHead(6);
    }

    /**
     * Method used to get a number of rows, defined by numberOfRows variable, from the DataFrame
     * @param numberOfRows Number of rows to be printed
     * @return A String version of a DataFrame containing the rows numbered from 0 to the numberOfRows - 1;
     */
    public String getHead(int numberOfRows){
        String tempString = "";
        if (numberOfRows < 1)
            throw new IllegalArgumentException("numberOfRows parameter must be a positive non-zero number.");

        numberOfRows = (this.rowSize > numberOfRows)? numberOfRows : this.rowSize;

        // Adds the column names
        for (int i = 0; i < this.columnSize; i++)
            tempString += this.columns[i].getName() + "  |  ";
        tempString += "\n";

        // Adds the data
        for (int i = 0; i < numberOfRows; i++){
            for (int j = 0; j < this.columnSize; j++)
                tempString += this.columns[j].getIndex(i) + "  |  ";
            tempString += "\n";
        }
        
        return tempString;
    }

    /**
     * Method to get the dimension of the data frame
     * @return an integer array of 2 elements [row size, column size]
     */
    public int[] getShape(){
        if (this.columnSize == 0)
            return new int[] {0, 0}; 
        else 
            return new int[] {this.rowSize, this.columnSize};
    }

    /**
     * Method to return a string containing the dimensions of the DataFrame
     * @return A String containing the row and column dimension of a DataFrame
     */
    public String getShape_String(){
        return "[ " + this.rowSize + ", " + this.columnSize + " ]";
    }

    /**
     * Gets the column names and data types for each column
     * @return A String containing the size of the Data Frame and its columns' data types
     */
    public String getInfo(){
        String tempString = "DataFrame info: \n";

        tempString += "Dimension : [ " + this.rowSize + ", " + this.columnSize + " ] \n\nColumns: \n";
        for (int i = 0; i < this.columnSize; i++){
            tempString += "   " + this.columns[i].getName() + " - " + this.columns[i].getIndex_DataType(0).getClass();

            if (i < this.columnSize - 1)
                tempString += "\n";
        }
        return tempString;
    }

    public String getDataType(String colName){
        for (int i = 0; i < this.columns.length; i++){
            if (this.columns[i].getName().equals(colName))
                return this.columns[i].getIndex_DataType(0).getClass().toString();
        }

        throw new UnknownColumnException("The column \"" + colName + "\" doesn't exist in the DataFrame.");
    }

// ===================================================================================================================================
//  MACHINE LEARNING RELATED

    // TODO: Implement this method
    /**
     * Splits the DataFrame for training and testing.
     * 
     * @param partition Percentage of data used for the testing data
     * @return A two element array containing the DataFrame for testing and training respectively
     */
    public DataFrame[] split(double partition){
        if (partition > 1)
            throw new IllegalArgumentException("The partition parameter must be set between 0 to 1.");
        
        // Get the number of rows for partitioning / training
        int trainingRow = (int) Math.floor(partition * this.rowSize);
        Random dice = (this.seed != -1)? new Random(this.seed) : new Random();

        int [] indices = new int[this.rowSize];
        for (int i = 0; i < this.rowSize; i++)
            indices[i] = i;
        
        // Shuffle the indices
        for (int i = 0; i < this.rowSize; i++){
            int randomIndex = dice.nextInt(this.rowSize);
            int temp = indices[i];
            indices[i] = indices[randomIndex];
            indices[randomIndex] = temp;
        }

        // Instantiate the training and testing DataFrames
        Series<?>[] trainingColumns = new Series[this.columnSize];
        Series<?>[] testingColumns = new Series[this.columnSize];
        createColumns(trainingColumns, trainingRow);
        createColumns(testingColumns, this.rowSize - trainingRow);

        // Fill the training and testing DataFrames
        for (int i = 0; i < trainingRow; i++){
            for (int j = 0; j < this.columnSize; j++){
                handleAddItem(trainingColumns[j], this.columns[j].getIndex_DataType(indices[i]));
            }
        }

        for (int i = trainingRow; i < this.rowSize; i++){
            for (int j = 0; j < this.columnSize; j++){
                handleAddItem(testingColumns[j], this.columns[j].getIndex_DataType(indices[i]));
            }
        }

        return new DataFrame[] {new DataFrame(trainingColumns), new DataFrame(testingColumns)};
    }

    /**
     *              No switches?
     * ⠀⣞⢽⢪⢣⢣⢣⢫⡺⡵⣝⡮⣗⢷⢽⢽⢽⣮⡷⡽⣜⣜⢮⢺⣜⢷⢽⢝⡽⣝
        ⠸⡸⠜⠕⠕⠁⢁⢇⢏⢽⢺⣪⡳⡝⣎⣏⢯⢞⡿⣟⣷⣳⢯⡷⣽⢽⢯⣳⣫⠇
        ⠀⠀⢀⢀⢄⢬⢪⡪⡎⣆⡈⠚⠜⠕⠇⠗⠝⢕⢯⢫⣞⣯⣿⣻⡽⣏⢗⣗⠏⠀
        ⠀⠪⡪⡪⣪⢪⢺⢸⢢⢓⢆⢤⢀⠀⠀⠀⠀⠈⢊⢞⡾⣿⡯⣏⢮⠷⠁⠀⠀
        ⠀⠀⠀⠈⠊⠆⡃⠕⢕⢇⢇⢇⢇⢇⢏⢎⢎⢆⢄⠀⢑⣽⣿⢝⠲⠉⠀⠀⠀⠀
        ⠀⠀⠀⠀⠀⡿⠂⠠⠀⡇⢇⠕⢈⣀⠀⠁⠡⠣⡣⡫⣂⣿⠯⢪⠰⠂⠀⠀⠀⠀
        ⠀⠀⠀⠀⡦⡙⡂⢀⢤⢣⠣⡈⣾⡃⠠⠄⠀⡄⢱⣌⣶⢏⢊⠂⠀⠀⠀⠀⠀⠀
        ⠀⠀⠀⠀⢝⡲⣜⡮⡏⢎⢌⢂⠙⠢⠐⢀⢘⢵⣽⣿⡿⠁⠁⠀⠀⠀⠀⠀⠀⠀
        ⠀⠀⠀⠀⠨⣺⡺⡕⡕⡱⡑⡆⡕⡅⡕⡜⡼⢽⡻⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        ⠀⠀⠀⠀⣼⣳⣫⣾⣵⣗⡵⡱⡡⢣⢑⢕⢜⢕⡝⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        ⠀⠀⠀⣴⣿⣾⣿⣿⣿⡿⡽⡑⢌⠪⡢⡣⣣⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        ⠀⠀⠀⡟⡾⣿⢿⢿⢵⣽⣾⣼⣘⢸⢸⣞⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
        ⠀⠀⠀⠀⠁⠇⠡⠩⡫⢿⣝⡻⡮⣒⢽⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     * @param col
     * @param obj
     */
    private void handleAddItem(Series<?> col, Object obj){
        // Ensures that the obj variable is not of class Object or String.
        if (!obj.getClass().toString().equals("class java.lang.Object") && 
            !obj.getClass().toString().equals("class java.lang.String")){
            if (obj instanceof LocalDate)
                ((Series<LocalDate>) col).addItem((LocalDate) obj);
            else if (obj instanceof Float)
                ((Series<Float>) col).addItem((Float) obj);
            else if (obj instanceof Double)
                ((Series<Double>) col).addItem((Double) obj);
            else if (obj instanceof Short)
                ((Series<Short>) col).addItem((Short) obj);
            else if (obj instanceof Integer)
                ((Series<Integer>) col).addItem((Integer) obj);
            else if (obj instanceof Long)
                ((Series<Long>) col).addItem((Long) obj);
            else if (obj instanceof Boolean)
                ((Series<Boolean>) col).addItem((Boolean) obj);
            else if (obj instanceof Character)
                ((Series<Character>) col).addItem(((String) obj).charAt(0));
            else if (obj instanceof String && ((String) obj).length() > 0)
                ((Series<String>) col).addItem((String) obj);
            else if (obj instanceof String && ((String) obj).length() == 0)
                ((Series<String>) col).addItem((String) null);
            else 
                System.out.println("Item: " + obj + " has no data type.");
        } else {
            if (col.getType().equals("LocalDate"))
                ((Series<LocalDate>) col).addItem(LocalDate.parse((String) obj));
            else if (col.getType().equals("Float"))
                ((Series<Float>) col).addItem(Float.parseFloat((String) obj));
            else if (col.getType().equals("Double"))
                ((Series<Double>) col).addItem(Double.parseDouble((String) obj));
            else if (col.getType().equals("Short"))
                ((Series<Short>) col).addItem(Short.parseShort((String) obj));
            else if (col.getType().equals("Integer"))
                ((Series<Integer>) col).addItem(Integer.parseInt((String) obj));
            else if (col.getType().equals("Long"))
                ((Series<Long>) col).addItem(Long.parseLong((String) obj));
            else if (col.getType().equals("Boolean"))
                ((Series<Boolean>) col).addItem(Boolean.parseBoolean((String) obj));
            else if (col.getType().equals("Character"))
                ((Series<Character>) col).addItem(((String) obj).charAt(0));
            else if (col.getType().equals("String"))
                ((Series<String>) col).addItem((String) obj);
            else 
                System.out.println("Item: " + obj + " has no data type.");
        }
    }

// ===================================================================================================================================
//  PRINT FORMATTING

    @Override
    public String toString(){
        String tempString = "";

        // Adds the column names
        for (int i = 0; i < this.columnSize; i++)
            tempString += this.columns[i].getName() + "  |  ";
        tempString += "\n";

        if (this.rowSize < 10){
            // Adds the data
            for (int i = 0; i < this.rowSize; i++){
                for (int j = 0; j < this.columnSize; j++)
                    tempString += this.columns[j].getIndex(i) + "  |  ";
                tempString += "\n";
            }
        } else {
            for (int i = 0; i < this.rowSize; i++){
                for (int j = 0; j < this.columnSize; j++)
                    tempString += this.columns[j].getIndex(i) + "  |  ";
                tempString += "\n";

                if (i == 5){
                    String continueString = "";
                    for (int k = 0; k < this.columnSize; k++)
                        continueString += "       .";
                    continueString += "\n";
                    tempString += continueString + continueString + continueString;
                    i = this.rowSize - 6;
                }
            }
        }

        return tempString;
    }
}
