package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ReplayEditorActivity extends ReplayActivity {
    AlertDialog dialog;
    EditText etReplay;
    Button btStart, btSave, btInfo;
    final String info="toast_long;TEXT;TICKS\n" +
            "toast;TEXT;TICKS\n" +
            "activity;ACTIVITY;TICKS\n" +
            "activity;ACTIVITY;5;TICKS\n" +
            "background;RESOURCE_ID;DRAWABLE_ID;TICKS\n" +
            "revert-background;TICKS\n" +
            "event1;TICKS\n" +
            "event2;TICKS\n" +
            "event3;TICKS\n" +
            "lv-item-background;RESOURCE_ID;DRAWABLE_ID;TICKS\n" +
            "exit\n" +
            "\n" +
            "ACTIVITY=[main|users|exercises|settings]\n" +
            "RESOURCE_ID=[lvNewItem|btMenuStart|llMenuStart|llMenuSettings|llMenuKnowledge]\n" +
            "DRAWABLE_ID=[colorAccent|mipmap/leaf_button_1|mipmap/leaf_button_1_red|replay_border|replay_border_inv]\n";

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replay_editor);

        ((TextView) findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.extra_settings_menu_replay_editor));

        etReplay = findViewById(R.id.etReplay);
        etReplay.setText(
                gameHelper.getSharedPreferencesString(
                        "replay_editor_last_string",
                        "toast-long;В разработке;50 # exit"
                )
        );

        btStart = findViewById(R.id.btStart);
        btStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameHelper.setSharedPreferencesString("replay_editor_last_string", etReplay.getText().toString());
                gameHelper.enableReplayMode(ReplayEditorActivity.this, etReplay.getText().toString());
            }
        });

        btInfo = findViewById(R.id.btInfo);
        btInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogShowTextKnowledgeItem(info);
            }
        });

        btSave = findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    try {
                        File sd = Environment.getExternalStorageDirectory();

                        if (sd.canWrite()) {
                            File myFile = new File(sd,"/SportInTheForest/replay_string.txt");
                            myFile.createNewFile();
                            FileOutputStream fOut = new FileOutputStream(myFile);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            myOutWriter.append(etReplay.getText().toString());
                            myOutWriter.close();
                            fOut.close();

                            Toast.makeText(getBaseContext(), "Сохранено", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getBaseContext(), "Нет прав", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Ошибка: " + e.toString(), Toast.LENGTH_LONG).show();
                        Log.d("myLogs", e.toString());
                        Log.d("myLogs", e.getStackTrace().toString());
                    }
            }
        });

    }

    protected void onStart() {
        super.onStart();


    }

    private void initDialogShowTextKnowledgeItem(String text_value)
    {
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_show_text_knowledge_item, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);
        alert_dialog_builder.setCancelable(true);
        dialog = alert_dialog_builder.create();

        TextView tvText = prompts_view.findViewById(R.id.tvText);
        tvText.setText(text_value);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameHelper.setSharedPreferencesString("replay_editor_last_string", etReplay.getText().toString());
    }

    @Override
    public void replayEvent1()
    {
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