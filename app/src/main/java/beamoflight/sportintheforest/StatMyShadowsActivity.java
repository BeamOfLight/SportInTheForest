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

public class StatMyShadowsActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    List<Stat> values = new ArrayList<>();
    ListView lvStat;
    TextView tvExerciseName, tvPosition;
    Spinner spinnerStatMyShadowsActivityType;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_my_shadows);

        dbHelper = new DBHelper( getBaseContext() );
        gameHelper = new GameHelper( getBaseContext() );

        lvStat = findViewById(R.id.lvStat);
        lvStat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(StatMyShadowsActivity.this, StatDaysActivity.class);
                intent.setAction(Integer.toString(values.get(position).year * 100 + values.get(position).month));
                startActivity(intent);
            }
        });

        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvPosition = findViewById(R.id.tvPosition);
        spinnerStatMyShadowsActivityType = findViewById(R.id.spinnerStatMyShadowsActivityType);
    }

    public void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));

        List<StatTypeOption> typeList = new ArrayList<>();
        typeList.add(new StatTypeOption("Суммарный результат", StatTypeOption.TYPE_RESULT));
        if (dbHelper.isUserExerciseTypeRPG()) {
            typeList.add(new StatTypeOption("Суммарный опыт", StatTypeOption.TYPE_EXP));
        }

        ArrayAdapter<StatTypeOption> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatMyShadowsActivityType.setAdapter(adapter);
        spinnerStatMyShadowsActivityType.setSelection(0);

        spinnerStatMyShadowsActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int currentValuePosition = 0;
                StatTypeOption type_option = (StatTypeOption) spinnerStatMyShadowsActivityType.getSelectedItem();

                int max_result = 0;
                switch (type_option.type) {
                    case StatTypeOption.TYPE_RESULT:
                        max_result = dbHelper.getCurrentUserExerciseMaxMonthSumResult(0);
                        values = dbHelper.getCurrentUserExerciseStatShadowMonthsSumResult();
                        break;
                    case StatTypeOption.TYPE_EXP:
                        max_result = dbHelper.getCurrentUserExerciseMaxMonthSumExp(0);
                        values = dbHelper.getCurrentUserExerciseStatShadowMonthsSumExp();
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
                tvPosition.setText(
                    String.format(
                        Locale.ROOT,
                        "Вы занимаете %d-е место из %d",
                        currentValuePosition,
                        values.size()
                    )
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

}