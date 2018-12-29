package beamoflight.sportintheforest;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by beamoflight on 30.05.17.
 */
class DBHelper extends DBHelperBaseLayer {
    private GameHelper gameHelper;
    private Context context;

    public DBHelper(Context current) {
        super (current);

        gameHelper = new GameHelper(current);
        context = current;
    }

    // ============== User
    public long addUser(String user_name)
    {
        ContentValues values = new ContentValues();
        values.put("name", user_name);
        values.put("creation_date", gameHelper.getTodayString());
        values.put("modification_date", gameHelper.getTodayString());

        return db.insert("users", null, values);
    }

    public long updateUser(int user_id, String user_name)
    {
        ContentValues values = new ContentValues();
        values.put("name", user_name);
        values.put("modification_date", gameHelper.getTodayString());
        return db.update("users", values, "user_id = ?", new String[]{Integer.toString(user_id)});
    }

    public boolean isUserNameExist(String user_name, int excluded_user_id)
    {
        String[] columns = new String[] { "user_id" };
        Cursor c = db.query("users", columns, "name = ?", new String[]{user_name}, null, null, null);
        boolean is_exist = false;
        if (c != null) {
            if (c.moveToFirst()) {
                is_exist = (c.getInt(c.getColumnIndex("user_id")) != excluded_user_id);
            }
            c.close();
        }

        return is_exist;
    }

    public ArrayList<Map<String, String>> getUsersData()
    {
        ArrayList<Map<String, String>> users_data = new ArrayList<>();
        Map<String, String> m;
        Cursor cursor = db.query(
                "users",
                new String[]{"user_id", "name", "creation_date", "modification_date"},
                null,
                null,
                null,
                null,
                "name ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    m.put("user_id", cursor.getString(cursor.getColumnIndex("user_id")));
                    m.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    m.put("creation_date", cursor.getString(cursor.getColumnIndex("creation_date")));
                    m.put("modification_date", cursor.getString(cursor.getColumnIndex("modification_date")));

                    m.put("info",
                            String.format(
                                    Locale.ROOT,
                                    "Создан: %s | Изменён: %s",
                                    m.get("creation_date"),
                                    m.get("modification_date")
                            )
                    );

                    users_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return users_data;
    }

    public ArrayList<UserEntity> getUsersWithExercise(int exercise_id)
    {
        ArrayList<UserEntity> users_entities = new ArrayList<>();
        Cursor cursor = db.query(
                "users AS u INNER JOIN user_exercises AS ue ON u.user_id = ue.user_id AND ue.exercise_id = " + Integer.toString(exercise_id),
                new String[]{"u.user_id", "u.name", "u.creation_date", "u.modification_date"},
                null,
                null,
                null,
                null,
                "u.name ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    UserEntity user_entity = new UserEntity();
                    user_entity.id = cursor.getInt(cursor.getColumnIndex("user_id"));
                    user_entity.name = cursor.getString(cursor.getColumnIndex("name"));
                    user_entity.creationDate = cursor.getString(cursor.getColumnIndex("creation_date"));
                    user_entity.modificationDate = cursor.getString(cursor.getColumnIndex("modification_date"));
                    user_entity.info = String.format(
                            Locale.ROOT,
                            "Создан: %s | Изменён: %s",
                            user_entity.creationDate,
                            user_entity.modificationDate
                    );
                    users_entities.add(user_entity);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return users_entities;
    }

    public boolean checkExerciseExist(String exercise_name) {
        boolean status = false;
        Cursor cursor = db.query(
                "exercises",
                new String[]{"exercise_id", "name"},
                "name = ?",
                new String[]{exercise_name},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    status = true;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return status;
    }

    public List<ExerciseEntity> getExercises(int user_id) {
        List<ExerciseEntity> exercises = new ArrayList<>();
        Map<String, String> m;

        Cursor cursor = db.query(
                "exercises",
                new String[]{"exercise_id", "name"},
                "exercise_id NOT IN (SELECT exercise_id FROM user_exercises WHERE user_id = ?)",
                new String[]{Integer.toString(user_id)},
                "exercise_id",
                null,
                "name ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ExerciseEntity exercise= new ExerciseEntity(
                            cursor.getInt(cursor.getColumnIndex("exercise_id")),
                            cursor.getString(cursor.getColumnIndex("name"))
                    );
                    exercises.add(exercise);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return exercises;
    }

    public ArrayList<Map<String, String>> getExercisesData()
    {
        ArrayList<Map<String, String>> users_data = new ArrayList<>();
        Map<String, String> m;
        Cursor cursor = db.query(
                "exercises",
                new String[]{"exercise_id", "name", "initial_name", "modification_date"},
                null,
                null,
                "exercise_id",
                null,
                "name ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    m.put("exercise_id", cursor.getString(cursor.getColumnIndex("exercise_id")));
                    m.put("initial_name", cursor.getString(cursor.getColumnIndex("initial_name")));
                    m.put("modification_date", cursor.getString(cursor.getColumnIndex("modification_date")));
                    m.put("name",
                            String.format(
                                    Locale.ROOT,
                                    "[ %s ]",
                                    cursor.getString(cursor.getColumnIndex("name"))
                            )
                    );
                    m.put("info",
                            String.format(
                                    Locale.ROOT,
                                    "Исходное название: %s | Последнее изменение: %s",
                                    m.get("initial_name"),
                                    m.get("modification_date")
                            )
                    );

                    users_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return users_data;
    }

    public ArrayList<Map<String, String>> getUserExercisesData()
    {
        ArrayList<Map<String, String>> users_data = new ArrayList<>();
        Map<String, String> m;
        int user_id = gameHelper.getUserId();
        Cursor cursor = db.query(
                "exercises AS e INNER JOIN user_exercises AS ue ON e.exercise_id = ue.exercise_id AND ue.user_id = " + Integer.toString(user_id),
                new String[]{"e.exercise_id", "e.name"},
                null,
                null,
                "e.exercise_id",
                null,
                "e.name ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    m.put("exercise_id", cursor.getString(cursor.getColumnIndex("exercise_id")));
                    m.put("name",
                            String.format(
                                Locale.ROOT,
                                "[ %s ]",
                                cursor.getString(cursor.getColumnIndex("name"))
                            )
                    );

                    int exercise_id = Integer.parseInt(m.get("exercise_id"));
                    int total_count = getTrainingSumResult(user_id, exercise_id);
                    int user_level = (int) getFloatPlayerLevel(getUserExerciseExp(user_id, exercise_id));
                    int user_fp = getUserFitnessPoints(user_id, exercise_id);
                    int user_resistance = getUserResistance(user_id, exercise_id);
                    float user_multiplier = getUserMultiplier(user_id, exercise_id);
                    float user_bonus_multiplier = getUserBonusMultiplier(user_id, exercise_id);
                    float user_bonus_chance = getUserBonusChance(user_id, exercise_id);
                    int finished_quest_count = getUserExerciseFinishedQuestsCount(user_id, exercise_id);
                    m.put("info",
                            String.format(
                                    Locale.ROOT,
                                    "Уровень %d | ФО: %d | Сопротивление: %d | Множитель: %.2f | Множитель бонуса: %.2f | Шанс бонуса: %.1f%% | Выполнено заданий: %d | Общий результат: %d",
                                    //"Уровень %d  ФО: %d | Сопротивление: %d  Множитель: %.2f  Множитель бонуса: %.2f  Шанс бонуса: %.2f  Выполнено заданий: %d",
                                    user_level,
                                    user_fp,
                                    user_resistance,
                                    user_multiplier,
                                    user_bonus_multiplier,
                                    user_bonus_chance * 100,
                                    finished_quest_count,
                                    total_count
                            )
                    );

                    users_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return users_data;
    }

    public long addExercise(String exercise_name)
    {
        ContentValues values = new ContentValues();
        values.put("name", exercise_name);
        values.put("initial_name", exercise_name);
        values.put("modification_date", gameHelper.getTodayString());

        return db.insert("exercises", null, values);
    }

    public long updateExercise(int exercise_id, String exercise_name)
    {
        ContentValues values = new ContentValues();
        values.put("name", exercise_name);
        values.put("modification_date", gameHelper.getTodayString());
        return db.update("exercises", values, "exercise_id = ?", new String[]{Integer.toString(exercise_id)});
    }

    public String getExerciseName(int exercise_id)
    {
        String exercise_name = null;
        Cursor cursor = db.query(
                "exercises",
                new String[]{"name"},
                "exercise_id = ?",
                new String[]{Integer.toString(exercise_id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    exercise_name = cursor.getString(cursor.getColumnIndex("name"));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return exercise_name;
    }

    // ===============UserExercise
    public void createUserExercise(int user_id, int exercise_id)
    {
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("exercise_id", exercise_id);
        values.put("specialisation", 0);
        values.put("competitions", 0);
        values.put("wins", 0);
        values.put("draws", 0);

        db.insert("user_exercises", null, values);
    }

    public Map<String, String> getCurrentUserExerciseData()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        return getUserExerciseData(user_id, exercise_id);
    }

    public Map<String, String> getUserExerciseData(int user_id, int exercise_id)
    {
        Map<String, String> user_exercise_data = null;
        Cursor cursor = db.query(
                "user_exercises",
                new String[]{"wins", "competitions", "draws", "specialisation"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    user_exercise_data = new HashMap<String, String>();
                    user_exercise_data.put("wins", cursor.getString(cursor.getColumnIndex("wins")));
                    user_exercise_data.put("competitions", cursor.getString(cursor.getColumnIndex("competitions")));
                    user_exercise_data.put("draws", cursor.getString(cursor.getColumnIndex("draws")));
                    user_exercise_data.put("specialisation", cursor.getString(cursor.getColumnIndex("specialisation")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return user_exercise_data;
    }

    public Map<String, String> getAchievementsResultData()
    {
        Map<String, String> user_exercise_data = getCurrentUserExerciseData();
        Map<String, String> m = new HashMap<String, String>();
        m.put("quests_count", Integer.toString(getCurrentUserExerciseFinishedQuestsCount()));
        m.put("total_result", Integer.toString(getCurrentUserTrainingSumResult()));
        m.put("total_number_of_moves", Integer.toString(getCurrentUserExerciseTrainingTotalNumberOfMoves()));
        m.put("max_competition_result", Integer.toString(getUserExerciseTrainingMaxCompetitionResult()));
        m.put("competitions", user_exercise_data.get("competitions"));
        m.put("wins", user_exercise_data.get("wins"));
        m.put("training_days", Integer.toString(getCurrentUserTrainingDaysCount()));

        return m;
    }

    public ArrayList<Map<String, String>> getAchievementsData()
    {
        Map<String, String> achievements_data = getAchievementsResultData();
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> m;
        int success_achievements_cnt = 0;
        Cursor cursor = db.query(
                "achievements",
                new String[]{"achievement_id", "required_parameter_name", "required_parameter_values", "skill_points_values", "name"},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    m.put("achievement_id", cursor.getString(cursor.getColumnIndex("achievement_id")));
                    m.put("required_parameter_name", cursor.getString(cursor.getColumnIndex("required_parameter_name")));
                    m.put("required_parameter_values", cursor.getString(cursor.getColumnIndex("required_parameter_values")));
                    m.put("skill_points_values", cursor.getString(cursor.getColumnIndex("skill_points_values")));
                    m.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    int received_skill_points = 0;
                    int current_count = Integer.parseInt(achievements_data.get(m.get("required_parameter_name")));

                    String[] expected_string_counts = m.get("required_parameter_values").split(";");
                    String[] skill_points_values = m.get("skill_points_values").split(";");

                    if (skill_points_values.length != expected_string_counts.length) {
                        Toast.makeText(
                                context,
                                String.format(
                                        Locale.ROOT,
                                        "Achievement data error for record \"%s\": %d != %d",
                                        m.get("name"),
                                        skill_points_values.length,
                                        expected_string_counts.length
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                        return data;
                    }

                    int achievement_level = 1;
                    int idx = 0;

                    boolean hasInfo = false;
                    for (String expected_string_count : expected_string_counts) {
                        int expected_count = Integer.parseInt(expected_string_count);
                        if (idx < expected_string_counts.length - 1) {
                            int skill_points = Integer.parseInt(skill_points_values[idx]);
                            if (current_count >= expected_count) {
                                received_skill_points += skill_points;
                                achievement_level++;
                                success_achievements_cnt++;
                            } else if (!hasInfo) {
                                m.put("info", String.format(
                                        Locale.ROOT,
                                        "[ %s / %s ] Награда в очках навыков: %d",
                                        current_count,
                                        expected_count,
                                        skill_points
                                ));
                                hasInfo = true;
                            }
                        } else {
                            if (current_count >= expected_count) {
                                achievement_level = expected_string_counts.length;
                                success_achievements_cnt++;
                                m.put("info", "[ Достигнуто ]");
                            } else if (!hasInfo) {
                                m.put("info", String.format(
                                        Locale.ROOT,
                                        "%s / %s",
                                        current_count,
                                        expected_count
                                ));
                            }
                        }

                        idx++;
                    }

                    m.put("success_achievements_cnt", Integer.toString(success_achievements_cnt));
                    m.put("success_progress", Integer.toString(achievement_level));
                    m.put("skill_points", Integer.toString(received_skill_points));
                    m.put("header", String.format(
                            Locale.ROOT,
                            "%s Ур. %d",
                            m.get("name"),
                            achievement_level
                    ));

                    data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return data;
    }

    public void addCompetition2UserExerciseStat(int user_id, int exercise_id)
    {
        db.execSQL("UPDATE user_exercises SET competitions = competitions + 1 WHERE user_id = " + Integer.toString(user_id) + " AND exercise_id = " + Integer.toString(exercise_id));
    }

    public void addWin2UserExerciseStat(int user_id, int exercise_id)
    {
        db.execSQL("UPDATE user_exercises SET wins = wins + 1 WHERE user_id = " + Integer.toString(user_id) + " AND exercise_id = " + Integer.toString(exercise_id));
    }

    public void addDraw2UserExerciseStat(int user_id, int exercise_id)
    {
        db.execSQL("UPDATE user_exercises SET draws = draws + 1 WHERE user_id = " + Integer.toString(user_id) + " AND exercise_id = " + Integer.toString(exercise_id));
    }

    public void setSpecialisation2UserExercise(int user_id, int exercise_id, int specialisation)
    {
        db.execSQL("UPDATE user_exercises SET specialisation = " +  Integer.toString(specialisation) + " WHERE user_id = " + Integer.toString(user_id) + " AND exercise_id = " + Integer.toString(exercise_id));
    }

    // ============== Location

    public String getLocationName(int location_id)
    {
        String location_name = null;
        Cursor cursor = db.query("locations", new String[]{"name"}, "location_id = ?", new String[]{Integer.toString(location_id)}, null, null, null );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                location_name = cursor.getString(cursor.getColumnIndex("name"));
            }
            cursor.close();
        }

        return location_name;
    }

    public ArrayList<Map<String, String>> getLocationsData()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        ArrayList<Map<String, String>> locations_data = new ArrayList<Map<String, String>>();
        Map<String, String> m;
        Cursor cursor = db.query(
                "locations AS l INNER JOIN user_exercise_locations AS uel ON l.location_id = uel.location_id LEFT JOIN user_exercise_quests AS ueq ON l.location_id = ueq.location_id AND uel.user_id = ueq.user_id AND uel.exercise_id = ueq.exercise_id",
                new String[]{"l.location_id", "l.name AS location_name", "count(ueq.location_id) AS finished_quests_count"},
                "uel.user_id = ? AND uel.exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "l.location_id",
                null,
                "l.location_id ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    m.put("location_id", cursor.getString(cursor.getColumnIndex("location_id")));
                    m.put("location_name", cursor.getString(cursor.getColumnIndex("location_name")));
                    String finished_quest_count = cursor.getString(cursor.getColumnIndex("finished_quests_count"));
                    if (finished_quest_count.equals("5")) {
                        m.put("quests_info", "Выполнены все задания");
                    } else {
                        m.put("quests_info", String.format(Locale.ROOT, "Выполнено заданий: %s / 5", finished_quest_count));
                    }


                    locations_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return locations_data;
    }

    public Map<String, String> getLocationDataById(int location_id)
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put("location_id", Integer.toString(location_id));
        data.put("location_name", null);
        Cursor cursor = db.query("locations", new String[]{"name"}, "location_id = ?", new String[]{Integer.toString(location_id)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                data.put("location_name", cursor.getString(cursor.getColumnIndex("name")));
            }
            cursor.close();
        }

        return data;
    }

    // ===================  non_player_characters
    public ArrayList<NonPlayerCharacterEntity> getNonPlayerCharactersByLocationPositionLevel(int location_id, int position, int level)
    {
        ArrayList<NonPlayerCharacterEntity> entities = new ArrayList<>();
        Cursor cursor = db.query(
                "non_player_characters AS n LEFT JOIN npc_in_location_positions AS nlp ON n.npc_id = nlp.npc_id LEFT JOIN location_positions AS lp ON nlp.location_level_position_id = lp.location_level_position_id",
                new String[]{"lp.location_id", "n.level", "lp.position", "n.type", "n.npc_id", "n.fp", "n.max_res", "n.multiplier", "n.exp", "n.bonus_chance", "n.bonus_multiplier", "n.name", "n.resistance", "n.actions", "n.pre_actions"},
                "lp.location_id = ? AND lp.position = ? AND lp.level = ?",
                new String[]{Integer.toString(location_id), Integer.toString(position), Integer.toString(level)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NonPlayerCharacterEntity npc_entity = new NonPlayerCharacterEntity(context);
                    int fitness_points = cursor.getInt(cursor.getColumnIndex("fp"));
                    int max_result = cursor.getInt(cursor.getColumnIndex("max_res"));
                    float multiplier = cursor.getFloat(cursor.getColumnIndex("multiplier"));
                    int initialActionPoints = Math.round(max_result * multiplier * location_id);
                    npc_entity.setId(cursor.getInt(cursor.getColumnIndex("npc_id")))
                            .setName(cursor.getString(cursor.getColumnIndex("name")))
                            .setInitialFitnessPoints(fitness_points)
                            .setCurrentFitnessPoints(fitness_points)
                            .setInitialActionPoints(initialActionPoints)
                            .setCurrentActionPoints(0)
                            .setMultiplier(cursor.getFloat(cursor.getColumnIndex("multiplier")))
                            .setResistance(cursor.getInt(cursor.getColumnIndex("resistance")))
                            .setBonusChance(cursor.getFloat(cursor.getColumnIndex("bonus_chance")))
                            .setLevel(cursor.getInt(cursor.getColumnIndex("level")))
                            .setSpecialisationId(0)
                            .setBonusMultiplier(cursor.getFloat(cursor.getColumnIndex("bonus_multiplier")));
                    npc_entity.setExp(cursor.getInt(cursor.getColumnIndex("exp")))
                            .setMaxResult(max_result)
                            .setLocationId(cursor.getInt(cursor.getColumnIndex("location_id")))
                            .setPosition(cursor.getInt(cursor.getColumnIndex("position")))
                            .setType(cursor.getString(cursor.getColumnIndex("type")))
                            .setActions(cursor.getString(cursor.getColumnIndex("actions")))
                            .setPreActions(cursor.getString(cursor.getColumnIndex("pre_actions")));
                    entities.add(npc_entity);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return entities;
    }

    public String getUserNameById(int user_id)
    {
        String userName = "";
        String[] columns = new String[] { "name"};
        Cursor cursor = db.query("users", columns, "user_id = ?", new String[]{Integer.toString(user_id)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userName = cursor.getString(cursor.getColumnIndex("name"));
            }
            cursor.close();
        }

        return userName;
    }

    //========= Skills
    public ArrayList<Map<String, String>> getUnexploredSkillsData()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        int user_level = gameHelper.getCachedUserLevel();

        ArrayList<Map<String, String>> skills_data = new ArrayList<Map<String, String>>();
        Map<String, String> m;
        Cursor cursor = db.query(
                "skills AS s LEFT JOIN skill_groups AS sg ON s.skill_group_id = sg.skill_group_id LEFT JOIN user_exercise_skills AS ues ON s.skill_id = ues.skill_id AND ues.user_id = " + Integer.toString(user_id) + " AND ues.exercise_id = " + Integer.toString(exercise_id) + " LEFT JOIN user_exercises AS ue ON ue.user_id = " + Integer.toString(user_id) + " AND ue.exercise_id = " + Integer.toString(exercise_id),
                new String[]{"s.skill_id", "s.specialisation", "s.skill_group_id", "MIN(s.skill_level) min_skill_level", "sg.name AS skill_group_name", "s.required_level", "s.skill_points", "s.info AS skill_info"},
                "ues.skill_id IS NULL AND s.required_level <= ? AND (s.specialisation = 0 OR s.specialisation = ue.specialisation)",
                new String[]{Integer.toString(user_level)},
                "s.skill_group_id",
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    m.put("skill_group_id", cursor.getString(cursor.getColumnIndex("skill_group_id")));
                    m.put("skill_group_name", cursor.getString(cursor.getColumnIndex("skill_group_name")));
                    m.put("skill_level", cursor.getString(cursor.getColumnIndex("min_skill_level")));
                    m.put("skill_id", cursor.getString(cursor.getColumnIndex("skill_id")));
                    m.put("specialisation", cursor.getString(cursor.getColumnIndex("specialisation")));
                    m.put("required_level", cursor.getString(cursor.getColumnIndex("required_level")));
                    m.put("skill_points", cursor.getString(cursor.getColumnIndex("skill_points")));
                    m.put("skill_info", cursor.getString(cursor.getColumnIndex("skill_info")));
                    m.put("full_info", String.format(
                            Locale.ROOT,
                            "[%s] Ур. %s: %s. Треб. ур.: %s",
                            m.get("skill_points"),
                            m.get("skill_level"),
                            m.get("skill_info"),
                            m.get("required_level")
                    ));
                    //Log.d("myLogs", m.toString());
                    skills_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return skills_data;
    }

    public int[] getLevelsForLocation(int location_id)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        int[] data = {1, 1, 1, 1, 1};
        Cursor cursor = db.query(
                "user_exercise_locations AS uel",
                new String[]{"uel.loc_pos_1_level", "uel.loc_pos_2_level", "uel.loc_pos_3_level", "uel.loc_pos_4_level", "uel.loc_pos_5_level"},
                "uel.location_id = ? AND uel.user_id = ? AND uel.exercise_id = ?",
                new String[]{Integer.toString(location_id), Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    data[0] = cursor.getInt(cursor.getColumnIndex("loc_pos_1_level"));
                    data[1] = cursor.getInt(cursor.getColumnIndex("loc_pos_2_level"));
                    data[2] = cursor.getInt(cursor.getColumnIndex("loc_pos_3_level"));
                    data[3] = cursor.getInt(cursor.getColumnIndex("loc_pos_4_level"));
                    data[4] = cursor.getInt(cursor.getColumnIndex("loc_pos_5_level"));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return data;
    }

    public boolean openLocationForCurrentUserExercise(int location_id)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return openLocationForUserExercise(user_id, exercise_id, location_id);
    }

    public boolean openLocationForUserExercise(int user_id, int exercise_id, int location_id)
    {
        Map<String, String> data = getLocationDataById(location_id);
        if (data.isEmpty()) {
            return false;
        }

        boolean has_record = false;
        Cursor cursor = db.query(
                "user_exercise_locations AS uel",
                new String[]{"uel.location_id"},
                "uel.location_id = ? AND uel.user_id = ? AND uel.exercise_id = ?",
                new String[]{Integer.toString(location_id), Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d("myLogs", cursor.getString(cursor.getColumnIndex("location_id")));
                    has_record = true;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (!has_record) {
            db.execSQL("INSERT INTO user_exercise_locations (location_id, user_id, exercise_id, loc_pos_1_level, loc_pos_2_level, loc_pos_3_level, loc_pos_4_level, loc_pos_5_level) VALUES ("
                    + Integer.toString(location_id) + ", " + Integer.toString(user_id) + ", " + Integer.toString(exercise_id) + ", 1, 1, 1, 1, 1);");
            return true;
        }

        return false;
    }

    public void levelUpForNPC(int user_id, int exercise_id, int location_id, int position)
    {
        String field = "loc_pos_" + Integer.toString(position) + "_level";
        db.execSQL("UPDATE user_exercise_locations SET " + field + " = " + field + " + 1 WHERE user_id = " +  Integer.toString(user_id) + " AND exercise_id = " +  Integer.toString(exercise_id) + " AND location_id = " +  Integer.toString(location_id));
    }

    public void learnSkillFromSkillGroup(int skill_id)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        boolean has_record = false;
        Cursor cursor = db.query(
                "user_exercise_skills AS ues",
                new String[]{"ues.user_id"},
                "ues.skill_id = ? AND ues.user_id = ? AND ues.exercise_id = ?",
                new String[]{Integer.toString(skill_id), Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d("myLogs", cursor.getString(cursor.getColumnIndex("user_id")));
                    has_record = true;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (!has_record) {
            db.execSQL("INSERT INTO user_exercise_skills (skill_id, user_id, exercise_id) VALUES ("
                    + Integer.toString(skill_id) + ", " + Integer.toString(user_id) + ", " + Integer.toString(exercise_id) + ");");
        }
    }

    public int getSpentSkillPoints()
    {
        int spent_skill_points = 0;
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        Cursor cursor = db.query(
                "skills AS s LEFT JOIN user_exercise_skills AS ues ON s.skill_id = ues.skill_id LEFT JOIN user_exercises AS ue ON ues.user_id = ue.user_id AND ues.exercise_id = ue.exercise_id",
                new String[]{"SUM(s.skill_points) AS spent_skill_points"},
                "ues.user_id = ? AND ues.exercise_id = ? AND (s.specialisation = 0 OR s.specialisation = ue.specialisation)",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    spent_skill_points = cursor.getInt(cursor.getColumnIndex("spent_skill_points"));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return spent_skill_points;
    }

    public Map<String, String> getExtraParametersFromLearntSkills(int user_id, int exercise_id, int type)
    {
        Map<String, String> result_data = new HashMap<String, String>();
        Cursor cursor = db.query(
                "skills AS s LEFT JOIN user_exercise_skills AS ues ON s.skill_id = ues.skill_id LEFT JOIN user_exercises AS ue ON ues.user_id = ue.user_id AND ues.exercise_id = ue.exercise_id LEFT JOIN skill_groups AS sg ON s.skill_group_id = sg.skill_group_id",
                new String[]{
                        "SUM(s.extra_fitness_points) AS sum_extra_fitness_points",
                        "SUM(s.extra_resistance) AS sum_extra_resistance",
                        "SUM(s.extra_multiplier) AS sum_extra_multiplier",
                        "SUM(s.extra_bonus_chance) AS sum_extra_bonus_chance",
                        "SUM(s.extra_bonus_multiplier) AS sum_extra_bonus_multiplier",

                        "SUM(s.extra_fitness_points_ratio1) AS sum_extra_fitness_points_ratio1",
                        "SUM(s.extra_resistance_ratio1) AS sum_extra_resistance_ratio1",
                        "SUM(s.extra_multiplier_ratio1) AS sum_extra_multiplier_ratio1",
                        "SUM(s.extra_bonus_chance_ratio1) AS sum_extra_bonus_chance_ratio1",
                        "SUM(s.extra_bonus_multiplier_ratio1) AS sum_extra_bonus_multiplier_ratio1",

                        "SUM(s.extra_regeneration_base) AS sum_extra_regeneration_base"
                },
                "ues.user_id = ? AND ues.exercise_id = ? AND sg.type = ? AND (s.specialisation = 0 OR s.specialisation = ue.specialisation)",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(type)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result_data.put("extra_fitness_points", cursor.getString(cursor.getColumnIndex("sum_extra_fitness_points")));
                    result_data.put("extra_resistance", cursor.getString(cursor.getColumnIndex("sum_extra_resistance")));
                    result_data.put("extra_multiplier", cursor.getString(cursor.getColumnIndex("sum_extra_multiplier")));
                    result_data.put("extra_bonus_chance", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_chance")));
                    result_data.put("extra_bonus_multiplier", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_multiplier")));

                    result_data.put("extra_fitness_points_ratio1", cursor.getString(cursor.getColumnIndex("sum_extra_fitness_points_ratio1")));
                    result_data.put("extra_resistance_ratio1", cursor.getString(cursor.getColumnIndex("sum_extra_resistance_ratio1")));
                    result_data.put("extra_multiplier_ratio1", cursor.getString(cursor.getColumnIndex("sum_extra_multiplier_ratio1")));
                    result_data.put("extra_bonus_chance_ratio1", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_chance_ratio1")));
                    result_data.put("extra_bonus_multiplier_ratio1", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_multiplier_ratio1")));

                    result_data.put("extra_regeneration_base", cursor.getString(cursor.getColumnIndex("sum_extra_regeneration_base")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        result_data.put("extra_fitness_points", setDefaultValueIfNull(result_data.get("extra_fitness_points"), "0"));
        result_data.put("extra_resistance", setDefaultValueIfNull(result_data.get("extra_resistance"), "0"));
        result_data.put("extra_multiplier", setDefaultValueIfNull(result_data.get("extra_multiplier"), "0.00"));
        result_data.put("extra_bonus_chance", setDefaultValueIfNull(result_data.get("extra_bonus_chance"), "0.00"));
        result_data.put("extra_bonus_multiplier", setDefaultValueIfNull(result_data.get("extra_bonus_multiplier"), "0.00"));

        result_data.put("extra_fitness_points_ratio1", setDefaultValueIfNull(result_data.get("extra_fitness_points_ratio1"), "0.00"));
        result_data.put("extra_resistance_ratio1", setDefaultValueIfNull(result_data.get("extra_resistance_ratio1"), "0.00"));
        result_data.put("extra_multiplier_ratio1", setDefaultValueIfNull(result_data.get("extra_multiplier_ratio1"), "0.00"));
        result_data.put("extra_bonus_chance_ratio1", setDefaultValueIfNull(result_data.get("extra_bonus_chance_ratio1"), "0.00"));
        result_data.put("extra_bonus_multiplier_ratio1", setDefaultValueIfNull(result_data.get("extra_bonus_multiplier_ratio1"), "0.00"));

        result_data.put("extra_regeneration_base", setDefaultValueIfNull(result_data.get("extra_regeneration_base"), "0"));

        return result_data;
    }

    private String setDefaultValueIfNull(String value, String default_value)
    {
        String result_value = value;
        if (value == null) {
            result_value = default_value;
        }
        return result_value;
    }

    public Map<String, String> getExtraParametersFromActiveSkill(int skill_group_id, int skill_level)
    {
        Map<String, String> result_data = new HashMap<String, String>();
        Cursor cursor = db.query(
                "skills AS s LEFT JOIN skill_groups AS sg ON s.skill_group_id = sg.skill_group_id",
                new String[]{
                        "SUM(s.extra_fitness_points) AS sum_extra_fitness_points",
                        "SUM(s.extra_resistance) AS sum_extra_resistance",
                        "SUM(s.extra_multiplier) AS sum_extra_multiplier",
                        "SUM(s.extra_bonus_chance) AS sum_extra_bonus_chance",
                        "SUM(s.extra_bonus_multiplier) AS sum_extra_bonus_multiplier",

                        "SUM(s.extra_fitness_points_ratio2) AS sum_extra_fitness_points_ratio2",
                        "SUM(s.extra_resistance_ratio2) AS sum_extra_resistance_ratio2",
                        "SUM(s.extra_multiplier_ratio2) AS sum_extra_multiplier_ratio2",
                        "SUM(s.extra_bonus_chance_ratio2) AS sum_extra_bonus_chance_ratio2",
                        "SUM(s.extra_bonus_multiplier_ratio2) AS sum_extra_bonus_multiplier_ratio2",

                        "SUM(s.extra_regeneration_base) AS sum_extra_regeneration_base",
                        "SUM(s.extra_regeneration_ratio) AS sum_extra_regeneration_ratio"
                },
                "(sg.type = 1 OR sg.type = 2) AND s.skill_group_id = ? AND s.skill_level <= ?",
                new String[]{Integer.toString(skill_group_id), Integer.toString(skill_level)},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result_data.put("extra_fitness_points", cursor.getString(cursor.getColumnIndex("sum_extra_fitness_points")));
                    result_data.put("extra_resistance", cursor.getString(cursor.getColumnIndex("sum_extra_resistance")));
                    result_data.put("extra_multiplier", cursor.getString(cursor.getColumnIndex("sum_extra_multiplier")));
                    result_data.put("extra_bonus_chance", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_chance")));
                    result_data.put("extra_bonus_multiplier", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_multiplier")));

                    result_data.put("extra_fitness_points_ratio2", cursor.getString(cursor.getColumnIndex("sum_extra_fitness_points_ratio2")));
                    result_data.put("extra_resistance_ratio2", cursor.getString(cursor.getColumnIndex("sum_extra_resistance_ratio2")));
                    result_data.put("extra_multiplier_ratio2", cursor.getString(cursor.getColumnIndex("sum_extra_multiplier_ratio2")));
                    result_data.put("extra_bonus_chance_ratio2", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_chance_ratio2")));
                    result_data.put("extra_bonus_multiplier_ratio2", cursor.getString(cursor.getColumnIndex("sum_extra_bonus_multiplier_ratio2")));

                    result_data.put("extra_regeneration_base", cursor.getString(cursor.getColumnIndex("sum_extra_regeneration_base")));
                    result_data.put("extra_regeneration_ratio", cursor.getString(cursor.getColumnIndex("sum_extra_regeneration_ratio")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        result_data.put("extra_fitness_points", setDefaultValueIfNull(result_data.get("extra_fitness_points"), "0"));
        result_data.put("extra_resistance", setDefaultValueIfNull(result_data.get("extra_resistance"), "0"));
        result_data.put("extra_multiplier", setDefaultValueIfNull(result_data.get("extra_multiplier"), "0.00"));
        result_data.put("extra_bonus_chance", setDefaultValueIfNull(result_data.get("extra_bonus_chance"), "0.00"));
        result_data.put("extra_bonus_multiplier", setDefaultValueIfNull(result_data.get("extra_bonus_multiplier"), "0.00"));

        result_data.put("extra_fitness_points_ratio2", setDefaultValueIfNull(result_data.get("extra_fitness_points_ratio2"), "0.00"));
        result_data.put("extra_resistance_ratio2", setDefaultValueIfNull(result_data.get("extra_resistance_ratio2"), "0.00"));
        result_data.put("extra_multiplier_ratio2", setDefaultValueIfNull(result_data.get("extra_multiplier_ratio2"), "0.00"));
        result_data.put("extra_bonus_chance_ratio2", setDefaultValueIfNull(result_data.get("extra_bonus_chance_ratio2"), "0.00"));
        result_data.put("extra_bonus_multiplier_ratio2", setDefaultValueIfNull(result_data.get("extra_bonus_multiplier_ratio2"), "0.00"));

        result_data.put("extra_regeneration_base", setDefaultValueIfNull(result_data.get("extra_regeneration_base"), "0"));
        result_data.put("extra_regeneration_ratio", setDefaultValueIfNull(result_data.get("extra_regeneration_ratio"), "0.00"));

        return result_data;
    }

    public SkillView getSkillView(int skill_id, String owner_name)
    {
        Cursor cursor = db.query(
                "skills AS s LEFT JOIN skill_groups AS sg ON s.skill_group_id = sg.skill_group_id",
                new String[]{"sg.name AS group_name", "sg.skill_group_id", "s.label", "s.duration", "s.reuse", "sg.target_type", "s.splash_multiplier", "MAX(s.skill_level) AS skill_level"},
                "s.skill_id = ?",
                new String[]{Integer.toString(skill_id)},
                null,
                null,
                null
        );

        SkillView skill_view = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                skill_view = new SkillView(
                        cursor.getString(cursor.getColumnIndex("label")),
                        cursor.getInt(cursor.getColumnIndex("skill_group_id")),
                        cursor.getInt(cursor.getColumnIndex("skill_level")),
                        cursor.getInt(cursor.getColumnIndex("duration")),
                        cursor.getInt(cursor.getColumnIndex("reuse")),
                        cursor.getInt(cursor.getColumnIndex("target_type")),
                        owner_name,
                        cursor.getFloat(cursor.getColumnIndex("splash_multiplier"))
                );
            }
        }
        return skill_view;
    }

    public ArrayList<Map<String, String>> getCurrentUserLearntSkills()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        return getLearntSkills(user_id, exercise_id);
    }

    public ArrayList<Map<String, String>> getLearntSkills(int user_id, int exercise_id)
    {
        ArrayList<Map<String, String>> result_data = new ArrayList<>();
        Cursor cursor = db.query(
                "skills AS s LEFT JOIN user_exercise_skills AS ues ON s.skill_id = ues.skill_id LEFT JOIN user_exercises AS ue ON ues.user_id = ue.user_id AND ues.exercise_id = ue.exercise_id LEFT JOIN skill_groups AS sg ON s.skill_group_id = sg.skill_group_id",
                new String[]{"sg.name AS group_name", "sg.skill_group_id", "s.label", "s.duration", "s.reuse", "sg.target_type", "s.splash_multiplier", "MAX(s.skill_level) AS skill_level"},
                "ues.user_id = ? AND ues.exercise_id = ? AND (s.specialisation = 0 OR s.specialisation = ue.specialisation)",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "sg.skill_group_id",
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> m = new HashMap<>();
                    m.put("skill_label", cursor.getString(cursor.getColumnIndex("label")));
                    m.put("skill_group_name", cursor.getString(cursor.getColumnIndex("group_name")));
                    m.put("skill_group_id", cursor.getString(cursor.getColumnIndex("skill_group_id")));
                    m.put("skill_level", cursor.getString(cursor.getColumnIndex("skill_level")));
                    m.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                    m.put("reuse", cursor.getString(cursor.getColumnIndex("reuse")));
                    m.put("target_type", cursor.getString(cursor.getColumnIndex("target_type")));
                    m.put("splash_multiplier", cursor.getString(cursor.getColumnIndex("splash_multiplier")));
                    m.put("full_info", String.format(
                            Locale.ROOT,
                            "Ур. %s: %s.",
                            m.get("skill_level"),
                            m.get("skill_label")
                    ));
                    result_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return result_data;
    }

    public ArrayList<Map<String, String>> getActiveLearntSkills(int user_id, int exercise_id, int type)
    {
        ArrayList<Map<String, String>> result_data = new ArrayList<>();
        Cursor cursor = db.query(
                "skills AS s LEFT JOIN user_exercise_skills AS ues ON s.skill_id = ues.skill_id LEFT JOIN user_exercises AS ue ON ues.user_id = ue.user_id AND ues.exercise_id = ue.exercise_id LEFT JOIN skill_groups AS sg ON s.skill_group_id = sg.skill_group_id",
                new String[]{"sg.name AS group_name", "sg.skill_group_id", "s.label", "s.duration", "s.reuse", "sg.target_type", "s.splash_multiplier", "MAX(s.skill_level) AS skill_level"},
                "ues.user_id = ? AND ues.exercise_id = ? AND sg.type = ? AND (s.specialisation = 0 OR s.specialisation = ue.specialisation)",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(type)},
                "sg.skill_group_id",
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> m = new HashMap<>();
                    m.put("skill_label", cursor.getString(cursor.getColumnIndex("label")));
                    m.put("skill_group_name", cursor.getString(cursor.getColumnIndex("group_name")));
                    m.put("skill_group_id", cursor.getString(cursor.getColumnIndex("skill_group_id")));
                    m.put("skill_level", cursor.getString(cursor.getColumnIndex("skill_level")));
                    m.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                    m.put("reuse", cursor.getString(cursor.getColumnIndex("reuse")));
                    m.put("target_type", cursor.getString(cursor.getColumnIndex("target_type")));
                    m.put("splash_multiplier", cursor.getString(cursor.getColumnIndex("splash_multiplier")));
                    result_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return result_data;
    }

    public int getUserLevel(int user_id, int exercise_id)
    {
        long total_exp = getUserExerciseExp(user_id, exercise_id);
        float user_level = getFloatPlayerLevel(total_exp);

        return (int) user_level;
    }

    public int getUserFitnessPoints(int user_id, int exercise_id)
    {
        int user_level = getUserLevel(user_id, exercise_id);
        int base   = Integer.parseInt(getLevelData(user_level).get("base_fp"));
        int extra1 = Integer.parseInt(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_fitness_points"));
        int extra2 = (int) Math.sqrt(getTrainingSumResult(user_id, exercise_id));
        float ratio1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_fitness_points_ratio1"));

        return (int)((base + extra1 + extra2) * (1 + ratio1));
    }

    public int getCurrentUserFitnessPoints()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserFitnessPoints(user_id, exercise_id);
    }

    public float getUserMultiplier(int user_id, int exercise_id)
    {
        int user_level = getUserLevel(user_id, exercise_id);
        float base   = Float.parseFloat(getLevelData(user_level).get("base_multiplier"));
        float extra1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_multiplier"));
        float extra2 = 0;
        float ratio1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_multiplier_ratio1"));

        return (base + extra1 + extra2) * (1 + ratio1);
    }

    public float getCurrentUserMultiplier()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserMultiplier(user_id, exercise_id);
    }

    public int getUserRegeneration(int user_id, int exercise_id)
    {
        return Integer.parseInt(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_regeneration_base"));
    }

    public float getUserBonusChance(int user_id, int exercise_id)
    {
        int user_level = getUserLevel(user_id, exercise_id);
        float base = Float.parseFloat(getLevelData(user_level).get("base_bonus_chance"));
        float extra1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_bonus_chance"));
        float extra2 = 0;
        float ratio1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_bonus_chance_ratio1"));

        return (base + extra1 + extra2) * (1 + ratio1);
    }

    public float getCurrentUserBonusChance()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserBonusChance(user_id, exercise_id);
    }

    public float getUserBonusMultiplier(int user_id, int exercise_id)
    {
        int user_level = getUserLevel(user_id, exercise_id);
        float base   = Float.parseFloat(getLevelData(user_level).get("base_bonus_multiplier"));
        float extra1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_bonus_multiplier"));
        float extra2 = 0;
        float ratio1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_bonus_multiplier_ratio1"));

        return (base + extra1 + extra2) * (1 + ratio1);
    }

    public float getCurrentUserBonusMultiplier()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserBonusMultiplier(user_id, exercise_id);
    }

    public int getUserResistance(int user_id, int exercise_id)
    {
        float RESISTANCE_RATIO = 537;
        int user_level = getUserLevel(user_id, exercise_id);
        Log.d(context.getResources().getString(R.string.log_tag), "DEBUG getCachedUserLevel: " + user_level);
        int base   = Integer.parseInt(getLevelData(user_level).get("base_resistance"));
        int extra1 = Integer.parseInt(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_resistance"));
        int extra2 = Math.round(getTrainingSumResult(user_id, exercise_id) / RESISTANCE_RATIO);
        float ratio1 = Float.parseFloat(getExtraParametersFromLearntSkills(user_id, exercise_id, 0).get("extra_resistance_ratio1"));

        return (int)((base + extra1 + extra2) * (1 + ratio1));
    }

    public int getCurrentUserResistance()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserResistance(user_id, exercise_id);
    }

    protected LocationPositionEntity getLocationPositionByIds(int location_id, int level, int position)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        Cursor cursor = db.query(
                "location_positions AS lp LEFT JOIN user_exercise_trainings AS uet ON lp.position = uet.position AND lp.location_id = uet.location_id AND uet.result_state = 2 AND uet.user_id = " + Integer.toString(user_id) + " AND uet.exercise_id = " + Integer.toString(exercise_id),
                new String[]{"lp.location_level_position_id", "lp.info", "lp.location_id", "lp.quest_cnt", "lp.quest_exp", "lp.name", "lp.position", "lp.level", "sum(uet.quest_owner) AS wins"},
                "lp.location_id = ? AND lp.level = ? AND lp.position = ?",
                new String[]{Integer.toString(location_id), Integer.toString(level), Integer.toString(position)},
                null,
                null,
                null
        );

        LocationPositionEntity location_position_entity = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                location_position_entity = new LocationPositionEntity();
                location_position_entity
                        .setLocationLevelPositionId(cursor.getInt(cursor.getColumnIndex("location_level_position_id")))
                        .setName(cursor.getString(cursor.getColumnIndex("name")))
                        .setLevel(cursor.getInt(cursor.getColumnIndex("level")))
                        .setLocationId(cursor.getInt(cursor.getColumnIndex("location_id")))
                        .setPosition(cursor.getInt(cursor.getColumnIndex("position")))
                        .setWins(cursor.getInt(cursor.getColumnIndex("wins")))
                        .setQuestCnt(cursor.getInt(cursor.getColumnIndex("quest_cnt")))
                        .setQuestExp(cursor.getInt(cursor.getColumnIndex("quest_exp")))
                        .setInfo(cursor.getString(cursor.getColumnIndex("info")));
            }
        }

        return location_position_entity;
    }

    protected LocationPositionEntity getLocationPosition(int location_level_position_id)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        Cursor cursor = db.query(
                "location_positions AS lp LEFT JOIN user_exercise_trainings AS uet ON lp.position = uet.position AND lp.location_id = uet.location_id AND uet.result_state = 2 AND uet.user_id = " + Integer.toString(user_id) + " AND uet.exercise_id = " + Integer.toString(exercise_id),
                new String[]{"lp.location_level_position_id", "lp.info", "lp.location_id", "lp.quest_cnt", "lp.quest_exp", "lp.name", "lp.position", "lp.level", "sum(uet.quest_owner) AS wins"},
                "lp.location_level_position_id = ?",
                new String[]{Integer.toString(location_level_position_id)},
                null,
                null,
                null
        );

        LocationPositionEntity location_position_entity = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                location_position_entity = new LocationPositionEntity();
                location_position_entity
                        .setLocationLevelPositionId(cursor.getInt(cursor.getColumnIndex("location_level_position_id")))
                        .setName(cursor.getString(cursor.getColumnIndex("name")))
                        .setLevel(cursor.getInt(cursor.getColumnIndex("level")))
                        .setLocationId(cursor.getInt(cursor.getColumnIndex("location_id")))
                        .setPosition(cursor.getInt(cursor.getColumnIndex("position")))
                        .setWins(cursor.getInt(cursor.getColumnIndex("wins")))
                        .setQuestCnt(cursor.getInt(cursor.getColumnIndex("quest_cnt")))
                        .setQuestExp(cursor.getInt(cursor.getColumnIndex("quest_exp")))
                        .setInfo(cursor.getString(cursor.getColumnIndex("info")));
            }
        }

        return location_position_entity;
    }

    protected List<LocationPositionEntity> getLocationPositionsData(int location_id)
    {
        int[] levels = getLevelsForLocation(location_id);

        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        List<LocationPositionEntity> location_positions_data = new ArrayList<>();
        Cursor cursor = db.query(
                "location_positions AS lp LEFT JOIN user_exercise_trainings AS uet ON lp.position = uet.position AND lp.location_id = uet.location_id AND uet.result_state = 2 AND uet.user_id = " + Integer.toString(user_id) + " AND uet.exercise_id = " + Integer.toString(exercise_id),
                new String[]{"lp.location_level_position_id", "lp.info", "lp.location_id", "lp.quest_cnt", "lp.quest_exp", "lp.name", "lp.position", "lp.level", "sum(uet.quest_owner) AS wins"},
                "lp.location_id = ? AND (lp.position = 1 AND lp.level = ? OR lp.position = 2 AND lp.level = ? OR lp.position = 3 AND lp.level = ? OR lp.position = 4 AND lp.level = ? OR lp.position = 5 AND lp.level = ?)",
                new String[]{Integer.toString(location_id), Integer.toString(levels[0]), Integer.toString(levels[1]), Integer.toString(levels[2]), Integer.toString(levels[3]), Integer.toString(levels[4])},
                "lp.position",
                null,
                "lp.position ASC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    LocationPositionEntity location_position_entity = new LocationPositionEntity();
                    location_position_entity
                            .setLocationLevelPositionId(cursor.getInt(cursor.getColumnIndex("location_level_position_id")))
                            .setName(cursor.getString(cursor.getColumnIndex("name")))
                            .setLevel(cursor.getInt(cursor.getColumnIndex("level")))
                            .setLocationId(cursor.getInt(cursor.getColumnIndex("location_id")))
                            .setPosition(cursor.getInt(cursor.getColumnIndex("position")))
                            .setWins(cursor.getInt(cursor.getColumnIndex("wins")))
                            .setQuestCnt(cursor.getInt(cursor.getColumnIndex("quest_cnt")))
                            .setQuestExp(cursor.getInt(cursor.getColumnIndex("quest_exp")))
                            .setInfo(cursor.getString(cursor.getColumnIndex("info")));

                    location_positions_data.add(location_position_entity);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return location_positions_data;
    }

    // ============== Training
    public long addTraining(int user_id, int exercise_id, int level, int sum_result, int max_result, int exp, int number_of_moves, int result_state, int location_id, int position, int duration, boolean quest_owner, int my_team_fp, int op_team_fp)
    {
        ContentValues values = new ContentValues();
        values.put("event_timestamp", gameHelper.getTodayTimestampString());
        values.put("user_id", user_id);
        values.put("exercise_id", exercise_id);
        values.put("level", level);
        values.put("sum_result", sum_result);
        values.put("max_result", max_result);
        values.put("number_of_moves", number_of_moves);
        values.put("duration", duration);
        values.put("result_state", result_state);
        values.put("exp", exp);
        values.put("location_id", location_id);
        values.put("position", position);
        values.put("quest_owner", quest_owner);
        values.put("my_team_fp", my_team_fp);
        values.put("op_team_fp", op_team_fp);

        return db.insert("user_exercise_trainings", null, values);
    }

    public long updateTraining(long training_id, int user_id, int exercise_id, int level, int sum_result, int max_result, int exp, int number_of_moves, int result_state, int location_id, int position, int duration, boolean quest_owner, int my_team_fp, int op_team_fp, String results)
    {
        ContentValues values = new ContentValues();
        values.put("level", level);
        values.put("sum_result", sum_result);
        values.put("max_result", max_result);
        values.put("number_of_moves", number_of_moves);
        values.put("duration", duration);
        values.put("result_state", result_state);
        values.put("exp", exp);
        values.put("location_id", location_id);
        values.put("position", position);
        values.put("quest_owner", quest_owner);
        values.put("my_team_fp", my_team_fp);
        values.put("op_team_fp", op_team_fp);
        values.put("results", results);

        return db.update(
                "user_exercise_trainings",
                values,
                "training_id = ? AND user_id = ? AND exercise_id = ?",
                new String[] {
                        Long.toString(training_id),
                        Integer.toString(user_id),
                        Integer.toString(exercise_id)
                }
        );
    }

    public int getCurrentUserTrainingDaysCount()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getTrainingDaysCount(user_id, exercise_id);
    }

    public int getTrainingDaysCount(int user_id, int exercise_id)
    {
        int training_days = 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"COUNT(DISTINCT(date(event_timestamp))) as training_days"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id",
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                training_days = cursor.getInt(cursor.getColumnIndex("training_days"));
            }

            cursor.close();
        }

        return training_days;
    }

    public int getCurrentUserTrainingSumResult()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getTrainingSumResult(user_id, exercise_id);
    }

    int getCurrentUserSkillPoints()
    {
        return getCurrentUserTrainingSumResult() + getAchievementSkillPoints() - getSpentSkillPoints();
    }

    int getAchievementSkillPoints()
    {
        int total_skill_points = 0;
        ArrayList<Map<String, String>> achievement_data = getAchievementsData();
        for (Map<String, String> data : achievement_data) {
            total_skill_points += Integer.parseInt(data.get("skill_points"));
        }

        return total_skill_points;
    }

    public int getTrainingSumResult(int user_id, int exercise_id)
    {
        int count = 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(sum_result) AS total_count"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id",
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex("total_count"));
            }
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG getTodayTraining: " + count);
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "Cursor is null");

        return count;
    }

    UserExerciseTrainingStat getUserExerciseTrainingStat(int from_field, int from_amount, int to_field, int to_amount)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        UserExerciseTrainingStat stat = new UserExerciseTrainingStat();

        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(sum_result) AS total_cnt, MAX(sum_result) AS max_competition_result, MAX(max_result) AS max_result, SUM(number_of_moves) AS total_number_of_moves, COUNT(DISTINCT(date(event_timestamp))) as training_days"},
                "user_id = ? AND exercise_id = ? AND date(event_timestamp) > ? AND date(event_timestamp) <= ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), gameHelper.getDateString(from_field, from_amount), gameHelper.getDateString(to_field, to_amount)},
                "user_id, exercise_id",
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                stat.total_cnt = cursor.getInt(cursor.getColumnIndex("total_cnt"));
                stat.max_competition_result = cursor.getInt(cursor.getColumnIndex("max_competition_result"));
                stat.max_result = cursor.getInt(cursor.getColumnIndex("max_result"));
                stat.total_number_of_moves = cursor.getInt(cursor.getColumnIndex("total_number_of_moves"));
                stat.training_days = cursor.getInt(cursor.getColumnIndex("training_days"));
            }
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG getUserExerciseTrainingStat: " + stat.toString());
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG getUserExerciseTrainingStat: Cursor is null");

        return stat;
    }

    public int getUserExerciseTrainingMaxCompetitionResult()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        int max_result = 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"MAX(sum_result) AS max_result"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id",
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                max_result = cursor.getInt(cursor.getColumnIndex("max_result"));
            }
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG getTodayTraining: " + max_result);
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "Cursor is null");

        return max_result;
    }

    public int getCurrentUserExerciseTrainingTotalNumberOfMoves()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseTrainingTotalNumberOfMoves(user_id, exercise_id);
    }

    public int getUserExerciseTrainingTotalNumberOfMoves(int user_id, int exercise_id)
    {
        int total_number_of_moves = 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(number_of_moves) AS total_number_of_moves"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id",
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total_number_of_moves = cursor.getInt(cursor.getColumnIndex("total_number_of_moves"));
            }
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG total_number_of_moves: " + total_number_of_moves);
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "Cursor is null");

        return total_number_of_moves;
    }

    public ArrayList<Map<String, String>> getCurrentUserLastTrainingsData(int count)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        return getLastTrainingsData(user_id, exercise_id, count);
    }

    public String getDuration(int duration)
    {
        int hours = duration / 3600;
        int t1 = duration - hours * 3600;
        int minutes = t1 / 60;
        int seconds = t1 - minutes * 60;

        return String.format(
                Locale.ROOT,
                "%d:%02d:%02d",
                hours,
                minutes,
                seconds
        );
    }

    public ArrayList<Map<String, String>> getLastTrainingsData(int user_id, int exercise_id, int count)
    {
        ArrayList<Map<String, String>> last_trainings_data = new ArrayList<Map<String, String>>();
        Map<String, String> m;

        Cursor cursor = db.query(
                "user_exercise_trainings AS uet LEFT JOIN location_positions AS lp ON uet.level = lp.level AND uet.location_id = lp.location_id AND uet.position = lp.position LEFT JOIN locations AS l ON lp.location_id = l.location_id",
                new String[] { "uet.event_timestamp", "uet.sum_result", "uet.max_result", "uet.number_of_moves", "uet.exp", "uet.result_state", "lp.name AS location_position_name", "lp.level AS level", "l.name AS location_name", "uet.duration", "uet.my_team_fp", "uet.op_team_fp", "uet.results" },
                "uet.user_id = ? AND uet.exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                "uet.training_id DESC LIMIT " + Integer.toString(count)
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                do {
                    m = new HashMap<>();
                    m.put("event_timestamp", cursor.getString(cursor.getColumnIndex("event_timestamp")));
                    m.put("sum_result", cursor.getString(cursor.getColumnIndex("sum_result")));
                    m.put("max_result", cursor.getString(cursor.getColumnIndex("max_result")));
                    m.put("number_of_moves",cursor.getString(cursor.getColumnIndex("number_of_moves")));
                    m.put("exp", cursor.getString(cursor.getColumnIndex("exp")));
                    m.put("result_state", cursor.getString(cursor.getColumnIndex("result_state")));
                    m.put("location_position_name", cursor.getString(cursor.getColumnIndex("location_position_name")));
                    m.put("level", cursor.getString(cursor.getColumnIndex("level")));
                    m.put("location_name", cursor.getString(cursor.getColumnIndex("location_name")));
                    m.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                    m.put("my_team_fp", cursor.getString(cursor.getColumnIndex("my_team_fp")));
                    m.put("op_team_fp", cursor.getString(cursor.getColumnIndex("op_team_fp")));
                    m.put("results", cursor.getString(cursor.getColumnIndex("results")));

                    m.put("header", String.format(
                            Locale.ROOT,
                            "[%s | %s] %s",
                            m.get("event_timestamp"),
                            m.get("location_name"),
                            m.get("location_position_name")
                    ));

                    String sum_result_str = "";
                    if (m.get("results") == null || m.get("results").isEmpty() || m.get("number_of_moves").equals("1")) {
                        sum_result_str = m.get("sum_result");
                    } else {
                        sum_result_str = String.format(
                            "%s=%s",
                            m.get("sum_result"),
                            m.get("results")
                        );
                    }
                    m.put("info", String.format(
                            Locale.ROOT,
                            "%s | Опыт: %s | Результат: %s | Подходы: %s | Максимум: %s | Продолжительность: %s | Счёт: %s : %s",
                            gameHelper.getCompetitionStatus(Integer.parseInt(m.get("result_state"))),
                            m.get("exp"),
                            sum_result_str,
                            m.get("number_of_moves"),
                            m.get("max_result"),
                            getDuration(Integer.parseInt(m.get("duration"))),
                            m.get("my_team_fp"),
                            m.get("op_team_fp")
                    ));

                    last_trainings_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return last_trainings_data;
    }

    // ============ user_exercise_quests
    public long addUserExerciseQuest(int location_id, int position)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        ContentValues values = new ContentValues();
        values.put("location_id", location_id);
        values.put("position", position);
        values.put("user_id", user_id);
        values.put("exercise_id", exercise_id);

        return db.insert("user_exercise_quests", null, values);
    }

    public boolean checkUserExerciseQuest(int location_id, int position)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        boolean check_status = false;
        Cursor cursor = db.query(
                "user_exercise_quests",
                new String[]{"count(location_id) AS total_count"},
                "user_id = ? AND exercise_id = ? AND location_id = ? AND position = ?",
                new String[]{
                        Integer.toString(user_id),
                        Integer.toString(exercise_id),
                        Integer.toString(location_id),
                        Integer.toString(position)
                },
                null,
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getInt(cursor.getColumnIndex("total_count")) > 0) {
                    check_status = true;
                    Log.d(context.getResources().getString(R.string.log_tag), "DEBUG getUserExerciseQuest: check_status = true");
                };
            }
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "Cursor is null");

        return check_status;
    }

    public long getUserExerciseQuestsExp(int user_id, int exercise_id)
    {
        long total_exp = 0;
        Cursor cursor = db.query(
                "user_exercise_quests AS ueq LEFT JOIN location_positions AS lp ON ueq.location_id = lp.location_id AND ueq.position = lp.position",
                new String[]{"SUM(lp.quest_exp) AS total_exp"},
                "ueq.user_id = ? AND ueq.exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total_exp = cursor.getLong(cursor.getColumnIndex("total_exp"));
            }
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "Cursor is null");

        return total_exp;
    }

    public int getUserExerciseFinishedQuestsCount(int user_id, int exercise_id)
    {
        int quests_count = 0;
        Cursor cursor = db.query(
                "user_exercise_quests",
                new String[]{"count(location_id) AS quests_count"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                null,
                null,
                null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                quests_count = cursor.getInt(cursor.getColumnIndex("quests_count"));
            }
            cursor.close();
        } else
            Log.d(context.getResources().getString(R.string.log_tag), "Cursor is null");

        return quests_count;
    }

    public int getCurrentUserExerciseFinishedQuestsCount()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseFinishedQuestsCount(user_id, exercise_id);
    }

    // ============ Stat
    public ArrayList<Stat> getCurrentUserExerciseStatShadowMonthsSumResult()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatShadowMonths(user_id, exercise_id, "sum_result");
    }

    public ArrayList<Stat> getCurrentUserExerciseStatShadowMonthsSumExp()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatShadowMonths(user_id, exercise_id, "exp");
    }

    private ArrayList<Stat> getUserExerciseStatShadowMonths(int user_id, int exercise_id, String field)
    {
        ArrayList<Stat> stat_entities = new ArrayList<>();
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS value, strftime('%Y', event_timestamp) AS year, strftime('%m', event_timestamp) AS month"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id, year, month",
                null,
                "value DESC, year, month"
        );

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        boolean needCurrentStat = true;

        int position = 1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Stat stat_entity = new Stat();
                    stat_entity.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex("year"))));
                    stat_entity.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex("month"))));
                    stat_entity.setValue(cursor.getInt(cursor.getColumnIndex("value")));
                    stat_entity.setPosition(position);
                    stat_entity.setCurrentPeriod(false);
                    stat_entity.setDay(0);
                    position++;

                    if (stat_entity.getYear() == year && stat_entity.getMonth() == month) {
                        needCurrentStat = false;
                        stat_entity.setCurrentPeriod(true);
                    }
                    stat_entities.add(stat_entity);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (needCurrentStat) {
            stat_entities.add(new Stat(year, month, 0, 0, 0, position, true));
        }

        return stat_entities;
    }

    public ArrayList<Stat> getCurrentUserExerciseStatMonthsSumResult(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatMonths(user_id, exercise_id, "sum_result", year);
    }

    public ArrayList<Stat> getCurrentUserExerciseStatMonthsSumExp(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatMonths(user_id, exercise_id, "exp", year);
    }

    private ArrayList<Stat> getUserExerciseStatMonths(int user_id, int exercise_id, String field, int year)
    {
        ArrayList<Stat> stat_entities = new ArrayList<>();
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS value, strftime('%Y', event_timestamp) AS year, strftime('%m', event_timestamp) AS month"},
                "user_id = ? AND exercise_id = ? AND year = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(year)},
                "user_id, exercise_id, year, month",
                null,
                "month ASC"
        );

        Calendar c = Calendar.getInstance();
        int current_month = c.get(Calendar.MONTH) + 1;

        for (int month_idx = 0; month_idx < 12; month_idx++) {
            stat_entities.add(
                new Stat(
                    year,
                    month_idx + 1,
                    0,
                    0,
                    0,
                    month_idx + 1,
                    current_month == month_idx + 1
                )
            );
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int month = Integer.parseInt(cursor.getString(cursor.getColumnIndex("month")));
                    int value = cursor.getInt(cursor.getColumnIndex("value"));
                    stat_entities.get(month - 1).setValue(value);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return stat_entities;
    }

    public ArrayList<Stat> getCurrentUserExerciseStatYearsSumResult()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatYears(user_id, exercise_id, "sum_result");
    }

    public ArrayList<Stat> getCurrentUserExerciseStatYearsSumExp()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatYears(user_id, exercise_id, "exp");
    }

    private ArrayList<Stat> getUserExerciseStatYears(int user_id, int exercise_id, String field)
    {
        ArrayList<Stat> stat_entities = new ArrayList<>();
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS value, strftime('%Y', event_timestamp) AS year"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id, year",
                null,
                "year ASC"
        );

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        boolean needCurrentStat = true;

        int position = 1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Stat stat_entity = new Stat();
                    stat_entity.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex("year"))));
                    stat_entity.setValue(cursor.getInt(cursor.getColumnIndex("value")));
                    stat_entity.setPosition(position);
                    stat_entity.setCurrentPeriod(false);
                    stat_entity.setMonth(0);
                    stat_entity.setDay(0);
                    position++;

                    if (stat_entity.getYear() == year) {
                        needCurrentStat = false;
                    }
                    stat_entities.add(stat_entity);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (needCurrentStat) {
            stat_entities.add(new Stat(year, 0, 0, 0, 0, position, false));
        }

        return stat_entities;
    }

    public int getCurrentUserExerciseMaxYearSumResult()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxYearSum(user_id, exercise_id, "sum_result");
    }

    public int getCurrentUserExerciseMaxYearSumExp()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxYearSum(user_id, exercise_id, "exp");
    }

    // TODO: rewrite SQL query
    private int getUserExerciseMaxYearSum(int user_id, int exercise_id, String field)
    {
        int max_value= 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS sum_value, strftime('%Y', event_timestamp) AS year"},
                "user_id = ? AND exercise_id = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id)},
                "user_id, exercise_id, year",
                null,
                null
        );

        int sum_value;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    sum_value = cursor.getInt(cursor.getColumnIndex("sum_value"));
                    if (sum_value > max_value) {
                        max_value = sum_value;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return max_value;
    }

    public int getCurrentUserExerciseMaxMonthSumResult(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxMonthSum(user_id, exercise_id, "sum_result", year);
    }

    public int getCurrentUserExerciseMaxMonthSumExp(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxMonthSum(user_id, exercise_id, "exp", year);
    }

    // TODO: rewrite SQL query
    private int getUserExerciseMaxMonthSum(int user_id, int exercise_id, String field, int year)
    {
        int max_value= 0;
        String additionalSelectionString = "";
        String[] selections = new String[]{Integer.toString(user_id), Integer.toString(exercise_id)};
        if (year != 0) {
            additionalSelectionString = " AND year = ?";
            selections = new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(year)};
        }
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS sum_value, strftime('%Y', event_timestamp) AS year, strftime('%m', event_timestamp) AS month"},
                "user_id = ? AND exercise_id = ?" + additionalSelectionString,
                selections,
                "user_id, exercise_id, year, month",
                null,
                null
        );

        int sum_value;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    sum_value = cursor.getInt(cursor.getColumnIndex("sum_value"));
                    if (sum_value > max_value) {
                        max_value = sum_value;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return max_value;
    }


    public int getCurrentUserExerciseMaxWeekSumResult(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxWeekSumValue(user_id, exercise_id, "sum_result", year);
    }

    public int getCurrentUserExerciseMaxWeekSumExp(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxWeekSumValue(user_id, exercise_id, "exp", year);
    }

    // TODO: rewrite SQL query
    private int getUserExerciseMaxWeekSumValue(int user_id, int exercise_id, String field, int year)
    {
        int max_value= 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS sum_value, strftime('%Y', event_timestamp) AS year, strftime('%W', event_timestamp) AS week"},
                "user_id = ? AND exercise_id = ? AND year = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(year)},
                "user_id, exercise_id, year, week",
                null,
                null
        );

        int sum_value;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    sum_value = cursor.getInt(cursor.getColumnIndex("sum_value"));
                    if (sum_value > max_value) {
                        max_value = sum_value;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return max_value;
    }

    public ArrayList<Stat> getCurrentUserExerciseStatWeeksSumResult(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatWeeks(user_id, exercise_id, "sum_result", year);
    }

    public ArrayList<Stat> getCurrentUserExerciseStatWeeksSumExp(int year)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatWeeks(user_id, exercise_id, "exp", year);
    }

    private ArrayList<Stat> getUserExerciseStatWeeks(int user_id, int exercise_id, String field, int year)
    {
        ArrayList<Stat> stat_entities = new ArrayList<>();
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS value, strftime('%Y', event_timestamp) AS year, strftime('%W', event_timestamp) AS week"},
                "user_id = ? AND exercise_id = ? AND year = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(year)},
                "user_id, exercise_id, year, week",
                null,
                "week ASC"
        );

        Calendar today = Calendar.getInstance();
        today.setTimeZone( TimeZone.getTimeZone("Europe/Moscow"));
//        int current_week = today.get(Calendar.WEEK_OF_YEAR) - 1;

        Calendar that_day = Calendar.getInstance();
        that_day.setTimeZone( TimeZone.getTimeZone("Europe/Moscow"));
        that_day.set(year, Calendar.JANUARY, 1);
        int max_week_count = that_day.getMaximum(Calendar.WEEK_OF_YEAR);

        for (int week_idx = 0; week_idx < max_week_count; week_idx++) {
            boolean current_period = that_day.getTime().getTime() <= today.getTime().getTime() && that_day.getTime().getTime() + 7 * 24 * 3600 * 1000 > today.getTime().getTime();
            stat_entities.add(
                    new Stat(
                            year,
                            that_day.get(Calendar.MONTH) + 1,
                            that_day.get(Calendar.DAY_OF_MONTH),
                            week_idx + 1,
                            0,
                            week_idx + 1,
                            current_period
                    )
            );
            that_day.add(Calendar.DAY_OF_MONTH, 7);
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int week = Integer.parseInt(cursor.getString(cursor.getColumnIndex("week"))) - 1;
                    int value = cursor.getInt(cursor.getColumnIndex("value"));
                    stat_entities.get(week).setValue(value);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return stat_entities;
    }


    public int getCurrentUserExerciseMaxDaySumResult(int year, int month)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxDaySumValue(user_id, exercise_id, "sum_result", year, month);
    }

    public int getCurrentUserExerciseMaxDaySumExp(int year, int month)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseMaxDaySumValue(user_id, exercise_id, "exp", year, month);
    }

    // TODO: rewrite SQL query
    private int getUserExerciseMaxDaySumValue(int user_id, int exercise_id, String field, int year, int month)
    {
        int max_value= 0;
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS sum_value, strftime('%Y', event_timestamp) AS year, strftime('%m', event_timestamp) AS month, strftime('%d', event_timestamp) AS day"},
                "user_id = ? AND exercise_id = ? AND year = ? AND month = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(year), String.format(Locale.ROOT, "%02d", month)},
                "user_id, exercise_id, year, month, day",
                null,
                null
        );

        int sum_value;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    sum_value = cursor.getInt(cursor.getColumnIndex("sum_value"));
                    if (sum_value > max_value) {
                        max_value = sum_value;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return max_value;
    }

    public ArrayList<Stat> getCurrentUserExerciseStatDaysSumResult(int year, int month)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatDays(user_id, exercise_id, "sum_result", year, month);
    }

    public ArrayList<Stat> getCurrentUserExerciseStatDaysSumExp(int year, int month)
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();
        return getUserExerciseStatDays(user_id, exercise_id, "exp", year, month);
    }

    private ArrayList<Stat> getUserExerciseStatDays(int user_id, int exercise_id, String field, int year, int month)
    {
        ArrayList<Stat> stat_entities = new ArrayList<>();
        Cursor cursor = db.query(
                "user_exercise_trainings",
                new String[]{"SUM(" + field + ") AS value, strftime('%Y', event_timestamp) AS year, strftime('%m', event_timestamp) AS month, strftime('%d', event_timestamp) AS day, strftime('%w', event_timestamp) AS day_of_week"},
                "user_id = ? AND exercise_id = ? AND year = ? AND month = ?",
                new String[]{Integer.toString(user_id), Integer.toString(exercise_id), Integer.toString(year), String.format(Locale.ROOT, "%02d", month)},
                "user_id, exercise_id, year, month, day",
                null,
                "day ASC"
        );

        Calendar today = Calendar.getInstance();
        int current_year = today.get(Calendar.YEAR);
        int current_month = today.get(Calendar.MONTH) + 1;
        int current_day = today.get(Calendar.DAY_OF_MONTH);

        Calendar that_day = Calendar.getInstance();
        that_day.set(year, month - 1, 1);
        int days_in_month = that_day.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day_idx = 0; day_idx < days_in_month; day_idx++) {
            stat_entities.add(
                    new Stat(
                            year,
                            month,
                            day_idx + 1,
                            0,
                            0,
                            day_idx + 1,
                            current_day == day_idx + 1 && current_year == year && current_month == month
                    )
            );
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int day = Integer.parseInt(cursor.getString(cursor.getColumnIndex("day")));
                    int value = cursor.getInt(cursor.getColumnIndex("value"));

                    stat_entities.get(day - 1).setValue(value);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return stat_entities;
    }


    // ============ Other

    private float getAbstractFloatLevel(long exp, String table_name, float default_result_level)
    {
        float result_level = default_result_level;
        Cursor c = db.query(table_name, new String[] { "target_level", "diff_exp", "min_exp" }, "min_exp > ?", new String[] {Long.toString(exp)}, null, null, "target_level");
        if (c != null) {
            if (c.moveToFirst()) {
                long min_exp = c.getInt(c.getColumnIndex("min_exp"));
                float diff_exp = c.getFloat(c.getColumnIndex("diff_exp"));
                int level = c.getInt(c.getColumnIndex("target_level"));
                result_level = level + (exp - min_exp)/diff_exp;
            }
            c.close();
        }

        return result_level;
    }

    public float getFloatPlayerLevel(long exp)
    {
        return getAbstractFloatLevel(exp, "player_exp", context.getResources().getInteger(R.integer.max_player_level));
    }

    public long getUserExerciseTrainingsTotalExp(int user_id, int exercise_id)
    {
        Cursor c = db.query("user_exercise_trainings", new String[] { "SUM(exp) as total_exp" }, "user_id = ? AND exercise_id = ?", new String[] {Integer.toString(user_id), Integer.toString(exercise_id)}, null, null, null);
        long total_exp = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                total_exp = c.getLong(c.getColumnIndex("total_exp"));
            }
            c.close();
        }

        return total_exp;
    }

    public long getUserExerciseExp(int user_id, int exercise_id)
    {
        return getUserExerciseTrainingsTotalExp(user_id, exercise_id) + getUserExerciseQuestsExp(user_id, exercise_id);
    }

    public void updateUserInfo()
    {
        int user_id = gameHelper.getUserId();
        long total_exp = getUserExerciseExp(gameHelper.getUserId(), gameHelper.getExerciseId());
        float user_level = getFloatPlayerLevel(total_exp);

        Log.d(context.getResources().getString(R.string.log_tag), "DEBUG updateUserInfo.user_level: " + user_level);
        //int int_user_level = (int) user_level;
        //int user_fitness_points = Integer.parseInt(getLevelData(int_user_level).get("base_fp"));
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(
                context.getResources().getString(R.string.preference_name_user_encoded_level),
                (int) Math.floor(user_level * 10000)
        );
        editor.putLong(context.getResources().getString(
                R.string.preference_name_user_exp),
                total_exp
        );
        editor.putString(
                context.getResources().getString(R.string.preference_name_user_name),
                getUserNameById(user_id)
        );
        editor.putInt(
                context.getResources().getString(R.string.preference_name_user_encoded_level),
                (int) Math.floor(user_level * 10000)
        );
        editor.commit();
    }

    public Map<String, String> getLevelData(int user_level)
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put("target_level", Integer.toString(user_level));
        Cursor cursor = db.query("player_exp", new String[] { "base_fp", "base_resistance", "base_multiplier", "base_bonus_chance", "base_bonus_multiplier" }, "target_level = ?", new String[] {Long.toString(user_level)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                data.put("base_fp", Integer.toString(cursor.getInt(cursor.getColumnIndex("base_fp"))));
                data.put("base_resistance", Integer.toString(cursor.getInt(cursor.getColumnIndex("base_resistance"))));
                data.put("base_multiplier", Float.toString(cursor.getFloat(cursor.getColumnIndex("base_multiplier"))));
                data.put("base_bonus_chance", Float.toString(cursor.getFloat(cursor.getColumnIndex("base_bonus_chance"))));
                data.put("base_bonus_multiplier", Float.toString(cursor.getFloat(cursor.getColumnIndex("base_bonus_multiplier"))));
            }
            cursor.close();
        }

        return data;
    }

    public ArrayList<Map<String, String>> getLevelsData()
    {
        ArrayList<Map<String, String>> player_exp_data = new ArrayList<Map<String, String>>();
        Map<String, String> m;
        Cursor cursor = db.query("player_exp", new String[] { "target_level", "diff_exp", "min_exp", "base_fp", "base_resistance", "base_multiplier", "base_bonus_chance", "base_bonus_multiplier" }, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                do {
                    m = new HashMap<>();
                    m.put("target_level", Integer.toString(cursor.getInt(cursor.getColumnIndex("target_level"))));
                    m.put("min_exp", Long.toString(cursor.getLong(cursor.getColumnIndex("min_exp"))));
                    m.put("diff_exp", Long.toString(cursor.getLong(cursor.getColumnIndex("diff_exp"))));
                    m.put("base_fp", Integer.toString(cursor.getInt(cursor.getColumnIndex("base_fp"))));
                    m.put("base_resistance", Integer.toString(cursor.getInt(cursor.getColumnIndex("base_resistance"))));
                    m.put("base_multiplier", Float.toString(cursor.getFloat(cursor.getColumnIndex("base_multiplier"))));
                    m.put("base_bonus_chance", Float.toString(cursor.getFloat(cursor.getColumnIndex("base_bonus_chance"))));
                    m.put("base_bonus_multiplier", Float.toString(cursor.getFloat(cursor.getColumnIndex("base_bonus_multiplier"))));
                    player_exp_data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return player_exp_data;
    }

    public void updateUserInfoWithLevelCheck()
    {
        int user_level_old = gameHelper.getCachedUserLevel();
        updateUserInfo();
        int user_level_new = gameHelper.getCachedUserLevel();
        if (user_level_new > user_level_old) {
            String msg = user_level_new == 14 ?
                    context.getResources().getString(R.string.msg_level_up_congratulations_specialisation)
                    : context.getResources().getString(R.string.msg_level_up_congratulations);
            Toast.makeText(
                    context,
                    String.format(msg, user_level_new),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public PlayerEntity getCurrentPlayerEntity()
    {
        int user_id = gameHelper.getUserId();
        int exercise_id = gameHelper.getExerciseId();

        return getPlayerEntity(user_id, exercise_id);
    }

    public PlayerEntity getPlayerEntity(int user_id, int exercise_id)
    {
        Map<String, String> user_exercise_data = getUserExerciseData(user_id, exercise_id);
        PlayerEntity player_entity = new PlayerEntity(context, user_id, exercise_id);
        player_entity.setName(getUserNameById(user_id))
                .setSpecialisationId(Integer.parseInt(user_exercise_data.get("specialisation")))
                .setLevel(getUserLevel(user_id, exercise_id))
                .setInitialFitnessPoints(getUserFitnessPoints(user_id, exercise_id))
                .setCurrentFitnessPoints(getUserFitnessPoints(user_id, exercise_id))
                .setMultiplier(getUserMultiplier(user_id, exercise_id))
                .setResistance(getUserResistance(user_id, exercise_id))
                .setBonusChance(getUserBonusChance(user_id, exercise_id))
                .setBonusMultiplier(getUserBonusMultiplier(user_id, exercise_id))
                .setInitialActionPoints(getUserExerciseTrainingMaxCompetitionResult())
                .setCurrentActionPoints(0);

        return player_entity;
    }

    public Map<String, String> getParameters()
    {
        Map<String, String> result_data = new HashMap<String, String>();
        try {
            Cursor cursor = db.query("parameters", new String[]{"app_version"}, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    result_data.put("app_version", cursor.getString(cursor.getColumnIndex("app_version")));
                }
                cursor.close();
            }
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d(context.getResources().getString(R.string.log_tag), "GetParameters exception: " + e.getMessage() + " " + e.toString());
        }

        return result_data;
    }

    public String getAppVersion()
    {
        Map<String, String> parameters = getParameters();
        String app_version = null;
        if (parameters.containsKey("app_version")) {
            app_version = parameters.get("app_version");
        }

        return app_version;
    }

    public String getAppVersionRevision()
    {
        return context.getString(R.string.app_version_revision);
    }

    public long updateAppVersion(String app_version)
    {
        ContentValues values = new ContentValues();
        values.put("app_version", app_version);
        return db.update("parameters", values, null, null);
    }
}