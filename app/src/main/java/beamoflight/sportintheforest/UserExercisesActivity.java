package beamoflight.sportintheforest;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class UserExercisesActivity extends Activity {

    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Map<String, String>> exercisesData;
    ListView lvExercises;
    TextView tvExercisesListInfo;

    FloatingActionButton fabAddUserExercise;
    //Button btAddUserExercise;
    AlertDialog dialogAddUserExercise;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_exercises);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(this );

        TextView tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(dbHelper.getUserNameById(gameHelper.getUserId()));
        tvExercisesListInfo = findViewById(R.id.tvExercisesListInfo);

        initExercisesListView();

    }

    protected void onStart() {
        super.onStart();

        initExercisesListView();
        initFABAddUser();
    }

    private void initFABAddUser()
    {
        fabAddUserExercise = (FloatingActionButton) findViewById(R.id.fabAddUserExercise);
        fabAddUserExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogAddUserExercise();
                dialogAddUserExercise.show();
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
                                int exerciseId = ((ExerciseEntity) spinnerAddUserExercise.getSelectedItem()).getId();
                                int type = dbHelper.USER_EXERCISE_TYPE_RPG;
                                if (cbDailyStatOnly.isChecked()) {
                                    type = dbHelper.USER_EXERCISE_TYPE_DAILY_STAT_ONLY;
                                }
                                dbHelper.createUserExercise(gameHelper.getUserId(), exerciseId, type);
                                dbHelper.openLocationForUserExercise(gameHelper.getUserId(), exerciseId, 1);

                                showExercisesList();
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialogAddUserExercise = alert_dialog_builder.create();
    }

    private void initExercisesListView()
    {
        lvExercises = findViewById(R.id.lvExercises);
        lvExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int exercise_id = Integer.parseInt(exercisesData.get(position).get("exercise_id"));
                gameHelper.saveExerciseId2Preferences(exercise_id);

                Intent intent = null;
                Map<String, String> data = dbHelper.getCurrentUserExerciseData();
                int user_exercise_type = Integer.parseInt(data.get("type"));
                if (user_exercise_type == dbHelper.USER_EXERCISE_TYPE_RPG) {
                    intent = new Intent(UserExercisesActivity.this, TabsActivity.class);
                } else if (user_exercise_type == dbHelper.USER_EXERCISE_TYPE_DAILY_STAT_ONLY) {
                    intent = new Intent(UserExercisesActivity.this, TabsDailyStatOnlyActivity.class);
                }
                startActivity(intent);
            }
        });

        lvExercises.setFooterDividersEnabled(true);
        showExercisesList();
    }

    private void showExercisesList()
    {
        exercisesData = dbHelper.getUserExercisesData();
        lvExercises.invalidateViews();
        SimpleAdapter exercisesAdapter = new SimpleAdapter(
                this,
                exercisesData,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "info", "exercise_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvExercises.setAdapter(exercisesAdapter);
        String exercises_list_info = "";
        if (exercisesData.size() == 0) {
            exercises_list_info = "У вас пока нет активных упражнений";
        }
        tvExercisesListInfo.setText(exercises_list_info);
    }
}