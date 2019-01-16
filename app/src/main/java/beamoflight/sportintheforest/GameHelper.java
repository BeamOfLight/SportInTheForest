package beamoflight.sportintheforest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
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

    private boolean startReplayLoop(Activity current_activity)
    {
        int replayPos = getSharedPreferencesInt("replay_pos", 0);
        Log.d("replay", "replayPos: " + replayPos);

        // Save new replay pos
        setSharedPreferencesInt("replay_pos", replayPos + 1);

        switch(replayPos) {
            case 0:
                Toast.makeText(context, "Начинаем", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Intent intent = new Intent(current_activity, UsersActivity.class);
                current_activity.startActivity(intent);
                return false;

            case 2:
                ListView lvNewUser = current_activity.findViewById(R.id.lvNewUser);
                lvNewUser.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

                break;
            case 3:
                Toast.makeText(context, "Завершаем", Toast.LENGTH_LONG).show();
                break;
            case 4:
                disableReplayMode();
                return false;
        }

        return true;
    }

    public void startReplay(final Activity current_activity)
    {
        currentReplayActivity = current_activity;
        initReplayHandler();
        initReplayTimerTask();
    }

    public boolean isReplayMode()
    {
        int replayIdx = getSharedPreferencesInt("replay_idx", 0);
        return (replayIdx > 0);
    }

    public void disableReplayMode()
    {
        setSharedPreferencesInt("replay_idx", 0);
    }

    public void enableReplayMode(int replay_idx)
    {
        setSharedPreferencesInt("replay_idx", replay_idx);
        setSharedPreferencesInt("replay_pos", 0);
    }

    private void initReplayTimerTask()
    {
        replaySecondsCounter = 0;
        replayTimerTask = new GameHelper.ReplayTimerTask();
        int replayPos = getSharedPreferencesInt("replay_pos", 0);
        int delay = 500;
        if (replayPos > 0) {
            delay = 3000;
        }
        replayTimer.scheduleAtFixedRate(replayTimerTask, delay, 3000);
    }

    private class ReplayTimerTask extends TimerTask {
        @Override
        public void run() {
            if (replaySecondsCounter > 100) {
                cancel();
            } else {
                replayHandler.sendEmptyMessage(0);
            }
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
        replayTimer.cancel();
        replayTimer.purge();
        if (replayTimerTask != null) {
            replayTimerTask.cancel();
        }
    }
}
