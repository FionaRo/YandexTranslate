package ru.yandex.translate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.yandex.translate.Objects.History;
import ru.yandex.translate.Objects.Settings;
import ru.yandex.translate.R;
import ru.yandex.translate.YanAPI.GetLangs;
import ru.yandex.translate.YanAPI.Translate;

public class TranslateActivity extends AppCompatActivity {

    HashMap<String, String> langs;
    Settings settings;
    History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            settings = Settings.loadSettings("settings"); //загружаем настройки с языками

            Spinner fromLang = (Spinner) findViewById(R.id.from_lang); //список с каких языков переводить
            Spinner toLang = (Spinner) findViewById(R.id.to_lang); //список на какие языки переводить

            Collection<String> collection = settings.getLangs().keySet(); //получаем ключи: Английский, Русский и т.д
            LinkedList<String> list = new LinkedList<>(collection); //преобразуем в связный список
            //создаём адаптер, связанный со списком
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

            fromLang.setAdapter(adapter); //устанавливаем адаптер
            fromLang.setSelection(adapter.getPosition("Русский")); //устанавливаем по умолчанию

            toLang.setAdapter(adapter);
            toLang.setSelection(adapter.getPosition("Английский")); //устанавливаем по умолчанию

            history = History.loadHistory("history"); //загружаем историю

        } catch (IOException ex) {
            this.finish();
        } catch (ClassNotFoundException ex) {
            this.finish();
        } catch (TimeoutException ex) {
            this.finish();
        } catch (InterruptedException e) {
            this.finish();
        } catch (ExecutionException e) {
            this.finish();
        } catch (JSONException e) {
            this.finish();
        }
    }

    //переход на экран истории
    public void toHistory(View view) {
        Intent intent = new Intent(TranslateActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    //переход на экран изранного
    public void toFavor(View view) {
        Intent intent = new Intent(TranslateActivity.this, FavorActivity.class);
        startActivity(intent);
    }

    //перевод
    public void YanTranslate(View view) {
        try {
            history = History.loadHistory("history"); //перезвгружаем историю - возможно что-то поменялось
        } catch (IOException ex) {
            return;
        } catch (ClassNotFoundException ex) {
            return;
        }
        EditText text = (EditText) findViewById(R.id.trans_text);
        AsyncTask<String, Void, String> t = new Translate();

        String fromLang = ((Spinner) findViewById(R.id.from_lang)).getSelectedItem().toString(); //получем ключ - с какого языка переводить
        String toLang = ((Spinner) findViewById(R.id.to_lang)).getSelectedItem().toString();//на какой язык переводить
        //получаем направление перевода для запроса
        String directionTranslate = settings.getLangs().get(fromLang).toString() + "-" + settings.getLangs().get(toLang).toString();
        t.execute(directionTranslate, text.getText().toString()); //запускаем перевод
        TextView et = (TextView) findViewById(R.id.translation);
        String translate;
        try {
            translate = t.get(5, TimeUnit.SECONDS);
            et.setText(translate); //выводим перевод
            String[] element = {text.getText().toString(), translate};
            history.addHistory(element); //добавляем перевод в историю
            history.saveHistory("history"); //сохраняем историю
        } catch (IOException ex) {
            this.finish();
        } catch (ExecutionException ex) {
            this.finish();
        } catch (TimeoutException ex) {
            et.setText("Соединение с интернетом не установлено");
            return;
        } catch (InterruptedException ex) {
            et.setText("Не удалось выполнить соединение с сервером");
            return;
        }

    }

    //смена языков
    public void Swap(View view) {
        //получаем позицию выбранных языков
        int fromLang = ((Spinner) findViewById(R.id.from_lang)).getSelectedItemPosition();
        int toLang = ((Spinner) findViewById(R.id.to_lang)).getSelectedItemPosition();


        Spinner from = (Spinner) findViewById(R.id.from_lang);
        Spinner to = (Spinner) findViewById(R.id.to_lang);
        //заменяем занчения выбора
        from.setSelection(toLang);
        to.setSelection(fromLang);
    }
}
