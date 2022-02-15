package beamoflight.sportintheforest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExercisesActivity extends ReplayActivity {

    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Map<String, String>> exercisesData;

    TextView tvExercisesListInfo;
    ListView lvExercises, lvNewUserExercise;

    AlertDialog dialogAddOrEditExercise;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_lists_std1);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(this.getBaseContext());


        ((TextView) findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.exercises_title));

        initUsersListView();
        initNewUserExerciseListView();
    }

    protected void onStart() {
        super.onStart();

        showExercisesList();
        showNewUserExerciseList();
    }

    private void initNewUserExerciseListView()
    {
        lvNewUserExercise = findViewById(R.id.lvItemsTop);
        lvNewUserExercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!gameHelper.isReplayMode()) {
                    initDialogAddOrEditExercise(-1);
                    dialogAddOrEditExercise.show();
                }
            }
        });
    }

    private void initUsersListView()
    {
        lvExercises = findViewById(R.id.lvItemsBottom);
        lvExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!gameHelper.isReplayMode()) {
                    int exercise_id = Integer.parseInt(exercisesData.get(position).get("exercise_id"));
                    initDialogAddOrEditExercise(exercise_id);
                    dialogAddOrEditExercise.show();
                }
            }
        });
    }

    private void showExercisesList()
    {
        exercisesData = dbHelper.getExercisesData();
        lvExercises.invalidateViews();
        SimpleAdapter exercisesAdapter = new SimpleAdapter(
                this,
                exercisesData,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "info", "exercise_id", "difficulty"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvExercises.setAdapter(exercisesAdapter);
    }

    private void showNewUserExerciseList()
    {
        lvNewUserExercise.invalidateViews();
        List<String> items = new ArrayList<>();
        items.add("Новое упражнение");
        SingleLineItemArrayAdapter itemsAdapter = new SingleLineItemArrayAdapter(this,items);

        lvNewUserExercise.setAdapter(itemsAdapter);
    }

    private void initDialogAddOrEditExercise(int current_exercise_id)
    {
        // get prompt_add_group.xmlgroup.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_add_exercise, null);
        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        final NumberPicker np_difficulty = (NumberPicker) prompts_view.findViewById(R.id.npDifficulty);
        np_difficulty.setMinValue(1);
        np_difficulty.setMaxValue(100);

        final EditText et_input_exercise_name = prompts_view.findViewById(R.id.editTextDialogExerciseInput);

        final int exercise_id = current_exercise_id;
        int difficulty;
        if (current_exercise_id != -1) {
            et_input_exercise_name.setText(dbHelper.getExerciseName(current_exercise_id));
            difficulty = dbHelper.getExerciseDifficulty(current_exercise_id);
        } else {
            et_input_exercise_name.setText("");
            difficulty = 1;
        }

        np_difficulty.setValue(difficulty);

        // set dialog message
        alert_dialog_builder
            .setCancelable(false)
            .setPositiveButton(R.string.btn_save_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (!gameHelper.isReplayMode()) {
                            String exercise_name = et_input_exercise_name.getText().toString();
                            if (exercise_name.trim().length() == 0) {
                                Toast.makeText(getBaseContext(), R.string.msg_exercise_need_name, Toast.LENGTH_SHORT).show();
                            } else if (dbHelper.checkExerciseExist(exercise_name, current_exercise_id)) {
                                Toast.makeText(getBaseContext(), R.string.msg_exercise_name_already_exists, Toast.LENGTH_SHORT).show();
                            } else if (exercise_id == -1) {
                                long result = dbHelper.addExercise(exercise_name, np_difficulty.getValue());
                                if (result > 0) {
                                    showExercisesList();
                                    Toast.makeText(getBaseContext(), R.string.msg_exercise_add_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), R.string.msg_exercise_add_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                long result = dbHelper.updateExercise(exercise_id, exercise_name, np_difficulty.getValue());
                                if (result > 0) {
                                    showExercisesList();
                                    Toast.makeText(getBaseContext(), R.string.msg_exercise_edit_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), R.string.msg_exercise_edit_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                })
            .setNegativeButton(R.string.btn_cancel_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (!gameHelper.isReplayMode()) {
                            dialog.cancel();
                        }
                    }
                });

        // create alert dialog
        dialogAddOrEditExercise = alert_dialog_builder.create();
    }

    @Override
    public void replayEvent1()
    {
        initDialogAddOrEditExercise(-1);
        dialogAddOrEditExercise.show();
    }

    @Override
    public void replayEvent2()
    {

    }

    @Override
    public void replayEvent3()
    {

    }
}