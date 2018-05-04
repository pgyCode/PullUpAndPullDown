package com.example.rtyui.androidteach.Test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rtyui on 2018/5/1.
 */

public class MySqliteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqLiteDatabase = null;

    public MySqliteHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mkTable(User.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
public void mkTable(Class c){
    Field[] fields = c.getFields();
    StringBuilder mktable = new StringBuilder("create table " +
            c.getSimpleName() +
            "(_id integer primary key autoincrement");
    for (Field field : fields){
        switch (field.getType().getName()){
            case "int":
                mktable.append(',' + field.getName() + " integer");
                break;
            case "java.lang.String":
                mktable.append(',' + field.getName() + " text");
                break;
        }
    }
    mktable.append(')');
    sqLiteDatabase.execSQL(mktable.toString());
}


    public void insert(Object object){
        Class c = object.getClass();
        Field[] fields = c.getFields();
        StringBuilder mktable = new StringBuilder("insert into " +
                c.getSimpleName());

        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        try{
            for (Field field : fields){
                switch (field.getType().getName()){
                    case "int":
                    case "java.lang.String":
                        name.append(field.getName() + ',');
                        value.append("'" + field.get(object) + "',");
                        break;
                }
            }
        }catch(IllegalAccessException e){
        }

        name.deleteCharAt(name.length() - 1);
        value.deleteCharAt(value.length() - 1);
        mktable.append("(" + name + ")").append(" values(" + value + ")");
        sqLiteDatabase.execSQL(mktable.toString());
    }

public List getAll(Class c){
    Cursor cursor = sqLiteDatabase.rawQuery("select * from " + c.getSimpleName(), null);
    return cursor2list(cursor, c);
}

    public void clear(Class c){
        sqLiteDatabase.execSQL("delete from " + c.getSimpleName());
    }

public void delete(Class c, String conditions){
    String string = "DELETE FROM " + c.getSimpleName() + " where ";
    string += conditions;
    System.out.println(string);
    sqLiteDatabase.execSQL(string);
}

public List query(Class c, String conditions){
    String string = "SELECT * from " + c.getSimpleName() + " where ";
    string += conditions;
    Cursor cursor = sqLiteDatabase.rawQuery(string, null);
    return cursor2list(cursor, c);
}

private List cursor2list(Cursor cursor, Class c){
    List<Object> list = new LinkedList<>();
    try {
        while (cursor.moveToNext()){
            Object o = c.newInstance();
            Field[] fields = c.getFields();
            for (int i = 1 ; i < cursor.getColumnCount(); i++){
                switch (fields[i - 1].getType().getName()){
                    case "int":
                        fields[i - 1].setInt(o, cursor.getInt(i));
                        break;
                    case "java.lang.String":
                        fields[i - 1].set(o, cursor.getString(i));
                        break;
                }
            }
            list.add(o);
            User user = (User) o;
            System.out.println(user.a + " " + user.username + " " + user.password);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

public void update(Class c, String results, String conditions){
    String string = "UPDATE " + c.getSimpleName() + " SET " + results +  " WHERE " + conditions;
    sqLiteDatabase.execSQL(string);
}
}
