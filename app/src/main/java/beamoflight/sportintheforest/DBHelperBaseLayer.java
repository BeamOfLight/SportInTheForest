package beamoflight.sportintheforest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by beamoflight on 30.05.17.
 */
class DBHelperBaseLayer extends SQLiteOpenHelper {
    protected Context context;

    public DBHelperBaseLayer(Context current) {
        // конструктор суперкласса
        super (current, "SportInTheForestDB", null, 5);

        context = current;
    }

    protected void createTablePlayerExp(SQLiteDatabase db)
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

    protected void createTableUsers(SQLiteDatabase db)
    {
        // создаем таблицу user_exercises
        db.execSQL("DROP TABLE IF EXISTS users;");
        db.execSQL("CREATE TABLE IF NOT EXISTS users ("
                + "user_id integer primary key autoincrement,"
                + "creation_date date,"
                + "modification_date date,"
                + "name text" + ");");
    }

    protected void createTableUserExercises(SQLiteDatabase db) {
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
                + "FOREIGN KEY(exercise_id) REFERENCES user_exercises(exercise_id)"
                + ");");
    }

    protected void createTableExercises(SQLiteDatabase db)
    {
        // создаем таблицу user_exercises
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
                            sql += ", (" + id + ", NOW(), \"" + name +"\", \"" + name +"\")";
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

    protected void createTableLocations(SQLiteDatabase db)
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

    protected void createTableUserExerciseLocations(SQLiteDatabase db) {
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
                + "FOREIGN KEY(exercise_id) REFERENCES user_exercises(exercise_id)"
                + ");");
    }

    protected void createTableNonPlayerCharacters(SQLiteDatabase db)
    {
        // создаем таблицу non_player_characters
        db.execSQL("DROP TABLE IF EXISTS non_player_characters;");
        db.execSQL("CREATE TABLE IF NOT EXISTS non_player_characters ("
                + "npc_id integer primary key autoincrement,"
                + "location_id integer,"
                + "position integer,"
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
                + "team text,"
                + "FOREIGN KEY(location_id) REFERENCES locations(location_id)"
                + ");");

        String base_sql = "INSERT INTO non_player_characters (npc_id, location_id, position, type, level, fp, max_res, multiplier, exp, resistance, quest_cnt, quest_exp, bonus_chance, bonus_multiplier, name, team) VALUES";
        String sql = "";
        int id, location_id, position, level, fp, max_res, resistance, quest_cnt;
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
                            location_id = Integer.parseInt(xpp.getAttributeValue(1));
                            position = Integer.parseInt(xpp.getAttributeValue(2));
                            type = xpp.getAttributeValue(3);
                            level = Integer.parseInt(xpp.getAttributeValue(4));
                            fp = Integer.parseInt(xpp.getAttributeValue(5));
                            max_res = Integer.parseInt(xpp.getAttributeValue(6));
                            multiplier = Float.parseFloat(xpp.getAttributeValue(7));
                            exp = Long.parseLong(xpp.getAttributeValue(8));
                            resistance = Integer.parseInt(xpp.getAttributeValue(9));
                            quest_cnt = Integer.parseInt(xpp.getAttributeValue(10));
                            quest_exp = Long.parseLong(xpp.getAttributeValue(11));
                            bonus_chance = Float.parseFloat(xpp.getAttributeValue(12));
                            bonus_multiplier = Float.parseFloat(xpp.getAttributeValue(13));
                            name = xpp.getAttributeValue(14);
                            team = xpp.getAttributeValue(15);
                            sql += ", ("+ id + ", " + location_id + ", " + position + ", \"" + type + "\", " + level + ", " + fp + ", " + max_res + ", "
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

    protected void createTableSkills(SQLiteDatabase db)
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

    protected void createTableAchievements(SQLiteDatabase db)
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

    protected void createTableSkillGroups(SQLiteDatabase db)
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

    protected void createTableUserExerciseSkills(SQLiteDatabase db) {
        // создаем таблицу user_exercise_skills
        db.execSQL("DROP TABLE IF EXISTS user_exercise_skills;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_skills ("
                + "skill_id integer,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "FOREIGN KEY(skill_id) REFERENCES skills(skill_id)"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES user_exercises(exercise_id)"
                + ");");
    }

    protected void createTableUserExerciseQuests(SQLiteDatabase db) {
        // создаем таблицу user_exercise_quests
        db.execSQL("DROP TABLE IF EXISTS user_exercise_quests;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_quests ("
                + "npc_location_id smallint,"
                + "npc_position smallint,"
                + "user_id integer,"
                + "exercise_id integer,"
                + "FOREIGN KEY(npc_location_id) REFERENCES locations(location_id)"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES user_exercises(exercise_id)"
                + ");");
    }

    protected void createUserExerciseTrainingsTable(SQLiteDatabase db)
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
                + "exp integer,"
                + "result_state smallint,"
                + "quest_owner boolean,"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + "FOREIGN KEY(exercise_id) REFERENCES user_exercises(exercise_id)"
                + "FOREIGN KEY(npc_id) REFERENCES non_player_characters(npc_id)"
                + ");");
    }

    public void recreateCommonTable(SQLiteDatabase db)
    {
        createTablePlayerExp(db);
        createTableLocations(db);
        createTableNonPlayerCharacters(db);
        createTableSkills(db);
        createTableSkillGroups(db);
        createTableAchievements(db);
    }

    //importing database
    public void importDB(String backup_filename) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

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
                Toast.makeText(context, backupDB.toString(), Toast.LENGTH_LONG).show();
                Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: importDB SUCCESS" + backupDB.toString());
            } else {
                Toast.makeText(context, "Нет прав", Toast.LENGTH_LONG).show();
                Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: Нет прав");
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.d(context.getResources().getString(R.string.log_tag), "DEBUG: importDB FAIL " + e.toString());
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onCreate database ---");

        createTableExercises(db);
        createUserExerciseTrainingsTable(db);
        createTableUsers(db);
        createTableUserExercises(db);
        createTableUserExerciseLocations(db);
        createTableUserExerciseSkills(db);
        createTableUserExerciseQuests(db);

        recreateCommonTable(db);

        // TODO: remove after debug
        //db.execSQL("INSERT INTO users (user_id, creation_date, modification_date, name) VALUES (1, \"1994-03-06\", \"1994-03-06\",\"test\");");
        //db.execSQL("INSERT INTO user_exercise_locations (location_id, user_id, exercise_id) SELECT 1 AS location_id, 1 as user_id, exercise_id FROM user_exercises");
        //db.execSQL("INSERT INTO user_exercise_locations (location_id, user_id, exercise_id) VALUES (1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onUpgrade database ---");
        //exportDB("update_backup.db", false);
        //onCreate(getWritableDatabase());
        //importDB("update_backup.db");
        recreateCommonTable(db);
        if (newVersion == 5) {
            db.execSQL("ALTER TABLE exercises ADD COLUMN initial_name text;");
            db.execSQL("ALTER TABLE exercises ADD COLUMN modification_date date;");
            db.execSQL("UPDATE exercises SET initial_name = name");

            //importDB("update_backup.db");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onDowngrade database ---");
        recreateCommonTable(db);
    }
}