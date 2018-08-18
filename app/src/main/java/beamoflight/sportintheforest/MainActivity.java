package beamoflight.sportintheforest;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    TextView tvVersion;
    Button btMenuStart, btMenuSettings, btMenuKnowledge;
    String app_version;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        app_version = gameHelper.getSharedPreferencesString("app_version", null);
        tvVersion = (TextView) findViewById(R.id.tvVersion);

        initMenuButtons();
        checkVersion();
    }

    private void showVersion()
    {
        tvVersion.setText(
            String.format(Locale.ROOT, "ver. %s", getResources().getString(R.string.app_version))
        );
        btMenuStart.setEnabled(true);
        btMenuSettings.setEnabled(true);
        btMenuKnowledge.setEnabled(true);
    }

    private void autoUpdate()
    {
        dbHelper.save2file("autosave.sif");
        dbHelper.customOnCreate();
        dbHelper.loadFromFile("autosave.sif");

        gameHelper.setSharedPreferencesString(
            "app_version",
            getResources().getString(R.string.app_version)
        );
    }

    private void checkVersion()
    {
        btMenuStart.setEnabled(false);
        btMenuSettings.setEnabled(false);
        btMenuKnowledge.setEnabled(false);

        String app_version = gameHelper.getSharedPreferencesString("app_version", null);
        if (!app_version.equals(getResources().getString(R.string.app_version))) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    autoUpdate();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showVersion();
                        }
                    });
                }
            });

            tvVersion.setText(
                String.format(
                    Locale.ROOT,
                    "Обновляю версию: %s => %s",
                    gameHelper.getSharedPreferencesString("app_version", null),
                    getResources().getString(R.string.app_version)
                )
            );

            thread.start();
        } else {
            showVersion();
        }
    }

    private void initMenuButtons()
    {
        btMenuStart = (Button) findViewById(R.id.btMenuStart);
        btMenuStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });

        btMenuSettings = (Button) findViewById(R.id.btMenuSettings);
        btMenuSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btMenuKnowledge = (Button) findViewById(R.id.btMenuKnowledge);
        btMenuKnowledge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KnowledgeActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();

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
    }
}