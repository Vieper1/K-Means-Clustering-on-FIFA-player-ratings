from sys import argv
from random import randint
import csv
import math
import os

import plotly.plotly as py
import plotly.graph_objs as graph_obj
import plotly
plotly.tools.set_credentials_file(username='naiduvishal13', api_key='kQ7Wg8Umcmjc8YJ6lt3p')


#-------------------------------------------------- Variables
# VAR_csv_ref = 'vehicles_cropped.csv'
VAR_csv_ref = 'datasetnew.csv'
# VAR_csv_ref = 'CoordSample.csv'
VAR_itr_max = 1000;
VAR_n_clusters = 7;


n_points = 0
points = list()
comparison_points_ref = list()
comparison_points = list()
cluster_refs = list()
distance_list = list()
#-------------------------------------------------- Variables










#-------------------------------------------------- Functions
#---------------------------------------- Graph
def plot_graph():
    data = list()
    
    for i in range(0, VAR_n_clusters):
        x_array = list()
        y_array = list()
        for point in points:
            if point[2] == i:
                x_array.append(point[0])
                y_array.append(point[1])
        

        col_r = randint(30, 255)
        col_g = randint(30, 255)
        col_b = randint(30, 255)

        trace = graph_obj.Scatter(
            x = y_array,
            y = x_array,
            mode = 'markers',
            marker = dict(
                color = 'rgba(' + str(col_r) + ', ' + str(col_g) + ', ' + str(col_b) + ', 0.95)',
                line = dict(
                    color = 'rgba(217, 217, 217, 1.0)',
                        width = 0.5,
                ),
                symbol = 'circle',
                size = 12,
            ),
            name = 'Cluster ' + str(i)
        )
        data.append(trace)

    

    
    # plotly.iplot(data, filename='basic-scatter')
    plot_url = py.plot(data, filename='basic-scatter')
    print(plot_url)
#---------------------------------------- Graph












#---------------------------------------- Main
def update_distance_list():
    while distance_list:
        distance_list.pop()

    for i in range(0, n_points):
        temp_array = list()
        for comparison_point in comparison_points:
            temp_array.append(dist_2_pts(comparison_point, points[i]))
            # print(dist_2_pts(comparison_point, points[i]))
        distance_list.append(temp_array)
    

def update_clusters():
    while cluster_refs:
        cluster_refs.pop()

    for i in range(0, n_points):
        points[i][2] = distance_list[i].index(min(distance_list[i]))


def update_cluster_refs():
    for i in range(0, n_points):
        cluster_refs.append(points[i][2])

        
def update_comparison_points():
    for i in range(0, VAR_n_clusters):
        comparison_points[i] = find_mean(i)
#---------------------------------------- Main














#---------------------------------------- Util
def print_proper():
    print("+---------------+---------------+---------------+")
    print("|\tX\t|\tY\t|    Cluster\t|")
    print("+---------------+---------------+---------------+")
    for point in points:
        print("|\t%s\t|\t%s\t|\t%s\t|" % (point[0], point[1], point[2]))
    print("+---------------+---------------+---------------+")

def print_cluster():
    for i in range(0, VAR_n_clusters):
        temp_array = list()
        for point in points:
            if point[2] == i:
                temp_array.append(point)
        print("Cluster %s: %s" % (i, temp_array))

def get_current_refs():
    temp_array = list()
    for point in points:
        temp_array.append(point[2])
    return temp_array


def find_mean(cluster_no):
    sum_x = 0.0
    sum_y = 0.0
    count = 0.0
    for point in points:
        if cluster_no == point[2]:
            sum_x = sum_x + float(point[0])
            sum_y = sum_y + float(point[1])
            count = count + 1
    if count == 0:
        return [0, 0]
    avg_x = sum_x / count
    avg_y = sum_y / count
    return [avg_x, avg_y]

            
def dist_2_pts(point1, point2):
    diff0 = float(point1[0]) - float(point2[0])
    diff1 = float(point1[1]) - float(point2[1])
    
    
    return math.sqrt((diff0*diff0) + (diff1*diff1))
#---------------------------------------- Util
#-------------------------------------------------- Functions

















#-------------------------------------------------- MAIN
os.system('cls')

with open(VAR_csv_ref, 'rb') as f_obj:
    n_points = sum(1 for _ in f_obj)

with open(VAR_csv_ref, 'rb') as f_obj:
    reader = csv.reader(f_obj)
    for row in reader:
        points.append([row[0], row[1], -1])

i = 0
while i < VAR_n_clusters:
    temp = randint(0, n_points-1)
    if temp in comparison_points_ref:
        continue
    comparison_points_ref.append(temp)
    i = i + 1


for temp in comparison_points_ref:
    comparison_points.append([
        points[temp][0],
        points[temp][1]
    ])
# [x_coord, y_coord, cluster]


update_distance_list()
update_clusters()
update_comparison_points()

itr_counter = 0;
# while set(cluster_refs) == set(get_current_refs()):
while itr_counter < VAR_itr_max:
    update_cluster_refs()

    update_distance_list()
    update_clusters()
    update_comparison_points()

    itr_counter = itr_counter + 1
    
print_proper()
print_cluster()

plot_graph()

#-------------------------------------------------- MAIN