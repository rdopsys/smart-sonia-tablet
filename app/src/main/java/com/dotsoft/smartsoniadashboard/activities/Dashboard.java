package com.dotsoft.smartsoniadashboard.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.fragments.Accidents;
import com.dotsoft.smartsoniadashboard.fragments.Alerts;
import com.dotsoft.smartsoniadashboard.fragments.Beacons;
import com.dotsoft.smartsoniadashboard.fragments.History;
import com.dotsoft.smartsoniadashboard.fragments.Recommendations;
import com.dotsoft.smartsoniadashboard.fragments.SendMessages;
import com.dotsoft.smartsoniadashboard.fragments.Sos;
import com.dotsoft.smartsoniadashboard.fragments.WorkerBoard;
import com.dotsoft.smartsoniadashboard.fragments.WorkerData;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.cms.SosCMS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Dashboard extends AppCompatActivity {
    public static final String CHANNEL_ID = "SmartSoniaNotificationChannel";
    private static final String FILE_USER = "user.json";
    private final int RECEIVE_SOS_RESULTS_DB_DASHBOARD_EVERY = 1000 * 15; // 15 sec
    private Alerts alertsFragment = new Alerts();
    private Beacons beaconsFragment = new Beacons();
    private History historyFragment = new History();
    private Recommendations recommendationsFragment = new Recommendations();
    private Accidents accidentsFragment = new Accidents();
    private SendMessages sendMessagesFragment = new SendMessages();
    private Sos sosFragment = new Sos();
    private WorkerBoard workerBoardFragment = new WorkerBoard();
    private WorkerData workerDataFragment = new WorkerData();
    private ImageView navWorkerBoard, navBeacons, navWorkerData, navAlerts, navSendAlert, navSos,
            navHistory, navLogout, navRecommendation, navAccident, previousDay, nextDay, closeLogoutSection;
    private int openFragment = 0;
    private Animation messageAnimationReveal, messageAnimationGone;
    private Handler timer;
    private Runnable runnable;
    private RelativeLayout messageSection, calendarButton, calendarSection, confirmLogout;
    private ProgressBar messageProgressBar;
    private TextView messageText,messageAction, todayText, dayText, logoutNo, logoutYes;
    private Instant displayedDate, yesterday, today;
    private String activeDay;
    private String currentDay;
    private ScrollView navigationBar, confirmLogoutInner;
    private boolean isDialogOpen = false;
    private boolean logoutDialogOpen = false;
    private boolean onCall = false;
    private boolean okToShow = true;
    private Runnable r;
    private final Handler handler = new Handler();
    private int sosResults;
    private boolean unreadSosNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        okToShow = true;
        // set username
        String username = DataHolder.getInstance().getSelectedUser().getUserName();
        TextView tvUsername = (TextView) findViewById(R.id.username_text);
        tvUsername.setText(username);

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

        //set sidebar navigation
        navWorkerBoard = (ImageView) findViewById(R.id.nav_worker_board);
        navBeacons = (ImageView) findViewById(R.id.nav_beacons);
        navWorkerData = (ImageView) findViewById(R.id.nav_worker_data);
        navAlerts = (ImageView) findViewById(R.id.nav_alerts);
        navSendAlert = (ImageView) findViewById(R.id.nav_send_alert);
        navSos = (ImageView) findViewById(R.id.nav_sos);
        navHistory = (ImageView) findViewById(R.id.nav_history);
        navLogout = (ImageView) findViewById(R.id.nav_logout);
        navRecommendation = (ImageView) findViewById(R.id.nav_recommendations);
        navAccident = (ImageView) findViewById(R.id.nav_accident);

        calendarSection = (RelativeLayout) findViewById(R.id.calendar_section);
        navigationBar = (ScrollView) findViewById(R.id.navigation_bar);

        // 1. Worker Board
        navWorkerBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navWorkerBoard.setImageResource(R.drawable.ic_worker_board_active);
                openFragment = 1;
                replaceFragments(workerBoardFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // 2. Beacons
        navBeacons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navBeacons.setImageResource(R.drawable.ic_beacons_data_active);
                openFragment = 2;
                replaceFragments(beaconsFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // 3. Worker Data
        navWorkerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navWorkerData.setImageResource(R.drawable.ic_worker_data_active);
                openFragment = 3;
                replaceFragments(workerDataFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // 4. Alerts
        navAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navAlerts.setImageResource(R.drawable.ic_alerts_active);
                openFragment = 4;
                replaceFragments(alertsFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // 5. Send Alerts
        navSendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navSendAlert.setImageResource(R.drawable.ic_send_alert_active);
                openFragment = 5;
                replaceFragments(sendMessagesFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // 6. SosAlert
        navSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navSos.setImageResource(R.drawable.ic_sos_active);
                openFragment = 6;
                replaceFragments(sosFragment.getClass());
                calendarSection.setVisibility(View.GONE);
                if(unreadSosNotification){
                    unreadSosNotification = false;
                }
            }
        });

        // 7. History
        navHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navHistory.setImageResource(R.drawable.ic_history_active);
                openFragment = 7;
                replaceFragments(historyFragment.getClass());
                calendarSection.setVisibility(View.GONE);
            }
        });

        // 8. Recommendations
        navRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navRecommendation.setImageResource(R.drawable.ic_recommendations_active);
                openFragment = 8;
                replaceFragments(recommendationsFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }
            }
        });

        // 9. Accidents
        navAccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearNavSelections();
                navAccident.setImageResource(R.drawable.ic_accident_active);
                openFragment = 9;
                replaceFragments(accidentsFragment.getClass());
                if(calendarSection.getVisibility()==View.GONE){
                    calendarSection.setVisibility(View.VISIBLE);
                }

            }
        });

        // 10. Logout
        navLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialogOpen = true;
                clearNavSelections();
                navLogout.setImageResource(R.drawable.ic_logout_active);
                //show confirm logout section
                confirmLogout = (RelativeLayout) findViewById(R.id.confirm_logout_section);
                confirmLogoutInner = (ScrollView) findViewById(R.id.confirm_logout_inner_section);
                logoutNo = (TextView) findViewById(R.id.confirm_logout_no);
                logoutYes = (TextView) findViewById(R.id.confirm_logout_yes);
                closeLogoutSection = (ImageView) findViewById(R.id.close_confirm_logout_section);

                logoutNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialogOpen = false;
                        hideLogoutDialog();
                    }
                });

                closeLogoutSection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialogOpen = false;
                        hideLogoutDialog();
                    }
                });

                logoutYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //logout user
                        logoutAction();
                        finish();
                    }
                });


                confirmLogout.setVisibility(View.VISIBLE);
                confirmLogoutInner.startAnimation(messageAnimationReveal);
                confirmLogoutInner.setVisibility(View.VISIBLE);
                //show after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    closeLogoutSection.setVisibility(View.VISIBLE);

                }, 200);
            }
        });

        //set up calendar
        displayedDate = null;
        today = null;
        today = Instant.now();
        displayedDate = today;
        yesterday = null;

        String yesterdayStr = today.toString();
        String[] spitTimestamp = yesterdayStr.split("T",2);
        String[] spitDate = spitTimestamp[0].split("-");
        currentDay =  spitDate[2] + "-" + spitDate[1] + "-" +spitDate[0];
        activeDay = currentDay;
        todayText = (TextView) findViewById(R.id.today);
        todayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDay = currentDay;
                displayedDate = today;
                dayText.setVisibility(View.INVISIBLE);
                todayText.setTextColor(getResources().getColor(R.color.blue));
                showData();
            }
        });

        dayText = (TextView) findViewById(R.id.yesterday);
        String y = "00-00-0000";
        dayText.setText(y);
        dayText.setVisibility(View.INVISIBLE);
        previousDay = (ImageView) findViewById(R.id.day_back_icon);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviousDay();
            }
        });
        nextDay = (ImageView) findViewById(R.id.day_forward_icon);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!activeDay.equals(currentDay)){
                    goToNextDay();
                }
            }
        });
        calendarButton = (RelativeLayout) findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // On create , open worker board page (home) by default.
        getSupportFragmentManager().beginTransaction().replace(R.id.Home_fragment, workerBoardFragment).commit();
        navWorkerBoard.setImageResource(R.drawable.ic_worker_board_active);
        openFragment = 1;

        sosResults = 0;

        /**
         *  set repeated process, every 15 sec. Get sos data
         */
        r = new Runnable() {
            public void run() {
                if(!onCall){
                    // check for new sos notifications
                    getSosResults();
                }
                handler.postDelayed(this, RECEIVE_SOS_RESULTS_DB_DASHBOARD_EVERY);
            }
        };

        handler.postDelayed(r, RECEIVE_SOS_RESULTS_DB_DASHBOARD_EVERY);


        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 202);
            }
        }
    }

    private void clearNavSelections(){
        navWorkerBoard.setImageResource(R.drawable.ic_worker_board_inactive);
        navBeacons.setImageResource(R.drawable.ic_beacons_data_inactive);
        navWorkerData.setImageResource(R.drawable.ic_worker_data_inactive);
        navAlerts.setImageResource(R.drawable.ic_alerts_inactive);
        navRecommendation.setImageResource(R.drawable.ic_recommendations_inactive);
        navSendAlert.setImageResource(R.drawable.ic_send_alert_inactive);
        navAccident.setImageResource(R.drawable.ic_accident_inactive);
        navHistory.setImageResource(R.drawable.ic_history_inactive);
        navLogout.setImageResource(R.drawable.ic_logout_inactive);
        if(unreadSosNotification){
            navSos.setImageResource(R.drawable.ic_sos_new_notifications);
        }else {
            navSos.setImageResource(R.drawable.ic_sos_inactive);
        }
        if(messageSection.getVisibility()==View.VISIBLE){
            hideWaitMessage();
        }
    }

    // App keeps in memory a pointer about the last fragment and replace it.
    private void goBackToPreviousFragment(){
        if(openFragment==2){
            navBeacons.setImageResource(R.drawable.ic_beacons_data_active);
            replaceFragments(beaconsFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }else if(openFragment==3){
            navWorkerData.setImageResource(R.drawable.ic_worker_data_active);
            replaceFragments(workerDataFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }else if(openFragment==4){
            navAlerts.setImageResource(R.drawable.ic_alerts_active);
            replaceFragments(alertsFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }else if(openFragment==5){
            navSendAlert.setImageResource(R.drawable.ic_send_alert_active);
            replaceFragments(sendMessagesFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }else if(openFragment==6){
            navSos.setImageResource(R.drawable.ic_sos_active);
            replaceFragments(sosFragment.getClass());
            calendarSection.setVisibility(View.GONE);
        }else if(openFragment==7){
            navHistory.setImageResource(R.drawable.ic_history_active);
            replaceFragments(historyFragment.getClass());
            calendarSection.setVisibility(View.GONE);
        }else if(openFragment==8) {
            navRecommendation.setImageResource(R.drawable.ic_recommendations_active);
            replaceFragments(recommendationsFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }else if(openFragment==9) {
            navAccident.setImageResource(R.drawable.ic_accident_active);
            replaceFragments(accidentsFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }else {
            navWorkerBoard.setImageResource(R.drawable.ic_worker_board_active);
            replaceFragments(workerBoardFragment.getClass());
            if(calendarSection.getVisibility()==View.GONE){
                calendarSection.setVisibility(View.VISIBLE);
            }
        }
    }

    public void replaceFragments(Class fragmentClass) {
        isDialogOpen = false;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Home_fragment, fragment).commit();
    }

    // On back press, app goes but to homepage (1st selection) aka Worker Board Page.
    // If the app is already there, it closed the activity (back button functionality)
    @Override
    public void onBackPressed() {
        if(logoutDialogOpen){
            logoutDialogOpen = false;
            hideLogoutDialog();
        }else {
            Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            if (!(myFragment instanceof WorkerBoard)) {
                clearNavSelections();
                navigationBar.smoothScrollTo(0,0);
                openFragment=1;
                navWorkerBoard.setImageResource(R.drawable.ic_worker_board_active);
                replaceFragments(workerBoardFragment.getClass());
            }else if(isDialogOpen){
                setDialogOpen(false);
                clearNavSelections();
                navigationBar.smoothScrollTo(0,0);
                openFragment=1;
                navWorkerBoard.setImageResource(R.drawable.ic_worker_board_active);
                replaceFragments(workerBoardFragment.getClass());
            }else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        okToShow = false;
        handler.removeCallbacks(r);
    }

    @Override
    public void onStop(){
        super.onStop();
        okToShow = false;
    }

    private void hideLogoutDialog(){
        confirmLogoutInner.startAnimation(messageAnimationGone);
        confirmLogoutInner.setVisibility(View.GONE);
        confirmLogout.setVisibility(View.VISIBLE);
        closeLogoutSection.setVisibility(View.GONE);

        // hide after 200 milsec
        timer.removeCallbacks(runnable);
        timer.postDelayed( runnable = () -> {
            confirmLogout.setVisibility(View.GONE);
            navLogout.setImageResource(R.drawable.ic_logout_inactive);
            goBackToPreviousFragment();
        }, 200);
    }

    private void logoutAction(){
        JsonObject jsonUser = new JsonObject();
        jsonUser.addProperty("userName","none");
        jsonUser.addProperty("userId","none");
        jsonUser.addProperty("accessToken","none");
        jsonUser.addProperty("login","OUT");
        jsonUser.addProperty("role","none");
        String userString = jsonUser.toString();
        User noneUser = new Gson().fromJson(userString,User.class);
        saveUser(userString);
        DataHolder.getInstance().setSelectedUser(noneUser);
        Intent intent = new Intent(this, Login.class);
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

    public void hideKeyboard(){
        try  {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showErrorLoadingData(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.error_data));
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
    public void showErrorNavigation(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.navigation_app_error));
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
    public void showWaitMessage(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.wait));
                        messageText.setVisibility(View.VISIBLE);
                        messageProgressBar.setVisibility(View.VISIBLE);
                        messageAction.setVisibility(View.GONE);
                        messageSection.startAnimation(messageAnimationReveal);
                        messageSection.setVisibility(View.VISIBLE);
                    }
                }, 100);
    }

    public void hideWaitMessage(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                            messageSection.startAnimation(messageAnimationGone);
                            messageSection.setVisibility(View.GONE);
                    }
                }, 100);
    }

    public void showSuccessfulAlert(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.send_successful));
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

    public void showSuccessfulAccident(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.report_successful));
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

    public void showCannotEdit(){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        messageText.setText(getString(R.string.can_not_edit));
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

    //set date
    public String getTimestamp(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdf.format(now);
    }

    // this method refresh the fragment view after going back from locked device
    @Override
    protected void onStart() {
        super.onStart();
        okToShow = true;
        goBackToPreviousFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        okToShow = true;
    }

    private void goToPreviousDay(){
        dayText.setVisibility(View.VISIBLE);
        todayText.setTextColor(getResources().getColor(R.color.grey));
        Instant yesterday = displayedDate.minus(1, ChronoUnit.DAYS);
        String yesterdayStr = yesterday.toString();
        String[] spitTimestamp = yesterdayStr.split("T",2);
        String[] spitDate = spitTimestamp[0].split("-");
        String finalYesterdayText =  spitDate[2] + "-" + spitDate[1] + "-" +spitDate[0];
        dayText.setText(finalYesterdayText);
        displayedDate = yesterday;
        activeDay = finalYesterdayText;
        showData();
    }

    private void goToNextDay(){
        dayText.setVisibility(View.VISIBLE);
        todayText.setTextColor(getResources().getColor(R.color.grey));
        Instant yesterday = displayedDate.plus(1, ChronoUnit.DAYS);
        String yesterdayStr = yesterday.toString();
        String[] spitTimestamp = yesterdayStr.split("T",2);
        String[] spitDate = spitTimestamp[0].split("-");
        String finalYesterdayText =  spitDate[2] + "-" + spitDate[1] + "-" +spitDate[0];
        dayText.setText(finalYesterdayText);
        displayedDate = yesterday;
        activeDay = finalYesterdayText;
        if(activeDay.equals(currentDay)){
            dayText.setVisibility(View.INVISIBLE);
            todayText.setTextColor(getResources().getColor(R.color.blue));
        }
        showData();
    }

    private void goToSpecificDay(String date){

       DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
                .toFormatter()
                .withZone(ZoneId.of("Europe/Paris"));
        displayedDate = FMT.parse(date, Instant::from);
        displayedDate = displayedDate.plus(1, ChronoUnit.DAYS);

        String[] spitDate = date.split("-");
        String finalText =  spitDate[2] + "-" + spitDate[1] + "-" +spitDate[0];
        dayText.setVisibility(View.VISIBLE);
        todayText.setTextColor(getResources().getColor(R.color.grey));
        dayText.setText(finalText);
        activeDay = finalText;
        if(activeDay.equals(currentDay)){
            dayText.setVisibility(View.INVISIBLE);
            todayText.setTextColor(getResources().getColor(R.color.blue));
        }
        showData();
    }

    public void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog= new DatePickerDialog(this, R.style.my_dialog_theme, datePickerListener, mYear, mMonth, mDay);
        dateDialog.getDatePicker().setMaxDate(new Date().getTime());
        dateDialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            String selectedDate;
            String d,m;
            if(day<10){
                d = "0"+day;
            }else {
                d = ""+day;
            }
            if(month<10){
                m = "0"+month;
            }else {
                m = ""+month;
            }
            selectedDate = year + "-" + m + "-"+d;
            goToSpecificDay(selectedDate);
        }
    };

    // format 23-02-14 (year (2 char) - Month - Day
    public String getSelectedDate(){
        String[] spitDate = activeDay.split("-");
        String splitYear = spitDate[2];
        splitYear = splitYear.substring(Math.max(splitYear.length() - 2, 0));
        return splitYear + "-" + spitDate[1] + "-" +spitDate[0];
    }

    // format 23-02-14 (year (2 char) - Month - Day
    public String getCurrentDate(){
        String[] spitDate = currentDay.split("-");
        String splitYear = spitDate[2];
        splitYear = splitYear.substring(Math.max(splitYear.length() - 2, 0));
        return splitYear + "-" + spitDate[1] + "-" +spitDate[0];
    }

    public void setDialogOpen(boolean x){
        isDialogOpen = x;
    }

    public boolean isDialogOpen() {
        return isDialogOpen;
    }

    public void hideCalendar(){
        calendarSection.setVisibility(View.GONE);
    }

    public void showCalendar(){
        calendarSection.setVisibility(View.VISIBLE);
    }

    public boolean isToday(){
        return activeDay.equals(currentDay);
    }

    private void getSosResults(){
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date" +
                "&order=desc&siteid=&perpage=100000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=sos";

        Request request = new Request.Builder()
                .url(registerUrl)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                onCall = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        SosCMS.Root sosAlertsResponse = new Gson().fromJson(myResponse,SosCMS.Root.class);
                        if(sosAlertsResponse.respond.equals("1")){
                            int tmpSosResults = sosAlertsResponse.result.size();
                            if(sosResults<tmpSosResults && sosResults!=0){
                                newSosNotification();
                            }
                            sosResults = tmpSosResults;
                            DataHolder.getInstance().setSosResults(tmpSosResults);
                        }
                    }
                }
                onCall = false;
            }
        });
    }

    public void setSosResults(int x){
        sosResults = x;
    }

    private void newSosNotification(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            //check if we are not on the sos page
                            Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
                            if (!(myFragment instanceof Sos)) {
                                navSos.setImageResource(R.drawable.ic_sos_new_notifications);
                                unreadSosNotification = true;
                                //show notification
                                if (Build.VERSION.SDK_INT >= 33) {
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                        createNotificationChannel();
                                        showNotification();
                                    }
                                }else {
                                    createNotificationChannel();
                                    showNotification();
                                }

                            }

                        }
                    }, 100);
        }
    }

    private void showNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sonia_app_icon)
                .setContentTitle("SOS notifications")
                .setContentText("There are new SOS notifications")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void showData(){
        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
        if (myFragment instanceof WorkerBoard) {
            Log.e("OPEN FRAGMENT","WorkerBoard");
            WorkerBoard fragment = (WorkerBoard) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            fragment.showData();
        }else if (myFragment instanceof Beacons) {
            Log.e("OPEN FRAGMENT","Beacons");
            Beacons fragment = (Beacons) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            fragment.showData();
        }else if (myFragment instanceof Alerts) {
            Log.e("OPEN FRAGMENT","Alerts");
            Alerts fragment = (Alerts) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            fragment.showData();
        }else if (myFragment instanceof WorkerData) {
            Log.e("OPEN FRAGMENT","WorkerData");
            WorkerData fragment = (WorkerData) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            fragment.showData();
        }else if (myFragment instanceof SendMessages) {
            Log.e("OPEN FRAGMENT", "SendMessages");
            SendMessages fragment = (SendMessages) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            fragment.showData();
        }else if (myFragment instanceof Recommendations) {
                Log.e("OPEN FRAGMENT","Recommendations");
                Recommendations fragment = (Recommendations) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
                fragment.showData();
        }else if (myFragment instanceof Accidents) {
            Log.e("OPEN FRAGMENT","Accidents");
            Accidents fragment = (Accidents) getSupportFragmentManager().findFragmentById(R.id.Home_fragment);
            fragment.showData();
        }else {
            Log.e("OPEN FRAGMENT","Unknown");
        }
    }

}