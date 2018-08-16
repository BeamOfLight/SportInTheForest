package beamoflight.sportintheforest;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        initMenuButtons();
        showVersion();

        DBHelper dbHelper = new DBHelper( getBaseContext() );

        //TODO: DB version. Update if version changes
        dbHelper.recreateCommonTable();
    }

    private void showVersion()
    {
        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText(
                String.format(
                        Locale.ROOT,
                        "%s",
                        getResources().getString(R.string.app_version)
                )
        );
    }

    private void initMenuButtons()
    {
        Button btMenuStart = (Button) findViewById(R.id.btMenuStart);
        btMenuStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });

        Button btMenuSettings = (Button) findViewById(R.id.btMenuSettings);
        btMenuSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button btMenuKnowledge = (Button) findViewById(R.id.btMenuKnowledge);
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