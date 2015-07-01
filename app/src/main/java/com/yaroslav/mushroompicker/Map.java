package com.yaroslav.mushroompicker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import static com.yaroslav.mushroompicker.ProjectConstants.*;


public class Map extends FragmentActivity implements OnMapReadyCallback{

    MapFragment mapFragment;
   // GoogleMap map;
    //ImageView img_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

       mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);

        //img_photo = (ImageView) findViewById(R.id.img_photo);
        //img_photo.setImageURI(Uri.parse(getIntent().getStringExtra("photo")));



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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

    @Override
    public void onMapReady(GoogleMap map) {
        //устанавливаем тип карты
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //активируем все жесты на карте
        map.getUiSettings().setAllGesturesEnabled(true);

        //активируем определение нашего местоположения и кнопку отображения его на карте
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);


        map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(getIntent().getStringExtra(INTENT_KEY_LATITUDE)),
                Double.parseDouble(getIntent().getStringExtra(INTENT_KEY_LONGITUDE))))
                .title(getIntent().getStringExtra(INTENT_KEY_NAME))
                .snippet(getIntent().getStringExtra(INTENT_KEY_ABOUTPOINT)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(getIntent().getStringExtra(INTENT_KEY_LATITUDE)),
                Double.parseDouble(getIntent().getStringExtra(INTENT_KEY_LONGITUDE))), 17.0f));


    }
}
