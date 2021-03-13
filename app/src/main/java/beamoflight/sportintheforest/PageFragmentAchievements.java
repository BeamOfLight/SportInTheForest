package beamoflight.sportintheforest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/* Fragment used as page 5 */
public class PageFragmentAchievements extends Fragment {

    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Map<String, String>> data;

    ListView lvAchievements;
    TextView tvUserAchievements;
    ViewGroup mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_achievements, container, false);
        mContainer = container;
        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        lvAchievements = rootView.findViewById(R.id.lvAchievements);
        tvUserAchievements = rootView.findViewById(R.id.tvUserAchievements);

        return rootView;
    }

    public void onStart() {
        super.onStart();
        showAchievementList();
    }

    private void showAchievementList()
    {
        data = dbHelper.getCurrentUserAchievementsData();
        lvAchievements.invalidateViews();
        SimpleAdapter achievementAdapter = new SimpleAdapter(
                mContainer.getContext(),
                data,
                android.R.layout.simple_list_item_2,
                new String[] {"header", "info", "achievement_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        if (data.size() > 0) {
            tvUserAchievements.setText(data.get(data.size() - 1).get("success_achievements_cnt"));
        } else {
            tvUserAchievements.setText("0");
        }

        lvAchievements.setAdapter(achievementAdapter);
    }
}
