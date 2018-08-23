#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest
import forest_dict

id = 1
extra_npc_id = 1
exp_ratio = forest.get_exp_ratio()
current_exp_ratio = exp_ratio

names = forest_dict.get_characters()
npc_data = []
npc_extra_data = []
forest.print_xml_header()
for location_id in xrange(1, forest.get_locations_count()):
#for location_id in xrange(1, 4):
	#print 'location_id = ' + str(location_id)
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

				#======================================================
				team = ''
				teammates_count = int((location_id - 1) / 3)
				for idx in xrange(teammates_count):
					team += '{};'.format(str(id - 4 * quest_cnt + idx))

					row = dict(npc_data[id - 4 * quest_cnt + idx - 1])
					row['npc_id'] = str(extra_npc_id + 10000)
					row['level'] = str(level)
					row['fp'] = int(row['fp']) / 4 
					row['exp'] = int(row['exp']) / 4
					row['max_res'] = int(row['max_res']) / 4
					row['name'] = forest_dict.get_extra_character_name(row['name'], idx)
					row['skills'] = ''
					npc_extra_data.append(row)
					extra_npc_id += 1
				if len(team) > 0:
					team = team[:-1]
			#======================================================
			#max_result = int(round(expected_result / 5)) + 1
			character_bonus_chance = location_id * 0.008
			character_bonus_multiplier = round(120 + expected_result * 0.189) / 100
			char_efficient_bonus_ratio = forest.get_efficient_bonus_ratio(character_bonus_chance, character_bonus_multiplier)
			steps = forest.get_steps(location_id, expected_result)
			init_max_result = forest.get_char_max_res(location_id, player_prev_fp, player_fp, player_resistance, char_efficient_bonus_ratio, steps, position)
			multiplier = 1.0 + 0.01 * (expected_result - 1)
			max_result = int(round(init_max_result / multiplier))

			npc_data.append({
				'npc_id': str(id),
				'type': rb_state,
				'level': str(level),
				'fp': str(fitness_points),
				'max_res': str(max_result),
				'multiplier': str(multiplier),
				'exp': str(exp),
				'resistance': str(resistance),
				#'quest_cnt': str(quest_cnt),
				#'quest_exp': str(quest_exp),
				'bonus_chance': str(character_bonus_chance),
				'bonus_multiplier': str(character_bonus_multiplier),
				'name': names[location_id - 1][position - 1],
				'skills': ''
			})

#			print '    <npc id="{}" type="{}" level="{}" fp="{}" max_res="{}" multiplier="{}" exp="{}" resistance="{}" quest_cnt="{}" quest_exp="{}" bonus_chance="{}" bonus_multiplier="{}" name="{}" />'.format(str(id), rb_state, str(level), str(fitness_points), str(max_result), str(multiplier), str(exp), str(resistance), str(quest_cnt), str(quest_exp), str(character_bonus_chance), str(character_bonus_multiplier), names[location_id - 1][position - 1])
			level_interval_exp += exp + quest_exp
			id += 1
			current_exp_ratio *= exp_ratio

for row in npc_data:
	print '    <npc id="{}" teammate="{}" type="{}" level="{}" fp="{}" max_res="{}" multiplier="{}" exp="{}" resistance="{}" bonus_chance="{}" bonus_multiplier="{}" name="{}" skills="{}" />'.format(row['npc_id'], "0", row['type'], row['level'], row['fp'], row['max_res'], row['multiplier'], row['exp'], row['resistance'], row['bonus_chance'], row['bonus_multiplier'], row['name'], row['skills'])

for row in npc_extra_data:
	print '    <npc id="{}" teammate="{}" type="{}" level="{}" fp="{}" max_res="{}" multiplier="{}" exp="{}" resistance="{}" bonus_chance="{}" bonus_multiplier="{}" name="{}" skills="{}" />'.format(row['npc_id'], "1", row['type'], row['level'], row['fp'], row['max_res'], row['multiplier'], row['exp'], row['resistance'], row['bonus_chance'], row['bonus_multiplier'], row['name'], row['skills'])
forest.print_xml_footer()