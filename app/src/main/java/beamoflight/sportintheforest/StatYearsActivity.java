package beamoflight.sportintheforest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatYearsActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    ListView lvStatYears;
    TextView tvExerciseName, tvPosition;
    Spinner spinner;
    List<Stat> values = new ArrayList<>();

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_years);

        dbHelper = new DBHelper(getBaseContext());
        gameHelper = new GameHelper(getBaseContext());

        lvStatYears = findViewById(R.id.lvStatYears);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        spinner = findViewById(R.id.spinnerStatYearsActivityType);

        lvStatYears.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                initDialogSelectStatType(values.get(position).year);
            }
        });
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

    private void initDialogSelectStatType(int year)
    {
        final String current_year_str = Integer.toString(year);
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_select_stat_type, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        TextView tvSelectStatTypeTitle = prompts_view.findViewById(R.id.tvSelectStatTypeTitle);
        tvSelectStatTypeTitle.setText(current_year_str);

        Button btSelectStatTypeMonths = prompts_view.findViewById(R.id.btSelectStatTypeMonths);
        btSelectStatTypeMonths.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StatYearsActivity.this, StatMonthsActivity.class);
                intent.setAction(current_year_str);
                startActivity(intent);
            }
        });

        Button btSelectStatTypeWeeks = prompts_view.findViewById(R.id.btSelectStatTypeWeeks);
        btSelectStatTypeWeeks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StatYearsActivity.this, StatWeeksActivity.class);
                intent.setAction(current_year_str);
                startActivity(intent);
            }
        });

        // set dialog message
        alert_dialog_builder.setCancelable(true);

        // create alert dialog
        AlertDialog dialog = alert_dialog_builder.create();
        dialog.show();
        //dialog.cancel();
    }

}