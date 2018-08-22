#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest
import forest_dict

main_data = []
extra_data = []
npc_id = 1
npc_extra_id = 1
exp_ratio = forest.get_exp_ratio()
current_exp_ratio = exp_ratio

names = forest_dict.get_characters()
forest.print_xml_header()
for location_id in xrange(1, forest.get_locations_count()):
	level_interval_id = location_id
	level_interval_exp = 0
	quest_exp_ratio = 0.7 + level_interval_id * 0.1
	level_interval_exp_ratio = 1.0 + level_interval_id * 0.1

	player_prev_fp = forest.get_player_prev_fp(location_id)
	player_fp = forest.get_expected_player_fitness_points(location_id)
	player_resistance = forest.get_expected_player_resistance(location_id)
	player_bonus_multiplier = forest.get_expected_player_bonus_multiplier(location_id)
	player_bonus_chance = forest.get_expected_player_bonus_chance(location_id)
	player_multiplier = forest.get_expected_player_multiplier(location_id)
	player_efficient_bonus_ratio = forest.get_efficient_bonus_ratio(player_bonus_chance, player_bonus_multiplier)
	quest_cnt = int(round((float(location_id) + 1) / 2))
	for position in xrange(1, len(names[location_id]) + 1):
		for level in xrange(1, quest_cnt + 1):
			expected_result = npc_id
			resistance = forest.get_char_resistance(expected_result)
			fitness_points = forest.get_char_fitness_points(expected_result, resistance, player_multiplier, player_efficient_bonus_ratio)
			exp = int(round(fitness_points * current_exp_ratio))
			quest_exp = 0
			if level == quest_cnt:
				quest_exp = int(round(exp * quest_exp_ratio * quest_cnt))
			main_data.append({
				'location_id': str(location_id),
				'position': str(position),
				'level': str(level),
				'npc_id': str(npc_id),
				'quest_cnt': str(quest_cnt),
				'quest_exp': str(quest_exp),
			})

			if position == 5:
				teammates_count = int((location_id - 1) / 3)
				for idx in xrange(teammates_count):
					extra_data.append({
						'location_id': str(location_id),
						'position': str(position),
						'level': str(level),
						'npc_id': str(npc_extra_id + 10000),
						'quest_cnt': '0',
						'quest_exp': '0',
					})
					npc_extra_id += 1

			npc_id += 1



for row in main_data:
	print '    <location_position location_id="{}" position="{}" level="{}" npc_id="{}" quest_cnt="{}" quest_exp="{}"/>'.format(row['location_id'], row['position'], row['level'], row['npc_id'], row['quest_cnt'], row['quest_exp'])

for row in extra_data:
	print '    <location_position location_id="{}" position="{}" level="{}" npc_id="{}" quest_cnt="{}" quest_exp="{}"/>'.format(row['location_id'], row['position'], row['level'], row['npc_id'], row['quest_cnt'], row['quest_exp'])
forest.print_xml_footer()