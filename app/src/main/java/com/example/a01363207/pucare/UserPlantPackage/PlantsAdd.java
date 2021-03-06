package com.example.a01363207.pucare.UserPlantPackage;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a01363207.pucare.GetImageFromURL;
import com.example.a01363207.pucare.MainActivity;
import com.example.a01363207.pucare.PlantPackage.PlantDP;
import com.example.a01363207.pucare.PlantPackage.PlantDatabaseController;
import com.example.a01363207.pucare.PlantsView;
import com.example.a01363207.pucare.R;

import java.util.ArrayList;
/*
PlantsAdd Class  -> Adds a new plant to an user
	~ plants_add.xml
		Show editable fields to create a new plants user in DB
*/
public class PlantsAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static String CHANNEL_ID = "Notification Chanel A";

    public static String EXTRA_INPUT_USER = "INPUT_USER";
    private static final String TAG = "PlantsAdd";

    UserPlantDatabaseController userPlantController;
    PlantDatabaseController plantController;

    private String userName = "";
    private String spValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get userName */
        userName = (getIntent().getStringExtra(MainActivity.EXTRA_INPUT_USER)).toString();
        //Log.d(TAG, "\n\n<----> Received:" + userName);

        plantController = new PlantDatabaseController(this.getBaseContext());
        userPlantController = new UserPlantDatabaseController(this.getBaseContext());

        // set layout
        setContentView(R.layout.plants_new);

        // load plants options
        ArrayList<String> list;
        list = getPlantsOptions();
        //Log.d(TAG, "getPlantsOptions. list: "+list.toString());

        // display spinner options
        Spinner spinner = (Spinner) findViewById(R.id.idSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_inner_layout, R.id.sData, list);
        spinner.setAdapter(adapter);
        // Log.d(TAG, "getPlantsOptions. Plants name were set");
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        plantController.close();
        userPlantController.close();
    }

    // gets the plants name from DB
    private ArrayList<String> getPlantsOptions() {
        ArrayList<String> list = new ArrayList<String>();
        //Log.d(TAG, "getPlantsOptions. after array creation");

        Cursor cursor = plantController.selectPlantsName();
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(PlantDP.COLUMN_PLANT_NAME));
                    list.add(name);
                    //Log.d(TAG, "getPlantsOptions; name: " + name);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception. getPlantsOptions; exception: " + e.getMessage());
        }

        cursor.close();
        return list;
    }

    // insert a plant to an user
    public void addUserPlant(View view) {
        // get user info
        EditText n  = (EditText)findViewById(R.id.idNewName);
        Spinner s   = (Spinner)findViewById(R.id.idSpinner);

        String plantName = n.getText().toString();
        String plantType = s.getSelectedItem().toString();

        //Log.d(TAG,"addUserPlant. plantName: "+plantName+"    ___plantType: "+plantType);

        if(plantName.isEmpty()){
            Toast.makeText(this,"Your plant must have a name",Toast.LENGTH_SHORT).show();
        }else{
            // add plant to the user
            OperationsHandler oh = new OperationsHandler();
            String lW = oh.getDate();
           // Log.d(TAG,"getDate: "+lW);
            String wW = getWater(plantType);
            String nW = oh.nextWater(lW, wW);
            String img = getPlantsImage(plantType);

            // because this is a new plant the countdown starts from now, from last water
            UserPlantDP input = new UserPlantDP(plantName,"Healthy",lW ,nW, img, userName, plantType, lW);

            long inserted = userPlantController.insert(input);
            Log.d(TAG, "addUserPlant. inserted " + inserted);
            Log.d(TAG, "addUserPlant. DATA--->>> plantName: " + plantName+" Health: Healthy Last: "+lW+" next: "+nW+" image: "+img+" userName: "+userName+" plantType: "+plantType+" date: "+lW);

            Toast.makeText(this,"Now, let's take a good care of "+plantName,Toast.LENGTH_LONG).show();
        }
        // load catalogue

        //Log.d(TAG, "<---- addUserPlant. inserted ");
        //setContentView(R.layout.plants_view);
        //setContentView(R.layout.plants_view);
        //getUserPlants();

        doNotification("New Plant Added", plantName, "plantName: "+": "+plantName);

        Intent intent = new Intent(this, PlantsView.class);
        intent.putExtra(EXTRA_INPUT_USER, userName);

        startActivity(intent);
        //Log.d(TAG, "Plant added. Display Catalogue");
    }

    // request when to water a plant to the DB
    public String getWater(String plantName) {
        String water = "";

        Log.d(TAG, "IN getWater method ");

        Cursor cursor = plantController.selectPlantsWater(plantName);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    water = cursor.getString(cursor.getColumnIndex(PlantDP.COLUMN_WATER));
                    //Log.d(TAG, "getWater; water: " + water);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception. getWater; exception: " + e.getMessage());
        }

        cursor.close();
        return water;
    }

    // gets the plants image from DB
    private String getPlantsImage(String plantName) {
        String link = "";

        //Log.d(TAG, "getPlantsImage method ");

        Cursor cursor = plantController.selectPlantsImage(plantName);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    link = cursor.getString(cursor.getColumnIndex(PlantDP.COLUMN_IMAGE));
                    //Log.d(TAG, "getPlantsImage; link: " + link);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception. getPlantsImage; exception: " + e.getMessage());
        }

        cursor.close();
        return link;
    }

    // Required by the implement, gets the value selected by user
    // parent   : AdapterView where the selection happened
    // view     : View within the AdapterView that was clicked
    // pos      : The position of the view in the adapter
    // id       : The row id of the item that is selected
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        spValue = (String) parent.getItemAtPosition(pos).toString();
        Log.d(TAG, "onItemSelected. value: " + spValue);
        // Change image icon
        // get image url

        String link = getPlantsImage(spValue);
        ImageView icon = (ImageView) findViewById(R.id.imageView);

        if (link.isEmpty()) {
            Log.d(TAG, "onItemSelected. an image for " + spValue + " was not found");
        }   // execute param is the url of the image
        else new GetImageFromURL(icon).execute(link);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback, or do nothing.
        // maybe that what the user wants
    }

    /** Notification **/
    void doNotification(String title, String text, String bigText){

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, mBuilder.build());
    }
}
