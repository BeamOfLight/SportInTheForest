#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest
import forest_dict

def get_player_prev_fp():
	if location_id == 1:
		player_prev_fp = 2
	else:
		player_prev_fp = forest.get_expected_player_fitness_points(location_id - 1)
	return player_prev_fp

id = 1
exp_ratio = forest.get_exp_ratio()
current_exp_ratio = exp_ratio

names = forest_dict.get_characters()

forest.print_xml_header()
for location_id in xrange(1, forest.get_locations_count()):
#for location_id in xrange(1, 4):
	#print 'location_id = ' + str(location_id)
	level_interval_id = location_id
	level_interval_exp = 0
	quest_exp_ratio = 0.7 + level_interval_id * 0.1
	level_interval_exp_ratio = 1.0 + level_interval_id * 0.1

	player_prev_fp = get_player_prev_fp()
	player_fp = forest.get_expected_player_fitness_points(location_id)
	player_resistance = forest.get_expected_player_resistance(location_id)
	player_bonus_multiplier = forest.get_expected_player_bonus_multiplier(location_id)
	player_bonus_chance = forest.get_expected_player_bonus_chance(location_id)
	player_multiplier = forest.get_expected_player_multiplier(location_id)
	player_efficient_bonus_ratio = forest.get_efficient_bonus_ratio(player_bonus_chance, player_bonus_multiplier)
	for position in xrange(1, len(names[location_id]) + 1):
		quest_cnt = int(round((float(location_id) + 1) / 2))
		
		rb_state = ""
		if position == 5:
			rb_state = "rb"

		for level in xrange(1, quest_cnt + 1):
			expected_result = id
			#print id, "\t", location_id, "\t", position, "\t", level, "\t", quest_cnt, "\t", exp, "\t", names[location_id - 1][position - 1], "\t", quest_exp, "\t", rb_state

			#fitness_points = expected_result * 5
			resistance = forest.get_char_resistance(expected_result)
			fitness_points = forest.get_char_fitness_points(expected_result, resistance, player_multiplier, player_efficient_bonus_ratio)
			exp = int(round(fitness_points * current_exp_ratio))
			quest_exp = 0
			if level == quest_cnt:
				quest_exp = int(round(exp * quest_exp_ratio * quest_cnt))

			team = ""
			
			if rb_state == "rb":
				fitness_points = int(round(fitness_points * 1.2))
				resistance = int(round(resistance * 1.3))
				exp = int(round(exp * 1.34))

				team = ''
				teammates_count = int((location_id - 1) / 3)
				for idx in xrange(teammates_count):
					team += '{};'.format(str(id - 4 * quest_cnt + idx))
				if len(team) > 0:
					team = team[:-1]

			#max_result = int(round(expected_result / 5)) + 1
			character_bonus_chance = location_id * 0.008
			character_bonus_multiplier = round(120 + expected_result * 0.189) / 100
			char_efficient_bonus_ratio = forest.get_efficient_bonus_ratio(character_bonus_chance, character_bonus_multiplier)
			steps = forest.get_steps(location_id, expected_result)
			init_max_result = forest.get_char_max_res(location_id, player_prev_fp, player_fp, player_resistance, char_efficient_bonus_ratio, steps, position)
			multiplier = 1.0 + 0.01 * (expected_result - 1)
			max_result = int(round(init_max_result / multiplier))

			print '    <npc id="{}" location_id="{}" position="{}" type="{}" level="{}" fp="{}" max_res="{}" multiplier="{}" exp="{}" resistance="{}" quest_cnt="{}" quest_exp="{}" bonus_chance="{}" bonus_multiplier="{}" name="{}" team="{}"/>'.format(str(id), str(location_id), str(position), rb_state, str(level), str(fitness_points), str(max_result), str(multiplier), str(exp), str(resistance), str(quest_cnt), str(quest_exp), str(character_bonus_chance), str(character_bonus_multiplier), names[location_id - 1][position - 1], team)
			level_interval_exp += exp + quest_exp
			id += 1
			current_exp_ratio *= exp_ratio
forest.print_xml_footer()