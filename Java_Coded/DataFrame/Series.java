package DataFrame;

import MachineLearningExceptions.*;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Series<DataType>{

    // Main Method used to test if the Series works as intended.
    public static void main(String [] args){
        System.out.println("Hello World");

        String [] tempList = {"5", "4", "3", "2", "1", "1","5.1","3.5","1.4","0.2"};
        Series<Float> set = new Series<Float>("Float", tempList.length, "Sample");
        
        for (int i = 0; i < tempList.length; i++)
            set.addItem(Float.parseFloat(tempList[i]));
        
        // Series<Float> otherList = new Series<>(set);
        Series<?> otherList = set.duplicate();
        System.out.println(otherList.getIndex(3, 6));
    }

    // Used to determine the type of data stored.
    private String type;

    // Size of the Series.
    // This is used when instantiating the DataType [] list array.
    private int size;

    // Current size while adding elements to the Series
    // This is also essential when getting the index from a Series.
    private int currentIndex;

    // Column name, or name for the Series
    private String name;

    // The list of items to be stored
    private DataType[] list;

    public Series(){
        this("noType", 0, "noName");
    }

    public Series(Series<DataType> other){
        this.type = other.getType();
        this.name = other.getName();
        this.size = other.getSize();
        this.currentIndex = other.currentIndex;
        this.list = Arrays.copyOf(other.list, other.list.length);
    }

    public Series(DataType[] data, String type, String name){
        this.type = type;
        this.name = name;
        this.size = data.length;
        this.currentIndex = data.length;
        this.list = Arrays.copyOf(data, data.length);
    }

    public Series(String type, int size, String name){
        try {
            if (size < 0)
                throw new IllegalArgumentException("Size must be a positive value");
            setList(size);   
            setType(type);
            setSize(size);
            setName(name);
            this.currentIndex = 0;
            this.list = createArray(size);
        } catch (Exception e){
            System.err.println(e);
        }

    }

    @SuppressWarnings("unchecked")
    private DataType[] createArray(int size){
        if (size > 0)
            return (DataType[]) Array.newInstance(list.getClass().getComponentType(), size);
        return (DataType[]) Array.newInstance(Object.class, size);
    }

    @SuppressWarnings("unchecked")
    private DataType[] createArray(DataType [] data){
        if (data != null)
            return (DataType[]) Array.newInstance(data.getClass().getComponentType(), data.length);
        return (DataType[]) Array.newInstance(Object.class, data.length);
    }

    /**
     * Adds a new value to the DataFrame
     */
    public void addItem(DataType item){
        if (this.currentIndex == this.size)
            throw new SeriesOverflowException("Max set size reached.");
        else if (item instanceof DataType == false)
            throw new IllegalArgumentException("The " + getName() + " Series only accept items of the type: " + getType());
        this.list[currentIndex++] =  item;
    }

    /**
     * Make a duplicate deep copy of a Series
     * @return A copy of a specific Series
     */
    public Series<DataType> duplicate(){
        return new Series<DataType>(this);
    }
    
    @Override
    /**
     * Heavily relies on the currentIndex variable.
     */
    public String toString(){
        String resultString = "[ ";

        for (int i = 0; i < currentIndex; i++){
            resultString += this.list[i].toString() + " ";

            if (i != this.currentIndex - 1)
                resultString += ", ";
        }

        return resultString += "]";
    }

// ===================================================================================================================================
//  SETTERS

    private void setType(String type){
        this.type = type;
    }

    private void setSize(int size){
        this.size = size;
    }

    public void setName(String name){
        this.name = name;
    }

    private void setList(int size){
        this.list = (DataType[]) new Object[size];
    }

// ===================================================================================================================================
//  GETTERS

    @SuppressWarnings("unchecked")
    private DataType[] getList(){
        return (DataType[]) Arrays.copyOf(this.list, list.length);
    }

    public String getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public int getSize(){
        return this.size;
    }

    /**
     * 
     * @param index Index of where the data is located
     * @return A Series containing the singular data, type, and name of the series
     */
    public Series<DataType> getIndex(int index){
        int resolvedIndex = index;

        if (index < 0)
            resolvedIndex = this.size - index;

        if (resolvedIndex < 0 || resolvedIndex >= this.size)
            throw new IllegalArgumentException("The parameter index must be within the size of the Series");
        
        DataType[] result = createArray(1);
        result[0] = this.list[resolvedIndex];
        return new Series<>(result, this.type, this.name);
    }

    // TODO: Implement this method
    public Series<DataType> getIndex(int startIndex, int endIndex){
        DataType[] newList = createArray(endIndex - startIndex + 1);
        
        if (startIndex >= currentIndex || startIndex > endIndex)
            throw new IllegalArgumentException("The parameter \"startIndex\" must be less than the object size and less than the endIndex.");
        else if (endIndex >= currentIndex)
            throw new IllegalArgumentException("The parameter \"endIndex\" must be less than the object size.");

        for (int i = startIndex; i <= endIndex; i++)
            newList[i - startIndex] = this.list[i];

        return new Series<DataType>(newList, this.type, this.name);
    }
}