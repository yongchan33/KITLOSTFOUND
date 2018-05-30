package kr.ac.kumoh.LostNFound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyInfoActivity extends AppCompatActivity {

    private final static String tag = "";
    private String sid; //로그인 한 아이디
    private String snickname, semail;    //로그인 한 사용자 닉네임, 이메일;
    private String snewnickname, soriginpassword, snewpassword, snewpasscheck;
    private TextView tv_myid, tv_myemail;
    private EditText et_mynickname, et_mypassword, et_newpassword, et_newpassch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");  //로그인한 아이디 받아옴
        snickname = intent.getStringExtra("nickname");
        Log.e("닉네임", "닉네임: " + snickname);
        semail = intent.getStringExtra("email");
        Log.e("이메일", "이메일: " + semail);

        tv_myid = (TextView) findViewById(R.id.myid);
        tv_myemail = (TextView) findViewById(R.id.myemail);

        et_mynickname = (EditText) findViewById(R.id.mynickname);
        et_mypassword = (EditText) findViewById(R.id.mypassword);
        et_newpassword = (EditText) findViewById(R.id.newpassword);
        et_newpassch = (EditText) findViewById(R.id.newpasscheck);

        tv_myid.setText(sid);
        tv_myemail.setText(semail);

        et_mynickname.setHint(snickname);
    }

    public void onEditNickname(View view) {

        snewnickname = et_mynickname.getText().toString();

        if (snewnickname.getBytes().length == 0)
            Toast.makeText(getApplicationContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (snewnickname.replace(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "닉네임은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
        else {
            editnicknameDB ennDB = new editnicknameDB();
            ennDB.execute();
        }
    }

    public void onEditPassword(View view) {

        soriginpassword = et_mypassword.getText().toString();
        snewpassword = et_newpassword.getText().toString();
        snewpasscheck = et_newpassch.getText().toString();

        if (soriginpassword.getBytes().length == 0)
            Toast.makeText(MyInfoActivity.this, "현재 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (snewpassword.getBytes().length == 0)
            Toast.makeText(MyInfoActivity.this, "새로운 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (snewpasscheck.getBytes().length == 0)
            Toast.makeText(MyInfoActivity.this, "새로운 비밀번호를 한번 더 입력하세요.", Toast.LENGTH_SHORT).show();
        else {
            if (!snewpassword.equals(snewpasscheck))
                Toast.makeText(MyInfoActivity.this, "새로운 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            else {
                editpasswordDB epwDB = new editpasswordDB();
                epwDB.execute();
            }
        }
    }

    public void onMyInfoBack(View view) {
        Intent intent = new Intent(MyInfoActivity.this, SubActivity.class);
        intent.putExtra("id", sid);
        startActivity(intent);
    }

    public class editnicknameDB extends AsyncTask<Void, Integer, Void> {  //DB에 닉네임 변경

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 파라미터값 생성 */
            String param = "&editnickname=1" + "&s_id=" + sid + "&s_originnickname=" + snickname + "&s_newnickname=" + snewnickname + "";
            try {
                /* 서버연결 */
                //URL url = new URL("http://10.0.2.2/login.php");
                //URL url = new URL("http://192.168.0.5/editmyinfo.php");
                URL url = new URL("http://192.168.123.122/editmyinfo.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.i(tag, "서버 연결 성공!!!");

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
                Log.e("받은 데이터", data);

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
                Toast.makeText(MyInfoActivity.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
            } else if (data.equals("3")) {
                Toast.makeText(MyInfoActivity.this, "이미 존재하는 닉네임 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class editpasswordDB extends AsyncTask<Void, Integer, Void> {  //DB에 비밀번호 변경

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 파라미터값 생성 */
            String param = "&editpassword=1" + "&s_id=" + sid + "&s_originpassword=" + soriginpassword + "&s_newpassword=" + snewpassword + "&s_newpasscheck=" + snewpasscheck + "";
            try {
                /* 서버연결 */
                //URL url = new URL("http://10.0.2.2/login.php");
                //URL url = new URL("http://192.168.0.5/editmyinfo.php");
                URL url = new URL("http://192.168.123.122/editmyinfo.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.i(tag, "서버 연결 성공!!!");

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
                Log.e("받은 데이터", data);

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
                Toast.makeText(MyInfoActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            } else if (data.equals("0")) {
                Toast.makeText(MyInfoActivity.this, "현재 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
