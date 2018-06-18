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

public class Frag2ListAdapter extends ArrayAdapter<ListFindNoticeItem> {
    private ImageLoader mImageLoader;
    private RequestQueue mQueue = null;

    protected ArrayList<ListFindNoticeItem> findonwerlist = new ArrayList<ListFindNoticeItem>();

    public Frag2ListAdapter(Context context, int textViewResourceId, ArrayList<ListFindNoticeItem> list) {
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
        mQueue = Volley.newRequestQueue(this.getContext());
        mImageLoader = new ImageLoader(mQueue, new BitmapLruCache());

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag2item, null, false);

            holder = new ViewHolder();
            holder.netiv = (NetworkImageView) convertView.findViewById(R.id.frag2img);
            holder.tv1 = convertView.findViewById(R.id.frag2tv1);
            holder.tv2 = convertView.findViewById(R.id.frag2tv2);
            holder.tv3 = convertView.findViewById(R.id.frag2tv3);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!findonwerlist.isEmpty()) {
            holder.netiv.setImageUrl(MainActivity.SERVERIP + findonwerlist.get(position).getImgpath() +
                    findonwerlist.get(position).getImgname1(), mImageLoader);
            holder.tv1.setText(findonwerlist.get(position).getTitle());
            holder.tv2.setText(findonwerlist.get(position).getType());
            holder.tv3.setText(findonwerlist.get(position).getDate());
            if (findonwerlist.get(position).getImgname1().toString().equals("")) {
                holder.netiv.setImageResource(R.drawable.noimage);
            }
        }

        return convertView;
    }

    class ViewHolder {
        NetworkImageView netiv; // 썸네일
        TextView tv1; // 게시물 제목
        TextView tv2; // 종류
        TextView tv3; // 등록 날짜
    }

}