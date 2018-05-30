package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Frag2ListAdapter extends ArrayAdapter<ListFindNoticeItem> {
    //private Context context;
    protected ArrayList<ListFindNoticeItem> findonwerlist = new ArrayList<ListFindNoticeItem>();

    public Frag2ListAdapter(Context context, int textViewResourceId , ArrayList<ListFindNoticeItem> list) {
        super(context, textViewResourceId);
        findonwerlist = list;
    }

    @Override
    public int getCount() {
        return findonwerlist.size();
    }

    @Override
    public ListFindNoticeItem getItem(int position) {
        return findonwerlist.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag2item, null, false);

            holder = new ViewHolder();
            holder.tv1 = convertView.findViewById(R.id.frag2tv1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv1.setText(findonwerlist.get(position).getTitle());

        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
    }

}