package ir.alijk.pedarshenas;

import com.squareup.okhttp.*;
import ir.alijk.pedarshenas.config.Config;

import java.io.IOException;
import java.util.Random;

public class SMSManager {
    public static String sendVerificationCode(String number, String playerName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(
                mediaType,
                "receptor=" + number + "&template=" + Config.GHASEDAK_TEMPLATE_NAME + "&type=1&param1=" + verifyCode
        );
        Request request = new Request.Builder()
                .url("https://api.ghasedak.me/v2/verification/send/simple")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("apikey", Config.GHASEDAK_API_KEY)
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response);
        // TODO remove debug message
        response.body().close();

        return verifyCode;
    }
}
