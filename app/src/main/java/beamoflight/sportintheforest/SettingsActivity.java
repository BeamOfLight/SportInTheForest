package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    Button btnTestReplay;

    private void turnOnOffButtons(boolean status)
    {
        btnImportDBAuto.setEnabled(status);
        btnExportDBold.setEnabled(status);
        btnImportDBnew.setEnabled(status);
        btnExportDBnew.setEnabled(status);

        btnAddExercise.setEnabled(status);
        btnWipe.setEnabled(status);
        btnWipeSaveUserProgress.setEnabled(status);
        btnTest.setEnabled(status);

        btnImportDBAuto.setEnabled(status);
        btnImportDBAutoMon.setEnabled(status);
        btnImportDBAutoTue.setEnabled(status);
        btnImportDBAutoWed.setEnabled(status);

        btnImportDBAutoThu.setEnabled(status);
        btnImportDBAutoFri.setEnabled(status);
        btnImportDBAutoSat.setEnabled(status);
        btnImportDBAutoSun.setEnabled(status);
    }

    private void loadAutoSaveDialog(final String filename)
    {
        new AlertDialog.Builder(SettingsActivity.this)
                .setMessage(R.string.are_you_sure)
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadAutoSave(filename);
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void loadAutoSave(String filename)
    {
        turnOnOffButtons(false);
        dbHelper.onCreate(dbHelper.getWritableDatabase());
        dbHelper.loadFromFile(filename, true);
        Toast.makeText(getBaseContext(), "Загружено!", Toast.LENGTH_LONG).show();
        turnOnOffButtons(true);
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        gameHelper = new GameHelper(getBaseContext());
        dbHelper = new DBHelper(getBaseContext());

        prepareBackUpDir();

        btnImportDBAuto = findViewById(R.id.btnImportDBAuto);
        btnImportDBAuto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave.sif");
            }
        });

        btnImportDBAutoMon = findViewById(R.id.btnImportDBAutoMon);
        btnImportDBAutoMon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Mon.sif");
            }
        });

        btnImportDBAutoTue = findViewById(R.id.btnImportDBAutoTue);
        btnImportDBAutoTue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Tue.sif");
            }
        });

        btnImportDBAutoWed = findViewById(R.id.btnImportDBAutoWed);
        btnImportDBAutoWed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Wed.sif");
            }
        });

        btnImportDBAutoThu = findViewById(R.id.btnImportDBAutoThu);
        btnImportDBAutoThu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Thu.sif");
            }
        });

        btnImportDBAutoFri = findViewById(R.id.btnImportDBAutoFri);
        btnImportDBAutoFri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Fri.sif");
            }
        });

        btnImportDBAutoSat = findViewById(R.id.btnImportDBAutoSat);
        btnImportDBAutoSat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Sat.sif");
            }
        });

        btnImportDBAutoSun = findViewById(R.id.btnImportDBAutoSun);
        btnImportDBAutoSun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("autosave_Sun.sif");
            }
        });

        btnImportDBold = findViewById(R.id.btnImportDBold);
        btnImportDBold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(R.string.are_you_sure)
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                turnOnOffButtons(false);
                                dbHelper.importDB("SportInTheForestDB.db", true);
                                turnOnOffButtons(true);
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });

        btnExportDBold = findViewById(R.id.btnExportDBold);
        btnExportDBold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                dbHelper.exportDB("SportInTheForestDB.db", false);
                dbHelper.exportDB("SportInTheForestDB_" + gameHelper.getTodayString() + ".db", true);
                turnOnOffButtons(true);
            }
        });

        btnImportDBnew = findViewById(R.id.btnImportDBnew);
        btnImportDBnew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadAutoSaveDialog("SportInTheForest.sif");
            }
        });

        btnExportDBnew = findViewById(R.id.btnExportDBnew);
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

        btnWipe = findViewById(R.id.btnWipe);
        btnWipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(R.string.are_you_sure)
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });

        btnWipeSaveUserProgress = findViewById(R.id.btnWipeSaveUserProgress);
        btnWipeSaveUserProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(R.string.are_you_sure)
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                turnOnOffButtons(false);
                                dbHelper.recreateCommonTable();
                                Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                                turnOnOffButtons(true);
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });

        btnAddExercise = findViewById(R.id.btnAddExercise);
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                Intent intent = new Intent(SettingsActivity.this, ExercisesActivity.class);
                startActivity(intent);
                turnOnOffButtons(true);
            }
        });


        btnTest = findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnOffButtons(false);
                Intent intent = new Intent(SettingsActivity.this, CompetitionViewerActivity.class);
                startActivity(intent);
                turnOnOffButtons(true);
            }
        });

        btnTestReplay = findViewById(R.id.btnTestReplay);
        btnTestReplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameHelper.enableReplayMode(SettingsActivity.this, "toast;Начинаем;long#activity;users;empty#bgcolor;lvNewUser;colorAccent#toast;Завершаем;long#exit;0;0");
            }
        });
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameHelper.removeReplayTimerTask();
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