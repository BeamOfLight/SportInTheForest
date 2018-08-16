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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by beamoflight on 30.05.17.
 */
class DBProgressSaver extends SQLiteOpenHelper {
    protected Context context;
    protected SQLiteDatabase db;

    public DBProgressSaver(Context current) {
        // конструктор суперкласса
        super (current, "SportInTheForestProgressDB", null, 1);
        db = getWritableDatabase();
        context = current;
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

    public void setTableData(String table_name, ArrayList<Map<String, String>> data)
    {
        Log.d("DEBUG2", data.toString());
        String[] fields = getFieldsByTableName(table_name);
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
        Log.d("DEBUG3", base_sql + sql.substring(1));
        if (cnt > 0) {
            Log.d("DEBUG7", "db.execSQL " + table_name);
            db.beginTransaction();
            db.execSQL("DELETE FROM " + table_name + ";");
            db.execSQL(base_sql + sql.substring(1));
            db.setTransactionSuccessful();
            db.endTransaction();
        }

    }

    public ArrayList<Map<String, String>> getTableData(String table_name)
    {
        String[] fields = getFieldsByTableName(table_name);
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
        Log.d("DEBUG4", data.toString());
        return data;
    }

    private String[] getFieldsByTableName(String table_name)
    {
        Map<String, String[]> m = new HashMap<>();
        m.put("users", new String[] {"user_id", "creation_date", "modification_date", "name"});
        m.put("user_exercises", new String[] {"user_id", "exercise_id", "wins", "competitions", "draws", "specialisation"});
        m.put("exercises", new String[] {"exercise_id", "modification_date", "initial_name", "name"});
        m.put("user_exercise_locations", new String[] {"location_id", "user_id", "exercise_id", "npc_1_level", "npc_2_level", "npc_3_level", "npc_4_level", "npc_5_level"});
        m.put("user_exercise_skills", new String[] {"skill_id", "user_id", "exercise_id"});
        m.put("user_exercise_quests", new String[] {"npc_location_id", "npc_position", "user_id", "exercise_id"});
        m.put("user_exercise_trainings", new String[] {"training_id", "user_id", "exercise_id", "npc_id", "npc_location_id", "npc_position", "event_timestamp", "event_timestamp", "sum_result", "max_result", "number_of_moves", "duration", "exp", "result_state", "quest_owner"});

        return m.get(table_name);
    }

    protected void createTableUsers()
    {
        db.execSQL("DROP TABLE IF EXISTS users;");
        db.execSQL("CREATE TABLE IF NOT EXISTS users ("
                + "user_id integer primary key autoincrement,"
                + "creation_date date,"
                + "modification_date date,"
                + "name text" + ");");
    }

    protected void createTableUserExercises() {
        db.execSQL("DROP TABLE IF EXISTS user_exercises;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercises ("
                + "user_id integer,"
                + "exercise_id integer,"
                + "wins integer,"
                + "competitions integer,"
                + "draws integer,"
                + "specialisation integer" + ");");
    }


//    public void setTableUserExercisesData_(SQLiteDatabase db, ArrayList<Map<String, String>> data)
//    {
//        String base_sql = "INSERT INTO user_exercises (user_id, exercise_id, wins, competitions, draws, specialisation) VALUES";
//        String sql = "";
//        for (Map<String, String> row : data) {
//            sql += ", (" + row.get("user_id") + ", " + row.get("exercise_id") + ", " + row.get("wins") + ", " + row.get("competitions") + ", " + row.get("draws") + ", " + row.get("specialisation") + ")";
//        }
//        sql += ";";
//        db.execSQL(base_sql + sql.substring(1));
//    }
//
//    public ArrayList<Map<String, String>> getTableUserExercisesData_(SQLiteDatabase db)
//    {
//        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
//        Map<String, String> m;
//        Cursor cursor = db.query("users", new String[] { "user_id", "exercise_id", "wins", "competitions", "draws", "specialisation" }, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    m = new HashMap<String, String>();
//                    m.put("user_id", Integer.toString(cursor.getInt(cursor.getColumnIndex("user_id"))));
//                    m.put("exercise_id", Integer.toString(cursor.getInt(cursor.getColumnIndex("exercise_id"))));
//                    m.put("wins", Integer.toString(cursor.getInt(cursor.getColumnIndex("wins"))));
//                    m.put("competitions", Integer.toString(cursor.getInt(cursor.getColumnIndex("competitions"))));
//                    m.put("draws", Integer.toString(cursor.getInt(cursor.getColumnIndex("draws"))));
//                    m.put("specialisation", Integer.toString(cursor.getInt(cursor.getColumnIndex("specialisation"))));
//                    data.add(m);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
//
//        return data;
//    }

    protected void createTableExercises()
    {
        db.execSQL("DROP TABLE IF EXISTS exercises;");
        db.execSQL("CREATE TABLE IF NOT EXISTS exercises ("
                + "exercise_id integer primary key autoincrement,"
                + "modification_date date,"
                + "initial_name text,"
                + "name text" + ");");
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
                + "npc_5_level integer" + ");");
    }

    protected void createTableUserExerciseSkills() {
        // создаем таблицу user_exercise_skills
        db.execSQL("DROP TABLE IF EXISTS user_exercise_skills;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_skills ("
                + "skill_id integer,"
                + "user_id integer,"
                + "exercise_id integer" + ");");
    }

    protected void createTableUserExerciseQuests() {
        // создаем таблицу user_exercise_quests
        db.execSQL("DROP TABLE IF EXISTS user_exercise_quests;");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_exercise_quests ("
                + "npc_location_id smallint,"
                + "npc_position smallint,"
                + "user_id integer,"
                + "exercise_id integer" + ");");
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
                + "exp integer,"
                + "result_state smallint,"
                + "quest_owner boolean" + ");");
    }

    //importing database
    public void importDB(String backup_filename) {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                //String backupDBPath = "/SportInTheForest/SportInTheForestDB_" + gameHelper.getTodayString();
                String currentDBPath = context.getDatabasePath("SportInTheForestProgressDB").toString();
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
                String currentDBPath = context.getDatabasePath("SportInTheForestProgressDB").toString();
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
        this.db = db;
        //Log.d(context.getResources().getString(R.string.log_tag), "--- onCreate database ---");

        createTableExercises();
        createUserExerciseTrainingsTable();
        createTableUsers();
        createTableUserExercises();
        createTableUserExerciseLocations();
        createTableUserExerciseSkills();
        createTableUserExerciseQuests();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d(context.getResources().getString(R.string.log_tag), "--- onUpgrade database ---");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(context.getResources().getString(R.string.log_tag), "--- onDowngrade database ---");
    }
}