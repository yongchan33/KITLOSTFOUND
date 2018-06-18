package kr.ac.kumoh.LostNFound;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SelectMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private EditText et_detailaddress;
    private String s_place, s_touch_address, s_detail_address, s_search_address;  // 검색창에 쓴 내용, 맵 터치 주소, 상세주소, 검색 주소
    private Double dlatitude, dlongitude;  // 위도, 경도

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectmap);

        Intent intent = getIntent();
        if (intent.hasExtra("latitude")) {   // 게시물 수정시 초기 위도, 경도 설정
            dlatitude = intent.getDoubleExtra("latitude", 36.1444249);
            if (intent.hasExtra("longitude"))
                dlongitude = intent.getDoubleExtra("longitude", 128.39332);
        }
        //editText = (EditText) findViewById(R.id.editText);    //주소 검색할 때 입력받는 EditText
        et_detailaddress = (EditText) findViewById(R.id.detailaddress);  // 상세 주소 입력받는 EditText

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        final Intent intent = getIntent();
        if (intent.hasExtra("lostplace")) {  // 게시물 수정시 초기 위치가 넘어오는지 확인
            if (dlatitude != null && dlongitude != null) {
                String tmpaddress;

                LatLng recentlyplace = new LatLng(dlatitude, dlongitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(recentlyplace);
                markerOptions.title(intent.getStringExtra("lostplace"));
                tmpaddress = intent.getStringExtra("lostplace");
                /*int idx = tmpaddress.indexOf(" ");
                s_touch_address = tmpaddress.substring(0, idx);*/
                s_touch_address = tmpaddress;
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(recentlyplace, 17));
            }
        } else {
            //초기 마커 지정 = 금오공과대학교
            LatLng KIT = new LatLng(36.1444249, 128.39332);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(KIT);
            markerOptions.title("금오공대");
            markerOptions.snippet("금오공과대학교");
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KIT, 17));
        }

        // 맵 클릭(터치) 이벤트 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("지정 위치");
                List<Address> list = null;
                dlatitude = point.latitude; // 위도
                dlongitude = point.longitude; // 경도

                //구글맵에서 금오공과대학교 내에 몇몇건물제외 각 건물별 인식이 되지 않아 임의로 지정해주는 부분
                if (36.146420 <= dlatitude && dlatitude <= 36.147417 && 128.391712 <= dlongitude && dlongitude <= 128.392742)

                {
                    s_touch_address = "금오공과대학교 글로벌관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);       // 마커(핀) 추가
                } else if (36.146108 <= dlatitude && dlatitude <= 36.146441 && 128.391767 <= dlongitude && dlongitude <= 128.392599)

                {
                    s_touch_address = "금오공과대학교 글로벌관 주차장";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.145420 <= dlatitude && dlatitude <= 36.146217 && 128.391985 <= dlongitude && dlongitude <= 128.392945)

                {
                    s_touch_address = "금오공과대학교 디지털관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.145064 <= dlatitude && dlatitude <= 36.145367 && 128.392078 <= dlongitude && dlongitude <= 128.392518)

                {
                    s_touch_address = "금오공과대학교 디지털관 주차장";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.146260 <= dlatitude && dlatitude <= 36.147109 && 128.393813 <= dlongitude && dlongitude <= 128.394773)

                {
                    s_touch_address = "금오공과대학교 테크노관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.145604 <= dlatitude && dlatitude <= 36.146718 && 128.386361 <= dlongitude && dlongitude <= 128.388308)

                {
                    s_touch_address = "금오공과대학교 운동장";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.144420 <= dlatitude && dlatitude <= 36.145425 && 128.387747 <= dlongitude && dlongitude <= 128.388984)

                {
                    s_touch_address = "금오공과대학교 체육관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.147127 <= dlatitude && dlatitude <= 36.147413 && 128.390772 <= dlongitude && dlongitude <= 128.391110)

                {
                    s_touch_address = "금오공과대학교 푸름관 식당";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.146738 <= dlatitude && dlatitude <= 36.147109 && 128.390925 <= dlongitude && dlongitude <= 128.391299)

                {
                    s_touch_address = "금오공과대학교 푸름관 1동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.146686 <= dlatitude && dlatitude <= 36.147196 && 128.390450 <= dlongitude && dlongitude <= 128.390753)

                {
                    s_touch_address = "금오공과대학교 푸름관 2동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.147379 <= dlatitude && dlatitude <= 36.147728 && 128.390205 <= dlongitude && dlongitude <= 128.390552)

                {
                    s_touch_address = "금오공과대학교 푸름관 3동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.147562 <= dlatitude && dlatitude <= 36.147920 && 128.390315 <= dlongitude && dlongitude <= 128.390937)

                {
                    s_touch_address = "금오공과대학교 푸름관 4동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.147121 <= dlatitude && dlatitude <= 36.147582 && 128.389449 <= dlongitude && dlongitude <= 128.390122)

                {
                    s_touch_address = "금오공과대학교 오름관 1동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.146404 <= dlatitude && dlatitude <= 36.146950 && 128.389613 <= dlongitude && dlongitude <= 128.390326)

                {
                    s_touch_address = "금오공과대학교 오름관 2동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.146162 <= dlatitude && dlatitude <= 36.147149 && 128.388671 <= dlongitude && dlongitude <= 128.389484)

                {
                    s_touch_address = "금오공과대학교 오름관 3동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.145537 <= dlatitude && dlatitude <= 36.146039 && 128.393364 <= dlongitude && dlongitude <= 128.394324)

                {
                    s_touch_address = "금오공과대학교 도서관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.145648 <= dlatitude && dlatitude <= 36.146038 && 128.394342 <= dlongitude && dlongitude <= 128.394739)

                {
                    s_touch_address = "금오공과대학교 도서관 주차장";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.144822 <= dlatitude && dlatitude <= 36.145233 && 128.393947 <= dlongitude && dlongitude <= 128.394360)

                {
                    s_touch_address = "금오공과대학교 우체국";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.144244 <= dlatitude && dlatitude <= 36.144744 && 128.393604 <= dlongitude && dlongitude <= 128.394323)

                {
                    s_touch_address = "금오공과대학교 학생회관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.144140 <= dlatitude && dlatitude <= 36.144712 && 128.392365 <= dlongitude && dlongitude <= 128.393100)

                {
                    s_touch_address = "금오공과대학교 본관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.144613 <= dlatitude && dlatitude <= 36.145142 && 128.392229 <= dlongitude && dlongitude <= 128.393130)

                {
                    s_touch_address = "금오공과대학교 본관 주차장";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.144777 <= dlatitude && dlatitude <= 36.144933 && 128.393416 <= dlongitude && dlongitude <= 128.393593)

                {
                    s_touch_address = "금오공과대학교 생협";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.142778 <= dlatitude && dlatitude <= 36.143272 && 128.392653 <= dlongitude && dlongitude <= 128.393119)

                {
                    s_touch_address = "금오공과대학교 이오스관(벤처창업관)";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.147540 <= dlatitude && dlatitude <= 36.147809 && 128.394258 <= dlongitude && dlongitude <= 128.395341)

                {
                    s_touch_address = "금오공과대학교 공동실험실 습관";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.147202 <= dlatitude && dlatitude <= 36.147419 && 128.395819 <= dlongitude && dlongitude <= 128.396210)

                {
                    s_touch_address = "금오공과대학교 영선관리동";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.141190 <= dlatitude && dlatitude <= 36.141709 && 128.394876 <= dlongitude && dlongitude <= 128.395692)

                {
                    s_touch_address = "금오공과대학교 정문";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else if (36.141484 <= dlatitude && dlatitude <= 36.142585 && 128.393567 <= dlongitude && dlongitude <= 128.394823)

                {
                    s_touch_address = "금오공과대학교 정문 주차장";
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    mOptions.title(s_touch_address);
                    mMap.clear();
                    mMap.addMarker(mOptions);
                } else

                {
                    try {
                        double d1 = Double.parseDouble(dlatitude.toString());
                        double d2 = Double.parseDouble(dlongitude.toString());

                        list = geocoder.getFromLocation(
                                d1, // 위도
                                d2, // 경도
                                10); // 얻어올 값의 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                        //Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }
                    //System.out.println(list.get(0).toString());
                    String[] splitStr = list.get(0).toString().split(",");
                    s_touch_address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
                    if (list != null) {
                        if (list.size() == 0) {
                            mOptions.snippet("해당되는 주소 정보가 없습니다.");
                        } else {
                            mOptions.snippet(s_touch_address);
                        }
                    }

                    // LatLng: 위도 경도 쌍을 나타냄
                    mOptions.position(new LatLng(dlatitude, dlongitude));
                    // 마커(핀) 추가
                    mMap.clear();
                    mMap.addMarker(mOptions);
                }
            }
        });
    }

    /*public void onSearchPlace(View view) {          //검색창에서 위치 검색하는 함수
        s_place = editText.getText().toString();
        List<Address> addressList = null;

        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    s_place, // 주소
                    10); // 최대 검색 결과 개수
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(addressList.get(0).toString());
        // 콤마를 기준으로 split
        System.out.println(addressList.get(0).toString());
        String[] splitStr = addressList.get(0).toString().split(",");
        s_search_address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소

        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
        System.out.println(latitude);
        System.out.println(longitude);

        // 좌표(위도, 경도) 생성
        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        // 마커 생성
        MarkerOptions mOptions2 = new MarkerOptions();
        mOptions2.title("검색 결과");
        dlatitude = point.latitude; // 위도
        dlongitude = point.longitude; // 경도

        mOptions2.snippet(s_search_address + " " + s_place);
        mOptions2.position(point);

        // 마커 추가
        mMap.clear();
        mMap.addMarker(mOptions2);
        // 해당 좌표로 화면을 줌 함
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 17));
    }*/

    public void onSelectMapOk(View view) {
        s_detail_address = et_detailaddress.getText().toString();

        Intent intent = new Intent();

        if (et_detailaddress.getText().toString().replace(" ", "").equals(""))  // 상세주소가 공백일 경우 지도에서 선택한 주소를 보냄
            intent.putExtra("place", s_touch_address);
        else
            intent.putExtra("place", s_touch_address.concat(" ").concat(s_detail_address));  // 상세주소가 공백이 아닐경우 지도에서 선택한 주소 + 상세주소를 보냄

        intent.putExtra("latitude", dlatitude);
        intent.putExtra("longitude", dlongitude);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    public void onSelectMapBack(View view) {
        this.finish();
    }

}
