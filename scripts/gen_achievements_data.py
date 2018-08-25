#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest
import forest_dict

parameter_values = []
skill_points_values = []
for i in xrange(7):
    parameter_values.append([])
    skill_points_values.append([])
parameter_values[0] = [5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100]
parameter_values[1] = [10,100,500,1000,2000,4000,10000,25000,40000,60000,100000,150000,300000,500000,1000000]
parameter_values[2] = [10,20,50,100,300,800,4000,10000,20000,40000,100000]
parameter_values[3] = [10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200,210,220,230,240,250,260,270,280,290,300,400,1000]
parameter_values[4] = [10,20,50,100,200,500,1000,4000,10000]
parameter_values[5] = [10,25,50,100,200,500,1000,5000,10000]
parameter_values[6] = [5,10,15,20,25,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200,210,220,230,240,250,260,270,280,290,300,310,320,330,340,350,365,500,730,1000,1500,2500,4000,6500,10000]

skill_points_values[0] = [1,10,30,50,80,100,120,160,220,260,300,350,400,500,650,800,1000,2500,4000,10000]
skill_points_values[1] = [1,5,10,50,100,300,1000,2500,4000,6000,10000,15000,30000,50000,100000]
skill_points_values[2] = [1,5,10,50,100,300,1000,2500,4000,6000,10000]
skill_points_values[3] = [10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200,210,220,230,240,250,260,270,280,290,300,400,1000]
skill_points_values[4] = [1,5,10,25,50,150,500,2000,4000]
skill_points_values[5] = [5,10,25,50,100,300,1000,5000,10000]
skill_points_values[6] = [10,20,30,50,75,100,150,200,250,300,350,400,450,500,550,600,650,700,750,800,850,900,950,1000,1050,1100,1150,1200,1250,1300,1350,1400,1450,1500,1550,1600,1650,1700,2000,5000,7300,10000,15000,25000,40000,65000,100000]

data = []
achievements = forest_dict.get_achievements()
for idx, achievement in enumerate(achievements):
    required_parameter_name, achievement_name = achievement
    achievement_id = idx + 1
    data.append({
        "achievement_id": achievement_id,
        "required_parameter_name": required_parameter_name,
        "required_parameter_values": ";".join(map(str, parameter_values[idx])),
        "skill_points_values": ";".join(map(str, skill_points_values[idx])),
        "name": achievement_name
        })

forest.print_xml_header()

for row in data:
    print '    <achievement achievement_id="{}" required_parameter_name="{}" required_parameter_values="{}" skill_points_values="{}" name="{}" />'.format(row['achievement_id'], row['required_parameter_name'], row['required_parameter_values'], row['skill_points_values'], row['name'])

forest.print_xml_footer()