package beamoflight.sportintheforest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class StatWeekArrayAdapter extends ArrayAdapter<Stat> {
    private final Context context;
    private final List<Stat> values;
    private int maxValue;
    GameHelper gameHelper;

    public StatWeekArrayAdapter(Context context, List<Stat> values, int maxValue) {
        super(context, R.layout.stat_week_list_item, values);
        this.context = context;
        this.values = values;
        this.maxValue = maxValue;
        gameHelper = new GameHelper(getContext());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.stat_week_list_item, parent, false);

        TextView tvStatWeek = rowView.findViewById(R.id.tvStatWeek);
        tvStatWeek.setText(String.format(Locale.ROOT,"%d", values.get(position).getWeekOfYear()));

        String startOfLine = "      ";
        if (values.get(position).isCurrentPeriod()) {
            startOfLine = "=> ";
            tvStatWeek.setTypeface(null, Typeface.BOLD);
        }
        tvStatWeek.setText(String.format("%s%s", startOfLine, tvStatWeek.getText()));

        TextView tvStatValue = rowView.findViewById(R.id.tvStatValue);
        if (values.get(position).getValue() < (float) maxValue / 10) {
            tvStatValue.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            tvStatValue.setTextColor(Color.parseColor("#000000"));
        }

        tvStatValue.setText(String.format(Locale.ROOT,"%d", values.get(position).getValue()));

        ProgressBar pbStatWeek = rowView.findViewById(R.id.pbStatWeek);
        pbStatWeek.setMax(maxValue);
        pbStatWeek.setProgress(values.get(position).getValue());

        return rowView;
    }
}
