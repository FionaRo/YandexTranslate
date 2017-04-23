package ru.yandex.translate.Objects;

import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;


public class Favorites implements Serializable {

    LinkedList<String[]> favorites = new LinkedList<>();

    //добаление элемента в избранное
    public void addFavor(String[] favor) {
        deleteFavor(favor); //исключение хранения одинаковых элементов в избранном
        favorites.push(favor);
    }

    //удаление элемента из избранного
    public void deleteFavor(String[] favor) {
        for (String[] el :
                favorites) {
            if (el[0].equals(favor[0]) && el[1].equals(favor[1])) {
                favorites.remove(el);
                break;
            }
        }
    }

    //получение списка избранного
    public LinkedList<String[]> getFavorites() {
        return favorites;
    }

    //сереализация класса
    public void saveFavor(String path) throws IOException {
        //папка ./data/
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "data");
        //если директории не существует (удалена или запущено в первый раз) - создать
        if (!folder.exists()) {
            folder.mkdir();
        }
        //сереализуем класс в ./data/favor
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() +
                File.separator + "data" + File.separator + path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        return;
    }

    //десериализация класса, принимает имя файла для десериализации и возвращает десериализованный класс
    public static Favorites loadFavor(String path) throws IOException, ClassNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory() +
                    File.separator + "data" + File.separator + path);
            ObjectInputStream oin = new ObjectInputStream(fis);
            Favorites favorites;
            favorites = (Favorites) oin.readObject();
            return favorites;
        } catch (FileNotFoundException exception) { //если файла не существует - удален или приложение запущено в первые
            Favorites favorites = new Favorites(); //создаем новый класс
            favorites.saveFavor("favor"); //сохраняем
            return favorites;
        }
    }
}
