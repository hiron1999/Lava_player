package com.example.hiron.media_player;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class plyardb extends SQLiteOpenHelper {
    private static final  String DB_NAME="mysong";
    private static  final int Db_VERTION=1;
    private static final  String TABLE_NAME="users";
    private static final  String FIRST_COL="name";
    private static final  String SECOEND_COL="Path";



    private static final String CREATE_QUERY="CREATE TABLE " + TABLE_NAME + " (" + FIRST_COL + " text,  " + SECOEND_COL + " text);" ;

    public plyardb(Context context){

        super(context,DB_NAME,null,Db_VERTION);
        Log.i("DB_MASSAGE","DB CREATE DONE");
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase ) {

        sqLiteDatabase.execSQL(CREATE_QUERY);
        Log.i("DB_MASSAGE","TABLE  created");


    }

    public void insertData(String name, String path,SQLiteDatabase sqLiteDatabase){

        ContentValues content=new ContentValues();
        content.put(FIRST_COL,name);
        content.put(SECOEND_COL,path);

        sqLiteDatabase.insert(TABLE_NAME,null,content);
        Log.i("DB_MASSAGE","1 row inserted");

    }

    public Cursor ViewData(SQLiteDatabase sqLiteDatabase){

        Cursor c=sqLiteDatabase.rawQuery(" SELECT * FROM " +TABLE_NAME ,null);
        return c;
    }

    public Cursor SearchDAta(SQLiteDatabase sqLiteDatabase,String name){
        Cursor c=sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + FIRST_COL+ " LIKE " +"'" +name +"'",null);
        //when pass a value in quary the syntax is("'" +name +"'");
        return c;
    }

   /* public  int UpdateData(String email,String name,String phone, SQLiteDatabase sqLiteDatabase){
        ContentValues content=new ContentValues();
        content.put(FIRST_COL,name);

        String  where="email==?";
        String[] whereArgs={String.valueOf(email)};
        int x=sqLiteDatabase.update(TABLE_NAME,content,where,whereArgs);
        return x;

    }*/

    public int DELEET(String name,SQLiteDatabase sqLiteDatabase){
        String  where="name==?";
        String[] whereArgs={String.valueOf(name)};
        int x=sqLiteDatabase.delete(TABLE_NAME,where,whereArgs);
        return x;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}


