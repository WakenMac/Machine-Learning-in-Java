package DataFrame;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
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

    @SuppressWarnings("rawtypes")
    private Series[] columns;
    private int columnSize;
    private int rowSize;
    private String filePath;

    public static void main(String [] args){
        try {
            // DataFrame df = new DataFrame("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv");
            DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine Learning\\Datasets\\Iris.csv");
            System.out.println(df.getHead());
            System.out.println(df.getInfo());
        } catch (Exception e){
            System.err.println(e);
        }
    }

    /**
     * Makes a copy of the passed DataFrame
     * @param df
     */
    public DataFrame(DataFrame df){
        int [] shape = df.getShape();
        if (shape[0] < 1 || shape[1] < 1)
            throw new IllegalArgumentException("Empty DataFrame was used.");
        this.filePath = df.filePath;
        this.rowSize = shape[0];
        this.columnSize = shape[1];
        this.columns = duplicateColumns(df.getColumns());
    }

    public DataFrame(String pathToFile){
        try {
            File file = new File(pathToFile);
            setFilePath(pathToFile);
            prepareDimensions(file);
            prepareFileData(file);
        } catch (Exception e){
            System.err.println(e);
            setFilePath(null);
            setColumnSize(0);
            setRowSize(0);
        }
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
    private void prepareFileData(File file){
        try {
            // PART 1: Prepares the file readers
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            // PART 3: Create the columns
            createColumns(file);

            // Get the data types per column
            String [] types = new String[this.columnSize];
            for (int i = 0; i < this.columnSize; i++){
                types[i] = this.columns[i].getType();
            }

            // Enter the records
            br.readLine(); // Skips the line containing the column names
            for (int i = 0; i < this.rowSize; i++){
                String [] row = br.readLine().split(",");
                for (int j = 0; j < this.columnSize; j++){
                    if (types[j].equals("LocalDate"))
                        this.columns[j].addItem(LocalDate.parse(row[j]));
                    else if (types[j].equals("Float"))
                        this.columns[j].addItem(Float.parseFloat(row[j]));
                    else if (types[j].equals("Integer"))
                        this.columns[j].addItem(Integer.parseInt(row[j]));
                    else if (types[j].equals("Boolean"))
                        this.columns[j].addItem(Boolean.parseBoolean(row[j]));
                    else if (types[j].equals("String"))
                        this.columns[j].addItem(row[j]);
                }
            }

            br.close();
        } catch (Exception e){
            System.err.println("ERROR: prepareFileData() \n" + e + "\n");
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
                else if (type == "Integer") 
                    this.columns[i] = new Series<Integer>(type, this.rowSize, columnNames[i]);
                else if (type == "Boolean")
                    this.columns[i] = new Series<Boolean>(type, this.rowSize, columnNames[i]);
                else if (type == "String")
                    this.columns[i] = new Series<String>(type, this.rowSize, columnNames[i]);
            }

            br.close();
        } catch (Exception e){
            System.err.println(e);
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
            if (data.contains(".")){
                Float.parseFloat(data);
                return "Float";
            } else {
                Integer.parseInt(data);
                return "Integer";
            }
        } catch (Exception e){}

        try {
            if (data.equals("True") || data.equals("true")){
                Boolean.parseBoolean(data);
                return "Boolean";
            }
        } catch (Exception e){}

        return "String";
    }


// ===================================================================================================================================
//  SETTERS

    private void setRowSize(int rowSize){
        this.rowSize = rowSize;
    }

    private void setColumnSize(int columnSize){
        this.columnSize = columnSize;
    }

    private void setFilePath(String filePath){
        this.filePath = filePath;
    }

// ===================================================================================================================================
//  SUBSETTING

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
//  GETTERS
    
    private String getFilePath(){
        return this.filePath;
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
     * Gets the column names and data types for each column
     * @return A String containing the size of the Data Frame and its columns' data types
     */
    public String getInfo(){
        String tempString = "DataFrame info: \n";

        tempString += "Dimension : [ " + this.rowSize + ", " + this.columnSize + " ] \n\nColumns: \n";
        for (int i = 0; i < this.columnSize; i++){
            tempString += "   " + this.columns[i].getName() + " - " + this.columns[i].getType();

            if (i < this.columnSize - 1)
                tempString += "\n";
        }
        return tempString;
    }

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
