package com.dotsoft.smartsoniadashboard.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.dotsoft.smartsoniadashboard.adapters.RecommendationsAdapter;
import com.dotsoft.smartsoniadashboard.objects.AlertObject;
import com.dotsoft.smartsoniadashboard.objects.RecommendationObject;
import com.dotsoft.smartsoniadashboard.objects.cms.AlertObjectCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.RecommendationObjectCMS;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Recommendations extends Fragment {
    private final static String TAG = "RECOMMENDATIONS";
    private ArrayList<RecommendationObject> recommendationsList = new ArrayList<>();
    private final int RECEIVE_RECOMMENDATIONS_DB_DASHBOARD_EVERY = 1000 * 15; // 15 sec
    private ProgressBar loading;
    private ImageView refresh;
    private boolean onCall = false;
    private boolean okToShow = true;
    private Runnable r;
    private final Handler handler = new Handler();
    private RecyclerView recyclerViewRecommendations;
    private RecommendationsAdapter recommendationsAdapter;
    private String date;
    private boolean isToday = true;
    private TextView noRecommendations;

    public Recommendations() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommendations, container, false);
        date = ((Dashboard) (getActivity())).getSelectedDate();
        isToday = ((Dashboard) (getActivity())).isToday();


        noRecommendations = (TextView) view.findViewById(R.id.no_recommendations);
        noRecommendations.setVisibility(View.GONE);

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
                    getRecommendationsList(date);
                }
            }
        });

        recommendationsList = new ArrayList<>();
        recyclerViewRecommendations = (RecyclerView) view.findViewById(R.id.recycler_view_recommendations);
        if(DataHolder.getInstance().getRecommendationsList()!=null){
            recommendationsList = DataHolder.getInstance().getRecommendationsList();
        }

        if(!onCall){
            onCall = true;
            showLoading();
            getRecommendationsList(date);
        }

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewSosAlerts = new GridLayoutManager(getContext(), 4);
        recyclerViewRecommendations.setLayoutManager(mLrecyclerViewSosAlerts);
        recommendationsAdapter = new RecommendationsAdapter(recommendationsList);
        recyclerViewRecommendations.setAdapter(recommendationsAdapter);

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
                            getRecommendationsList(date);
                        }
                    }

                }
                handler.postDelayed(this, RECEIVE_RECOMMENDATIONS_DB_DASHBOARD_EVERY);
            }
        };

        handler.postDelayed(r, RECEIVE_RECOMMENDATIONS_DB_DASHBOARD_EVERY);

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

    private void getRecommendationsList(String date){
        showLoading();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=100000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&custom_post=recommendations&custom_meta={\"date_split_recommendation\": \""+date+"\"}";
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
                        RecommendationObjectCMS.Root recommendationsResponse = new Gson().fromJson(myResponse, RecommendationObjectCMS.Root.class);
                        if(recommendationsResponse.respond.equals("1")) {
                            ArrayList<RecommendationObject> recommendations = new ArrayList();
                            for (int i = 0; i < recommendationsResponse.result.size(); i++) {
                                recommendations.add(new RecommendationObject(recommendationsResponse.result.get(i).ID, recommendationsResponse.result.get(i).postmeta.worker_name,
                                        recommendationsResponse.result.get(i).post_title, recommendationsResponse.result.get(i).postmeta.read,
                                        recommendationsResponse.result.get(i).postmeta.date_split_recommendation, recommendationsResponse.result.get(i).postmeta.timestamp));
                            }
                            DataHolder.getInstance().setRecommendationsList(recommendations);
                            recommendationsList = recommendations;
                            showRecommendations();
                            hideLoading();
                            onCall = false;
                        }else{
                            ArrayList<RecommendationObject> recommendations = new ArrayList();
                            DataHolder.getInstance().setRecommendationsList(recommendations);
                            recommendationsList = recommendations;
                            showNoRecommendations();
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
                    }                }
            }
        });
    }

    private void showNoRecommendations(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            if(recommendationsList.size()==0){
                                noRecommendations.setVisibility(View.VISIBLE);
                                recyclerViewRecommendations.setVisibility(View.GONE);
                            }else {
                                noRecommendations.setVisibility(View.GONE);
                                recyclerViewRecommendations.setVisibility(View.VISIBLE);
                            }
                            ArrayList subList = new ArrayList<>(recommendationsList);
                            recommendationsAdapter.setAdapterList(subList);
                        }

                    }, 100);
        }

    }

    private void showRecommendations(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            ArrayList<RecommendationObject> subList;
                            subList = new ArrayList<>(recommendationsList);
                            recommendationsAdapter.setAdapterList(subList);
                            if(subList.size()==0){
                                noRecommendations.setVisibility(View.VISIBLE);
                                recyclerViewRecommendations.setVisibility(View.GONE);
                            }else {
                                noRecommendations.setVisibility(View.GONE);
                                recyclerViewRecommendations.setVisibility(View.VISIBLE);
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
            getRecommendationsList(date);
        }
    }
}