package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* Fragment used as page 4 */
public class PageFragmentSkills extends Fragment {

    DBHelper dbHelper;
    GameHelper gameHelper;
    ArrayList<Map<String, String>> skillsData;

    ListView lvSkills;
    ViewGroup mContainer;
    TextView tvUserSkillPoints;
    Button btLearnSkills;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_skills, container, false);
        mContainer = container;

        dbHelper = new DBHelper(container.getContext());
        gameHelper = new GameHelper(container.getContext());

        btLearnSkills = rootView.findViewById(R.id.btLearnSkills);
        btLearnSkills.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogLearnSkills();
            }
        });

        lvSkills = rootView.findViewById(R.id.lvSkills);
        tvUserSkillPoints = rootView.findViewById(R.id.tvUserSkillPoints);

        return rootView;
    }

    public void onStart() {
        super.onStart();
        showSkillsList();
    }

    private void showSkillsList()
    {
        skillsData = dbHelper.getCurrentUserLearntSkills();
        lvSkills.invalidateViews();
        SimpleAdapter skillsAdapter = new SimpleAdapter(
                mContainer.getContext(),
                skillsData,
                android.R.layout.simple_list_item_2,
                new String[] {"skill_group_name", "full_info", "skill_group_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvSkills.setAdapter(skillsAdapter);

        int user_skill_points = dbHelper.getCurrentUserSkillPoints();
        tvUserSkillPoints.setText(String.format(Locale.ROOT, "%d", user_skill_points));
    }

    private void initDialogLearnSkills()
    {
        LayoutInflater li = LayoutInflater.from(getContext());
        View prompts_view = li.inflate(R.layout.prompt_learn_skills, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(getContext());
        alert_dialog_builder.setView(prompts_view);

        int user_skill_points = dbHelper.getCurrentUserSkillPoints();
        final TextView tvDialogUserSkillPoints = prompts_view.findViewById(R.id.tvUserSkillPoints);
        tvDialogUserSkillPoints.setText(String.format(Locale.ROOT, "%d", user_skill_points));

        final ListView lvDialogSkills = prompts_view.findViewById(R.id.lvSkills);
        final ArrayList<Map<String, String>> dialogSkillsData = dbHelper.getUnexploredSkillsData();
        lvDialogSkills.invalidateViews();
        SimpleAdapter dialogSkillsAdapter = new SimpleAdapter(
                mContainer.getContext(),
                dialogSkillsData,
                android.R.layout.simple_list_item_2,
                new String[] {"skill_group_name", "full_info", "skill_group_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvDialogSkills.setAdapter(dialogSkillsAdapter);

        lvDialogSkills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> skill = dialogSkillsData.get(position);
                int skill_id = Integer.parseInt(dialogSkillsData.get(position).get("skill_id"));
                int current_skill_points = dbHelper.getCurrentUserSkillPoints();
                int required_skill_points = Integer.parseInt(skill.get("skill_points"));
                int required_level = Integer.parseInt(skill.get("required_level"));

                // Check user_level
                if (gameHelper.getCachedUserLevel() < required_level) {
                    Toast.makeText(
                            mContainer.getContext(),
                            getResources().getString(R.string.msg_new_skill_need_higher_level),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                // Check skill points
                if (current_skill_points < required_skill_points) {
                    Toast.makeText(
                            mContainer.getContext(),
                            getResources().getString(R.string.msg_new_skill_need_more_skill_points),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                dbHelper.learnSkillFromSkillGroup(skill_id);
                Toast.makeText(
                        mContainer.getContext(),
                        String.format(
                                getResources().getString(R.string.msg_new_skill_congratulations),
                                skill.get("skill_group_name") + ". Уровень " + skill.get("skill_level")
                        ),
                        Toast.LENGTH_LONG
                ).show();
                showSkillsList();

                startActivity(gameHelper.getIntent4refreshedView(getActivity(), 2));
            }
        });


        // set dialog message
        alert_dialog_builder.setCancelable(true);

        // create alert dialog
        alert_dialog_builder.create().show();
    }

}
