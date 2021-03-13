package beamoflight.sportintheforest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class LocationPositionItemArrayAdapter extends ArrayAdapter<LocationPositionEntity> {
    private final Context context;
    private final List<LocationPositionEntity> values;

    public LocationPositionItemArrayAdapter(Context context, List<LocationPositionEntity> values) {
        super(context, R.layout.npc_list_item, values);
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
        View rowView = inflater.inflate(R.layout.npc_list_item, parent, false);
        TextView tvNPCName = (TextView) rowView.findViewById(R.id.tvNPCName);
        tvNPCName.setText(values.get(position).name);

        TextView tvNPCInfo = (TextView) rowView.findViewById(R.id.tvNPCInfo);
        tvNPCInfo.setText(values.get(position).info);

        int quest_expected_wins = values.get(position).getQuestCnt();
        int quest_current_wins = values.get(position).getWins();
        ProgressBar pbQuestProgress = (ProgressBar) rowView.findViewById(R.id.pbQuestProgress);
        pbQuestProgress.setMax(quest_expected_wins);
        pbQuestProgress.setProgress(quest_current_wins);

        TextView tvQuestInfo = (TextView) rowView.findViewById(R.id.tvQuestInfo);
        if (quest_current_wins >= quest_expected_wins) {
          tvQuestInfo.setTextSize(14);
          tvQuestInfo.setText("готово");
        } else {
          tvQuestInfo.setText(
            String.format(Locale.ROOT, "%d / %d", quest_current_wins, quest_expected_wins)
          );
        }


        return rowView;
    }
}
