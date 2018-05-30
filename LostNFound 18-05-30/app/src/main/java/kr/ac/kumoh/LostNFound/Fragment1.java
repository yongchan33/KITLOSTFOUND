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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment1 extends Fragment {

    private final static String tag = "";
    private String sid;
    private View view;
    private ListView mlistView;
    private Frag1ListAdapter adapter;
    protected ArrayList<ListFindNoticeItem> findthinglist = new ArrayList<ListFindNoticeItem>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment1, container, false);
        mlistView = (ListView) view.findViewById(R.id.frag1listview);

        sid = getArguments().getString("id");

        getfindthingDB gfdb = new getfindthingDB();
        gfdb.execute();

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
                intent.putExtra("lostplace", findthinglist.get(i).getLostplace());
                intent.putExtra("latitude", findthinglist.get(i).getLatitude());
                intent.putExtra("longitude", findthinglist.get(i).getLongitude());
                intent.putExtra("id", sid);      //현재 로그인한 아이디 전달
                startActivity(intent);
            }
        });

        return view;
    }

    public class getfindthingDB extends AsyncTask<Void, Integer, String> {

        ProgressDialog asyncDialog = new ProgressDialog(view.getContext());

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
        protected String doInBackground(Void... voids) {

            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                //URL url = new URL("http://10.0.2.2/getfindthing.php");
                URL url = new URL("http://192.168.0.5/getfindthing.php");
                //URL url = new URL("http://192.168.123.122/getfindthing.php");
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < 5; i++) {
                asyncDialog.setProgress(i * 25);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {
            asyncDialog.dismiss();

            Integer no;
            String no_notice, title, category, type, date, lostdate, reward, content, writer, lostplace;
            Double latitude, longitude;
            Date tmpdate, tmplostdate;

            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
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
                    writer = jo.getString("writer");
                    lostplace = jo.getString("lostplace");
                    latitude = jo.getDouble("latitude");
                    longitude = jo.getDouble("longitude");
                    findthinglist.add(new ListFindNoticeItem(no, title, category, type, date, lostdate, reward, content, writer, lostplace, latitude, longitude));
                }   //물건찾기 리스트에 물건찾기 게시물을 추가
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new Frag1ListAdapter(view.getContext(), R.layout.frag1item, findthinglist);
            mlistView.setAdapter(adapter);
            super.onPostExecute(str);
        }
    }

}