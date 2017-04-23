package ru.yandex.translate.YanAPI;


import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class GetLangs extends AsyncTask<String, Void, JSONObject> {

    @Override
    //получение доступных языков - возвращаем данные в формате Json
    protected JSONObject doInBackground(String... params) {
        try {
            String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20170416T200755Z.26f2351e3ef284b9.146253f0d85227ff84e6e92eaddfc042d2cf5ebd";
            URL urlObj = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes("&ui=" + params[0]);
            InputStream response = connection.getInputStream();
            String json = new java.util.Scanner(response).nextLine();
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (Exception ex) {
            return null;
        }
    }
}
