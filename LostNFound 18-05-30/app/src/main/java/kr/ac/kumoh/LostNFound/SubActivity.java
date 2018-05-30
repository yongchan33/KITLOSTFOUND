package kr.ac.kumoh.LostNFound;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SubActivity extends Activity implements View.OnClickListener {

    private final static String tag = "";

    private final int FRAGMENT1 = 1;
    private final int FRAGMENT2 = 2;
    private final int FRAGMENT3 = 3;

    private String sid, snickname, semail;
    private Integer categorycheck = 10;
    private Button bt_tab1, bt_tab2, bt_tab3, bt_tab4;
    private DrawerLayout drawer;

    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction transaction = fragmentManager.beginTransaction();

    private SharedPreferences setting;  // 초기 화면에서 자동 로그인에 설정된 값
    private SharedPreferences.Editor editor;

    protected ArrayList<ListFindNoticeItem> findthinglist = new ArrayList<ListFindNoticeItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_main);

        Bundle bundle = new Bundle(1);     //파라미터는 전달할 데이터 개수

        bt_tab1 = (Button) findViewById(R.id.bt_tab1);
        bt_tab2 = (Button) findViewById(R.id.bt_tab2);
        bt_tab3 = (Button) findViewById(R.id.bt_tab3);
        bt_tab4 = (Button) findViewById(R.id.bt_tab4);

        // 탭 버튼에 대한 리스너 연결
        bt_tab1.setOnClickListener(this);
        bt_tab2.setOnClickListener(this);
        bt_tab3.setOnClickListener(this);
        bt_tab4.setOnClickListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");
        categorycheck = intent.getIntExtra("category", 10);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        getmyinfoDB gmiDB = new getmyinfoDB();
        gmiDB.execute();

        if (categorycheck == 10)  //초기 실행일 경우
        {
            Fragment1 fragment1 = new Fragment1();
            bundle.putString("id", sid);     //key, value
            fragment1.setArguments(bundle);
            transaction.replace(R.id.fragment_container, fragment1);
            transaction.commit();
        } else if (categorycheck == 0) {
            Fragment1 fragment1 = new Fragment1();
            bundle.putString("id", sid);     //key, value
            fragment1.setArguments(bundle);
            transaction.replace(R.id.fragment_container, fragment1);
            transaction.commit();
        } else if (categorycheck == 1) {
            Fragment2 fragment2 = new Fragment2();
            bundle.putString("id", sid);     //key, value
            fragment2.setArguments(bundle);
            transaction.replace(R.id.fragment_container, fragment2);
            transaction.commit();
        } else if (categorycheck == 2) {
            Fragment3 fragment3 = new Fragment3();
            bundle.putString("id", sid);     //key, value
            fragment3.setArguments(bundle);
            transaction.replace(R.id.fragment_container, fragment3);
            transaction.commit();
        }
    }

    private void callFragment(int frament_no) {
        // 프래그먼트 사용을 위해
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle(1);     //파라미터는 전달할 데이터 개수

        switch (frament_no) {
            case FRAGMENT1:
                // 프래그먼트1 호출
                Fragment1 fragment1 = new Fragment1();
                bundle.putString("id", sid);     //key, value
                fragment1.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case FRAGMENT2:
                // 프래그먼트2 호출
                Fragment2 fragment2 = new Fragment2();
                bundle.putString("id", sid);     //key, value
                fragment2.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;

            case FRAGMENT3:
                // 프래그먼트3 호출
                Fragment3 fragment3 = new Fragment3();
                bundle.putString("id", sid);     //key, value
                fragment3.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment3);
                transaction.commit();
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_tab1:  // 물건찾기 탭
                // 클릭 시 '프래그먼트1' 호출
                callFragment(FRAGMENT1);
                break;

            case R.id.bt_tab2:  // 주인찾기 탭
                // 클릭 시 '프래그먼트2' 호출
                callFragment(FRAGMENT2);
                break;

            case R.id.bt_tab3:  // 벼룩시장 탭
                // 클릭 시 '프래그먼트3' 호출
                callFragment(FRAGMENT3);
                break;

            case R.id.bt_tab4:  // 설정 탭
                /*Intent intent = new Intent(SubActivity.this, SettingActivity.class);
                intent.putExtra("id", sid);
                startActivity(intent);*/
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {  // drawerlayout이 왼쪽에서 열려 있지 않다면
                    drawer.openDrawer(Gravity.LEFT);  // drawerlayout을 왼쪽에서 open
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);  // 터치(클릭)로 종료 막기
                    bt_tab1.setEnabled(false);
                    bt_tab2.setEnabled(false);
                    bt_tab3.setEnabled(false);
                } else if (drawer.isDrawerOpen(Gravity.LEFT)) {  // drawerlayout이 왼쪽에서 열려 있다면
                    drawer.closeDrawer(Gravity.LEFT);  // drawerlayout을 왼쪽으로 close
                    bt_tab1.setEnabled(true);
                    bt_tab2.setEnabled(true);
                    bt_tab3.setEnabled(true);
                }
                break;
        }
    }

    public void onMoveFindWirte(View view) {
        Intent intent = new Intent(SubActivity.this, WriteFindActivity.class);
        intent.putExtra("id", sid);
        startActivity(intent);
    }

    public void onMoveSellWirte(View view) {
        Intent intent = new Intent(SubActivity.this, WriteSellActivity.class);
        intent.putExtra("id", sid);
        startActivity(intent);
    }

    public void onMoveMyInfo(View view) {
        Intent intent = new Intent(SubActivity.this, MyInfoActivity.class);
        intent.putExtra("id", sid);    //MyInfoActivity에 로그인한 아이디를 넘겨줌
        intent.putExtra("nickname", snickname);  //닉네임을 넘겨줌
        intent.putExtra("email", semail);  //이메일을 넘겨줌
        startActivity(intent);
    }

    public void onLogOut(View view) {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(SubActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //기록에 남아있는 Activity들을 지움
        startActivity(intent);
    }

    public void onMoveLeave(View view) {
        Intent intent = new Intent(SubActivity.this, LeaveActivity.class);
        intent.putExtra("id", sid);    //LeaveActivity에 로그인한 아이디를 넘겨줌
        startActivity(intent);
    }

    public void onSettingBack(View view) {
        drawer.closeDrawer(Gravity.LEFT);
        bt_tab1.setEnabled(true);
        bt_tab2.setEnabled(true);
        bt_tab3.setEnabled(true);
    }

    public class getmyinfoDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            String param = "&s_id=" + sid + "";
            try {
                URL url = new URL("http://192.168.0.5/getmyinfo.php");
                //URL url = new URL("http://192.168.123.122/getmyinfo.php");
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
                snickname = Result.getString("nickname");
                semail = Result.getString("email");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}