package ru.yandex.translate.Objects;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.yandex.translate.YanAPI.GetLangs;


public class Settings implements Serializable {

    HashMap<String, String> langs;

    //возвращаем ассоцциативный массив типа key="Английский", value="en"
    public HashMap<String, String> getLangs() {
        return langs;
    }

    //устанавливаем массив языков
    public void setLangs(HashMap<String, String> langs) {
        this.langs = langs;
    }

    //сериализация класса
    public void saveSettings(String path) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        //сохраняем в файл ./data/settings
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() +
                File.separator + "data" + File.separator + path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        return;
    }

    //десериализация класса
    public static Settings loadSettings(String path) throws IOException, InterruptedException, JSONException, TimeoutException, ExecutionException, ClassNotFoundException {
        try {
            //./data/settings - пытаемся получить данные
            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory() +
                    File.separator + "data" + File.separator + path);
            ObjectInputStream oin = new ObjectInputStream(fis);
            Settings settings;
            settings = (Settings) oin.readObject();
            return settings;
        } catch (FileNotFoundException exception) { //если файл не найден - создаем новые настройки
            Settings settings = new Settings();
            AsyncTask<String, Void, JSONObject> t = new GetLangs();
            t.execute("ru"); //отправляем запрос
            JSONObject jsonObject = t.get(5, TimeUnit.SECONDS);
            if (jsonObject== null)
            {
                throw new TimeoutException();
            }
            jsonObject = jsonObject.getJSONObject("langs");  //получаем список доступных языков на русском в виде Json
            Iterator<String> iterator = jsonObject.keys(); //получаем итератор по ключам в объекте
            HashMap<String, String> langs = new HashMap<>();
            //ходим по полученным ключам и записываем в ассоциативный массив в виде "Английский" = "en"
            while (iterator.hasNext()) {
                String s = iterator.next().toString();
                langs.put(jsonObject.get(s).toString(), s);
            }
            //записываем языки в настройки
            settings.setLangs(langs);
            settings.saveSettings("settings"); //сохраняем
            return settings;
        }
    }
}