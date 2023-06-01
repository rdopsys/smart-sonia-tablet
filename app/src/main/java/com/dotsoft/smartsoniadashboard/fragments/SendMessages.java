package com.dotsoft.smartsoniadashboard.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.MessagesAdapter;
import com.dotsoft.smartsoniadashboard.adapters.SelectWorkerAdapter;
import com.dotsoft.smartsoniadashboard.objects.MessageObject;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.Worker;
import com.dotsoft.smartsoniadashboard.objects.cms.MessagesObjectCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.UserCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.WorkerDataCMS;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class SendMessages extends Fragment implements SelectWorkerAdapter.HandleEventClick  {
    private final static String TAG = "SEND_ALERT";
    private ArrayList<MessageObject> messagesList = new ArrayList<>();
    private ArrayList<Worker> workersList = new ArrayList<>();
    private final int REFRESH_MESSAGES_DB_DASHBOARD_EVERY = 1000 * 15; // 15 sec
    private ProgressBar loading;
    private ImageView refresh;
    private boolean onCall = false;
    private RecyclerView recyclerViewSendAlerts,recyclerViewMessages;
    private SelectWorkerAdapter selectWorkerAdapter;
    private MessagesAdapter messagesAdapter;
    private String date;
    private Runnable r,r1;
    private final Handler handler = new Handler();
    private boolean okToShow = true;
    private TextView alertTitle,noMessages;
    private RelativeLayout addAlertSection, sendButton, cancelButton;
    private ScrollView addAlertInnerSection;
    private TextInputEditText title,message;
    private TextInputLayout titleLayout, messageLayout;
    private Animation messageAnimationReveal, messageAnimationGone;
    private Handler timer;
    private Runnable runnable;
    private boolean isToday = true;
    private final Handler handler1 = new Handler();



    public SendMessages() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_messages, container, false);
        okToShow = true;
        date = ((Dashboard) (getActivity())).getSelectedDate();
        isToday = ((Dashboard) (getActivity())).isToday();


        //set up loading
        loading = (ProgressBar) view.findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        //set up refresh data
        refresh = (ImageView) view.findViewById(R.id.refresh_button);
        refresh.setVisibility(View.VISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!onCall){
                    if(DataHolder.getInstance().getWorkersList()!=null){
                        workersList = DataHolder.getInstance().getWorkersList();
                        onCall = true;
                        showLoading();
                        getMessagesList(date);
                        for(int i=0;i<workersList.size();i++){
                            getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), i == workersList.size() - 1);
                        }
                    }
                }
            }
        });


        recyclerViewSendAlerts = (RecyclerView) view.findViewById(R.id.recycler_view_send_alerts);
        if(DataHolder.getInstance().getWorkersList()!=null){
            workersList = DataHolder.getInstance().getWorkersList();
            onCall = true;
            showLoading();
            for(int i=0;i<workersList.size();i++){
                getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), i == workersList.size() - 1);
            }
        }else{
            onCall = true;
            showLoading();
            getWorkerList();
            recyclerViewSendAlerts.setVisibility(View.GONE);
        }

        //set messages recycler view
        recyclerViewMessages = (RecyclerView) view.findViewById(R.id.recycler_view_messages);
        recyclerViewMessages.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLrecyclerViewMessages = new GridLayoutManager(getContext(), 4);
        recyclerViewMessages.setLayoutManager(mLrecyclerViewMessages);
        messagesAdapter = new MessagesAdapter(messagesList);
        recyclerViewMessages.setAdapter(messagesAdapter);
        recyclerViewMessages.setVisibility(View.VISIBLE);

        //set workers list
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSendAlerts.setLayoutManager(layoutManager);
        selectWorkerAdapter = new SelectWorkerAdapter(workersList,this);
        recyclerViewSendAlerts.setAdapter(selectWorkerAdapter);

        noMessages = (TextView) view.findViewById(R.id.no_messages);
        noMessages.setVisibility(View.GONE);

        if(DataHolder.getInstance().getMessagesList()!=null){
            messagesList = DataHolder.getInstance().getMessagesList();
            showData();
        }

        showLoading();
        getMessagesList(date);

        //set animations and timer
        messageAnimationReveal = AnimationUtils.loadAnimation(getContext(), R.anim.fadein_once);
        messageAnimationGone = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout_once);
        timer = new Handler();

        //set up new alert form
        addAlertSection = (RelativeLayout) view.findViewById(R.id.background_add_alerts_form_section);
        addAlertInnerSection = (ScrollView) view.findViewById(R.id.add_alert_form_section);

        alertTitle = (TextView) view.findViewById(R.id.add_alert_title);
        sendButton = (RelativeLayout) view.findViewById(R.id.send_alert);
        cancelButton = (RelativeLayout) view.findViewById(R.id.cancel_send_alert);

        //set input fields
        title = (TextInputEditText) view.findViewById(R.id.alert_title_input_text);
        message = (TextInputEditText) view.findViewById(R.id.alert_message_input_text);
        titleLayout = (TextInputLayout) view.findViewById(R.id.alert_title_input_section);
        messageLayout = (TextInputLayout) view.findViewById(R.id.alert_message_input_section);

        /**
         *  set repeated process, every 15 sec. Get sos data
         */
        r1 = new Runnable() {
            public void run() {
                if(!onCall){
                    // Load sos alerts
                    showData();
                }
                handler1.postDelayed(this, REFRESH_MESSAGES_DB_DASHBOARD_EVERY);
            }
        };

        handler1.postDelayed(r1, REFRESH_MESSAGES_DB_DASHBOARD_EVERY);


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(r);
        handler1.removeCallbacks(r1);
        okToShow = false;
    }

    @Override
    public void onStop(){
        super.onStop();
        okToShow = false;
    }

    private void getWorkerData(String workerID, boolean lastWorker){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String date = ((Dashboard) (getActivity())).getCurrentDate();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?author_id="+workerID+"&perpage=1&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&custom_post=worker_data&orderby=postmeta.timestamp&order=DEC&custom_meta={\"date_split_worker\": \""+date+"\"}";

        Request request = new Request.Builder()
                .url(registerUrl)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                hideLoading();
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                onCall = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        WorkerDataCMS.Root workerDataResponse = new Gson().fromJson(myResponse,WorkerDataCMS.Root.class);
                        int workerPosition = getWorkerIndexById(workerID, workersList);
                        if(workerDataResponse.result.size()!=0){
                                if(workerPosition!=-1){
                                    if(workersList.get(workerPosition).getData().size()==0){
                                        workersList.get(workerPosition).addWorkerData(workerDataResponse.result.get(0).custom_fields.timestamp,
                                                workerDataResponse.result.get(0).custom_fields.heartrate,
                                                workerDataResponse.result.get(0).custom_fields.height,
                                                workerDataResponse.result.get(0).custom_fields.location,
                                                workerDataResponse.result.get(0).custom_fields.beacon_id,
                                                workerDataResponse.result.get(0).custom_fields.beacon_distance);
                                        DataHolder.getInstance().setWorkersList(workersList);
                                    }else if(!workersList.get(workerPosition).getData().get(0).getTimestampRawFormat().equals(workerDataResponse.result.get(0).custom_fields.timestamp)){
                                        workersList.get(workerPosition).addWorkerData(workerDataResponse.result.get(0).custom_fields.timestamp,
                                                workerDataResponse.result.get(0).custom_fields.heartrate,
                                                workerDataResponse.result.get(0).custom_fields.height,
                                                workerDataResponse.result.get(0).custom_fields.location,
                                                workerDataResponse.result.get(0).custom_fields.beacon_id,
                                                workerDataResponse.result.get(0).custom_fields.beacon_distance);
                                        DataHolder.getInstance().setWorkersList(workersList);
                                    }

                                }
                            }else{
                                if(workerPosition!=-1){
                                    workersList.get(workerPosition).addWorkerData("N/A","N/A","0.0","N/A","N/A","N/A");
                                    DataHolder.getInstance().setWorkersList(workersList);
                                }
                            }
                        if(lastWorker){
                            onCall = false;
                            showWorkerData();
                            hideLoading();
                        }
                    }

                }else{
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    onCall = false;
                }
            }
        });
    }

    private void showWorkerData(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            ArrayList subList = new ArrayList<>(workersList);
                            for(int i =0;i< subList.size();i++){
                                selectWorkerAdapter.setAdapterList(subList);
                                if(recyclerViewSendAlerts.getVisibility()==View.GONE){
                                    recyclerViewSendAlerts.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }, 100);
        }

    }

    private void showLoading(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            refresh.setVisibility(View.GONE);
                            loading.setVisibility(View.VISIBLE);
                        }
                    }, 100);
        }
    }

    private void hideLoading(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            loading.setVisibility(View.GONE);
                            if(isToday){
                                refresh.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 100);
        }
    }

    private Integer getWorkerIndexById(String ID, ArrayList<Worker> list){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getWorkerProfileInfo().getUserId().equals(ID)){
                return i;
            }
        }
        return -1;
    }

    private void getWorkerList(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/authors/?role=subscriber&perpage=100000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq";
        Request request = new Request.Builder()
                .url(registerUrl)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                hideLoading();
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                onCall = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();

                if (response.isSuccessful()){
                    if(okToShow){
                        //Log.e("RESPONSE - onResponse", "register call isSuccessful" + myResponse);
                        UserCMS.Root userResponse = new Gson().fromJson(myResponse, UserCMS.Root.class);
                        if(userResponse.respond.equals("1")) {
                            ArrayList<Worker> workers = new ArrayList();
                            for (int i = 0; i < userResponse.result.size(); i++) {
                                if (userResponse.result.get(i).custom_fields.user_role.equals("WORKER")) {
                                    // get user raw format history info
                                    String info = userResponse.result.get(i).custom_fields.user_info;

                                    // Build user json
                                    JsonObject jsonWorker = new JsonObject();
                                    jsonWorker.addProperty("userName", userResponse.result.get(i).nickname);
                                    jsonWorker.addProperty("userId", userResponse.result.get(i).ID);
                                    jsonWorker.addProperty("accessToken", userResponse.result.get(i).Access_Token);
                                    jsonWorker.addProperty("login", "null");
                                    jsonWorker.addProperty("role", userResponse.result.get(i).custom_fields.user_role);
                                    String workerString = jsonWorker.toString();
                                    User workerUserInfo = new Gson().fromJson(workerString, User.class);
                                    workers.add(new Worker(workerUserInfo,info));

                                    //Build user history info
                                    String active,smartwatch,timestamp;
                                    if(info==null){
                                        timestamp ="";
                                        active = "";
                                        smartwatch = "";
                                        workers.get(workers.size()-1).addWorkerInfo(active,smartwatch,timestamp);

                                    }else {
                                        String[] revision = info.split("\\|");
                                        for(int y=0;y<revision.length;y++) {
                                            String[] value = revision[y].split(",");
                                            if (value.length == 3) {
                                                timestamp = value[2];
                                                active = value[0];
                                                smartwatch = value[1];
                                                workers.get(workers.size() - 1).addWorkerInfo(active, smartwatch, timestamp);
                                            } else if (y == 0) {
                                                timestamp = "";
                                                active = "";
                                                smartwatch = "";
                                                workers.get(workers.size() - 1).addWorkerInfo(active, smartwatch, timestamp);
                                            }
                                        }
                                    }
                                }
                            }
                            DataHolder.getInstance().setWorkersList(workers);
                            workersList = workers;
                            for(int i=0;i<workersList.size();i++){
                                getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), i == workersList.size() - 1);
                            }
                        }else{
                            onCall = false;
                            hideLoading();
                            try {
                                ((Dashboard) getActivity()).showErrorLoadingData();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }

                }else{
                    onCall = false;
                    hideLoading();
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void itemClickAddAlert(String id) {
        if(!onCall){
            try{
                ((Dashboard) getActivity()).hideCalendar();
            } catch (Exception e) {
                e.printStackTrace();
            }
            addNewAlert(id);
        }
    }

    private void addNewAlert(String workerUsername){
        //set up low alert active by default
        String t = getString(R.string.send_alert_to) + " " + workerUsername.toLowerCase();
        alertTitle.setText(t);
        title.setText("");
        message.setText("");
        titleLayout.setErrorEnabled(false);
        title.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
        addAlertSection.setVisibility(View.VISIBLE);
        addAlertInnerSection.startAnimation(messageAnimationReveal);
        addAlertInnerSection.setVisibility(View.VISIBLE);
        addAlertInnerSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });

        addAlertSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                addAlertInnerSection.startAnimation(messageAnimationGone);
                addAlertInnerSection.setVisibility(View.GONE);
                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    try{
                        ((Dashboard) getActivity()).showCalendar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addAlertSection.setVisibility(View.GONE);
                }, 200);

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                //add beacon
                titleLayout.setErrorEnabled(false);
                title.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                String titleText = title.getText().toString();
                String messageText = message.getText().toString();

                if(titleText.equals("")){
                    titleLayout.setErrorEnabled(true);
                    titleLayout.setError(getString(R.string.empty_field_title));
                    title.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                } else{
                    sendNewMessage(workerUsername,titleText,messageText);
                    addAlertInnerSection.startAnimation(messageAnimationGone);
                    addAlertSection.setVisibility(View.GONE);
                    // hide after 200 milsec
                    timer.removeCallbacks(runnable);
                    timer.postDelayed( runnable = () -> {
                        try{
                            ((Dashboard) getActivity()).showCalendar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addAlertSection.setVisibility(View.GONE);
                    }, 200);
                }
            }
        });
    }

    private void sendNewMessage(String username, String title, String message){
        onCall = true;
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String time = ((Dashboard) getActivity()).getTimestamp();
        String[] spitTimestamp = time.split(" ",2);
        String dateOnly = spitTimestamp[0];
        String timeOnly = spitTimestamp[1];
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String endpoint = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/newpost/?subject="+title+"&custom_field={\"notes\":\""+message+"\",\"worker_Name\":\""+username+"\",\"date_split_message\":\""+dateOnly+"\",\"read\":\"1\",\"timestamp\":\""+timeOnly+"\"}" +
                "&custom_post=messages&post_status=publish&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq"+"&ACCESS_TOKEN=" +DataHolder.getInstance().getSelectedUser().getAccessToken();
        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                onCall = false;
                Log.e(TAG,"Send message failed");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e(TAG,"Message send successful");
                    try {
                        ((Dashboard) getActivity()).showSuccessfulAlert();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    showData();
                } else {
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    Log.e(TAG,"Message DO NOT send successful");
                }
                onCall = false;
            }
        });
    }

    private void getMessagesList(String date){
        showLoading();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=1000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&custom_post=messages&custom_meta={\"date_split_message\": \""+date+"\"}";
        Request request = new Request.Builder()
                .url(registerUrl)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                hideLoading();
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                onCall = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        //Log.e("RESPONSE - onResponse", "message call isSuccessful" + myResponse);
                        MessagesObjectCMS.Root messagesResponse = new Gson().fromJson(myResponse, MessagesObjectCMS.Root.class);
                        if(messagesResponse.respond.equals("1")) {
                            ArrayList<MessageObject> messages = new ArrayList();
                            for (int i = 0; i < messagesResponse.result.size(); i++) {
                                messages.add(new MessageObject(messagesResponse.result.get(i).ID,messagesResponse.result.get(i).custom_fields.worker_name,
                                        messagesResponse.result.get(i).post_title,messagesResponse.result.get(i).custom_fields.read,
                                        messagesResponse.result.get(i).custom_fields.date_split_message, messagesResponse.result.get(i).custom_fields.timestamp,
                                        messagesResponse.result.get(i).custom_fields.notes));
                            }
                            DataHolder.getInstance().setMessagesList(messages);
                            messagesList = messages;
                            showMessages();
                            hideLoading();
                            onCall = false;
                        }else{
                            ArrayList<MessageObject> messages = new ArrayList();
                            DataHolder.getInstance().setMessagesList(messages);
                            messagesList = messages;
                            onCall = false;
                            showMessages();
                            hideLoading();
                        }
                    }
                }else{
                    onCall = false;
                    hideLoading();
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    private void showMessages(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            if(messagesList.size()==0){
                                noMessages.setVisibility(View.VISIBLE);
                                recyclerViewMessages.setVisibility(View.GONE);
                            }else {
                                noMessages.setVisibility(View.GONE);
                                recyclerViewMessages.setVisibility(View.VISIBLE);
                            }
                            ArrayList<MessageObject> subList;
                            subList = new ArrayList<>(messagesList);
                            messagesAdapter.setAdapterList(subList);
                        }
                    }, 100);
        }
    }


    public void showData(){
        if(okToShow){
            if(((Dashboard) (getActivity())).isToday()) {
                isToday = true;
                if(refresh.getVisibility()==View.GONE){
                    refresh.setVisibility(View.VISIBLE);
                }
            }else {
                isToday = false;
                refresh.setVisibility(View.GONE);
            }
            date = ((Dashboard) (getActivity())).getSelectedDate();
            onCall = true;
            showLoading();
            getMessagesList(date);
        }
    }

}