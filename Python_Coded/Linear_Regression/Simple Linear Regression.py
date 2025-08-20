import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import time

from sklearn.linear_model import LinearRegression

def advertising():
    # Import the dataset
    dataset = pd.read_csv("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv")

    # Converts our independent variable (x) and dependent variable (y) into a numpy array
    x = dataset['TV'].to_numpy().reshape((-1, 1))
    y = dataset['Sales'].to_numpy()

    model = LinearRegression()    # Create a new LinearRegression object
    model.fit(x, y)               # Fit the data into our model
    print(model.intercept_, model.coef_)

    user_input = 100
    coef = model.coef_
    intercept = model.intercept_
    result = coef * user_input + intercept     # This is the same as model.predict()
    print(result)
    print(model.predict([[100]]))         # Predicts what Sales is if TV advertising efforts are at 100.

    # Correlation of Determination (R-squared)
    print(model.score(x, y))

advertising()