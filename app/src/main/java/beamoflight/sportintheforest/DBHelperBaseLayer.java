package beamoflight.sportintheforest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by beamoflight on 30.05.17.
 */
class DBHelperBaseLayer extends SQLiteOpenHelper {
    protected SQLiteDatabase db;
    protected Context context;
    protected int formatVersion = 3;

    public DBHelperBaseLayer(Context current) {
        // конструктор суперкласса
        super (current, "SportInTheForestDB", null, 5);
        context = current;
        db = getWritableDatabase();
    }

    protected void createTablePlayerExp()
    {
        db.execSQL("DROP TABLE IF EXISTS player_exp;");
        db.execSQL("CREATE TABLE IF NOT EXISTS player_exp ("
                + "target_level integer primary key autoincrement,"
                + "min_exp bigint,"
                + "diff_exp bigint,"
                + "base_fp int,"
                + "base_resistance int,"
                + "base_multiplier float,"
                + "base_bonus_chance float,"
                + "base_bonus_multiplier float"
                + ");");

        String base_sql = "INSERT INTO player_exp (target_level, min_exp, diff_exp, base_fp, base_resistance, base_multiplier, base_bonus_chance, base_bonus_multiplier) VALUES";
        String sql = "";
        int current_level, base_fp, base_resistance;
        long min_exp, diff_exp;
        float base_multiplier, base_bonus_chance, base_bonus_multiplier;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.player_exp_data);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("level")) {
                            current_level = Integer.parseInt(xpp.getAttributeValue(0));
                            diff_exp = Long.parseLong(xpp.getAttributeValue(1));
                            min_exp = Long.parseLong(xpp.getAttributeValue(2));
                            base_fp = Integer.parseInt(xpp.getAttributeValue(3));
                            base_resistance = Integer.parseInt(xpp.getAttributeValue(4));
                            base_multiplier = Float.parseFloat(xpp.getAttributeValue(5));
                            base_bonus_chance = Float.parseFloat(xpp.getAttributeValue(6));
                            base_bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(7));
                            sql += ", ("+ current_level +", " + min_exp + ", " + diff_exp + ", " + base_fp
                                    + ", " + base_resistance + ", " + base_multiplier + ", " + base_bonus_chance + ", " + base_bonus_multiplier +")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableUsers()
    {
        // создаем таблицу users
        db.execSQL("DROP TABLE IF EXISTS users;");
        db.execSQL("CREATE TABLE IF NOT EXISTS users ("
                + "user_id integer primary key autoincrement,"
                + "creation_date date,"
                + "modification_date date,"
                + "name text" + ");");
    }

    protected void createTableUserExercises() {
        // создаем таблицу user_exercises
        db.execSQL("DROP TABLE IF EXISTS user_exercises;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercises ("
                + "user_id integer,"
                + "exercise_id integer,"
                + "wins integer,"
                + "competitions integer,"
                + "draws integer,"
                + "specialisation integer,"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createTableParameters() {
        // создаем таблицу user_exercises
        db.execSQL("DROP TABLE IF EXISTS parameters;");
        db.execSQL("CREATE TABLE IF NOT EXISTS parameters ("
                + "id integer,"
                + "app_version text" + ");");
        db.execSQL("INSERT INTO parameters (id, app_version) VALUES (\"1\", \"0.0.0\");");
    }

    protected void createTableExercises()
    {
        // создаем таблицу exercises
        db.execSQL("DROP TABLE IF EXISTS exercises;");
        db.execSQL("CREATE TABLE IF NOT EXISTS exercises ("
                + "exercise_id integer primary key autoincrement,"
                + "modification_date date,"
                + "initial_name text,"
                + "name text" + ");");

        String base_sql = "INSERT INTO exercises (exercise_id, modification_date, initial_name, name) VALUES";
        String sql = "";
        int id;
        String name;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.exercises);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("exercise")) {
                            id = Integer.parseInt(xpp.getAttributeValue(0));
                            name = xpp.getAttributeValue(1);
                            sql += ", (" + id + ", date('now'), \"" + name +"\", \"" + name +"\")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableLocationPositions()
    {
        // создаем таблицу locations
        db.execSQL("DROP TABLE IF EXISTS location_positions;");
        db.execSQL("CREATE TABLE IF NOT EXISTS location_positions ("
                + "location_id integer,"
                + "position integer,"
                + "level integer,"
                + "npc_id integer,"
                + "FOREIGN KEY(location_id) REFERENCES locations(location_id)"
                + "FOREIGN KEY(npc_id) REFERENCES non_player_characters(npc_id)"
                + ");");

        String base_sql = "INSERT INTO location_positions (location_id, position, level, npc_id) VALUES";
        String sql = "";
        int location_id, position, level, npc_id;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.location_positions);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("location_position")) {
                            location_id = Integer.parseInt(xpp.getAttributeValue(0));
                            position = Integer.parseInt(xpp.getAttributeValue(1));
                            level = Integer.parseInt(xpp.getAttributeValue(2));
                            npc_id = Integer.parseInt(xpp.getAttributeValue(3));
                            sql += ", (" + location_id + ", " + position + ", " + level + ", " + npc_id + ")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableLocations()
    {
        // создаем таблицу locations
        db.execSQL("DROP TABLE IF EXISTS locations;");
        db.execSQL("CREATE TABLE IF NOT EXISTS locations ("
                + "location_id integer primary key autoincrement,"
                + "name text" + ");");

        String base_sql = "INSERT INTO locations (location_id, name) VALUES";
        String sql = "";
        int id;
        String name;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.locations);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("location")) {
                            id = Integer.parseInt(xpp.getAttributeValue(0));
                            name = xpp.getAttributeValue(1);
                            sql += ", ("+ id +", \"" + name +"\")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableUserExerciseLocations() {
        // создаем таблицу user_exercise_locations
        db.execSQL("DROP TABLE IF EXISTS user_exercise_locations;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_locations ("
                + "location_id integer,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "npc_1_level integer,"
                + "npc_2_level integer,"
                + "npc_3_level integer,"
                + "npc_4_level integer,"
                + "npc_5_level integer,"
                + "FOREIGN KEY(location_id) REFERENCES locations(location_id)"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createTableNonPlayerCharacters()
    {
        // создаем таблицу non_player_characters
        db.execSQL("DROP TABLE IF EXISTS non_player_characters;");
        db.execSQL("CREATE TABLE IF NOT EXISTS non_player_characters ("
                + "npc_id integer primary key autoincrement,"
                + "type text,"
                + "level integer,"
                + "fp integer,"
                + "max_res integer,"
                + "multiplier float,"
                + "exp integer,"
                + "resistance integer,"
                + "quest_cnt integer,"
                + "quest_exp integer,"
                + "bonus_chance float,"
                + "bonus_multiplier float,"
                + "name text,"
                + "team text" + ");");

        String base_sql = "INSERT INTO non_player_characters (npc_id, type, level, fp, max_res, multiplier, exp, resistance, quest_cnt, quest_exp, bonus_chance, bonus_multiplier, name, team) VALUES";
        String sql = "";
        int id, level, fp, max_res, resistance, quest_cnt;
        long exp, quest_exp;
        float multiplier, bonus_chance, bonus_multiplier;
        String type, name, team;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.non_player_characters);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("npc")) {
                            id = Integer.parseInt(xpp.getAttributeValue(0));
                            type = xpp.getAttributeValue(1);
                            level = Integer.parseInt(xpp.getAttributeValue(2));
                            fp = Integer.parseInt(xpp.getAttributeValue(3));
                            max_res = Integer.parseInt(xpp.getAttributeValue(4));
                            multiplier = Float.parseFloat(xpp.getAttributeValue(5));
                            exp = Long.parseLong(xpp.getAttributeValue(6));
                            resistance = Integer.parseInt(xpp.getAttributeValue(7));
                            quest_cnt = Integer.parseInt(xpp.getAttributeValue(8));
                            quest_exp = Long.parseLong(xpp.getAttributeValue(9));
                            bonus_chance = Float.parseFloat(xpp.getAttributeValue(10));
                            bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(11));
                            name = xpp.getAttributeValue(12);
                            team = xpp.getAttributeValue(13);
                            sql += ", ("+ id + ", \"" + type + "\", " + level + ", " + fp + ", " + max_res + ", "
                                    + multiplier + ", " + exp + ", " + resistance + ", " + quest_cnt + ", " + quest_exp + ", " + bonus_chance + ", "
                                    + bonus_multiplier + ", \"" + name +"\", \"" + team +"\")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableSkills()
    {
        // создаем таблицу skills
        db.execSQL("DROP TABLE IF EXISTS skills;");
        db.execSQL("CREATE TABLE IF NOT EXISTS skills ("
                + "skill_id integer primary key autoincrement,"
                + "skill_group_id integer,"
                + "skill_level integer,"
                + "required_level integer,"
                + "skill_points integer,"
                + "extra_fitness_points integer,"
                + "extra_resistance integer,"
                + "extra_multiplier float,"
                + "extra_bonus_chance float,"
                + "extra_bonus_multiplier float,"
                + "extra_fitness_points_ratio1 float,"
                + "extra_fitness_points_ratio2 float,"
                + "extra_resistance_ratio1 float,"
                + "extra_resistance_ratio2 float,"
                + "extra_multiplier_ratio1 float,"
                + "extra_multiplier_ratio2 float,"
                + "extra_bonus_chance_ratio1 float,"
                + "extra_bonus_chance_ratio2 float,"
                + "extra_bonus_multiplier_ratio1 float,"
                + "extra_bonus_multiplier_ratio2 float,"
                + "extra_regeneration_base integer,"
                + "extra_regeneration_ratio float,"
                + "info text,"
                + "label text,"
                + "duration integer,"
                + "reuse integer,"
                + "specialisation smallint,"
                + "splash_multiplier float"
                + ");");

        String base_sql = "INSERT INTO skills (skill_id, skill_group_id, skill_level, required_level, skill_points, extra_fitness_points, extra_resistance, extra_multiplier, extra_bonus_chance, extra_bonus_multiplier, info, label, duration, reuse, specialisation, extra_fitness_points_ratio1, extra_fitness_points_ratio2, extra_resistance_ratio1, extra_resistance_ratio2, extra_multiplier_ratio1, extra_multiplier_ratio2, extra_bonus_chance_ratio1, extra_bonus_chance_ratio2, extra_bonus_multiplier_ratio1, extra_bonus_multiplier_ratio2, extra_regeneration_base, extra_regeneration_ratio, splash_multiplier) VALUES";
        String sql = "";
        int skill_id, skill_group_id, skill_level, required_level, skill_points;
        int duration, extra_fitness_points, extra_resistance, reuse, specialisation;
        float extra_multiplier, extra_bonus_chance, extra_bonus_multiplier;
        float extra_fitness_points_ratio1, extra_fitness_points_ratio2, extra_resistance_ratio1, extra_resistance_ratio2;
        float extra_multiplier_ratio1, extra_multiplier_ratio2, extra_bonus_chance_ratio1, extra_bonus_chance_ratio2;
        float extra_bonus_multiplier_ratio1, extra_bonus_multiplier_ratio2;
        int extra_regeneration_base;
        float extra_regeneration_ratio;
        float splash_multiplier;

        String info, label;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.skills);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("skill")) {
                            skill_id = Integer.parseInt(xpp.getAttributeValue(0));
                            skill_group_id = Integer.parseInt(xpp.getAttributeValue(1));
                            skill_level = Integer.parseInt(xpp.getAttributeValue(2));
                            required_level = Integer.parseInt(xpp.getAttributeValue(3));
                            skill_points = Integer.parseInt(xpp.getAttributeValue(4));
                            extra_fitness_points = Integer.parseInt(xpp.getAttributeValue(5));
                            extra_resistance = Integer.parseInt(xpp.getAttributeValue(6));
                            extra_multiplier = Float.parseFloat(xpp.getAttributeValue(7));
                            extra_bonus_chance = Float.parseFloat(xpp.getAttributeValue(8));
                            extra_bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(9));
                            info = xpp.getAttributeValue(10);
                            label = xpp.getAttributeValue(11);
                            duration = Integer.parseInt(xpp.getAttributeValue(12));
                            reuse = Integer.parseInt(xpp.getAttributeValue(13));
                            specialisation = Integer.parseInt(xpp.getAttributeValue(14));
                            extra_fitness_points_ratio1 = Float.parseFloat(xpp.getAttributeValue(15));
                            extra_fitness_points_ratio2 = Float.parseFloat(xpp.getAttributeValue(16));
                            extra_resistance_ratio1 = Float.parseFloat(xpp.getAttributeValue(17));
                            extra_resistance_ratio2 = Float.parseFloat(xpp.getAttributeValue(18));
                            extra_multiplier_ratio1 = Float.parseFloat(xpp.getAttributeValue(19));
                            extra_multiplier_ratio2 = Float.parseFloat(xpp.getAttributeValue(20));
                            extra_bonus_chance_ratio1 = Float.parseFloat(xpp.getAttributeValue(21));
                            extra_bonus_chance_ratio2 = Float.parseFloat(xpp.getAttributeValue(22));
                            extra_bonus_multiplier_ratio1 = Float.parseFloat(xpp.getAttributeValue(23));
                            extra_bonus_multiplier_ratio2 = Float.parseFloat(xpp.getAttributeValue(24));
                            extra_regeneration_base = Integer.parseInt(xpp.getAttributeValue(25));
                            extra_regeneration_ratio = Float.parseFloat(xpp.getAttributeValue(26));
                            splash_multiplier = Float.parseFloat(xpp.getAttributeValue(27));

                            sql += ", (" + skill_id + ", " + skill_group_id + ", " + skill_level + ", " + required_level + ", " + skill_points
                                    + ", " + extra_fitness_points + ", " + extra_resistance + ", " + extra_multiplier + ", " + extra_bonus_chance
                                    + ", " + extra_bonus_multiplier + ", \"" + info + "\", \"" + label + "\", " + duration + ", " + reuse
                                    + ", " + specialisation
                                    + ", " + extra_fitness_points_ratio1 + ", " + extra_fitness_points_ratio2
                                    + ", " + extra_resistance_ratio1 + ", " + extra_resistance_ratio2
                                    + ", " + extra_multiplier_ratio1 + ", " + extra_multiplier_ratio2
                                    + ", " + extra_bonus_chance_ratio1 + ", " + extra_bonus_chance_ratio2
                                    + ", " + extra_bonus_multiplier_ratio1 + ", " + extra_bonus_multiplier_ratio2
                                    + ", " + extra_regeneration_base + ", " + extra_regeneration_ratio
                                    + ", " + splash_multiplier
                                    + ")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableAchievements()
    {
        // создаем таблицу achievements
        db.execSQL("DROP TABLE IF EXISTS achievements;");
        db.execSQL("CREATE TABLE IF NOT EXISTS achievements ("
                + "achievement_id integer primary key autoincrement,"
                + "required_parameter_name text,"
                + "required_parameter_values text,"
                + "skill_points_values text,"
                + "name text"
                + ");");


        String base_sql = "INSERT INTO achievements (achievement_id, required_parameter_name, required_parameter_values, skill_points_values, name) VALUES";
        String sql = "";
        int achievement_id;
        String required_parameter_name, required_parameter_values, skill_points_values, name;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.achievements);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("achievement")) {
                            achievement_id = Integer.parseInt(xpp.getAttributeValue(0));
                            required_parameter_name = xpp.getAttributeValue(1);
                            required_parameter_values = xpp.getAttributeValue(2);
                            skill_points_values = xpp.getAttributeValue(3);
                            name = xpp.getAttributeValue(4);

                            sql += ", (" + achievement_id + ", \"" + required_parameter_name + "\", \"" + required_parameter_values + "\", \"" + skill_points_values + "\", \"" + name + "\")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableSkillGroups()
    {
        // создаем таблицу skill_groups
        db.execSQL("DROP TABLE IF EXISTS skill_groups;");
        db.execSQL("CREATE TABLE IF NOT EXISTS skill_groups ("
                + "skill_group_id integer primary key autoincrement,"
                + "name text,"
                + "type smallint,"
                + "target_type smallint"
                + ");");

        String base_sql = "INSERT INTO skill_groups (skill_group_id, name, type, target_type) VALUES";
        String sql = "";
        int skill_group_id, type, target_type;
        String name;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.skill_groups);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("skill_group")) {
                            skill_group_id = Integer.parseInt(xpp.getAttributeValue(0));
                            name = xpp.getAttributeValue(1);
                            type = Integer.parseInt(xpp.getAttributeValue(2));
                            target_type = Integer.parseInt(xpp.getAttributeValue(3));

                            sql += ", (" + skill_group_id
                                    + ", \"" + name
                                    + "\"," + type
                                    + ", " + target_type
                                    + ")";
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sql += ";";
        db.execSQL(base_sql + sql.substring(1));
    }

    protected void createTableUserExerciseSkills() {
        // создаем таблицу user_exercise_skills
        db.execSQL("DROP TABLE IF EXISTS user_exercise_skills;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_skills ("
                + "skill_id integer,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "FOREIGN KEY(skill_id) REFERENCES skills(skill_id)"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createTableUserExerciseQuests() {
        // создаем таблицу user_exercise_quests
        db.execSQL("DROP TABLE IF EXISTS user_exercise_quests;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_quests ("
                + "npc_location_id smallint,"
                + "npc_position smallint,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "FOREIGN KEY(npc_location_id) REFERENCES locations(location_id)"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createUserExerciseTrainingsTable()
    {
        // создаем таблицу trainings
        db.execSQL("DROP TABLE IF EXISTS user_exercise_trainings;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_trainings ("
                + "training_id integer primary key autoincrement,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "npc_id integer,"
                + "npc_location_id smallint,"
                + "npc_position smallint,"
                + "event_timestamp DATETIME,"
                + "sum_result smallint,"
                + "max_result smallint,"
                + "number_of_moves smallint,"
                + "duration smallint,"
                + "my_team_fp integer,"
                + "op_team_fp integer,"
                + "exp integer,"
                + "result_state smallint,"
                + "quest_owner boolean,"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + "FOREIGN KEY(npc_id) REFERENCES non_player_characters(npc_id)"
                + ");");
    }

    public void recreateCommonTable()
    {
        createTableParameters();
        createTablePlayerExp();
        createTableLocations();
        createTableNonPlayerCharacters();
        createTableLocationPositions();
        createTableSkills();
        createTableSkillGroups();
        createTableAchievements();
    }

    //importing database
    public void importDB(String backup_filename, boolean toastOn) {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                //String backupDBPath = "/SportInTheForest/SportInTheForestDB_" + gameHelper.getTodayString();
                String currentDBPath = context.getDatabasePath("SportInTheForestDB").toString();
                String baseBackupDBPath = "/SportInTheForest/";
                String backupDBPath = baseBackupDBPath + backup_filename;

                File backupDB = new File(currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: importDB SUCCESS" + backupDB.toString());
                if (toastOn) Toast.makeText(context, backupDB.toString(), Toast.LENGTH_LONG).show();
            } else {
                Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: Нет прав");
                if (toastOn) Toast.makeText(context, "Нет прав", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: importDB FAIL " + e.toString());
            if (toastOn) Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    //exporting database
    public void exportDB(String backup_filename, boolean toastOn) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = context.getDatabasePath("SportInTheForestDB").toString();
                //String backupDBPath = "/SportInTheForest/SportInTheForestDB_" + gameHelper.getTodayString();
                String baseBackupDBPath = "/SportInTheForest/";
                String backupDBPath = baseBackupDBPath + backup_filename;


                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                Log.d(context.getResources().getString(R.string.log_tag), "currentDBPath: " + currentDBPath);
                Log.d(context.getResources().getString(R.string.log_tag), "backupDBPath: " + backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                if (toastOn) Toast.makeText(context, backupDB.toString(), Toast.LENGTH_LONG).show();
                Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: exportDB SUCCESS" + backupDB.toString());
            } else {
                if (toastOn) Toast.makeText(context, "Нет прав", Toast.LENGTH_LONG).show();
                Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: Нет прав");
            }
        } catch (Exception e) {
            if (toastOn) Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: exportDB FAIL " + e.toString());
        }
    }

    public void customOnCreate()
    {
        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onCreate database ---");
        this.db = db;

        createTableExercises();
        createUserExerciseTrainingsTable();
        createTableUsers();
        createTableUserExercises();
        createTableUserExerciseLocations();
        createTableUserExerciseSkills();
        createTableUserExerciseQuests();

        recreateCommonTable();

        // TODO: remove after debug
        //db.execSQL("INSERT INTO users (user_id, creation_date, modification_date, name) VALUES (1, \"1994-03-06\", \"1994-03-06\",\"test\");");
        //db.execSQL("INSERT INTO user_exercise_locations (location_id, user_id, exercise_id) SELECT 1 AS location_id, 1 as user_id, exercise_id FROM user_exercises");
        //db.execSQL("INSERT INTO user_exercise_locations (location_id, user_id, exercise_id) VALUES (1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onUpgrade database ---");
        this.db = db;

        recreateCommonTable();
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onDowngrade database ---");
        this.db = db;

        recreateCommonTable();
    }

    public static String implode(String separator, String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1].trim());
        return sb.toString();
    }

    public void setTableData(String table_name, ArrayList<Map<String, String>> data, int old_format_version)
    {
        String[] fields = getFieldsByTableName(table_name, old_format_version);
        String base_sql = "INSERT INTO " + table_name + " (" + implode(", ", fields) + ") VALUES";
        String sql = "";
        int cnt = 0;
        for (Map<String, String> row : data) {
            cnt++;
            sql += ", (";
            for (int i = 0; i < fields.length; i++) {
                sql += "\"" + row.get(fields[i]) + "\", ";
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ")";
        }
        sql += ";";
        if (cnt > 0) {
            db.beginTransaction();
            db.execSQL("DELETE FROM " + table_name + ";");
            db.execSQL(base_sql + sql.substring(1));
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public ArrayList<Map<String, String>> getTableData(String table_name)
    {
        String[] fields = getFieldsByTableName(table_name, formatVersion);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> m;
        Cursor cursor = db.query(table_name, fields, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    for (int i = 0; i < fields.length; i++) {
                        m.put(fields[i], cursor.getString(cursor.getColumnIndex(fields[i])));
                    }
                    data.add(m);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return data;
    }

    private String[] getFieldsByTableName(String table_name, int format_version)
    {
        Map<String, String[]> m = new HashMap<>();
        m.put("users", new String[] {"user_id", "creation_date", "modification_date", "name"});
        m.put("user_exercises", new String[] {"user_id", "exercise_id", "wins", "competitions", "draws", "specialisation"});
        m.put("exercises", new String[] {"exercise_id", "modification_date", "initial_name", "name"});
        m.put("user_exercise_locations", new String[] {"location_id", "user_id", "exercise_id", "npc_1_level", "npc_2_level", "npc_3_level", "npc_4_level", "npc_5_level"});
        m.put("user_exercise_skills", new String[] {"skill_id", "user_id", "exercise_id"});
        m.put("user_exercise_quests", new String[] {"npc_location_id", "npc_position", "user_id", "exercise_id"});
        m.put("parameters", new String[] {"app_version"});
        if (format_version >= 2) {
            m.put("user_exercise_trainings", new String[] {"training_id", "user_id", "exercise_id", "npc_id", "npc_location_id", "npc_position", "event_timestamp", "event_timestamp", "sum_result", "max_result", "number_of_moves", "duration", "exp", "result_state", "quest_owner", "my_team_fp", "op_team_fp"});
        } else if (format_version == 1) {
            m.put("user_exercise_trainings", new String[] {"training_id", "user_id", "exercise_id", "npc_id", "npc_location_id", "npc_position", "event_timestamp", "event_timestamp", "sum_result", "max_result", "number_of_moves", "duration", "exp", "result_state", "quest_owner"});
        }

        return m.get(table_name);
    }

    class Record
    {
        @SerializedName("tables")
        @Expose
        Map<String, ArrayList<Map<String, String>>> tables;

        @SerializedName("format_version")
        @Expose
        int format_version;

        Record()
        {
            format_version = formatVersion;
            tables = new HashMap<>();
        }
    }

    public String[] getTables2Save(int format_version)
    {
        String[] data;
        if (format_version >= 3)
            data = new String[]{"users", "user_exercises", "exercises", "user_exercise_locations", "user_exercise_skills", "user_exercise_quests", "user_exercise_trainings", "parameters"};
        else {
            data = new String[]{"users", "user_exercises", "exercises", "user_exercise_locations", "user_exercise_skills", "user_exercise_quests", "user_exercise_trainings"};
        }

        return data;
    }

    public void save2file(String filename)
    {
        File sd = Environment.getExternalStorageDirectory();
        String baseBackupDBPath = "/SportInTheForest/" + filename;
        Record record = new Record();
        String tables[] = getTables2Save(formatVersion);
        for (int i = 0; i < tables.length; i++) {
            record.tables.put(tables[i], getTableData(tables[i]));
        }
        String json_string = new Gson().toJson(record);

        try {
            File fout = new File(sd, baseBackupDBPath);
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(json_string);
//        for (int i = 0; i < 10; i++) {
//            bw.write("something");
//            bw.newLine();
//        }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String filename, boolean toastOn)
    {
        String json_string = "";
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canRead()) {
                //String path = sd.getAbsolutePath();
                File file = new File(sd, "/SportInTheForest/" + filename);
                StringBuilder text = new StringBuilder();

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                json_string = text.toString();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Record record;
        if (json_string.length() > 0) {
            record = (new Gson()).fromJson(json_string, Record.class);
            String tables[] = getTables2Save(record.format_version);
            for (String table_name : tables) {
                setTableData(table_name, record.tables.get(table_name), record.format_version);
            }
        } else {
            Log.d("myLogs", "error");
            if (toastOn) Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}