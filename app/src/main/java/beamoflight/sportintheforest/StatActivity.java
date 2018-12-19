package beamoflight.sportintheforest;

import android.app.Activity;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class StatActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;
    int position;

    ListView lvStat;
    TextView tvExerciseName, tvPosition;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        lvStat = findViewById(R.id.lvStat);
        position = 0;

        int max_result = dbHelper.getCurrentUserExerciseMaxMonthlySumResult();
        List<Stat> values = dbHelper.getCurrentUserExerciseStat();
        for (Stat value : values) {
            if (value.isCurrentPeriod()) {
                position = value.getPosition();
            }
        }

        StatArrayAdapter statAdapter;
        statAdapter = new StatArrayAdapter(getBaseContext(), values, max_result);
        lvStat.setAdapter(statAdapter);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvPosition = findViewById(R.id.tvPosition);
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
        tvPosition.setText(String.format("Вы занимаете %d-е место.", position));
    }

}