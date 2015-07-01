package com.yaroslav.mushroompicker;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import static com.yaroslav.mushroompicker.ProjectConstants.*;


public class MyLocationListener implements LocationListener {

    // здесь будет всегда доступна самая последняя информация о местоположении пользователя
    static Location imHere = null;
    static Handler handler = null;
    Message msg ;


    public static void SetUpLocationListener(Context context)
    {

        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                10,
                locationListener);

        imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public static void setHandler(Handler hnd) {
        handler = hnd ;
    }

    @Override
    public void onLocationChanged(Location loc) {
        imHere = loc;
        if (handler!=null) {
            //создаем сообщение
            msg = handler.obtainMessage(GPS_LOCATION_CHANGED, imHere);
            //отправляем сообщение
            handler.sendMessage(msg);
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
        if (handler!=null)handler.sendEmptyMessage(GPS_TURNED_OF);

    }
    @Override
    public void onProviderEnabled(String provider) {
        if (handler!=null)handler.sendEmptyMessage(GPS_TURNED_ON);

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
