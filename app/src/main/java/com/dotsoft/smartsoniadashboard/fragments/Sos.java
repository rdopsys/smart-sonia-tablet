package com.dotsoft.smartsoniadashboard.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.SosAlertsAdapter;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;
import com.dotsoft.smartsoniadashboard.objects.cms.SosCMS;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Sos extends Fragment implements SosAlertsAdapter.HandleEventClick {
    private final static String TAG = "SOS";
    private ArrayList<SosAlert> sosAlertsList = new ArrayList<>();
    private final int RECEIVE_ALERTS_DB_DASHBOARD_EVERY = 1000 * 15; // 15 sec
    private RecyclerView recyclerViewSosAlerts;
    private SosAlertsAdapter sosAlertsAdapter;
    private RelativeLayout recyclerViewSosAlertsSection, confirmDeleteSection,confirmDeleteInnerSection;
    private ProgressBar loading;
    private ImageView refresh;
    private boolean onCall = false;
    private TextView noSosAlerts,deleteNo,deleteYes;
    private Animation messageAnimationReveal, messageAnimationGone;
    private boolean okToShow = true;
    private Runnable r,runnable;
    private final Handler handler = new Handler();
    private Handler timer;

    public Sos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sos, container, false);
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
                    getSosAlertsList();
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

        // check if sos alerts have been loaded
        noSosAlerts = (TextView) view.findViewById(R.id.no_sos_alert);
        noSosAlerts.setVisibility(View.GONE);
        recyclerViewSosAlertsSection = (RelativeLayout) view.findViewById(R.id.recycler_view_sos_alerts_section);
        recyclerViewSosAlerts = (RecyclerView) view.findViewById(R.id.recycler_view_sos_alerts);
        recyclerViewSosAlertsSection.setVisibility(View.GONE);
        if(DataHolder.getInstance().getSosAlertsList()!=null){
            sosAlertsList = DataHolder.getInstance().getSosAlertsList();
            recyclerViewSosAlertsSection.setVisibility(View.VISIBLE);
            if(sosAlertsList.size()==0){
                noSosAlerts.setVisibility(View.VISIBLE);
            }else {
                noSosAlerts.setVisibility(View.GONE);
            }
        }
        // Load sos alerts
        getSosAlertsList();
        ArrayList<SosAlert> test = new ArrayList<>();

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewSosAlerts = new GridLayoutManager(getContext(), 1);
        recyclerViewSosAlerts.setLayoutManager(mLrecyclerViewSosAlerts);
        sosAlertsAdapter = new SosAlertsAdapter(test,this);
        recyclerViewSosAlerts.setAdapter(sosAlertsAdapter);

        /**
         *  set repeated process, every 15 sec. Get sos data
         */
        r = new Runnable() {
            public void run() {
                if(!onCall){
                    // Load sos alerts
                    getSosAlertsList();
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
        okToShow = false;
        handler.removeCallbacks(r);
    }

    @Override
    public void onStop(){
        super.onStop();
        okToShow = false;
    }

    private void getSosAlertsList(){
        noSosAlerts.setVisibility(View.GONE);
        onCall = true;
        showLoading();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&siteid=&perpage=100000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=sos";
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
                        SosCMS.Root sosAlertsResponse = new Gson().fromJson(myResponse,SosCMS.Root.class);
                        ArrayList<SosAlert> sosAlerts = new ArrayList();
                        if(sosAlertsResponse.respond.equals("1")){
                            for(int i=0;i<sosAlertsResponse.result.size();i++){
                                sosAlerts.add(new SosAlert(sosAlertsResponse.result.get(i).ID, sosAlertsResponse.result.get(i).post_title,
                                        sosAlertsResponse.result.get(i).custom_fields.timestamp, sosAlertsResponse.result.get(i).custom_fields.location));
                            }
                        }else{
                            showNoSosAlerts();
                        }
                        DataHolder.getInstance().setSosAlertsList(sosAlerts);
                        sosAlertsList = sosAlerts;
                        hideLoading();
                        showSosAlerts();
                    }
                }else{
                    hideLoading();
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                onCall = false;
            }
        });
    }

    private void showSosAlerts(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            ArrayList<SosAlert> subList = new ArrayList<>(sosAlertsList);
                            sosAlertsAdapter.setAdapterList(subList);
                            if(recyclerViewSosAlertsSection.getVisibility()==View.GONE){
                                recyclerViewSosAlertsSection.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 100);
        }
    }

    private void showNoSosAlerts(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            noSosAlerts.setVisibility(View.VISIBLE);
                            ArrayList subList = new ArrayList<>(sosAlertsList);
                            sosAlertsAdapter.setAdapterList(subList);
                            if(recyclerViewSosAlertsSection.getVisibility()==View.GONE){
                                recyclerViewSosAlertsSection.setVisibility(View.VISIBLE);
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
                            refresh.setVisibility(View.VISIBLE);
                        }
                    }, 100);
        }

    }

    private void deleteSosAlert(String id){
        showLoading();
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/custom_service/?service=delete_by_id&postid="+id+"&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq";
        Request request = new Request.Builder()
                .url(registerUrl)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                onCall = false;
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
                if (response.isSuccessful()) {
                    if(okToShow){
                        // we need to refresh the sos results
                        int tmp = sosAlertsList.size() - 1;
                        try {
                            ((Dashboard) getActivity()).setSosResults(tmp);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        sosAlertsList = new ArrayList<>();
                        getSosAlertsList();
                        try {
                            ((Dashboard) getActivity()).hideWaitMessage();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    hideLoading();
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                onCall = false;
            }
        });
    }

    @Override
    public void itemClickDeleteSosAlert(String id) {
        if(!onCall){
            confirmDeleteSosAlert(id);
        }
    }

    private void confirmDeleteSosAlert(String id){
        confirmDeleteSection.setVisibility(View.VISIBLE);
        confirmDeleteInnerSection.startAnimation(messageAnimationReveal);
        confirmDeleteInnerSection.setVisibility(View.VISIBLE);

        deleteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteInnerSection.startAnimation(messageAnimationGone);
                confirmDeleteInnerSection.setVisibility(View.GONE);

                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    confirmDeleteSection.setVisibility(View.GONE);
                }, 200);

            }
        });


        deleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete sos alert
                deleteSosAlert(id);
                confirmDeleteInnerSection.startAnimation(messageAnimationGone);
                confirmDeleteInnerSection.setVisibility(View.GONE);

                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    confirmDeleteSection.setVisibility(View.GONE);
                }, 200);

            }
        });
    }

    @Override
    public void itemClickLocationSosAlert(String latLog) {

        // opening location in google maps
        String ulrString = "http://maps.google.com/maps?q=loc:" + latLog;
        Uri gmmIntentUri = Uri.parse(ulrString);
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                try {
                    ((Dashboard) getActivity()).showErrorNavigation();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

}