package com.yaroslav.mushroompicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.yaroslav.mushroompicker.ProjectConstants.*;



public class InputActivity extends Activity{

    String location_str = "";
    String location_str_edit="";
    String mode;
    String id;
    String photo = null;
    final int REQUEST_CODE_PHOTO = 1;
    EditText edt_txt_pointname;
    EditText edt_txt_aboutpoint;
    TextView txt_location;
    ImageView imageView;
    ImageButton btn_camera;
    ImageButton btn_gallery;
    Location location = null;
    Button btn_save;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);



        mode = getIntent().getStringExtra(INTENT_KEY_MODE);
        id = getIntent().getStringExtra(INTENT_KEY_ID);
        photo = getIntent().getStringExtra(INTENT_KEY_PHOTO);


        imageView = (ImageView) findViewById(R.id.imageView);

        txt_location = (TextView) findViewById(R.id.txt_gps_status);
        edt_txt_pointname = (EditText) findViewById(R.id.edt_txt_pointname);
        edt_txt_aboutpoint = (EditText) findViewById(R.id.edt_txt_aboutpoint);
        btn_camera = (ImageButton) findViewById(R.id.btn_take_photo);
        btn_gallery = (ImageButton) findViewById(R.id.dtn_chose_gall);
        btn_save = (Button) findViewById(R.id.btn_save_point);

        if (mode.contains("edit")) {
            edt_txt_pointname.setText(getIntent().getStringExtra(INTENT_KEY_NAME));
            edt_txt_aboutpoint.setText(getIntent().getStringExtra(INTENT_KEY_ABOUTPOINT));
            location_str_edit = getIntent().getStringExtra(INTENT_KEY_LOCATION);
            txt_location.setText("Координаты" + location_str_edit);
            if (photo!=null)
            imageView.setImageURI(Uri.parse(photo));
        }
        else if (mode.contains("add")){
            edt_txt_pointname.setText("");
            edt_txt_aboutpoint.setText("");
            btn_save.setEnabled(false);
        }


        MyLocationListener.setHandler(handler);

    }

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case (GPS_LOCATION_CHANGED):
                    location = (Location) msg.obj;
                    location_str = String.valueOf(MyLocationListener.imHere.getLatitude())+" "
                            + String.valueOf(MyLocationListener.imHere.getLongitude());
                    txt_location.setText("Координаты" + location_str);
                    if (msg.obj==null)
                        btn_save.setEnabled(false);
                    else btn_save.setEnabled(true);


                    break;
                case (GPS_TURNED_OF):
                    Toast.makeText(getBaseContext(), "GPS turned of", Toast.LENGTH_LONG).show();
                    btn_save.setEnabled(false);
                    break;
                case (GPS_TURNED_ON):
                    Toast.makeText(getBaseContext(), "GPS turned on", Toast.LENGTH_LONG).show();
                    break;



            }
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            Uri selectedImage = intent.getData();

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            photo = cursor.getString(cursor.getColumnIndex(filePath[0]));
            imageView.setImageURI(Uri.parse(photo));
            cursor.close();
        }
    }
    //обработчик нажатия на кнопки и  на рисунок
    public void onClickPhoto(View view) {

        Intent intent;

        switch (view.getId()) {
            case (R.id.imageView):
            case (R.id.dtn_chose_gall):
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,REQUEST_CODE_PHOTO);
                break;
            case (R.id.btn_take_photo):
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CODE_PHOTO);

        }

    }
    // нажатие на кнопку сохранить
    public void onClick_Save_Btn (View view){
        String pointname = edt_txt_pointname.getText().toString();
        String aboutpoint = edt_txt_aboutpoint.getText().toString();


        if (pointname.equals("")) {
            Toast.makeText(getBaseContext(), R.string.input_text, Toast.LENGTH_LONG).show();
        }
        else if (aboutpoint.equals("")){
            Toast.makeText(getBaseContext(), R.string.input_text, Toast.LENGTH_LONG).show();
        }
        else {


            Intent intent = new Intent(InputActivity.this, MainActivity.class);
            intent.putExtra(INTENT_KEY_NAME, pointname);
            intent.putExtra(INTENT_KEY_ABOUTPOINT, aboutpoint);
            intent.putExtra(INTENT_KEY_MODE, mode);
            intent.putExtra(INTENT_KEY_PHOTO, photo);

            if (mode.contains("edit")) {

                intent.putExtra(INTENT_KEY_LOCATION, location_str_edit);
                intent.putExtra(INTENT_KEY_ID, id);
                setResult(RESULT_OK, intent);
                finish();}
            else if (mode.contains("add")) {
                if (MyLocationListener.imHere == null)
                    Toast.makeText(getBaseContext(), R.string.txt_gps_status, Toast.LENGTH_LONG).show();
                else {
                    location_str = String.valueOf(MyLocationListener.imHere.getLatitude())+" "
                            + String.valueOf(MyLocationListener.imHere.getLongitude());
                    intent.putExtra(INTENT_KEY_LOCATION, location_str);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }


    }








}
