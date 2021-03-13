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

public class KnowledgeCategoryArrayAdapter extends ArrayAdapter<Map<String, String>> {
    private final Context context;
    private final List<Map<String, String>> values;

    public static final int KNOWLEDGE_ITEM_TYPE_TEXT = 0;
    public static final int KNOWLEDGE_ITEM_TYPE_REPLAY = 1;

    public KnowledgeCategoryArrayAdapter(Context context, List<Map<String, String>> values) {
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
        View rowView = inflater.inflate(R.layout.knowledge_list_item, parent, false);

        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        tvTitle.setText(String.format(Locale.ROOT, "%s", values.get(position).get("name")));

        ImageView ivIcon = rowView.findViewById(R.id.ivIcon);
        ivIcon.setBackgroundResource(android.R.drawable.ic_dialog_dialer);

        return rowView;
    }
}
