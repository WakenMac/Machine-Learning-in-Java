package LinearRegression;
import DataFrame.*;

/**
 * A Machine Learning algorithm to run Linear Regression.
 * 
 * How to use it:
 * 1. Instantiate the model
 * 2. Pass the split DataFrame and call out the train() method
 * 3. When using the predict() method, pass the variables in the same order as how you've trained the data.
 */
public class LinearRegression {
    public static void main (String [] args){
        System.out.println("Hello World!");
        // DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine Learning\\Datasets\\Iris.csv");
        // DataFrame df = new DataFrame("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv");
        DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine-Learning-in-Java-main\\Machine-Learning-in-Java\\Datasets\\Car_Price_Prediction.csv");
        df.setSeed(10);
        System.out.println(df.getInfo());
        System.out.println("\n\n" + df.iloc(0, 7));

        // For Machine Learning
        // df.split(0.7);
        // DataFrame training = df.getTraining();
        // System.out.println(df.getHead());
    }

    // Column names used for the DataFrame
    String [] independentVars;
    String dependentVar;

    // Resulting values to be multiplied to when solving predicting the value of the dependentVar.
    float [] independentPredictors;
    float bias;

    // Dataset to be used for training
    DataFrame trainingDataset;
    
    // Dataset to be used for testing
    DataFrame testingDataset;

    public LinearRegression(){
        this.trainingDataset = this.testingDataset = null;
        this.independentVars = null;
        this.dependentVar = null;
    }

    // TODO: Implement this method
    // Runs the linear regression algorithm
    public void train(DataFrame trainingDataset, String [] independentVars, String dependentVars){

    }

    // TODO: Implement this method
    // Prints out the formula for the linear regression model.
    public String toString(){
        return null;
    }
}
