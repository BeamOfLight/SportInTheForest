#!/bin/bash
python gen_npc_data.py > ../app/src/main/res/xml/non_player_characters.xml
python gen_player_exp_data.py > ../app/src/main/res/xml/player_exp_data.xml
python gen_skills_data.py > ../app/src/main/res/xml/skills.xml
python gen_skill_groups_data.py > ../app/src/main/res/xml/skill_groups.xml
python gen_locations_data.py > ../app/src/main/res/xml/locations.xml
python gen_exercises_data.py > ../app/src/main/res/xml/exercises.xml