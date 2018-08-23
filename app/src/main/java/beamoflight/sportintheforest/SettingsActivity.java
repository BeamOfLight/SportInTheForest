package beamoflight.sportintheforest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Locale;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends Activity {
    GameHelper gameHelper;
    DBHelper dbHelper;

//    AlertDialog dialogAddExercise;

    Button btnImportDBold, btnExportDBold, btnImportDBnew, btnExportDBnew;
    Button btnAddExercise, btnWipe, btnWipeSaveUserProgress, btnTest;
    Button btnImportDBAuto, btnImportDBAutoMon, btnImportDBAutoTue, btnImportDBAutoWed;
    Button btnImportDBAutoThu, btnImportDBAutoFri, btnImportDBAutoSat, btnImportDBAutoSun;

    //TextView txtString;


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //txtString = (TextView) findViewById(R.id.txtString);

        gameHelper = new GameHelper(getBaseContext());
        dbHelper = new DBHelper(getBaseContext());

        prepareBackUpDir();

        btnImportDBAuto = (Button) findViewById(R.id.btnImportDBAuto);
        btnImportDBAuto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoMon = (Button) findViewById(R.id.btnImportDBAutoMon);
        btnImportDBAutoMon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Mon.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoTue = (Button) findViewById(R.id.btnImportDBAutoTue);
        btnImportDBAutoTue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Tue.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoWed = (Button) findViewById(R.id.btnImportDBAutoWed);
        btnImportDBAutoWed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Wed.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoThu = (Button) findViewById(R.id.btnImportDBAutoThu);
        btnImportDBAutoThu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Thu.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoFri = (Button) findViewById(R.id.btnImportDBAutoFri);
        btnImportDBAutoFri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Fri.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoSat = (Button) findViewById(R.id.btnImportDBAutoSat);
        btnImportDBAutoSat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Sat.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBAutoSun = (Button) findViewById(R.id.btnImportDBAutoSun);
        btnImportDBAutoSun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Sun.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnImportDBold = (Button) findViewById(R.id.btnImportDBold);
        btnImportDBold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.importDB("SportInTheForestDB.db", true);
            }
        });

        btnExportDBold = (Button) findViewById(R.id.btnExportDBold);
        btnExportDBold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.exportDB("SportInTheForestDB.db", false);
                dbHelper.exportDB("SportInTheForestDB_" + gameHelper.getTodayString() + ".db", true);
            }
        });

        btnImportDBnew = (Button) findViewById(R.id.btnImportDBnew);
        btnImportDBnew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("SportInTheForest.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
            }
        });

        btnExportDBnew = (Button) findViewById(R.id.btnExportDBnew);
        btnExportDBnew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.save2file("SportInTheForest.sif");
                dbHelper.save2file("SportInTheForest_" + gameHelper.getTodayStringWithHours() + ".sif");
                dbHelper.save2file("SportInTheForest_" + gameHelper.getTodayString() + ".sif");
                Toast.makeText(getBaseContext(), "Сохранено!", Toast.LENGTH_LONG).show();
            }
        });

        btnWipe = (Button) findViewById(R.id.btnWipe);
        btnWipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnWipe.setEnabled(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());

                SharedPreferences s_pref = getBaseContext().getSharedPreferences(getBaseContext().getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = s_pref.edit();
                editor.putInt(getBaseContext().getResources().getString(R.string.preference_name_user_encoded_level), 0);
                editor.putLong(getBaseContext().getResources().getString(R.string.preference_name_user_exp), 0);
                editor.putString(getBaseContext().getResources().getString(R.string.preference_name_user_name), "");
                editor.commit();

                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                btnWipe.setEnabled(true);
            }
        });

        btnWipeSaveUserProgress = (Button) findViewById(R.id.btnWipeSaveUserProgress);
        btnWipeSaveUserProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnWipeSaveUserProgress.setEnabled(false);
                dbHelper.recreateCommonTable();
                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                btnWipeSaveUserProgress.setEnabled(true);
            }
        });

        btnAddExercise = (Button) findViewById(R.id.btnAddExercise);
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnAddExercise.setEnabled(false);
                Intent intent = new Intent(SettingsActivity.this, ExercisesActivity.class);
                startActivity(intent);
                btnAddExercise.setEnabled(true);
//                dialogAddExercise.show();
            }
        });


        btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnTest.setEnabled(false);
                Intent intent = new Intent(SettingsActivity.this, CompetitionViewerActivity.class);
                startActivity(intent);
                btnTest.setEnabled(true);
            }
        });


//        btnRecreateTrainings = (Button) findViewById(R.id.btnRecreateTrainings);
//        btnRecreateTrainings.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                btnRecreateTrainings.setEnabled(false);
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                db.execSQL("ALTER TABLE user_exercise_trainings ADD COLUMN quest_owner boolean;");
//                db.execSQL("UPDATE user_exercise_trainings SET quest_owner = 1;");
//                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
//                btnRecreateTrainings.setEnabled(true);
                /*db.execSQL("UPDATE user_exercise_trainings SET duration = 1;");
                db.execSQL("ALTER TABLE user_exercise_trainings ADD COLUMN event_timestamp DATETIME;");
                db.execSQL("UPDATE user_exercise_trainings SET event_timestamp = event_date");
                //db.execSQL("ALTER TABLE user_exercise_trainings DROP COLUMN event_date;");
                db.execSQL("BEGIN TRANSACTION;" +
                "CREATE TEMPORARY TABLE user_exercise_trainings_backup(training_id, user_id, exercise_id, npc_id, npc_location_id, npc_position , event_timestamp, sum_result, max_result, number_of_moves, duration , exp, result_state);" +
                "INSERT INTO user_exercise_trainings_backup SELECT training_id, user_id, exercise_id, npc_id, npc_location_id, npc_position , event_timestamp, sum_result, max_result, number_of_moves, duration , exp, result_state FROM user_exercise_trainings;" +
                "DROP TABLE user_exercise_trainings;" +
                "CREATE TABLE user_exercise_trainings(training_id, user_id, exercise_id, npc_id, npc_location_id, npc_position , event_timestamp, sum_result, max_result, number_of_moves, duration , exp, result_state);" +
                "INSERT INTO user_exercise_trainings SELECT training_id, user_id, exercise_id, npc_id, npc_location_id, npc_position , event_timestamp, sum_result, max_result, number_of_moves, duration , exp, result_state FROM user_exercise_trainings_backup;" +
                "DROP TABLE user_exercise_trainings_backup;" +
                "COMMIT;");

//            }
        });
//
*/
/*
        btnVersionUpgrade = (Button) findViewById(R.id.btnVersionUpgrade);
        btnVersionUpgrade.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnVersionUpgrade.setEnabled(false);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //db.execSQL("ALTER TABLE skills ADD COLUMN extra_regeneration_base integer;");
                //db.execSQL("ALTER TABLE skills ADD COLUMN extra_regeneration_ratio float;");
                db.execSQL("ALTER TABLE skills ADD COLUMN extra_regeneration_base integer, ADD COLUMN extra_regeneration_ratio float;");

                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                btnVersionUpgrade.setEnabled(true);
            }
        });
*/

//        btnAction1 = (Button) findViewById(R.id.btnAction1);
//        btnAction1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
////                try {
////                    run();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//            }
//        });



//
//        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this,
//                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
//                    PackageManager.PERMISSION_GRANTED
//            );
//        }
    }

    protected void onStart() {
        super.onStart();
//        initDialogAddExercise();
    }

//    private void initDialogAddExercise()
//    {
//        // get prompt_add_group.xmlgroup.xml view
//        LayoutInflater li = LayoutInflater.from(this);
//        View prompts_view = li.inflate(R.layout.prompt_add_exercise, null);
//        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
//        alert_dialog_builder.setView(prompts_view);
//
//        final EditText et_input_user_name = (EditText) prompts_view.findViewById(R.id.editTextDialogExerciseInput);
//
//        // set dialog message
//        alert_dialog_builder
//                .setCancelable(false)
//                .setPositiveButton(R.string.btn_save_text,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                String exercise_name = et_input_user_name.getText().toString();
//                                if (dbHelper.checkExerciseExist(exercise_name)) {
//                                    Toast.makeText(getBaseContext(), "Упражнение с таким названием уже существует", Toast.LENGTH_LONG).show();
//                                } else {
//                                    dbHelper.createExercise(exercise_name);
//                                    Toast.makeText(getBaseContext(), "Упражнение создано", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        })
//                .setNegativeButton(R.string.btn_cancel_text,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        // create alert dialog
//        dialogAddExercise = alert_dialog_builder.create();
//    }

    private void prepareBackUpDir()
    {
        File direct = new File(Environment.getExternalStorageDirectory() + "/SportInTheForest");

        if(!direct.exists())
        {
            if(direct.mkdir())
            {
                //directory is created;
            } else {
                Toast.makeText(getBaseContext(), "Не могу создать директорию", Toast.LENGTH_LONG).show();
                Log.d(getResources().getString(R.string.log_tag), "DEBUG: Не могу создать директорию");
            }
        } else {
            Log.d(getResources().getString(R.string.log_tag), "DEBUG: Директория уже существует");
        }
    }

/*
    Handler mainHandler;
    void run() throws IOException {
        mainHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                txtString.setText((String) msg.obj);
            }
        };

        NetworkHelper networkHelper = new NetworkHelper(getBaseContext());
        //networkHelper.getClient().newCall(networkHelper.getGetCompetitionInfo("AAAA3214AVVD")).enqueue(new Callback() {
        CompetitionView competition_view = new CompetitionView();
        networkHelper.getClient().newCall(networkHelper.getCreateCompetitionRequest(competition_view, "werwert2")).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.obj = e.toString();
                mainHandler.sendMessage(msg);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                String json_string = response.body().string();
                //json_string +="{";
                try {
                    NetworkHelper.CreateCompetitionResponse response_object = (new Gson()).fromJson(json_string, NetworkHelper.CreateCompetitionResponse.class);
                    json_string = String.format(Locale.ROOT, "invite_code: %s state: %s msg: %s", response_object.inviteCode, response_object.state, response_object.msg);
                } catch (Exception e) {
                    json_string += e.toString();
                }
                msg.obj = json_string;
                mainHandler.sendMessage(msg);
            }
        });
    }
    */
}