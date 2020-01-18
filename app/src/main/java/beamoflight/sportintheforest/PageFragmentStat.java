package beamoflight.sportintheforest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

/* Fragment used as page 3 */
public class PageFragmentStat extends Fragment {

    DBHelper dbHelper;
    GameHelper gameHelper;

    TextView tvUserCompetitionWins, tvUserCompetitionDefeats, tvUserCompetitionDraws;
    TextView tvUserCompetitions, tvUserSpecialisation;
    Button btSelectSpecialisation;
    AlertDialog dialogSelectSpecialisation;
    Context mContext;
    int mSpecialisationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_stat, container, false);

        mContext = container.getContext();
        dbHelper = new DBHelper(mContext);
        gameHelper = new GameHelper(mContext);
        mSpecialisationId = 0;

        tvUserSpecialisation = (TextView) rootView.findViewById(R.id.tvUserSpecialisation);
        tvUserCompetitionWins = (TextView) rootView.findViewById(R.id.tvUserCompetitionWins);
        tvUserCompetitionDefeats = (TextView) rootView.findViewById(R.id.tvUserCompetitionDefeats);
        tvUserCompetitionDraws = (TextView) rootView.findViewById(R.id.tvUserCompetitionDraws);
        tvUserCompetitions = (TextView) rootView.findViewById(R.id.tvUserCompetitions);
        btSelectSpecialisation = (Button) rootView.findViewById(R.id.btSelectSpecialisation);

        return rootView;
    }

    private void initDialogSelectSpecialisation()
    {
        LayoutInflater li = LayoutInflater.from(mContext);
        View prompts_view = li.inflate(R.layout.prompt_select_specialisation, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(mContext);
        alert_dialog_builder.setView(prompts_view);

        final Button btSelectSpecialisationResult = (Button) prompts_view.findViewById(R.id.btSelectSpecialisationResult);
        final Button btSelectSpecialisationResistance = (Button) prompts_view.findViewById(R.id.btSelectSpecialisationResistance);
        final Button btSelectSpecialisationRegeneration = (Button) prompts_view.findViewById(R.id.btSelectSpecialisationRegeneration);

        btSelectSpecialisationResult.setEnabled(true);
        btSelectSpecialisationResistance.setEnabled(true);
        btSelectSpecialisationRegeneration.setEnabled(true);

        btSelectSpecialisationResult.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpecialisationId = 1;
                btSelectSpecialisationResistance.setEnabled(false);
                btSelectSpecialisationRegeneration.setEnabled(false);
            }
        });

        btSelectSpecialisationResistance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpecialisationId = 2;
                btSelectSpecialisationResult.setEnabled(false);
                btSelectSpecialisationRegeneration.setEnabled(false);
            }
        });

        btSelectSpecialisationRegeneration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpecialisationId = 3;
                btSelectSpecialisationResult.setEnabled(false);
                btSelectSpecialisationResistance.setEnabled(false);
            }
        });


        // set dialog message
        alert_dialog_builder
                .setCancelable(true)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dbHelper.setSpecialisation2UserExercise(gameHelper.getUserId(), gameHelper.getExerciseId(), mSpecialisationId);
                                Toast.makeText(
                                        mContext,
                                        String.format(
                                                Locale.ROOT,
                                                getResources().getString(R.string.msg_new_specialisation),
                                                gameHelper.getSpecialisationName(mSpecialisationId)
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();
                                //refreshSpecialisationViews();
                                startActivity(gameHelper.getIntent4refreshedView(getActivity(), 1));
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialogSelectSpecialisation = alert_dialog_builder.create();
    }

    private void refreshSpecialisationViews()
    {
        Map<String, String> user_exercise_data = dbHelper.getUserExerciseData(gameHelper.getUserId(), gameHelper.getExerciseId());
        tvUserSpecialisation.setText(gameHelper.getSpecialisationName(Integer.parseInt(user_exercise_data.get("specialisation"))));
        int user_level = dbHelper.getUserLevel(gameHelper.getUserId(), gameHelper.getExerciseId());
        if (dbHelper.getCurrentUserExerciseData().get("specialisation").equals("0") && user_level >= getResources().getInteger(R.integer.specialisation_level)) {
            btSelectSpecialisation.setVisibility(View.VISIBLE);
        } else {
            btSelectSpecialisation.setVisibility(View.INVISIBLE);
        }
    }

    public void onStart() {
        super.onStart();
        Map<String, String> user_exercise_data = dbHelper.getUserExerciseData(gameHelper.getUserId(), gameHelper.getExerciseId());

        int competitions = Integer.parseInt(user_exercise_data.get("competitions"));
        int wins = Integer.parseInt(user_exercise_data.get("wins"));
        int draws = Integer.parseInt(user_exercise_data.get("draws"));
        int defeats = competitions - wins - draws;
        tvUserCompetitionWins.setText(String.format(Locale.ROOT, "%d", wins));
        tvUserCompetitionDefeats.setText(String.format(Locale.ROOT, "%d", defeats));
        tvUserCompetitionDraws.setText(String.format(Locale.ROOT, "%d", draws));
        tvUserCompetitions.setText(String.format(Locale.ROOT, "%d", competitions));

        btSelectSpecialisation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogSelectSpecialisation();
                dialogSelectSpecialisation.show();
            }
        });
        refreshSpecialisationViews();
    }
}
