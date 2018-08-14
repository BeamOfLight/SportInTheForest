package beamoflight.sportintheforest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * Created by beamoflight on 23.06.17.
 */

public class PlayerExpActivity extends Activity {
    DBHelper dbHelper;
    GameHelper gameHelper;

    public void addCell(TableRow row, String text, int special)
    {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        switch(special)
        {
            case 1:
                tv.setBackgroundColor(Color.argb(50, 30, 200, 30));
                break;
            case 2:
                tv.setBackgroundColor(Color.argb(50, 30, 30, 200));
                break;
            case 3:
                tv.setBackgroundColor(Color.argb(50, 70, 70, 70));
                break;
            default:
                break;
        }

        row.addView(tv);
    }

    private String getReadableExp(String str_exp)
    {
        String result = str_exp;
        int digits_count = str_exp.length();
        if (digits_count > 4) {
            int groups_count = (digits_count - 1)/ 3;

            int main_value = Math.round(Float.parseFloat(str_exp.substring(0, digits_count - groups_count*3 + 2))) / 10;
            float main_fvalue = (float) main_value / 10;

            result = Float.toString(main_fvalue) + "e" + Integer.toString(groups_count * 3);
            /*for (int i = 0 ; i < groups_count; i++)
            {
                result += 'k';
            }*/
        }

        return result;//result.replace("kk", "m");
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_exp);

        dbHelper = new DBHelper(getBaseContext());
        gameHelper = new GameHelper(this.getBaseContext());
        TableLayout tl = (TableLayout) findViewById(R.id.tlPlayerExp);

        TableRow header = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(lp);

        addCell(header, " Уровень ", 3);
        addCell(header, " Опыт ", 3);
        //addCell(header, " Прирост ", 3);
        addCell(header, " Фитнес очки ", 3);
        addCell(header, " Сопротивление ", 3);
        addCell(header, " Множитель ", 3);
        addCell(header, " Множитель бонуса ", 3);
        addCell(header, " Шанс ", 3);

        tl.addView(header, 0);

        int user_level = gameHelper.getCachedUserLevel();
        int cur_row_number = 1;
        int special;
        ArrayList<Map<String, String>> levelsData = dbHelper.getLevelsData();
        List<Integer> skillLevels = Arrays.asList(5, 10, 14, 18, 21, 24, 27, 30, 32, 34, 36, 38, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60);

        //remove last row
        if (levelsData.size() > 0) {
            levelsData.remove(levelsData.size() - 1);
        }

        // loop
        for (Map<String, String> levelData : levelsData) {
            TableRow row = new TableRow(this);
            special = 0;
            if (user_level == Integer.parseInt(levelData.get("target_level"))) {
                special = 1;
            } else if (skillLevels.contains(cur_row_number)) {
                special = 2;
            }
            addCell(row, levelData.get("target_level"), special);
            addCell(row, getReadableExp(levelData.get("min_exp")), special);
            //addCell(row, levelData.get("diff_exp"), special);
            addCell(row, levelData.get("base_fp"), special);
            addCell(row, levelData.get("base_resistance"), special);
            addCell(row, String.format(Locale.ROOT, "%2.2f", Float.parseFloat(levelData.get("base_multiplier"))), special);
            addCell(row, String.format(Locale.ROOT, "%2.2f", Float.parseFloat(levelData.get("base_bonus_multiplier"))), special);
            addCell(row, String.format(Locale.ROOT, "%2.2f", Float.parseFloat(levelData.get("base_bonus_chance"))), special);



            tl.addView(row, cur_row_number);
            cur_row_number++;
        }
    }
}
