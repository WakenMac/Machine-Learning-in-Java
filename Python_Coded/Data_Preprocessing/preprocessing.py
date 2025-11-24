
# Import the dataset
import pandas as pd
import numpy as np
import os
DATASET_PATH = os.getenv("BURGERS_PATH")
dataset = pd.read_csv(DATASET_PATH)
X = dataset.iloc[:, 0:-1].values
y = dataset.iloc[:, -1].values

# The dataset.iloc[:, :].values transforms the DF to a numpy array

# Handle missing data
from sklearn.impute import SimpleImputer
imputer = SimpleImputer(missing_values=np.nan, strategy='mean')
X[:, :] = imputer.fit_transform(X[:, :])

# Split the data
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=1)

# Feature Scaling (Standardization)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=1)

from sklearn.preprocessing import StandardScaler
ss = StandardScaler()
X_train[:, :] = ss.fit_transform(X_train[:, :])
X_test[:, :] = ss.transform(X_test[:, :])

print(X_train)

# Feature Scaling (Normalization)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=1)

from sklearn.preprocessing import MinMaxScaler
mms = MinMaxScaler()
X_train[:, :] = mms.fit_transform(X_train[:, :])
X_test[:, :] = mms.transform(X_test[:, :])

# Comparison
print(X_train)