package com.yaroslav.mushroompicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.yaroslav.mushroompicker.ProjectConstants.*;


public class ShowActivity extends Activity {

    String photo = null;
    TextView txt_pointname;
    TextView txt_aboutpoint;
    TextView txt_gps_koord;
    ImageView img_photo;
    Button btn_showpoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        photo = getIntent().getStringExtra(INTENT_KEY_PHOTO);
        img_photo = (ImageView) findViewById(R.id.img_photo);
        txt_pointname = (TextView) findViewById(R.id.txt_pointname);
        txt_aboutpoint = (TextView) findViewById(R.id.txt_aboutpoint);
        txt_gps_koord = (TextView) findViewById(R.id.txt_gps_koord);
        btn_showpoint = (Button) findViewById(R.id.btn_showpoint);

        txt_pointname.setText(getIntent().getStringExtra(INTENT_KEY_NAME));
        txt_aboutpoint.setText(getIntent().getStringExtra(INTENT_KEY_ABOUTPOINT));
        txt_gps_koord.setText(getIntent().getStringExtra(INTENT_KEY_LOCATION));
        if (photo!=null)
            img_photo.setImageURI(Uri.parse(photo));
    }

    public void onBtnShowClick (View view){

        String latitude = getIntent().getStringExtra(INTENT_KEY_LOCATION).split(" ")[0];
        String longitude = getIntent().getStringExtra(INTENT_KEY_LOCATION).split(" ")[1];

        Intent intent = new Intent(this, Map.class);
        //указываем пары ключ - значение
        intent.putExtra(INTENT_KEY_NAME,getIntent().getStringExtra(INTENT_KEY_NAME));
        intent.putExtra(INTENT_KEY_ABOUTPOINT, getIntent().getStringExtra(INTENT_KEY_NAME));
        intent.putExtra(INTENT_KEY_LATITUDE,latitude);
        intent.putExtra(INTENT_KEY_LONGITUDE,longitude);
        intent.putExtra(INTENT_KEY_PHOTO, photo);


        //стартуем карту
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
