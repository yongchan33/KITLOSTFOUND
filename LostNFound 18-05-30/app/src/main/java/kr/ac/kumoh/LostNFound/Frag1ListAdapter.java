package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Frag1ListAdapter extends ArrayAdapter<ListFindNoticeItem> {
    //private Context context;
    protected ArrayList<ListFindNoticeItem> findthinglist = new ArrayList<ListFindNoticeItem>();

    public Frag1ListAdapter(Context context, int textViewResourceId , ArrayList<ListFindNoticeItem> list) {
        super(context, textViewResourceId);
        findthinglist = list;
    }

    @Override
    public int getCount() {
        return findthinglist.size();
    }

    @Override
    public ListFindNoticeItem getItem(int position) {
        return findthinglist.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag1item, null, false);

            holder = new ViewHolder();
            holder.tv1 = convertView.findViewById(R.id.frag1tv1);
            holder.tv2 = convertView.findViewById(R.id.frag1tv2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv1.setText(findthinglist.get(position).getTitle());
        holder.tv2.setText(findthinglist.get(position).getReward());

        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
    }

}