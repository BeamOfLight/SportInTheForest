package beamoflight.sportintheforest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by beamoflight on 30.05.17.
 */

public class GameHelper {
    public static final int RESULT_STATE_DEFEAT = 0;
    public static final int RESULT_STATE_DRAW = 1;
    public static final int RESULT_STATE_WIN = 2;
    public static final int RESULT_STATE_UNFINISHED = 3;
    public static final int RESULT_STATE_LEFT = 4;

    public static final int SPECIALISATION_NO = 0;
    public static final int SPECIALISATION_RESULT = 1;
    public static final int SPECIALISATION_RESISTANCE = 2;
    public static final int SPECIALISATION_REGENERATION = 3;

    private static final int REPLAY_TIMER_TICK = 100;
    private static final int REPLAY_TIMER_FIRST_TICK = 100;

    public static final String REPLAY_CMD_DELIMITER = " # ";
    public static final String REPLAY_ARG_DELIMITER = ";";

    private Context context;
    private Timer replayTimer;
    private TimerTask replayTimerTask;
    private Handler replayHandler;
    private ReplayActivity currentReplayActivity;
    private Drawable lastChangedDrawable;
    private View lastChangedView;

    public GameHelper(Context current)
    {
        context = current;
        replayTimer = new Timer(true);
    }

    public String getDateString(int field, int amount)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        calendar.add(field, amount);

        return date_format.format(calendar.getTime());
    }

    public String getCurrentYearString()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy", Locale.ROOT);

        return date_format.format(calendar.getTime());
    }

    public String getCurrentMonthString()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM", Locale.ROOT);

        return date_format.format(calendar.getTime());
    }

    public String getTodayString()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        return date_format.format(calendar.getTime());
    }

    public String getDayInWeekString(Date target_date)
    {
        SimpleDateFormat date_format = new SimpleDateFormat("E", Locale.ROOT);

        return date_format.format(target_date);
    }

    public String getDayInWeekStringRu(Date target_date)
    {
        String engDayIngWeekStr = getDayInWeekString(target_date);
        switch(engDayIngWeekStr) {
            case "Sun":
                return "Вс";
            case "Mon":
                return "Пн";
            case "Tue":
                return "Вт";
            case "Wed":
                return "Ср";
            case "Thu":
                return "Чт";
            case "Fri":
                return "Пт";
            case "Sat":
                return "Сб";
        }

        return "";
    }

    public String getDayInWeekString()
    {
        Calendar calendar = Calendar.getInstance();
        return getDayInWeekString(calendar.getTime());
    }

    public int getCurrentWeekResult()
    {
        return 0;
    }

    public String getCurrentWeekString()
    {
        Calendar first_january = Calendar.getInstance();
        first_january.setTimeZone( TimeZone.getTimeZone("Europe/Moscow"));
        first_january.set(Integer.parseInt(getCurrentYearString()), Calendar.JANUARY, 1, 0, 0, 0);
        String result = "";

        Calendar that_day = Calendar.getInstance();
        that_day.setTimeZone( TimeZone.getTimeZone("Europe/Moscow"));
        that_day.set(Calendar.DAY_OF_WEEK, that_day.getFirstDayOfWeek());
        that_day.add(Calendar.DATE, first_january.getFirstDayOfWeek());

        // date from
        int day_from = that_day.get(Calendar.DAY_OF_MONTH);
        String month_from = getMonthName(that_day.get(Calendar.MONTH) + 1);
        result += Integer.toString(day_from) + " " + month_from;

        // date to
        that_day.add(Calendar.DAY_OF_YEAR, 6);
        int day_to = that_day.get(Calendar.DAY_OF_MONTH);
        String month_to = getMonthName(that_day.get(Calendar.MONTH) + 1);
        result += " - " + Integer.toString(day_to) + " " + month_to;

        return result;
    }

    public String getTodayStringWithHours()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH", Locale.ROOT);

        return date_format.format(calendar.getTime());
    }

    public String getTodayTimestampString()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

        return date_format.format(cal.getTime());
    }

    public String getShortCurrentTime()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);

        return date_format.format(cal.getTime());
    }

    public String getSpecialisationName(int specialisation_id)
    {
        String specialisation_name = "";
        switch(specialisation_id)
        {
            case SPECIALISATION_NO:
                specialisation_name = "Нет";
                break;
            case SPECIALISATION_RESULT:
                specialisation_name = "Результат";
                break;
            case SPECIALISATION_RESISTANCE:
                specialisation_name = "Сопротивление";
                break;
            case SPECIALISATION_REGENERATION:
                specialisation_name = "Восстановление";
                break;
        }

        return specialisation_name;
    }

    public int getCachedUserLevel()
    {
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int encodedUserLevel = sPref.getInt(
            context.getResources().getString(R.string.preference_name_user_encoded_level),
            context.getResources().getInteger(R.integer.default_user_encoded_level)
        );

        return (int) Math.floor(encodedUserLevel / 10000.0);
    }

    public float getUserPercents()
    {
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int encodedUserLevel = sPref.getInt(
            context.getResources().getString(R.string.preference_name_user_encoded_level),
            context.getResources().getInteger(R.integer.default_user_encoded_level)
        );

        return (float) (encodedUserLevel % 10000.0) / 100;
    }

    public int getUserId()
    {
        int user_id;
        if (isReplayMode()) {
            user_id = getSharedPreferencesInt(
                    "replay_user_id",
                    context.getResources().getInteger(R.integer.default_user_id)
            );
        } else {
            user_id = getSharedPreferencesInt(
                    context.getResources().getString(R.string.preference_name_user_id),
                    context.getResources().getInteger(R.integer.default_user_id)
            );
        }

        return user_id;
    }

    public int getExerciseId()
    {
        return getSharedPreferencesInt(
                context.getResources().getString(R.string.preference_name_exercise_id),
                context.getResources().getInteger(R.integer.default_exercise_id)
        );
    }

    public int getSharedPreferencesInt(String key, int default_value)
    {
        SharedPreferences sPref = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
        );

        return sPref.getInt(key, default_value);
    }

    public void setSharedPreferencesInt(String key, int value)
    {
        SharedPreferences s_pref = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getSharedPreferencesString(String key, String default_value)
    {
        SharedPreferences sPref = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
        );

        return sPref.getString(key, default_value);
    }

    public void setSharedPreferencesString(String key, String value)
    {
        SharedPreferences s_pref = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveUserId2Preferences(int user_id)
    {
        setSharedPreferencesInt(
                context.getResources().getString(R.string.preference_name_user_id),
                user_id
        );
    }

    public void saveExerciseId2Preferences(int exercise_id)
    {
        setSharedPreferencesInt(
                context.getResources().getString(R.string.preference_name_exercise_id),
                exercise_id
        );
    }

    private String getLastSelectionKey(int user_id, int exercise_id)
    {
        return String.format(
                Locale.ROOT,
                "%s_%d_%d",
                context.getResources().getString(R.string.preference_name_last_selection),
                user_id,
                exercise_id
        );
    }
    public void saveLastSelection2Preferences(int user_id, int exercise_id, int result)
    {
        setSharedPreferencesInt(
                getLastSelectionKey(user_id, exercise_id),
                result
        );
    }

    public float getResistanceInPercents(int userResistance)
    {
        if (userResistance < 0) {
            userResistance = 0;
        } else if (userResistance > 10000) {
            userResistance = 10000;
        }

        return (float) (Math.sqrt((float) userResistance / 1.5));
    }

    public int getLastSelectionFromPreferences(int user_id, int exercise_id)
    {
        return getSharedPreferencesInt(
                getLastSelectionKey(user_id, exercise_id),
                context.getResources().getInteger(R.integer.default_last_selection)
        );
    }

    public int getTargetFitnessPointsDifference(int target_FP, int target_resistance, float opponentResult)
    {
        float target_resistance_in_percents = getResistanceInPercents(target_resistance);
        int target_FP_diff = Math.round(opponentResult * (1 - target_resistance_in_percents / 100));
        if (target_FP_diff > target_FP) {
            target_FP_diff = target_FP;
        }
        return target_FP_diff;
    }

    public void saveCurrentTabId2Preferences(int tab_id)
    {
        setSharedPreferencesInt(
                context.getResources().getString(R.string.preference_name_current_tab_id),
                tab_id
        );
    }

    public int getCurrentTabIdFromPreferences()
    {
        return getSharedPreferencesInt(
                context.getResources().getString(R.string.preference_name_current_tab_id),
                context.getResources().getInteger(R.integer.default_current_tab_id)
        );
    }

    public String getCompetitionStatus(int status)
    {
        switch(status)
        {
            case RESULT_STATE_WIN:
                return "Победа";
            case RESULT_STATE_DRAW:
                return "Ничья";
            case RESULT_STATE_DEFEAT:
                return "Поражение";
            case RESULT_STATE_UNFINISHED:
                return "Не окончено";
            case RESULT_STATE_LEFT:
                return "Игрок сдался";
            default:
                return null;
        }
    }

    public String getCorrectPointWordRU(int val)
    {
        int last_digit = val % 10;
        String word;
        if ((val > 9 && val <= 20) || last_digit == 0) {
            word = "очков";
        } else if (last_digit == 1) {
            word = "очко";
        } else if (last_digit >= 2 && last_digit <= 4){
            word = "очка";
        } else {
            word = "очков";
        }
        return word;
    }

    public Intent getIntent4refreshedView(Activity activity, int tab_id)
    {
        saveCurrentTabId2Preferences(tab_id);
        Intent intent = activity.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.overridePendingTransition(0, 0);
        activity.finish();

        activity.overridePendingTransition(0, 0);
        return intent;
    }

    public String getDeviceIMEI() {
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = telephonyManager.getDeviceId();
//            if (imei != null && !imei.isEmpty()) {
//                return imei;
//            } else {
//                return android.os.Build.SERIAL;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return "not_found";
    }

    public String getMonthName(int month){
        switch (month){
            case 1:
                return "Янв";
            case 2:
                return "Фев";
            case 3:
                return "Мар";
            case 4:
                return "Апр";
            case 5:
                return "Май";
            case 6:
                return "Июн";
            case 7:
                return "Июл";
            case 8:
                return "Авг";
            case 9:
                return "Сен";
            case 10:
                return "Окт";
            case 11:
                return "Ноя";
            case 12:
                return "Дек";
        }

        return "";
    }

    public List<String> getActivityList()
    {
        String[] values = {"main", "users", "user-exercises", "exercises", "settings"};
        return Arrays.asList(values);
    }

    private Class getActivityByString(Activity default_activity, String str_id)
    {
        Class new_activity = default_activity.getClass();
        switch (str_id) {
            case "main":
                new_activity = Main4ReplayActivity.class;
                break;
            case "users":
                new_activity = UsersActivity.class;
                break;
            case "user-exercises":
                new_activity = UserExercisesActivity.class;
                break;
            case "settings":
                new_activity = SettingsActivity.class;
                break;
            case "exercises":
                new_activity = ExercisesActivity.class;
                break;
        }

        return new_activity;
    }

    public List<String> getResourcesList()
    {
        String[] values = {"lvNewItem", "llMenuStart", "llMenuSettings", "llMenuKnowledge"};
        return Arrays.asList(values);
    }

    public List<String> getResourcesListLV()
    {
        String[] values = {"lvNewItem"};
        return Arrays.asList(values);
    }

    private int getResourceViewId(String str_id)
    {
        int view_id = 0;
        switch (str_id) {
            case "lvNewItem":
                view_id = R.id.lvItemsTop;
                break;
            case "btMenuStart":
                view_id = R.id.btMenuStart;
                break;
            case "llMenuStart":
                view_id = R.id.llMenuStart;
                break;
            case "llMenuSettings":
                view_id = R.id.llMenuSettings;
                break;
            case "llMenuKnowledge":
                view_id = R.id.llMenuKnowledge;
                break;
        }

        return view_id;
    }

    public List<String> getDrawableList()
    {
        String[] values = {"colorAccent", "replay_border", "replay_border_inv"};
        return Arrays.asList(values);
    }

    private int getResourceDrawableId(String str_id)
    {
        int drawable_id = R.color.titleColor;
        switch (str_id) {
            case "colorAccent":
                drawable_id = R.color.colorAccent;
                break;
            case "mipmap/leaf_button_1":
                drawable_id = R.mipmap.leaf_button_1;
                break;
            case "mipmap/leaf_button_1_red":
                drawable_id = R.mipmap.leaf_button_1_red;
                break;
            case "replay_border":
                drawable_id = R.drawable.replay_border;
                break;
            case "replay_border_inv":
                drawable_id = R.drawable.replay_border_invisible;
                break;

        }

        return drawable_id;
    }
/*
toast_long;TEXT;TICKS
toast;TEXT;TICKS
activity;users;TICKS
activity;users;5;TICKS
exit
 */
    private boolean startReplayLoop(ReplayActivity current_activity)
    {
        int replay_wait_ticks = getSharedPreferencesInt("replay_wait_ticks", 0);
        Log.d("replay", "replay_wait_ticks: " + replay_wait_ticks);
        if (replay_wait_ticks > 0) {
            setSharedPreferencesInt("replay_wait_ticks", replay_wait_ticks - 1);
            return true;
        }

        int replay_pos = getSharedPreferencesInt("replay_pos", 0);
        Log.d("replay", "replay_pos: " + replay_pos);

        // Save new replay pos
        setSharedPreferencesInt("replay_pos", replay_pos + 1);

        String replay_record = getSharedPreferencesString(getReplayRecordName(replay_pos), "");
        String[] replay_record_parts = replay_record.split(REPLAY_ARG_DELIMITER);
        String cmd;
        if (replay_record_parts.length >= 1) {
            cmd = replay_record_parts[0];
            switch(cmd) {
                case "toast-long":
                    if (replay_record_parts.length == 3) {
                        Toast.makeText(context, replay_record_parts[1], Toast.LENGTH_LONG).show();
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[2]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "toast":
                    if (replay_record_parts.length == 3) {
                        Toast.makeText(context, replay_record_parts[1], Toast.LENGTH_SHORT).show();
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[2]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "activity":
                    if (replay_record_parts.length == 3) {
                        Class new_activity = getActivityByString(
                                current_activity,
                                replay_record_parts[1]
                        );

                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[2]));
                        Intent intent = new Intent(current_activity, new_activity);
                        current_activity.startActivity(intent);
                        setSharedPreferencesInt(
                                "replay_opened_activities",
                                getSharedPreferencesInt(
                                        "replay_opened_activities",
                                        0
                                ) + 1
                        );
                        //setReplayBorder(false);
                        return false;
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                case "activity-action":
                    if (replay_record_parts.length == 4) {
                        //setReplayBorder(false);
                        setSharedPreferencesInt(
                                "replay_opened_activities",
                                getSharedPreferencesInt(
                                        "replay_opened_activities",
                                        0
                                ) + 1
                        );
                        Class new_activity = getActivityByString(
                                current_activity,
                                replay_record_parts[1]
                        );

                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[3]));
                        Intent intent = new Intent(current_activity, new_activity);
                        intent.setAction(replay_record_parts[2]);
                        current_activity.startActivity(intent);
                        return false;
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "background":
                    if (replay_record_parts.length == 4) {
                        int view_id = getResourceViewId(replay_record_parts[1]);
                        int drawable_id = getResourceDrawableId(replay_record_parts[2]);
                        Log.d("replay", "view_id: " + view_id);
                        Log.d("replay", "drawable_id: " + drawable_id);
                        View view = current_activity.findViewById(view_id);
                        if (view != null) {
                            lastChangedView = view;
                            lastChangedDrawable = view.getBackground();
                            view.setBackground(context.getDrawable(drawable_id));
                        } else {
                            Log.d("replay", String.format("[%s] Empty view", cmd));
                        }
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[3]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "revert-background":
                    if (replay_record_parts.length == 2) {
                        if (lastChangedView != null && lastChangedDrawable != null) {
                            lastChangedView.setBackground(lastChangedDrawable);
                        }
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[1]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "event1":
                    if (replay_record_parts.length == 2) {
                        current_activity.replayEvent1();
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[1]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "event2":
                    if (replay_record_parts.length == 2) {
                        current_activity.replayEvent2();
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[1]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "event3":
                    if (replay_record_parts.length == 2) {
                        current_activity.replayEvent3();
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[1]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;

                case "lv-item-background":
                    if (replay_record_parts.length == 5) {
                        int view_id = getResourceViewId(replay_record_parts[1]);
                        int drawable_id = getResourceDrawableId(replay_record_parts[2]);
                        Log.d("replay", "view_id: " + view_id);
                        Log.d("replay", "drawable_id: " + drawable_id);
                        View view = current_activity.findViewById(view_id);
                        if (view != null) {
                            ListView lv_view = (ListView) view;
                            lastChangedView = lv_view;
                            lastChangedDrawable = lv_view.getBackground();
                            int child_id = Integer.parseInt(replay_record_parts[3]);
                            lv_view.getChildAt(child_id).setBackground(context.getDrawable(drawable_id));
                        } else {
                            Log.d("replay", String.format("[%s] Empty view", cmd));
                        }
                        setSharedPreferencesInt("replay_wait_ticks", Integer.parseInt(replay_record_parts[4]));
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "login":
                    setSharedPreferencesInt("replay_user_id", getSharedPreferencesInt("default_replay_user_id", 0));
                    break;
                case "unlogin":
                    setSharedPreferencesInt("replay_user_id", 0);
                    break;
                default:
                case "exit":
                    disableReplayMode();
                    return false;
            }
        } else {
            Log.e("replay", "replay_record_parts.length: " + Integer.toString(replay_record_parts.length));
            for (String replay_record_part : replay_record_parts) {
                Log.e("replay", "=> " + replay_record_part);
            }
        }

        return true;
    }

    private int getReplayDuration(String replay_string)
    {
        int duration = 0;
        String[] replay_records = replay_string.split(REPLAY_CMD_DELIMITER);
        for (String replay_record : replay_records) {
            String[] replay_record_parts = replay_record.split(REPLAY_ARG_DELIMITER);
            if (replay_record_parts.length >= 1) {
                switch(replay_record_parts[0]) {
                    case "toast-long":
                    case "toast":
                        if (replay_record_parts.length == 3) {
                            try {
                                duration += Integer.parseInt(replay_record_parts[replay_record_parts.length - 1]);
                            } catch (Exception e){}
                        }
                        break;
                    case "revert-background":
                    case "event1":
                    case "event2":
                    case "event3":
                        if (replay_record_parts.length == 2) {
                            try {
                                duration += Integer.parseInt(replay_record_parts[replay_record_parts.length - 1]);
                            } catch (Exception e){}
                        }
                        break;
                    case "activity":
                    case "activity-action":
                        duration++;
                    case "background":
                    case "lv-item-background":
                        if (replay_record_parts.length == 4) {
                            try {
                                duration += Integer.parseInt(replay_record_parts[replay_record_parts.length - 1]);
                            } catch (Exception e){}
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return duration;
    }

    public void startReplay(final ReplayActivity current_activity)
    {
        if (isReplayMode()) {
            currentReplayActivity = current_activity;
            initReplayHandler();
            initReplayTimerTask();

            setReplayBorder(true);
        }
    }

    public void setReplayBorder(boolean visible)
    {
        int drawable_id = R.drawable.replay_border;
        if (!visible) {
            drawable_id = R.drawable.replay_border_invisible;
        }

        if (currentReplayActivity != null) {
            View view = currentReplayActivity.findViewById(R.id.llBorder);
            if (view != null) {
                view.setBackground(context.getDrawable(drawable_id));
            }
        }
    }

    public boolean isReplayMode()
    {
        return (getSharedPreferencesInt("replay_enable", 0) > 0);
    }

    public void disableReplayMode()
    {
        Log.d("replay", "disableReplayMode: " + isReplayMode());
        if (isReplayMode()) {
            setSharedPreferencesInt("replay_enable", 0);
            int replay_opened_activities = getSharedPreferencesInt("replay_opened_activities", 0);
            if (replay_opened_activities > 0) {
                currentReplayActivity.finish();
            } else {
                setReplayBorder(false);
            }
        }
    }

    public void enableReplayMode(final ReplayActivity current_activity, String replay_string)
    {
        if (!isReplayMode()) {
            setSharedPreferencesInt("replay_user_id", getSharedPreferencesInt("default_replay_user_id", 0));
            setSharedPreferencesInt("replay_opened_activities", 0);
            setSharedPreferencesInt("replay_enable", 1);
            setSharedPreferencesInt("replay_pos", 0);
            setSharedPreferencesInt("replay_wait_ticks", 0);
            setSharedPreferencesInt("replay_ticks", 0);
            setSharedPreferencesInt("replay_duration", getReplayDuration(replay_string));
            String[] replay_records = replay_string.split(REPLAY_CMD_DELIMITER);
            int idx = 0;
            for (String replay_record : replay_records) {
                setSharedPreferencesString(getReplayRecordName(idx), replay_record);
                idx++;
            }
            startReplay(current_activity);
        }
    }

    private String getReplayRecordName(int idx)
    {
        return String.format(Locale.ROOT,"replay_record_%02d", idx);
    }

    private void initReplayTimerTask()
    {
        replayTimerTask = new GameHelper.ReplayTimerTask();
        int replay_pos = getSharedPreferencesInt("replay_pos", 0);
        int delay = REPLAY_TIMER_FIRST_TICK;
        if (replay_pos > 0) {
            delay = REPLAY_TIMER_TICK;
        }
        replayTimer.scheduleAtFixedRate(replayTimerTask, delay, REPLAY_TIMER_TICK);
    }

    private class ReplayTimerTask extends TimerTask {
        @Override
        public void run() {
            replayHandler.sendEmptyMessage(0);
        }
    }


    private void initReplayHandler()
    {
        replayHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //Log.d("replay", "handleMessage " + Integer.toString(replaySecondsCounter));

                int ticks = getSharedPreferencesInt("replay_ticks", 0);
                setSharedPreferencesInt("replay_ticks", ticks + 1);

                //Log.d("replay", "handleMessage.isReplayMode: " + (isReplayMode()? "true" : "false"));
                boolean res = startReplayLoop(currentReplayActivity);
                if (!res) {
                    removeReplayTimerTask();
                    int replay_opened_activities = getSharedPreferencesInt("replay_opened_activities", 0);
                    if (replay_opened_activities > 1) {
                        currentReplayActivity.finish();
                    }
                    //setReplayBorder(false);
                }
            };
        };
    }

    public void removeReplayTimerTask()
    {
        Log.d("replay", "removeReplayTimerTask");
        if (replayTimerTask != null) {
            replayTimerTask.cancel();
        }
    }

    public void removeReplayTimer()
    {
        replayTimer.cancel();
        replayTimer.purge();
    }
}
