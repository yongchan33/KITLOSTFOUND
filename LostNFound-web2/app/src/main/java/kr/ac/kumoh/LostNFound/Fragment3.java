package kr.ac.kumoh.LostNFound;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

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

public class Fragment3 extends Fragment {

    private View view;
    private String sid, snickname; // 현재 사용자의 아이디, 닉네임
    private ListView find1ListView, find2ListView; //물건찾기, 주인찾기 리스트뷰
    private Frag1ListAdapter frag1adapter;
    private Frag2ListAdapter frag2adapter;

    protected ArrayList<ListFindNoticeItem> findthinglist = new ArrayList<>(); // 물건찾기 ArrayList
    protected ArrayList<ListFindNoticeItem> findownerlist = new ArrayList<ListFindNoticeItem>(); // 주인찾기 ArrayList

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment3, container, false);
        find1ListView = (ListView) view.findViewById(R.id.frag3find1list);
        find2ListView = (ListView) view.findViewById(R.id.frag3find2list);

        sid = getArguments().getString("id");
        snickname = getArguments().getString("nickname");

        frag1adapter = new Frag1ListAdapter(view.getContext(), R.layout.frag1item, findthinglist);
        find1ListView.setAdapter(frag1adapter);

        frag2adapter = new Frag2ListAdapter(view.getContext(), R.layout.frag2item, findownerlist);
        find2ListView.setAdapter(frag2adapter);

        getmynoticeDB gmndb = new getmynoticeDB();
        gmndb.execute();

        find1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {  // 리스트뷰 아이템마다 각각의 게시물 세부액티비티로 값 넘겨줌
                Intent intent = new Intent(getActivity(), FindThingActivity.class);
                intent.putExtra("no_notice", findthinglist.get(i).getNo());
                intent.putExtra("title", findthinglist.get(i).getTitle());
                intent.putExtra("writer", findthinglist.get(i).getWriter());
                intent.putExtra("category", findthinglist.get(i).getCategory());
                intent.putExtra("type", findthinglist.get(i).getType());
                intent.putExtra("date", findthinglist.get(i).getDate());
                intent.putExtra("lostdate", findthinglist.get(i).getLostdate());
                intent.putExtra("reward", findthinglist.get(i).getReward());
                intent.putExtra("content", findthinglist.get(i).getContent());
                intent.putExtra("imgpath", findthinglist.get(i).getImgpath());
                intent.putExtra("imgname1", findthinglist.get(i).getImgname1());
                intent.putExtra("imgname2", findthinglist.get(i).getImgname2());
                intent.putExtra("imgname3", findthinglist.get(i).getImgname3());
                intent.putExtra("lostplace", findthinglist.get(i).getLostplace());
                intent.putExtra("latitude", findthinglist.get(i).getLatitude());
                intent.putExtra("longitude", findthinglist.get(i).getLongitude());
                intent.putExtra("id", sid); //현재 로그인한 아이디 전달
                intent.putExtra("nickname", snickname); // 현재 사용자의 닉네임 전달
                startActivity(intent);
            }
        });

        find2ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                intent.putExtra("id", sid); //현재 로그인한 아이디 전달
                intent.putExtra("nickname", snickname); // 현재 사용자의 닉네임 전달
                startActivity(intent);
            }
        });

        return view;
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

    public class getmynoticeDB extends AsyncTask<Void, Integer, Void> {  // 내 물건찾기 게시물 불러오기

        ProgressDialog asyncDialog = new ProgressDialog(view.getContext());

        Integer no;
        String no_notice, title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, imgname3, writer, lostplace;
        Double latitude, longitude;
        Date tmpdate, tmplostdate;

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
            StringBuilder jsonfindthing = new StringBuilder(); //물건찾기 결과물
            StringBuilder jsonfindowner = new StringBuilder(); //주인찾기 결과물
            String param = "&mynotice=0" + "&s_id=" + sid;

            try {
                // 연결 url 설정
                URL url = new URL(MainActivity.SERVERIP + "getfindthing.php");
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                // 연결되었으면.
                if (conn != null) {
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
                            // 저장된 텍스트 라인을 jsonfindthing에 붙여넣음
                            jsonfindthing.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
                JSONObject root = new JSONObject(jsonfindthing.toString());
                JSONArray ja = root.getJSONArray("results");
                findthinglist.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);   //인덱스 별로(순서대로)
                    no_notice = jo.getString("no");  //게시물 번호를 가져옴
                    no = Integer.parseInt(no_notice);  //String no를 Integer형으로 변환
                    title = jo.getString("title");  //게시물 제목을 가져옴
                    category = jo.getString("category");  //게시물 카테고리를 가져옴
                    type = jo.getString("type");  //게시물 종류를 가져옴
                    date = jo.getString("date");  //게시물 등록날짜를 가져옴
                    lostdate = jo.getString("lostdate");  //잃어버린 날짜를 가져옴
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");    //date 형식 지정 년-월-일 시:분
                    try {
                        tmpdate = sdf.parse(date);
                        tmplostdate = sdf.parse(lostdate);
                        date = sdf.format(tmpdate);
                        lostdate = sdf.format(tmplostdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    reward = jo.getString("reward");
                    content = jo.getString("content");
                    imgpath = jo.getString("imgpath");
                    imgname1 = jo.getString("imgname1");
                    imgname2 = jo.getString("imgname2");
                    imgname3 = jo.getString("imgname3");
                    writer = jo.getString("writer");
                    lostplace = jo.getString("lostplace");
                    latitude = jo.getDouble("latitude");
                    longitude = jo.getDouble("longitude");
                    findthinglist.add(new ListFindNoticeItem(no, title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, imgname3, writer, lostplace, latitude, longitude));
                }   //물건찾기 리스트에 물건찾기 게시물을 추가
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String param1 = "&mynotice=1" + "&s_id=" + sid;

            try {
                URL url = new URL(MainActivity.SERVERIP + "getfindowner.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                if (conn != null) {
                    OutputStream outs = conn.getOutputStream();
                    outs.write(param1.getBytes("UTF-8"));
                    outs.flush();
                    outs.close();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            String line = br.readLine();
                            if (line == null) break;
                            jsonfindowner.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
                JSONObject root = new JSONObject(jsonfindowner.toString());
                JSONArray ja = root.getJSONArray("results");
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
                }   //주인찾기 리스트에 주인찾기 게시물을 추가
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            frag1adapter.notifyDataSetChanged();
            setListViewHeightBasedOnItems(find1ListView);
            frag2adapter.notifyDataSetChanged();
            setListViewHeightBasedOnItems(find2ListView);
            this.asyncDialog.setCancelable(true);
            if (this.asyncDialog.isShowing() == true)
                asyncDialog.dismiss();
            onResume();
            super.onPostExecute(avoid);
        }
    }
}