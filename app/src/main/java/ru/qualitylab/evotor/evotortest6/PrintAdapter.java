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

public class PrintAdapter extends ArrayAdapter<PrintData> {
    private Context mContext;
    private List<PrintData> mItems;
    private ListViewHolder holder;

    PrintAdapter(Context context, int resourceId, List<PrintData> objects) {
        super(context, resourceId, objects);
        this.mItems = objects;
        this.mContext = context;
    }

    private class ListViewHolder {
        TextView name, type;
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
            holder.type = (TextView) v.findViewById(R.id.tvPrice);
            holder.btnRemove = (Button) v.findViewById(R.id.btnRemove);
            v.setTag(holder);
        } else holder = (ListViewHolder) v.getTag();

        final PrintData item = mItems.get(position);
        if (item != null) {
            holder.name.setText(item.getData());
            holder.type.setText(item.getPrintType().toString());
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