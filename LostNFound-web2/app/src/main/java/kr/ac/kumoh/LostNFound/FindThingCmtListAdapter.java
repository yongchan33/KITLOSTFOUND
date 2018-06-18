package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FindThingCmtListAdapter extends ArrayAdapter<ListCommentItem> {
    protected ArrayList<ListCommentItem> findthingcommentlist = new ArrayList<ListCommentItem>();

    public FindThingCmtListAdapter(Context context, int textViewResourceId, ArrayList<ListCommentItem> list) {
        super(context, textViewResourceId);
        findthingcommentlist = list;
    }

    @Override
    public int getCount() {
        return findthingcommentlist.size();
    }

    @Override
    public ListCommentItem getItem(int position) {
        return findthingcommentlist.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findthingcmtitem, null, false);

            holder = new ViewHolder();
            holder.tv_writer = convertView.findViewById(R.id.ftcommentwriter);
            holder.tv_content = convertView.findViewById(R.id.ftcommentcontent);
            holder.tv_date = convertView.findViewById(R.id.ftcommentdate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_writer.setText(findthingcommentlist.get(position).getCommnet_writer());
        holder.tv_content.setText(findthingcommentlist.get(position).getComment_content());
        holder.tv_date.setText(findthingcommentlist.get(position).getComment_date());

        return convertView;
    }

    class ViewHolder {
        TextView tv_writer;
        TextView tv_content;
        TextView tv_date;
    }

}