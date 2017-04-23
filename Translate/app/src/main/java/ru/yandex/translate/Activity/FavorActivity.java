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

//окно избранного
public class FavorActivity extends Activity {

    Favorites favorites;
    LinkedList<String[]> selectedItem; //выбранные элементы избранного
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_activity);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            favorites = Favorites.loadFavor("favor");
        } catch (IOException ex) {
            this.finish();
        } catch (ClassNotFoundException ex){
            this.finish();
        }
        final ListView listView = (ListView) findViewById(R.id.list_favor);
        final LinkedList<String> list = new LinkedList<>();
        //преобразуем элементы в формат "Текст - первод"
        for (String[] s :
                favorites.getFavorites()) {
            list.push(s[0] + " - " + s[1]);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, list);
        listView.setAdapter(adapter);
        selectedItem = new LinkedList<>();
        //обрабатываем выбор элементов из списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SparseBooleanArray sp = listView.getCheckedItemPositions();
                String[] hist = list.get(position).split(" - "); //получаем два элемента: текст и перевод
                Button butDelete = (Button) findViewById(R.id.btn_delete);
                //если выбрано - добавляем элемент, иначе находим и удаляем
                if (sp.get(position)) {
                    selectedItem.push(hist);
                } else {
                    for (String[] el :
                            selectedItem) {
                        if (el[0].equals(hist[0]) && el[1].equals(hist[1])) {
                            selectedItem.remove(el);
                            break;
                        }
                    }
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
        for (String[] el :
                selectedItem) {
            favorites.deleteFavor(el);
        }
        try {
            favorites.saveFavor("favor");
        } catch (IOException ex) {
           return;
        }
        //удаляем элементы из адаптера
        for (String[] el :
                selectedItem) {
            String del = el[0] + " - " + el[1];
            adapter.remove(del);
        }
        //уведомляем об изменении данных
        adapter.notifyDataSetChanged();

        //чистим список выбранных элементоы
        selectedItem.clear();
    }
}
