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
import java.util.Locale;

public class StatDaysActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    ListView lvStatDays;
    TextView tvExerciseName, tvYearWithMonth;
    Spinner spinner;
    int year;
    int month;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_days);

        dbHelper = new DBHelper(getBaseContext());
        gameHelper = new GameHelper(getBaseContext());

        int action = Integer.parseInt(this.getIntent().getAction());
        month = action % 100;
        year = (action - month) / 100;

        lvStatDays = findViewById(R.id.lvStatDays);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvYearWithMonth = findViewById(R.id.tvYearWithMonth);
        spinner = findViewById(R.id.spinnerStatDaysActivityType);
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
        tvYearWithMonth.setText(String.format(Locale.ROOT, "%s %d", gameHelper.getMonthName(month), year));

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
                        max_result = dbHelper.getCurrentUserExerciseMaxDaySumResult(year, month);
                        values = dbHelper.getCurrentUserExerciseStatDaysSumResult(year, month);
                        break;
                    case StatTypeOption.TYPE_EXP:
                        max_result = dbHelper.getCurrentUserExerciseMaxDaySumExp(year, month);
                        values = dbHelper.getCurrentUserExerciseStatDaysSumExp(year, month);
                        break;
                }

                StatDayArrayAdapter statAdapter;
                statAdapter = new StatDayArrayAdapter(getBaseContext(), values, max_result);
                lvStatDays.setAdapter(statAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

}