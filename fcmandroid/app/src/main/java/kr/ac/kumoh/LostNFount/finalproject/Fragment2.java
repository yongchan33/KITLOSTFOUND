package kr.ac.kumoh.LostNFount.finalproject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment2 extends Fragment implements View.OnClickListener{
    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment2, container, false);
        Button write = (Button)view.findViewById(R.id.frag2write);

        write.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        Intent write = new Intent(Fragment2.this.getActivity(), WriteActivity.class);

        switch (view.getId()){
            case R.id.frag2write :
                startActivity(write);
                break;
        }
    }
}