package kr.ac.kumoh.LostNFound;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment2 extends Fragment implements AbsListView.OnScrollListener {

    private boolean lastItemVisibleFlag = false; // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private int page = 0; // 페이징변수 초기 값 0
    private final int OFFSET = 10; // 한 페이지마다 로드할 데이터 갯수
    private boolean mLockListView = false; // 데이터 불러올때 중복안되게 하기위한 변수
    private ProgressBar mBottomProgressBar; // 리스트뷰 끊어서 읽을 때 프로그레스바
    private String keyword;
    private String sid, snickname; // 로그인한 아이디, 닉네임
    private View view;
    private ListView mlistView;
    private SwipeRefreshLayout refreshview; // 새로고침 레이아웃
    private Frag2ListAdapter adapter;
    protected ArrayList<ListFindNoticeItem> findownerlist = new ArrayList<ListFindNoticeItem>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment2, container, false);
        mlistView = (ListView) view.findViewById(R.id.frag2listview);
        mBottomProgressBar = (ProgressBar) view.findViewById(R.id.frag2progressbar);
        refreshview = (SwipeRefreshLayout) view.findViewById(R.id.contentSwipeLayout);

        sid = getArguments().getString("id");
        snickname = getArguments().getString("nickname");

        adapter = new Frag2ListAdapter(view.getContext(), R.layout.frag2item, findownerlist);
        mlistView.setAdapter(adapter);
        mlistView.setOnScrollListener(this);

        mBottomProgressBar.setVisibility(View.GONE);

        getfindownerDB gfoDB = new getfindownerDB();
        gfoDB.execute();

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), FindOwnerActivity.class);
                intent.putExtra("no_notice", findownerlist.get(i).getNo());
                intent.putExtra("title", findownerlist.get(i).getTitle());
                intent.putExtra("writer", findownerlist.get(i).getWriter());
                intent.putExtra("category", findownerlist.get(i).getCategory());
                intent.putExtra("type", findownerlist.get(i).getType());
                intent.putExtra("date", findownerlist.get(i).getDate());
                intent.putExtra("content", findownerlist.get(i).getContent());
                intent.putExtra("imgpath", findownerlist.get(i).getImgpath());
                intent.putExtra("imgname1", findownerlist.get(i).getImgname1());
                intent.putExtra("imgname2", findownerlist.get(i).getImgname2());
                intent.putExtra("imgname3", findownerlist.get(i).getImgname3());
                intent.putExtra("id", sid); // 현재 로그인한 아이디 전달
                intent.putExtra("nickname", snickname); // 현재 사용자의 닉네임 전달
                startActivity(intent);
            }
        });

        refreshview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                view_Refresh();
                refreshview.setRefreshing(false);
                Fragment2.this.onResume(); // onResume 호출 안할시 새로고침 모양이 계속 유지되는 현상 발생해서 호출해줌
            }
        });

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
        // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
        // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
        // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
            mBottomProgressBar.setVisibility(View.VISIBLE);

            // 다음 데이터를 불러온다.
            new getfindownerDB().execute();
        } else if (page != 1 && findownerlist.size() == adapter.getCount()) { // 모든 게시물을 다 불러왔으면
            mLockListView = true;
            mlistView.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS); // 리스트뷰 끝에서 스크롤 막기
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
        // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
        // totalItemCount : 리스트 전체의 총 갯수
        // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    public void view_Refresh() { // 위에서 아래로 드래그시 새로고침
        page = 0;
        findownerlist.clear();
        new getfindownerDB().execute();
    }

    public void search_Refresh(String query) {
        this.keyword = query;
        new Search_getfindownerDB().execute();
    }

    public class getfindownerDB extends AsyncTask<Void, Integer, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(view.getContext());

        Integer no;
        String no_notice, title, category, type, date, content, imgpath, imgname1, imgname2, imgname3, writer;
        Date tmpdate;

        @Override
        protected void onPreExecute() {
            mLockListView = true;
            if (page == 0) {
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setTitle("로딩중");
                asyncDialog.setMessage("로딩 중 입니다...");
                asyncDialog.setCancelable(false);
                asyncDialog.show();
            }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(MainActivity.SERVERIP + "getfindowner.php");
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
                JSONObject root = new JSONObject(jsonHtml.toString());
                JSONArray ja = root.getJSONArray("results");
                for (int i = (page * 9) + page; i < (page * 10) + OFFSET; i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    no_notice = jo.getString("no");
                    no = Integer.parseInt(no_notice);
                    title = jo.getString("title");
                    category = jo.getString("category");
                    type = jo.getString("type");
                    date = jo.getString("date");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분
                    try {
                        tmpdate = sdf.parse(date);
                        date = sdf.format(tmpdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    content = jo.getString("content");
                    imgpath = jo.getString("imgpath");
                    imgname1 = jo.getString("imgname1");
                    imgname2 = jo.getString("imgname2");
                    imgname3 = jo.getString("imgname3");
                    writer = jo.getString("writer");
                    findownerlist.add(new ListFindNoticeItem(no, title, category, type, date, content, imgpath, imgname1, imgname2, imgname3, writer));
                } //주인찾기 리스트에 물건찾기 게시물을 추가
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            long end = System.currentTimeMillis() - start;
            Log.d("게시물 출력(주인찾기)", "수행 시간 : " + end + "ms");
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            mBottomProgressBar.setVisibility(View.GONE);
            page++;
            adapter.notifyDataSetChanged();
            this.asyncDialog.setCancelable(true);
            if (this.asyncDialog.isShowing() == true)
                asyncDialog.dismiss();
            onResume();
            mLockListView = false;
            super.onPostExecute(avoid);
        }
    }

    public class Search_getfindownerDB extends AsyncTask<Void, Integer, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(view.getContext());

        Integer no;
        String no_notice, title, category, type, date, content, imgpath, imgname1, imgname2, imgname3, writer;
        Date tmpdate;

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setTitle("로딩중");
            asyncDialog.setMessage("로딩 중 입니다...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder jsonHtml = new StringBuilder();
            String param = "&search=2" + "&s_keyword=" + keyword;
            try {
                // 연결 url 설정
                URL url = new URL(MainActivity.SERVERIP + "searchnotice.php");
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                // 연결되었으면.
                if (conn != null) { //널은아니다.
                    //conn.setConnectTimeout(10000);
                    //conn.setUseCaches(false);
                    OutputStream outs = conn.getOutputStream();
                    outs.write(param.getBytes("UTF-8"));
                    outs.flush();
                    outs.close();
                    // 연결되었음 코드가 리턴되면
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
                JSONObject root = new JSONObject(jsonHtml.toString());
                JSONArray ja = root.getJSONArray("results");
                findownerlist.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    no_notice = jo.getString("no");
                    no = Integer.parseInt(no_notice);
                    title = jo.getString("title");
                    category = jo.getString("category");
                    type = jo.getString("type");
                    date = jo.getString("date");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분
                    try {
                        tmpdate = sdf.parse(date);
                        date = sdf.format(tmpdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    content = jo.getString("content");
                    imgpath = jo.getString("imgpath");
                    imgname1 = jo.getString("imgname1");
                    imgname2 = jo.getString("imgname2");
                    imgname3 = jo.getString("imgname3");
                    writer = jo.getString("writer");
                    findownerlist.add(new ListFindNoticeItem(no, title, category, type, date, content, imgpath, imgname1, imgname2, imgname3, writer));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            adapter.notifyDataSetChanged();
            this.asyncDialog.setCancelable(true);
            if (this.asyncDialog.isShowing() == true)
                asyncDialog.dismiss();
            onResume();
            super.onPostExecute(avoid);
        }
    }
}