package kr.ac.kumoh.LostNFound;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FindThingActivity extends AppCompatActivity implements OnMapReadyCallback {

    //파이어베이스 메시지 보내는 URL
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    //파이어베이스 클라우딩 메시지에서 제공하는 서버키
    private static final String SERVER_KEY = "AAAA4Su1umo:APA91bFIzdG-DPP3KhO6XmaqYXqY8Qwk1ybECO9JIuhxahpQGomhc7uf3tpFnWXefvcygXbvX_sWLh69vmz5eEOfHMGUl4eDvgwrVoIlzmnK4NQ2YjJlPb79Oh9o-Xw_12Zn1y7WVAWg";
    private Integer no_comment, no_notice; // 댓글 번호, 게시물 번호
    private String sid, snickname;      //현재 로그인한 아이디, 닉네임을 받을 변수
    private long tmpdate;   //현재 시간 받아올 변수
    private long mLastClickTime = 0; // 버튼 더블 클릭 방지용 시스템 클럭 타임 얻어올 변수
    private String scmtcontent, sdate;   //댓글 내용, 댓글 날짜
    private String snoticewriter, stoken;     //게시물 작성자, 게시물 작성자 토큰 값
    private String setitle, secategory, setype, selostdate, sereward, secontent, selostplace;  // 게시물 주인의 정보값들
    private Double dlatitude, dlongitude;     //위도, 경도 받아올 변수
    private Double delatitude, delongitude;  // 게시물 수정 액티비티로 넘겨줄 위도, 경도값
    private TextView tv_title, tv_writer, tv_category, tv_type, tv_date, tv_lostdate, tv_reward, tv_content, tv_lostplace;
    private EditText et_comment;
    private Button bt_editnotice, bt_delnotice, bt_writecomment; // 글수정, 글삭제, 댓글달기 버튼
    private ImageView iv_img1, iv_img2, iv_img3; // 사진 띄우는 ImageView
    private String imgpath, imgname1, imgname2, imgname3; // 이미지 파일경로, 파일이름1,2,3
    private ListView mlistview;     //물건찾기 댓글 리스트뷰
    private GoogleMap mMap;
    private Geocoder geocoder;
    private FindThingCmtListAdapter adapter;
    protected ArrayList<ListCommentItem> findthingcommentlist = new ArrayList<ListCommentItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findthing);

        Intent intent = getIntent();

        no_notice = intent.getIntExtra("no_notice", 0);
        sid = intent.getStringExtra("id"); // 현재 로그인한 아이디
        snickname = intent.getStringExtra("nickname"); // 현재 사용자의 닉네임
        snoticewriter = intent.getStringExtra("writer");

        setitle = intent.getStringExtra("title");  // 게시물 수정할 때 넘겨줄 값들 세팅 (게시물 주인의 정보값들)
        secategory = intent.getStringExtra("category");
        setype = intent.getStringExtra("type");
        selostdate = intent.getStringExtra("lostdate");
        sereward = intent.getStringExtra("reward");
        secontent = intent.getStringExtra("content");
        imgpath = intent.getStringExtra("imgpath");
        imgname1 = intent.getStringExtra("imgname1");
        imgname2 = intent.getStringExtra("imgname2");
        imgname3 = intent.getStringExtra("imgname3");
        selostplace = intent.getStringExtra("lostplace");
        dlatitude = intent.getDoubleExtra("latitude", 36.1444249);
        dlongitude = intent.getDoubleExtra("longitude", 128.39332);
        delatitude = dlatitude;
        delongitude = dlongitude;

        et_comment = (EditText) findViewById(R.id.ftcomment);
        bt_editnotice = (Button) findViewById(R.id.editnoticebt);
        bt_delnotice = (Button) findViewById(R.id.delnoticebt);
        if (snickname.equals(snoticewriter)) { // 로그인한 사람의 닉네임과 게시물 작성자가 같으면 글수정, 글삭제 버튼을 보여줌
            bt_editnotice.setVisibility(View.VISIBLE);
            bt_delnotice.setVisibility(View.VISIBLE);
        }
        bt_writecomment = (Button) findViewById(R.id.ftcommentok);
        iv_img1 = (ImageView) findViewById(R.id.ftimg1);
        iv_img2 = (ImageView) findViewById(R.id.ftimg2);
        iv_img3 = (ImageView) findViewById(R.id.ftimg3);
        mlistview = (ListView) findViewById(R.id.ftcommentlist);

        tv_title = (TextView) findViewById(R.id.fttitle);
        tv_writer = (TextView) findViewById(R.id.ftwriter);
        tv_category = (TextView) findViewById(R.id.ftcategory);
        tv_type = (TextView) findViewById(R.id.fttype);
        tv_date = (TextView) findViewById(R.id.ftdate);
        tv_lostdate = (TextView) findViewById(R.id.ftlostdate);
        tv_reward = (TextView) findViewById(R.id.ftreward);
        tv_content = (TextView) findViewById(R.id.ftcontent);
        tv_lostplace = (TextView) findViewById(R.id.ftlostplace);

        tv_title.setText(intent.getStringExtra("title"));
        tv_writer.setText(intent.getStringExtra("writer"));
        tv_category.setText(intent.getStringExtra("category"));
        tv_type.setText(intent.getStringExtra("type"));
        tv_date.setText(intent.getStringExtra("date"));
        tv_lostdate.setText(intent.getStringExtra("lostdate"));
        tv_reward.setText(intent.getStringExtra("reward"));
        tv_content.setText(intent.getStringExtra("content"));
        tv_lostplace.setText(intent.getStringExtra("lostplace"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ftmap);
        mapFragment.getMapAsync(this);

        tmpdate = System.currentTimeMillis();   //현재 시간 받아오기
        Date date = new Date(tmpdate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분

        sdate = sdf.format(date);

        //사진불러올때
        getimageserver gis = new getimageserver();
        gis.execute(new String[]{MainActivity.SERVERIP + imgpath + imgname1,
                MainActivity.SERVERIP + imgpath + imgname2,
                MainActivity.SERVERIP + imgpath + imgname3});
        getcommentDB gcDB = new getcommentDB();
        gcDB.execute();

        bt_writecomment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                scmtcontent = et_comment.getText().toString();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) { // 버튼 클릭 중복 방지
                    return;
                } else {
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if (scmtcontent.getBytes().length == 0)
                        Toast.makeText(getApplicationContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
                    else if (scmtcontent.replace(" ", "").equals(""))
                        Toast.makeText(getApplicationContext(), "댓글은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
                    else {
                        writecommentDB wcdb = new writecommentDB();
                        wcdb.execute();
                        getcommentDB gcDB = new getcommentDB();
                        gcDB.execute();
                    }
                }
            }
        });

        mlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.comm_edit_dialog, null);
            EditText et_comment = (EditText) dialogView.findViewById(R.id.edit);

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                no_comment = findthingcommentlist.get(position).getNo_comment();
                et_comment.setText(findthingcommentlist.get(position).getComment_content());

                if (snickname.equals(findthingcommentlist.get(position).getCommnet_writer())) { // 자신의 댓글이면 댓글 수정, 삭제 Dialog 띄워줌
                    new AlertDialog.Builder(FindThingActivity.this).setView(dialogView).setTitle("댓글").setMessage("댓글 내용을 수정하세요.")
                            .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    scmtcontent = et_comment.getText().toString();
                                    editcommentDB ecDB = new editcommentDB();
                                    ecDB.execute();

                                    dialog.dismiss();
                                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                                }
                            }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                        }
                    }).setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            deletecommentDB dcDB = new deletecommentDB();
                            dcDB.execute();
                            dialog.dismiss();
                            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                        }
                    }).setCancelable(false).create().show();
                    return false;
                } else return false; // 자신의 댓글이 아니면 return false
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        LatLng result = new LatLng(dlatitude, dlongitude);    //DB에서 불러온 위도, 경도 값
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(result);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(result, 17));
    }

    public void onMoveFind1Edit(View view) {  // 게시물 수정화면으로 이동
        Intent intent = new Intent(FindThingActivity.this, WriteFindActivity.class);
        intent.putExtra("id", sid);
        intent.putExtra("no_notice", no_notice);
        intent.putExtra("title", setitle);
        intent.putExtra("writer", snoticewriter);
        intent.putExtra("category", secategory);
        intent.putExtra("type", setype);
        intent.putExtra("lostdate", selostdate);
        intent.putExtra("reward", sereward);
        intent.putExtra("content", secontent);
        intent.putExtra("imgpath", imgpath);
        intent.putExtra("imgname1", imgname1);
        intent.putExtra("imgname2", imgname2);
        intent.putExtra("imgname3", imgname3);
        intent.putExtra("lostplace", selostplace);
        intent.putExtra("latitude", delatitude);
        intent.putExtra("longitude", delongitude);
        intent.putExtra("nickname", snickname); // 현재 사용자의 닉네임 전달
        startActivity(intent);
    }

    public void onDeleteFind1Notice(View view) {  // 게시물 삭제
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(FindThingActivity.this);
        alert_confirm.setMessage("정말 게시물을 삭제 하시겠습니까?\n게시물을 삭제하면 복구할 수 없습니다.").setCancelable(false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletenoticeDB dnDB = new deletenoticeDB();
                        dnDB.execute();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
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

    public class deletenoticeDB extends AsyncTask<Void, Integer, Void> {          //게시물 삭제

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&findnotice=2" + "&no_notice=" + no_notice + "";

            try {
                /* 서버연결 */
                URL url = new URL(MainActivity.SERVERIP + "deletenotice.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

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

            Intent intent = new Intent(FindThingActivity.this, SubActivity.class);
            intent.putExtra("id", sid);
            intent.putExtra("nickname", snickname); // 게시물 삭제하고 자신의 닉네임을 넘겨줌
            intent.putExtra("category", 0); // 물건찾기 탭으로 이동
            startActivity(intent);
            Toast.makeText(FindThingActivity.this, "게시물 삭제 성공", Toast.LENGTH_SHORT).show();
        }
    }

    public class writecommentDB extends AsyncTask<Void, Integer, Void> {  // DB에 댓글 쓰기

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&comment=0" + "&flag=0" + "&s_id=" + sid + "&no_notice=" + no_notice.toString() +
                    "&s_content=" + scmtcontent + "&s_date=" + sdate + "&s_noticewriter=" + snoticewriter;
            try {
                URL url = new URL(MainActivity.SERVERIP + "writecomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

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
                root.put("to", stoken);  //토큰값넣음
                // FMC 메시지 생성 end
                root.put("click_action", "OPEN_ACTIVITY");       //click_actiion 추가

                URL fcmurl = new URL(FCM_MESSAGE_URL);                          //url 인스턴스 생성
                HttpURLConnection fcmconn = (HttpURLConnection) fcmurl.openConnection();  // 해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다.
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
                View view = FindThingActivity.this.getCurrentFocus();

                Toast.makeText(FindThingActivity.this, "댓글 쓰기 성공", Toast.LENGTH_SHORT).show();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(FindThingActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                et_comment.setText("");     //댓글 쓴 뒤 댓글 입력 창 비우기
            } else if (data.equals("0")) {
                Toast.makeText(FindThingActivity.this, "댓글 쓰기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class editcommentDB extends AsyncTask<Void, Integer, Void> {  // DB에 댓글 수정

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&comment=1" + "&flag=0" + "&s_id=" + sid + "&no_comment=" + no_comment.toString() +
                    "&s_content=" + scmtcontent + "&s_date=" + sdate;
            try {
                URL url = new URL(MainActivity.SERVERIP + "writecomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (data.equals("1")) {
                Toast.makeText(FindThingActivity.this, "댓글 수정 성공", Toast.LENGTH_SHORT).show();
                getcommentDB gcDB = new getcommentDB();
                gcDB.execute();
            } else if (data.equals("0")) {
                Toast.makeText(FindThingActivity.this, "댓글 수정 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class deletecommentDB extends AsyncTask<Void, Integer, Void> {  // DB에 댓글 삭제

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&comment=2" + "&flag=0" + "&s_id=" + sid + "&no_comment=" + no_comment.toString();
            try {
                URL url = new URL(MainActivity.SERVERIP + "writecomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (data.equals("1")) {
                Toast.makeText(FindThingActivity.this, "댓글 삭제 성공", Toast.LENGTH_SHORT).show();
                getcommentDB gcDB = new getcommentDB();
                gcDB.execute();
            } else if (data.equals("0")) {
                Toast.makeText(FindThingActivity.this, "댓글 삭제 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class getcommentDB extends AsyncTask<Void, Integer, String> {  // DB에서 댓글을 불러옴

        String data = "";

        @Override
        protected String doInBackground(Void... voids) {

            String param = "&flag=0" + "&no_notice=" + no_notice;
            try {
                URL url = new URL(MainActivity.SERVERIP + "getcomment.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

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

            findthingcommentlist.clear();

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
                    findthingcommentlist.add(new ListCommentItem(no_comment, no_notice, writer, content, date));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new FindThingCmtListAdapter(FindThingActivity.this, R.layout.findthingcmtitem, findthingcommentlist);
            mlistview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setListViewHeightBasedOnItems(mlistview);
            super.onPostExecute(str);
        }
    }

    public class getimageserver extends AsyncTask<String, Void, Bitmap[]> {  // 웹서버에서 이미지 가져옴
        @Override
        protected Bitmap[] doInBackground(String... urls) {
            Bitmap[] map = new Bitmap[urls.length];
            for (int i = 0; i < urls.length; i++) {
                String[] url = new String[urls.length];
                url[i] = urls[i];
                map[i] = downloadImage(url[i]);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap[] result) {
            iv_img1.setImageBitmap(result[0]);
            iv_img2.setImageBitmap(result[1]);
            iv_img3.setImageBitmap(result[2]);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                if (stream != null)
                    stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }
}