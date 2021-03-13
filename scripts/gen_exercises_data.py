#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest
import forest_dict

def print_exercises():
	exercises = forest_dict.get_exercises()
	for idx, exercise in enumerate(exercises):
		print '    <exercise exercise_id="{}" name="{}"/>'.format(str(idx + 1), exercise)

forest.print_xml_header()
print_exercises()
forest.print_xml_footer()