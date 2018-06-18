package kr.ac.kumoh.LostNFound;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WriteFindActivity extends Activity {

    private boolean flag = false; // 게시물 올리기 버튼 눌렀을때 다중으로 눌렀는지 확인용
    private boolean mapflag = false; // 지도 선택이 눌렸는지 확인용
    private Spinner sp_category, sp_type, sp_reward;
    private long tmpdate;
    private int categorycheck; // 물건찾기:0, 주인찾기:1, 벼룩시장:2
    private int write_editcheck = 0;  // 글쓰기: 0 (초기값) , 글수정:1
    private Integer no_notice; // 게시물 수정시 게시물 번호 받아올 변수
    private String stmplostdate, stmplostplace;
    private Button bt_lostdate, bt_losttime, bt_selectmap;
    private TextView tv_lostdate, tv_losttime, tv_maptext;
    private EditText et_title, et_content;
    private ImageView iv_preview1, iv_preview2, iv_preview3; // 게시물 작성시 사진 첨부할때 미리보기 창
    private String sid, swriter, stitle, scategory, stype, sdate, slostdate, slosttime, sreward, scontent, slostplace, simgname2, simgname3;
    private Double dlatitude, dlongitude;
    private int year, month, day, hour, minute;
    private ArrayAdapter category_adapter;
    public static ArrayList<String> selectedPhotos = new ArrayList<>();
    private String uploadFilePath;                     //파일경로
    private String uploadFileName1, uploadFileName2, uploadFileName3; // 웹서버에 올릴 이미지 이름 1,2,3
    private final static int REQUEST_CODE_MAPACTIVITY = 100;
    private final static int REQUEST_CODE_GALLERY = 101;
    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writefind);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");
        swriter = intent.getStringExtra("nickname"); // 게시물 수정시 사용자의 닉네임을 받아옴

        bt_lostdate = (Button) findViewById(R.id.btlostdate);
        bt_losttime = (Button) findViewById(R.id.btlosttime);
        bt_selectmap = (Button) findViewById(R.id.btselectmap);

        tv_lostdate = (TextView) findViewById(R.id.lostdate);
        tv_losttime = (TextView) findViewById(R.id.losttime);
        tv_maptext = (TextView) findViewById(R.id.maptext);

        et_title = (EditText) findViewById(R.id.notice_title);
        et_content = (EditText) findViewById(R.id.content);

        iv_preview1 = (ImageView) findViewById(R.id.preimg1);
        iv_preview2 = (ImageView) findViewById(R.id.preimg2);
        iv_preview3 = (ImageView) findViewById(R.id.preimg3);

        sp_category = (Spinner) findViewById(R.id.category);
        sp_category.bringToFront();
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
                    tv_lostdate.setText("");
                    tv_losttime.setText("");
                    tv_maptext.setText("");
                    slostdate = "";
                    slosttime = "";
                    slostplace = "";
                    categorycheck = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 글 수정 버튼 눌렀을때 값들이 넘어오면 글 수정 값들 세팅해줌
        if (intent.hasExtra("no_notice")) {
            no_notice = intent.getIntExtra("no_notice", 0);
        }
        if (intent.hasExtra("title")) {
            et_title.setText(intent.getStringExtra("title"));
            write_editcheck = 1; // 게시물 수정임을 표시
        }
        if (intent.hasExtra("category")) {
            if (intent.getStringExtra("category").equals("물건찾기"))
                sp_category.setSelection(0);
            else if (intent.getStringExtra("category").equals("주인찾기"))
                sp_category.setSelection(1);
        }
        if (intent.hasExtra("type")) {
            if (intent.getStringExtra("type").equals("학용품"))
                sp_type.setSelection(0);
            else if (intent.getStringExtra("type").equals("전자기기"))
                sp_type.setSelection(1);
            else if (intent.getStringExtra("type").equals("의류"))
                sp_type.setSelection(2);
            else if (intent.getStringExtra("type").equals("패션잡화"))
                sp_type.setSelection(3);
            else if (intent.getStringExtra("type").equals("도서"))
                sp_type.setSelection(4);
            else if (intent.getStringExtra("type").equals("현금"))
                sp_type.setSelection(5);
            else if (intent.getStringExtra("type").equals("카드"))
                sp_type.setSelection(6);
            else if (intent.getStringExtra("type").equals("기타물품"))
                sp_type.setSelection(7);
        }
        if (intent.hasExtra("lostdate")) {
            String tmplostdate = intent.getStringExtra("lostdate");
            int idx = tmplostdate.indexOf(" ");
            slostdate = tmplostdate.substring(0, idx);
            slosttime = tmplostdate.substring(idx + 1);
            tv_lostdate.setText(slostdate);
            tv_losttime.setText(slosttime);
        }
        if (intent.hasExtra("lostplace")) {
            tv_maptext.setText(intent.getStringExtra("lostplace"));
            stmplostplace = intent.getStringExtra("lostplace");
        }
        if (intent.hasExtra("latitude")) {
            dlatitude = intent.getDoubleExtra("latitude", 36.1444249);
        }
        if (intent.hasExtra("longitude")) {
            dlongitude = intent.getDoubleExtra("longitude", 128.39332);
        }
        if (intent.hasExtra("reward")) {
            if (intent.getStringExtra("reward").equals("없음"))
                sp_reward.setSelection(0);
            else if (intent.getStringExtra("reward").equals("500원"))
                sp_reward.setSelection(1);
            else if (intent.getStringExtra("reward").equals("1000원"))
                sp_reward.setSelection(2);
            else if (intent.getStringExtra("reward").equals("3000원"))
                sp_reward.setSelection(3);
        }
        if (intent.hasExtra("content")) {
            et_content.setText(intent.getStringExtra("content"));
        }
        if (intent.hasExtra("imgname1")) { // 글 수정 버튼 눌렀을때 넘어온 이미지 값이 있으면
            simgname2 = intent.getStringExtra("imgname2");
            simgname3 = intent.getStringExtra("imgname3");
            getimageserver gis = new getimageserver();
            gis.execute(new String[]{MainActivity.SERVERIP + intent.getStringExtra("imgpath") + intent.getStringExtra("imgname1"),
                    MainActivity.SERVERIP + intent.getStringExtra("imgpath") + intent.getStringExtra("imgname2"),
                    MainActivity.SERVERIP + intent.getStringExtra("imgpath") + intent.getStringExtra("imgname3")});
        }
    }

    public void onMoveSelectMap(View view) {
        Intent intent = new Intent(WriteFindActivity.this, SelectMapActivity.class);
        if (dlatitude != null && dlongitude != null) {
            intent.putExtra("latitude", dlatitude);
            intent.putExtra("longitude", dlongitude);
            intent.putExtra("lostplace", stmplostplace);
        }
        mapflag = true;
        startActivityForResult(intent, REQUEST_CODE_MAPACTIVITY);
    }

    //갤러리 창 띄울때, 권환 확인 창까지
    public void onMoveGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteFindActivity.this);
                intent.setMaxSelectCount(3);            //최대개수
                intent.setShowCamera(false);            //카메라 찍기
                intent.setShowGif(true);
                intent.setSelectCheckBox(false);
                intent.setMaxGrideItemCount(3);

                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        } else {
            YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteFindActivity.this);
            intent.setMaxSelectCount(3);            //최대개수
            intent.setShowCamera(false);            //카메라 찍기
            intent.setShowGif(true);
            intent.setSelectCheckBox(false);
            intent.setMaxGrideItemCount(3);

            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }
    }

    public void onLostDate(View view) { // 잃어버린 날짜 선택 함수

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> photos = null;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_MAPACTIVITY:
                    slostplace = data.getStringExtra("place");
                    dlatitude = data.getDoubleExtra("latitude", 36.1444249);
                    dlongitude = data.getDoubleExtra("longitude", 128.39332);
                    tv_maptext.setText(slostplace);
                    break;

                case REQUEST_CODE_GALLERY:
                    uploadFileName1 = null;
                    uploadFileName2 = null;
                    uploadFileName3 = null;

                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    }
                    if (photos != null) {
                        selectedPhotos.addAll(photos);
                    }

                    //파일 이름이랑 경로 만들기위해서 스트링에서 문자열 자름
                    switch (photos.size()) {  //사진 선택 갯수에 따라
                        case 1:
                            uploadFileName1 = photos.get(0).toString().substring(photos.get(0).toString().lastIndexOf("/") + 1);
                            break;
                        case 2:
                            uploadFileName1 = photos.get(0).toString().substring(photos.get(0).toString().lastIndexOf("/") + 1);
                            uploadFileName2 = photos.get(1).toString().substring(photos.get(1).toString().lastIndexOf("/") + 1);

                            break;
                        case 3:
                            uploadFileName1 = photos.get(0).toString().substring(photos.get(0).toString().lastIndexOf("/") + 1);
                            uploadFileName2 = photos.get(1).toString().substring(photos.get(1).toString().lastIndexOf("/") + 1);
                            uploadFileName3 = photos.get(2).toString().substring(photos.get(2).toString().lastIndexOf("/") + 1);

                            break;
                    }

                    uploadFilePath = photos.get(0).toString().replaceAll(uploadFileName1, "");
                    File imgFile1 = new File(uploadFilePath + uploadFileName1);
                    File imgFile2 = new File(uploadFilePath + uploadFileName2);
                    File imgFile3 = new File(uploadFilePath + uploadFileName3);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4; // 사진 이미지가 클 수 있으므로 1/4배로 줄임

                    if (imgFile1.exists()) {
                        if (imgFile2.exists()) {
                            if (imgFile3.exists()) { // 사진을 3개 모두 선택했을경우
                                Bitmap myBitmap1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath(), options);
                                Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath(), options);
                                Bitmap myBitmap3 = BitmapFactory.decodeFile(imgFile3.getAbsolutePath(), options);
                                iv_preview1.setImageBitmap(myBitmap1);
                                iv_preview2.setImageBitmap(myBitmap2);
                                iv_preview3.setImageBitmap(myBitmap3);
                            } else { // 사진을 2개만 선택했을경우
                                if (simgname3 != null) {
                                    Bitmap myBitmap1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath(), options);
                                    Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath(), options);
                                    iv_preview1.setImageBitmap(myBitmap1);
                                    iv_preview2.setImageBitmap(myBitmap2);
                                } else {
                                    Bitmap myBitmap1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath(), options);
                                    Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath(), options);
                                    iv_preview1.setImageBitmap(myBitmap1);
                                    iv_preview2.setImageBitmap(myBitmap2);
                                    iv_preview3.setImageBitmap(null);
                                }
                            }
                        } else { // 사진을 1개만 선택했을경우
                            if (simgname2 != null) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath(), options);
                                iv_preview1.setImageBitmap(myBitmap);
                                iv_preview3.setImageBitmap(null);
                            } else {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath(), options);
                                iv_preview1.setImageBitmap(myBitmap);
                                iv_preview2.setImageBitmap(null);
                                iv_preview3.setImageBitmap(null);
                            }
                        }
                    } else
                        break;

                default:
                    break;
            }
        } else
            return;
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

        if (mapflag == false)
            slostplace = stmplostplace;

        if (stitle.getBytes().length == 0)   //게시물 제목이 비어있는지 확인
            Toast.makeText(getApplicationContext(), "게시물 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (stitle.replace(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "게시물 제목은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
        else if (stitle.length() > 14) {
            Toast.makeText(getApplicationContext(), "게시물 제목이 너무 깁니다.", Toast.LENGTH_SHORT).show();
        } else if (categorycheck == 0 && slostdate.equals(" "))
            Toast.makeText(getApplicationContext(), "잃어버린 날짜와 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
        else if (categorycheck == 0 && (dlatitude == null || dlongitude == null))
            Toast.makeText(getApplicationContext(), "잃어버린 위치를 선택해주세요.", Toast.LENGTH_SHORT).show();
        else if (scontent.getBytes().length == 0)      //게시물 내용이 비어있는지 확인
            Toast.makeText(getApplicationContext(), "게시물 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
        else if (scontent.replace(" ", "").equals(""))
            Toast.makeText(getApplicationContext(), "게시물 내용은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
        else {
            if (write_editcheck == 0) {
                if (flag == false) {
                    flag = true;
                    writenoticeDB wndb = new writenoticeDB();
                    wndb.execute();
                }
            } else if (write_editcheck == 1) {
                if (flag == false) {
                    flag = true;
                    editnoticeDB enDB = new editnoticeDB();
                    enDB.execute();
                }
            }
        }
    }

    public void onWriteBack(View view) {
        this.finish();
    }

    public class writenoticeDB extends AsyncTask<Void, Integer, Void> {          //DB에 게시물 쓰기
        String param;
        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            if (uploadFileName1 != null) {
                if (uploadFileName2 != null) {
                    if (uploadFileName3 != null) {
                        param = "&findnotice=" + write_editcheck + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                                "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                                "&imgname1=" + uploadFileName1 + "&imgname2=" + uploadFileName2 + "&imgname3=" + uploadFileName3 +
                                "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude;
                    } else {
                        param = "&findnotice=" + write_editcheck + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                                "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                                "&imgname1=" + uploadFileName1 + "&imgname2=" + uploadFileName2 +
                                "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude;
                    }
                } else {
                    param = "&findnotice=" + write_editcheck + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                            "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                            "&imgname1=" + uploadFileName1 + "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude;
                }
            } else {
                param = "&findnotice=" + write_editcheck + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                        "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                        "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude;
            }

            try {
                /* 서버연결 */
                URL url = new URL(MainActivity.SERVERIP + "writenotice.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                uploadFile(uploadFilePath + uploadFileName1);  // 파일(이미지) 웹서버에 올림
                uploadFile(uploadFilePath + uploadFileName2);
                uploadFile(uploadFilePath + uploadFileName3);

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
            } catch (
                    MalformedURLException e) {
                e.printStackTrace();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (data.equals("1")) {
                flag = false;
                Toast.makeText(WriteFindActivity.this, "글쓰기 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteFindActivity.this, SubActivity.class);
                intent.putExtra("id", sid);
                intent.putExtra("nickname", swriter); // 게시물 작성하고 작성자의 닉네임을 넘겨줌, 나중에 게시물의 세부액티비티에서 자신의 글인지 비교할때 사용
                intent.putExtra("category", categorycheck);
                WriteFindActivity.this.finish();
                startActivity(intent);
            } else if (data.equals("0")) {
                Toast.makeText(WriteFindActivity.this, "글쓰기 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class editnoticeDB extends AsyncTask<Void, Integer, Void> {          //DB에 게시물 수정

        String param;
        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            if (uploadFileName1 != null) {
                if (uploadFileName2 != null) {
                    if (uploadFileName3 != null) {
                        param = "&findnotice=" + write_editcheck + "&no_notice=" + no_notice + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                                "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                                "&imgname1=" + uploadFileName1 + "&imgname2=" + uploadFileName2 + "&imgname3=" + uploadFileName3 + "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude + "";
                    } else {
                        param = "&findnotice=" + write_editcheck + "&no_notice=" + no_notice + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                                "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                                "&imgname1=" + uploadFileName1 + "&imgname2=" + uploadFileName2 + "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude + "";
                    }
                } else {
                    param = "&findnotice=" + write_editcheck + "&no_notice=" + no_notice + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                            "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                            "&imgname1=" + uploadFileName1 + "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude + "";
                }
            } else {
                param = "&findnotice=" + write_editcheck + "&no_notice=" + no_notice + "&s_id=" + sid + "&s_title=" + stitle + "&s_category=" + scategory +
                        "&s_type=" + stype + "&s_date=" + sdate + "&s_lostdate=" + slostdate + "&s_reward=" + sreward + "&s_content=" + scontent +
                        "&s_lostplace=" + slostplace + "&d_latitude=" + dlatitude + "&d_longitude=" + dlongitude + "";
            }

            try {
                /* 서버연결 */
                URL url = new URL(MainActivity.SERVERIP + "writenotice.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                uploadFile(uploadFilePath + uploadFileName1);  // 파일(이미지) 웹서버에 올림
                uploadFile(uploadFilePath + uploadFileName2);
                uploadFile(uploadFilePath + uploadFileName3);

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
                flag = false;
                Toast.makeText(WriteFindActivity.this, "글수정 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteFindActivity.this, SubActivity.class);
                intent.putExtra("id", sid);
                intent.putExtra("nickname", swriter); // 게시물 수정하고 작성자의 닉네임을 넘겨줌, 나중에 게시물의 세부액티비티에서 자신의 글인지 비교할때 사용
                intent.putExtra("category", categorycheck);
                WriteFindActivity.this.finish();
                startActivity(intent);
            } else if (data.equals("0")) {
                Toast.makeText(WriteFindActivity.this, "글수정 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int serverResponseCode = 0;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 4 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile())  // 파일이 없을경우
            return 0;
        else {
            try {

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(MainActivity.SERVERIP + "uploadfile.php");

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serverResponseCode;
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
            iv_preview1.setImageBitmap(result[0]);
            iv_preview2.setImageBitmap(result[1]);
            iv_preview3.setImageBitmap(result[2]);
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
