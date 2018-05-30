package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FindOwnerCmtListAdapter extends ArrayAdapter<ListCommentItem> {
    //private Context context;
    protected ArrayList<ListCommentItem> findownercommentlist = new ArrayList<ListCommentItem>();

    public FindOwnerCmtListAdapter(Context context, int textViewResourceId, ArrayList<ListCommentItem> list) {
        super(context, textViewResourceId);
        findownercommentlist = list;
    }

    @Override
    public int getCount() {
        return findownercommentlist.size();
    }

    @Override
    public ListCommentItem getItem(int position) {
        return findownercommentlist.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findownercmtitem, null, false);

            holder = new ViewHolder();
            holder.tv_writer = convertView.findViewById(R.id.focommentwriter);
            holder.tv_content = convertView.findViewById(R.id.focommentcontent);
            holder.tv_date = convertView.findViewById(R.id.focommentdate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_writer.setText(findownercommentlist.get(position).getCommnet_writer());
        holder.tv_content.setText(findownercommentlist.get(position).getComment_content());
        holder.tv_date.setText(findownercommentlist.get(position).getComment_date());

        return convertView;
    }

    class ViewHolder {
        TextView tv_writer;
        TextView tv_content;
        TextView tv_date;
    }

}