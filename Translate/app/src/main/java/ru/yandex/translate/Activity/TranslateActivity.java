package ru.yandex.translate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.yandex.translate.Objects.History;
import ru.yandex.translate.Objects.Settings;
import ru.yandex.translate.Objects.TextTranslate;
import ru.yandex.translate.R;
import ru.yandex.translate.YanAPI.GetLangs;
import ru.yandex.translate.YanAPI.Translate;

public class TranslateActivity extends AppCompatActivity {

    Settings settings;
    History history;
    HashMap<String, String> recentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //создаем ассоциативный массив недавних запросов - расчитан на одну сессию
        recentRequest = new HashMap<>();

        try {
            settings = Settings.loadSettings("settings"); //загружаем настройки с языками

            Spinner fromLang = (Spinner) findViewById(R.id.from_lang); //список с каких языков переводить
            Spinner toLang = (Spinner) findViewById(R.id.to_lang); //список на какие языки переводить

            Collection<String> collection = settings.getLangs().keySet(); //получаем ключи: Английский, Русский и т.д
            LinkedList<String> list = new LinkedList<>(collection); //преобразуем в связный список
            Collections.sort(list); //сортируем по алфавиту

            //создаём адаптер, связанный со списком
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

            fromLang.setAdapter(adapter); //устанавливаем адаптер
            fromLang.setSelection(adapter.getPosition("Русский")); //устанавливаем по умолчанию

            toLang.setAdapter(adapter);
            toLang.setSelection(adapter.getPosition("Английский")); //устанавливаем по умолчанию

        } catch (Exception ex) {
            this.finish(); //если не загрузились настройки - приложение не будет работать
        }
        try {
            history = History.loadHistory("history"); //загружаем историю
        } catch (Exception ex) {
            history = new History();
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
    public void YanTranslate(View view) throws Exception {

        try {
            history = History.loadHistory("history"); //перезагружаем историю - возможно что-то поменялось
        } catch (Exception ex) {
            history = new History();
        }

        EditText text = (EditText) findViewById(R.id.trans_text); //поле текста ввода
        AsyncTask<String, Void, String> t = new Translate(); //создаем таск на перевод

        String fromLang = ((Spinner) findViewById(R.id.from_lang)).getSelectedItem().toString(); //получем ключ - с какого языка переводить
        String toLang = ((Spinner) findViewById(R.id.to_lang)).getSelectedItem().toString();//на какой язык переводить

        //получаем направление перевода для запроса
        String directionTranslate = settings.getLangs().get(fromLang).toString() + "-" + settings.getLangs().get(toLang).toString();

        t.execute(directionTranslate, text.getText().toString()); //запускаем перевод

        TextView et = (TextView) findViewById(R.id.translation); //поле вывода перевода

        TextTranslate translate = new TextTranslate(); //перевод

        try {
            translate.setDirectionTranslate(fromLang, toLang);

            //проверяем был ли недавно такой запрос
            String request = recentRequest.get(text.getText().toString() + " - " + toLang);
            if (request == null) {
                request = t.get(5, TimeUnit.SECONDS);
                if (request == null)
                    throw new RuntimeException();
                recentRequest.put(text.getText().toString() + " - " + toLang, request);
                translate.setPairTranslate(text.getText().toString(), request);
                history.addHistory(translate); //добавляем перевод в историю
            }
            request = request.replace("\\n", "<br>");
            request = request.replace("\\\"", "&quot;");
            et.setText(Html.fromHtml(request)); //выводим перевод
        } catch (ExecutionException ex) {
            et.setText("Не удалось связаться с сервером");
        } catch (TimeoutException ex) {
            et.setText("Проверьте соединение с интернетом");
            return;
        } catch (InterruptedException ex) {
            et.setText("Запрос к серверу был прерван");
            return;
        } catch (RuntimeException ex) {
            et.setText("Не удалось получить ответ от сервера");
            return;
        }

        try {
            history.saveHistory("history"); //сохраняем историю
        } catch (Exception ex) {
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
