package kr.ac.kumoh.LostNFound;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.regex.Pattern;

public class SignUpActivity extends Activity {

    private final static String tag = "";
    private EditText et_id, et_nickname, et_password, et_passch, et_email, et_authnumber;
    private String semailcode;
    private int emailok = 0, emailcheck = 0;        //eamilok는 이메일 발송을 눌렀는지 확인  emailcheck는 이메일이 이미 인증된 이메일인지 확인용(db에서 확인)
    private String sid, snickname, spassword, spassch, semail;
    private Button bt_signup, bt_emailcheck, bt_authcheck, bt_signupback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_main);

        et_id = (EditText) findViewById(R.id.userid);
        et_nickname = (EditText) findViewById(R.id.nickname);
        et_password = (EditText) findViewById(R.id.password);
        et_passch = (EditText) findViewById(R.id.passcheck);
        et_email = (EditText) findViewById(R.id.email);
        et_authnumber = (EditText) findViewById(R.id.authnumber);

        bt_signup = (Button) findViewById(R.id.signup);
        bt_emailcheck = (Button) findViewById(R.id.emailcheck);
        bt_authcheck = (Button) findViewById(R.id.authcheck);
        bt_signupback = (Button) findViewById(R.id.signupback);

    }

    public void onAuthCheck(View view) {
        if (et_authnumber.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(), "인증번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (et_authnumber.getText().toString().equals(semailcode)) {
            Toast.makeText(getApplicationContext(), "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            emailok = 1;
        } else {
            Toast.makeText(getApplicationContext(), "인증번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            emailok = 0;
        }
    }

    public void onEmailCheck(View view) {
        sid = et_id.getText().toString();
        snickname = et_nickname.getText().toString();
        spassword = et_password.getText().toString();
        spassch = et_passch.getText().toString();
        semail = et_email.getText().toString();

        emailcheckDB echdb = new emailcheckDB();
        echdb.execute();

        if (emailcheck == 1) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            if (sid.getBytes().length > 0 && snickname.getBytes().length > 0 && spassword.getBytes().length > 0 && spassch.getBytes().length > 0 && semail.getBytes().length > 0) {
                if (!Pattern.matches("^[a-zA-Z0-9]+@[kumoh.ac.kr]+$", et_email.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "이메일 형식은 @kumoh.ac.kr 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        GMailSender gMailSender = new GMailSender("id@gmail.com", "password");  //보내는 사람 GMail주소, 비밀번호
                        semailcode = gMailSender.getEmailCode();
                        gMailSender.sendMail("금오공대 Lost&Found 본인인증 메일입니다.",
                                "인증번호: " + semailcode,
                                et_email.getText().toString());
                        Toast.makeText(getApplicationContext(), "이메일로 인증번호 전송됨", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "이메일 인증 오류", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            } else if (semail.getBytes().length == 0)
                Toast.makeText(SignUpActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(SignUpActivity.this, "필수 정보를 모두 채워주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public class emailcheckDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {
            String param = "&emailcheck=1" + "&s_email=" + semail + "";
            try {
                //URL url = new URL("http://10.0.2.2/check.php");
                URL url = new URL("http://192.168.0.5/signup.php");
                //URL url = new URL("http://192.168.123.122/signup.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.i(tag, "서버 연결 성공!!!");

                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

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

            if (data.equals("4")) {
                Toast.makeText(SignUpActivity.this, "이미 인증에 사용된 이메일 입니다.", Toast.LENGTH_SHORT).show();
                emailcheck = 0;
            } else emailcheck = 1;
        }
    }

    public class signupDB extends AsyncTask<Void, Integer, Void> {          //회원가입 - DB에 등록

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            /* 파라미터값 생성 */
            String param = "&signup=1" + "&s_id=" + sid + "&s_nickname=" + snickname + "&s_pw=" + spassword + "&s_pwch=" + spassch + "&s_email=" + semail + "";
            try {
                /* 서버연결 */
                //URL url = new URL("http://10.0.2.2/signup.php");
                URL url = new URL("http://192.168.0.5/signup.php");
                //URL url = new URL("http://192.168.123.122/signup.php");
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

            if (emailok == 1) {
                if (data.equals("1")) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                } else if (data.equals("2")) {
                    Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                } else if (data.equals("3")) {
                    Toast.makeText(SignUpActivity.this, "이미 존재하는 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(SignUpActivity.this, "이메일 인증을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSignUp(View view) {
        sid = et_id.getText().toString();
        snickname = et_nickname.getText().toString();
        spassword = et_password.getText().toString();
        spassch = et_passch.getText().toString();
        semail = et_email.getText().toString();

        if (sid.getBytes().length > 0 && snickname.getBytes().length > 0 && spassword.getBytes().length > 0 && spassch.getBytes().length > 0 && semail.getBytes().length > 0) {
            if (!spassword.equals(spassch))
                Toast.makeText(SignUpActivity.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
            else {
                signupDB rdb = new signupDB();
                rdb.execute();
            }
        } else if (sid.getBytes().length == 0)
            Toast.makeText(SignUpActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (sid.replace(" ", "").equals(""))
            Toast.makeText(SignUpActivity.this, "아이디에 공백문자를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        else if (snickname.getBytes().length == 0)
            Toast.makeText(SignUpActivity.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (snickname.replace(" ", "").equals(""))
            Toast.makeText(SignUpActivity.this, "닉네임에 공백문자를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        else if (spassword.getBytes().length == 0)
            Toast.makeText(SignUpActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (spassch.getBytes().length ==0 )
            Toast.makeText(SignUpActivity.this, "비밀번호를 한번 더 입력하세요.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(SignUpActivity.this, "인증번호로 인증해주세요.", Toast.LENGTH_SHORT).show();
    }

    public void onSignUpBack(View view) {
        this.finish();
        /*Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);*/
    }
}