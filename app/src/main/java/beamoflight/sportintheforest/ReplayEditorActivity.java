package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplayEditorActivity extends ReplayActivity {
     Map<String, Integer> replayCommandLayouts;

    class ReplayCommand {
        String cmd, arg1, arg2, arg3;
        int ticks;
        ReplayCommand() {
            this.cmd = "";
            this.arg1 = "";
            this.arg2 = "";
            this.arg3 = "";
            this.ticks = 0;
        }
        ReplayCommand(String cmd, String arg1, String arg2, String arg3, int ticks) {
            this.cmd = cmd;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.ticks = ticks;
        }
    }

    ArrayList<ReplayCommand> replayCommands;

    AlertDialog dialog;
//    EditText etReplay;
//    Button btStart, btSave, btInfo;
    ListView lvMenu, lvCommands;

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

        initReplayCommandsDict();
        replayCommands = new ArrayList<>();
        initMenuListView();
        initCommandsListView();

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

        setReplayCommands(
                gameHelper.getSharedPreferencesString(
                        "replay_editor_last_string",
                        "toast-long;В разработке;50 # exit"
                )
        );

        showMenuListView();
        showCommandsListView();
    }

    protected void initReplayCommandsDict()
    {
        replayCommandLayouts = new HashMap<>();
        replayCommandLayouts.put("event1", R.layout.prompt_replay_command_spinner_ticks);
        replayCommandLayouts.put("event2", R.layout.prompt_replay_command_spinner_ticks);
        replayCommandLayouts.put("event3", R.layout.prompt_replay_command_spinner_ticks);
        replayCommandLayouts.put("activity", R.layout.prompt_replay_command_spinner);
        replayCommandLayouts.put("activity-action", R.layout.prompt_replay_command_spinner);
        replayCommandLayouts.put("toast", R.layout.prompt_replay_command_spinner);
        replayCommandLayouts.put("toast-long", R.layout.prompt_replay_command_spinner);
        replayCommandLayouts.put("background", R.layout.prompt_replay_command_spinner);
        replayCommandLayouts.put("revert-background", R.layout.prompt_replay_command_spinner_ticks);
        replayCommandLayouts.put("lv-item-background", R.layout.prompt_replay_command_spinner);
        replayCommandLayouts.put("exit", R.layout.prompt_replay_command_spinner);
    }

    protected void setReplayCommands(String replay_string) {
        replayCommands.clear();

        String cmd_str;
        String[] replay_records = replay_string.split(GameHelper.REPLAY_CMD_DELIMITER);
        for (String replay_record : replay_records) {
            String[] replay_record_parts = replay_record.split(GameHelper.REPLAY_ARG_DELIMITER);
            if (replay_record_parts.length >= 1) {
                ReplayCommand cmd = new ReplayCommand();
                cmd_str = replay_record_parts[0];
                cmd.cmd = cmd_str;
                switch(cmd_str) {
                    case "event1":
                    case "event2":
                    case "event3":
                    case "revert-background":
                        if (replay_record_parts.length == 2) {
                            cmd.ticks = Integer.parseInt(replay_record_parts[1]);
                        }
                        break;
                    case "toast-long":
                    case "toast":
                    case "activity":
                        if (replay_record_parts.length == 3) {
                            cmd.arg1 = replay_record_parts[1];
                            cmd.ticks = Integer.parseInt(replay_record_parts[2]);
                        }
                        break;
                    case "activity-action":
                    case "background":
                        if (replay_record_parts.length == 4) {
                            cmd.arg1 = replay_record_parts[1];
                            cmd.arg2 = replay_record_parts[2];
                            cmd.ticks = Integer.parseInt(replay_record_parts[3]);
                        }
                        break;
                    case "lv-item-background":
                        if (replay_record_parts.length == 5) {
                            cmd.arg1 = replay_record_parts[1];
                            cmd.arg2 = replay_record_parts[2];
                            cmd.arg3 = replay_record_parts[3];
                            cmd.ticks = Integer.parseInt(replay_record_parts[4]);
                        }
                        break;
                    default:
                    case "exit":
                        cmd.cmd = "exit";
                }
                replayCommands.add(cmd);
            }
        }
    }

    private void initDialogChangeReplayCommand(String cmd_str, int layout_id, final int position)
    {
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(layout_id, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);

        TextView tvCommandType = prompts_view.findViewById(R.id.tvCommandType);
        tvCommandType.setText(cmd_str);

        Spinner spinner = prompts_view.findViewById(R.id.spinnerCommandType);
        List<String> commandsList = new ArrayList<>(replayCommandLayouts.keySet());
        java.util.Collections.sort(commandsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                commandsList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(commandsList.indexOf(cmd_str));

        final NumberPicker finalNpTicks = prompts_view.findViewById(R.id.npTicks);
        final Spinner finalSpinner = spinner;
        final int finalLayoutId = layout_id;

        switch (layout_id) {
            case R.layout.prompt_replay_command_spinner:
                break;
            case R.layout.prompt_replay_command_spinner_ticks:
                finalNpTicks.setMinValue(0);
                finalNpTicks.setMaxValue(50);
                finalNpTicks.setValue(replayCommands.get(position).ticks);

                break;
        }

        alert_dialog_builder.setView(prompts_view)
            .setMessage("Редактирование")
            .setCancelable(true)
            .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    replayCommands.get(position).cmd = (String) finalSpinner.getSelectedItem();
                    switch (finalLayoutId) {
                        case R.layout.prompt_replay_command_spinner:
                            break;
                        case R.layout.prompt_replay_command_spinner_ticks:
                            replayCommands.get(position).ticks = finalNpTicks.getValue();
                            break;
                    }

                    showCommandsListView();
                }
            });

        dialog = alert_dialog_builder.create();

        dialog.show();
    }

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

    private void showMenuListView()
    {
        lvMenu.invalidateViews();
        String[] items = {"Просмотр", "Сохранить"};
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.new_user_list_item, items);

        lvMenu.setAdapter(itemsAdapter);
    }

    private void initCommandsListView()
    {
        lvCommands = findViewById(R.id.lvItemsBottom);
        lvCommands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String cmd_str = replayCommands.get(position).cmd;
                int layout_id = replayCommandLayouts.get(cmd_str);
                initDialogChangeReplayCommand(cmd_str, layout_id, position);
            }
        });
    }

    private void showCommandsListView()
    {
        lvCommands.invalidateViews();
        ReplayCommandItemArrayAdapter itemsAdapter = new ReplayCommandItemArrayAdapter(this, replayCommands);
        lvCommands.setAdapter(itemsAdapter);
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