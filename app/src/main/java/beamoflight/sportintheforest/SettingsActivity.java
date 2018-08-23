package beamoflight.sportintheforest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;

public class SettingsActivity extends Activity {
    GameHelper gameHelper;
    DBHelper dbHelper;

    Button btnImportDBold, btnExportDBold, btnImportDBnew, btnExportDBnew;
    Button btnAddExercise, btnWipe, btnWipeSaveUserProgress, btnTest;
    Button btnImportDBAuto, btnImportDBAutoMon, btnImportDBAutoTue, btnImportDBAutoWed;
    Button btnImportDBAutoThu, btnImportDBAutoFri, btnImportDBAutoSat, btnImportDBAutoSun;

    private void turnOnOffButtons(boolean enabled)
    {
        btnImportDBAuto.setEnabled(enabled);
        btnExportDBold.setEnabled(enabled);
        btnImportDBnew.setEnabled(enabled);
        btnExportDBnew.setEnabled(enabled);

        btnAddExercise.setEnabled(enabled);
        btnWipe.setEnabled(enabled);
        btnWipeSaveUserProgress.setEnabled(enabled);
        btnTest.setEnabled(enabled);

        btnImportDBAuto.setEnabled(enabled);
        btnImportDBAutoMon.setEnabled(enabled);
        btnImportDBAutoTue.setEnabled(enabled);
        btnImportDBAutoWed.setEnabled(enabled);

        btnImportDBAutoThu.setEnabled(enabled);
        btnImportDBAutoFri.setEnabled(enabled);
        btnImportDBAutoSat.setEnabled(enabled);
        btnImportDBAutoSun.setEnabled(enabled);
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        gameHelper = new GameHelper(getBaseContext());
        dbHelper = new DBHelper(getBaseContext());

        prepareBackUpDir();

        btnImportDBAuto = (Button) findViewById(R.id.btnImportDBAuto);
        btnImportDBAuto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoMon = (Button) findViewById(R.id.btnImportDBAutoMon);
        btnImportDBAutoMon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Mon.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoTue = (Button) findViewById(R.id.btnImportDBAutoTue);
        btnImportDBAutoTue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Tue.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoWed = (Button) findViewById(R.id.btnImportDBAutoWed);
        btnImportDBAutoWed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Wed.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoThu = (Button) findViewById(R.id.btnImportDBAutoThu);
        btnImportDBAutoThu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Thu.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoFri = (Button) findViewById(R.id.btnImportDBAutoFri);
        btnImportDBAutoFri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Fri.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoSat = (Button) findViewById(R.id.btnImportDBAutoSat);
        btnImportDBAutoSat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Sat.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBAutoSun = (Button) findViewById(R.id.btnImportDBAutoSun);
        btnImportDBAutoSun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("autosave_Sun.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnImportDBold = (Button) findViewById(R.id.btnImportDBold);
        btnImportDBold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.importDB("SportInTheForestDB.db", true);
                turnOnOffButtons(true);
            }
        });

        btnExportDBold = (Button) findViewById(R.id.btnExportDBold);
        btnExportDBold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.exportDB("SportInTheForestDB.db", false);
                dbHelper.exportDB("SportInTheForestDB_" + gameHelper.getTodayString() + ".db", true);
                turnOnOffButtons(true);
            }
        });

        btnImportDBnew = (Button) findViewById(R.id.btnImportDBnew);
        btnImportDBnew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());
                dbHelper.loadFromFile("SportInTheForest.sif", true);
                Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnExportDBnew = (Button) findViewById(R.id.btnExportDBnew);
        btnExportDBnew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.save2file("SportInTheForest.sif");
                dbHelper.save2file("SportInTheForest_" + gameHelper.getTodayStringWithHours() + ".sif");
                dbHelper.save2file("SportInTheForest_" + gameHelper.getTodayString() + ".sif");
                Toast.makeText(getBaseContext(), "Сохранено!", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnWipe = (Button) findViewById(R.id.btnWipe);
        btnWipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.onCreate(dbHelper.getWritableDatabase());

                SharedPreferences s_pref = getBaseContext().getSharedPreferences(getBaseContext().getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = s_pref.edit();
                editor.putInt(getBaseContext().getResources().getString(R.string.preference_name_user_encoded_level), 0);
                editor.putLong(getBaseContext().getResources().getString(R.string.preference_name_user_exp), 0);
                editor.putString(getBaseContext().getResources().getString(R.string.preference_name_user_name), "");
                editor.commit();

                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnWipeSaveUserProgress = (Button) findViewById(R.id.btnWipeSaveUserProgress);
        btnWipeSaveUserProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.recreateCommonTable();
                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                turnOnOffButtons(true);
            }
        });

        btnAddExercise = (Button) findViewById(R.id.btnAddExercise);
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                Intent intent = new Intent(SettingsActivity.this, ExercisesActivity.class);
                startActivity(intent);
                turnOnOffButtons(true);
            }
        });


        btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                Intent intent = new Intent(SettingsActivity.this, CompetitionViewerActivity.class);
                startActivity(intent);
                turnOnOffButtons(true);
            }
        });
    }

    protected void onStart() {
        super.onStart();
    }

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
}