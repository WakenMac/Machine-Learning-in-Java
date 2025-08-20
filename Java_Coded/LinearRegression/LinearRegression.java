package LinearRegression;
import java.util.Arrays;

import DataFrame.*;
import MachineLearningExceptions.NoTrainingExecutedException;

/**
 * A Machine Learning algorithm to run Linear Regression.
 * 
 * How to use it:
 * 1. Instantiate the model (LinearRegression lr = new LinearRegression())
 * 2. Pass the split DataFrame and call out the train() method
 * 3. When using the predict() method, pass the variables in the same order as how you've trained the data.
 */
public class LinearRegression {
    public static void main (String [] args){
        long start = System.nanoTime();
        // DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine Learning\\Datasets\\Iris.csv");
        DataFrame df = new DataFrame("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv");
        // DataFrame df = new DataFrame("C:\\Users\\Waks\\Downloads\\USEP BSCS\\Coding\\Machine-Learning-in-Java-main\\Machine-Learning-in-Java\\Datasets\\Car_Price_Prediction.csv");
        
        df.setSeed(10);

        // For Machine Learning
        DataFrame [] splitData = df.split(1);
        DataFrame training = splitData[0];
        DataFrame testing = splitData[1];

        LinearRegression lr = new LinearRegression();
        lr.train(training, "Sales", "TV");
        System.out.println(lr);

        float [] coefs = lr.getRegressionCoefs();
        float intercept = lr.getIntercept();
        System.out.println("\n\nCoefficient: " + coefs[0]);
        System.out.println("Intercept: " + intercept);
        System.out.println((coefs[0] * 100 + intercept));

        float [] predictors = new float[] {100};
        float salesResult = lr.predict(predictors);
        System.out.println("If TV = 100, the Sales will be: " + salesResult);
        long end = System.nanoTime();
        System.out.println(end - start);
        System.out.println((float) (end - start) / 1_000_000_000);
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
        bias = Float.MIN_VALUE;
    }

// ===================================================================================================================================
//  MACHINE LEARNING MODEL

    // TODO: Implement this method
    // Runs the linear regression algorithm
    public void train(DataFrame trainingDataset, String dependentVar, String... independentVars){
        if (trainingDataset == null)
            throw new IllegalArgumentException("The parameter \"trainingDataset\" is null.");
        else if (dependentVar == null)
            throw new IllegalArgumentException("The parameter \"dependentVar\" is null.");
        else if (independentVars == null)
            throw new IllegalArgumentException("The parameter \"independentVars\" is null.");
        else if (!trainingDataset.getDataType(dependentVar).equals("class java.lang.Float"))
            throw new IllegalArgumentException("The selected column for prediction: \"" + dependentVar + "\" must be a float.");

        // Prepare the global data
        this.trainingDataset = trainingDataset;
        this.dependentVar = dependentVar;
        this.independentVars = independentVars;
        this.independentVarClasses = new String[independentVars.length];
        for (int i = 0; i < independentVars.length; i++)
            this.independentVarClasses[i] = trainingDataset.getDataType(independentVars[i]);
        this.independentPredictors = new float[independentVars.length];

        if (independentVars.length == 1)
            simpleLinearRegression();
    }

    public float predict(float... predictors){
        if (trainingDataset == null || dependentVar == null || independentVars == null || independentPredictors == null)
            throw new NoTrainingExecutedException("Unable to run prediction as no training has been called. Call the train() before running the predict().");
        else if (predictors == null || predictors.length < 1)
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
        float sumX, sumY, sumXY, sumX2;                                         // sum of all x, y, x * y, and x * x respectively
        sumX = sumY = sumXY = sumX2 = 0;
        Series<?> iv = this.trainingDataset.select(this.independentVars[0]);    // iv represents x (Independent Var)
        Series<?> dv = this.trainingDataset.select(this.dependentVar);          // dv represents y (Dependent Var)

        for (int i = 0; i < dv.getSize(); i++){
            // Gets the values of X and Y
            Object tempX = iv.getIndex_DataType(i);
            Object tempY = dv.getIndex_DataType(i);

            if (tempX == null || tempY == null)
                continue;

            float x = (Float) iv.getIndex_DataType(i);
            float y = (Float) dv.getIndex_DataType(i);

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        this.bias = ((sumY * sumX2) - (sumX * sumXY)) / ((iv.getSize() * sumX2) - (sumX * sumX));
        independentPredictors[0] = ((iv.getSize() * sumXY) - (sumX * sumY)) / ((iv.getSize() * sumX2) - (sumX * sumX));
    }

    // TODO: Implement this method
    public void multipleLinearRegression(){
        DataFrame iv = this.trainingDataset.select(this.independentVars);    // iv represents x (Independent Var)
        Series<?> dv = this.trainingDataset.select(this.dependentVar);       // dv represents y (Dependent Var)


    }

// ===================================================================================================================================
//  GETTERS

    /**
     * Gets the intercept or bias of the regression model
     * @return  A float containing the bias of the regression model
     */
    public float getIntercept(){
        return this.bias;
    }

    /**
     * Gets the regression coefficients taken from the model
     * @return An array of regression coefficients
     */
    public float[] getRegressionCoefs(){
        return Arrays.copyOf(independentPredictors, independentPredictors.length);
    }

    /**
     * Gets the R-squared score of the Linear Regression model.
     * The score represents the percentage of variance in the dependent variable is influenced by the independent variables
     * 
     * It compares the training dataset's dependent variable, and cross checks it against the model's prediction
     * using the training dataset's independent variable 
     * 
     * Formula for R-Squared:
     * 1 - (SSR / SST)
     * SSR = Residual Sum of Squares (the difference between the true pred and the model's prediction)
     *       Formula: sum((y_true - y_pred) ^ 2)
     * 
     * SST = Total variation (The difference between the true dataset's pred and its mean)
     *       Formula: sum((y_true - y_true_mean) ^ 2)
     * 
     * @param independentColumns The dependent variables used to train the model
     * @param dependentColumn    The independent variable found in the dataset
     * @return A float representing the score of the Linear Model
     */
    public float getScore(Series<?> independentColumns, Series<?> dependentColumn){
        // TODO: Add error handling

        float rSquared = 0;
        /*
         * 1 - (SSR / SST)
         * SSR = Residual Sum of Squares (the difference between the true pred and the model's prediction)
         *       This determines the distance between the true data and the model's prediction
         *       Formula: sum((y_true - y_pred) ^ 2)
         * 
         * SST = Total variation (The difference between the true dataset's pred and its mean)
         *       Formula: sum((y_true - y_true_mean) ^ 2)
         */
        
        //TODO: Implement method

        return rSquared;
    }

    // TODO: Implement this method
    // Prints out the formula for the linear regression model.
    public String toString(){
        if (this.trainingDataset == null || dependentVar == null || independentVars == null)
            return "LinearRegression model (No Training nor Dataset was passed.)";

        String refString = "";
        String regressionFormula = "\nRegression Formula: y = ";
        refString += "Coefficients: ";

        for (int i = 0; i < independentVars.length; i++){
            refString += independentPredictors[i];
            regressionFormula += independentPredictors[i] + " * x" + (i + 1);

            if (i < independentVars.length - 1){
                refString += ", ";
                regressionFormula += " + ";
            }
        }

        return refString += "\nIntercept: " + this.bias + regressionFormula;
    }
}
