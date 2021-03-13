#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest
import forest_dict

def print_locations():
	locations = forest_dict.get_locations()
	for idx, location in enumerate(locations):
		print '    <location location_id="{}" name="{}"/>'.format(str(idx + 1), location)

forest.print_xml_header()
print_locations()
forest.print_xml_footer()