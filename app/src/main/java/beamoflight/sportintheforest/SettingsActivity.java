package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends ReplayActivity {
    ListView lvSettings;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_lists_std1);

        initTopListView();
    }

    protected void onStart() {
        super.onStart();

        ((TextView) findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.main_menu_settings));
        showTopList();
    }

    private void initTopListView()
    {
        lvSettings = findViewById(R.id.lvItemsTop);
        lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!gameHelper.isReplayMode()) {
                    Intent intent;
                    switch (position) {
                        case 0:
                            dbHelper.exportDB("SportInTheForestDB.db", false);
                            dbHelper.exportDB("SportInTheForestDB_" + gameHelper.getTodayString() + ".db", false);
                            dbHelper.save2file("SportInTheForest.sif");
                            dbHelper.save2file("SportInTheForest_" + gameHelper.getTodayStringWithHours() + ".sif");
                            dbHelper.save2file("SportInTheForest_" + gameHelper.getTodayString() + ".sif");
                            Toast.makeText(getBaseContext(), "Сохранено!", Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setMessage(R.string.are_you_sure)
                                    .setCancelable(false)
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dbHelper.importDB("SportInTheForestDB.db", true);
                                        }
                                    })
                                    .setNegativeButton("Нет", null)
                                    .show();
                            break;
                        case 2:
                            intent = new Intent(SettingsActivity.this, ExercisesActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setMessage(R.string.are_you_sure)
                                    .setCancelable(false)
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dbHelper.recreateCommonTable();
                                            Toast.makeText(getBaseContext(), "Выполнено", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton("Нет", null)
                                    .show();
                            break;
                        case 4:
                            intent = new Intent(getBaseContext(), ExtraSettingsActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            }
        });
    }

    private void showTopList()
    {
        lvSettings.invalidateViews();
        String[] items = {"Сохранить", "Загрузить", "Настройка упражнений", "Сброс данных с сохранением прогресса", "Дополнительные настройки"};
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.new_user_list_item, items);

        lvSettings.setAdapter(itemsAdapter);
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