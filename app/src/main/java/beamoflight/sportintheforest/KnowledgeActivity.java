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
import android.widget.Button;
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
    List<Map<String, String>> knowledgeItemsData;
    ListView lvKnowledgeCategories;
    int categoryId;
    String categoryName;
    AlertDialog dialog;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_lists_std1);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(this );
    }

    protected void onStart() {
        super.onStart();

        categoryId = Integer.parseInt(this.getIntent().getAction());
        categoryName = dbHelper.getKnowledgeCategoryName(categoryId);

        ((TextView) findViewById(R.id.tvTitle)).setText(categoryName);

        initKnowledgeItemsListView();
    }

    private void initKnowledgeItemsListView()
    {
        lvKnowledgeCategories = findViewById(R.id.lvItemsBottom);
        lvKnowledgeCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int type = Integer.parseInt(knowledgeItemsData.get(position).get("type"));
                String text_value = knowledgeItemsData.get(position).get("text_value");
                if (KnowledgeItemArrayAdapter.KNOWLEDGE_ITEM_TYPE_TEXT == type) {
                    initDialogShowTextKnowledgeItem(text_value);
                } else if (KnowledgeItemArrayAdapter.KNOWLEDGE_ITEM_TYPE_REPLAY == type) {
                    gameHelper.enableReplayMode(KnowledgeActivity.this, text_value);
                }

            }
        });

        showKnowledgeItemsList();
    }

    private void showKnowledgeItemsList()
    {
        knowledgeItemsData = dbHelper.getKnowledgeItemsData(categoryId);
        lvKnowledgeCategories.invalidateViews();
        KnowledgeItemArrayAdapter knowledgeItemsAdapter = new KnowledgeItemArrayAdapter(
                this,
                knowledgeItemsData
        );

        lvKnowledgeCategories.setAdapter(knowledgeItemsAdapter);
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
}