package beamoflight.sportintheforest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

/* Fragment used as page 7 */
public class PageFragmentLocations extends Fragment {

    DBHelper dbHelper;
    GameHelper gameHelper;
    ArrayList<Map<String, String>> locationsData;

    ListView lvLocations;
    ViewGroup mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_locations, container, false);
        mContainer = container;

        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        lvLocations = (ListView) rootView.findViewById(R.id.lvLocations);
        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContainer.getContext(), NonPlayerCharactersActivity.class);
                intent.setAction(locationsData.get(position).get("location_id"));
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onStart() {
        super.onStart();

        locationsData = dbHelper.getLocationsData();
        showLocationsList();
    }

    private void showLocationsList()
    {
        lvLocations.invalidateViews();
        SimpleAdapter locationsAdapter = new SimpleAdapter(
                mContainer.getContext(),
                locationsData,
                android.R.layout.simple_list_item_2,
                new String[] {"location_name", "quests_info", "location_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvLocations.setAdapter(locationsAdapter);
    }


}
