package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SellThingCmtListAdapter extends ArrayAdapter<ListCommentItem> {
    //private Context context;
    protected ArrayList<ListCommentItem> sellthingcommentlist = new ArrayList<ListCommentItem>();

    public SellThingCmtListAdapter(Context context, int textViewResourceId, ArrayList<ListCommentItem> list) {
        super(context, textViewResourceId);
        sellthingcommentlist = list;
    }

    @Override
    public int getCount() {
        return sellthingcommentlist.size();
    }

    @Override
    public ListCommentItem getItem(int position) {
        return sellthingcommentlist.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sellthingcmtitem, null, false);

            holder = new ViewHolder();
            holder.tv_writer = convertView.findViewById(R.id.stcommentwriter);
            holder.tv_content = convertView.findViewById(R.id.stcommentcontent);
            holder.tv_date = convertView.findViewById(R.id.stcommentdate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_writer.setText(sellthingcommentlist.get(position).getCommnet_writer());
        holder.tv_content.setText(sellthingcommentlist.get(position).getComment_content());
        holder.tv_date.setText(sellthingcommentlist.get(position).getComment_date());
        
        return convertView;
    }

    class ViewHolder {
        TextView tv_writer;
        TextView tv_content;
        TextView tv_date;
    }

}