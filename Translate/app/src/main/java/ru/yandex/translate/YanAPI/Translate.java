package ru.yandex.translate.YanAPI;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class Translate extends AsyncTask<String, Void, String> {

    @Override
    //получем перевод текста - возвращает строку
    protected String doInBackground(String... params) {
        try {
            String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170416T200755Z.26f2351e3ef284b9.146253f0d85227ff84e6e92eaddfc042d2cf5ebd";
            URL urlObj = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

            dataOutputStream.writeBytes("text=" + URLEncoder.encode(params[1], "UTF-8") + "&lang=" + params[0]);
            InputStream response = connection.getInputStream();
            String json = new java.util.Scanner(response).nextLine();
            int start = json.indexOf("["); //получаем индексы, ограничивающие преведенную строку
            int end = json.indexOf("]");
            String translated = json.substring(start + 2, end - 1); //обрезаем строку
            return translated;
        } catch (Exception ex) {
            return null;
        }
    }
}
