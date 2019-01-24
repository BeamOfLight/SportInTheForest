package beamoflight.sportintheforest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
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
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by beamoflight on 30.05.17.
 */
class DBHelperBaseLayer extends SQLiteOpenHelper {
    protected SQLiteDatabase db;
    protected Context context;
    protected final static int formatVersion = 7;

    public DBHelperBaseLayer(Context current) {
        // конструктор суперкласса
        super (current, "SportInTheForestDB", null, formatVersion);
        context = current;
        db = getWritableDatabase();
    }

    protected String convertLong2Decimal(String value)
    {
        BigDecimal bd = new BigDecimal(value);
        return bd.toPlainString();
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
                + "base_bonus_multiplier float" + ");");

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
                            current_level = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                            diff_exp = Long.parseLong(convertLong2Decimal(xpp.getAttributeValue(null, "diff_exp")));
                            min_exp = Long.parseLong(convertLong2Decimal(xpp.getAttributeValue(null, "min_exp")));
                            base_fp = Integer.parseInt(xpp.getAttributeValue(null, "base_fp"));
                            base_resistance = Integer.parseInt(xpp.getAttributeValue(null, "base_resistance"));
                            base_multiplier = Float.parseFloat(xpp.getAttributeValue(null, "base_multiplier"));
                            base_bonus_chance = Float.parseFloat(xpp.getAttributeValue(null, "base_bonus_chance"));
                            base_bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(null, "base_bonus_multiplier"));
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
                + "for_replay integer DEFAULT(0),"
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
                + "type integer,"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id),"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createTableParameters() {
        // создаем таблицу parameters
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
                            id = Integer.parseInt(xpp.getAttributeValue(null, "exercise_id"));
                            name = xpp.getAttributeValue(null, "name");
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
                + "location_level_position_id integer primary key autoincrement,"
                + "name text,"
                + "location_id integer,"
                + "position integer,"
                + "level integer,"
                + "quest_cnt integer,"
                + "quest_exp integer,"
                + "info text,"
                + "FOREIGN KEY(location_id) REFERENCES locations(location_id)"
                + ");");

        String base_sql = "INSERT INTO location_positions (location_level_position_id, name, location_id, position, level, quest_cnt, quest_exp, info) VALUES";
        String sql = "";
        int location_level_position_id, location_id, position, level, quest_cnt, quest_exp;
        String name, info;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.location_positions);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("location_position")) {
                            location_level_position_id = Integer.parseInt(xpp.getAttributeValue(null, "location_level_position_id"));
                            name = xpp.getAttributeValue(null, "name");
                            location_id = Integer.parseInt(xpp.getAttributeValue(null, "location_id"));
                            position = Integer.parseInt(xpp.getAttributeValue(null, "position"));
                            level = Integer.parseInt(xpp.getAttributeValue(null, "level"));
                            quest_cnt = Integer.parseInt(xpp.getAttributeValue(null, "quest_cnt"));
                            quest_exp = Integer.parseInt(xpp.getAttributeValue(null, "quest_exp"));
                            info = xpp.getAttributeValue(null, "info");
                            sql += ", (" + location_level_position_id + ", \"" + name + "\", " + location_id + ", " + position + ", " + level + ", " + quest_cnt + ", " + quest_exp + ", \"" + info + "\")";
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

    protected void createTableNpcInLocationPositions()
    {
        // создаем таблицу locations
        db.execSQL("DROP TABLE IF EXISTS npc_in_location_positions;");
        db.execSQL("CREATE TABLE IF NOT EXISTS npc_in_location_positions ("
                + "location_level_position_id integer,"
                + "npc_id integer,"
                + "FOREIGN KEY(location_level_position_id) REFERENCES location_positions(location_level_position_id),"
                + "FOREIGN KEY(npc_id) REFERENCES non_player_characters(npc_id)"
                + ");");

        String base_sql = "INSERT INTO npc_in_location_positions (location_level_position_id, npc_id) VALUES";
        String sql = "";
        int location_level_position_id, npc_id;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.npc_in_location_positions);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("npc_in_location_position")) {
                            location_level_position_id = Integer.parseInt(xpp.getAttributeValue(null, "location_level_position_id"));
                            npc_id = Integer.parseInt(xpp.getAttributeValue(null, "npc_id"));
                            sql += ", (" + location_level_position_id + ", " + npc_id + ")";
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
                            id = Integer.parseInt(xpp.getAttributeValue(null, "location_id"));
                            name = xpp.getAttributeValue(null, "name");
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
                + "loc_pos_1_level integer,"
                + "loc_pos_2_level integer,"
                + "loc_pos_3_level integer,"
                + "loc_pos_4_level integer,"
                + "loc_pos_5_level integer,"
                + "FOREIGN KEY(location_id) REFERENCES locations(location_id),"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id),"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createTableNonPlayerCharacters()
    {
        // создаем таблицу non_player_characters
        db.execSQL("DROP TABLE IF EXISTS non_player_characters;");
        db.execSQL("CREATE TABLE IF NOT EXISTS non_player_characters ("
                + "npc_id integer primary key autoincrement,"
                + "teammate boolean,"
                + "type text,"
                + "level integer,"
                + "fp integer,"
                + "max_res integer,"
                + "multiplier float,"
                + "exp integer,"
                + "resistance integer,"
                + "bonus_chance float,"
                + "bonus_multiplier float,"
                + "name text,"
                + "actions text,"
                + "pre_actions text"
                + ");");

        String base_sql = "INSERT INTO non_player_characters (npc_id, teammate, type, level, fp, max_res, multiplier, exp, resistance, bonus_chance, bonus_multiplier, name, actions, pre_actions) VALUES";
        String sql = "";
        int id, level, fp, max_res, resistance, teammate;
        long exp;
        float multiplier, bonus_chance, bonus_multiplier;
        String type, name, actions, pre_actions;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.non_player_characters);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("npc")) {
                            id = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                            teammate = Integer.parseInt(xpp.getAttributeValue(null, "teammate"));
                            type = xpp.getAttributeValue(null, "type");
                            level = Integer.parseInt(xpp.getAttributeValue(null, "level"));
                            fp = Integer.parseInt(xpp.getAttributeValue(null, "fp"));
                            max_res = Integer.parseInt(xpp.getAttributeValue(null, "max_res"));
                            multiplier = Float.parseFloat(xpp.getAttributeValue(null, "multiplier"));
                            exp = Long.parseLong(convertLong2Decimal(xpp.getAttributeValue(null, "exp")));
                            resistance = Integer.parseInt(xpp.getAttributeValue(null, "resistance"));
                            bonus_chance = Float.parseFloat(xpp.getAttributeValue(null, "bonus_chance"));
                            bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(null, "bonus_multiplier"));
                            name = xpp.getAttributeValue(null, "name");
                            actions = xpp.getAttributeValue(null, "actions");
                            pre_actions = xpp.getAttributeValue(null, "pre_actions");
                            sql += ", ("+ id + ", " + teammate + ", \"" + type + "\", " + level + ", " + fp + ", " + max_res + ", "
                                    + multiplier + ", " + exp + ", " + resistance + ", " + bonus_chance + ", "
                                    + bonus_multiplier + ", \"" + name +"\", \"" + actions +"\", \"" + pre_actions + "\")";
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
                            skill_id = Integer.parseInt(xpp.getAttributeValue(null, "skill_id"));
                            skill_group_id = Integer.parseInt(xpp.getAttributeValue(null, "skill_group_id"));
                            skill_level = Integer.parseInt(xpp.getAttributeValue(null, "skill_level"));
                            required_level = Integer.parseInt(xpp.getAttributeValue(null, "required_level"));
                            skill_points = Integer.parseInt(xpp.getAttributeValue(null, "skill_points"));
                            extra_fitness_points = Integer.parseInt(xpp.getAttributeValue(null, "extra_fitness_points"));
                            extra_resistance = Integer.parseInt(xpp.getAttributeValue(null, "extra_resistance"));
                            extra_multiplier = Float.parseFloat(xpp.getAttributeValue(null, "extra_multiplier"));
                            extra_bonus_chance = Float.parseFloat(xpp.getAttributeValue(null, "extra_bonus_chance"));
                            extra_bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(null, "extra_bonus_multiplier"));
                            info = xpp.getAttributeValue(null, "info");
                            label = xpp.getAttributeValue(null, "label");
                            duration = Integer.parseInt(xpp.getAttributeValue(null, "duration"));
                            reuse = Integer.parseInt(xpp.getAttributeValue(null, "reuse"));
                            specialisation = Integer.parseInt(xpp.getAttributeValue(null, "specialisation"));
                            extra_fitness_points_ratio1 = Float.parseFloat(xpp.getAttributeValue(null, "extra_fitness_points_ratio1"));
                            extra_fitness_points_ratio2 = Float.parseFloat(xpp.getAttributeValue(null, "extra_fitness_points_ratio2"));
                            extra_resistance_ratio1 = Float.parseFloat(xpp.getAttributeValue(null, "extra_resistance_ratio1"));
                            extra_resistance_ratio2 = Float.parseFloat(xpp.getAttributeValue(null, "extra_resistance_ratio2"));
                            extra_multiplier_ratio1 = Float.parseFloat(xpp.getAttributeValue(null, "extra_multiplier_ratio1"));
                            extra_multiplier_ratio2 = Float.parseFloat(xpp.getAttributeValue(null, "extra_multiplier_ratio2"));
                            extra_bonus_chance_ratio1 = Float.parseFloat(xpp.getAttributeValue(null, "extra_bonus_chance_ratio1"));
                            extra_bonus_chance_ratio2 = Float.parseFloat(xpp.getAttributeValue(null, "extra_bonus_chance_ratio2"));
                            extra_bonus_multiplier_ratio1 = Float.parseFloat(xpp.getAttributeValue(null, "extra_bonus_multiplier_ratio1"));
                            extra_bonus_multiplier_ratio2 = Float.parseFloat(xpp.getAttributeValue(null, "extra_bonus_multiplier_ratio2"));
                            extra_regeneration_base = Integer.parseInt(xpp.getAttributeValue(null, "extra_regeneration_base"));
                            extra_regeneration_ratio = Float.parseFloat(xpp.getAttributeValue(null, "extra_regeneration_ratio"));
                            splash_multiplier = Float.parseFloat(xpp.getAttributeValue(null, "splash_multiplier"));

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
                + "name text,"
                + "exp_values text"
                + ");");


        String base_sql = "INSERT INTO achievements (achievement_id, required_parameter_name, required_parameter_values, skill_points_values, name, exp_values) VALUES";
        String sql = "";
        int achievement_id;
        String required_parameter_name, required_parameter_values, skill_points_values, name, exp_values;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.achievements);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("achievement")) {
                            achievement_id = Integer.parseInt(xpp.getAttributeValue(null, "achievement_id"));
                            required_parameter_name = xpp.getAttributeValue(null, "required_parameter_name");
                            required_parameter_values = xpp.getAttributeValue(null, "required_parameter_values");
                            skill_points_values = xpp.getAttributeValue(null, "skill_points_values");
                            name = xpp.getAttributeValue(null, "name");
                            exp_values = xpp.getAttributeValue(null, "exp_values");

                            sql += ", (" + achievement_id + ", \"" + required_parameter_name + "\", \"" + required_parameter_values + "\", \"" + skill_points_values + "\", \"" + name + "\", \"" + exp_values + "\")";
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
                            skill_group_id = Integer.parseInt(xpp.getAttributeValue(null, "skill_group_id"));
                            name = xpp.getAttributeValue(null, "name");
                            type = Integer.parseInt(xpp.getAttributeValue(null, "type"));
                            target_type = Integer.parseInt(xpp.getAttributeValue(null, "target_type"));

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

    protected void createTableKnowledgeCategories()
    {
        // создаем таблицу skill_groups
        db.execSQL("DROP TABLE IF EXISTS knowledge_categories;");
        db.execSQL("CREATE TABLE IF NOT EXISTS knowledge_categories ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "order_value integer"
                + ");");

        String base_sql = "INSERT INTO knowledge_categories (id, name, order_value) VALUES";
        String sql = "";
        int id, order_value;
        String name;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.knowledge_categories);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("knowledge_category")) {
                            id = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                            name = xpp.getAttributeValue(null, "name");
                            order_value = Integer.parseInt(xpp.getAttributeValue(null, "order_value"));

                            sql += ", (" + id + ", \"" + name + "\", " + order_value + ")";
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

    protected void createTableKnowledgeItems()
    {
        // создаем таблицу skill_groups
        db.execSQL("DROP TABLE IF EXISTS knowledge_items;");
        db.execSQL("CREATE TABLE IF NOT EXISTS knowledge_items ("
                + "id integer primary key autoincrement,"
                + "category_id integer,"
                + "name text,"
                + "type integer,"
                + "text_value text,"
                + "FOREIGN KEY(category_id) REFERENCES knowledge_categories(id)"
                + ");");

        String base_sql = "INSERT INTO knowledge_items (category_id, name, type, text_value) VALUES";
        String sql = "";
        int category_id, type;
        String name, text_value;

        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.knowledge_items);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("knowledge_item")) {
                            category_id = Integer.parseInt(xpp.getAttributeValue(null, "category_id"));
                            name = xpp.getAttributeValue(null, "name");
                            text_value = xpp.getAttributeValue(null, "text_value");
                            type = Integer.parseInt(xpp.getAttributeValue(null, "type"));

                            sql += ", (" + category_id + ", \"" + name + "\", " + type + ",\"" + text_value + "\")";
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
                + "FOREIGN KEY(skill_id) REFERENCES skills(skill_id),"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id),"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    protected void createTableUserExerciseQuests() {
        // создаем таблицу user_exercise_quests
        db.execSQL("DROP TABLE IF EXISTS user_exercise_quests;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_quests ("
                + "location_id smallint,"
                + "position smallint,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "FOREIGN KEY(location_id) REFERENCES locations(location_id),"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id),"
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
                + "level integer,"
                + "location_id smallint,"
                + "position smallint,"
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
                + "results text,"
                + "FOREIGN KEY(npc_id) REFERENCES non_player_characters(npc_id),"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id),"
                + "FOREIGN KEY(exercise_id) REFERENCES exercises(exercise_id)"
                + ");");
    }

    public void recreateCommonTable()
    {
        createTableParameters();
        createTablePlayerExp();
        createTableLocations();
        createTableNonPlayerCharacters();
        createTableLocationPositions();
        createTableNpcInLocationPositions();
        createTableSkills();
        createTableSkillGroups();
        createTableAchievements();
        createTableKnowledgeCategories();
        createTableKnowledgeItems();
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

    public int setTableData(String table_name, List<Map<String, String>> data, int old_format_version, boolean deleteOldData)
    {
        int cnt = 0;
        String[] old_fields = getFieldsByTableName(table_name, old_format_version);
        String[] new_fields = getFieldsByTableName(table_name, formatVersion);
        String base_sql = "INSERT INTO " + table_name + " (" + implode(", ", new_fields) + ") VALUES";
        String sql = "";
        for (Map<String, String> row : data) {
            cnt++;
            sql += ", (";
            for (int i = 0; i < new_fields.length; i++) {
                if (i < old_fields.length) {
                    if (!old_fields[i].equals("")) {
                        sql += "\"" + row.get(old_fields[i]) + "\", ";
                    }
                } else {
                    sql += "\"\", ";
                }
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ")";
        }
        sql += ";";
        if (cnt > 0) {
            db.beginTransaction();
            if (deleteOldData) {
                db.execSQL("DELETE FROM " + table_name + ";");
            }
            db.execSQL(base_sql + sql.substring(1));
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return cnt;
    }

    public boolean setTableData(String table_name, ArrayList<Map<String, String>> data, int old_format_version, boolean deleteOldData)
    {
        boolean status = false;
        String[] old_fields = getFieldsByTableName(table_name, old_format_version);
        String[] new_fields = getFieldsByTableName(table_name, formatVersion);
        String base_sql = "INSERT INTO " + table_name + " (" + implode(", ", new_fields) + ") VALUES";
        String sql = "";
        int cnt = 0;
        for (Map<String, String> row : data) {
            cnt++;
            sql += ", (";
            for (int i = 0; i < new_fields.length; i++) {
                if (i < old_fields.length) {
                    if (!old_fields[i].equals("")) {
                        sql += "\"" + row.get(old_fields[i]) + "\", ";
                    }
                } else {
                    sql += "\"\", ";
                }
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ")";
        }
        sql += ";";
        if (cnt > 0) {
            db.beginTransaction();
            if (deleteOldData) {
                db.execSQL("DELETE FROM " + table_name + ";");
            }
            db.execSQL(base_sql + sql.substring(1));
            db.setTransactionSuccessful();
            db.endTransaction();
            status = true;
        }

        return status;
    }

    public ArrayList<Map<String, String>> getTableData(String table_name)
    {
        String[] fields = getFieldsByTableName(table_name, formatVersion);
        ArrayList<Map<String, String>> data = new ArrayList<>();
        Map<String, String> m;
        Cursor cursor = db.query(table_name, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    m = new HashMap<>();
                    for (int i = 0; i < fields.length; i++) {
                        int columnIndex = cursor.getColumnIndex(fields[i]);
                        if (columnIndex != -1) {
                            m.put(fields[i], cursor.getString(columnIndex));
                        }
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
        if (format_version >= 7) {
            m.put("users", new String[] {"user_id", "creation_date", "modification_date", "name", "for_replay"});
        } else {
            m.put("users", new String[] {"user_id", "creation_date", "modification_date", "name"});
        }

        if (format_version >= 6) {
            m.put("user_exercises", new String[]{"user_id", "exercise_id", "wins", "competitions", "draws", "specialisation", "type"});
        } else {
            m.put("user_exercises", new String[]{"user_id", "exercise_id", "wins", "competitions", "draws", "specialisation"});
        }
        m.put("exercises", new String[] {"exercise_id", "modification_date", "initial_name", "name"});

        m.put("user_exercise_skills", new String[] {"skill_id", "user_id", "exercise_id"});
        if (format_version >= 4) {
            m.put("user_exercise_locations", new String[] {"location_id", "user_id", "exercise_id", "loc_pos_1_level", "loc_pos_2_level", "loc_pos_3_level", "loc_pos_4_level", "loc_pos_5_level"});
            m.put("user_exercise_quests", new String[] {"location_id", "position", "user_id", "exercise_id"});
        } else {
            m.put("user_exercise_locations", new String[] {"location_id", "user_id", "exercise_id", "npc_1_level", "npc_2_level", "npc_3_level", "npc_4_level", "npc_5_level"});
            m.put("user_exercise_quests", new String[] {"npc_location_id", "npc_position", "user_id", "exercise_id"});
        }
        m.put("parameters", new String[] {"app_version"});
        if (format_version >= 5) {
            m.put("user_exercise_trainings", new String[]{"training_id", "user_id", "exercise_id", "npc_id", "location_id", "position", "event_timestamp", "event_timestamp", "sum_result", "max_result", "number_of_moves", "duration", "exp", "result_state", "quest_owner", "my_team_fp", "op_team_fp", "level", "results"});
        } else if (format_version >= 4) {
            m.put("user_exercise_trainings", new String[]{"training_id", "user_id", "exercise_id", "npc_id", "location_id", "position", "event_timestamp", "event_timestamp", "sum_result", "max_result", "number_of_moves", "duration", "exp", "result_state", "quest_owner", "my_team_fp", "op_team_fp", "level"});
        } else if (format_version >= 2) {
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

    public boolean loadFromFile(String filename, boolean toastOn)
    {
        boolean status = true;
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
            status = false;
        }
        Record record;
        if (json_string.length() > 0) {
            record = (new Gson()).fromJson(json_string, Record.class);
            String tables[] = getTables2Save(record.format_version);
            for (String table_name : tables) {
                boolean local_status = setTableData(table_name, record.tables.get(table_name), record.format_version, true);
                if (!local_status) {
                    status = false;
                }
            }
            if (record.format_version < 4 && formatVersion >= 4) {
                db.execSQL("UPDATE user_exercise_trainings SET level=(SELECT non_player_characters.level FROM non_player_characters WHERE non_player_characters.npc_id = user_exercise_trainings.npc_id) WHERE user_exercise_trainings.npc_id IS NOT NULL");
            }
        } else {
            status = false;
            Log.d("myLogs", "error");
            if (toastOn) Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
        }

        return status;
    }

    public boolean loadFromFileWithProgress(String filename, boolean toastOn, ProgressBar pbUpdate, int load_file_progress, int chunk_size)
    {
        int progress = 0;
        pbUpdate.setProgress(progress);

        boolean status = true;
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
            status = false;
        }
        Record record;
        if (json_string.length() > 0) {
            record = (new Gson()).fromJson(json_string, Record.class);
            String tables[] = getTables2Save(record.format_version);

            progress = load_file_progress;
            int total_cnt = load_file_progress;
            for (String table_name : tables) {
                total_cnt += record.tables.get(table_name).size();
            }
            pbUpdate.setMax(total_cnt);
            pbUpdate.setProgress(progress);

            for (String table_name : tables) {
                int data_length = record.tables.get(table_name).size();
                int chunks_cnt = (int) Math.ceil((double) data_length / chunk_size);
                Log.d("update", String.format("[%s] Update data_length: %d  chunk_size: %d", table_name, data_length, chunk_size));
                Log.d("update", String.format("Update chunks_cnt: %d", chunks_cnt));
                for (int chunk_id = 0; chunk_id < chunks_cnt; chunk_id++) {
                    Log.d("update", String.format("Update chunks_id: %d", chunk_id));
                    List<Map<String, String>> chunk_data = record.tables.get(table_name).subList(chunk_id * chunk_size, Math.min((chunk_id + 1) * chunk_size, data_length));
                    int local_cnt= setTableData(table_name, chunk_data, record.format_version, chunk_id == 0);
                    if (local_cnt == 0) {
                        status = false;
                    }
                    progress += local_cnt;
                    pbUpdate.setProgress(progress);

                    Log.d("update", String.format("Update progress: %d / %d", progress, total_cnt));
                    Log.d("update", String.format("Update diff: %d", local_cnt));
                }


//                boolean local_status = setTableData(table_name, record.tables.get(table_name), record.format_version, true);
//                if (!local_status) {
//                    status = false;
//                }


            }
            if (record.format_version < 4 && formatVersion >= 4) {
                db.execSQL("UPDATE user_exercise_trainings SET level=(SELECT non_player_characters.level FROM non_player_characters WHERE non_player_characters.npc_id = user_exercise_trainings.npc_id) WHERE user_exercise_trainings.npc_id IS NOT NULL");
            }
        } else {
            status = false;
            Log.d("myLogs", "error");
            if (toastOn) Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
        }

        return status;
    }
}