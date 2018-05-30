package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Frag3ListAdapter extends ArrayAdapter<ListSellNoticeItem> {
    //private Context context;
    protected ArrayList<ListSellNoticeItem> sellthinglist = new ArrayList<ListSellNoticeItem>();

    public Frag3ListAdapter(Context context, int textViewResourceId , ArrayList<ListSellNoticeItem> list) {
        super(context, textViewResourceId);
        sellthinglist = list;
    }

    @Override
    public int getCount() {
        return sellthinglist.size();
    }

    @Override
    public ListSellNoticeItem getItem(int position) {
        return sellthinglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = inflater.inflate(R.layout.frag1item, parent,false);

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag3item, null, false);

            holder = new ViewHolder();
            holder.tv1 = convertView.findViewById(R.id.frag3tv1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv1.setText(sellthinglist.get(position).getTitle());

        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
    }

}