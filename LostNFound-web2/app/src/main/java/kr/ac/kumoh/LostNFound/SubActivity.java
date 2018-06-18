package kr.ac.kumoh.LostNFound;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

public class SubActivity extends Activity implements View.OnClickListener {

    //private final static String tag = "";

    private final int FRAGMENT1 = 1;
    private final int FRAGMENT2 = 2;
    private final int FRAGMENT3 = 3;

    private String sid, snickname, semail;
    private int categorycheck = 10;
    private int tabcheck = 0;// Who am I? 어느 프래그 먼트인가?
    private Button bt_tab1, bt_tab2, bt_tab3, bt_tab4; //물건찾기, 주인찾기, 내글보기, 설정 탭
    private DrawerLayout drawer;

    private SharedPreferences setting;  // 초기 화면에서 자동 로그인에 설정된 값
    private SharedPreferences.Editor editor;

    private SearchView msearchView; // 검색창

    //Fragment
    protected Fragment1 fragment1 = null;
    protected Fragment2 fragment2 = null;
    protected Fragment3 fragment3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_main);

        Intent intent = getIntent();
        sid = intent.getStringExtra("id");
        snickname = intent.getStringExtra("nickname");
        semail = intent.getStringExtra("email");
        categorycheck = intent.getIntExtra("category", 10);

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

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        msearchView = (SearchView) findViewById(R.id.search);
        //확인버튼 활성화
        msearchView.setSubmitButtonEnabled(true);
        msearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msearchView.onActionViewExpanded();
            }
        });

        msearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //검색버튼을 눌렀을 경우

            @Override
            public boolean onQueryTextSubmit(String query) {
                switch (tabcheck) {
                    case 1:
                        fragment1.search_Refresh(query);
                        msearchView.setQuery("", false);
                        msearchView.setIconified(true);
                        msearchView.onActionViewCollapsed();
                        break;
                    case 2:
                        fragment2.search_Refresh(query);
                        msearchView.setQuery("", false);
                        msearchView.setIconified(true);
                        msearchView.onActionViewCollapsed();
                        break;
                    case 3:
                        break;
                    default:
                        // 에러
                        break;
                }
                //new SearchArticle().execute();
                return true;
            }

            //텍스트가 바뀔때마다 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        if (categorycheck == 10)  // 초기 실행일 경우
            callFragment(FRAGMENT1);
        else if (categorycheck == 0) // '물건찾기'일 경우
            callFragment(FRAGMENT1);
        else if (categorycheck == 1) // '주인찾기'일 경우
            callFragment(FRAGMENT2);

    }

    private void callFragment(int frament_no) {
        // 프래그먼트 사용을 위해
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle(2);     //파라미터는 전달할 데이터 개수

        switch (frament_no) {
            case FRAGMENT1: // 물건찾기
                // 프래그먼트1 호출
                bt_tab1.setSelected(true);
                bt_tab2.setSelected(false);
                bt_tab3.setSelected(false);
                bt_tab4.setSelected(false);
                fragment1 = new Fragment1();
                tabcheck = 1;
                bundle.putString("id", sid);     //key, value
                bundle.putString("nickname", snickname);
                fragment1.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case FRAGMENT2: // 주인찾기
                // 프래그먼트2 호출
                bt_tab1.setSelected(false);
                bt_tab2.setSelected(true);
                bt_tab3.setSelected(false);
                bt_tab4.setSelected(false);
                fragment2 = new Fragment2();
                tabcheck = 2;
                bundle.putString("id", sid);     //key, value
                bundle.putString("nickname", snickname);
                fragment2.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;

            case FRAGMENT3: //  내글보기
                // 프래그먼트3 호출
                bt_tab1.setSelected(false);
                bt_tab2.setSelected(false);
                bt_tab3.setSelected(true);
                bt_tab4.setSelected(false);
                fragment3 = new Fragment3();
                tabcheck = 3;
                bundle.putString("id", sid);     //key, value
                bundle.putString("nickname", snickname);
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

            case R.id.bt_tab3:  // 내글보기 탭
                // 클릭 시 '프래그먼트3' 호출
                callFragment(FRAGMENT3);
                break;

            case R.id.bt_tab4:  // 설정 탭
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {  // drawerlayout이 왼쪽에서 열려 있지 않다면
                    drawer.openDrawer(Gravity.LEFT);  // drawerlayout을 왼쪽에서 open
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);  // 터치(클릭)로 종료 막기 (락모드)
                    bt_tab1.setEnabled(false);
                    bt_tab1.setSelected(false);
                    bt_tab2.setEnabled(false);
                    bt_tab2.setSelected(false);
                    bt_tab3.setEnabled(false);
                    bt_tab3.setSelected(false);
                    bt_tab4.setSelected(true);

                } else if (drawer.isDrawerOpen(Gravity.LEFT)) {  // drawerlayout이 왼쪽에서 열려 있다면
                    drawer.closeDrawer(Gravity.LEFT);  // drawerlayout을 왼쪽으로 close
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);  // 락모드 해제 (언락모드)
                    bt_tab1.setEnabled(true);
                    bt_tab2.setEnabled(true);
                    bt_tab3.setEnabled(true);
                    if (tabcheck == 1)
                        bt_tab1.setSelected(true);
                    else if (tabcheck == 2)
                        bt_tab2.setSelected(true);
                    else if (tabcheck == 3)
                        bt_tab3.setSelected(true);
                    bt_tab4.setSelected(false);
                }
                break;
        }
    }

    public void onMoveFindWrite(View view) { // 물건찾기 게시물 작성으로 이동
        Intent intent = new Intent(SubActivity.this, WriteFindActivity.class);
        intent.putExtra("id", sid); // 사용자의 아이디를 넘겨줌
        intent.putExtra("nickname", snickname); // 사용자의 닉네임을 넘겨줌
        startActivity(intent);
    }

    public void onMoveMyInfo(View view) { // 내 정보로 이동
        Intent intent = new Intent(SubActivity.this, MyInfoActivity.class);
        intent.putExtra("id", sid);    //MyInfoActivity에 로그인한 아이디를 넘겨줌
        intent.putExtra("nickname", snickname);  //닉네임을 넘겨줌
        intent.putExtra("email", semail);  //이메일을 넘겨줌
        startActivity(intent);
    }

    public void onLogOut(View view) {
        editor.clear();
        editor.apply();
        this.finish();
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
        drawer.closeDrawer(Gravity.LEFT);  // drawerlayout을 왼쪽으로 close
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);  // 락모드 해제 (언락모드)
        bt_tab1.setEnabled(true);
        bt_tab2.setEnabled(true);
        bt_tab3.setEnabled(true);
        bt_tab4.setEnabled(true);
        bt_tab4.setSelected(false);
        if (tabcheck == 1)
            bt_tab1.setSelected(true);
        else if (tabcheck == 2)
            bt_tab2.setSelected(true);
        else if (tabcheck == 3)
            bt_tab3.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT))   // drawerlayout이 왼쪽에서 열려 있다면 앱 종료 안되게
            return;
        else
            super.onBackPressed();
    }
}