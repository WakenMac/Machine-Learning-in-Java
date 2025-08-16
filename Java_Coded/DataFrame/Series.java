package DataFrame;

import MachineLearningExceptions.*;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Series<DataType>{
    public static void main(String [] args){
        System.out.println("Hello World");

        String [] tempList = {"5", "4", "3", "2", "1", "1","5.1","3.5","1.4","0.2"};
        try {
            Integer.parseInt(tempList[0]);
            Series<Float> set = new Series<Float>("Float", tempList.length, "Sample");
            
            for (int i = 0; i < tempList.length; i++)
                set.addItem(Float.parseFloat(tempList[i]));
            Series<?> otherList = new Series<>(set.getList(), set.getType(), set.getName());
            System.out.println(otherList);
        } catch (Exception e){
            System.err.println(e);
        }
    }

    private String type;
    private int size;
    private int currentIndex;
    private String name;
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
        try {
            setType(type);
            setName(name);
            setSize(data.length);
            this.currentIndex = data.length;
            this.list = Arrays.copyOf(data, data.length);
        } catch (Exception e){
            System.err.println(e);
        }
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

    private DataType[] createArray(int size){
        if (size > 0)
            return (DataType[]) Array.newInstance(list.getClass().getComponentType(), size);
        return (DataType[]) Array.newInstance(Object.class, size);
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

    private void setName(String name){
        this.name = name;
    }

    private void setList(int size){
        this.list = (DataType[]) new Object[size];
    }

// ===================================================================================================================================
//  GETTERS

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
    public DataType[] getIndex(int startIndex, int endIndex){
        return null;
    }

    private void incrementSize(){
        this.size++;
    }

}