#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest

levels_diff_exp = forest.get_levels_diff_exp()
total_exp = 0
forest.print_xml_header()
print '    <level id= "1" diff_exp="0" min_exp="0" base_fp="{}" base_resistance="{}" base_multiplier="{}" base_bonus_chance="{}" base_bonus_multiplier="{}" />'.format(forest.get_base_player_fp(0), forest.get_base_player_resistance(0), forest.get_base_player_multiplier(0), forest.get_base_player_bonus_chance(0), forest.get_base_player_bonus_multiplier(0))
for idx, level_diff_exp in enumerate(levels_diff_exp):
	level = idx + 2
	total_exp += level_diff_exp
	base_fp = forest.get_base_player_fp(level)
	base_resistance = forest.get_base_player_resistance(level)
	base_multiplier = forest.get_base_player_multiplier(level)
	base_bonus_chance = forest.get_base_player_bonus_chance(level)
	base_bonus_multiplier = forest.get_base_player_bonus_multiplier(level)
	#print (idx + 2), "\t", level_diff_exp, "\t", total_exp
	print '    <level id= "{}" diff_exp="{}" min_exp="{}" base_fp="{}" base_resistance="{}" base_multiplier="{}" base_bonus_chance="{}" base_bonus_multiplier="{}" />'.format(str(level), level_diff_exp, total_exp, base_fp, base_resistance, base_multiplier, base_bonus_chance, base_bonus_multiplier)
	#print level, forest.get_total_level_quest_count(level)
forest.print_xml_footer()