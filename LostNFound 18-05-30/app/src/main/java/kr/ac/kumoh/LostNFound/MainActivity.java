package kr.ac.kumoh.LostNFound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

public class MainActivity extends AppCompatActivity {

    private final static String tag = "";

    private EditText et_id, et_password;
    private CheckBox cb_autologin;
    private String sid, spassword, sandroidId;
    private SharedPreferences setting;  // 자동 로그인 값 세팅
    private SharedPreferences.Editor editor;  // 자동 로그인에 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_main);

        et_id = (EditText) findViewById(R.id.loginid);
        et_password = (EditText) findViewById(R.id.loginpw);

        cb_autologin = (CheckBox) findViewById(R.id.autologin);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        if (setting.getBoolean("cb_autologin", false)) {  // 처음 로그인시 setting에 값이 없으므로 false -> if문 통과
            et_id.setText(setting.getString("id", ""));  // setting에 설정된 id 값을 가져옴
            et_password.setText(setting.getString("password", ""));  // setting에 설정된 password 값을 가져옴
            sid = et_id.getText().toString();
            spassword = et_password.getText().toString();
            cb_autologin.setChecked(true);

            Toast.makeText(MainActivity.this, "자동 로그인 중", Toast.LENGTH_SHORT).show();

            loginDB logindb = new loginDB();
            logindb.execute();
        }

        sandroidId = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void onMoveSignUp(View view) {  //회원가입 버튼 클릭시 회원가입 엑티비티로 이동

        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onLogIn(View view) {  //로그인 버튼 클릭시 이벤트
        //로그인 버튼을 누르면 입력한 id, password를 얻어와 String으로 변환
        sid = et_id.getText().toString();
        spassword = et_password.getText().toString();

        if (cb_autologin.isChecked()) {
            editor.putString("id", sid);
            editor.putString("password", spassword);
            editor.putBoolean("cb_autologin", true);
            editor.commit();
        } else {
            editor.clear();
            editor.commit();
        }

        if (sid.getBytes().length > 0 && spassword.getBytes().length > 0) {
            loginDB logindb = new loginDB();
            logindb.execute();

        } else if (sid.getBytes().length == 0)
            Toast.makeText(MainActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (spassword.getBytes().length == 0)
            Toast.makeText(MainActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "에러!!!", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "로그인 클릭성공!!!", Toast.LENGTH_SHORT).show();
    }

    public class loginDB extends AsyncTask<Void, Integer, Void> {  // DB를 통한 로그인

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 파라미터값 생성 */
            String param = "&s_id=" + sid + "&s_password=" + spassword + "&s_androidId=" + sandroidId + "";
            try {
                /* 서버연결 */
                //URL url = new URL("http://10.0.2.2/login.php");
                URL url = new URL("http://192.168.0.5/login.php");
                //URL url = new URL("http://192.168.123.122/login.php");
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
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("id", sid);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            } else if (data.equals("0")) {
                Toast.makeText(MainActivity.this, "아이디, 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
