package beamoflight.sportintheforest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class CharacterViewArrayAdapter extends ArrayAdapter<CharacterView> {
    private final Context context;
    private final List<CharacterView> values;

    public CharacterViewArrayAdapter(Context context, List<CharacterView> values) {
        super(context, R.layout.competition_player_list_item, values);
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
        View rowView = inflater.inflate(R.layout.competition_player_list_item, parent, false);

        TextView tvPlayerName = (TextView) rowView.findViewById(R.id.tvPlayerName);
        tvPlayerName.setText(String.format(Locale.ROOT, "%s", values.get(position).getName()));

        TextView tvPlayerFPInfo = (TextView) rowView.findViewById(R.id.tvPlayerFPInfo);
        tvPlayerFPInfo.setText(
                String.format(
                        Locale.ROOT,
                        "%d / %d",
                        values.get(position).getCurrentFitnessPoints(),
                        values.get(position).getInitialFitnessPoints()
                )
        );
        if (values.get(position).getCurrentFitnessPoints() < (float) values.get(position).getInitialFitnessPoints() / 2) {
            tvPlayerFPInfo.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            tvPlayerFPInfo.setTextColor(Color.parseColor("#000000"));
        }

        ProgressBar pbPlayerFP = (ProgressBar) rowView.findViewById(R.id.pbPlayerFP);
        pbPlayerFP.setMax(values.get(position).getInitialFitnessPoints());
        pbPlayerFP.setProgress(values.get(position).getCurrentFitnessPoints());

        return rowView;
    }
}
