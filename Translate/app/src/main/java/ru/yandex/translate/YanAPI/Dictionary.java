package ru.yandex.translate.YanAPI;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

import ru.yandex.translate.Objects.Settings;


public class Dictionary extends AsyncTask<String, Void, JSONObject> {

    @Override
    //получение доступных языков - возвращаем данные в формате Json
    protected JSONObject doInBackground(String... params) {
        try {
            String urlStr = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20170416T203604Z.25ba57a91a07caea.c9028b6b91977047a91cdfacec218aeca30666e9";
            URL urlObj = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes("&lang=" + params[0] + "&text=" + URLEncoder.encode(params[1], "UTF-8"));
            InputStream response = connection.getInputStream();
            String json = new java.util.Scanner(response).nextLine();
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (Exception ex) {
            return null;
        }
    }

    public static LinkedList<String> getDictionary(JSONObject obj) {

        if (obj == null) return null;

        try {
            JSONArray array = obj.getJSONArray("def");
            obj = array.getJSONObject(0);
            array = obj.getJSONArray("tr");
            //obj = array.getJSONObject(0);
            //array = obj.getJSONArray("syn");
            LinkedList<String> dict = new LinkedList<>();

            for (int i =0; i<array.length(); i++){
                dict.push(((JSONObject)array.get(i)).get("text").toString());
            }
            return dict;
        } catch (Exception ex) {
            return null;
        }
    }

}

