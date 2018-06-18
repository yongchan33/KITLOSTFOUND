package kr.ac.kumoh.LostNFound;

import android.provider.Settings;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String sandroidId, stoken;

    @Override
    public void onTokenRefresh() {
        sandroidId = Settings.Secure.getString(FirebaseInstanceIDService.this.getContentResolver(), Settings.Secure.ANDROID_ID);  //안드로이드 고유 ID값
        stoken = FirebaseInstanceId.getInstance().getToken();  //토큰 값

        sendRegistrationToServer(sandroidId, stoken);
    }

    public void sendRegistrationToServer(String androidId, String token) {  //DB에 안드로이드 고유 ID값과 토큰 값 쌍 저장

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("s_androidId", androidId).add("s_token", token).build();   //안드로이드 ID값과 토큰 값을 전송

        Request request = new Request.Builder().url(MainActivity.SERVERIP + "registtoken.php").post(body).build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
