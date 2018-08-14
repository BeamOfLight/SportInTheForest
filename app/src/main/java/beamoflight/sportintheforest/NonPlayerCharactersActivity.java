package beamoflight.sportintheforest;


import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class NonPlayerCharactersActivity extends Activity {

    int locationId;
    DBHelper dbHelper;
    GameHelper gameHelper;
    List<NonPlayerCharacterEntity> nonPlayerCharactersData;

    TextView tvNPCsListInfo;
    ListView lvNPCs;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_player_characters);

        tvNPCsListInfo = (TextView) findViewById(R.id.tvNPCsListInfo);
        dbHelper = new DBHelper( this );
        locationId = Integer.parseInt(this.getIntent().getAction());
        gameHelper = new GameHelper(this.getBaseContext());
    }

    protected void onStart() {
        super.onStart();

        showLocationName();
        initNPCsListView();
    }

    /*
    private void initFABAddNPC()
    {
        fabAddNPC = (FloatingActionButton) findViewById(R.id.fabAddNPC);
        fabAddNPC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            initDialogAddOrEditNPC(-1);
            dialogAddOrEditNPC.show();
            }
        });
    }*/

    private void initNPCsListView()
    {
        lvNPCs = (ListView) findViewById(R.id.lvNPCs);
        lvNPCs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NonPlayerCharactersActivity.this, CompetitionActivity.class);
                intent.setAction(Integer.toString(nonPlayerCharactersData.get(position).getId()));
                startActivity(intent);
            }
        });

        refreshNPCsData();
        showNPCsList();
    }


    private void showNPCsList()
    {
        lvNPCs.invalidateViews();
        NonPlayerCharacterItemArrayAdapter adapter = new NonPlayerCharacterItemArrayAdapter(this, nonPlayerCharactersData);

        lvNPCs.setAdapter(adapter);
        lvNPCs.setFooterDividersEnabled(true);
    }

    private void showLocationName()
    {
        TextView tv_location_name = (TextView) findViewById(R.id.tvLocationName);
        String location_name = dbHelper.getLocationName(locationId);
        if (location_name != null) {
            tv_location_name.setText(location_name);
        } else {
            tv_location_name.setText(R.string.msg_location_not_found);
        }
    }

    private void refreshNPCsData()
    {
        nonPlayerCharactersData = dbHelper.getNonPlayerCharactersData(locationId);
        if (nonPlayerCharactersData.size() > 0) {
            tvNPCsListInfo.setText("");
        } else {
            tvNPCsListInfo.setText(getResources().getString(R.string.npcs_list_info_msg));
        }
    }
}