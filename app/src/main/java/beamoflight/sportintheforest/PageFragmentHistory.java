package beamoflight.sportintheforest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* Fragment used as page 6 */
public class PageFragmentHistory extends Fragment {

    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Map<String, String>> data;

    ListView lvHistory;
    TextView tvHistory;
    ViewGroup mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_history, container, false);
        mContainer = container;
        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        lvHistory = (ListView) rootView.findViewById(R.id.lvHistory);
        tvHistory = (TextView) rootView.findViewById(R.id.tvHistory);


        return rootView;
    }

    public void onStart() {
        super.onStart();
        showHistoryList();
    }

    private void showHistoryList()
    {
        data = dbHelper.getCurrentUserLastTrainingsData(10);
        if (data.size() > 0) {
            lvHistory.invalidateViews();
            SimpleAdapter historyAdapter = new SimpleAdapter(
                    mContainer.getContext(),
                    data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"header", "info", "training_id"},
                    new int[] {android.R.id.text1, android.R.id.text2}
            );

            lvHistory.setAdapter(historyAdapter);
            tvHistory.setText("");
        } else {
            tvHistory.setText("Вы пока не участвовали в соревнованиях");
        }
    }
}
