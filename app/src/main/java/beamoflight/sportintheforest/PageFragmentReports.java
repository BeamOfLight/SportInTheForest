package beamoflight.sportintheforest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/* Fragment used as page 2 */
public class PageFragmentReports extends Fragment {
    private class UserExerciseTrainingStatTimeOption {
        String title;
        int fromField;
        int fromAmount;
        int toField;
        int toAmount;

        UserExerciseTrainingStatTimeOption(String title, int from_field, int from_amount, int to_field, int to_amount) {
            this.title = title;
            this.fromField = from_field;
            this.fromAmount = from_amount;
            this.toField = to_field;
            this.toAmount = to_amount;
        }

        public String toString() {
            return title;
        }
    }

    DBHelper dbHelper;
    GameHelper gameHelper;

    Button btStatCharts, btStatMyShadows;
    Spinner spinnerFragmentPageStatTime;
    TextView tvFragmentPageStatTrainingDays, tvFragmentPageStatAverageResult, tvFragmentPageStatTotalNumberOfMoves;
    TextView tvFragmentPageStatMaxCompetitionResult, tvFragmentPageStatMaxResult, tvFragmentPageStatTotalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_reports, container, false);

        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        spinnerFragmentPageStatTime = rootView.findViewById(R.id.spinnerFragmentPageStatTime);
        tvFragmentPageStatTrainingDays = rootView.findViewById(R.id.tvFragmentPageStatTrainingDays);
        tvFragmentPageStatTotalCount = rootView.findViewById(R.id.tvFragmentPageStatTotalCount);
        tvFragmentPageStatMaxCompetitionResult = rootView.findViewById(R.id.tvFragmentPageStatMaxCompetitionResult);
        tvFragmentPageStatMaxResult = rootView.findViewById(R.id.tvFragmentPageStatMaxResult);
        tvFragmentPageStatTotalNumberOfMoves = rootView.findViewById(R.id.tvFragmentPageStatTotalNumberOfMoves);
        tvFragmentPageStatAverageResult = rootView.findViewById(R.id.tvFragmentPageStatAverageResult);

        btStatCharts = rootView.findViewById(R.id.btStatCharts);
        btStatCharts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StatYearsActivity.class);
                startActivity(intent);
            }
        });

        btStatMyShadows = rootView.findViewById(R.id.btStatMyShadows);
        btStatMyShadows.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StatMyShadowsActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onStart() {
        super.onStart();

//        List<Stat> values = new ArrayList<>();
//        values.add(new Stat(2018, 6, 30));
//        values.add(new Stat(2018, 7, 120));
//        values.add(new Stat(2018, 8, 160));
//        values.add(new Stat(2018, 9, 200));
//        values.add(new Stat(2018, 10, 220));
//        values.add(new Stat(2018, 11, 20));
//        values.add(new Stat(2018, 12, 320));
//        values.add(new Stat(2019, 1, 100));
//
//        StatArrayAdapter statAdapter;
//        statAdapter = new StatArrayAdapter(getContext(), values, 500);
//        lvStat.setAdapter(statAdapter);


        List<UserExerciseTrainingStatTimeOption> timeList = new ArrayList<>();
        timeList.add(new UserExerciseTrainingStatTimeOption("Сегодня", Calendar.DAY_OF_YEAR, -1, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("Вчера", Calendar.DAY_OF_YEAR, -2, Calendar.DAY_OF_YEAR , -1));
        timeList.add(new UserExerciseTrainingStatTimeOption("Позавчера", Calendar.DAY_OF_YEAR, -3, Calendar.DAY_OF_YEAR , -2));
        timeList.add(new UserExerciseTrainingStatTimeOption("Последняя неделя", Calendar.WEEK_OF_YEAR, -1, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("Последние 2 недели", Calendar.WEEK_OF_YEAR, -2, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("Последний месяц", Calendar.MONTH, -1, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("Последние 3 месяца", Calendar.MONTH, -3, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("Последние 6 месяцев", Calendar.MONTH, -6, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("Последний год", Calendar.YEAR, -1, Calendar.DAY_OF_YEAR , 0));
        timeList.add(new UserExerciseTrainingStatTimeOption("За всё время", Calendar.YEAR, -999, Calendar.DAY_OF_YEAR , 0));

        ArrayAdapter<UserExerciseTrainingStatTimeOption> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, timeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFragmentPageStatTime.setAdapter(adapter);
        spinnerFragmentPageStatTime.setSelection(gameHelper.getSharedPreferencesInt("stat_spinner_position", 0));

        spinnerFragmentPageStatTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                UserExerciseTrainingStatTimeOption time_option = (UserExerciseTrainingStatTimeOption) spinnerFragmentPageStatTime.getSelectedItem();
                UserExerciseTrainingStat stat = dbHelper.getUserExerciseTrainingStat(
                        time_option.fromField,
                        time_option.fromAmount,
                        time_option.toField,
                        time_option.toAmount
                );

                tvFragmentPageStatTrainingDays.setText(String.format(Locale.ROOT, "%d", stat.training_days));
                tvFragmentPageStatTotalCount.setText(String.format(Locale.ROOT, "%d", stat.total_cnt));
                tvFragmentPageStatMaxCompetitionResult.setText(String.format(Locale.ROOT, "%d", stat.max_competition_result));
                tvFragmentPageStatMaxResult.setText(String.format(Locale.ROOT, "%d", stat.max_result));
                tvFragmentPageStatTotalNumberOfMoves.setText(String.format(Locale.ROOT, "%d", stat.total_number_of_moves));
                tvFragmentPageStatAverageResult.setText(stat.getAverageResultString());
                gameHelper.setSharedPreferencesInt("stat_spinner_position", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }
}
