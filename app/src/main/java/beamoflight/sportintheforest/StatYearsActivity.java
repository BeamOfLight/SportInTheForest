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
import java.util.List;

public class StatYearsActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    ListView lvStatYears;
    TextView tvExerciseName, tvPosition;
    Spinner spinner;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_years);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        lvStatYears = findViewById(R.id.lvStatYears);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        spinner = findViewById(R.id.spinnerStatYearsActivityType);
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));

        List<StatTypeOption> typeList = new ArrayList<>();
        typeList.add(new StatTypeOption("Суммарный результат", StatTypeOption.TYPE_RESULT));
        typeList.add(new StatTypeOption("Суммарный опыт", StatTypeOption.TYPE_EXP));

        ArrayAdapter<StatTypeOption> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                StatTypeOption type_option = (StatTypeOption) spinner.getSelectedItem();

                int max_result = 0;
                List<Stat> values = new ArrayList<>();
                switch (type_option.type) {
                    case StatTypeOption.TYPE_RESULT:
                        max_result = dbHelper.getCurrentUserExerciseMaxYearSumResult();
                        values = dbHelper.getCurrentUserExerciseStatYearsSumResult();
                        break;
                    case StatTypeOption.TYPE_EXP:
                        max_result = dbHelper.getCurrentUserExerciseMaxYearSumExp();
                        values = dbHelper.getCurrentUserExerciseStatYearsSumExp();
                        break;
                }

                StatYearArrayAdapter statAdapter;
                statAdapter = new StatYearArrayAdapter(getBaseContext(), values, max_result);
                lvStatYears.setAdapter(statAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

}