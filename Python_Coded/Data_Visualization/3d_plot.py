import os
import pandas as pd
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
import seaborn as sns

DATASET_PATH = os.getenv("HOMELESSNESS_PATH")
dataset = pd.read_csv(DATASET_PATH)

df_mean = dataset[['region', 'state_pop', 'family_members']].groupby(['region']).mean()
df_count = dataset[['region']].value_counts()
df_final = df_mean.join(other=df_count, on='region', how='inner', validate='one_to_one').reset_index()

print(df_final['region'])

xpos = df_final['state_pop']
ypos = df_final['family_members']
zpos = [0] * df_final['count']
height = df_final['count']
color = ['red' if region in ('East North Central', 'East South Central') else 
         'orange' if region in ('West North Central', 'West South Central') else 
         'yellow' if region in ('Mid-Atlantic', 'South Atlantic') else 
         'green' if region in ('Pacific', 'Mountain') else 
         'purple' if region == 'New England' else 
         'gray' for region in iter(df_final['region'])]

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

ax.bar3d(xpos, ypos, zpos, dx = 500_000, dy = 10_000, dz=height, color=color)
ax.set_title('Population between members per family')
ax.set_xlabel('State Population')
ax.set_ylabel('Family Members')
ax.set_zlabel('Count of States per Region')

plt.show()
