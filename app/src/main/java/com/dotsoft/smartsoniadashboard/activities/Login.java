package com.dotsoft.smartsoniadashboard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.cms.UserCMS;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Login extends AppCompatActivity {
    private static final String FILE_USER = "user.json";
    private static String TAG = "LOGINPAGE";
    private RelativeLayout messageSection,mainLayout;
    private ProgressBar messageProgressBar;
    private TextView messageText,messageAction;
    private Handler timer;
    private Runnable runnable;
    private Animation messageAnimationReveal, messageAnimationGone;
    private TextInputEditText username,password;
    private TextInputLayout usernameLayout, passwordLayout;
    private String usernameValue,passwordValue;
    private Boolean onCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // change Navigation bar color
        getWindow().setNavigationBarColor(getResources().getColor(R.color.blue));

        File file = getApplicationContext().getFileStreamPath(FILE_USER);
        if(file != null && file.exists()) {
            String userRaw = loadTextFromFile(FILE_USER);
            User user = new Gson().fromJson(userRaw,User.class);
            DataHolder.getInstance().setSelectedUser(user);
            if(user.isLogin()){
                goToDashBoard();
                finish();
            }
        }

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        //set message
        messageSection = (RelativeLayout) findViewById(R.id.message_section);
        messageProgressBar = (ProgressBar) findViewById(R.id.message_progress);
        messageText = (TextView) findViewById(R.id.message_text);
        messageAction = (TextView) findViewById(R.id.message_action);

        messageSection.setVisibility(View.GONE);
        messageProgressBar.setVisibility(View.GONE);
        messageAction.setVisibility(View.GONE);

        //set animations and timer
        messageAnimationReveal = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_once);
        messageAnimationGone = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout_once);
        timer = new Handler();

        //set input fields
        username = (TextInputEditText) findViewById(R.id.username_text);
        password = (TextInputEditText) findViewById(R.id.password_text);
        usernameLayout = (TextInputLayout) findViewById(R.id.username_section);
        passwordLayout = (TextInputLayout) findViewById(R.id.password_section);

        onCall = false;
        //set login button
        TextView loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(!onCall){
                    hideKeyboard();
                    if(checkNetwork()){
                        usernameValue = String.valueOf(username.getText());
                        passwordValue = String.valueOf(password.getText());
                        usernameLayout.setErrorEnabled(false);
                        passwordLayout.setErrorEnabled(false);
                        password.setBackground(getDrawable(R.drawable.white_round_button));
                        username.setBackground(getDrawable(R.drawable.white_round_button));

                        // If a field is empty
                        if (usernameValue.equals("")){
                            usernameLayout.setErrorEnabled(true);
                            usernameLayout.setError(getString(R.string.empty_field_username));
                            username.setBackground(getDrawable(R.drawable.white_round_button_red_stroke));
                        } else if (usernameValue.contains(" ")) {
                            usernameLayout.setErrorEnabled(true);
                            usernameLayout.setError(getString(R.string.space_on_field_username));
                            username.setBackground(getDrawable(R.drawable.white_round_button_red_stroke));
                        }else if (passwordValue.equals("")) {
                            passwordLayout.setErrorEnabled(true);
                            passwordLayout.setError(getString(R.string.empty_field_password));
                            password.setBackground(getDrawable(R.drawable.white_round_button_red_stroke));
                        }else {
                            //show message and do login
                            showWaitMessage();
                            onCall = true;
                            loginAction();
                        }
                    }
                }
            }
        });
    }

    private void loginAction(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String loginEndpoint = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/login/?username="+usernameValue+"&password="+passwordValue+"&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&scope=public,core,posts,taxonomies,comments,profiles,publish_posts,publish_comments,edit_profile,manage_posts";
        Request request = new Request.Builder()
                .url(loginEndpoint)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                showUnSuccessLogin();
                onCall = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    //Log.e("RESPONSE - onResponse", "register call isSuccessful" + myResponse);
                    UserCMS.Root userResponse = new Gson().fromJson(myResponse, UserCMS.Root.class);
                    if(userResponse.respond.equals("1")){
                        if(userResponse.result.get(0).custom_fields.user_role.equals("OSH MANAGER")){
                            hideMessage();
                            clearInputField();
                            // Build user json
                            JsonObject jsonUser = new JsonObject();
                            jsonUser.addProperty("userName",userResponse.result.get(0).nickname);
                            jsonUser.addProperty("userId",userResponse.result.get(0).ID);
                            jsonUser.addProperty("accessToken",userResponse.result.get(0).Access_Token);
                            jsonUser.addProperty("login","IN");
                            jsonUser.addProperty("role",userResponse.result.get(0).custom_fields.user_role);
                            String userString = jsonUser.toString();
                            saveUser(userString);
                            User user = new Gson().fromJson(userString,User.class);
                            DataHolder.getInstance().setSelectedUser(user);

                            goToDashBoard();
                            finish();
                        }else {
                            showInvalidUser();
                        }
                    }else{
                        showInvalidUser();
                    }
                }else{
                    //Log.e("RESPONSE - onResponse", "register call is NOT Successful" + myResponse);
                    showUnSuccessLogin();
                }
                //pointer to activate the button again
                onCall = false;
            }
        });
    }

    private void hideKeyboard(){
        try  {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean checkNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.message_network));
                        messageText.setVisibility(View.VISIBLE);
                        messageProgressBar.setVisibility(View.GONE);
                        messageAction.setVisibility(View.GONE);
                        messageSection.startAnimation(messageAnimationReveal);
                        messageSection.setVisibility(View.VISIBLE);

                        // hide after 4 sec
                        timer.removeCallbacks(runnable);
                        timer.postDelayed( runnable = () -> {
                            messageSection.startAnimation(messageAnimationGone);
                            messageSection.setVisibility(View.GONE);
                        }, 4000);
                    }
                }, 100);
        return false;
    }

    // login message
    private void showWaitMessage(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.message_wait));
                        messageText.setVisibility(View.VISIBLE);
                        messageProgressBar.setVisibility(View.VISIBLE);
                        messageAction.setVisibility(View.GONE);
                        messageSection.setVisibility(View.VISIBLE);
                        messageSection.startAnimation(messageAnimationReveal);
                    }
                }, 100);
    }

    private void showUnSuccessLogin(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.error));
                        messageText.setVisibility(View.VISIBLE);
                        messageProgressBar.setVisibility(View.GONE);
                        messageAction.setVisibility(View.GONE);
                        messageSection.startAnimation(messageAnimationReveal);
                        messageSection.setVisibility(View.VISIBLE);

                        // hide after 4 sec
                        timer.removeCallbacks(runnable);
                        timer.postDelayed( runnable = () -> {
                            messageSection.startAnimation(messageAnimationGone);
                            messageSection.setVisibility(View.GONE);
                        }, 4000);
                    }
                }, 100);
    }

    private void showInvalidUser(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.invalid_user));
                        messageText.setVisibility(View.VISIBLE);
                        messageProgressBar.setVisibility(View.GONE);
                        messageAction.setVisibility(View.GONE);
                        messageSection.startAnimation(messageAnimationReveal);
                        messageSection.setVisibility(View.VISIBLE);

                        // hide after 4 sec
                        timer.removeCallbacks(runnable);
                        timer.postDelayed( runnable = () -> {
                            messageSection.startAnimation(messageAnimationGone);
                            messageSection.setVisibility(View.GONE);
                        }, 4000);
                    }
                }, 100);
    }

    private void hideMessage(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                            messageSection.startAnimation(messageAnimationGone);
                            messageSection.setVisibility(View.GONE);
                    }
                }, 100);
    }

    private void clearInputField(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        username.setText("");
                        password.setText("");
                    }
                }, 100);
    }

    private void goToDashBoard(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    private void saveUser(String jsonString){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(FILE_USER, Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonString);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadTextFromFile(String filename) {
        String localeString = "none";
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;
            while ((text = br.readLine()) != null) {
                localeString = text;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localeString;
    }


}

