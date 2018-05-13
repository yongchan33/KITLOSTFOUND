package kr.ac.kumoh.LostNFount.finalproject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment {
    /*private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();*/
    private ImageView imgview;
    private Button bt_write;
    private ListView listview;
    private TextView tv_frag1tv1, tv_frag1tv2;
    private ArrayList<list_item> noticeList;
    private MyListAdapter adapter;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;

    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment1, container, false);
        bt_write = (Button) view.findViewById(R.id.frag1write);
/*        tv_frag1tv1 = (TextView) view.findViewById(R.id.frag1tv1);
        tv_frag1tv2 = (TextView) view.findViewById(R.id.frag1tv2);*/
        imgview = (ImageView) view.findViewById(R.id.frag1img);
        listview = (ListView) view.findViewById(R.id.listview);

        noticeList = new ArrayList<list_item>();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference("Notice").child("title");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(noticeList != null) {
                    noticeList.clear();
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    noticeList.add(new list_item(data));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG: ", "데이터 읽기 실패", databaseError.toException());
            }
        });

       /* if (user != null) {
            // Name, email address, and profile photo Url
            String tt = user.getUid();
            String email = user.getEmail();

            tv_frag1tv1.setText(tt);
            tv_frag1tv2.setText(email);
        }*/

        bt_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent write = new Intent(getActivity(), WriteActivity.class);
                startActivity(write);
            }
        });

        adapter = new MyListAdapter(getActivity(), noticeList);
        listview.setAdapter(adapter);

        return view;
    }

    public ImageView getimgView() {
        return imgview;
    }

    public TextView getTv_frag1tv1() {
        return tv_frag1tv1;
    }
}