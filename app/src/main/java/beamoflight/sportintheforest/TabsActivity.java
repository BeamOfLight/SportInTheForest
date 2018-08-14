package beamoflight.sportintheforest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class TabsActivity extends Activity {

    // Titles of the individual pages (displayed in tabs)
    private final String[] PAGE_TITLES = new String[] {
            "Статистика",
            "Соревнования",
            "Навыки",
            "Достижения",
            "История",
            "Места"
    };

    // The fragments that are used as the individual pages
    private final Fragment[] PAGES = new Fragment[] {
            new PageFragmentStat(),
            new PageFragmentCompetitions(),
            new PageFragmentSkills(),
            new PageFragmentAchievements(),
            new PageFragmentHistory(),
            new PageFragmentLocations()
    };

    // The ViewPager is responsible for sliding pages (fragments) in and out upon user input
    private ViewPager mViewPager;

    DBHelper dbHelper;
    GameHelper gameHelper;

    ProgressBar pbUserExp;
    TextView tvExerciseName, tvUserLevel, tvUserPercentsAndExp;
    TextView tvUserFitnessPoints, tvUserResistance, tvUserMultiplier, tvUserBonusChance, tvUserBonusMultiplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(getBaseContext());

        // Set the Toolbar as the activity's app bar (instead of the default ActionBar)
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Connect the ViewPager to our custom PagerAdapter. The PagerAdapter supplies the pages
        // (fragments) to the ViewPager, which the ViewPager needs to display.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getFragmentManager()));
        mViewPager.setCurrentItem(gameHelper.getCurrentTabIdFromPreferences());
        gameHelper.saveCurrentTabId2Preferences(0);

        // Connect the tabs with the ViewPager (the setupWithViewPager method does this for us in
        // both directions, i.e. when a new tab is selected, the ViewPager switches to this page,
        // and when the ViewPager switches to a new page, the corresponding tab is selected)
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pbUserExp = (ProgressBar) findViewById(R.id.pbUserExp);
        tvUserLevel = (TextView) findViewById(R.id.tvUserLevel);
        tvExerciseName = (TextView) findViewById(R.id.tvExerciseName);
        tvUserPercentsAndExp = (TextView) findViewById(R.id.tvUserPercentsAndExp);

        tvUserFitnessPoints = (TextView) findViewById(R.id.tvUserFitnessPoints);
        tvUserResistance = (TextView) findViewById(R.id.tvUserResistance);
        tvUserMultiplier = (TextView) findViewById(R.id.tvUserMultiplier);
        tvUserBonusChance = (TextView) findViewById(R.id.tvUserBonusChance);
        tvUserBonusMultiplier = (TextView) findViewById(R.id.tvUserBonusMultiplier);

        tvUserLevel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TabsActivity.this, PlayerExpActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
        showUserInfo();
    }

    public void showUserInfo()
    {
        dbHelper.updateUserInfo();

        int user_level = gameHelper.getCachedUserLevel();
        float user_percents = gameHelper.getUserPercents();

        pbUserExp.setProgress(Math.round(user_percents));
        tvUserLevel.setText(String.format(Locale.ROOT, "%d", user_level));
        tvUserPercentsAndExp.setText(String.format(Locale.ROOT, "%2.2f%% | %d", user_percents, dbHelper.getUserExerciseExp(gameHelper.getUserId(), gameHelper.getExerciseId())));
        if (user_percents < 50) {
            tvUserPercentsAndExp.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            tvUserPercentsAndExp.setTextColor(Color.parseColor("#000000"));
        }

        tvUserFitnessPoints.setText(String.format(Locale.ROOT, "%d", dbHelper.getCurrentUserFitnessPoints()));
        tvUserResistance.setText(
                String.format(
                        Locale.ROOT,
                        "%d ( %.1f%% )",
                        dbHelper.getCurrentUserResistance(),
                        gameHelper.getResistanceInPercents(dbHelper.getCurrentUserResistance())
                )
        );
        tvUserMultiplier.setText(String.format(Locale.ROOT, "%.2f", dbHelper.getCurrentUserMultiplier()));
        tvUserBonusChance.setText(String.format(Locale.ROOT, "%.1f%%", 100.0 * dbHelper.getCurrentUserBonusChance()));
        tvUserBonusMultiplier.setText(String.format(Locale.ROOT, "%.2f", dbHelper.getCurrentUserBonusMultiplier()));
    }

    /* PagerAdapter for supplying the ViewPager with the pages (fragments) to display. */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) { return PAGES[position]; }

        @Override
        public int getCount() {
            return PAGES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }

    }
}
