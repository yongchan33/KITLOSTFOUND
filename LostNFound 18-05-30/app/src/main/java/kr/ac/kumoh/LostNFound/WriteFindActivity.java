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

public class WriteFindActivity extends Activity {

    private final static String tag = "";
    private Spinner sp_category, sp_type, sp_reward;
    private long tmpdate;
    private int categorycheck; //물건찾기:0, 주인찾기:1, 벼룩시장:2
    private String stmplostdate;
    private Button bt_uplode, bt_lostdate, bt_losttime, bt_selectmap;
    private TextView tv_lostdate, tv_losttime, tv_maptext;
    private EditText et_title, et_content;
    private ImageView iv_preview;
    private String sid, stitle, scategory, stype, sdate, slostdate, sreward, scontent, slostplace;
    private Double dlatitude, dlongitude;
    private int year, month, day, hour, minute;
    private Uri filePath;
    private Bitmap bitmap;
    private ArrayAdapter category_adapter;
    private final static int REQUEST_CODE_MAPACTIVITY = 100;
    private final static int REQUEST_CODE_GALLY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writefind);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");

        bt_uplode = (Button) findViewById(R.id.btuplode);
        bt_lostdate = (Button) findViewById(R.id.btlostdate);
        bt_losttime = (Button) findViewById(R.id.btlosttime);
        bt_selectmap = (Button) findViewById(R.id.btselectmap);

        tv_lostdate = (TextView) findViewById(R.id.lostdate);
        tv_losttime = (TextView) findViewById(R.id.losttime);
        tv_maptext = (TextView) findViewById(R.id.maptext);

        et_title = (EditText) findViewById(R.id.notice_title);
        et_content = (EditText) findViewById(R.id.content);

        iv_preview = (ImageView) findViewById(R.id.preimage);

        sp_category = (Spinner) findViewById(R.id.category);
        sp_type = (Spinner) findViewById(R.id.type);
        sp_reward = (Spinner) findViewById(R.id.reward);

        category_adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);        //카테고리 스피너 어댑터
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(category_adapter);

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {             //카테고리 스피너 아이템 선택 이벤트
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (category_adapter.getItem(i).toString().equals("물건찾기")) {           //버튼 or 스피너 활성화, 비활성화 처리
                    bt_lostdate.setEnabled(true);
                    bt_losttime.setEnabled(true);
                    bt_selectmap.setEnabled(true);
                    sp_reward.setEnabled(true);
                    categorycheck = 0;

                }
                if (category_adapter.getItem(i).toString().equals("주인찾기")) {
                    bt_lostdate.setEnabled(false);
                    bt_losttime.setEnabled(false);
                    bt_selectmap.setEnabled(false);
                    sp_reward.setEnabled(false);
                    /*tv_lostdate.setVisibility(View.GONE);
                    tv_losttime.setVisibility(View.GONE);
                    tv_maptext.setVisibility(View.GONE);
                    bt_lostdate.setVisibility(View.GONE);
                    bt_losttime.setVisibility(View.GONE);
                    bt_selectmap.setVisibility(View.GONE);
                    sp_reward.setVisibility(View.GONE);*/

                    categorycheck = 1;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);*/  //키보드 관련 설정

    }

    public void onMoveSelectMap(View view) {
        Intent intent = new Intent(WriteFindActivity.this, SelectMapActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MAPACTIVITY);
    }

    public void onWrite(View view) {
        stitle = et_title.getText().toString();
        scategory = sp_category.getSelectedItem().toString();
        stype = sp_type.getSelectedItem().toString();

        tmpdate = System.currentTimeMillis();   //현재 시간 받아오기
        Date date = new Date(tmpdate);
        stmplostdate = tv_lostdate.getText().toString().concat(" ").concat(tv_losttime.getText().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분

        sdate = sdf.format(date);
        slostdate = stmplostdate;
        sreward = sp_reward.getSelectedItem().toString();
        scontent = et_content.getText().toString();

        if (stitle.getBytes().length == 0)   //게시물 제목이 비어있는지 확인
            Toast.makeText(getApplicationContext(), "게시물 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (stitle.replace(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "게시물 제목은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
        else if (categorycheck == 0 && slostdate.equals(" "))
            Toast.makeText(getApplicationContext(), "잃어버린 날짜와 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
        else if (categorycheck == 0 && (dlatitude == null || dlongitude == null))
            Toast.makeText(getApplicationContext(), "잃어버린 위치를 선택해주세요.", Toast.LENGTH_SHORT).show();
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

            String param = "&findnotice=1" + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                    "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                    "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude + "";
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
                Toast.makeText(WriteFindActivity.this, "글쓰기 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteFindActivity.this, SubActivity.class);
                intent.putExtra("id", sid);
                intent.putExtra("category", categorycheck);
                startActivity(intent);
            } else if (data.equals("0")) {
                Toast.makeText(WriteFindActivity.this, "글쓰기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadimage() {

        String attachmentName = "bitmap";
        String attachmentFileName = bitmap + ".png";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            // request 준비
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL("http://192.168.123.122/uploadimage.php");
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // content wrapper시작
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

// Bitmap을 ByteBuffer로 전환
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }
            request.write(pixels);

// content wrapper종료
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);

// buffer flush
            request.flush();
            request.close();

// Response받기
            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());
            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            String response = stringBuilder.toString();


//Response stream종료
            responseStream.close();

// connection종료
            httpUrlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onWriteBack(View view) {
        this.finish();

        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);     //키보드 보이기 안보이기 설정
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
    }

    public void onLostDate(View view) {            //잃어버린 날짜 선택 함수

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        if (month < 10) {
                            if (day < 10)
                                tv_lostdate.setText(year + "-0" + (month + 1) + "-0" + day);
                            else if (day >= 10)
                                tv_lostdate.setText(year + "-0" + (month + 1) + "-" + day);
                        } else if (month >= 10) {
                            if (day < 10)
                                tv_lostdate.setText(year + "-" + (month + 1) + "-0" + day);
                            else if (day >= 10)
                                tv_lostdate.setText(year + "-" + (month + 1) + "-" + day);
                        }
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void onLostTime(View view) {        //잃어버린 시간 선택 함수

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        if (hour < 10) {
                            if (minute < 10) {
                                tv_losttime.setText("0" + hour + ":0" + minute);
                            } else if (minute >= 10)
                                tv_losttime.setText("0" + hour + ":" + minute);
                        } else if (hour >= 10) {
                            if (minute < 10) {
                                tv_losttime.setText(hour + ":0" + minute);
                            } else if (minute >= 10)
                                tv_losttime.setText(hour + ":" + minute);
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    public void onMoveGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), REQUEST_CODE_GALLY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_MAPACTIVITY:
                    slostplace = data.getStringExtra("place");
                    dlatitude = data.getDoubleExtra("latitude", 36.1444249);
                    dlongitude = data.getDoubleExtra("longitude", 128.39332);
                    tv_maptext.setText(slostplace);

                /*case REQUEST_CODE_GALLY:
                    filePath = data.getData();
                    try {
                        //Uri 파일을 Bitmap으로 만들어서 ImageView에 띄움(이미지 미리보기)
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        iv_preview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
            }
        } else
            return;
    }
}
