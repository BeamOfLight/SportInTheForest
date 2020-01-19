package beamoflight.sportintheforest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

/* Fragment used as page 7 */
public class PageFragmentTrainings extends Fragment {

    DBHelper dbHelper;
    GameHelper gameHelper;
    ViewGroup mContainer;
    Button btStartWorkout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_trainings, container, false);
        mContainer = container;

        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        btStartWorkout = rootView.findViewById(R.id.btStartWorkout);

        btStartWorkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WorkoutActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onStart() {
        super.onStart();
    }
}
