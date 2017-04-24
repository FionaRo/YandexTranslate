package ru.yandex.translate.Activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.LinkedList;

import ru.yandex.translate.Objects.*;
import ru.yandex.translate.R;
import ru.yandex.translate.YanAPI.Translate;

//окно избранного
public class FavorActivity extends Activity {

    Favorites favorites;
    LinkedList<TextTranslate> selectedItem; //выбранные элементы избранного
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //
        try {
            favorites = Favorites.loadFavor("favor");
        } catch (Exception ex) {
            favorites = new Favorites(); //вместо того, чтобы не отображать окно избранного в принципе - будем отображать пустой список
        }

        final ListView listView = (ListView) findViewById(R.id.list_favor);
        final LinkedList<String> list = new LinkedList<>();

        //кладем в список преобразованный перевод в виде "Текст - Перевод"
        for (TextTranslate el :
                favorites.getFavorites()) {
            list.push(el.toString());
        }

        //заполняем listView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, list);
        listView.setAdapter(adapter);

        //Создаем список выбранных элементов
        selectedItem = new LinkedList<>();

        //обрабатываем выбор элементов из списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SparseBooleanArray sp = listView.getCheckedItemPositions();
                String[] hist = list.get(position).split(" - "); //получаем два элемента: текст и перевод
                TextTranslate textTranslate = new TextTranslate(hist[0], hist[1]);

                Button butDelete = (Button) findViewById(R.id.btn_delete); //кнопка удаления

                //если выбрано - добавляем элемент, иначе находим и удаляем
                if (sp.get(position)) {
                    selectedItem.push(textTranslate);
                } else {
                    selectedItem.remove(textTranslate);
                }

                //кнопка удаления показывается в зависимости от количества выбранных элементов
                //0 элементов - не показывается
                //2 и больше - показывается
                if (selectedItem.size() > 0) {
                    butDelete.setVisibility(View.VISIBLE);
                } else {
                    butDelete.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //удаление из Избранного
    public void deleteFromFavor(View view) {
        //удаляем все выбранные элементы
        for (TextTranslate el :
                selectedItem) {
            favorites.deleteFavor(el);
        }

        //пытаемся сохранить историю
        try {
            favorites.saveFavor("favor");
        } catch (IOException ex) {
            return;
        }

        //удаляем элементы из адаптера
        for (TextTranslate el :
                selectedItem) {
            adapter.remove(el.toString());
        }

        //уведомляем об изменении данных
        adapter.notifyDataSetChanged();

        //чистим список выбранных элементоы
        selectedItem.clear();
    }
}
