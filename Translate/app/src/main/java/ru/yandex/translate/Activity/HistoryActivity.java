package ru.yandex.translate.Activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.LinkedList;

import ru.yandex.translate.Objects.Favorites;
import ru.yandex.translate.Objects.*;
import ru.yandex.translate.R;


public class HistoryActivity extends Activity {

    History history;
    Favorites favorites;
    LinkedList<TextTranslate> selectedItem; //список выбранных элементов
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //подгружаем историю
        try {
            history = History.loadHistory("history");
        } catch (Exception ex) {
            history = new History();
        }
        //подргружаем избранное
        try {
            favorites = Favorites.loadFavor("favor"); //подгружаем избранное для возможности добавления элементов из истории
        } catch (Exception ex) {
            favorites = new Favorites();
        }


        final ListView historyView = (ListView) findViewById(R.id.list_history); //История
        final LinkedList<String> historyElements = new LinkedList<>(); //список элементов listView

        //приводим элементы к виду "Текст - перевод"
        for (TextTranslate el :
                history.getHistory()) {
            historyElements.push(el.toString());
        }

        //заполняем ListView историей
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, historyElements);
        historyView.setAdapter(adapter);
        //создаем список выбранных элементов
        selectedItem = new LinkedList<>();

        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SparseBooleanArray sp = historyView.getCheckedItemPositions();
                String[] hist = historyElements.get(position).split(" - ");
                TextTranslate textTranslate = new TextTranslate(hist[0], hist[1]);
                Button butAdd = (Button) findViewById(R.id.btn_add_favor), butDel = (Button) findViewById(R.id.btn_delete);

                //если элемент выбран - добавляем в список выбранных, иначе удаляем из него
                if (sp.get(position)) {
                    selectedItem.push(textTranslate);
                } else {
                    selectedItem.remove(textTranslate);
                }

                //0 элементов - кнопки не показываются
                //1 и больше - показываются
                if (selectedItem.size() > 0) {
                    butAdd.setVisibility(View.VISIBLE);
                    butDel.setVisibility(View.VISIBLE);
                } else {
                    butAdd.setVisibility(View.INVISIBLE);
                    butDel.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //добавление в избранное
    public void addToFavor(View view) {
        //добавляем все элементы в избранное
        for (TextTranslate el :
                selectedItem) {
            favorites.addFavor(el);
            try {
                favorites.saveFavor("favor"); //сохраняем избранное
            } catch (IOException ex) {
                return;
            }
        }
    }

    //удаление из истории
    public void deleteFromHistory(View view) {
        //удаляем все выбранные элементы
        for (TextTranslate el :
                selectedItem) {
            history.deleteHistory(el);
        }
        try {
            history.saveHistory("history"); //сохраняем историю
        } catch (IOException ex) {
            return;
        }
        for (TextTranslate el :
                selectedItem) {
            adapter.remove(el.toString()); //удаляем все элементы из адаптера
        }
        //уведомляем об изменеии
        adapter.notifyDataSetChanged();
        //чистим список выбранных элементов
        selectedItem.clear();
    }


}
