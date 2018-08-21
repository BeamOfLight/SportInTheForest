#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest
import forest_dict

npc_id = 1
names = forest_dict.get_characters()
forest.print_xml_header()
for location_id in xrange(1, forest.get_locations_count()):
	quest_cnt = int(round((float(location_id) + 1) / 2))
	for position in xrange(1, len(names[location_id]) + 1):
		if position == 5:
			pass

		for level in xrange(1, quest_cnt + 1):
			print '    <location_position location_id="{}" position="{}" level="{}" npc_id="{}"/>'.format(location_id, position, level, npc_id)
			npc_id += 1

forest.print_xml_footer()