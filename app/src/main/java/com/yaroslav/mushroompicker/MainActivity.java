package com.yaroslav.mushroompicker;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static com.yaroslav.mushroompicker.ProjectConstants.*;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {


    EditText edt_txt_pointname;
    EditText edt_txt_aboutpoint;
    EditText edt_txt_search;
    ListView listView;

    String location_str = "";
    String name;
    String about_point;
    String photo = null;
    String filter = "";
    Thread thread;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_SHOW_ID = 2;
    private static final int CM_EDIT_ID = 3;
    private static final int CM_ADD_ID = 4;

    DataBase db;
    SimpleCursorAdapter cursorAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       final Context mainContext = this;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
               new MyLocationListener ().SetUpLocationListener(mainContext);
            }
        };
         thread = new Thread(runnable);
        //так запускается
         thread.run();

        edt_txt_pointname = (EditText) findViewById(R.id.edt_txt_pointname);
        edt_txt_aboutpoint = (EditText) findViewById(R.id.edt_txt_aboutpoint);
        edt_txt_search = (EditText)findViewById(R.id.edt_txt_search);



        // открываем подключение к БД
        db = new DataBase(this);
        db.open();


        // формируем столбцы сопоставления
       String [] from = new String[] { DataBase.COLUMN_NAME, DataBase.COLUMN_ABOUT, DataBase.COLUMN_LOCATION };
        int [] to = new int[]{ R.id.txt_pointname, R.id.txt_aboutpoint, R.id.txt_gps_koord};

        // создааем адаптер и настраиваем список

        cursorAdapter = new SimpleCursorAdapter(this,R.layout.razmetka_spiska,null,from,to,0) ;
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(cursorAdapter);

        // создаем лоадер для чтения данных
        getLoaderManager().initLoader(0, null, MainActivity.this);

        // создаем поиск
        edt_txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filter = s.toString();

                getLoaderManager().restartLoader(0, null, MainActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // добавляем контекстное меню к списку
        registerForContextMenu(listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filter = "";


                Intent intent = new Intent(MainActivity.this, ShowActivity.class);

                String [] message ;

                getLoaderManager().restartLoader(0, null, MainActivity.this);

                message = db.getPoint(id);

                intent.putExtra(INTENT_KEY_NAME,  message [1]);
                intent.putExtra(INTENT_KEY_ABOUTPOINT,message [2]);
                intent.putExtra(INTENT_KEY_LOCATION,message [3]);
                intent.putExtra(INTENT_KEY_PHOTO,message[4]);

                edt_txt_search.setText("");

                startActivity(intent);


            }
        });

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0, CM_SHOW_ID, 0, R.string.swow_record);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_ADD_ID, 0, R.string.add_record);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // получаем из пункта контекстного меню данные по пункту списка
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
                .getMenuInfo();

        Intent intent_input = new Intent(MainActivity.this, InputActivity.class);

        String [] message ;

        String name ;
        String about;
        String latitude;
        String longitude;
        String location;
        String photo;

        switch (item.getItemId()){
            case CM_DELETE_ID:

                // извлекаем id записи и удаляем соответствующую запись в БД
                db.delRec(acmi.id);
                getLoaderManager().getLoader(0).forceLoad();
                break;

            case CM_SHOW_ID:

                //получаем строковый массив для передачи на активити карты
                message = db.getPoint(acmi.id);
                name = message [1];
                about = message [2];

                latitude = message[3].split(" ")[0];
                longitude = message [3].split(" ")[1];

                photo = message[4];

                //передаем данные на активити карты
                //говорим куда передать
                Intent intent = new Intent(this, Map.class);
                //указываем пары ключ - значение
                intent.putExtra(INTENT_KEY_NAME,name);
                intent.putExtra(INTENT_KEY_ABOUTPOINT, about);
                intent.putExtra(INTENT_KEY_LATITUDE,latitude);
                intent.putExtra(INTENT_KEY_LONGITUDE,longitude);
                intent.putExtra(INTENT_KEY_PHOTO,photo);
                //стартуем карту
                startActivity(intent);
                break;
            case CM_EDIT_ID:
                //получаем строковый массив для передачи на активити карты
                message = db.getPoint(acmi.id);
                String id = String.valueOf(acmi.id);
                name = message [1];
                about = message [2];
                location = message [3];
                photo = message[4];
                intent_input.putExtra(INTENT_KEY_NAME, name);
                intent_input.putExtra(INTENT_KEY_ABOUTPOINT,about);
                intent_input.putExtra(INTENT_KEY_LOCATION,location);
                intent_input.putExtra(INTENT_KEY_PHOTO,photo);
                intent_input.putExtra(INTENT_KEY_MODE,"edit");
                intent_input.putExtra(INTENT_KEY_ID, id);

                startActivityForResult(intent_input, INPUT_RESULT);

                break;
            case CM_ADD_ID:
                intent_input.putExtra(INTENT_KEY_MODE,"add");
                startActivityForResult(intent_input,INPUT_RESULT);

                break;
            }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent dataIntent){
        super.onActivityResult(requestCode, resultCode, dataIntent);


        if (requestCode == INPUT_RESULT){
            if (resultCode == RESULT_OK){
                name = dataIntent.getStringExtra(INTENT_KEY_NAME);
                about_point = dataIntent.getStringExtra(INTENT_KEY_ABOUTPOINT);
                location_str = dataIntent.getStringExtra(INTENT_KEY_LOCATION);
                String mode = dataIntent.getStringExtra(INTENT_KEY_MODE);
                photo = dataIntent.getStringExtra(INTENT_KEY_PHOTO);


                if (mode.contains("add")) db.addRec(name,about_point,location_str,photo);
                else {
                    String id = dataIntent.getStringExtra(INTENT_KEY_ID);
                    db.editRec(Integer.parseInt(id),name,about_point,location_str,photo);
                }


            }
        }
        // получаем новый курсор с данными
        getLoaderManager().getLoader(0).forceLoad();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
        thread.stop();


    }


   public void SaveBtn_onClick(View view) {
       Intent intent = new Intent(MainActivity.this,InputActivity.class);
       intent.putExtra(INTENT_KEY_MODE,"add");
       startActivityForResult(intent,INPUT_RESULT);

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db, filter);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    static class MyCursorLoader extends CursorLoader {

        DataBase db;
        String filter;

        public MyCursorLoader(Context context, DataBase db, String filter) {
            super(context);
            this.db = db;
            this.filter = filter;
        }

        @Override
        public Cursor loadInBackground() {
            if (filter != "") {
                return db.searchByInputText(filter);
            } else {

                return db.getAllData();
                }
        }


       

    }




}

