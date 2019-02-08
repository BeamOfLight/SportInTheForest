package beamoflight.sportintheforest;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    TextView tvVersion;
    Button btMenuStart, btMenuSettings, btMenuKnowledge;
    ProgressBar pbUpdate;
    String app_version;
    Handler errorHandler;
    Thread thread;

    boolean updateStarted = false;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        gameHelper.disableReplayMode();
        gameHelper.setSharedPreferencesInt("replay_close_last_activity", 0);

        app_version = dbHelper.getAppVersion();
        tvVersion = findViewById(R.id.tvVersion);

        pbUpdate = findViewById(R.id.pbUpdate);
        pbUpdate.setProgress(0);

        errorHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                btMenuSettings.setEnabled(true);
                Toast.makeText(getBaseContext(), "Не удалось загрузить автосохранение", Toast.LENGTH_LONG).show();
                Log.d("APP", "errorHandler");

            }
        };

        initMenuButtons();
        checkVersion();
    }

    private void showVersion()
    {
        tvVersion.setText(
            String.format(
                Locale.ROOT,
                "ver. %s.%s",
                dbHelper.getAppVersion(),
                dbHelper.getAppVersion().equals("0.0.0") ? "0" : dbHelper.getAppVersionRevision()
            )
        );
    }

    private void autoUpdate()
    {
        try {
            dbHelper.customOnCreate();
            dbHelper.loadFromFileWithProgress("autosave.sif", false, pbUpdate, 500, 100);
            dbHelper.updateAppVersion(getResources().getString(R.string.app_version));
            updateStarted = false;
        } catch (Exception e){
            // Toast.makeText(getBaseContext(), "Не удалось загрузить автосохранение", Toast.LENGTH_LONG).show();
            String st = "";
            if (e.getStackTrace().length > 0) {
                for (StackTraceElement ste: e.getStackTrace()) {
                    st += ste.toString() + "\n";
                }
            }
            Log.d("APP", "Не удалось загрузить автосохранение: " + e.toString() + "\n" + st);
            errorHandler.sendEmptyMessage(0);
        }
    }

    private void turnOnOffButtons(boolean status)
    {
        btMenuStart.setEnabled(status);
        btMenuSettings.setEnabled(status);
        btMenuKnowledge.setEnabled(status);
        if (status) {
            btMenuStart.setVisibility(View.VISIBLE);
            btMenuSettings.setVisibility(View.VISIBLE);
            btMenuKnowledge.setVisibility(View.VISIBLE);
        } else {
            btMenuStart.setVisibility(View.INVISIBLE);
            btMenuSettings.setVisibility(View.INVISIBLE);
            btMenuKnowledge.setVisibility(View.INVISIBLE);
        }
    }

    private void checkVersion()
    {
        turnOnOffButtons(false);

        String app_version = dbHelper.getAppVersion();
        if (app_version == null || !app_version.equals(getResources().getString(R.string.app_version))) {
            updateStarted = true;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    autoUpdate();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showVersion();
                            turnOnOffButtons(true);
                            pbUpdate.setProgress(0);
                        }
                    });
                }
            });

            tvVersion.setText(
                String.format(
                    Locale.ROOT,
                    "Обновляю версию: %s => %s",
                    dbHelper.getAppVersion(),
                    getResources().getString(R.string.app_version)
                )
            );

            thread.start();

        } else {
            showVersion();
            turnOnOffButtons(true);
        }
    }

    private void initMenuButtons()
    {
        btMenuStart = findViewById(R.id.btMenuStart);
        btMenuStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                if (gameHelper.getUserId() == 0) {
                    intent = new Intent(getBaseContext(), UsersActivity.class);
                } else {
                    intent = new Intent(getBaseContext(), UserExercisesActivity.class);
                }
                startActivity(intent);
            }
        });

        btMenuSettings = findViewById(R.id.btMenuSettings);
        btMenuSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        btMenuKnowledge = findViewById(R.id.btMenuKnowledge);
        btMenuKnowledge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), KnowledgeCategoriesActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();

        //TODO: temporary fix
        gameHelper.disableReplayMode();

        if (
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                )
        {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE},
                    PackageManager.PERMISSION_GRANTED
            );
        }
        if (!updateStarted) {
            showVersion();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("APP", "onDestroy " + gameHelper.getTodayTimestampString());

        if (!updateStarted) {
            try {
                dbHelper.exportDB("autosave_" + gameHelper.getDayInWeekString() + ".db", false);
                dbHelper.exportDB("autosave.db", false);

                dbHelper.save2file("autosave_" + gameHelper.getDayInWeekString() + ".sif");
                dbHelper.save2file("autosave.sif");
            } catch (Exception e) {
                Log.d("APP", e.toString());
            }
            Toast.makeText(getBaseContext(), "Автосохранение прошло успешно", Toast.LENGTH_LONG).show();
        }

        gameHelper.disableReplayMode();
        gameHelper.removeReplayTimer();
        gameHelper.removeReplayTimerTask();
    }

    @Override
    public void onBackPressed() {
        if (updateStarted) {
            new AlertDialog.Builder(this)
                    .setMessage("Обновление не завершилось, вы уверены?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        } else {
            finish();
        }
    }
}