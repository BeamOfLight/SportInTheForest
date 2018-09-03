#! /usr/bin/env python
# -*- coding: utf-8 -*-

import forest

npc_data, npc_extra_data = forest.get_npc_data()

forest.print_xml_header()
for row in npc_data:
	print '    <npc id="{}" teammate="{}" type="{}" level="{}" fp="{}" max_res="{}" multiplier="{}" exp="{}" resistance="{}" bonus_chance="{}" bonus_multiplier="{}" name="{}" actions="{}" pre_actions="{}" />'.format(row['npc_id'], "0", row['type'], row['level'], row['fp'], row['max_res'], row['multiplier'], row['exp'], row['resistance'], row['bonus_chance'], row['bonus_multiplier'], row['name'], row['actions'], row['pre_actions'])

for row in npc_extra_data:
	print '    <npc id="{}" teammate="{}" type="{}" level="{}" fp="{}" max_res="{}" multiplier="{}" exp="{}" resistance="{}" bonus_chance="{}" bonus_multiplier="{}" name="{}" actions="{}" pre_actions="{}" />'.format(row['npc_id'], "1", row['type'], row['level'], row['fp'], row['max_res'], row['multiplier'], row['exp'], row['resistance'], row['bonus_chance'], row['bonus_multiplier'], row['name'], row['actions'], row['pre_actions'])
forest.print_xml_footer()