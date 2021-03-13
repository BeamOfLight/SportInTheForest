package beamoflight.sportintheforest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class SkillArrayAdapter extends ArrayAdapter<SkillView> {
    private final Context context;
    private final List<SkillView> values;

    public SkillArrayAdapter(Context context, List<SkillView> values) {
        super(context, android.R.layout.simple_list_item_2, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        SkillView active_skill = values.get(position);
        if (active_skill != null) {
            TextView tvText1 = (TextView) rowView.findViewById(android.R.id.text1);
            tvText1.setText(
                    String.format(
                        Locale.ROOT,
                        "%s Ур. %d",
                        active_skill.name,
                        active_skill.level
                    )
            );

            TextView tvText2 = (TextView) rowView.findViewById(android.R.id.text2);
            tvText2.setText(
                    String.format(
                            Locale.ROOT,
                            "Осталось ходов: %d",
                            active_skill.duration
                    )
            );
        }

        return rowView;
    }
}
