package beamoflight.sportintheforest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    final int REPLAY_TIMER_TICK = 1000;
    final int REPLAY_TIMER_FIRST_TICK = 500;

    private Context context;
    private Timer replayTimer;
    TimerTask replayTimerTask;
    private Handler replayHandler;
    private int replaySecondsCounter;
    private Activity currentReplayActivity;

    public GameHelper(Context current)
    {
        context = current;
        replaySecondsCounter = 0;
        replayTimer = new Timer(true);
    }

    public String getDateString(int field, int amount)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        calendar.add(field, amount);

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
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

        return sPref.getInt(
                context.getResources().getString(R.string.preference_name_user_id),
                context.getResources().getInteger(R.integer.default_user_id)
        );
    }

    public int getExerciseId()
    {
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

        return sPref.getInt(
                context.getResources().getString(R.string.preference_name_exercise_id),
                context.getResources().getInteger(R.integer.default_exercise_id)
        );
    }

    public int getSharedPreferencesInt(String key, int default_value)
    {
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        return sPref.getInt(key, default_value);
    }

    public void setSharedPreferencesInt(String key, int value)
    {
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getSharedPreferencesString(String key, String default_value)
    {
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        return sPref.getString(key, default_value);
    }

    public void setSharedPreferencesString(String key, String value)
    {
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveUserId2Preferences(int user_id)
    {
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(context.getResources().getString(R.string.preference_name_user_id), user_id);
        editor.commit();
    }

    public void saveExerciseId2Preferences(int exercise_id)
    {
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(context.getResources().getString(R.string.preference_name_exercise_id), exercise_id);
        editor.commit();
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
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(getLastSelectionKey(user_id, exercise_id), result);
        editor.commit();
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
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

        return sPref.getInt(
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
        SharedPreferences s_pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s_pref.edit();
        editor.putInt(context.getResources().getString(R.string.preference_name_current_tab_id), tab_id);
        editor.commit();
    }

    public int getCurrentTabIdFromPreferences()
    {
        SharedPreferences sPref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

        return sPref.getInt(
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
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
/*
toast_long;TEXT
toast;TEXT
activity;users
activity;users;5
pass
exit
 */
    private boolean startReplayLoop(Activity current_activity)
    {
        int replayPos = getSharedPreferencesInt("replay_pos", 0);
        Log.d("replay", "replayPos: " + replayPos);

        // Save new replay pos
        setSharedPreferencesInt("replay_pos", replayPos + 1);

        String replay_record = getSharedPreferencesString(getReplayRecordName(replayPos), "");
        String[] replay_record_parts = replay_record.split(";");
        String cmd;
        if (replay_record_parts.length >= 1 && replay_record_parts.length <= 3) {
            cmd = replay_record_parts[0];
            switch(cmd) {
                case "toast_long":
                    if (replay_record_parts.length == 2) {
                        Toast.makeText(context, replay_record_parts[1], Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "toast":
                    Toast.makeText(context, replay_record_parts[1], Toast.LENGTH_SHORT).show();
                    break;
                case "activity":
                    setReplayBorder(false);
                    setSharedPreferencesInt("close_last_activity", 1);
                    if (replay_record_parts.length >= 2) {
                        Class new_activity = current_activity.getClass();
                        switch (replay_record_parts[1]) {
                            case "users":
                                new_activity = UsersActivity.class;
                                break;
                        }

                        Intent intent = new Intent(current_activity, new_activity);
                        if (replay_record_parts.length == 3) {
                            intent.setAction(replay_record_parts[2]);
                        }
                        current_activity.startActivity(intent);
                        return false;
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }

                case "bgcolor":
                    if (replay_record_parts.length == 3) {
                        int view_id = 0;
                        switch (replay_record_parts[1]) {
                            case "lvNewItem":
                                view_id = R.id.lvItemsTop;
                                break;
                        }

                        int color_id = R.color.titleColor;
                        switch (replay_record_parts[2]) {
                            case "colorAccent":
                                color_id = R.color.colorAccent;
                                break;
                        }
                        Log.d("replay", "view_id: " + view_id);
                        Log.d("replay", "color_id: " + color_id);
                        View view = current_activity.findViewById(view_id);
                        if (view != null) {
                            view.setBackgroundColor(context.getResources().getColor(color_id, context.getTheme()));
                        } else {
                            Log.d("replay", String.format("[%s] Empty view", cmd));
                        }
                    } else {
                        Log.d("replay", String.format("[%s] Wrong arguments count", cmd));
                    }
                    break;
                case "pass":
                    break;
                case "exit":
                    setReplayBorder(false);
                    int close_last_activity = getSharedPreferencesInt("close_last_activity", 0);
                    if (close_last_activity == 1) {
                        current_activity.finish();
                    }
                    disableReplayMode();
                    return false;
                default:
                    Log.d("replay", String.format("Wrong command: %s", cmd));
                    break;
            }
        } else {
            Log.e("replay", "replay_record_parts.length: " + Integer.toString(replay_record_parts.length));
            for (String replay_record_part : replay_record_parts) {
                Log.e("replay", "=> " + replay_record_part);
            }
        }

        return true;
    }

    public void startReplay(final Activity current_activity)
    {
        if (isReplayMode()) {
            currentReplayActivity = current_activity;
            initReplayHandler();
            initReplayTimerTask();

            setReplayBorder(true);
        }
    }

    private void setReplayBorder(boolean visible)
    {
        int drawable_id = R.drawable.replay_border;
        if (!visible) {
            drawable_id = R.drawable.replay_border_invisible;
        }

        View view = currentReplayActivity.findViewById(R.id.llBorder);
        if (view != null) {
            view.setBackground(context.getDrawable(drawable_id));
        }
    }

    public boolean isReplayMode()
    {
        return (getSharedPreferencesInt("replay_enable", 0) > 0);
    }

    public void disableReplayMode()
    {
        Log.d("replay", "disableReplayMode");
        setSharedPreferencesInt("replay_enable", 0);
    }

    public void enableReplayMode(final Activity current_activity, String replay_string)
    {
        if (!isReplayMode()) {
            setSharedPreferencesInt("close_last_activity", 0);
            setSharedPreferencesInt("replay_enable", 1);
            setSharedPreferencesInt("replay_pos", 0);
            String[] replay_records = replay_string.split(" # ");
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
        replaySecondsCounter = 0;
        replayTimerTask = new GameHelper.ReplayTimerTask();
        int replayPos = getSharedPreferencesInt("replay_pos", 0);
        int delay = REPLAY_TIMER_FIRST_TICK;
        if (replayPos > 0) {
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
                Log.d("replay", Integer.toString(replaySecondsCounter));
                replaySecondsCounter++;

                boolean res = startReplayLoop(currentReplayActivity);
                if (!res) {
                    removeReplayTimerTask();
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
