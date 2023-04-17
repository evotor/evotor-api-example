package ru.qualitylab.evotor.evotortest6;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by power on 10.10.2017.
 */

public class CustomAdapter extends ArrayAdapter<CustomEditObject> {
    private Context mContext;
    private List<CustomEditObject> mItems;
    private ListViewHolder holder;

    CustomAdapter(Context context, int resourceId, List<CustomEditObject> objects) {
        super(context, resourceId, objects);
        this.mItems = objects;
        this.mContext = context;
    }

    private class ListViewHolder {
        TextView name, price, qty;
        Button btnRemove;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final int pos = position;
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.customeditrow, null);
            holder = new ListViewHolder();
            holder.name = (TextView) v.findViewById(R.id.tvName);
            holder.price = (TextView) v.findViewById(R.id.tvPrice);
            holder.qty = (TextView) v.findViewById(R.id.tvQty);
            holder.btnRemove = (Button) v.findViewById(R.id.btnRemove);
            v.setTag(holder);
        } else holder = (ListViewHolder) v.getTag();

        final CustomEditObject item = mItems.get(position);
        if (item != null) {
            holder.name.setText(item.getName());
            holder.qty.setText(item.getQty());
            holder.price.setText(item.getPrice());
            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItem(pos);
                }
            });
        }
        return v;
    }

    private void removeItem(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }
}