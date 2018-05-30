package kr.ac.kumoh.LostNFound;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WriteSellActivity extends Activity {

    private final static String tag = "";
    private Spinner sp_type, sp_trademethod;
    private int categorycheck = 2; //물건찾기:0, 주인찾기:1, 벼룩시장:2
    private EditText et_title, et_thingname, et_price, et_content;
    private long tmpdate;
    private String sid, stitle, scategory, stype, sdate, sthingname, strademethod, sprice, scontent;

    private final static int REQUEST_CODE_GALLY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writesell);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");

        et_title = (EditText) findViewById(R.id.notice_title);
        et_thingname = (EditText) findViewById(R.id.thingname);
        et_price = (EditText) findViewById(R.id.price);
        et_content = (EditText) findViewById(R.id.content);

        sp_type = (Spinner) findViewById(R.id.type);
        sp_trademethod = (Spinner) findViewById(R.id.trademethod);
    }

    public void onWrite(View view) {
        stitle = et_title.getText().toString();
        scategory = "벼룩시장";
        stype = sp_type.getSelectedItem().toString();

        tmpdate = System.currentTimeMillis();   //현재 시간 받아오기
        Date date = new Date(tmpdate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분

        sdate = sdf.format(date);
        sthingname = et_thingname.getText().toString();
        strademethod = sp_trademethod.getSelectedItem().toString();
        sprice = et_price.getText().toString();
        sprice = sprice.concat("원");
        scontent = et_content.getText().toString();

        if (stitle.getBytes().length == 0)   //게시물 제목이 비어있는지 확인
            Toast.makeText(getApplicationContext(), "게시물 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (stitle.replace(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "게시물 제목은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
        else if (scontent.getBytes().length == 0)      //게시물 내용이 비어있는지 확인
            Toast.makeText(getApplicationContext(), "게시물 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (scontent.replace(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "게시물 내용은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
        else {
            writenoticeDB wndb = new writenoticeDB();
            wndb.execute();
        }
    }

    public class writenoticeDB extends AsyncTask<Void, Integer, Void> {          //DB에 게시물 쓰기

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&sellnotice=1" + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                    "&s_type=" + stype + "&s_date=" + sdate + "&s_thingname=" + sthingname + "&s_trademethod=" + strademethod + "&s_price=" + sprice +
                    "&s_content=" + scontent + "";
            try {
                /* 서버연결 */
                //URL url = new URL("http://10.0.2.2/writenotice.php");
                URL url = new URL("http://192.168.0.5/writenotice.php");
                //URL url = new URL("http://192.168.123.122/writenotice.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.e(tag, "서버 연결 성공!!!");

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
                Toast.makeText(WriteSellActivity.this, "글쓰기 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteSellActivity.this, SubActivity.class);
                intent.putExtra("id", sid);
                intent.putExtra("category", categorycheck);
                startActivity(intent);
            } else if (data.equals("0")) {
                Toast.makeText(WriteSellActivity.this, "글쓰기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onWriteBack(View view) {
        this.finish();

        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);     //키보드 보이기 안보이기 설정
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
    }

    public void onMoveGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), REQUEST_CODE_GALLY);
    }

}
