package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TrainingActivity extends CompetitionBaseActivity {
    LocationPositionEntity locationPositionEntity;
    CompetitionEngine competitionEngine;
    AlertDialog dialogCompetitionMove;

    protected void setButtonsBeforeCompetitions()
    {
        btCompetitionStart.setVisibility(View.VISIBLE);
        btCompetitionRestart.setVisibility(View.INVISIBLE);
        btInvite.setVisibility(View.INVISIBLE);
        btNext.setVisibility(View.INVISIBLE);
        btFinish.setVisibility(View.INVISIBLE);
        btAddResult.setVisibility(View.INVISIBLE);
    }

    private void setButtonsDuringCompetitions()
    {
        btCompetitionStart.setVisibility(View.INVISIBLE);
        btCompetitionRestart.setVisibility(View.INVISIBLE);
        btInvite.setVisibility(View.INVISIBLE);
        btNext.setVisibility(View.INVISIBLE);
        btFinish.setVisibility(View.INVISIBLE);
        btAddResult.setVisibility(View.VISIBLE);
    }

    private void setButtonsAfterCompetitions()
    {
        btCompetitionStart.setVisibility(View.INVISIBLE);
        btCompetitionRestart.setVisibility(View.INVISIBLE);
        btInvite.setVisibility(View.INVISIBLE);
        btNext.setVisibility(View.INVISIBLE);
        btFinish.setVisibility(View.VISIBLE);
        btAddResult.setVisibility(View.INVISIBLE);
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exitMessage = "Вы уверены, что хотите прекратить тренировку?";
        competitionEngine = new CompetitionEngine(getBaseContext(), dbHelper.getExerciseName(gameHelper.getExerciseId()));

        initCompetitionTeamLeft();
        initCompetitionTeamRight();
        initAddResultButton();

        btCompetitionRestart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocationPositionEntity nextLocationPosition = null;
                if (locationPositionEntity.getWins() + 1 < locationPositionEntity.getQuestCnt()) {
                    nextLocationPosition = dbHelper.getLocationPositionByIds(
                            locationPositionEntity.getLocationId(),
                            locationPositionEntity.getLevel() + 1,
                            locationPositionEntity.getPosition()
                    );
                } else {
                    nextLocationPosition = locationPositionEntity;
                }

                if (nextLocationPosition != null) {
                    Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
                    intent.setAction(Integer.toString(nextLocationPosition.getLocationLevelPositionId()));
                    startActivity(intent);
                    finish();
                }
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocationPositionEntity nextLocationPosition = null;
                if (locationPositionEntity.getWins() + 1 == locationPositionEntity.getQuestCnt() && locationPositionEntity.getPosition() == 5) {
                    nextLocationPosition = dbHelper.getLocationPositionByIds(
                        locationPositionEntity.getLocationId() + 1, 1, 1
                    );
                }

                if (nextLocationPosition != null) {
                    Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
                    intent.setAction(Integer.toString(nextLocationPosition.getLocationLevelPositionId()));
                    startActivity(intent);
                    finish();
                }
            }
        });


        btInvite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogInviteUser();
            }
        });
        btFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        competitionView = competitionEngine.getCompetitionView();
        refreshView();
    }

    private void initAddResultButton()
    {
        btAddResult.setEnabled(false);
        btAddResult.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btAddResult.setEnabled(false);
                btTranslation.setVisibility(View.INVISIBLE);

                competitionEngine.startNewRound();
                competitionEngine.proceedNPCsStep1();

                initDialogCompetitionMove();

                competitionView = competitionEngine.getCompetitionView();
                showCompetitionLogListView();
                refreshView();
            }
        });
    }

    private void networkUpdateCompetitionInfo()
    {
        try {
            //networkHelper.getClient().newCall(networkHelper.getGetCompetitionInfo("AAAA3214AVVD")).enqueue(new Callback() {
            networkHelper.getClient().newCall(networkHelper.getUpdateCompetitionInfoRequest(competitionView, dbHelper.getCurrentPlayerEntity().getName(), inviteCode)).enqueue(new Callback() {
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
                        NetworkHelper.UpdateCompetitionInfoResponse response_object = (new Gson()).fromJson(json_string, NetworkHelper.UpdateCompetitionInfoResponse.class);
                        if (response_object.state != "ok") {
                            //error message
                        }
//                        Message msg = new Message();
//                        msg.obj = response_object.state;
//                        createCompetitionResponseHandler.sendMessage(msg);
                    } catch (Exception e) {
                        Message msg = new Message();
                        msg.obj = e.toString();
                        createCompetitionResponseHandler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initStartButton()
    {
        btCompetitionStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setButtonsDuringCompetitions();
                btAddResult.setEnabled(true);
                btAddResult.setVisibility(View.VISIBLE);
                btCompetitionStart.setEnabled(false);
                btCompetitionStart.setVisibility(View.INVISIBLE);

                competitionEngine.start();
                competitionView = competitionEngine.getCompetitionView();
                showCompetitionLogListView();
                refreshView();
            }
        });
    }

    private void postCompetitionActions()
    {
        setButtonsAfterCompetitions();
        btAddResult.setEnabled(false);
        btAddResult.setVisibility(View.INVISIBLE);
        btFinish.setVisibility(View.VISIBLE);

        competitionView = competitionEngine.getCompetitionView();
        showCompetitionLogListView();
        refreshView();
    }

    private SparseArray<ArrayAdapter<CharacterView>> getAdaptersTargetList(int teamIdx, int idxInTeam)
    {
        SparseArray<ArrayAdapter<CharacterView>> adapter_targets_array = new SparseArray<>();
        SparseArray<List<CharacterView>> action_targets_array = competitionEngine.getActionTargets(teamIdx, idxInTeam);
        for(int idx = 0; idx < action_targets_array.size(); idx++) {
            int key = action_targets_array.keyAt(idx);
            List<CharacterView> action_targets = action_targets_array.get(key);
            adapter_targets_array.put(key, new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, action_targets));
            adapter_targets_array.get(key).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        return adapter_targets_array;
    }

    private void initDialogCompetitionMove()
    {
        final CompetitionEngine.MoveRequest request = competitionEngine.getMoveRequest();

        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_competition_move, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        final NumberPicker np_count = prompts_view.findViewById(R.id.npCount);
        final TextView tv_prompt_exercise_name = prompts_view.findViewById(R.id.tvPromptExerciseName);
        //tv_prompt_exercise_name.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
        tv_prompt_exercise_name.setText(request.name);

        np_count.setMinValue(0);
        np_count.setMaxValue(100);
        np_count.setValue(gameHelper.getLastSelectionFromPreferences(request.userId, request.exerciseId));

        // адаптер PreActions
        ArrayAdapter<SkillView> adapter_pre_actions = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, competitionEngine.getPlayerPreActions(request.teamIdx, request.idxInTeam));
        adapter_pre_actions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner_pre_action = prompts_view.findViewById(R.id.spinnerPreAction);
        spinner_pre_action.setAdapter(adapter_pre_actions);

        // адаптер Actions
        ArrayAdapter<SkillView> adapter_actions = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, competitionEngine.getPlayerActions(request.teamIdx, request.idxInTeam));
        adapter_actions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // адаптер Targets
        final SparseArray<ArrayAdapter<CharacterView>> adapter_targets_array = getAdaptersTargetList(request.teamIdx, request.idxInTeam);
        final Spinner spinner_target = prompts_view.findViewById(R.id.spinnerTarget);

        final Spinner spinner_action = prompts_view.findViewById(R.id.spinnerAction);
        spinner_action.setAdapter(adapter_actions);
        spinner_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                SkillView action_skill = (SkillView) parent.getItemAtPosition(position);
                //String selectedItem = parent.getItemAtPosition(position).toString();
                final ArrayAdapter<CharacterView> adapter_targets = adapter_targets_array.get(action_skill.targetType);
                spinner_target.setAdapter(adapter_targets);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SkillView pre_action_skill = (SkillView) spinner_pre_action.getSelectedItem();
                                if (pre_action_skill.groupId == -1) {
                                    pre_action_skill = null;
                                }
                                SkillView action_skill = (SkillView) spinner_action.getSelectedItem();
                                if (action_skill.groupId == -1) {
                                    action_skill = null;
                                }

                                CharacterView target = ((CharacterView) spinner_target.getSelectedItem());
                                int teamIdx = -1;
                                int idxInTeam = -1;
                                if (target != null) {
                                    teamIdx = target.teamIdx;
                                    idxInTeam = target.idxInTeam;
                                } else if(action_skill != null && action_skill.targetType != SkillView.TARGET_TYPE_ACTIVE_ALL && action_skill.targetType != SkillView.TARGET_TYPE_ACTIVE_MY_TEAM && action_skill.targetType != SkillView.TARGET_TYPE_ACTIVE_OPPOSITE_TEAM) {
                                    dialog.cancel();
                                }
                                competitionEngine.setMove(
                                        request.teamIdx,
                                        request.idxInTeam,
                                        pre_action_skill,
                                        action_skill,
                                        teamIdx,
                                        idxInTeam,
                                        np_count.getValue()
                                );


                                if (competitionEngine.needMove()) {
                                    initDialogCompetitionMove();
                                } else {
                                    competitionEngine.proceed();
                                    competitionView = competitionEngine.getCompetitionView();
                                    refreshView();

                                    if (isInternetMode) {
                                        networkUpdateCompetitionInfo();
                                    }

                                    if (competitionEngine.finishedCompetition) {
                                        postCompetitionActions();
                                    }
                                }
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                competitionEngine.decreaseNumberOfMoves();
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialogCompetitionMove = alert_dialog_builder.create();
        dialogCompetitionMove.show();
        btAddResult.setEnabled(true);
        btAddResult.setVisibility(View.VISIBLE);
    }

    private void initDialogInviteUser()
    {
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_invite_user, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        // адаптер PreActions
        int exercise_id = gameHelper.getExerciseId();
        final List<UserEntity> invite_users = competitionEngine.filterUserList(
                dbHelper.getUsersWithExercise(exercise_id, gameHelper.isReplayMode()),
                exercise_id
        );
        ArrayAdapter<UserEntity> adapter_invite_user = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, invite_users);
        adapter_invite_user.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner_invite_user = prompts_view.findViewById(R.id.spinnerInviteUser);
        spinner_invite_user.setAdapter(adapter_invite_user);

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (spinner_invite_user.getSelectedItem() != null) {
                                    boolean status = competitionEngine.addCharacter(
                                            CompetitionEngine.LEFT_TEAM_IDX,
                                            dbHelper.getPlayerEntity(
                                                    ((UserEntity) spinner_invite_user.getSelectedItem()).id,
                                                    gameHelper.getExerciseId()
                                            )
                                    );
                                    if (status) {
                                        competitionView = competitionEngine.getCompetitionView();
                                        refreshView();
                                        if (invite_users.size() <= 1) {
                                            btInvite.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialogCompetitionMove = alert_dialog_builder.create();
        dialogCompetitionMove.show();
    }

    private void initCompetitionTeamLeft()
    {
        competitionEngine.addCharacter(CompetitionEngine.LEFT_TEAM_IDX, dbHelper.getPlayerEntity(gameHelper.getUserId(), gameHelper.getExerciseId()));
        competitionEngine.setMainPlayerTeamIdx(CompetitionEngine.LEFT_TEAM_IDX)
                .setMainPlayerIdx(competitionEngine.getCharactersCount(CompetitionEngine.LEFT_TEAM_IDX) - 1);
    }

    private void initCompetitionTeamRight()
    {
        locationPositionEntity = dbHelper.getLocationPosition(Integer.parseInt(getIntent().getAction()));

        ArrayList<NonPlayerCharacterEntity> entities = dbHelper.getNonPlayerCharactersByLocationPositionLevel(
                locationPositionEntity.getLocationId(),
                locationPositionEntity.getPosition(),
                locationPositionEntity.getLevel()
        );

        for (NonPlayerCharacterEntity entity: entities) {
            competitionEngine.addCharacter(CompetitionEngine.RIGHT_TEAM_IDX, entity);
        }
        competitionEngine.setLocationPosition(locationPositionEntity);
    }

    protected void leaveCompetition()
    {
        competitionEngine.leave(locationPositionEntity.getLocationId(), locationPositionEntity.getPosition(), locationPositionEntity.getLevel());
    }
}