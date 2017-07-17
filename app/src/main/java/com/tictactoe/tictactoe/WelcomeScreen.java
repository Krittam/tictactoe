package com.tictactoe.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blackhawk.blackhawk.NotificationInstanceIdService;
import com.blackhawk.blackhawk.NotificationService;

public class WelcomeScreen extends AppCompatActivity {
    public SharedPreferences sPref;
    public static String key_preferences = "key_preferences";
    public static String key_userName = "key_username";
    public static String key_userEmail = "key_useremail";
    EditText username;
    EditText useremail;
    Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        sPref = getSharedPreferences(key_preferences, Context.MODE_PRIVATE);
        if(sPref!=null){
            if(sPref.getString(key_userEmail,null)!=null &sPref.getString(key_userName,null)!=null ){
                goToGameScreen();
            }
            else{
             waitForUserInput();
            }
        }
    }
    public void waitForUserInput(){
        useremail = (EditText) findViewById(R.id.user_email);
        username = (EditText) findViewById(R.id.user_name);
        done = (Button) findViewById(R.id.set_user_details);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable email = useremail.getText();
                Editable name = username.getText();
                if (validateInput(name, email)){
                    saveDetailsToPreferences(name.toString(), email.toString());
                    goToGameScreen();
                }
                else{
                    Toast.makeText(WelcomeScreen.this, "Please enter valid inputs!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean validateInput(Editable name, Editable email){
        if (name==null |email==null){
            return false;
        }
        if (!email.toString().contains("@")|!email.toString().contains(".")){
            return false;
        }
        return true;
    }
    public void goToGameScreen(){
        Intent i = new Intent(WelcomeScreen.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void saveDetailsToPreferences(String username, String email){
        if(sPref!=null){
            SharedPreferences.Editor editor = sPref.edit();
            editor.putString (key_userEmail,email);
            editor.putString (key_userName,username);
            editor.commit();
            NotificationService.setUserDetails(this, email, "TicTacToe");
            NotificationInstanceIdService.sendTokenToServer(this);
        }
    }
}

