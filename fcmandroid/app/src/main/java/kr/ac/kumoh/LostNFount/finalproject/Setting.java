package kr.ac.kumoh.LostNFount.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Setting extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        mAuth = FirebaseAuth.getInstance();

        signout = (Button)findViewById(R.id.settingsignout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(Setting.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onBack(View view) {
        this.finish();
    }
}
