package beamoflight.sportintheforest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserExercisesActivity extends ReplayActivity {
    List<Map<String, String>> userExercisesData;
    ListView lvExercises, lvNewUserExercise;
    AlertDialog dialogAddUserExercise;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_lists_std1);
    }

    protected void onStart() {
        super.onStart();

        ((TextView) findViewById(R.id.tvTitle)).setText(dbHelper.getUserNameById(gameHelper.getUserId()));

        initNewUserListView();
        initExercisesListView();
    }

    private void initNewUserListView()
    {
        lvNewUserExercise = findViewById(R.id.lvItemsTop);
        lvNewUserExercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!gameHelper.isReplayMode()) {
                    switch (position) {
                        case 0:
                            initDialogAddUserExercise();
                            dialogAddUserExercise.show();
                            break;
                        case 1:
                            Intent intent = new Intent(getBaseContext(), UsersActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            }
        });
    }

    private void initDialogAddUserExercise()
    {
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_add_user_exercise, null);
        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        final Spinner spinnerAddUserExercise = prompts_view.findViewById(R.id.spinnerAddUserExercise);
        ArrayAdapter<ExerciseEntity> spinnerAddUserExerciseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getExercises(gameHelper.getUserId()));
        spinnerAddUserExerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddUserExercise.setAdapter(spinnerAddUserExerciseAdapter);

        final CheckBox cbDailyStatOnly = prompts_view.findViewById(R.id.cbDailyStatOnly);

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (!gameHelper.isReplayMode()) {
                                    int exerciseId = ((ExerciseEntity) spinnerAddUserExercise.getSelectedItem()).getId();
                                    int type = dbHelper.USER_EXERCISE_TYPE_RPG;
                                    if (cbDailyStatOnly.isChecked()) {
                                        type = dbHelper.USER_EXERCISE_TYPE_DAILY_STAT_ONLY;
                                    }
                                    dbHelper.addUserExercise(gameHelper.getUserId(), exerciseId, type);
                                    dbHelper.openLocationForUserExercise(gameHelper.getUserId(), exerciseId, 1);

                                    showUserExercisesList();
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
        dialogAddUserExercise = alert_dialog_builder.create();
    }

    private void initExercisesListView()
    {
        lvExercises = findViewById(R.id.lvItemsBottom);
        lvExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!gameHelper.isReplayMode()) {
                    int exercise_id = Integer.parseInt(userExercisesData.get(position).get("exercise_id"));
                    gameHelper.saveExerciseId2Preferences(exercise_id);

                    Map<String, String> data = dbHelper.getCurrentUserExerciseData();
                    if (data != null) {
                        int user_exercise_type = Integer.parseInt(data.get("type"));
                        if (user_exercise_type == dbHelper.USER_EXERCISE_TYPE_RPG) {
                            if (dbHelper.getExerciseDifficulty(exercise_id) > 0) {
                                startActivity(
                                        new Intent(UserExercisesActivity.this, TabsActivity.class));
                            } else {
                                Toast.makeText(
                                        getBaseContext(),"Укажите сложность выбранного упражнения в его настройках. ",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } else if (user_exercise_type == dbHelper.USER_EXERCISE_TYPE_DAILY_STAT_ONLY) {
                            startActivity(
                                    new Intent(UserExercisesActivity.this, TabsDailyStatOnlyActivity.class));
                        }
                    }
                }
            }
        });

        showUserExercisesList();
        showNewUserExerciseList();
    }

    private void showUserExercisesList()
    {
        userExercisesData = dbHelper.getUserExercisesData(-1);
        //final ArrayList<Map<String, String>> userExercisesData = dbHelper.getUserExercisesData(-1);
        lvExercises.invalidateViews();
        SimpleAdapter userExercisesAdapter = new SimpleAdapter(
                this,
                userExercisesData,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "info", "exercise_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvExercises.setAdapter(userExercisesAdapter);
    }

    private void showNewUserExerciseList()
    {
        lvNewUserExercise.invalidateViews();
        List<String> items = new ArrayList<>();
        items.add("Новое упражнение");
        items.add("Выбрать другого пользователя");
        SingleLineItemArrayAdapter itemsAdapter = new SingleLineItemArrayAdapter(this, items);

        lvNewUserExercise.setAdapter(itemsAdapter);
    }

    @Override
    public void replayEvent1()
    {
        initDialogAddUserExercise();
        dialogAddUserExercise.show();
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