package kr.ac.kumoh.LostNFound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LeaveActivity extends AppCompatActivity {

    //private final static String tag = "";
    private String sid; // 로그인 한 아이디
    private String soriginpassword; // 현재 비밀번호
    private EditText et_password;
    private SharedPreferences setting;  // 초기 화면에서 자동 로그인에 설정된 값
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave);

        et_password = (EditText) findViewById(R.id.mypassword);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
    }

    public void onLeave(View view) {
        soriginpassword = et_password.getText().toString();

        if (soriginpassword.getBytes().length == 0)
            Toast.makeText(LeaveActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        else {
            leaveDB lDB = new leaveDB();
            lDB.execute();
        }
    }

    public void onLeaveBack(View view) {
        this.finish();
    }

    public class leaveDB extends AsyncTask<Void, Integer, Void> {  // DB에서 회원 탈퇴

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 파라미터값 생성 */
            String param = "&s_id=" + sid + "&s_originpassword=" + soriginpassword + "";
            try {
                /* 서버연결 */
                URL url = new URL(MainActivity.SERVERIP + "leave.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                //Log.i(tag, "서버 연결 성공!!!");

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                //Log.e("받은 데이터", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (data.equals("1")) {
                editor.clear();
                editor.apply();
                LeaveActivity.this.finish();
                Intent intent = new Intent(LeaveActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //기록에 남아있는 Activity들을 지움
                startActivity(intent);
                Toast.makeText(LeaveActivity.this, "회원탈퇴가 정상적으로 처리 되었습니다.", Toast.LENGTH_SHORT).show();
            } else if (data.equals("0")) {
                et_password.setText("");
                Toast.makeText(LeaveActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
