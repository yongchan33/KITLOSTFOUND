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

public class Fragment3 extends Fragment {

    private View view;
    private String sid;
    private ListView mlistView;
    private Frag3ListAdapter adapter;
    protected ArrayList<ListSellNoticeItem> sellthinglist = new ArrayList<ListSellNoticeItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment3, container, false);
        mlistView = (ListView) view.findViewById(R.id.frag3listview);

        sid = getArguments().getString("id");

        getsellthingDB gstDB = new getsellthingDB();
        gstDB.execute();

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SellThingActivity.class);
                intent.putExtra("no_notice", sellthinglist.get(i).getNo());
                intent.putExtra("title", sellthinglist.get(i).getTitle());
                intent.putExtra("category", sellthinglist.get(i).getCategory());
                intent.putExtra("type", sellthinglist.get(i).getType());
                intent.putExtra("date", sellthinglist.get(i).getDate());
                intent.putExtra("thingname", sellthinglist.get(i).getThingname());
                intent.putExtra("trademethod", sellthinglist.get(i).getTrademethod());
                intent.putExtra("price", sellthinglist.get(i).getPrice());
                intent.putExtra("content", sellthinglist.get(i).getContent());
                intent.putExtra("writer", sellthinglist.get(i).getWriter());
                intent.putExtra("id", sid);      //현재 로그인한 아이디 전달
                startActivity(intent);
            }
        });

        return view;
    }

    public class getsellthingDB extends AsyncTask<Void, Integer, String> {

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
                //URL url = new URL("http://10.0.2.2/getsellthing.php");
                URL url = new URL("http://192.168.0.5/getsellthing.php");
                //URL url = new URL("http://192.168.123.122/getsellthing.php");
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
            String no_notice, title, category, type, date, thingname, trademethod, price, content, writer;
            Date tmpdate;

            try {
                JSONObject root = new JSONObject(str);
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
                    thingname = jo.getString("thingname");
                    trademethod = jo.getString("trademethod");
                    price = jo.getString("price");
                    content = jo.getString("content");
                    writer = jo.getString("writer");
                    sellthinglist.add(new ListSellNoticeItem(no, title, category, type, date, thingname, trademethod, price, content, writer));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new Frag3ListAdapter(view.getContext(), R.layout.frag3item, sellthinglist);
            mlistView.setAdapter(adapter);
            super.onPostExecute(str);
        }
    }
}