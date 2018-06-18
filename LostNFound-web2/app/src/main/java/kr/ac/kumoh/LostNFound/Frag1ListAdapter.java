package kr.ac.kumoh.LostNFound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class Frag1ListAdapter extends ArrayAdapter<ListFindNoticeItem> {
    private ImageLoader mImageLoader;
    private RequestQueue mQueue = null;

    protected ArrayList<ListFindNoticeItem> findthinglist = new ArrayList<ListFindNoticeItem>();

    public Frag1ListAdapter(Context context, int textViewResourceId, ArrayList<ListFindNoticeItem> list) {
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
        mQueue = Volley.newRequestQueue(this.getContext());
        mImageLoader = new ImageLoader(mQueue, new BitmapLruCache());

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag1item, null, false);

            holder = new ViewHolder();
            holder.netiv = (NetworkImageView) convertView.findViewById(R.id.frag1img);
            holder.tv1 = convertView.findViewById(R.id.frag1tv1);
            holder.tv2 = convertView.findViewById(R.id.frag1tv2);
            holder.tv3 = convertView.findViewById(R.id.frag1tv3);
            holder.tv4 = convertView.findViewById(R.id.frag1tv4);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!findthinglist.isEmpty()) {
            holder.netiv.setImageUrl(MainActivity.SERVERIP + findthinglist.get(position).getImgpath() +
                    findthinglist.get(position).getImgname1(), mImageLoader);
            holder.tv1.setText(findthinglist.get(position).getTitle());
            holder.tv2.setText(findthinglist.get(position).getType());
            holder.tv3.setText(findthinglist.get(position).getReward());
            holder.tv4.setText(findthinglist.get(position).getDate());
            if (findthinglist.get(position).getImgname1().toString().equals("")) {
                holder.netiv.setImageResource(R.drawable.noimage);
            }
        }

        return convertView;
    }

    class ViewHolder {
        NetworkImageView netiv; // 썸네일
        TextView tv1; // 게시물 제목
        TextView tv2; // 종류
        TextView tv3; // 사례금
        TextView tv4; // 등록 날짜
    }
}
