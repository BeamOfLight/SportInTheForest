#!/bin/bash
python gen_npc_data.py > ../app/src/main/res/xml/non_player_characters.xml
python gen_player_exp_data.py > ../app/src/main/res/xml/player_exp_data.xml
python gen_skills_data.py > ../app/src/main/res/xml/skills.xml
python gen_skill_groups_data.py > ../app/src/main/res/xml/skill_groups.xml
python gen_locations_data.py > ../app/src/main/res/xml/locations.xml
python gen_exercises_data.py > ../app/src/main/res/xml/exercises.xml
python gen_location_positions_data.py > ../app/src/main/res/xml/location_positions.xml
python gen_npc_in_location_positions_data.py > ../app/src/main/res/xml/npc_in_location_positions.xml
python gen_achievements_data.py > ../app/src/main/res/xml/achievements.xml
python gen_knowledge_categories_data.py > ../app/src/main/res/xml/knowledge_categories.xml