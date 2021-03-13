package beamoflight.sportintheforest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SingleLineItemArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public SingleLineItemArrayAdapter(Context context, List<String> values) {
        super(context, R.layout.knowledge_list_item, values);
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
        View rowView = inflater.inflate(R.layout.single_line_list_item, parent, false);

        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        tvTitle.setText(String.format(Locale.ROOT, "%s", values.get(position)));
        return rowView;
    }
}
