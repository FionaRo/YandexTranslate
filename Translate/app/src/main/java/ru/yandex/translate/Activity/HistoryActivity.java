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
    LinkedList<String[]> selectedItem; //список выбранных элементов
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            history = History.loadHistory("history");
            favorites = Favorites.loadFavor("favor"); //подгружаем избранное для возможности добавления элементов из истории
        } catch (IOException ex) {
            this.finish();
        } catch (ClassNotFoundException ex){
            this.finish();
        }

        final ListView listView = (ListView) findViewById(R.id.list_history);
        final LinkedList<String> list = new LinkedList<>();
        //приводим элементы к виду "Текст - перевод"
        for (String[] s :
                history.getHistory()) {
            list.push(s[0] + " - " + s[1]);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, list);
        listView.setAdapter(adapter);

        selectedItem = new LinkedList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SparseBooleanArray sp = listView.getCheckedItemPositions();
                String[] hist = list.get(position).split(" - ");
                Button butAdd = (Button) findViewById(R.id.btn_add_favor), butDel = (Button) findViewById(R.id.btn_delete);
                if (sp.get(position)) {
                    selectedItem.push(hist);
                } else {
                    for (String[] el:
                            selectedItem) {
                        if (el[0].equals(hist[0]) && el[1].equals(hist[1])) {
                            selectedItem.remove(el);
                            break;
                        }
                    }
                }
                //0 элементов - кнопки не показываются
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
        for (String[] el :
                selectedItem) {
            String[] favor = {el[0], el[1]};
            favorites.addFavor(favor);
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
        for (String[] el :
                selectedItem) {
            history.deleteHistory(el);
        }
        try {
            history.saveHistory("history"); //сохраняем историю
        } catch (IOException ex) {
           return;
        }
        for (String[] el :
                selectedItem) {
            String del = el[0] + " - " + el[1];
            adapter.remove(del); //удаляем все элементы из адаптера
        }
        //уведомляем об изменеии
        adapter.notifyDataSetChanged();
        //чистим список выбранных элементов
        selectedItem.clear();
    }


}
