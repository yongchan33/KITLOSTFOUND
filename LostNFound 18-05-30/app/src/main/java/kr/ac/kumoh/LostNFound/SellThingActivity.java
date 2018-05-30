package kr.ac.kumoh.LostNFound;

import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SellThingActivity extends AppCompatActivity {

    //파이어베이스 메시지 보내는 URL
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    //파이어베이스 클라우딩 메시지에서 제공하는 서버키
    private static final String SERVER_KEY = "AAAA4Su1umo:APA91bFIzdG-DPP3KhO6XmaqYXqY8Qwk1ybECO9JIuhxahpQGomhc7uf3tpFnWXefvcygXbvX_sWLh69vmz5eEOfHMGUl4eDvgwrVoIlzmnK4NQ2YjJlPb79Oh9o-Xw_12Zn1y7WVAWg";
    private final static String tag = "";
    private Integer no_notice;
    private String sid;      //현재 로그인한 아이디를 받을 변수
    private long tmpdate;   //현재 시간 받아올 변수
    private String scontent, sdate;   //댓글 내용, 댓글 날짜
    private String snoticewriter, stoken;     //게시물 작성자, 게시물 작성자 토큰 값
    private TextView tv_title, tv_category, tv_type, tv_date, tv_thingname, tv_trademtehod, tv_price, tv_content, tv_writer;
    private EditText et_comment;
    private Button bt_writecomment;
    private ListView mlistview;  // 댓글 리스트뷰
    private SellThingCmtListAdapter adapter;
    protected ArrayList<ListCommentItem> sellthingcommentlist = new ArrayList<ListCommentItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellthing);

        et_comment = (EditText) findViewById(R.id.stcomment);
        bt_writecomment = (Button) findViewById(R.id.stcommentok);
        mlistview = (ListView) findViewById(R.id.stcommentlist);

        Intent intent = getIntent();

        no_notice = intent.getIntExtra("no_notice", 0);
        sid = intent.getStringExtra("id");      //현재 로그인한 아이디
        snoticewriter = intent.getStringExtra("writer");

        tv_title = (TextView) findViewById(R.id.sttitle);
        tv_category = (TextView) findViewById(R.id.stcategory);
        tv_type = (TextView) findViewById(R.id.sttype);
        tv_date = (TextView) findViewById(R.id.stdate);
        tv_thingname = (TextView) findViewById(R.id.stthingname);
        tv_trademtehod = (TextView) findViewById(R.id.sttrademethod);
        tv_price = (TextView) findViewById(R.id.stprice);
        tv_content = (TextView) findViewById(R.id.stcontent);
        tv_writer = (TextView) findViewById(R.id.stwriter);

        tv_title.setText(intent.getStringExtra("title"));
        tv_category.setText(intent.getStringExtra("category"));
        tv_type.setText(intent.getStringExtra("type"));
        tv_date.setText(intent.getStringExtra("date"));
        tv_thingname.setText(intent.getStringExtra("thingname"));
        tv_trademtehod.setText(intent.getStringExtra("trademethod"));
        tv_price.setText(intent.getStringExtra("price"));
        tv_content.setText(intent.getStringExtra("content"));
        tv_writer.setText(intent.getStringExtra("writer"));

        tmpdate = System.currentTimeMillis();   //현재 시간 받아오기
        Date date = new Date(tmpdate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분

        sdate = sdf.format(date);

        getcommentDB gcDB = new getcommentDB();
        gcDB.execute();

        bt_writecomment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                scontent = et_comment.getText().toString();

                if (scontent.getBytes().length == 0)
                    Toast.makeText(getApplicationContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
                else if (scontent.replace(" ", "").equals(""))
                    Toast.makeText(getApplicationContext(), "댓글은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
                else {
                    writecommentDB wcdb = new writecommentDB();
                    wcdb.execute();

                    getcommentDB gcDB = new getcommentDB();
                    gcDB.execute();
                }
            }
        });
    }

    public class writecommentDB extends AsyncTask<Void, Integer, Void> {  // DB에 댓글 쓰기

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&s_id=" + sid + "&no_notice=" + no_notice.toString() + "&s_content=" + scontent + "&s_date=" + sdate + "&s_noticewriter=" + snoticewriter + "";
            try {
                //URL url = new URL("http://10.0.2.2/writecomment.php");
                URL url = new URL("http://192.168.0.5/writecomment.php");
                //URL url = new URL("http://192.168.123.122/writecomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.e(tag, "서버 연결 성공!!!");

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

                JSONObject Result = new JSONObject(data);
                stoken = Result.getString("token");

                int index = data.indexOf("}");      //토큰 값 마지막 대괄호 부분 인덱스 찾기
                data = data.substring(index + 1);     //인덱스 뒷부분 가져오기

                // FMC 메시지 생성 start
                JSONObject root = new JSONObject();                              //root 제이슨 생성
                JSONObject notification = new JSONObject();                      //알림 제이슨 생성
                notification.put("body", "게시물에 새로운 댓글이 달렸습니다.");
                root.put("notification", notification);                 //제이슨 안에 제이슨넣음
                //토큰값 넣음
                root.put("to", stoken);  //토큰값넣음
                // FMC 메시지 생성 end
                root.put("click_action", "OPEN_ACTIVITY");       //click_actiion 추가

                URL fcmurl = new URL(FCM_MESSAGE_URL);                          //url 인스턴스 생성
                HttpURLConnection fcmconn = (HttpURLConnection) fcmurl.openConnection();// 해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다.
                fcmconn.setRequestMethod("POST");                                  // POST방식으로 요청한다.( 기본값은 GET )
                fcmconn.setDoOutput(true);                                         // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션을 정의한다.
                fcmconn.setDoInput(true);                                          // inputStream으로 응답 헤더와 메시지를 읽어들이겠다는 옵션을 정의한다
                fcmconn.addRequestProperty("Authorization", "key=" + SERVER_KEY); //http 요청 헤더를 설정
                fcmconn.setRequestProperty("Accept", "application/json");      //request header json형식 값 세팅
                fcmconn.setRequestProperty("Content-type", "application/json");
                OutputStream os = fcmconn.getOutputStream();                       //request body에 data 닮기 위해 outputstream 객체 생성
                os.write(root.toString().getBytes("utf-8"));     //request body에 data 셋팅
                os.flush();                                                     //request body에 data 입력
                fcmconn.getResponseCode();                                         //실제 서버에 request 요청
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (data.equals("1")) {
                Toast.makeText(SellThingActivity.this, "댓글 쓰기 성공", Toast.LENGTH_SHORT).show();
                et_comment.setText("");     //댓글 쓴 뒤 댓글 입력 창 비우기
            } else if (data.equals("0")) {
                Toast.makeText(SellThingActivity.this, "댓글 쓰기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class getcommentDB extends AsyncTask<Void, Integer, String> {  // DB에서 댓글을 불러옴

        String data = "";

        @Override
        protected String doInBackground(Void... voids) {

            String param = "&no_notice=" + no_notice + "";
            try {
                //URL url = new URL("http://10.0.2.2/getcomment.php");
                URL url = new URL("http://192.168.0.5/getcomment.php");
                //URL url = new URL("http://192.168.123.122/getcomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.e(tag, "서버 연결 성공!!!");

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
            return data.toString();
        }

        @Override
        protected void onPostExecute(String str) {

            sellthingcommentlist.clear();

            Integer no_comment, no_notice;
            String sno_comment, sno_notice, writer, content, date;
            Date tmpdate;

            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    sno_comment = jo.getString("no");
                    no_comment = Integer.parseInt(sno_comment);
                    sno_notice = jo.getString("no_notice");
                    no_notice = Integer.parseInt(sno_notice);
                    writer = jo.getString("writer");
                    content = jo.getString("content");
                    date = jo.getString("date");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분
                    try {
                        tmpdate = sdf.parse(date);
                        date = sdf.format(tmpdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sellthingcommentlist.add(new ListCommentItem(no_comment, no_notice, writer, content, date));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new SellThingCmtListAdapter(SellThingActivity.this, R.layout.sellthingcmtitem, sellthingcommentlist);
            mlistview.setAdapter(adapter);
            setListViewHeightBasedOnItems(mlistview);
            super.onPostExecute(str);
        }
    }

    public void setListViewHeightBasedOnItems(ListView listView) {  // XML에서 스크롤뷰 안에 리스트뷰를 넣을시 height가 제대로 설정되지 않아
        // 리스트뷰의 리스트어댑터를 얻어옴                           // 메소드로 따로 구현해주어야 함 (메소드로 리스트뷰의 height를 설정)
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int numberOfItems = listAdapter.getCount();

        // 리스튜브의 모든 아이템의 전체 높이를 구함
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // item dividers의 높이를 구함
        int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

        // 리스트뷰의 높이를 재지정
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
