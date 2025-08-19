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
        DataFrame df = new DataFrame("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv");
        // DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine-Learning-in-Java-main\\Machine-Learning-in-Java\\Datasets\\Car_Price_Prediction.csv");
        df.setSeed(10);
        System.out.println(df.getInfo());
        System.out.println(df.select("Sales").getIndex_DataType(0).getClass());

        // For Machine Learning
        DataFrame [] splitData = df.split(1);
        DataFrame training = splitData[0];
        DataFrame testing = splitData[1];

        System.out.println(training);
        System.out.println(testing);

        LinearRegression lr = new LinearRegression();
        lr.train(training, "Sales", "TV");
        float salesResult = lr.predict(100, 200);
        System.out.println("If TV = 100, the Sales will be: " + salesResult);
    }

    // Column names used for the DataFrame
    String [] independentVars;
    String [] independentVarClasses;
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
    public void train(DataFrame trainingDataset, String dependentVar, String... independentVars){
        if (trainingDataset == null)
            throw new IllegalArgumentException("The parameter \"trainingDataset\" is null.");
        else if (dependentVar == null)
            throw new IllegalArgumentException("The parameter \"dependentVar\" is null.");
        else if (independentVars == null)
            throw new IllegalArgumentException("The parameter \"independentVars\" is null.");
        else if (trainingDataset.getDataType(dependentVar) != "class java.lang.Float")
            throw new IllegalArgumentException("The selected column for prediction: \"" + dependentVar + "\" must be a float.");

        // Prepare the global data
        this.trainingDataset = trainingDataset;
        this.dependentVar = dependentVar;
        this.independentVars = independentVars;
        this.independentVarClasses = new String[independentVars.length];
        for (int i = 0; i < independentVars.length; i++)
            this.independentVarClasses[i] = independentVars[i].getClass().toString();
        this.independentPredictors = new float[independentVars.length];

        if (independentVars.length == 1)
            simpleLinearRegression();
    }

    public float predict(float... predictors){
        if (predictors == null || predictors.length < 1)
            throw new IllegalArgumentException("The parameter \"predictors\" must not be null nor empty.");
        else if (predictors.length != independentVars.length){
            String concat = "";
            for (int i = 0; i < independentVars.length; i++){
                concat += independentVars[i];
                if (i < independentVars.length - 1)
                    concat += ", ";
            }
            throw new IllegalArgumentException("The parameter \"predictors\" doesn't match the number of independent variables: " + concat);
        }

        float result = bias;
        for (int i = 0; i < independentVars.length; i++)
            result += independentPredictors[i] * predictors[i];
        return result;
    }

    private void simpleLinearRegression(){
        float sumX, sumY, sumXY, sumX2;
        sumX = sumY = sumXY = sumX2 = 0;
        Series<?> iv = this.trainingDataset.select(this.independentVars[0]);    // iv represents x (Independent Var)
        Series<?> dv = this.trainingDataset.select(this.dependentVar);          // dv represents y (Dependent Var)

        for (int i = 0; i < dv.getSize(); i++){
            System.out.println(dv.getIndex_DataType(i).getClass());

            float x = (Float) iv.getIndex_DataType(i);
            float y = (Float) dv.getIndex_DataType(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        bias = ((sumY * sumX2) - (sumX * sumXY)) / ((iv.getSize() * sumXY) - sumX2);
        independentPredictors[0] = ((iv.getSize() * sumXY) - (sumX * sumY)) / ((iv.getSize() * sumX2) - (sumX * sumX));
    }

    // TODO: Implement this method
    // Prints out the formula for the linear regression model.
    public String toString(){
        return null;
    }
}
