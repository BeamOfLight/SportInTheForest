package beamoflight.sportintheforest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CompetitionViewerActivity extends CompetitionBaseActivity {
    Timer mainTimer = new Timer(true);
    Handler mainHandler;
    Handler viewHandler;
    AlertDialog dialogInviteCode;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogInviteCode();
        dialogInviteCode.show();

        exitMessage = "Вы уверены, что хотите покинуть трансляцию?";
        initHandlers();
        initTimerTasks();
        btCompetitionStart.setVisibility(View.INVISIBLE);
        btTranslation.setVisibility(View.INVISIBLE);
    }

    private void initDialogInviteCode()
    {
        // get prompt_add_group.xmlgroup.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_invite_code, null);
        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        final EditText et_input_invite_code = (EditText) prompts_view.findViewById(R.id.editInviteCodeInput);

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                inviteCode = et_input_invite_code.getText().toString();
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

        // create alert dialog
        dialogInviteCode = alert_dialog_builder.create();
    }

    protected void initStartButton()
    {

    }

    protected void leaveCompetition()
    {

    }

    private void initTimerTasks()
    {
        TimerTask mainTimerTask = new MainTimerTask();
        mainTimer.scheduleAtFixedRate(mainTimerTask, 0, 5000);
    }

    private class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            mainHandler.sendEmptyMessage(0);
        }
    }

    private void initHandlers()
    {
        viewHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                refreshView();
                tvExerciseName.setText(dbHelper.getExerciseName(competitionView.exerciseId));
            }
        };

        mainHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (inviteCode.equals("")) {
                    return;
                }

                try {
                    //networkHelper.getClient().newCall(networkHelper.getGetCompetitionInfo("AAAA3214AVVD")).enqueue(new Callback() {
                    networkHelper.getClient().newCall(networkHelper.getGetCompetitionInfoRequest(inviteCode)).enqueue(new okhttp3.Callback() {
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
                                NetworkHelper.GetCompetitionInfoResponse response_object = (new Gson()).fromJson(json_string, NetworkHelper.GetCompetitionInfoResponse.class);
                                competitionView = (new Gson()).fromJson(response_object.competitionState, CompetitionView.class);
                                viewHandler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                Message msg = new Message();
                                msg.obj = e.toString();
//                                createCompetitionResponseHandler.sendMessage(msg);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                String json_string = "";
//                try {
//                    File sd = Environment.getExternalStorageDirectory();
//                    if (sd.canRead()) {
//                        //String path = sd.getAbsolutePath();
//                        File file = new File(sd, "/SportInTheForest/json.txt");
//                        StringBuilder text = new StringBuilder();
//
//                        BufferedReader br = new BufferedReader(new FileReader(file));
//                        String line;
//
//                        while ((line = br.readLine()) != null) {
//                            text.append(line);
//                            text.append('\n');
//                        }
//                        json_string = text.toString();
//                    }
//
//
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//                if (json_string.length() > 0) {
//                    competitionView = (new Gson()).fromJson(json_string, CompetitionView.class);
//                    refreshView();
//                } else {
//                    Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
//                }
            };
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mainTimer != null) {
            mainTimer.cancel();
        }
    }
}