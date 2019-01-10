package beamoflight.sportintheforest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;

public class TabsDailyStatOnlyActivity extends Activity {

    // Titles of the individual pages (displayed in tabs)
    private final String[] PAGE_TITLES = new String[] {
            "Статистика"
    };

    // The fragments that are used as the individual pages
    private final Fragment[] PAGES = new Fragment[] {
            new PageFragmentStat()
    };

    // The ViewPager is responsible for sliding pages (fragments) in and out upon user input
    private ViewPager mViewPager;

    AlertDialog dialogAddDayInfo;
    DBHelper dbHelper;
    GameHelper gameHelper;


    TextView tvExerciseName;
    Button btAddTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_daily_stat_only);

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

        tvExerciseName = findViewById(R.id.tvExerciseName);
        btAddTraining = findViewById(R.id.btAddTraining);

        btAddTraining.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogAddDayInfo();
            }
        });
    }

    private void initDialogAddDayInfo()
    {
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_add_day_info, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        final NumberPicker npSaladsCount = prompts_view.findViewById(R.id.npSaladsCount);
        final CalendarView cvDate = prompts_view.findViewById(R.id.cvDate);
        final Calendar calendar = new GregorianCalendar();

        cvDate.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);

                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                String selectedDate = date_format.format(calendar.getTime());

                Map<String, String> m = dbHelper.getFirstTrainingByDate(gameHelper.getUserId(), gameHelper.getExerciseId(), selectedDate);
                int sum_result = Integer.parseInt(m.get("sum_result"));
                npSaladsCount.setValue(sum_result);
            }
        });

        Map<String, String> m = dbHelper.getFirstTrainingByDate(gameHelper.getUserId(), gameHelper.getExerciseId(), gameHelper.getTodayString());
        int sum_result = Integer.parseInt(m.get("sum_result"));

        npSaladsCount.setMinValue(0);
        npSaladsCount.setMaxValue(100);
        npSaladsCount.setValue(sum_result);

        // set dialog message
        alert_dialog_builder
                .setCancelable(false)
                .setPositiveButton(R.string.btn_save_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                                String selectedDate = date_format.format(calendar.getTime());

                                int result = npSaladsCount.getValue();
                                Map<String, String> m = dbHelper.getFirstTrainingByDate(gameHelper.getUserId(), gameHelper.getExerciseId(), selectedDate);
                                int training_id = Integer.parseInt(m.get("training_id"));
                                if (training_id == 0) {
                                    dbHelper.addTraining(gameHelper.getUserId(), gameHelper.getExerciseId(), 0, result, result, 0, 1, GameHelper.RESULT_STATE_WIN, 0,0, 0, false, 0, 0, selectedDate);
                                } else {
                                    dbHelper.updateTrainingResult(
                                            training_id,
                                            gameHelper.getUserId(),
                                            gameHelper.getExerciseId(),
                                            result
                                    );
                                }
                            }
                        })
                .setNegativeButton(R.string.btn_cancel_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialogAddDayInfo = alert_dialog_builder.create();
        dialogAddDayInfo.show();
    }

    protected void onStart() {
        super.onStart();

        tvExerciseName.setText(dbHelper.getExerciseName(gameHelper.getExerciseId()));
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
