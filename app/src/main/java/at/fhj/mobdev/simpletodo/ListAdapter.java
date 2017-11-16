package at.fhj.mobdev.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.fhj.mobdev.simpletodo.db.Todo;

public class ListAdapter extends BaseAdapter {

    private List<Todo> items;

    public ListAdapter() {
        items = new ArrayList<>();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<Todo> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(Todo item) {
        items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Todo getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ItemViewHolder holder = new ItemViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

            holder.title = convertView.findViewById(R.id.title);
            holder.description = convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        }

        Todo item = getItem(position);
        ItemViewHolder holder = (ItemViewHolder) convertView.getTag();

        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        return convertView;
    }

    private static class ItemViewHolder {

        private TextView title;

        private TextView description;
    }
}
