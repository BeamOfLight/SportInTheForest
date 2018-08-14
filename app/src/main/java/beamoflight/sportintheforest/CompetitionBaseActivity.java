package beamoflight.sportintheforest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


abstract public class CompetitionBaseActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;
    NetworkHelper networkHelper;
    TextView tvExerciseName;
    ListView lvCompetitionTeamLeft, lvCompetitionTeamRight;
    ListView lvCompetitionLog;
    Button btCompetitionStart, btCompetitionRestart, btFinish, btAddResult, btInvite, btNextLocation;
    Button btTranslation;
    CompetitionView competitionView;
    String exitMessage;
    String inviteCode;
    boolean isInternetMode = false;
    Handler createCompetitionResponseHandler;
    Handler createCompetitionResponseErrorHandler;

    protected void setButtonsBeforeCompetitions()
    {
        btCompetitionStart.setVisibility(View.VISIBLE);
        btCompetitionRestart.setVisibility(View.INVISIBLE);
        btInvite.setVisibility(View.INVISIBLE);
        btNextLocation.setVisibility(View.INVISIBLE);
        btFinish.setVisibility(View.INVISIBLE);
        btAddResult.setVisibility(View.INVISIBLE);
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.competition);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(getBaseContext());
        networkHelper = new NetworkHelper(getBaseContext());
        competitionView = new CompetitionView();

        btCompetitionStart = (Button) findViewById(R.id.btCompetitionStart);
        btCompetitionRestart = (Button) findViewById(R.id.btCompetitionRestart);
        btInvite = (Button) findViewById(R.id.btInvite);
        btNextLocation = (Button) findViewById(R.id.btNextLocation);
        btFinish = (Button) findViewById(R.id.btFinish);
        btAddResult = (Button) findViewById(R.id.btAddResult);
        btTranslation = (Button) findViewById(R.id.btTranslation);
        lvCompetitionLog = (ListView) findViewById(R.id.lvCompetitionLog);
        lvCompetitionTeamLeft = (ListView) findViewById(R.id.lvCompetitionTeamLeft);
        lvCompetitionTeamRight = (ListView) findViewById(R.id.lvCompetitionTeamRight);
        tvExerciseName = (TextView) findViewById(R.id.tvExerciseName);

        inviteCode = "";
        setButtonsBeforeCompetitions();

        initStartButton();
        btFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btTranslation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isInternetMode = true;
                btTranslation.setVisibility(View.INVISIBLE);
                networkCreateCompetition();
            }
        });

        createCompetitionResponseHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                setExerciseNameWithInviteCode();
            }
        };

        createCompetitionResponseErrorHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Toast.makeText(
                        getBaseContext(),
                        "При подключении к серверу возникла ошибка!",
                        Toast.LENGTH_LONG
                ).show();
            }
        };
    }

    private void setExerciseNameWithInviteCode()
    {
        String str;
        if (inviteCode.equals("")) {
            str = String.format(Locale.ROOT,"%s", competitionView.exerciseName);
        } else {
            str = String.format(Locale.ROOT,"%s [%s]", competitionView.exerciseName, inviteCode);
        }
        tvExerciseName.setText(str);
    }

    private void networkCreateCompetition()
    {
        try {
            //networkHelper.getClient().newCall(networkHelper.getGetCompetitionInfo("AAAA3214AVVD")).enqueue(new Callback() {
            networkHelper.getClient().newCall(networkHelper.getCreateCompetitionRequest(competitionView, dbHelper.getCurrentPlayerEntity().getName())).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //Message msg = new Message();
                    //msg.obj = e.toString();
                    //mainHandler.sendMessage(msg);
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json_string = response.body().string();
                    try {
                        NetworkHelper.CreateCompetitionResponse response_object = (new Gson()).fromJson(json_string, NetworkHelper.CreateCompetitionResponse.class);
                        //Message msg = new Message();
                        inviteCode = response_object.inviteCode;
                        //createCompetitionResponseHandler.sendMessage(msg);
                        createCompetitionResponseHandler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        createCompetitionResponseErrorHandler.sendEmptyMessage(0);
//                        Message msg = new Message();
//                        msg.obj = e.toString();
//                        createCompetitionResponseHandler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();

        lvCompetitionTeamLeft.setFooterDividersEnabled(true);
        lvCompetitionTeamLeft.setMinimumHeight(50 * competitionView.getTeamsData().get(CompetitionEngine.LEFT_TEAM_IDX).size());
        lvCompetitionTeamLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initDialogShowUserInfo(competitionView.getTeamsData().get(CompetitionEngine.LEFT_TEAM_IDX).get(position));
            }
        });

        lvCompetitionTeamRight.setFooterDividersEnabled(true);
        lvCompetitionTeamRight.setMinimumHeight(50 * competitionView.getTeamsData().get(CompetitionEngine.RIGHT_TEAM_IDX).size());
        lvCompetitionTeamRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initDialogShowUserInfo(competitionView.getTeamsData().get(CompetitionEngine.RIGHT_TEAM_IDX).get(position));
            }
        });
        refreshView();
        setExerciseNameWithInviteCode();
    }
    abstract protected void initStartButton();

    protected void refreshView()
    {
        showCompetitionLogListView();

        CharacterViewArrayAdapter teamLeftArrayAdapter;
        teamLeftArrayAdapter = new CharacterViewArrayAdapter(getBaseContext(), competitionView.getTeamsData().get(CompetitionEngine.LEFT_TEAM_IDX));
        lvCompetitionTeamLeft.setAdapter(teamLeftArrayAdapter);
        teamLeftArrayAdapter.notifyDataSetChanged();

        CharacterViewArrayAdapter teamRightArrayAdapter;
        teamRightArrayAdapter = new CharacterViewArrayAdapter(getBaseContext(), competitionView.getTeamsData().get(CompetitionEngine.RIGHT_TEAM_IDX));
        lvCompetitionTeamRight.setAdapter(teamRightArrayAdapter);
        teamRightArrayAdapter.notifyDataSetChanged();

        //tvExerciseName.setText(competitionView.exerciseName);
    }

    private void initDialogShowUserInfo(CharacterView character_entity)
    {
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_character_info, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        TextView tvCharacterName = (TextView) prompts_view.findViewById(R.id.tvCharacterName);
        tvCharacterName.setText(character_entity.getName());

        TextView tvCharacterFitnessPoints = (TextView) prompts_view.findViewById(R.id.tvCharacterFitnessPoints);
        tvCharacterFitnessPoints.setText(
                String.format(
                        Locale.ROOT,
                        "%d / %d",
                        character_entity.getCurrentFitnessPoints(),
                        character_entity.getInitialFitnessPoints()
                )
        );

        TextView tvCharacterResistance = (TextView) prompts_view.findViewById(R.id.tvCharacterResistance);
        tvCharacterResistance.setText(
                String.format(
                        Locale.ROOT,
                        "%d ( %.1f%% )",
                        character_entity.getResistance(),
                        character_entity.getResistanceInPercents()
                )
        );

        TextView tvCharacterBonusChance = (TextView) prompts_view.findViewById(R.id.tvCharacterBonusChance);
        tvCharacterBonusChance.setText(String.format(Locale.ROOT, "%.1f%%", 100.0 * character_entity.getBonusChance()));

        TextView tvCharacterBonusMultiplier = (TextView) prompts_view.findViewById(R.id.tvCharacterBonusMultiplier);
        tvCharacterBonusMultiplier.setText(String.format(Locale.ROOT, "%.2f", character_entity.getBonusMultiplier()));

        TextView tvCharacterMultiplier = (TextView) prompts_view.findViewById(R.id.tvCharacterMultiplier);
        tvCharacterMultiplier.setText(String.format(Locale.ROOT, "%.2f", character_entity.getMultiplier()));

        TextView tvCharacterAvgResult = (TextView) prompts_view.findViewById(R.id.tvCharacterAvgResult);
        tvCharacterAvgResult.setText(String.format(Locale.ROOT, "%.0f", character_entity.getAvgResult()));

        ListView lvActiveSkills = (ListView) prompts_view.findViewById(R.id.lvActiveSkills);
        List<SkillView> active_skills = character_entity.getActiveSkills();
        lvActiveSkills.setAdapter(new SkillArrayAdapter(getBaseContext(), active_skills));

        TextView tvActiveSkillsStatus = (TextView) prompts_view.findViewById(R.id.tvActiveSkillsStatus);
        String active_skills_status = "";
        if (active_skills.size() == 0) {
            active_skills_status = "Нет усилений";
        }
        tvActiveSkillsStatus.setText(active_skills_status);

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                            }
                        });

        // create alert dialog
        AlertDialog dialogShowUserInfo = alert_dialog_builder.create();
        dialogShowUserInfo.show();
    }

    protected void showCompetitionLogListView()
    {
        SimpleAdapter competitionLogAdapter = new SimpleAdapter(
                this,
                competitionView.getLogData(),
                android.R.layout.simple_list_item_1,
                new String[] {"log_msg"},
                new int[] {android.R.id.text1}
        );

        lvCompetitionLog.setAdapter(competitionLogAdapter);
    }

    abstract protected void leaveCompetition();

    @Override
    public void onBackPressed() {
        if (competitionView != null && !competitionView.isFinishedCompetition()) {
            new AlertDialog.Builder(this)
                    .setMessage(exitMessage)
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            leaveCompetition();
                            CompetitionBaseActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        } else {
            finish();
        }
    }
}