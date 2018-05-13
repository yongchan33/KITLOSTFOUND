package kr.ac.kumoh.LostNFount.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends Activity {

   private EditText et_sumail, et_supw, et_authnumber;
   private Button bt_signup, bt_emailcheck, bt_authcheck;
   private int emailcheckcnt = 0;
   String EmailCode;
   private FirebaseAuth mAuth;
   private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        mAuth = FirebaseAuth.getInstance();

        et_sumail = (EditText)findViewById(R.id.suemail);
        et_supw = (EditText)findViewById(R.id.supw);
        et_authnumber = (EditText)findViewById(R.id.authnumber);
        bt_emailcheck = (Button)findViewById(R.id.emailcheck);
        bt_authcheck = (Button)findViewById(R.id.authcheck);
        bt_signup = (Button)findViewById(R.id.signup);

        bt_emailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Pattern.matches("^[a-zA-Z0-9]+@[kumoh.ac.kr]+$", et_sumail.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "이메일 형식은 @kumoh.ac.kr 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        GMailSender gMailSender = new GMailSender("id@gmail.com", "password");  //보내는 사람 GMail주소, 비밀번호
                        EmailCode = gMailSender.getEmailCode();
                        gMailSender.sendMail("금오공대 Lost&Found 본인인증 메일입니다.",
                                "인증번호: " + EmailCode,
                                et_sumail.getText().toString());
                        Toast.makeText(getApplicationContext(), "이메일로 인증번호 전송됨", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "이메일 인증 오류", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        
        bt_authcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_authnumber.getText().toString().equals(EmailCode)) {
                    Toast.makeText(getApplicationContext(), "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    emailcheckcnt = 1;
                }
                else {
                    Toast.makeText(getApplicationContext(), "인증번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    emailcheckcnt = 0;
                }
            }
        });
        
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailcheckcnt == 1) {
                    signUpUser(et_sumail.getText().toString(), et_supw.getText().toString());
                }
                else {
                    Toast.makeText(SignUpActivity.this, "이메일 인증을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/

    public void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignUpActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onSignupBack(View view) {
        //Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        //startActivity(intent);
        this.finish();
    }

    /*public void onRegist(View view) {
        et_suid = (EditText) findViewById(R.id.suid);
        et_supw = (EditText) findViewById(R.id.supw);
        et_supwch = (EditText) findViewById(R.id.supwcheck);
        et_suname = (EditText) findViewById(R.id.suname);

        ssuid = et_suid.getText().toString();
        ssupw = et_supw.getText().toString();
        ssupwch = et_supwch.getText().toString();
        ssuname = et_suname.getText().toString();

        if(ssuid.getBytes().length > 0 && ssupw.getBytes().length > 0 && ssupwch.getBytes().length > 0
                && ssuname.getBytes().length > 0) {
            if(!ssupw.equals(ssupwch))
                Toast.makeText(SignUpActivity.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
            else {
                registDB rdb = new registDB();
                rdb.execute();
//                    if (rdb != null) {
//                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
            }
        }
        else
            Toast.makeText(SignUpActivity.this, "필수 정보를 모두 채워주세요.", Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, "회원가입 성공!!!", Toast.LENGTH_SHORT).show();
    }

    public class registDB extends AsyncTask<Void, Integer, Void> {          //회원가입 - DB에 등록

        private final static String tag = "";
        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

*//* 파라미터값 생성 *//*
//            String param = "su_id=" + ssuid + "&su_pw=" + ssupw + "&su_pwch=" + ssupwch + "&su_name=" + ssuname +
//                    "&su_gender=" + ssugender + "";
            String param = "&su_id=" + ssuid + "&su_name=" + ssuname + "&su_pw=" + ssupw + "&su_pwch=" + ssupwch + "";
            try {
*//* 서버연결 *//*
                URL url = new URL("http://10.0.2.2/signup.php");
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
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
            } else if (data.equals("0")) {
                Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}