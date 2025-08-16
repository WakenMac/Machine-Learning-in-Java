import pandas as pd
import numpy as np

def main():
    df = pd.read_csv("C:/Users/Waks/Downloads/USEP BSCS/Coding/Machine Learning/Datasets/advertising.csv")
    df['Sales'] = df["Sales"] * 1000
    print('Hello World')

main()