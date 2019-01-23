package beamoflight.sportintheforest;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnowledgeCategoriesActivity extends Activity {

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

        ArrayList<Map<String, String>> usersData = dbHelper.getUsersData(true);
        if (usersData.size() == 0) {
            long user_id1 = dbHelper.addUser("Иван Иванович", true);
            long user_id2 = dbHelper.addUser("Пётр Петрович", true);
            int exercise_id = 1;
            dbHelper.addUserExercise(user_id1, exercise_id, dbHelper.USER_EXERCISE_TYPE_RPG);
            dbHelper.addUserExercise(user_id2, exercise_id, dbHelper.USER_EXERCISE_TYPE_RPG);
        }
    }

    protected void onStart() {
        super.onStart();

        ((TextView) findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.main_menu_knowledge));

        initKnowledgeCategoriesListView();
    }

    private void initKnowledgeCategoriesListView()
    {
        lvKnowledgeCategories = findViewById(R.id.lvItemsBottom);
        lvKnowledgeCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getBaseContext(), KnowledgeActivity.class);
                intent.setAction(knowledgeCategoriesData.get(position).get("id"));
                startActivity(intent);

            }
        });

        showKnowledgeCategoriesList();
    }

    private void showKnowledgeCategoriesList()
    {
        knowledgeCategoriesData = dbHelper.getKnowledgeCategoriesData();
        lvKnowledgeCategories.invalidateViews();
        KnowledgeCategoryArrayAdapter KnowledgeCategoriesAdapter = new KnowledgeCategoryArrayAdapter(
                this,
                knowledgeCategoriesData
        );

        lvKnowledgeCategories.setAdapter(KnowledgeCategoriesAdapter);
    }
}