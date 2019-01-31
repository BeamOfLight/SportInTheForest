package beamoflight.sportintheforest;

import android.app.Activity;
import android.os.Bundle;

public abstract class ReplayActivity extends Activity {
    protected DBHelper dbHelper;
    protected GameHelper gameHelper;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );
    }

    protected void onStart() {
        super.onStart();

        gameHelper.startReplay(this);
    }

    @Override
    public void onBackPressed() {
        if (gameHelper.isReplayMode()) {
            gameHelper.disableReplayMode(true);
            gameHelper.removeReplayTimerTask();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameHelper.removeReplayTimer();
        gameHelper.removeReplayTimerTask();
    }

    abstract public void replayEvent1();
    abstract public void replayEvent2();
    abstract public void replayEvent3();
}
