#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest
import forest_dict

def print_knowledge_categories():
    arr = forest_dict.get_knowledge_categories()
    for idx, knowledge_category in enumerate(arr):
    	category_name, order_value = knowledge_category
        print '    <knowledge_category id="{}" name="{}" order_value="{}"/>'.format(str(idx + 1), category_name, order_value)

forest.print_xml_header()
print_knowledge_categories()
forest.print_xml_footer()