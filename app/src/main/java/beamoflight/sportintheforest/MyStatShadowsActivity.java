package beamoflight.sportintheforest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyStatShadowsActivity extends Activity {
    private class MyStatShadowsActivityTypeOption {
        String title;
        int type;

        final static int TYPE_RESULT = 1;
        final static int TYPE_EXP = 2;

        MyStatShadowsActivityTypeOption(String title, int type) {
            this.title = title;
            this.type = type;
        }

        public String toString() {
            return title;
        }
    }

    DBHelper dbHelper;
    GameHelper gameHelper;

    ListView lvStat;
    TextView tvExerciseName, tvPosition;
    Spinner spinnerMyStatShadowsActivityType;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        lvStat = findViewById(R.id.lvStat);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvPosition = findViewById(R.id.tvPosition);
        spinnerMyStatShadowsActivityType = findViewById(R.id.spinnerMyStatShadowsActivityType);
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));

        List<MyStatShadowsActivityTypeOption> typeList = new ArrayList<>();
        typeList.add(new MyStatShadowsActivityTypeOption("Суммарный результат", MyStatShadowsActivityTypeOption.TYPE_RESULT));
        typeList.add(new MyStatShadowsActivityTypeOption("Суммарный опыт", MyStatShadowsActivityTypeOption.TYPE_EXP));

        ArrayAdapter<MyStatShadowsActivityTypeOption> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMyStatShadowsActivityType.setAdapter(adapter);
        spinnerMyStatShadowsActivityType.setSelection(0);

        spinnerMyStatShadowsActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int currentValuePosition = 0;
                MyStatShadowsActivityTypeOption type_option = (MyStatShadowsActivityTypeOption) spinnerMyStatShadowsActivityType.getSelectedItem();

                int max_result = 0;
                List<Stat> values = new ArrayList<>();
                switch (type_option.type) {
                    case MyStatShadowsActivityTypeOption.TYPE_RESULT:
                        max_result = dbHelper.getCurrentUserExerciseMaxMonthlySumResult();
                        values = dbHelper.getCurrentUserExerciseStatMonthSumResult();
                        break;
                    case MyStatShadowsActivityTypeOption.TYPE_EXP:
                        max_result = dbHelper.getCurrentUserExerciseMaxMonthlySumExp();
                        values = dbHelper.getCurrentUserExerciseStatMonthSumExp();
                        break;
                }

                for (Stat value : values) {
                    if (value.isCurrentPeriod()) {
                        currentValuePosition = value.getPosition();
                    }
                }

                StatArrayAdapter statAdapter;
                statAdapter = new StatArrayAdapter(getBaseContext(), values, max_result);
                lvStat.setAdapter(statAdapter);
                tvPosition.setText(String.format(Locale.ROOT,"Вы занимаете %d-е место.", currentValuePosition));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

}