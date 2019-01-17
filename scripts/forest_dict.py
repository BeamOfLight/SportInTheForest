#! /usr/bin/env python
# -*- coding: utf-8 -*-
import forest

def get_exercises():
	exercises = []
	for i in xrange(5):
		exercises.append('')

	exercises[0]="Подтягивания"
	exercises[1]="Отжимания"
	exercises[2]="Приседания"
	exercises[3]="Пресс"
	exercises[4]="Отжимания на брусьях"

	return exercises

def get_locations():
	locations = []
	for i in xrange(18):
		locations.append('')

	locations[0]="Зелёный перелесок"
	locations[1]="Цветущий луг"
	locations[2]="Опушка"
	locations[3]="Берёзовая роща"
	locations[4]="Овраг"
	locations[5]="Болото"
	locations[6]="Речка"
	locations[7]="Разнотравие"
	locations[8]="Ручей"
	locations[9]="Кустарники"
	locations[10]="Родник"
	locations[11]="Южный склон холма"
	locations[12]="Холм"
	locations[13]="Перелесок"
	locations[14]="Северный склон холма"
	locations[15]="Лес"
	locations[16]="Сосновый бор"
	locations[17]="Бурелом"

	return locations

def get_skill_groups():
	"""
	название | тип навыка | тип цели
	тип навыка:
		0 - пассивный
		1 - усиление
		2 - активный)
	тип цели:
		TARGET_TYPE_SELF 								= 0
		TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM 			= 1
		TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM 		= 2
		TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES 		= 3
		TARGET_TYPE_ACTIVE_MY_TEAM 						= 4
		TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM 	= 5
		TARGET_TYPE_SINGLE_INACTIVE_FROM_OPPOSITE_TEAM 	= 6
		TARGET_TYPE_ACTIVE_OPPOSITE_TEAM 				= 7
		TARGET_TYPE_ACTIVE_ALL 							= 8
	"""
	SKILL_TYPE_PASSIVE 	= 0
	SKILL_TYPE_GAIN		= 1
	SKILL_TYPE_ACTIVE 	= 2
	TARGET_TYPE_SELF 								= 0
	TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM 			= 1
	TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM 		= 2
	TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES 		= 3
	TARGET_TYPE_ACTIVE_MY_TEAM 						= 4
	TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM 	= 5
	TARGET_TYPE_SINGLE_INACTIVE_FROM_OPPOSITE_TEAM 	= 6
	TARGET_TYPE_ACTIVE_OPPOSITE_TEAM 				= 7
	TARGET_TYPE_ACTIVE_ALL 							= 8

	skill_groups = []
	for i in xrange(17):
		skill_groups.append('')

	skill_groups[0] = 'Увеличение сопротивления|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[1] = 'Увеличение фитнес очков|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[2] = 'Увеличение множителя ФО|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[3] = 'Увеличение бонуса|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[4] = 'Увеличение множителя на 1 ход|{}|{}'.format(SKILL_TYPE_GAIN, TARGET_TYPE_SELF)
	skill_groups[5] = 'Увеличение сопротивления на 1 ход|{}|{}'.format(SKILL_TYPE_GAIN, TARGET_TYPE_SELF)
	skill_groups[6] = 'Восстановление своих ФО|{}|{}'.format(SKILL_TYPE_ACTIVE, TARGET_TYPE_SELF)
	skill_groups[7] = 'Увеличение максимума  ФО команды|{}|{}'.format(SKILL_TYPE_GAIN, TARGET_TYPE_ACTIVE_MY_TEAM)
	skill_groups[8] = 'Увеличение сопротивления команды|{}|{}'.format(SKILL_TYPE_GAIN, TARGET_TYPE_ACTIVE_MY_TEAM)
	skill_groups[9] = 'Увеличение множителя команды|{}|{}'.format(SKILL_TYPE_GAIN, TARGET_TYPE_ACTIVE_MY_TEAM)
	skill_groups[10] = 'Увеличение бонуса команды|{}|{}'.format(SKILL_TYPE_GAIN, TARGET_TYPE_ACTIVE_MY_TEAM)
	skill_groups[11] = 'Базовое увеличение регенерации|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[12] = 'Базовое увеличение множителя|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[13] = 'Базовое увеличение сопротивления|{}|{}'.format(SKILL_TYPE_PASSIVE, TARGET_TYPE_SELF)
	skill_groups[14] = 'Воздействие на всю команду соперников|{}|{}'.format(SKILL_TYPE_ACTIVE, TARGET_TYPE_ACTIVE_OPPOSITE_TEAM)
	skill_groups[15] = 'Восстановление ФО своей команды|{}|{}'.format(SKILL_TYPE_ACTIVE, TARGET_TYPE_ACTIVE_MY_TEAM)
	skill_groups[16] = 'Восстановление ФО союзника|{}|{}'.format(SKILL_TYPE_ACTIVE, TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM)
	#skill_groups[11] = 'Возвращение игрока в игру|{}|{}'

	return skill_groups

def get_characters():
	names = []
	for i in xrange(forest.get_locations_count()):
		names.append([])
		for j in xrange(forest.get_max_character_position()):
			names[i].append('')
		
	names[0][0]="Молодой ёжик"
	names[0][1]="Ёж"
	names[0][2]="Старший ёж"
	names[0][3]="Ёжик в кепке"
	names[0][4]="Ежище"
	names[1][0]="Молодой заяц-русак"
	names[1][1]="Заяц-русак"
	names[1][2]="Сильный заяц-русак"
	names[1][3]="Опытный заяц-русак"
	names[1][4]="Вожак зайцев-русаков"
	names[2][0]="Лисёнок"
	names[2][1]="Молодой лис"
	names[2][2]="Лис"
	names[2][3]="Опытный лис"
	names[2][4]="Лис-главарь"
	names[3][0]="Волчонок"
	names[3][1]="Молодой волк"
	names[3][2]="Волк"
	names[3][3]="Матёрый волк"
	names[3][4]="Волк-вожак"
	names[4][0]="Медвежонок"
	names[4][1]="Молодой медведь"
	names[4][2]="Медведь-лежебока"
	names[4][3]="Медведь"
	names[4][4]="Гигантский медведь"
	names[5][0]="Молодая куница"
	names[5][1]="Куница"
	names[5][2]="Сильная куница"
	names[5][3]="Опытная куница"
	names[5][4]="Предводительница куниц"
	names[6][0]="Молодой барсук"
	names[6][1]="Ленивый барсук"
	names[6][2]="Барсук"
	names[6][3]="Барсук-атлет"
	names[6][4]="Главарь барсуков"
	names[7][0]="Молодой бурундук"
	names[7][1]="Бурундук"
	names[7][2]="Сильный бурундук"
	names[7][3]="Опытный бурундук"
	names[7][4]="Главарь бурундуков"
	names[8][0]="Бельчонок"
	names[8][1]="Молодая белка"
	names[8][2]="Белка"
	names[8][3]="Сытая белка"
	names[8][4]="Белка-сопелка"
	names[9][0]="Лосёнок"
	names[9][1]="Молодой лось"
	names[9][2]="Лосиха"
	names[9][3]="Лось"
	names[9][4]="Вожак лосей"
	names[10][0]="Молодой зубр"
	names[10][1]="Зубр"
	names[10][2]="Зубр-мыслитель"
	names[10][3]="Сильный зубр"
	names[10][4]="Вожак зубров"
	names[11][0]="Молодой кабан"
	names[11][1]="Суровый кабан"
	names[11][2]="Кабан"
	names[11][3]="Сильный кабан"
	names[11][4]="Главный кабан"
	names[12][0]="Молодая выдра"
	names[12][1]="Выдра"
	names[12][2]="Выдра-мыслитель"
	names[12][3]="Сильная выдра"
	names[12][4]="Старшая выдра"
	names[13][0]="Молодой енот"
	names[13][1]="Енот"
	names[13][2]="Енот-сказочник"
	names[13][3]="Сильный енот"
	names[13][4]="Предводитель енотов"
	names[14][0]="Молодая рысь"
	names[14][1]="Рысь"
	names[14][2]="Рысь-попрыгунья"
	names[14][3]="Сильная рысь"
	names[14][4]="Крупная рысь"
	names[15][0]="Молодая куропатка"
	names[15][1]="Куропатка"
	names[15][2]="Куропатка-экстремалка"
	names[15][3]="Сильная куропатка"
	names[15][4]="Вожак куропаток"
	names[16][0]="Молодой сайгак"
	names[16][1]="Сайгак-болтун"
	names[16][2]="Сайгак"
	names[16][3]="Сильный сайгак"
	names[16][4]="Главный сайгак"
	names[17][0]="Оленёнок"
	names[17][1]="Молодой олень"
	names[17][2]="Олень"
	names[17][3]="Шустрый олень"
	names[17][4]="Олень-вожак"
	return names

def get_extra_character_name(name, id):
	extras = []
	extras.append(''); 				# id = 0
	extras.append('-защитник'); 	# id = 1
	extras.append('-лекарь');		# id = 2
	extras.append('-спортсмен');	# id = 3
	return '{}{}'.format(name, extras[id % 4])

def get_achievements():
	achievements = []
	for i in xrange(12):
		achievements.append({})

	achievements[0] = ("quests_count", "Выполнить задания")
	achievements[1] = ("total_result", "Общий результат")
	achievements[2] = ("total_number_of_moves", "Сделать несколько подходов")
	achievements[3] = ("max_competition_result", "Рекорд соревнования")
	achievements[4] = ("competitions", "Поучаствовать в соревнованиях")
	achievements[5] = ("wins", "Победить в соревнованиях")
	achievements[6] = ("training_days", "Дни тренировок")
	achievements[7] = ("max_weekly_result", "Максимум за неделю")
	achievements[8] = ("max_monthly_result", "Максимум за месяц")
	achievements[9] = ("weekly_greater_100_periods_cnt", "Неделя с результатом больше 100")
	achievements[10] = ("weekly_greater_300_periods_cnt", "Неделя с результатом больше 300")
	achievements[11] = ("weekly_greater_700_periods_cnt", "Неделя с результатом больше 700")
	return achievements

def get_knowledge_categories():
	arr = []
	for i in xrange(6):
		arr.append('')

	arr[0]="Общая информация"
	arr[1]="Характеристики персонажа"
	arr[2]="Специализация"
	arr[3]="Соревнования"
	arr[4]="Навыки"
	arr[5]="Часто задаваемые вопросы"

	return arr