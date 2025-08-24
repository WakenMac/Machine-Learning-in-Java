import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import time

from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split

def slr(file: str | pd.DataFrame, dependent_var: str, independent_var: str, seed: int = 0):
    dataset = None
    if (file == None or independent_var == None or dependent_var == None):
        raise ValueError("The parameters may contain a 'None' value.")
    elif isinstance(file, str):
        dataset = pd.read_csv(file)
    elif isinstance(file, pd.DataFrame):
        dataset = file
    else:
        raise TypeError("File must be a str (file path) or a DataFrame")

    # Converts our independent variable (x) and dependent variable (y) into a numpy array
    X = dataset[independent_var].to_numpy().reshape((-1, 1))
    y = dataset[dependent_var].to_numpy()

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=seed)

    model = LinearRegression()    # Create a new LinearRegression object
    model.fit(X_train, y_train)               # Fit the data into our model

    # Correlation of Determination (R-squared)
    print(f'Model\'s score: {model.score(X_test, y_test)}')
    print(f'{dependent_var}: {model.coef_[0]}')

    return model

def mlr(file: str | pd.DataFrame, independent_vars: list[str], dependent_var: str, seed: int = 0):
    dataset = None
    if (file == None or independent_vars == None or dependent_var == None):
        raise ValueError("The parameters may contain a 'None' value.")
    elif isinstance(file, str):
        dataset = pd.read_csv(file)
    elif isinstance(file, pd.DataFrame):
        dataset = file
    else:
        raise TypeError("File must be a str (file path) or a DataFrame")

    if dependent_var not in dataset.columns:
        raise KeyError(f"There is no column {dependent_var} in the DataFrame.")

    for i in range(len(independent_vars)):
        if independent_vars[i] not in dataset.columns:
            raise KeyError(f"There is no column {independent_vars[i]} in the DataFrame.")

    X = dataset[independent_vars]
    y = dataset[dependent_var].to_numpy()

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=seed)
    model = LinearRegression()
    model.fit(X_train, y_train)

        
    print(f'Model\'s score: {model.score(X_test, y_test)}')
    for i, var in enumerate(independent_vars):
        print(f'{var}: {model.coef_[i]}')
    return model


my_model = slr("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv", 'TV', "Sales")
# print(my_model.predict([[100]]))

# my_model = mlr(file="C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv", independent_vars=["TV", "Radio", "Newspaper"], dependent_var="Sales")
# print(my_model.predict(pd.DataFrame([[100, 100, 100]], columns=['TV', 'Radio', 'Newspaper'])))