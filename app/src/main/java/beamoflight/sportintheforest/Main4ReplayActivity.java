package beamoflight.sportintheforest;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Locale;

public class Main4ReplayActivity extends ReplayActivity {
    TextView tvVersion;
    ProgressBar pbUpdate;
    String app_version;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        pbUpdate = findViewById(R.id.pbUpdate);
        pbUpdate.setProgress(0);

        app_version = dbHelper.getAppVersion();
        tvVersion = findViewById(R.id.tvVersion);
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

    protected void onStart() {
        super.onStart();

        showVersion();
    }

    public void replayEvent1()
    {

    }

    @Override
    public void replayEvent2()
    {

    }

    @Override
    public void replayEvent3()
    {

    }
}