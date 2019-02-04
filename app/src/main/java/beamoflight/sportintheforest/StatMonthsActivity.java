package beamoflight.sportintheforest;

import android.app.Activity;
import android.content.Intent;
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

public class StatMonthsActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Stat> values = new ArrayList<>();

    ListView lvStatMonths;
    TextView tvExerciseName, tvYear;
    Spinner spinner;
    int year;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_months);

        dbHelper = new DBHelper(getBaseContext());
        gameHelper = new GameHelper(getBaseContext());
        year = Integer.parseInt(this.getIntent().getAction());

        lvStatMonths = findViewById(R.id.lvStatMonths);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvYear = findViewById(R.id.tvYear);
        spinner = findViewById(R.id.spinnerStatMonthsActivityType);

        lvStatMonths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(StatMonthsActivity.this, StatDaysActivity.class);
                intent.setAction(Integer.toString(values.get(position).year * 100 + values.get(position).month));
                startActivity(intent);
            }
        });
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
        tvYear.setText(String.format(Locale.ROOT, "%d", year));

        List<StatTypeOption> typeList = new ArrayList<>();
        typeList.add(new StatTypeOption("Суммарный результат", StatTypeOption.TYPE_RESULT));
        if (dbHelper.isUserExerciseTypeRPG()) {
            typeList.add(new StatTypeOption("Суммарный опыт", StatTypeOption.TYPE_EXP));
        }

        ArrayAdapter<StatTypeOption> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                StatTypeOption type_option = (StatTypeOption) spinner.getSelectedItem();

                int max_result = 0;
                switch (type_option.type) {
                    case StatTypeOption.TYPE_RESULT:
                        max_result = dbHelper.getCurrentUserExerciseMaxMonthSumResult(year);
                        values = dbHelper.getCurrentUserExerciseStatMonthsSumResult(year);
                        break;
                    case StatTypeOption.TYPE_EXP:
                        max_result = dbHelper.getCurrentUserExerciseMaxMonthSumExp(year);
                        values = dbHelper.getCurrentUserExerciseStatMonthsSumExp(year);
                        break;
                }

                StatMonthArrayAdapter statAdapter;
                statAdapter = new StatMonthArrayAdapter(getBaseContext(), values, max_result);
                lvStatMonths.setAdapter(statAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

}