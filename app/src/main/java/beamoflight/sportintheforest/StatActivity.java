package beamoflight.sportintheforest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class StatActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    ListView lvStat;
    TextView tvExerciseName;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        lvStat = findViewById(R.id.lvStat);

        int max_result = dbHelper.getCurrentUserExerciseMaxMonthlySumResult();
        List<Stat> values = dbHelper.getCurrentUserExerciseStat();

        StatArrayAdapter statAdapter;
        statAdapter = new StatArrayAdapter(getBaseContext(), values, max_result);
        lvStat.setAdapter(statAdapter);
        tvExerciseName = findViewById(R.id.tvExerciseName);
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
    }

}