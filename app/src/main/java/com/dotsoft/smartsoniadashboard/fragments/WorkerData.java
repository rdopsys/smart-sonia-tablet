package com.dotsoft.smartsoniadashboard.fragments;

import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.SosAlertsAdapter;
import com.dotsoft.smartsoniadashboard.adapters.WorkerDataAdapter;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.Worker;
import com.dotsoft.smartsoniadashboard.objects.cms.SosCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.UserCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.WeatherCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.WorkerDataCMS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WorkerData extends Fragment {
    private final static String TAG = "WORKER_DATA";
    private final int GET_DATA_EVERY_MILL = 1000 * 10; // 10 sec
    private ArrayList<Worker> workersList = new ArrayList<>();
    private ProgressBar loading;
    private ImageView refresh;
    private boolean onCall = false;
    private RecyclerView recyclerViewWorkerData;
    private WorkerDataAdapter workerDataAdapter;
    private Runnable r;
    private final Handler handler = new Handler();
    private boolean okToShow = true;
    private String date;
    private boolean isToday = true;

    public WorkerData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_worker_data, container, false);
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
                        for(int i=0;i<workersList.size();i++){
                            getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), date,i == workersList.size() - 1);
                        }
                    }
                }
            }
        });
        recyclerViewWorkerData = (RecyclerView) view.findViewById(R.id.recycler_view_worker_data);
        if(DataHolder.getInstance().getWorkersList()!=null){
            workersList = DataHolder.getInstance().getWorkersList();
            onCall = true;
            showLoading();
            for(int i=0;i<workersList.size();i++){
                getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), date,i == workersList.size() - 1);
            }
        }else{
            onCall = true;
            showLoading();
            getWorkerList();
            recyclerViewWorkerData.setVisibility(View.GONE);
        }

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewSosAlerts = new GridLayoutManager(getContext(), 4);
        recyclerViewWorkerData.setLayoutManager(mLrecyclerViewSosAlerts);
        workerDataAdapter = new WorkerDataAdapter(workersList,isToday);
        recyclerViewWorkerData.setAdapter(workerDataAdapter);

        /**
         *  set repeated process, every 5 sec. Get worker data
         */
        r = new Runnable() {
            public void run() {
                if(!onCall){
                    if(okToShow){
                        if(isToday){
                            if(workersList!=null){
                                onCall = true;
                                showLoading();
                                for(int i=0;i<workersList.size();i++){
                                    getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), date,i == workersList.size() - 1);
                                }
                            }
                        }
                    }
                }
                handler.postDelayed(this, GET_DATA_EVERY_MILL);

            }

        };

        handler.postDelayed(r, GET_DATA_EVERY_MILL);

        return view;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(r);
        okToShow = false;
        super.onDestroy();
    }

    @Override
    public void onStop(){
        okToShow = false;
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getWorkerData(String workerID, String date, boolean getWeather){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?author_id="+workerID+"&perpage=1&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=worker_data" +
                "&orderby=postmeta.timestamp&order=DEC&custom_meta={\"date_split_worker\": \""+date+"\"}";
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
                        if(getWeather){
                            showWorkerData();
                            getWeatherData();
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

    private void getWeatherData(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getpost/?post_id=2000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq";
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
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        try {
                            WeatherCMS.Root weatherDataResponse = new Gson().fromJson(myResponse, WeatherCMS.Root.class);
                            if (weatherDataResponse.respond.equals("1")) {
                                for (int i = 0; i < workersList.size(); i++) {
                                    workersList.get(i).setWeatherData(weatherDataResponse);
                                }
                                DataHolder.getInstance().setWorkersList(workersList);
                                showWorkerData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                ((Dashboard) getActivity()).showErrorLoadingData();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        hideLoading();
                    }
                }else{
                    hideLoading();
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                onCall=false;
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
                                workerDataAdapter.setAdapterList(subList,isToday);
                                if(recyclerViewWorkerData.getVisibility()==View.GONE){
                                    recyclerViewWorkerData.setVisibility(View.VISIBLE);
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
                                getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), date,i == workersList.size() - 1);
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
            if(DataHolder.getInstance().getWorkersList()!=null){
                workersList = DataHolder.getInstance().getWorkersList();
                onCall = true;
                showLoading();
                for(int i=0;i<workersList.size();i++){
                    getWorkerData(workersList.get(i).getWorkerProfileInfo().getUserId(), date,i == workersList.size() - 1);
                }
            }else{
                onCall = true;
                showLoading();
                getWorkerList();
            }
        }

    }

}