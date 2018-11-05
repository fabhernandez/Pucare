package com.example.a01363207.pucare;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a01363207.pucare.UserPackage.SignUp;
import com.example.a01363207.pucare.UserPackage.UserDP;
import com.example.a01363207.pucare.UserPackage.UserDatabaseController;

public class MainActivity extends AppCompatActivity {
    // for the signUp & PlantsView
    public static String EXTRA_INPUT_USER = "INPUT_USER";
    private String strUser = "";
    // for the logIn
    UserDatabaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = new UserDatabaseController(this.getBaseContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }

    /* activity_main: signUp = onClick */
    public void signUp(View view) {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
    }

    public int valstrUser(String u, String p) {
        int result = -2;
        Log.d("VALID_USER_METHOD", "Llegue___ USER: " + u + " PASS: " + p);

        // Ask DB look for user
        String[] selectionArgs = new String[]{u, p};
        // for requirement: Initialize DatabaseHelper, in this case calls the DBcontroller to do it
        String selection = UserDP.COLUMN_USERNAME + " =? AND " + UserDP.COLUMN_PASSWORD + " = ?";

        Cursor cursor = controller.selectUser(selection, selectionArgs);

        // get info from cursor

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                strUser = cursor.getString(cursor.getColumnIndex(UserDP.COLUMN_IDUSER));
                String name = cursor.getString(cursor.getColumnIndex(UserDP.COLUMN_USERNAME));
                String email = cursor.getString(cursor.getColumnIndex(UserDP.COLUMN_EMAIL));
                String pass = cursor.getString(cursor.getColumnIndex(UserDP.COLUMN_PASSWORD));

                Log.d("DATABASE_INFO", "USER_INFO____ID: " + strUser + " User: " + name + " Email: " + email + " Pass: " + pass);
            }
            result = 1;
        }

        cursor.close();
        return result;
    }
    /* activity_main: logIn = onClick */
    public void logIn(View view) {
        String message = "";
        EditText user = (EditText) findViewById(R.id.strUser);
        EditText pass = (EditText) findViewById(R.id.idPass);

        String u = user.getText().toString();
        String p = pass.getText().toString();

        Log.d("User", "User: " + u + " Pass: " + p);
        TextView textView = (TextView) findViewById(R.id.idTextMessages);

        // user exists?
        int result = valstrUser(u, p);

        // if user exist && data is correct?
        if (result == 1) {
            // move to next activity
            Intent intent = new Intent(MainActivity.this, PlantsView.class);
            intent.putExtra(EXTRA_INPUT_USER, strUser);

            startActivity(intent);
            Log.d("RECEPTION", "\nFOUND: USUARIO Y CONTRASENA CORRECTOS");
            message = "USUARIO Y CONTRASENA CORRECTOS";

        } else {
            // throw error, data do not match or user does not exists
            Log.d("RECEPTION", "\nEXCEPTION: DATA DO NOT MATCH");
            message = "DATA DO NOT MATCH";
        }
        textView.setText(message);
    }
}