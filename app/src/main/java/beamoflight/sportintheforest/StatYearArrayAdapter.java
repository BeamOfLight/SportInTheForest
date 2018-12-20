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

public class StatYearArrayAdapter extends ArrayAdapter<Stat> {
    private final Context context;
    private final List<Stat> values;
    private int maxValue;

    public StatYearArrayAdapter(Context context, List<Stat> values, int maxValue) {
        super(context, R.layout.stat_shadow_month_list_item, values);
        this.context = context;
        this.values = values;
        this.maxValue = maxValue;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.stat_year_list_item, parent, false);

        TextView tvStatYear = rowView.findViewById(R.id.tvStatYear);
        tvStatYear.setText(
                String.format(
                        Locale.ROOT,
                        "%d %s",
                        values.get(position).getYear(),
                        values.get(position).getMonthName()
                )
        );

        String startOfLine = "      ";
        if (values.get(position).isCurrentPeriod()) {
            startOfLine = "=> ";
            tvStatYear.setTypeface(null, Typeface.BOLD);
        }
        tvStatYear.setText(String.format("%s%s", startOfLine, tvStatYear.getText()));

        TextView tvStatValue = rowView.findViewById(R.id.tvStatValue);
        if (values.get(position).getValue() < (float) maxValue / 10) {
            tvStatValue.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            tvStatValue.setTextColor(Color.parseColor("#000000"));
        }

        tvStatValue.setText(String.format(Locale.ROOT,"%d", values.get(position).getValue()));

        ProgressBar pbStatYear = rowView.findViewById(R.id.pbStatYear);
        pbStatYear.setMax(maxValue);
        pbStatYear.setProgress(values.get(position).getValue());

        return rowView;
    }
}
