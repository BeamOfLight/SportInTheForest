#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest
import forest_dict

def print_knowledge_categories():
    arr = forest_dict.get_knowledge_categories()
    for idx, row in enumerate(arr):
        print '    <knowledge_category id="{}" name="{}"/>'.format(str(idx + 1), row)

forest.print_xml_header()
print_knowledge_categories()
forest.print_xml_footer()