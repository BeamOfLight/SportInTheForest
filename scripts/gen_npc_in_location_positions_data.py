#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest
import forest_dict

main_data = []
location_level_position_id = 0
npc_id = 1
npc_extra_id = 1

names = forest_dict.get_characters()
forest.print_xml_header()
for location_id in xrange(1, forest.get_locations_count()):
	quest_cnt = int(round((float(location_id) + 1) / 2))
	for position in xrange(1, len(names[location_id]) + 1):
		for level in xrange(1, quest_cnt + 1):
			location_level_position_id += 1
			main_data.append({
				'location_level_position_id': str(location_level_position_id),
				'npc_id': str(npc_id)
			})

			if position == 5:
				teammates_count = int((location_id - 1) / 3)
				for idx in xrange(teammates_count):
					main_data.append({
						'location_level_position_id': str(location_level_position_id),
						'npc_id': str(npc_extra_id + 10000)
					})
					npc_extra_id += 1

			npc_id += 1


for row in main_data:
	print '    <npc_in_location_position location_level_position_id="{}" npc_id="{}"/>'.format(row['location_level_position_id'], row['npc_id'])
	
forest.print_xml_footer()