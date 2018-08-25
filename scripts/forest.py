#! /usr/bin/env python
# -*- coding: utf-8 -*-
import math

SPECIALISATION_NO = 0
SPECIALISATION_RESULT = 1
SPECIALISATION_RESISTANCE = 2
SPECIALISATION_REGENERATION = 3

def get_exp_ratio():
	return 1.007

def get_locations_count():
	return 34

def get_max_character_position():
	return 5

def get_total_skill_points(location_id):
	id = 1
	total_skill_points = 0
	for cur_location_id in xrange(1, location_id + 1):
		quest_cnt = int(round((float(cur_location_id) + 1) / 2))
		for position in xrange(1, 6):
			for level in xrange(1, quest_cnt + 1):
				total_skill_points += id
				id += 1
	return total_skill_points

def get_quest_count(location_id):
	return int(round((float(location_id) + 1) / 2))

def print_xml_header():
	print '<?xml version="1.0" encoding="utf-8"?>'
	print '<data>'

def print_xml_footer():
	print '</data>'

def get_required_level(location_id):
	required_levels = [1,5,10,14,18,21,24,27,30,32,34,36,38,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62]
	if location_id < 0 or location_id >= len(required_levels):
		return None
	else:
		return required_levels[location_id - 1]

def get_expected_total_result(location_id):
	total_result = 0
	expected_result = 1
	for cur_location_id in xrange(1, location_id + 1):
		quest_cnt = int(round((float(cur_location_id) + 1) / 2))
		for position in xrange(1, 6):
			for level in xrange(1, quest_cnt + 1):
				total_result += expected_result
				expected_result += 1

	return total_result

def get_skills_interval_id_by_level(player_level):
	skills_interval_id = 1
	location_id = skills_interval_id
	while (location_id < get_locations_count()):
		if player_level >= get_required_level(location_id):
			location_id += 1
			continue
		else:
			return location_id - 1
	
	return get_locations_count()

def get_total_level_quest_count(player_level):
	total_sum = 0
	for level in xrange(1, player_level + 1):
		total_sum += get_quest_count(get_skills_interval_id_by_level(level))
	return total_sum

def get_total_skill_quest_count(skills_interval_id):
	total_sum = 0
	if skills_interval_id > get_locations_count():
		return 0

	for location_id in xrange(1, skills_interval_id + 1):
		total_sum += get_quest_count(location_id)
	return total_sum

def get_skill_points(location_id):
	return int(round(get_total_skill_points(location_id + 1) / 55)) * 10

def get_skills_resistance_data():
	data = []
	skill_group_id = 1
	expected_result = 0
	resistance_last = 0
	for location_id in xrange(1, get_locations_count() + 1):
		quest_cnt = get_quest_count(location_id)
		expected_result += quest_cnt * 5	
		skill_id = skill_group_id * 100 + location_id
		skill_level = location_id
		required_level = get_required_level(location_id)
		skill_points = get_skill_points(location_id)
		resistance_new = int(round(expected_result*2.41))
		resistance_diff = resistance_new - resistance_last
		resistance_current = resistance_last
		
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': skill_points,
			'extra_fitness_points': 0,
			'extra_resistance': resistance_diff,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'resistance_current': resistance_current,
			'resistance_new': resistance_new,
			'info': 'Сопротивление +{} -> +{}'.format(str(resistance_current), str(resistance_new)),
			'label':'',
			'duration': 0,
			'reuse': 50,
			'specialisation': SPECIALISATION_NO,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		resistance_last = resistance_new
	return data



def get_skills_fitness_points_data():
	data = []
	skill_group_id = 2
	expected_result = 0
	fitness_points_last = 0
	for location_id in xrange(1, get_locations_count() + 1):
		quest_cnt = get_quest_count(location_id)
		expected_result += quest_cnt * 5
		skill_id = skill_group_id * 100 + location_id
		skill_level = location_id
		required_level = get_required_level(location_id)
		skill_points = get_skill_points(location_id)
		fitness_points_new = expected_result*2
		fitness_points_diff = fitness_points_new - fitness_points_last
		fitness_points_current = fitness_points_last
		
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': skill_points,
			'extra_fitness_points': fitness_points_diff,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'fitness_points_current': fitness_points_current,
			'fitness_points_new': fitness_points_new,
			'info': 'Фитнес-очки +{} -> +{}'.format(str(fitness_points_current), str(fitness_points_new)),
			'label':'',
			'duration': 0,
			'reuse': 50,
			'specialisation': SPECIALISATION_NO,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		fitness_points_last = fitness_points_new
	return data

def get_skills_multiplier_data():
	data = []
	skill_group_id = 3
	expected_result = 0
	multiplier_last = 0
	for location_id in xrange(1, get_locations_count() + 1):
		quest_cnt = get_quest_count(location_id)
		expected_result += quest_cnt * 5	
		skill_id = skill_group_id * 100 + location_id
		skill_level = location_id
		required_level = get_required_level(location_id)
		skill_points = get_skill_points(location_id)
		multiplier_new = expected_result*.01
		multiplier_diff = multiplier_new - multiplier_last
		multiplier_current = multiplier_last
		
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': skill_points,
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': multiplier_diff,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'multiplier_current': multiplier_current,
			'multiplier_new': multiplier_new,
			'info': 'Множитель +{} -> +{}'.format(str(multiplier_current), str(multiplier_new)),
			'label':'',
			'duration': 0,
			'reuse': 50,
			'specialisation': SPECIALISATION_NO,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		multiplier_last = multiplier_new
	return data

def get_skills_bonus_info_msg(bonus_multiplier_current, bonus_multiplier_new, bonus_chance_current, bonus_chance_new):
	if bonus_chance_current == bonus_chance_new:
		return 'Множитель бонуса +{0:.2f} -> +{1:.2f}'.format(bonus_multiplier_current, bonus_multiplier_new)
	return 'Множитель бонуса +{0:.2f} -> +{1:.2f} и шанс бонуса +{2:.1f}% -> +{3:.1f}%'.format(bonus_multiplier_current, bonus_multiplier_new, 100.0 * bonus_chance_current, 100.0 * bonus_chance_new)		

def get_skills_bonus_data():
	data = []
	skill_group_id = 4
	expected_result = 0
	bonus_chance_last = 0
	bonus_multiplier_last = 0
	for location_id in xrange(1, get_locations_count() + 1):
		quest_cnt = get_quest_count(location_id)
		expected_result += quest_cnt * 5
		skill_level = location_id
		skill_id = skill_group_id * 100 + skill_level
		required_level = get_required_level(location_id)
		skill_points = get_skill_points(location_id)
		bonus_chance_new = round(expected_result * 0.173) / 1000
		bonus_chance_diff = bonus_chance_new - bonus_chance_last
		bonus_chance_current = bonus_chance_last
		bonus_multiplier_new = round(expected_result * 0.1) / 100
		bonus_multiplier_diff = bonus_multiplier_new - bonus_multiplier_last
		bonus_multiplier_current = bonus_multiplier_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': skill_points,
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance': bonus_chance_diff,
			'extra_bonus_multiplier': bonus_multiplier_diff,
			'bonus_chance_current': bonus_chance_current,
			'bonus_chance_new': bonus_chance_new,
			'bonus_multiplier_current': bonus_multiplier_current,
			'bonus_multiplier_new': bonus_multiplier_new,
			'info': get_skills_bonus_info_msg(bonus_multiplier_current, bonus_multiplier_new, bonus_chance_current, bonus_chance_new),
			'label':'',
			'duration': 0,
			'reuse': 50,
			'specialisation': SPECIALISATION_NO,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})	
		bonus_chance_last = bonus_chance_new
		bonus_multiplier_last = bonus_multiplier_new
	return data

#def get_skills_bonus_multiplier_data():
#	data = []
#	skill_group_id = 5
#	expected_result = 0
#	bonus_multiplier_last = 0
#	for location_id in xrange(1, get_locations_count() + 1):
#		quest_cnt = get_quest_count(location_id)
#		expected_result += quest_cnt * 5
#		skill_level = (location_id + 1)
#		skill_id = skill_group_id * 100 + skill_level
#		required_level = get_required_level(location_id)
#		skill_points = int(round(get_total_skill_points(location_id + 1) / 40)) * 10
#		bonus_multiplier_new = expected_result * 0.003
#		bonus_multiplier_diff = bonus_multiplier_new - bonus_multiplier_last
#		bonus_multiplier_current = bonus_multiplier_last
#		data.append({
#			'skill_id': skill_id,
#			'skill_group_id': skill_group_id,
#			'skill_level': skill_level,
#			'required_level': required_level,
#			'skill_points': skill_points,
#			'extra_fitness_points': 0,
#			'extra_resistance': 0,
#			'extra_multiplier': 0.0,
#			'extra_bonus_chance':0.0,
#			'extra_bonus_multiplier': bonus_multiplier_diff,
#			'bonus_multiplier_current': bonus_multiplier_current,
#			'bonus_multiplier_new': bonus_multiplier_new,
#			'info': 'Множитель бонуса +{} -> +{}'.format(str(bonus_multiplier_current), str(bonus_multiplier_new))
#		})	
#		bonus_multiplier_last = bonus_multiplier_new
#	return data

def get_skills_multiplier_info_msg(multiplier1_current, multiplier1_new, multiplier2_current, multiplier2_new):
	if multiplier2_current == multiplier2_new:
		return 'Множитель +{} -> +{}'.format(multiplier1_current, multiplier1_new)
	return 'Множитель +{} и +{}% -> +{} и +{}%'.format(multiplier1_current, (int) (100 * multiplier2_current), multiplier1_new, (int) (100 * multiplier2_new))

def get_skills_active_multiplier_data():
	data = []
	skill_group_id = 5
	multiplier1_last = 0
	multiplier2_last = 0
	for location_id in xrange(1, 20):
		skill_id = skill_group_id * 100 + location_id
		skill_level = location_id
		required_level = get_required_level(location_id)
		skill_points = get_skill_points(location_id)
		multiplier1_new = 0.25 * (location_id + 1)
		multiplier1_diff = multiplier1_new - multiplier1_last
		multiplier1_current = multiplier1_last
		
		multiplier2_new = 0
		if skill_level > 3:
			multiplier2_new = 0.1 * (skill_level - 3)
		multiplier2_diff = multiplier2_new - multiplier2_last
		multiplier2_current = multiplier2_last

		specialisation = SPECIALISATION_NO
		if skill_level > 3:
			specialisation = SPECIALISATION_RESULT

		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': skill_points,
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': multiplier1_diff,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'multiplier_current': multiplier1_current,
			'multiplier_new': multiplier1_new,
			'info': get_skills_multiplier_info_msg(multiplier1_current, multiplier1_new, multiplier2_current, multiplier2_new),
			'label': 'Множитель +{} и +{}% на 1 ход'.format(multiplier1_new, (int) (100 * multiplier2_new)),
			'duration': 1,
			'reuse': 12 - skill_level / 3,
			'specialisation': specialisation,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': multiplier2_diff,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		multiplier1_last = multiplier1_new
		multiplier2_last = multiplier2_new

	return data

def get_skills_active_resistance_data():
	data = []
	skill_group_id = 6
	resistance_last = 0
	for location_id in xrange(1, 20):
		skill_id = skill_group_id * 100 + location_id
		skill_level = location_id
		required_level = get_required_level(location_id)
		skill_points = get_skill_points(location_id)
		resistance_new = (skill_level + 1) * 500
		resistance_diff = resistance_new - resistance_last
		resistance_current = resistance_last
		specialisation = SPECIALISATION_NO
		if skill_level > 3:
			specialisation = SPECIALISATION_RESISTANCE
		
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': skill_points,
			'extra_fitness_points': 0,
			'extra_resistance': resistance_diff,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'resistance_current': resistance_current,
			'resistance_new': resistance_new,
			'info': 'Сопротивление +{} -> +{}'.format(str(resistance_current), str(resistance_new)),
			'label': 'Сопротивление +{} на 1 ход'.format(str(resistance_new)),
			'duration': 1,
			'reuse': 12 - skill_level / 3,
			'specialisation': specialisation,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		resistance_last = resistance_new

	return data

def get_skills_active_self_restoration_data():
	data = []
	skill_group_id = 7
	skill_level = 1
	value1_last = 0
	value2_last = 0
	for required_level in [1, 5, 10, 14, 18, 21, 24, 27, 30, 32, 34, 36, 38, 40]:
		skill_id = skill_group_id * 100 + skill_level

		value1_new = 5 * skill_level
		value1_diff = value1_new - value1_last
		value1_current = value1_last

		value2_new = 0.5 * (skill_level + 1)
		value2_diff = value2_new - value2_last
		value2_current = value2_last

		specialisation = SPECIALISATION_NO
		if specialisation > 3:
			specialisation = SPECIALISATION_REGENERATION

		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(skill_level),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Свои ФО +{} + результат x {} -> +{} + результат x {}'.format(str(value1_current), str(value2_current), str(value1_new), str(value2_new)),
			'label':'Восстановление своих ФО',
			'duration': 1,
			'reuse': 50,
			'specialisation': specialisation,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': value1_diff,
			'extra_regeneration_ratio': value2_diff,
			'splash_multiplier': 0
		})
		value1_last = value1_new
		value2_last = value2_new
		skill_level += 1

	return data

def get_skills_gain_team_fitness_points_data():
	data = []
	skill_group_id = 8
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40, 42, 44, 46, 48, 50, 52]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = 0.10 + (skill_level - 1)* 0.06
		value_diff = value_new - value_last
		value_current = value_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Максимум ФО команды +{}% -> +{}%'.format((int)(100 * value_current), (int)(100 * value_new)),
			'label':'Максимум ФО команды +{}%'.format((int)(100 * value_new)),
			'duration': 4 + skill_level,
			'reuse': 50,
			'specialisation': SPECIALISATION_REGENERATION,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': value_diff,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		value_last = value_new
		skill_level+=1
	return data

def get_skills_gain_team_resistance_data():
	data = []
	skill_group_id = 9
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40, 42, 44, 46, 48, 50, 52]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = 0.20 + (skill_level - 1)* 0.10
		value_diff = value_new - value_last
		value_current = value_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Сопротивление команды +{}% -> +{}%'.format((int)(100 * value_current), (int)(100 * value_new)),
			'label':'Сопротивление команды +{}%'.format((int)(100 * value_new)),
			'duration': 4 + skill_level,
			'reuse': 10,
			'specialisation': SPECIALISATION_RESISTANCE,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': value_diff,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		value_last = value_new
		skill_level+=1
	return data

def get_skills_gain_team_multiplier_data():
	data = []
	skill_group_id = 10
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40, 42, 44, 46, 48, 50, 52]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = 0.06 + (skill_level - 1)* 0.03
		value_diff = value_new - value_last
		value_current = value_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Множитель команды +{}% -> +{}%'.format((int)(100 * value_current), (int)(100 * value_new)),
			'label':'Множитель команды +{}%'.format((int)(100 * value_new)),
			'duration': 4 + skill_level,
			'reuse': 10,
			'specialisation': SPECIALISATION_RESULT,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': value_diff,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		value_last = value_new
		skill_level+=1
	return data

def get_skills_passive_regeneration_data():
	data = []
	skill_group_id = 12
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = (int) (round((skill_level * skill_level) / 0.9) + 1)
		value_diff = value_new - value_last
		value_current = value_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Регенерация +{} -> +{}'.format((int)(value_current), (int)(value_new)),
			'label':'Регенерация +{}'.format((int)(value_new)),
			'duration': 0,
			'reuse': 50,
			'specialisation': SPECIALISATION_REGENERATION,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': value_diff,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		value_last = value_new
		skill_level+=1
	return data

def get_skills_passive_multiplier_data():
	data = []
	skill_group_id = 13
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = 0.10 + (skill_level - 1)* 0.10
		value_diff = value_new - value_last
		value_current = value_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Множитель +{}% -> +{}%'.format((int)(100 * value_current), (int)(100 * value_new)),
			'label':'Множитель +{}%'.format((int)(100 * value_new)),
			'duration': 4 + skill_level,
			'reuse': 50,
			'specialisation': SPECIALISATION_RESULT,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': value_diff,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		value_last = value_new
		skill_level+=1
	return data

def get_skills_passive_resistance_data():
	data = []
	skill_group_id = 14
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = 0.10 + (skill_level - 1)* 0.10
		value_diff = value_new - value_last
		value_current = value_last
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Сопротивление +{}% -> +{}%'.format((int)(100 * value_current), (int)(100 * value_new)),
			'label':'Сопротивление +{}%'.format((int)(100 * value_new)),
			'duration': 4 + skill_level,
			'reuse': 50,
			'specialisation': SPECIALISATION_RESISTANCE,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': value_diff,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': 0
		})
		value_last = value_new
		skill_level+=1
	return data


def get_skills_active_splash_data():
	data = []
	skill_group_id = 15
	skill_level = 1
	value_last = 0
	for required_level in [14, 21, 27, 32, 36, 40, 42, 44, 45]:
		skill_id = skill_group_id * 100 + skill_level
		value_new = 0.25 + (skill_level - 1)* 0.12
		value_diff = value_new - value_last
		value_current = value_last
		info_value_from = int(round(100 * value_current))
		info_value_to = int(round(100 * value_new))
		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'Воздействие на всю команду, {}% -> {}% от множителя'.format(info_value_from, info_value_to),
			'label':'Воздействие на всю команду, {}% от множителя'.format(info_value_to),
			'duration': 1,
			'reuse': 9 - skill_level,
			'specialisation': SPECIALISATION_REGENERATION,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': 0,
		  	'extra_regeneration_ratio': 0,
		  	'splash_multiplier': value_new
		})
		value_last = value_new
		skill_level+=1
	return data

def get_skills_active_my_team_restoration_data():
	data = []
	skill_group_id = 16
	skill_level = 1
	value1_last = 0
	value2_last = 0
	for required_level in [18, 21, 24, 27, 30, 32, 34, 36, 38, 40]:
		skill_id = skill_group_id * 100 + skill_level

		value1_new = 3 * skill_level
		value1_diff = value1_new - value1_last
		value1_current = value1_last

		value2_new = 0.4 * (skill_level + 1)
		value2_diff = value2_new - value2_last
		value2_current = value2_last

		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'ФО команды +{} + результат x {} -> +{} + результат x {}'.format(str(value1_current), str(value2_current), str(value1_new), str(value2_new)),
			'label':'Восстановление ФО своей команды',
			'duration': 1,
			'reuse': 8 - required_level / 2,
			'specialisation': SPECIALISATION_REGENERATION,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': value1_diff,
			'extra_regeneration_ratio': value2_diff,
			'splash_multiplier': 0
		})
		value1_last = value1_new
		value2_last = value2_new
		skill_level += 1

	return data

def get_skills_active_teammate_restoration_data():
	data = []
	skill_group_id = 17
	skill_level = 1
	value1_last = 0
	value2_last = 0
	for required_level in [14, 18, 21, 24, 27, 30, 32, 34, 36, 38]:
		skill_id = skill_group_id * 100 + skill_level

		value1_new = 12 * skill_level
		value1_diff = value1_new - value1_last
		value1_current = value1_last

		value2_new = 0.8 * (skill_level + 1)
		value2_diff = value2_new - value2_last
		value2_current = value2_last

		data.append({
			'skill_id': skill_id,
			'skill_group_id': skill_group_id,
			'skill_level': skill_level,
			'required_level': required_level,
			'skill_points': get_skill_points(2 * (skill_level + 1)),
			'extra_fitness_points': 0,
			'extra_resistance': 0,
			'extra_multiplier': 0.0,
			'extra_bonus_chance':0.0,
			'extra_bonus_multiplier': 0.0,
			'info': 'ФО +{} + результат x {} -> +{} + результат x {}'.format(str(value1_current), str(value2_current), str(value1_new), str(value2_new)),
			'label':'Восстановление ФО союзника',
			'duration': 1,
			'reuse': 7 - required_level / 2,
			'specialisation': SPECIALISATION_REGENERATION,
			'extra_fitness_points_ratio1': 0,
		  	'extra_fitness_points_ratio2': 0,
		  	'extra_resistance_ratio1': 0,
		  	'extra_resistance_ratio2': 0,
		  	'extra_multiplier_ratio1': 0,
		  	'extra_multiplier_ratio2': 0,
		  	'extra_bonus_chance_ratio1': 0,
		  	'extra_bonus_chance_ratio2': 0,
		  	'extra_bonus_multiplier_ratio1': 0,
		  	'extra_bonus_multiplier_ratio2': 0,
		  	'extra_regeneration_base': value1_diff,
			'extra_regeneration_ratio': value2_diff,
			'splash_multiplier': 0
		})
		value1_last = value1_new
		value2_last = value2_new
		skill_level += 1

	return data

#def get_skills_test_data():
#	data = []
#	data.append({
#		'skill_id': 1101,
#		'skill_group_id': 11,
#		'skill_level': 1,
#		'required_level': 40,
#		'skill_points': 0,
#		'extra_fitness_points': 0,
#		'extra_resistance': 0,
#		'extra_multiplier': 0.0,
#		'extra_bonus_chance':0.0,
#		'extra_bonus_multiplier': 0.0,
#		'info': 'Восстановление ФО союзника',
#		'label':'Восстановление ФО союзника',
#		'duration': 10,
#		'reuse': 50,
#		'specialisation': 0,
#		'extra_fitness_points_ratio1': 0,
#	  	'extra_fitness_points_ratio2': 0.1,
#	  	'extra_resistance_ratio1': 0,
#	  	'extra_resistance_ratio2': 0.1,
#	  	'extra_multiplier_ratio1': 0,
#	  	'extra_multiplier_ratio2': 0.1,
#	  	'extra_bonus_chance_ratio1': 0,
#	  	'extra_bonus_chance_ratio2': 0.1,
#	  	'extra_bonus_multiplier_ratio1': 0,
#	  	'extra_bonus_multiplier_ratio2': 0.1
#	})
#
#	data.append({
#		'skill_id': 1201,
#		'skill_group_id': 12,
#		'skill_level': 1,
#		'required_level': 40,
#		'skill_points': 0,
#		'extra_fitness_points': 0,
#		'extra_resistance': 0,
#		'extra_multiplier': 0.0,
#		'extra_bonus_chance':0.0,
#		'extra_bonus_multiplier': 0.0,
#		'info': 'Восстановление ФО своей команды',
#		'label':'Восстановление ФО своей команды',
#		'duration': 10,
#		'reuse': 50,
#		'specialisation': 0,
#		'extra_fitness_points_ratio1': 0,
#	  	'extra_fitness_points_ratio2': 0.1,
#	  	'extra_resistance_ratio1': 0,
#	  	'extra_resistance_ratio2': 0.1,
#	  	'extra_multiplier_ratio1': 0,
#	  	'extra_multiplier_ratio2': 0.1,
#	  	'extra_bonus_chance_ratio1': 0,
#	  	'extra_bonus_chance_ratio2': 0.1,
#	  	'extra_bonus_multiplier_ratio1': 0,
#	  	'extra_bonus_multiplier_ratio2': 0.1
#	})
#
#	data.append({
#		'skill_id': 1301,
#		'skill_group_id': 13,
#		'skill_level': 1,
#		'required_level': 40,
#		'skill_points': 0,
#		'extra_fitness_points': 0,
#		'extra_resistance': 0,
#		'extra_multiplier': 0.0,
#		'extra_bonus_chance':0.0,
#		'extra_bonus_multiplier': 0.0,
#		'info': 'Воздействие на всю команду соперников',
#		'label':'Воздействие на всю команду соперников',
#		'duration': 10,
#		'reuse': 50,
#		'specialisation': 0,
#		'extra_fitness_points_ratio1': 0,
#	  	'extra_fitness_points_ratio2': 0.1,
#	  	'extra_resistance_ratio1': 0,
#	  	'extra_resistance_ratio2': 0.1,
#	  	'extra_multiplier_ratio1': 0,
#	  	'extra_multiplier_ratio2': 0.1,
#	  	'extra_bonus_chance_ratio1': 0,
#	  	'extra_bonus_chance_ratio2': 0.1,
#	  	'extra_bonus_multiplier_ratio1': 0,
#	  	'extra_bonus_multiplier_ratio2': 0.1
#	})


	return data

def get_levels_diff_exp():
	id = 1
	exp_ratio = get_exp_ratio()
	current_exp_ratio = exp_ratio

	level_interval_exps = []
	for location_id in xrange(1, get_locations_count() + 1):
		#print 'location_id = ' + str(location_id)
		level_interval_id = location_id
		level_interval_exp = 0
		quest_exp_ratio = 0.7 + level_interval_id * 0.1
		level_interval_exp_ratio = 1.0 + level_interval_id * 0.1
		for position in xrange(1, 6):
			quest_cnt = int(round((float(location_id) + 1) / 2))

			for level in xrange(1, quest_cnt + 1):
				expected_result = id
				exp = int(round(expected_result * current_exp_ratio))
				quest_exp = 0
				if level == quest_cnt:
					quest_exp = int(round(exp * quest_exp_ratio * quest_cnt))
				level_interval_exp += exp + quest_exp
				id += 1
				current_exp_ratio *= exp_ratio
		level_interval_exps.append(int(round(level_interval_exp * level_interval_exp_ratio)))

	levels_diff_exp = []
	levels_count = [4, 5, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
	for idx, level_interval_exp in enumerate(level_interval_exps):
		if levels_count[idx] == 1:
			levels_diff_exp.append(level_interval_exp)
		elif levels_count[idx] == 2:
			exp1 = int(round(level_interval_exp * 0.45))
			exp2 = level_interval_exp - exp1
			levels_diff_exp.append(exp1)
			levels_diff_exp.append(exp2)
		elif levels_count[idx] == 3:
			exp1 = int(round(level_interval_exp * 0.26))
			exp2 = int(round(level_interval_exp * 0.31))
			exp3 = level_interval_exp - exp2 - exp1
			levels_diff_exp.append(exp1)
			levels_diff_exp.append(exp2)
			levels_diff_exp.append(exp3)
		elif levels_count[idx] == 4:
			exp1 = int(round(level_interval_exp * 0.17))
			exp2 = int(round(level_interval_exp * 0.20))
			exp3 = int(round(level_interval_exp * 0.24))
			exp4 = level_interval_exp - exp3 - exp2 - exp1
			levels_diff_exp.append(exp1)
			levels_diff_exp.append(exp2)
			levels_diff_exp.append(exp3)
			levels_diff_exp.append(exp4)
		elif levels_count[idx] == 5:
			exp1 = int(round(level_interval_exp * 0.13))
			exp2 = int(round(level_interval_exp * 0.15))
			exp3 = int(round(level_interval_exp * 0.19))
			exp4 = int(round(level_interval_exp * 0.22))
			exp5 = level_interval_exp - exp4 - exp3 - exp2 - exp1
			levels_diff_exp.append(exp1)
			levels_diff_exp.append(exp2)
			levels_diff_exp.append(exp3)
			levels_diff_exp.append(exp4)
			levels_diff_exp.append(exp5)
	return levels_diff_exp

# [ fitness_points ]
def get_base_player_fp(level):
	return 2 + get_total_level_quest_count(level)

def get_extra1_player_fp(location_id):
	data = get_skills_fitness_points_data()
	return data[location_id]['fitness_points_new']

def get_extra2_player_fp(expected_total_result):
	return int(math.sqrt(expected_total_result))

# [ resistance ]
def get_base_player_resistance(level):
	return int(round(1.74 * get_total_level_quest_count(level)))

def get_extra1_player_resistance(location_id):
	data = get_skills_resistance_data()
	return data[location_id]['resistance_new']

def get_extra2_player_resistance(expected_total_result):
	return int(round(expected_total_result/537))

# [ multiplier ]
def get_base_player_multiplier(level):
	return 1.0 + round(0.5 * get_total_level_quest_count(level))/100

def get_extra1_player_multiplier(location_id):
	data = get_skills_multiplier_data()
	return data[location_id]['multiplier_new']

def get_extra2_player_multiplier(expected_total_result):
	return 0

# [ bonus_multiplier ]
def get_base_player_bonus_multiplier(level):
	return 1.2 + round(0.17 * get_total_level_quest_count(level))/100

def get_extra1_player_bonus_multiplier(location_id):
	data = get_skills_bonus_data()
	return data[location_id]['bonus_multiplier_new']

def get_extra2_player_bonus_multiplier(expected_total_result):
	return 0

# [ bonus_chance ]
def get_base_player_bonus_chance(level):
	return 0.01 + round(0.027 * get_total_level_quest_count(level))/100

def get_extra1_player_bonus_chance(location_id):
	data = get_skills_bonus_data()
	return data[location_id]['bonus_chance_new']

def get_extra2_player_bonus_chance(expected_total_result):
	return 0

def get_expected_player_fitness_points(location_id):
	level = get_required_level(location_id + 1)
	base = get_base_player_fp(level)
	extra1 = get_extra1_player_fp(location_id)
	extra2 = get_extra2_player_fp(get_expected_total_result(location_id))
	return  base + extra1 + extra2

def get_expected_player_resistance(location_id):
	level = get_required_level(location_id + 1)
	base = get_base_player_resistance(level)
	extra1 = get_extra1_player_resistance(location_id)
	extra2 = get_extra2_player_resistance(get_expected_total_result(location_id))
	return  base + extra1 + extra2

def get_expected_player_multiplier(location_id):
	level = get_required_level(location_id + 1)
	base = get_base_player_multiplier(level)
	extra1 = get_extra1_player_multiplier(location_id)
	extra2 = get_extra2_player_multiplier(get_expected_total_result(location_id))
	return  base + extra1 + extra2

def get_expected_player_bonus_multiplier(location_id):
	level = get_required_level(location_id + 1)
	base = get_base_player_bonus_multiplier(level)
	extra1 = get_extra1_player_bonus_multiplier(location_id)
	extra2 = get_extra2_player_bonus_multiplier(get_expected_total_result(location_id))
	return  base + extra1 + extra2

def get_expected_player_bonus_chance(location_id):
	level = get_required_level(location_id + 1)
	base = get_base_player_bonus_chance(level)
	extra1 = get_extra1_player_bonus_chance(location_id)
	extra2 = get_extra2_player_bonus_chance(get_expected_total_result(location_id))
	return  base + extra1 + extra2

def get_resistance_in_percents(resistance):
	if resistance > 10000:
		resistance = 10000
	if resistance < 0:
		resistance = 0
	return math.sqrt(float(resistance) / 1.5) / 100

def get_efficient_bonus_ratio(bonus_chance, bonus_multiplier):
	return bonus_chance * bonus_multiplier + 1 - bonus_chance

def get_char_fitness_points(expected_result, char_resistance, player_multiplier, player_efficient_bonus_ratio):
	return int(round(float(player_multiplier * player_efficient_bonus_ratio * expected_result) / (1 - get_resistance_in_percents(char_resistance))))

def get_char_resistance(expected_result):
	return int(round(expected_result * 4.37))

def get_char_max_res(location_id, player_prev_fp, player_fp, player_resistance, char_efficient_bonus_ratio, steps, position):
	#return int(round(float(player_fp) / ((1 - get_resistance_in_percents(player_resistance)) * char_efficient_bonus_ratio * (steps + 0.1))))
	#real_player_fp = player_prev_fp + (player_fp - player_prev_fp) * (float(position) - 1) / 4
	if location_id == 1:
		real_player_fp = player_prev_fp
	else:
		real_player_fp = player_prev_fp + (player_fp - player_prev_fp) * (float(position) - 1) / 4
	#print real_player_fp, char_efficient_bonus_ratio, steps, get_resistance_in_percents(player_resistance)
	return int(math.ceil(float(real_player_fp) / ((1 - get_resistance_in_percents(player_resistance)) * char_efficient_bonus_ratio * (steps + 1.1 - (position - 1)* 0.27))))

def get_steps(location_id, expected_result):
	steps = 5 + int(math.floor((location_id - 1) / 3.0))
	return steps

def debug_player_params():
	for loc_id in xrange(1, 34):
		print loc_id, "\t", get_expected_player_fitness_points(loc_id), "\t", get_resistance_in_percents(get_expected_player_resistance(loc_id)), "\t", get_expected_player_resistance(loc_id), "\t", get_expected_player_multiplier(loc_id), "\t", get_expected_player_bonus_multiplier(loc_id), "\t", get_expected_player_bonus_chance(loc_id)

def get_player_prev_fp(location_id):
	if location_id == 1:
		player_prev_fp = 2
	else:
		player_prev_fp = get_expected_player_fitness_points(location_id - 1)
	return player_prev_fp

#debug_player_params()
