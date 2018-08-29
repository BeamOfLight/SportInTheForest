package beamoflight.sportintheforest;


import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class LocationPositionsActivity extends Activity {

    int locationId;
    DBHelper dbHelper;
    GameHelper gameHelper;
    List<LocationPositionEntity> locationPositionData;

    TextView tvLocationPositionsListInfo;
    ListView lvLocationPositions;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_player_characters);

        tvLocationPositionsListInfo = (TextView) findViewById(R.id.tvLocationPositionsListInfo);
        dbHelper = new DBHelper( this );
        locationId = Integer.parseInt(this.getIntent().getAction());
        gameHelper = new GameHelper(this.getBaseContext());
    }

    protected void onStart() {
        super.onStart();

        showLocationName();
        initNPCsListView();
    }

    private void initNPCsListView()
    {
        lvLocationPositions = (ListView) findViewById(R.id.lvLocationPositions);
        lvLocationPositions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LocationPositionsActivity.this, CompetitionActivity.class);
                intent.setAction(Integer.toString(locationPositionData.get(position).getLocationLevelPositionId()));
                startActivity(intent);
            }
        });

        refreshData();
        showList();
    }


    private void showList()
    {
        lvLocationPositions.invalidateViews();
        LocationPositionItemArrayAdapter adapter = new LocationPositionItemArrayAdapter(this, locationPositionData);

        lvLocationPositions.setAdapter(adapter);
        lvLocationPositions.setFooterDividersEnabled(true);
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

    private void refreshData()
    {
        locationPositionData = dbHelper.getLocationPositionsData(locationId);
        if (locationPositionData.size() > 0) {
            tvLocationPositionsListInfo.setText("");
        } else {
            tvLocationPositionsListInfo.setText(getResources().getString(R.string.npcs_list_info_msg));
        }
    }
}