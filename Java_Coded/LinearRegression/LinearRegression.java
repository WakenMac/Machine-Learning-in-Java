package LinearRegression;
import DataFrame.*;


public class LinearRegression {
    public static void main (String [] args){
        System.out.println("Hello World!");
        DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine Learning\\Datasets\\Iris.csv");
        DataFrame otherDf = new DataFrame(df);
        System.out.println(otherDf.getHead(3));
    }
}
