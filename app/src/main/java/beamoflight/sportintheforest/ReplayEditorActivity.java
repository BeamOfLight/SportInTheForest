package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ReplayEditorActivity extends ReplayActivity {
    class ReplayCommand {
        String cmd, arg1, arg2;
        int ticks;
        ReplayCommand(String cmd, String arg1, String arg2, int ticks) {
            this.cmd = cmd;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.ticks = ticks;
        }
    }

    ArrayList<ReplayCommand> replayCmds;

    AlertDialog dialog;
//    EditText etReplay;
//    Button btStart, btSave, btInfo;
    ListView lvMenu;

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
        setContentView(R.layout.menu_lists_std1);

        ((TextView) findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.extra_settings_menu_replay_editor));

        replayCmds = new ArrayList<>();
        initMenuListView();

//        etReplay = findViewById(R.id.etReplay);
//        etReplay.setText(
//                gameHelper.getSharedPreferencesString(
//                        "replay_editor_last_string",
//                        "toast-long;В разработке;50 # exit"
//                )
//        );
//
//        btStart = findViewById(R.id.btStart);
//        btStart.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //gameHelper.setSharedPreferencesString("replay_editor_last_string", etReplay.getText().toString());
//                gameHelper.enableReplayMode(ReplayEditorActivity.this, etReplay.getText().toString());
//            }
//        });
//
//        btInfo = findViewById(R.id.btInfo);
//        btInfo.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                initDialogShowTextKnowledgeItem(info);
//            }
//        });
//
//        btSave = findViewById(R.id.btSave);
//        btSave.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                    try {
//                        File sd = Environment.getExternalStorageDirectory();
//
//                        if (sd.canWrite()) {
//                            File myFile = new File(sd,"/SportInTheForest/replay_string.txt");
//                            myFile.createNewFile();
//                            FileOutputStream fOut = new FileOutputStream(myFile);
//                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//                            myOutWriter.append(etReplay.getText().toString());
//                            myOutWriter.close();
//                            fOut.close();
//
//                            Toast.makeText(getBaseContext(), "Сохранено", Toast.LENGTH_LONG).show();
//
//                        } else {
//                            Toast.makeText(getBaseContext(), "Нет прав", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(getBaseContext(), "Ошибка: " + e.toString(), Toast.LENGTH_LONG).show();
//                        Log.d("myLogs", e.toString());
//                        Log.d("myLogs", e.getStackTrace().toString());
//                    }
//            }
//        });

    }

    protected void onStart() {
        super.onStart();

        showMenu();
    }

    protected void setReplayCmds(String replay_string) {
        replayCmds.clear();
    }

//    private void initDialogShowTextKnowledgeItem(String text_value)
//    {
//        LayoutInflater li = LayoutInflater.from(this);
//        View prompts_view = li.inflate(R.layout.prompt_show_text_knowledge_item, null);
//
//        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
//        alert_dialog_builder.setView(prompts_view);
//        alert_dialog_builder.setCancelable(true);
//        dialog = alert_dialog_builder.create();
//
//        TextView tvText = prompts_view.findViewById(R.id.tvText);
//        tvText.setText(text_value);
//
//        dialog.show();
//    }

    private void initMenuListView()
    {
        lvMenu = findViewById(R.id.lvItemsTop);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                }
            }
        });
    }

    private void showMenu()
    {
        lvMenu.invalidateViews();
        String[] items = {"Просмотр", "Сохранить"};
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.new_user_list_item, items);

        lvMenu.setAdapter(itemsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //gameHelper.setSharedPreferencesString("replay_editor_last_string", etReplay.getText().toString());
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