#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest
import forest_dict

def print_skill_groups():
	skill_groups = forest_dict.get_skill_groups()
	for idx, skill_group in enumerate(skill_groups):
		values = skill_group.split("|")
		print '    <skill_group skill_group_id="{}" name="{}" type="{}" target_type="{}"/>'.format(str(idx + 1), values[0], values[1], values[2])

forest.print_xml_header()
print_skill_groups()
forest.print_xml_footer()