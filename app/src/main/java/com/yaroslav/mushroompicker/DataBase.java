package com.yaroslav.mushroompicker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase {

    private static final String DB_NAME = "Points";
    private static final int DB_VERSION = 3;
    private static final String DB_TABLE = "Points_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_ABOUT = "About";
    public static final String COLUMN_LOCATION = "Location";
    public static final String COLUMN_PHOTO = "Photo";

    private static final String DB_CREATE =
            "CREATE TABLE " + DB_TABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_ABOUT + " TEXT," +
                    COLUMN_LOCATION + " TEXT," +
                    COLUMN_PHOTO + " TEXT" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DataBase(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        Cursor cursor = mDB.query(DB_TABLE, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //фильтруем базу для поиска
    public Cursor searchByInputText(String inputText) {

        String selections = COLUMN_NAME + " LIKE ?";
        String [] selectionsArgs = new String[] {"%" + inputText + "%"};


        Cursor cursor = mDB.query(true,DB_TABLE, null , selections ,selectionsArgs ,null, null, null,null );

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    //получить данные об одной точке в виде массива строк {id,name,about,location}
    public String [] getPoint (long id){
        Cursor cursor = mDB.query(DB_TABLE,new String[]{COLUMN_ID,COLUMN_NAME,COLUMN_ABOUT,COLUMN_LOCATION,COLUMN_PHOTO},COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        String [] point = new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3),cursor.getString(4)};
        cursor.close();
        return point;
    }

    // добавить запись в DB_TABLE
    public void addRec(String name, String about, String location, String photo) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_ABOUT, about);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_PHOTO, String.valueOf(photo));
        mDB.insert(DB_TABLE, null, cv);
    }
    // редактировать запись в DB_TABLE
    public void editRec(long id,String name, String about, String location, String photo) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_ABOUT, about);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_PHOTO, photo);
        mDB.update(DB_TABLE, cv, "_id=?", new String[]{String.valueOf(id)});
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    //фильтруем базу для поиска


    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
          //  ContentValues cv = new ContentValues();



            }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (isTableExist(db,DB_TABLE))
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

            onCreate(db);
        }

        public boolean isTableExist(SQLiteDatabase db, String tableName){
            Cursor cursor = db.rawQuery("Select distinct tbl_name from sqlite_master where tbl_name = '" +
            tableName + "';", null);
            if (cursor != null){
                if (cursor.getCount() > 0){
                    cursor.close();
                    return true;
                }
                cursor.close();
            }
            return false;
        }
    }


    }
