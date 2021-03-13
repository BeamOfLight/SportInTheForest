#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest

def print_data(data):
	for row in data:
		print '    <skill skill_id="{}" skill_group_id="{}" skill_level="{}" required_level="{}" skill_points="{}" extra_fitness_points="{}"\
		  extra_resistance="{}" extra_multiplier="{}" extra_bonus_chance="{}" extra_bonus_multiplier="{}" info="{}" label="{}" duration="{}"\
		  reuse="{}" specialisation="{}" extra_fitness_points_ratio1="{}" extra_fitness_points_ratio2="{}" extra_resistance_ratio1="{}" extra_resistance_ratio2="{}"\
		  extra_multiplier_ratio1="{}" extra_multiplier_ratio2="{}" extra_bonus_chance_ratio1="{}" extra_bonus_chance_ratio2="{}"\
		  extra_bonus_multiplier_ratio1="{}" extra_bonus_multiplier_ratio2="{}" extra_regeneration_base="{}" extra_regeneration_ratio="{}"\
		  splash_multiplier="{}" />'.format(
		  	row['skill_id'],
		  	row['skill_group_id'],
		  	row['skill_level'],
		  	row['required_level'],
		  	row['skill_points'],
		  	row['extra_fitness_points'],
		  	row['extra_resistance'],
		  	row['extra_multiplier'],
		  	row['extra_bonus_chance'],
		  	row['extra_bonus_multiplier'],
		  	row['info'],
		  	row['label'],
		  	row['duration'],
		  	row['reuse'],
		  	row['specialisation'],
		  	row['extra_fitness_points_ratio1'],
		  	row['extra_fitness_points_ratio2'],
		  	row['extra_resistance_ratio1'],
		  	row['extra_resistance_ratio2'],
		  	row['extra_multiplier_ratio1'],
		  	row['extra_multiplier_ratio2'],
		  	row['extra_bonus_chance_ratio1'],
		  	row['extra_bonus_chance_ratio2'],
		  	row['extra_bonus_multiplier_ratio1'],
		  	row['extra_bonus_multiplier_ratio2'],
		  	row['extra_regeneration_base'],
		  	row['extra_regeneration_ratio'],
		  	row['splash_multiplier']
		)

forest.print_xml_header()
print_data(forest.get_skills_resistance_data())
print_data(forest.get_skills_fitness_points_data())
print_data(forest.get_skills_multiplier_data())
print_data(forest.get_skills_bonus_data())
print_data(forest.get_skills_active_multiplier_data())
print_data(forest.get_skills_active_resistance_data())
print_data(forest.get_skills_active_self_restoration_data())
print_data(forest.get_skills_gain_team_fitness_points_data())
print_data(forest.get_skills_gain_team_multiplier_data())
print_data(forest.get_skills_gain_team_resistance_data())

print_data(forest.get_skills_passive_regeneration_data())
print_data(forest.get_skills_passive_multiplier_data())
print_data(forest.get_skills_passive_resistance_data())
print_data(forest.get_skills_active_splash_data())

print_data(forest.get_skills_active_my_team_restoration_data())
print_data(forest.get_skills_active_teammate_restoration_data())


#print_data(forest.get_skills_test_data())
forest.print_xml_footer()
