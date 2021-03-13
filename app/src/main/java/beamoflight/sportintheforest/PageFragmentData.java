package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* Fragment used as page 2 */
public class PageFragmentData extends Fragment {
    DBHelper dbHelper;
    GameHelper gameHelper;

    AlertDialog dialogAddDayInfo;

    Button btAddTraining;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_data, container, false);

        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        btAddTraining = rootView.findViewById(R.id.btAddTraining);
        btAddTraining.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogAddDayInfo();
            }
        });

        return rootView;
    }

    private void initDialogAddDayInfo()
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View prompts_view = li.inflate(R.layout.prompt_add_day_info, null);


        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(getContext());
        alert_dialog_builder.setView(prompts_view);

        final NumberPicker npCount = prompts_view.findViewById(R.id.npSaladsCount);
        final CalendarView cvDate = prompts_view.findViewById(R.id.cvDate);
        final android.icu.util.Calendar calendar = new GregorianCalendar();

        cvDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);

                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                String selectedDate = date_format.format(calendar.getTime());

                Map<String, String> m = dbHelper.getFirstTrainingByDate(gameHelper.getUserId(), gameHelper.getExerciseId(), selectedDate);
                int sum_result = Integer.parseInt(m.get("sum_result"));
                npCount.setValue(sum_result);
            }
        });

        Map<String, String> m = dbHelper.getFirstTrainingByDate(gameHelper.getUserId(), gameHelper.getExerciseId(), gameHelper.getTodayString());
        int sum_result = Integer.parseInt(m.get("sum_result"));

        npCount.setMinValue(0);
        npCount.setMaxValue(100);
        npCount.setValue(sum_result);

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                                String selectedDate = date_format.format(calendar.getTime());

                                int result = npCount.getValue();
                                Map<String, String> m = dbHelper.getFirstTrainingByDate(gameHelper.getUserId(), gameHelper.getExerciseId(), selectedDate);
                                int training_id = Integer.parseInt(m.get("training_id"));
                                if (training_id == 0) {
                                    dbHelper.addTraining(gameHelper.getUserId(), gameHelper.getExerciseId(), 0, result, result, 0, 1, GameHelper.RESULT_STATE_WIN, 0,0, 0, false, 0, 0, selectedDate);
                                } else {
                                    dbHelper.updateTrainingResult(
                                            training_id,
                                            gameHelper.getUserId(),
                                            gameHelper.getExerciseId(),
                                            result
                                    );
                                }
                                startActivity(gameHelper.getIntent4refreshedView(getActivity(), 1));
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialogAddDayInfo = alert_dialog_builder.create();
        dialogAddDayInfo.show();
    }


    public void onStart() {
        super.onStart();

    }
}
