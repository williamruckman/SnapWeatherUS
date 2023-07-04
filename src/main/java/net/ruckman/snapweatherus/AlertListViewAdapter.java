package net.ruckman.snapweatherus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.List;

public class AlertListViewAdapter extends ArrayAdapter<AlertListViewItem> {

    AlertListViewAdapter(Context context, List<AlertListViewItem> items) {
        super(context, R.layout.alert_listview_item, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.alert_listview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.event = convertView.findViewById(R.id.event);
            viewHolder.effective = convertView.findViewById(R.id.effective);
            viewHolder.expires = convertView.findViewById(R.id.expires);
            viewHolder.headline = convertView.findViewById(R.id.headline);
            viewHolder.description = convertView.findViewById(R.id.description);
            viewHolder.instruction = convertView.findViewById(R.id.instruction);

            convertView.setTag(viewHolder);

        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        AlertListViewItem item = getItem(position);

        viewHolder.event.setText(item.event);
        viewHolder.effective.setText(item.effective);
        viewHolder.expires.setText(item.expires);
        viewHolder.headline.setText(item.headline);
        viewHolder.description.setText(item.description);
        viewHolder.instruction.setText(item.instruction);

        return convertView;
    }

    private static class ViewHolder {
        TextView event;
        TextView effective;
        TextView expires;
        TextView headline;
        TextView description;
        TextView instruction;
    }
}
