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
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.AlertsAdapter;
import com.dotsoft.smartsoniadashboard.objects.AlertObject;
import com.dotsoft.smartsoniadashboard.objects.cms.AlertObjectCMS;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Alerts extends Fragment {
    private final static String TAG = "ALERTS";
    private ArrayList<AlertObject> alertsList = new ArrayList<>();
    private final int RECEIVE_ALERTS_DB_DASHBOARD_EVERY = 1000 * 15; // 15 sec
    private ProgressBar loading;
    private ImageView refresh;
    private boolean onCall = false;
    private TextView noAlerts,allAlerts, highAlerts, lowAlerts,veryLowAlerts,mediumAlerts,veryHighAlerts,deleteNo,deleteYes;
    private RelativeLayout allAlertsButton, highAlertsButton, lowAlertsButton,veryLowAlertsButton, mediumAlertsButton,veryHighAlertsButton, confirmDeleteSection,confirmDeleteInnerSection;
    private boolean okToShow = true;
    private Runnable r,runnable;
    private final Handler handler = new Handler();
    private Handler timer;
    private RecyclerView recyclerViewAlerts;
    private AlertsAdapter alertsAdapter;
    private String activeFilterButton = "ALL";
    private Animation messageAnimationReveal, messageAnimationGone;
    private String date;
    private boolean isToday = true;

    public Alerts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        date = ((Dashboard) (getActivity())).getSelectedDate();
        isToday = ((Dashboard) (getActivity())).isToday();


        noAlerts = (TextView) view.findViewById(R.id.no_alerts);
        noAlerts.setVisibility(View.GONE);

        allAlerts = (TextView) view.findViewById(R.id.all_alerts);
        allAlertsButton = (RelativeLayout) view.findViewById(R.id.all_alerts_section);
        allAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeFilterButton = "ALL";
                allAlerts.setTextColor(getResources().getColor(R.color.yellow));
                allAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_section));
                highAlerts.setTextColor(getResources().getColor(R.color.blue));
                highAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_high_outline_no_round_button_normal));
                lowAlerts.setTextColor(getResources().getColor(R.color.blue));
                lowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_low_outline_no_round_button_normal));
                veryLowAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryLowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_low_outline_no_round_button_normal));
                mediumAlerts.setTextColor(getResources().getColor(R.color.blue));
                mediumAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_medium_outline_no_round_button_normal));
                veryHighAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryHighAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_high_outline_no_round_button_normal));
                showAlerts();
            }
        });

        veryLowAlerts = (TextView) view.findViewById(R.id.very_low_risk_alerts);
        veryLowAlertsButton = (RelativeLayout) view.findViewById(R.id.very_low_risk_alerts_section);
        veryLowAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeFilterButton = "VERY LOW";
                allAlerts.setTextColor(getResources().getColor(R.color.blue));
                allAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_outline_no_round_button_normal));
                highAlerts.setTextColor(getResources().getColor(R.color.blue));
                highAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_high_outline_no_round_button_normal));
                lowAlerts.setTextColor(getResources().getColor(R.color.blue));
                lowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_low_outline_no_round_button_normal));
                veryLowAlerts.setTextColor(getResources().getColor(R.color.yellow));
                veryLowAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_section));
                mediumAlerts.setTextColor(getResources().getColor(R.color.blue));
                mediumAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_medium_outline_no_round_button_normal));
                veryHighAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryHighAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_high_outline_no_round_button_normal));
                showAlerts();
            }
        });

        lowAlerts = (TextView) view.findViewById(R.id.low_risk_alerts);
        lowAlertsButton = (RelativeLayout) view.findViewById(R.id.low_risk_alerts_section);
        lowAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeFilterButton = "LOW";
                allAlerts.setTextColor(getResources().getColor(R.color.blue));
                allAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_outline_no_round_button_normal));
                highAlerts.setTextColor(getResources().getColor(R.color.blue));
                highAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_high_outline_no_round_button_normal));
                lowAlerts.setTextColor(getResources().getColor(R.color.yellow));
                lowAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_section));
                veryLowAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryLowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_low_outline_no_round_button_normal));
                mediumAlerts.setTextColor(getResources().getColor(R.color.blue));
                mediumAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_medium_outline_no_round_button_normal));
                veryHighAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryHighAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_high_outline_no_round_button_normal));
                showAlerts();
            }
        });

        mediumAlerts = (TextView) view.findViewById(R.id.medium_risk_alerts);
        mediumAlertsButton = (RelativeLayout) view.findViewById(R.id.medium_alerts_section);
        mediumAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeFilterButton = "MEDIUM";
                allAlerts.setTextColor(getResources().getColor(R.color.blue));
                allAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_outline_no_round_button_normal));
                highAlerts.setTextColor(getResources().getColor(R.color.blue));
                highAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_high_outline_no_round_button_normal));
                lowAlerts.setTextColor(getResources().getColor(R.color.blue));
                lowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_low_outline_no_round_button_normal));
                veryLowAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryLowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_low_outline_no_round_button_normal));
                mediumAlerts.setTextColor(getResources().getColor(R.color.yellow));
                mediumAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_section));
                veryHighAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryHighAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_high_outline_no_round_button_normal));
                showAlerts();
            }
        });

        highAlerts = (TextView) view.findViewById(R.id.high_risk_alerts);
        highAlertsButton = (RelativeLayout) view.findViewById(R.id.high_risk_alerts_section);
        highAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeFilterButton = "HIGH";
                allAlerts.setTextColor(getResources().getColor(R.color.blue));
                allAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_outline_no_round_button_normal));
                highAlerts.setTextColor(getResources().getColor(R.color.yellow));
                highAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_section));
                lowAlerts.setTextColor(getResources().getColor(R.color.blue));
                lowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_low_outline_no_round_button_normal));
                veryLowAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryLowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_low_outline_no_round_button_normal));
                mediumAlerts.setTextColor(getResources().getColor(R.color.blue));
                mediumAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_medium_outline_no_round_button_normal));
                veryHighAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryHighAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_high_outline_no_round_button_normal));
                showAlerts();
            }
        });

        veryHighAlerts = (TextView) view.findViewById(R.id.very_high_risk_alerts);
        veryHighAlertsButton = (RelativeLayout) view.findViewById(R.id.very_high_risk_alerts_section);
        veryHighAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeFilterButton = "VERY HIGH";
                allAlerts.setTextColor(getResources().getColor(R.color.blue));
                allAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_outline_no_round_button_normal));
                highAlerts.setTextColor(getResources().getColor(R.color.blue));
                highAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_high_outline_no_round_button_normal));
                lowAlerts.setTextColor(getResources().getColor(R.color.blue));
                lowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_low_outline_no_round_button_normal));
                veryLowAlerts.setTextColor(getResources().getColor(R.color.blue));
                veryLowAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_very_low_outline_no_round_button_normal));
                mediumAlerts.setTextColor(getResources().getColor(R.color.blue));
                mediumAlertsButton.setBackground(getResources().getDrawable(R.drawable.risk_medium_outline_no_round_button_normal));
                veryHighAlerts.setTextColor(getResources().getColor(R.color.yellow));
                veryHighAlertsButton.setBackground(getResources().getDrawable(R.drawable.blue_section));
                showAlerts();
            }
        });

        okToShow = true;
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
                    onCall = true;
                    showLoading();
                    getAlertsList(date);
                }
            }
        });

        //set up confirm delete window
        confirmDeleteSection = (RelativeLayout) view.findViewById(R.id.confirm_delete_section);
        confirmDeleteInnerSection = (RelativeLayout) view.findViewById(R.id.confirm_delete_inner_section);
        deleteNo = (TextView) view.findViewById(R.id.confirm_no);
        deleteYes = (TextView) view.findViewById(R.id.confirm_yes);

        //set animations and timer
        messageAnimationReveal = AnimationUtils.loadAnimation(getContext(), R.anim.fadein_once);
        messageAnimationGone = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout_once);
        timer = new Handler();

        recyclerViewAlerts = (RecyclerView) view.findViewById(R.id.recycler_view_alerts);
        recyclerViewAlerts.setHasFixedSize(true);
        if(DataHolder.getInstance().getAlertsList()!=null){
            alertsList = DataHolder.getInstance().getAlertsList();
        }

        if(!onCall){
            onCall = true;
            showLoading();
            getAlertsList(date);
        }

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewSosAlerts = new GridLayoutManager(getContext(), 4);
        recyclerViewAlerts.setLayoutManager(mLrecyclerViewSosAlerts);
        alertsAdapter = new AlertsAdapter(alertsList);

        recyclerViewAlerts.setAdapter(alertsAdapter);

        /**
         *  set repeated process, every 15 sec. Get alerts
         */
        r = new Runnable() {
            public void run() {
                if(!onCall){
                    if(okToShow){
                        if(isToday){
                            onCall = true;
                            showLoading();
                            getAlertsList(date);
                        }
                    }

                }
                handler.postDelayed(this, RECEIVE_ALERTS_DB_DASHBOARD_EVERY);
            }
        };

        handler.postDelayed(r, RECEIVE_ALERTS_DB_DASHBOARD_EVERY);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(r);
        okToShow = false;
    }

    @Override
    public void onStop(){
        super.onStop();
        okToShow = false;
    }

    private void getAlertsList(String date){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showLoading();
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=100000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                        "&custom_post=alerts&custom_meta={\"date_split_alert\": \""+date+"\"}";
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
                                AlertObjectCMS.Root alertsResponse = new Gson().fromJson(myResponse, AlertObjectCMS.Root.class);
                                if(alertsResponse.respond.equals("1")) {
                                    ArrayList<AlertObject> alerts = new ArrayList();
                                    for (int i = 0; i < alertsResponse.result.size(); i++) {
                                        alerts.add(new AlertObject(alertsResponse.result.get(i).ID,alertsResponse.result.get(i).postmeta.worker_name,
                                                alertsResponse.result.get(i).post_title,
                                                alertsResponse.result.get(i).postmeta.risk_grading,alertsResponse.result.get(i).postmeta.date_split_alert,
                                                alertsResponse.result.get(i).postmeta.timestamp));
                                    }
                                    DataHolder.getInstance().setAlertsList(alerts);
                                    alertsList = alerts;
                                    showAlerts();
                                    hideLoading();
                                    onCall = false;
                                }else{
                                    ArrayList<AlertObject> alerts = new ArrayList();
                                    DataHolder.getInstance().setAlertsList(alerts);
                                    alertsList = alerts;
                                    showNoAlerts();
                                    onCall = false;
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
        });

    }

    private void showNoAlerts(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            if(alertsList.size()==0){
                                noAlerts.setVisibility(View.VISIBLE);
                                recyclerViewAlerts.setVisibility(View.GONE);
                            }else {
                                noAlerts.setVisibility(View.GONE);
                                recyclerViewAlerts.setVisibility(View.VISIBLE);
                            }
                            ArrayList subList = new ArrayList<>(alertsList);
                            alertsAdapter.setAdapterList(subList);
                            if(okToShow) {
                                String allAl = getString(R.string.all_alerts) + " 0";
                                allAlerts.setText(allAl);

                                String highAl = getString(R.string.high_risk_level) + " 0";
                                highAlerts.setText(highAl);

                                String lowAl = getString(R.string.low_risk_level) + " 0";
                                lowAlerts.setText(lowAl);

                                String veryLowAl = getString(R.string.very_low_risk_level) + " 0";
                                veryLowAlerts.setText(veryLowAl);

                                String veryHighAl = getString(R.string.very_high_risk_level) + " 0";
                                veryHighAlerts.setText(veryHighAl);

                                String mediumAl = getString(R.string.medium_risk_level) + " 0";
                                mediumAlerts.setText(mediumAl);
                            }
                        }

                    }, 100);
        }

    }

    private void showAlerts(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {

                            ArrayList<AlertObject> subList;

                            if(activeFilterButton.equals("HIGH")){
                                subList = new ArrayList<>(getHighLevelAlerts(alertsList));
                            }else if(activeFilterButton.equals("VERY HIGH")){
                                subList = new ArrayList<>(getVeryHighLevelAlerts(alertsList));
                            }else if(activeFilterButton.equals("MEDIUM")){
                                subList = new ArrayList<>(getMediumLevelAlerts(alertsList));
                            }else if(activeFilterButton.equals("LOW")){
                                subList = new ArrayList<>(getLowLevelAlerts(alertsList));
                            }else if(activeFilterButton.equals("VERY LOW")){
                                subList = new ArrayList<>(getVeryLowLevelAlerts(alertsList));
                            }else{
                                subList = new ArrayList<>(alertsList);
                            }
                            alertsAdapter.setAdapterList(subList);
                            if(subList.size()==0){
                                noAlerts.setVisibility(View.VISIBLE);
                                recyclerViewAlerts.setVisibility(View.GONE);
                            }else {
                                noAlerts.setVisibility(View.GONE);
                                recyclerViewAlerts.setVisibility(View.VISIBLE);
                            }

                            if(okToShow){
                                String allAl = getString(R.string.all_alerts) + " "+alertsList.size();
                                allAlerts.setText(allAl);

                                String highAl = getString(R.string.high_risk_level) + " "+ getHighLevel(alertsList);
                                highAlerts.setText(highAl);

                                String lowAl = getString(R.string.low_risk_level) + " "+ getLowLevel(alertsList);
                                lowAlerts.setText(lowAl);

                                String veryLowAl = getString(R.string.very_low_risk_level) + " "+ getVeryLowLevel(alertsList);
                                veryLowAlerts.setText(veryLowAl);

                                String veryHighAl = getString(R.string.very_high_risk_level) + " "+ getVeryHighLevel(alertsList);
                                veryHighAlerts.setText(veryHighAl);

                                String mediumAl = getString(R.string.medium_risk_level) + " "+ getMediumLevel(alertsList);
                                mediumAlerts.setText(mediumAl);
                            }


                        }
                    }, 100);
        }
    }

    private Integer getVeryHighLevel(ArrayList<AlertObject> list){
        int n = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getRiskGrading().equalsIgnoreCase("very high")){
                n++;
            }
        }
        return n;
    }

    private Integer getHighLevel(ArrayList<AlertObject> list){
        int n = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getRiskGrading().equalsIgnoreCase("high")){
                n++;
            }
        }
        return n;
    }

    private Integer getMediumLevel(ArrayList<AlertObject> list){
        int n = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getRiskGrading().equalsIgnoreCase("medium")){
                n++;
            }
        }
        return n;
    }

    private Integer getLowLevel(ArrayList<AlertObject> list){
        int n = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getRiskGrading().equalsIgnoreCase("low")){
                n++;
            }
        }
        return n;
    }

    private Integer getVeryLowLevel(ArrayList<AlertObject> list){
        int n = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getRiskGrading().equalsIgnoreCase("very low")){
                n++;
            }
        }
        return n;
    }

    private ArrayList<AlertObject> getVeryHighLevelAlerts(ArrayList<AlertObject> list){
        ArrayList<AlertObject> copyList = new ArrayList<>(list);
        ArrayList<AlertObject> filterList = new ArrayList<>();
        for(int i=0;i<copyList.size();i++){
            if(copyList.get(i).getRiskGrading().equalsIgnoreCase("very high")){
                filterList.add(copyList.get(i));
            }
        }
        return filterList;
    }

    private ArrayList<AlertObject> getHighLevelAlerts(ArrayList<AlertObject> list){
        ArrayList<AlertObject> copyList = new ArrayList<>(list);
        ArrayList<AlertObject> filterList = new ArrayList<>();
        for(int i=0;i<copyList.size();i++){
            if(copyList.get(i).getRiskGrading().equalsIgnoreCase("high")){
                filterList.add(copyList.get(i));
            }
        }
        return filterList;
    }

    private ArrayList<AlertObject> getMediumLevelAlerts(ArrayList<AlertObject> list){
        ArrayList<AlertObject> copyList = new ArrayList<>(list);
        ArrayList<AlertObject> filterList = new ArrayList<>();
        for(int i=0;i<copyList.size();i++){
            if(copyList.get(i).getRiskGrading().equalsIgnoreCase("medium")){
                filterList.add(copyList.get(i));
            }
        }
        return filterList;
    }

    private ArrayList<AlertObject> getLowLevelAlerts(ArrayList<AlertObject> list){
        ArrayList<AlertObject> copyList = new ArrayList<>(list);
        ArrayList<AlertObject> filterList = new ArrayList<>();
        for(int i=0;i<copyList.size();i++){
            if(copyList.get(i).getRiskGrading().equalsIgnoreCase("low")){
                filterList.add(copyList.get(i));
            }
        }
        return filterList;
    }

    private ArrayList<AlertObject> getVeryLowLevelAlerts(ArrayList<AlertObject> list){
        ArrayList<AlertObject> copyList = new ArrayList<>(list);
        ArrayList<AlertObject> filterList = new ArrayList<>();
        for(int i=0;i<copyList.size();i++){
            if(copyList.get(i).getRiskGrading().equalsIgnoreCase("very low")){
                filterList.add(copyList.get(i));
            }
        }
        return filterList;
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
            getAlertsList(date);
        }
    }
}