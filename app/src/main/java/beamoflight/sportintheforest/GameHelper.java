package beamoflight.sportintheforest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    public GameHelper(Context current) {
        context = current;
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

    public String getDayInWeekString()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("E", Locale.ROOT);

        return date_format.format(calendar.getTime());
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
}
