package LinearRegression;
import DataFrame.*;


public class LinearRegression {
    public static void main (String [] args){
        System.out.println("Hello World!");
        DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine Learning\\Datasets\\Iris.csv");
        DataFrame otherDf = new DataFrame(df);
        System.out.println(otherDf.getHead(3));
    }

    String [] independentVars;
    String dependentVar;
    DataFrame trainingDataset, testingDataset;
    int seed;

    public LinearRegression(){
        this.trainingDataset = this.testingDataset = null;
        this.independentVars = null;
        this.dependentVar = null;
        this.seed = -1;
    }

    public void setSeed(int seed){
        this.seed = seed;
    }

    // TODO: Implement this method
    // Runs the linear regression algorithm
    public void train(DataFrame trainingDataset){

    }
}
