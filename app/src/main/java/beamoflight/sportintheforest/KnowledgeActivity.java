package beamoflight.sportintheforest;


import android.app.Activity;
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

import java.util.List;
import java.util.Map;

public class KnowledgeActivity extends Activity {

    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Map<String, String>> knowledgeCategoriesData;
    ListView lvKnowledgeCategories;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_lists_std1);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(this );
    }

    protected void onStart() {
        super.onStart();

        ((TextView) findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.main_menu_knowledge));

        initExercisesListView();
    }

    private void initExercisesListView()
    {
        lvKnowledgeCategories = findViewById(R.id.lvItems);
        lvKnowledgeCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


            }
        });

        showExercisesList();
    }

    private void showExercisesList()
    {
        knowledgeCategoriesData = dbHelper.getKnowledgeCategoriesData();
        lvKnowledgeCategories.invalidateViews();
        SimpleAdapter exercisesAdapter = new SimpleAdapter(
                this,
                knowledgeCategoriesData,
                android.R.layout.simple_list_item_1,
                new String[] {"name", "id"},
                new int[] {android.R.id.text1}
        );

        lvKnowledgeCategories.setAdapter(exercisesAdapter);
    }
}