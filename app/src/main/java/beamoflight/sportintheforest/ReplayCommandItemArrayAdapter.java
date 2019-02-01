package beamoflight.sportintheforest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReplayCommandItemArrayAdapter extends ArrayAdapter<ReplayEditorActivity.ReplayCommand> {
    private final Context context;
    private final List<ReplayEditorActivity.ReplayCommand> values;

    public ReplayCommandItemArrayAdapter(Context context, ArrayList<ReplayEditorActivity.ReplayCommand> values) {
        super(context, R.layout.list_item_replay_command, values);
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
        View rowView = inflater.inflate(R.layout.list_item_replay_command, parent, false);

        String cmd_str = values.get(position).cmd;
        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        tvTitle.setText(String.format(Locale.ROOT, "%s", cmd_str));

        String info;
        switch (cmd_str) {
            case "event1":
            case "event2":
            case "event3":
            case "revert-background":
                info = String.format(
                        Locale.ROOT,
                        "%d",
                        values.get(position).ticks
                );
                break;
            case "activity":
            case "toast-long":
            case "toast":
                info = String.format(
                        Locale.ROOT,
                        "%s;%d",
                        values.get(position).arg1,
                        values.get(position).ticks
                );
                break;

            case "activity-action":
            case "background":
                info = String.format(
                        Locale.ROOT,
                        "%s;%s;%d",
                        values.get(position).arg1,
                        values.get(position).arg2,
                        values.get(position).ticks
                );
                break;
            case "lv-item-background":
                info = String.format(
                        Locale.ROOT,
                        "%s;%s;%s;%d",
                        values.get(position).arg1,
                        values.get(position).arg2,
                        values.get(position).arg3,
                        values.get(position).ticks
                );
                break;
            default:
            case "exit":
                info = "";
                break;
        }

        TextView tvInfo = rowView.findViewById(R.id.tvInfo);
        tvInfo.setText(info);

        return rowView;
    }
}
