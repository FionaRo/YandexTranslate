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


public class History implements Serializable {

    LinkedList<String[]> history = new LinkedList<>();

    //добавление элемента в историю
    public void addHistory(String[] s) {
        deleteHistory(s); //избегание повторов
        history.push(s); //если такой перевод уже существовал, то он в конец списка
    }

    //удаление эдемента из истории
    public void deleteHistory(String[] s) {
        for (String[] el :
                history) {
            if (el[0].equals(s[0]) && el[1].equals(s[1])) {
                history.remove(el);
                break;
            }
        }
    }

    //получение полной истории
    public LinkedList<String[]> getHistory() {
        return history;
    }

    //сериализация класса
    public void saveHistory(String path) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        //сериализуем в файл ./data/history
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() +
                File.separator + "data" + File.separator + path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        return;
    }

    public static History loadHistory(String path) throws IOException, ClassNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory() +
                    File.separator + "data" + File.separator + path);
            ObjectInputStream oin = new ObjectInputStream(fis);
            History history;
            history = (History) oin.readObject();
            return history;
        } catch (FileNotFoundException exception) {
            History history = new History(); //если файла нет - создаем новую историю
            history.saveHistory("history"); //сохраняем
            return history;
        }
    }

}
