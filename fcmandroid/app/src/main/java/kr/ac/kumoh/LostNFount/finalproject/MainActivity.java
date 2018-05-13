package kr.ac.kumoh.LostNFount.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private EditText et_email, et_pw;
    private Button bt_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_main);

        mAuth = FirebaseAuth.getInstance();

        et_email = (EditText)findViewById(R.id.email);
        et_pw = (EditText)findViewById(R.id.pw);
        bt_signin = (Button)findViewById(R.id.signin);

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in 상태변화 : 로그인
                } else {
                    // User is signed out 상태변화 : 로그아웃
                }
            }
        };*/

        bt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(et_email.getText().toString(), et_pw.getText().toString());
            }
        });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "이메일, 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, SubActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onSignup(View view){        //회원가입 버튼 클릭시 회원가입 엑티비티로 이동

        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);

        //Toast.makeText(this, "회원가입 클릭성공!!!", Toast.LENGTH_SHORT).show();
    }

    /*public void onLogin(View view){         //로그인 버튼 클릭시 이벤트
        //버튼 누르면 입력한 id, pw를 얻어와 String으로 변환
        et_id = (EditText) findViewById(R.id.id);
        et_pw = (EditText) findViewById(R.id.pw);

        sid = et_id.getText().toString();
        spw = et_pw.getText().toString();
        if(sid.getBytes().length > 0 && spw.getBytes().length > 0) {
            loginDB logindb = new loginDB();
            logindb.execute();

//            if(logindb != null) {
//                Intent intent = new Intent(MainActivity.this, SubActivity.class);
//                startActivity(intent);
//            }
        }
        else{
            Toast.makeText(MainActivity.this, "아이디, 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "로그인 클릭성공!!!", Toast.LENGTH_SHORT).show();
    }

    public class loginDB extends AsyncTask<Void, Integer, Void> {          //로그인 - DB에 id, pw 일치 확인

        private final static String tag = "";
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

*//* 파라미터값 생성 *//*
            String param = "&s_id=" + sid + "&s_pw=" + spw + "";
            try {
*//* 서버연결 *//*
                URL url = new URL("http://10.0.2.2/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.i(tag, "서버 연결 성공!!!");

*//* 안드로이드 -> 서버 파라메터값 전달 *//*
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

*//* 서버 -> 안드로이드 파라메터값 전달 *//*
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
                //intent.putExtra("id", sid);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            } else if (data.equals("0")) {
                Toast.makeText(MainActivity.this, "아이디, 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}
