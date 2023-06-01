package com.dotsoft.smartsoniadashboard.fragments;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.AccidentsAdapter;
import com.dotsoft.smartsoniadashboard.adapters.AccidentsCalendarAdapter;
import com.dotsoft.smartsoniadashboard.adapters.AlertsCalendarAdapter;
import com.dotsoft.smartsoniadashboard.adapters.DaysAdapter;
import com.dotsoft.smartsoniadashboard.adapters.DetailAlertsAdapter;
import com.dotsoft.smartsoniadashboard.adapters.DetailHeartRateAdapter;
import com.dotsoft.smartsoniadashboard.adapters.DetailMessagesAdapter;
import com.dotsoft.smartsoniadashboard.adapters.DetailRecommendationsAdapter;
import com.dotsoft.smartsoniadashboard.adapters.DetailSosAdapter;
import com.dotsoft.smartsoniadashboard.adapters.SosAlertsCalendarAdapter;
import com.dotsoft.smartsoniadashboard.objects.AccidentObject;
import com.dotsoft.smartsoniadashboard.objects.AlertObject;
import com.dotsoft.smartsoniadashboard.objects.CalendarDay;
import com.dotsoft.smartsoniadashboard.objects.MessageObject;
import com.dotsoft.smartsoniadashboard.objects.RecommendationObject;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.Worker;
import com.dotsoft.smartsoniadashboard.objects.WorkerDataEntry;
import com.dotsoft.smartsoniadashboard.objects.cms.AccidentsObjectCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.AlertObjectCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.MessagesObjectCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.RecommendationObjectCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.SosCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.UserCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.WorkerDataCMS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class History extends Fragment implements DaysAdapter.HandleEventClick, AccidentsCalendarAdapter.HandleEventClick, SosAlertsCalendarAdapter.HandleEventClick, DetailSosAdapter.HandleEventClick{
    private final static String TAG = "HISTORY";
    private RecyclerView recyclerViewCalendar,recyclerViewSosDialog,recyclerViewAlertsDialog,recyclerViewAccidentsDialog;
    private DaysAdapter daysAdapter;
    private AlertsCalendarAdapter alertsAdapter;
    private AccidentsCalendarAdapter accidentsAdapter;
    private SosAlertsCalendarAdapter sosAlertsAdapter;
    private RelativeLayout recyclerViewCalendarSection,dialogSection,sosInnerSection,alertsInnerSection,accidentsInnerSection;
    private ScrollView dialogInnerSection;
    private ProgressBar loading;
    private ImageView refresh,previousMonthIc, nextMonthIc,closeDialog;
    private boolean onCall = false;
    private boolean okToShow = true;
    private Instant today;
    private String activeMonth, currentDay,currentMonth,currentMonthYear,selectedMonthYear;
    private TextView monthYearDisplayed;
    private ArrayList<CalendarDay> daysList = new ArrayList<>();
    private ArrayList<SosAlert> sosAlertsList = new ArrayList<>();
    private ArrayList<AlertObject> alertsList = new ArrayList<>();
    private ArrayList<AccidentObject> accidentsList = new ArrayList<>();
    private Animation messageAnimationReveal, messageAnimationGone;
    private Handler timer;
    private Runnable runnable;
    private boolean onCall1 = false;
    private ArrayList<AlertObject> accidentDetailsAlerts = new ArrayList<>();
    private ArrayList<MessageObject> accidentDetailsMessages = new ArrayList<>();
    private ArrayList<WorkerDataEntry> accidentDetailsData  = new ArrayList<>();
    private ArrayList<RecommendationObject> accidentDetailsRecommendations = new ArrayList<>();
    private ArrayList<SosAlert> accidentDetailsSos = new ArrayList<>();
    private ArrayList<Worker> accidentDetailsWorker = new ArrayList<>();
    private ArrayList<AlertObject> accidentDetailsAlertsCopy = new ArrayList<>();
    private ArrayList<MessageObject> accidentDetailsMessagesCopy = new ArrayList<>();
    private ArrayList<RecommendationObject> accidentDetailsRecommendationsCopy = new ArrayList<>();
    private ArrayList<SosAlert> accidentDetailsSosCopy = new ArrayList<>();
    private ArrayList<WorkerDataEntry> accidentDetailsDataCopy = new ArrayList<>();
    private ArrayList<WorkerDataEntry> accidentDetailsDataCopyTrim = new ArrayList<>();
    private RecyclerView recyclerViewDetailsSos, recyclerViewDetailsAlerts, recyclerViewDetailsMessages, recyclerViewDetailsRecommendations, recyclerViewDetailsHeartRate;
    private ImageView closeDetails;
    private TextView detailTitleVal1, detailTitleVal2, detailsAlertsText, detailsRecommendationsText, detailsMessagesText, detailsSosText, detailsHeartRateText, detailsTitle, detailsNote, detailsWorker, detailsTime, detailsZone, detailsNoData;
    private RelativeLayout detailsBackground, detailsAlerts, detailsRecommendations, detailsMessages, detailsSos, detailsHeartRate;
    private ScrollView detailsWindow;
    private DetailAlertsAdapter detailAlertsAdapter;
    private DetailHeartRateAdapter detailHeartRateAdapter;
    private DetailRecommendationsAdapter detailRecommendationsAdapter;
    private DetailMessagesAdapter detailMessagesAdapter;
    private DetailSosAdapter detailSosAdapter;
    private LinearLayout infoDetailTitleSection;
    private int newN = 40;
    private int heartRateAdded = 0;
    private int messagesAdded = 0;
    private int alertsAdded = 0;
    private int recommendationsAdded = 0;
    private int sosAdded = 0;
    private int selectedCategory = 1;

    public History() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

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
                    if(okToShow){
                        showLoading();
                        getSosAlertsList();
                    }
                }
            }
        });

        sosAlertsList = new ArrayList<>();
        alertsList = new ArrayList<>();
        accidentsList = new ArrayList<>();


        // check if calendar have been loaded
        recyclerViewCalendarSection = (RelativeLayout) view.findViewById(R.id.recycler_view_calendar_section);
        recyclerViewCalendar = (RecyclerView) view.findViewById(R.id.recycler_view_calendar);
        recyclerViewCalendarSection.setVisibility(View.GONE);

        //set days recycler view
        RecyclerView.LayoutManager mLrecyclerViewSosAlerts = new GridLayoutManager(getContext(), 7);
        recyclerViewCalendar.setLayoutManager(mLrecyclerViewSosAlerts);
        daysAdapter = new DaysAdapter(daysList,getContext(),this);
        recyclerViewCalendar.setAdapter(daysAdapter);
        recyclerViewCalendarSection.setVisibility(View.GONE);

        //set up calendar
        monthYearDisplayed = (TextView) view.findViewById(R.id.month_text);
        if(okToShow){
            loadCurrentMonth();
        }

        if(okToShow){
            showLoading();
            getSosAlertsList();
        }

        previousMonthIc = (ImageView) view.findViewById(R.id.month_prev);
        previousMonthIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] currentMonthYearSpit = selectedMonthYear.split("-",2);
                String extractedMonth =  currentMonthYearSpit[0];
                String extractedYear =  currentMonthYearSpit[1];
                int extractedMonthInt = Integer.parseInt(extractedMonth);
                int extractedYearInt = Integer.parseInt(extractedYear);
                extractedMonthInt--;
                if(extractedMonthInt<1){
                    extractedMonthInt=12;
                    extractedYearInt--;
                }
                if(extractedMonthInt<10){
                    extractedMonth = "0"+extractedMonthInt;
                }else {
                    extractedMonth = ""+extractedMonthInt;
                }
                selectedMonthYear = extractedMonth + "-" + extractedYearInt;
                loadMonth(extractedYearInt + "-" + extractedMonth + "-"+"01");
            }
        });

        nextMonthIc = (ImageView) view.findViewById(R.id.month_next);
        nextMonthIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currentMonthYear.equals(selectedMonthYear)) {
                    String[] currentMonthYearSpit = selectedMonthYear.split("-", 2);
                    String extractedMonth = currentMonthYearSpit[0];
                    String extractedYear = currentMonthYearSpit[1];
                    int extractedMonthInt = Integer.parseInt(extractedMonth);
                    int extractedYearInt = Integer.parseInt(extractedYear);
                    extractedMonthInt++;
                    if (extractedMonthInt > 12) {
                        extractedMonthInt = 1;
                        extractedYearInt++;
                    }
                    if (extractedMonthInt < 10) {
                        extractedMonth = "0" + extractedMonthInt;
                    } else {
                        extractedMonth = "" + extractedMonthInt;
                    }
                    selectedMonthYear = extractedMonth + "-" + extractedYearInt;
                    if(currentMonthYear.equals(selectedMonthYear)) {
                        loadCurrentMonth();
                    }else{
                        loadMonth(extractedYearInt + "-" + extractedMonth + "-" + "01");
                    }
                }
            }
        });

        //set up dialog window
        dialogSection = (RelativeLayout) view.findViewById(R.id.calendar_dialog_section);
        dialogInnerSection = (ScrollView) view.findViewById(R.id.calendar_dialog_section_inner);
        sosInnerSection = (RelativeLayout) view.findViewById(R.id.recycler_view_sos_alerts_section_calendar);
        recyclerViewSosDialog = (RecyclerView) view.findViewById(R.id.recycler_view_sos_alerts_calendar);
        alertsInnerSection = (RelativeLayout) view.findViewById(R.id.recycler_view_alerts_section_calendar);
        recyclerViewAlertsDialog = (RecyclerView) view.findViewById(R.id.recycler_view_alerts_calendar);
        accidentsInnerSection = (RelativeLayout) view.findViewById(R.id.recycler_view_accidents_section_calendar);
        recyclerViewAccidentsDialog = (RecyclerView) view.findViewById(R.id.recycler_view_accidents_calendar);
        closeDialog = (ImageView) view.findViewById(R.id.close_calendar_dialog_section);

        dialogSection.setVisibility(View.GONE);
        dialogInnerSection.setVisibility(View.GONE);
        sosInnerSection.setVisibility(View.GONE);
        sosInnerSection.setVisibility(View.GONE);
        alertsInnerSection.setVisibility(View.GONE);
        accidentsInnerSection.setVisibility(View.GONE);
        recyclerViewSosDialog.setVisibility(View.GONE);
        recyclerViewAlertsDialog.setVisibility(View.GONE);
        recyclerViewAccidentsDialog.setVisibility(View.GONE);
        closeDialog.setVisibility(View.GONE);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sosInnerSection.setVisibility(View.GONE);
                alertsInnerSection.setVisibility(View.GONE);
                accidentsInnerSection.setVisibility(View.GONE);
                dialogInnerSection.startAnimation(messageAnimationGone);
                dialogInnerSection.setVisibility(View.GONE);
                closeDialog.setVisibility(View.GONE);

                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    dialogSection.setVisibility(View.GONE);
                }, 200);
            }
        });

        //set animations and timer
        messageAnimationReveal = AnimationUtils.loadAnimation(getContext(), R.anim.fadein_once);
        messageAnimationGone = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout_once);
        timer = new Handler();

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewSosCalendarAlerts = new GridLayoutManager(getContext(), 1);
        recyclerViewSosDialog.setLayoutManager(mLrecyclerViewSosCalendarAlerts);
        sosAlertsAdapter = new SosAlertsCalendarAdapter(sosAlertsList,this);
        recyclerViewSosDialog.setAdapter(sosAlertsAdapter);

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewAlerts = new GridLayoutManager(getContext(), 3);
        recyclerViewAlertsDialog.setLayoutManager(mLrecyclerViewAlerts);
        alertsAdapter = new AlertsCalendarAdapter(alertsList);
        recyclerViewAlertsDialog.setAdapter(alertsAdapter);

        //set sos alerts recycler view
        RecyclerView.LayoutManager mLrecyclerViewAccidents = new GridLayoutManager(getContext(), 3);
        recyclerViewAccidentsDialog.setLayoutManager(mLrecyclerViewAccidents);
        accidentsAdapter = new AccidentsCalendarAdapter(accidentsList,this);
        recyclerViewAccidentsDialog.setAdapter(accidentsAdapter);

        // for accident details
        detailsBackground = (RelativeLayout) view.findViewById(R.id.background_accident_details_form_section);
        detailsWindow = (ScrollView) view.findViewById(R.id.accident_details_section);

        detailsAlertsText = (TextView) view.findViewById(R.id.alerts_filter_text);
        detailsRecommendationsText = (TextView) view.findViewById(R.id.recommendations_filter_text);
        detailsMessagesText = (TextView) view.findViewById(R.id.messages_filter_text);
        detailsSosText = (TextView) view.findViewById(R.id.sos_filter_text);
        detailsHeartRateText = (TextView) view.findViewById(R.id.heart_rate_filter_text);
        infoDetailTitleSection = (LinearLayout) view.findViewById(R.id.info_detail_title_section);
        detailTitleVal1 = (TextView) view.findViewById(R.id.info_detail_val1);
        detailTitleVal2 = (TextView) view.findViewById(R.id.info_detail_val2);

        detailsAlerts = (RelativeLayout) view.findViewById(R.id.alerts_filter_button);
        detailsAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                showDetailsAlerts();
                            }
                        }, 100);
            }
        });
        detailsRecommendations = (RelativeLayout) view.findViewById(R.id.recommendations_filter_button);
        detailsRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                showDetailsRecommendations();
                            }
                        }, 100);
            }
        });
        detailsMessages = (RelativeLayout) view.findViewById(R.id.messages_filter_button);
        detailsMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                showDetailsMessages();
                            }
                        }, 100);
            }
        });
        detailsSos = (RelativeLayout) view.findViewById(R.id.sos_filter_button);
        detailsSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                showDetailsSos();
                            }
                        }, 100);
            }
        });
        detailsHeartRate = (RelativeLayout) view.findViewById(R.id.heart_rate_filter_button);
        detailsHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                showDetailsHeartRate();
                            }
                        }, 100);
            }
        });

        recyclerViewDetailsSos = (RecyclerView) view.findViewById(R.id.recycler_view_details_sos);
        recyclerViewDetailsAlerts = (RecyclerView) view.findViewById(R.id.recycler_view_details_alerts);
        recyclerViewDetailsRecommendations = (RecyclerView) view.findViewById(R.id.recycler_view_details_recommendations);
        recyclerViewDetailsHeartRate = (RecyclerView) view.findViewById(R.id.recycler_view_details_heart_rate);
        recyclerViewDetailsMessages = (RecyclerView) view.findViewById(R.id.recycler_view_details_messages);

        closeDetails = (ImageView) view.findViewById(R.id.close_details);
        closeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsWindow.startAnimation(messageAnimationGone);
                detailsWindow.setVisibility(View.GONE);
                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    detailsBackground.setVisibility(View.GONE);
                }, 200);
            }
        });

        detailsTitle = (TextView) view.findViewById(R.id.accident_details_title);
        detailsNote = (TextView)  view.findViewById(R.id.accident_details_note);
        detailsWorker = (TextView)  view.findViewById(R.id.ac_name_value);
        detailsTime = (TextView)  view.findViewById(R.id.ac_name_time);
        detailsZone = (TextView)  view.findViewById(R.id.ac_name_zone);
        detailsNoData = (TextView)  view.findViewById(R.id.no_data);

        RecyclerView.LayoutManager mLrecyclerViewDetailsM = new GridLayoutManager(getContext(), 1);
        recyclerViewDetailsMessages.setLayoutManager(mLrecyclerViewDetailsM);
        detailMessagesAdapter = new DetailMessagesAdapter(accidentDetailsMessages);
        recyclerViewDetailsMessages.setAdapter(detailMessagesAdapter);

        RecyclerView.LayoutManager mLrecyclerViewDetailsS = new GridLayoutManager(getContext(), 1);
        recyclerViewDetailsSos.setLayoutManager(mLrecyclerViewDetailsS);
        detailSosAdapter = new DetailSosAdapter(accidentDetailsSos,this);
        recyclerViewDetailsSos.setAdapter(detailSosAdapter);

        RecyclerView.LayoutManager mLrecyclerViewDetailsR = new GridLayoutManager(getContext(), 1);
        recyclerViewDetailsRecommendations.setLayoutManager(mLrecyclerViewDetailsR);
        detailRecommendationsAdapter = new DetailRecommendationsAdapter(accidentDetailsRecommendations);
        recyclerViewDetailsRecommendations.setAdapter(detailRecommendationsAdapter);

        RecyclerView.LayoutManager mLrecyclerViewDetailsH = new GridLayoutManager(getContext(), 1);
        recyclerViewDetailsHeartRate.setLayoutManager(mLrecyclerViewDetailsH);
        detailHeartRateAdapter = new DetailHeartRateAdapter(accidentDetailsData);
        recyclerViewDetailsHeartRate.setAdapter(detailHeartRateAdapter);

        RecyclerView.LayoutManager mLrecyclerViewDetailsA = new GridLayoutManager(getContext(), 1);
        recyclerViewDetailsAlerts.setLayoutManager(mLrecyclerViewDetailsA);
        detailAlertsAdapter = new DetailAlertsAdapter(accidentDetailsAlerts);
        recyclerViewDetailsAlerts.setAdapter(detailAlertsAdapter);

        selectedCategory=1;

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.accident_details_section);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!scrollView.canScrollVertically(1)) {
                    if(okToShow){
                        addItems();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        okToShow = false;
    }

    private void loadMonth(String date){
        if(okToShow){
            DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd")
                    .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
                    .toFormatter()
                    .withZone(ZoneId.of("Europe/Paris"));
            today = FMT.parse(date, Instant::from);
            int monthFirstDayValue = today.atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();
            today = today.plus(1, ChronoUnit.DAYS);
            String dayStr = today.toString();
            String [] spitTimestamp = dayStr.split("T",2);
            String [] spitDate = spitTimestamp[0].split("-");
            currentMonth = spitDate[1];
            String day = spitDate[2];
            String monthYear = today.atZone(ZoneId.systemDefault()).getMonth().name() + " " + today.atZone(ZoneId.systemDefault()).getYear();
            monthYearDisplayed.setText(monthYear);
            daysList = new ArrayList<>();
            String month = currentMonth;
            while (currentMonth.equals(month)){
                daysList.add(new CalendarDay(day,month,spitDate[0],true,0,0,0));
                Instant dayIn = today.plus(1, ChronoUnit.DAYS);
                dayStr = dayIn.toString();
                spitTimestamp = dayStr.split("T",2);
                spitDate = spitTimestamp[0].split("-");
                month = spitDate[1];
                day = spitDate[2];
                today = dayIn;
            }

            while (!(monthFirstDayValue<=1)) {
                daysList.add(0, new CalendarDay(" ", "0", "0", false, 0, 0,0));
                monthFirstDayValue--;
            }
            showMonthData();
        }

    }

    private void loadCurrentMonth(){
        if(okToShow){
            today = Instant.now();
            String dayStr = today.toString();
            String[] spitTimestamp = dayStr.split("T",2);
            String[] spitDay = spitTimestamp[0].split("-");
            currentDay =  spitDay[2];
            currentMonth = spitDay[1];
            currentMonthYear = spitDay[1] + "-" + spitDay[0];
            selectedMonthYear = spitDay[1] + "-" + spitDay[0];
            activeMonth = currentMonth;
            String monthYear = today.atZone(ZoneId.systemDefault()).getMonth().name() + " " + today.atZone(ZoneId.systemDefault()).getYear();
            monthYearDisplayed.setText(monthYear);
            daysList = new ArrayList<>();
            String month = currentMonth;
            String day = currentDay;
            while (currentMonth.equals(month)){
                daysList.add(new CalendarDay(day,month,spitDay[0],true,0,0,0));
                Instant dayIn = today.minus(1, ChronoUnit.DAYS);
                dayStr = dayIn.toString();
                spitTimestamp = dayStr.split("T",2);
                String [] spitDate = spitTimestamp[0].split("-");
                month = spitDate[1];
                day = spitDate[2];
                today = dayIn;
            }
            int monthFirstDayValue = today.plus(1, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();

            Collections.reverse(daysList);

            while (!(monthFirstDayValue<=1)){
                daysList.add(0,new CalendarDay(" ","0","0",false,0,0,0));
                monthFirstDayValue--;
            }
            showMonthData();
        }
    }

    private void showMonthData(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            showLoading();
                            matchSosWithDate();
                            matchAlertsWithDate();
                            matchAccidentsWithDate();
                            ArrayList subList = new ArrayList<>(daysList);
                            daysAdapter.setAdapterList(subList);
                            if(recyclerViewCalendarSection.getVisibility()==View.GONE){
                                recyclerViewCalendarSection.setVisibility(View.VISIBLE);
                            }else {
                                hideLoading();
                            }
                        }
                    }, 100);
        }
    }

    private void getSosAlertsList(){
        if(okToShow){
            onCall = true;
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
                            }
                            sosAlertsList = sosAlerts;
                        }
                    }else{
                        try {
                            ((Dashboard) getActivity()).showErrorLoadingData();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    getAlertsList();
                    onCall = false;
                }
            });
        }

    }

    private void getAlertsList(){
        if(okToShow){
            showLoading();
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=10000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                    "&custom_post=alerts";
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
                            AlertObjectCMS.Root alertsResponse = new Gson().fromJson(myResponse, AlertObjectCMS.Root.class);
                            if(alertsResponse.respond.equals("1")) {
                                ArrayList<AlertObject> alerts = new ArrayList();
                                for (int i = 0; i < alertsResponse.result.size(); i++) {
                                    alerts.add(new AlertObject(alertsResponse.result.get(i).ID,alertsResponse.result.get(i).postmeta.worker_name,
                                            alertsResponse.result.get(i).post_title,
                                            alertsResponse.result.get(i).postmeta.risk_grading,alertsResponse.result.get(i).postmeta.date_split_alert,
                                            alertsResponse.result.get(i).postmeta.timestamp));
                                }
                                alertsList = alerts;
                            }else{
                                ArrayList<AlertObject> alerts = new ArrayList();
                                alertsList = alerts;
                            }
                            onCall = false;
                        }
                    }else{
                        onCall = false;
                        try {
                            ((Dashboard) getActivity()).showErrorLoadingData();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    getAccidentsList();
                }
            });
        }

    }

    private void getAccidentsList(){
        if(okToShow){
            showLoading();
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=10000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                    "&custom_post=accident";
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
                            AccidentsObjectCMS.Root accidentsResponse = new Gson().fromJson(myResponse, AccidentsObjectCMS.Root.class);
                            if(accidentsResponse.respond.equals("1")) {
                                ArrayList<AccidentObject> accidents = new ArrayList();
                                for (int i = 0; i < accidentsResponse.result.size(); i++) {
                                    accidents.add(new AccidentObject(accidentsResponse.result.get(i).ID,accidentsResponse.result.get(i).custom_fields.worker_name,
                                            accidentsResponse.result.get(i).post_title,
                                            accidentsResponse.result.get(i).custom_fields.dangerous_zone, accidentsResponse.result.get(i).custom_fields.date_split_accident,
                                            accidentsResponse.result.get(i).custom_fields.timestamp,accidentsResponse.result.get(i).custom_fields.notes));
                                }
                                accidentsList = accidents;
                            }else{
                                ArrayList<AccidentObject> accidents = new ArrayList();
                                accidentsList = accidents;
                            }
                            onCall = false;
                            showMonthData();
                        }
                    }else{
                        onCall = false;
                        try {
                            ((Dashboard) getActivity()).showErrorLoadingData();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    hideLoading();
                }
            });
        }

    }

    private void matchSosWithDate(){
        String[] test = selectedMonthYear.split("-");
        int yr = Integer.parseInt(test[1]);
        int trimmedYear = yr- 2000;
        String selectedMonthYearNewFormat = test[0] + "-"+ trimmedYear;
        int sosSum;
        for(int i=0;i<daysList.size();i++){
            if(daysList.get(i).isActiveDay()){
                ArrayList<SosAlert> emptySos = new ArrayList<>();
                daysList.get(i).setSosList(emptySos);
                String d = daysList.get(i).getDayNumber() + "-"+ selectedMonthYearNewFormat;
                sosSum=0;
                for(int y=0;y<sosAlertsList.size();y++){
                    String sosD = sosAlertsList.get(y).getDate();
                    if(d.equals(sosD)){
                        sosSum++;
                        daysList.get(i).addSos(sosAlertsList.get(y));
                    }
                }
                daysList.get(i).setSos(sosSum);
            }
        }
    }

    private void matchAlertsWithDate(){
        String[] test = selectedMonthYear.split("-");
        int yr = Integer.parseInt(test[1]);
        int trimmedYear = yr- 2000;
        String selectedMonthYearNewFormat = test[0] + "-"+ trimmedYear;
        int alertsSum;
        for(int i=0;i<daysList.size();i++){
            if(daysList.get(i).isActiveDay()){
                ArrayList<AlertObject> emptyAlerts = new ArrayList<>();
                daysList.get(i).setAlertsList(emptyAlerts);
                String d = daysList.get(i).getDayNumber() + "-"+ selectedMonthYearNewFormat;
                alertsSum=0;
                for(int y=0;y<alertsList.size();y++){
                    String alertD2 = alertsList.get(y).getDate();
                    String [] spitDate = alertD2.split("-");
                    String alertD = spitDate[2] +"-"+spitDate[1] +"-"+spitDate[0];
                    if(d.equals(alertD)){
                        alertsSum++;
                        daysList.get(i).addAlert(alertsList.get(y));
                    }
                }
                daysList.get(i).setAlerts(alertsSum);
            }
        }
    }

    private void matchAccidentsWithDate(){
        String[] test = selectedMonthYear.split("-");
        int yr = Integer.parseInt(test[1]);
        int trimmedYear = yr- 2000;
        String selectedMonthYearNewFormat = test[0] + "-"+ trimmedYear;
        int accidentsSum;
        for(int i=0;i<daysList.size();i++){
            if(daysList.get(i).isActiveDay()){
                ArrayList<AccidentObject> emptyAccidents = new ArrayList<>();
                daysList.get(i).setAccidentsList(emptyAccidents);
                String d = daysList.get(i).getDayNumber() + "-"+ selectedMonthYearNewFormat;
                accidentsSum=0;
                for(int y=0;y<accidentsList.size();y++){
                    String accidentD2 = accidentsList.get(y).getDate();
                    String [] spitDate = accidentD2.split("-");
                    String accidentD = spitDate[2] +"-"+spitDate[1] +"-"+spitDate[0];
                    if(d.equals(accidentD)){
                        accidentsSum++;
                        daysList.get(i).addAccidents(accidentsList.get(y));
                    }
                }
                daysList.get(i).setAccidents(accidentsSum);
            }
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

    @Override
    public void itemClickSosSection(CalendarDay day) {
        if(okToShow){
            ArrayList subList = new ArrayList<>(day.getSosList());
            sosAlertsAdapter.setAdapterList(subList);
            if(recyclerViewSosDialog.getVisibility()==View.GONE){
                recyclerViewSosDialog.setVisibility(View.VISIBLE);
            }
            dialogInnerSection.setBackground(getResources().getDrawable(R.drawable.calendar_green_outline_section_white_back));
            dialogSection.setVisibility(View.VISIBLE);
            dialogInnerSection.startAnimation(messageAnimationReveal);
            dialogInnerSection.setVisibility(View.VISIBLE);
            //show after 200 milsec
            timer.removeCallbacks(runnable);
            timer.postDelayed( runnable = () -> {
                sosInnerSection.setVisibility(View.VISIBLE);
                alertsInnerSection.setVisibility(View.GONE);
                accidentsInnerSection.setVisibility(View.GONE);
                closeDialog.setVisibility(View.VISIBLE);
            }, 200);

        }
    }

    @Override
    public void itemClickAlertSection(CalendarDay day) {
        if(okToShow){
            ArrayList subList = new ArrayList<>(day.getAlertsList());
            alertsAdapter.setAdapterList(subList);
            if(recyclerViewAlertsDialog.getVisibility()==View.GONE){
                recyclerViewAlertsDialog.setVisibility(View.VISIBLE);
            }

            dialogInnerSection.setBackground(getResources().getDrawable(R.drawable.calendar_yellow_outline_section_white_back));
            dialogSection.setVisibility(View.VISIBLE);
            dialogInnerSection.startAnimation(messageAnimationReveal);
            dialogInnerSection.setVisibility(View.VISIBLE);
            //show after 200 milsec
            timer.removeCallbacks(runnable);
            timer.postDelayed( runnable = () -> {
                sosInnerSection.setVisibility(View.GONE);
                alertsInnerSection.setVisibility(View.VISIBLE);
                accidentsInnerSection.setVisibility(View.GONE);
                closeDialog.setVisibility(View.VISIBLE);
            }, 200);
        }
    }


    @Override
    public void itemClickAccidentSection(CalendarDay day) {
        if(okToShow){
            ArrayList subList = new ArrayList<>(day.getAccidentsList());
            accidentsAdapter.setAdapterList(subList);
            if(recyclerViewAccidentsDialog.getVisibility()==View.GONE){
                recyclerViewAccidentsDialog.setVisibility(View.VISIBLE);
            }
            dialogInnerSection.setBackground(getResources().getDrawable(R.drawable.calendar_red_outline_section_white_back));
            dialogSection.setVisibility(View.VISIBLE);
            dialogInnerSection.startAnimation(messageAnimationReveal);
            dialogInnerSection.setVisibility(View.VISIBLE);
            //show after 200 milsec
            timer.removeCallbacks(runnable);
            timer.postDelayed( runnable = () -> {
                sosInnerSection.setVisibility(View.GONE);
                alertsInnerSection.setVisibility(View.GONE);
                accidentsInnerSection.setVisibility(View.VISIBLE);
                closeDialog.setVisibility(View.VISIBLE);
            }, 200);

        }
    }

    /**
     * The classes below are for the accident's details.
     *
     * USED:
     *     private boolean onCall1 = false;
     *     private ArrayList<AlertObject> accidentDetailsAlerts;
     *     private ArrayList<MessageObject> accidentDetailsMessages;
     *     private ArrayList<WorkerDataEntry> accidentDetailsData ;
     *     private ArrayList<RecommendationObject> accidentDetailsRecommendations;
     *     private ArrayList<SosAlert> accidentDetailsSos;
     *     private ArrayList<Worker> accidentDetailsWorker;
     *
     * @param accidentObject the selected accident.
     *
     */

    @Override
    public void itemClickDetails(AccidentObject accidentObject) {
        if(!onCall1){
            onCall1=true;
            try {
                ((Dashboard) getActivity()).showWaitMessage();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            getDetails(accidentObject);
        }
    }

    private void getDetails(AccidentObject accident){
        accidentDetailsAlerts = new ArrayList<>();
        accidentDetailsMessages = new ArrayList<>();
        accidentDetailsData = new ArrayList<>();
        accidentDetailsRecommendations = new ArrayList<>();
        accidentDetailsSos = new ArrayList<>();
        accidentDetailsWorker = new ArrayList<>();

        String time = accident.getTime();
        String[] timeSplit = time.split(":");
        String hourBefore;
        String hourAfter;
        if (timeSplit.length==3){
            int hour = Integer.parseInt(timeSplit[0]);
            int minus1 = hour - 1;
            int plus1 = hour + 1;
            String plus1Str = ""+plus1;
            if(plus1<10){
                plus1Str = "0"+plus1;
            }
            String minus1Str = ""+minus1;
            if(minus1<10){
                minus1Str = "0"+minus1;
            }
            if(hour==0){
                hourBefore = "00:00:00";
                hourAfter = plus1Str+":"+timeSplit[1]+":"+timeSplit[2];
            }else if(hour==24){
                hourBefore =  minus1Str+":"+timeSplit[1]+":"+timeSplit[2];
                hourAfter = "23:59:59";
            }else {
                hourBefore = minus1Str+":"+timeSplit[1]+":"+timeSplit[2];
                hourAfter = plus1Str+":"+timeSplit[1]+":"+timeSplit[2];
            }
            getAccidentsMessages(accident,hourBefore,hourAfter);

        }else {
            Log.e(TAG,"                 ERROR ON TIME FORMAT : ");
            try {
                ((Dashboard) getActivity()).showErrorLoadingData();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            onCall1 = false;
        }

    }

    private void getAccidentsMessages(AccidentObject accident,String hourBefore, String hourAfter){
        String userName = accident.getWorkerName();
        String date = accident.getDate();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=100000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&custom_post=messages&custom_meta={\"date_split_message\": \""+date+"\"}";

        Request request = new Request.Builder()
                .url(url)
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
                onCall1 = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){

                    if(okToShow){
                        MessagesObjectCMS.Root messagesResponse = new Gson().fromJson(myResponse, MessagesObjectCMS.Root.class);
                        ArrayList<MessageObject> messages = new ArrayList();
                        if(messagesResponse.respond.equals("1")) {
                            for (int i = 0; i < messagesResponse.result.size(); i++) {
                                if(messagesResponse.result.get(i).custom_fields.worker_name.equals(userName)){
                                    String t = messagesResponse.result.get(i).custom_fields.timestamp;
                                    if(t.compareTo(hourBefore)>=0 && t.compareTo(hourAfter)<=0){
                                        messages.add(0,new MessageObject(messagesResponse.result.get(i).ID,messagesResponse.result.get(i).custom_fields.worker_name,
                                                messagesResponse.result.get(i).post_title,messagesResponse.result.get(i).custom_fields.read,
                                                messagesResponse.result.get(i).custom_fields.date_split_message, t,
                                                messagesResponse.result.get(i).custom_fields.notes));
                                    }
                                }
                            }
                        }
                        accidentDetailsMessages = messages;
                        getAccidentsAlerts(accident,hourBefore,hourAfter);
                    }
                }else{
                    onCall1 = false;
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    private void getAccidentsAlerts(AccidentObject accident,String hourBefore, String hourAfter){
        String userName = accident.getWorkerName();
        String date = accident.getDate();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=100000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&custom_post=alerts&custom_meta={\"date_split_alert\": \""+date+"\"}";
        Request request = new Request.Builder()
                .url(url)
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
                onCall1 = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        AlertObjectCMS.Root alertsResponse = new Gson().fromJson(myResponse, AlertObjectCMS.Root.class);
                        ArrayList<AlertObject> alerts = new ArrayList();
                        if(alertsResponse.respond.equals("1")) {
                            for (int i = 0; i < alertsResponse.result.size(); i++) {
                                if(alertsResponse.result.get(i).postmeta.worker_name.equals(userName)) {
                                    String t = alertsResponse.result.get(i).postmeta.timestamp;
                                    if (t.compareTo(hourBefore) >= 0 && t.compareTo(hourAfter) <= 0) {
                                        alerts.add(0,new AlertObject(alertsResponse.result.get(i).ID, alertsResponse.result.get(i).postmeta.worker_name,
                                                alertsResponse.result.get(i).post_title,
                                                alertsResponse.result.get(i).postmeta.risk_grading, alertsResponse.result.get(i).postmeta.date_split_alert,
                                                t));
                                    }
                                }
                            }

                        }
                        accidentDetailsAlerts = alerts;
                        getAccidentsRecommendations(accident,hourBefore,hourAfter);
                    }
                }else{
                    onCall1 = false;
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    private void getAccidentsRecommendations(AccidentObject accident,String hourBefore, String hourAfter){
        String userName = accident.getWorkerName();
        String date = accident.getDate();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=desc&perpage=100000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&custom_post=recommendations&custom_meta={\"date_split_recommendation\": \""+date+"\"}";
        Request request = new Request.Builder()
                .url(url)
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
                onCall1 = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        RecommendationObjectCMS.Root recommendationsResponse = new Gson().fromJson(myResponse, RecommendationObjectCMS.Root.class);
                        ArrayList<RecommendationObject> recommendations = new ArrayList();
                        if(recommendationsResponse.respond.equals("1")) {
                            for (int i = 0; i < recommendationsResponse.result.size(); i++) {
                                if(recommendationsResponse.result.get(i).postmeta.worker_name.equals(userName)) {
                                    String t = recommendationsResponse.result.get(i).postmeta.timestamp;
                                    if (t.compareTo(hourBefore) >= 0 && t.compareTo(hourAfter) <= 0) {
                                        recommendations.add(0,new RecommendationObject(recommendationsResponse.result.get(i).ID, recommendationsResponse.result.get(i).postmeta.worker_name,
                                                recommendationsResponse.result.get(i).post_title, recommendationsResponse.result.get(i).postmeta.read,
                                                recommendationsResponse.result.get(i).postmeta.date_split_recommendation, recommendationsResponse.result.get(i).postmeta.timestamp));
                                    }
                                }
                            }

                        }
                        accidentDetailsRecommendations = recommendations;
                        getWorkerListAc(accident,hourBefore,hourAfter);
                    }
                }else{
                    onCall1 = false;
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    public void getWorkerListAc(AccidentObject accident,String hourBefore, String hourAfter){

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
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                onCall1 = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();

                if (response.isSuccessful()){
                    if(okToShow) {
                        ArrayList<Worker> workers = new ArrayList();
                        UserCMS.Root userResponse = new Gson().fromJson(myResponse, UserCMS.Root.class);
                        if (userResponse.respond.equals("1")) {
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
                                    workers.add(new Worker(workerUserInfo, info));
                                    //Build user history info
                                    String active, smartwatch, timestamp;
                                    if (info == null) {
                                        timestamp = "";
                                        active = "";
                                        smartwatch = "";
                                        workers.get(workers.size() - 1).addWorkerInfo(active, smartwatch, timestamp);

                                    } else {
                                        String[] revision = info.split("\\|");
                                        for (int y = 0; y < revision.length; y++) {
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
                        }
                        accidentDetailsWorker = workers;
                        String id = getIdByUsername(accident.getWorkerName());
                        getAccidentsData(accident,hourBefore,hourAfter,id);
                    }
                }else{
                    onCall1 = false;
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

    private void getAccidentsData(AccidentObject accident,String hourBefore, String hourAfter,String workerID){
        String date = accident.getDate();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?author_id="+workerID+"&perpage=10000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=worker_data" +
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
                onCall1 = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        WorkerDataCMS.Root workerDataResponse = new Gson().fromJson(myResponse,WorkerDataCMS.Root.class);
                        ArrayList<WorkerDataEntry> data = new ArrayList();
                        if(workerDataResponse.result.size()!=0){
                            for (int i = 0; i < workerDataResponse.result.size(); i++) {
                                String timestamp = workerDataResponse.result.get(i).custom_fields.timestamp;
                                String[] spitTimestamp = timestamp.split(" ",2);
                                String t = spitTimestamp[1];
                                if(t.compareTo(hourBefore)>=0 && t.compareTo(hourAfter)<=0) {
                                    data.add(new WorkerDataEntry(workerDataResponse.result.get(i).custom_fields.timestamp,
                                            workerDataResponse.result.get(i).custom_fields.heartrate,
                                            workerDataResponse.result.get(i).custom_fields.height,
                                            workerDataResponse.result.get(i).custom_fields.location,
                                            workerDataResponse.result.get(i).custom_fields.beacon_id,
                                            workerDataResponse.result.get(i).custom_fields.beacon_distance));
                                }
                            }
                        }
                        accidentDetailsData = data;
                        sortAccidentDetailsData();
                        getAccidentsSos(accident,hourBefore,hourAfter);
                    }
                }else{
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    onCall1 = false;
                }
            }
        });
    }

    private void getAccidentsSos(AccidentObject accident,String hourBefore, String hourAfter){
        String userName = accident.getWorkerName();
        String date = accident.getDate();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date" +
                "&order=desc&siteid=&perpage=100000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=sos";
        Request request = new Request.Builder()
                .url(url)
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
                onCall1 = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()){
                    if(okToShow){
                        SosCMS.Root sosAlertsResponse = new Gson().fromJson(myResponse,SosCMS.Root.class);
                        ArrayList<SosAlert> sos = new ArrayList();
                        if(sosAlertsResponse.respond.equals("1")){
                            for(int i=0;i<sosAlertsResponse.result.size();i++){
                                String title = sosAlertsResponse.result.get(i).post_title;
                                String[] spitTitle = title.split(" ",2);
                                String name = spitTitle[1];

                                String timestamp = sosAlertsResponse.result.get(i).custom_fields.timestamp;
                                String[] spitTimestamp = timestamp.split(" ",2);
                                String t = spitTimestamp[1];
                                String d = spitTimestamp[0];
                                if(name.equals(userName)){
                                    if(d.equals(date)){
                                        if(t.compareTo(hourBefore)>=0 && t.compareTo(hourAfter)<=0) {
                                            sos.add(new SosAlert(sosAlertsResponse.result.get(i).ID, sosAlertsResponse.result.get(i).post_title,
                                                    sosAlertsResponse.result.get(i).custom_fields.timestamp, sosAlertsResponse.result.get(i).custom_fields.location));
                                        }
                                    }
                                }

                            }
                        }
                        accidentDetailsSos= sos;
                        sortAccidentDetailsSos();
                        showDetails(accident);
                    }
                }else{
                    onCall1 = false;
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showDetails(AccidentObject accident) {
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override public void run() {
                        try {
                            ((Dashboard) getActivity()).hideWaitMessage();
                            ((Dashboard) getActivity()).hideCalendar();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        detailsBackground.setVisibility(View.VISIBLE);
                        detailsWindow.startAnimation(messageAnimationReveal);
                        detailsWindow.setVisibility(View.VISIBLE);
                        detailsTitle.setText(accident.getTitle());
                        detailsNote.setText(accident.getNotes());
                        detailsWorker.setText(accident.getWorkerName());
                        detailsTime.setText(accident.getTime());
                        if(accident.getDangerousZone().equalsIgnoreCase("0")){
                            detailsZone.setText("No");
                        }else{
                            detailsZone.setText("Yes");
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(
                                new Runnable() {
                                    @Override public void run() {
                                        showDetailsAlerts();
                                    }
                                }, 100);
                        onCall1 = false;

                    }
                }, 100);
    }

    private void showDetailsAlerts(){
        selectedCategory=1;
        detailsAlertsText.setTextColor(getResources().getColor(R.color.white));
        detailsAlerts.setBackground(getResources().getDrawable(R.drawable.blue_no_round_button));
        detailsMessagesText.setTextColor(getResources().getColor(R.color.grey));
        detailsMessages.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsRecommendationsText.setTextColor(getResources().getColor(R.color.grey));
        detailsRecommendations.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsHeartRateText.setTextColor(getResources().getColor(R.color.grey));
        detailsHeartRate.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsSosText.setTextColor(getResources().getColor(R.color.grey));
        detailsSos.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsNoData.setVisibility(View.GONE);
        recyclerViewDetailsAlerts.setVisibility(View.GONE);
        recyclerViewDetailsMessages.setVisibility(View.GONE);
        recyclerViewDetailsRecommendations.setVisibility(View.GONE);
        recyclerViewDetailsHeartRate.setVisibility(View.GONE);
        recyclerViewDetailsSos.setVisibility(View.GONE);
        ArrayList<AlertObject> a = new ArrayList<>();
        detailAlertsAdapter.setAdapterList(a);
        accidentDetailsAlertsCopy = new ArrayList<>(accidentDetailsAlerts);
        alertsAdded = 0;
        addItems();

        if(accidentDetailsAlerts.size()==0){
            infoDetailTitleSection.setVisibility(View.GONE);
            detailsNoData.setVisibility(View.VISIBLE);
        }else {
            detailTitleVal1.setText(getString(R.string.alert_t));
            detailTitleVal1.setVisibility(View.VISIBLE);
            detailTitleVal2.setText(getString(R.string.risk_grading));
            detailTitleVal2.setVisibility(View.VISIBLE);
            infoDetailTitleSection.setVisibility(View.VISIBLE);
            recyclerViewDetailsAlerts.setVisibility(View.VISIBLE);
        }
    }

    private void showDetailsRecommendations(){
        selectedCategory=2;
        detailsAlertsText.setTextColor(getResources().getColor(R.color.grey));
        detailsAlerts.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsMessagesText.setTextColor(getResources().getColor(R.color.grey));
        detailsMessages.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsRecommendationsText.setTextColor(getResources().getColor(R.color.white));
        detailsRecommendations.setBackground(getResources().getDrawable(R.drawable.blue_no_round_button));
        detailsHeartRateText.setTextColor(getResources().getColor(R.color.grey));
        detailsHeartRate.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsSosText.setTextColor(getResources().getColor(R.color.grey));
        detailsSos.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        recyclerViewDetailsAlerts.setVisibility(View.GONE);
        recyclerViewDetailsMessages.setVisibility(View.GONE);
        recyclerViewDetailsRecommendations.setVisibility(View.GONE);
        recyclerViewDetailsHeartRate.setVisibility(View.GONE);
        recyclerViewDetailsSos.setVisibility(View.GONE);
        detailsNoData.setVisibility(View.GONE);

        ArrayList<RecommendationObject> r = new ArrayList<>();
        detailRecommendationsAdapter.setAdapterList(r);
        accidentDetailsRecommendationsCopy = new ArrayList<>(accidentDetailsRecommendations);
        recommendationsAdded = 0;
        addItems();
        detailTitleVal1.setText(getString(R.string.recommendation_t));
        detailTitleVal1.setVisibility(View.VISIBLE);
        detailTitleVal2.setVisibility(View.GONE);
        recyclerViewDetailsRecommendations.setVisibility(View.VISIBLE);
        infoDetailTitleSection.setVisibility(View.VISIBLE);

        if(accidentDetailsRecommendations.size()==0){
            detailsNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDetailsMessages(){
        selectedCategory=3;
        detailsAlertsText.setTextColor(getResources().getColor(R.color.grey));
        detailsAlerts.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsMessagesText.setTextColor(getResources().getColor(R.color.white));
        detailsMessages.setBackground(getResources().getDrawable(R.drawable.blue_no_round_button));
        detailsRecommendationsText.setTextColor(getResources().getColor(R.color.grey));
        detailsRecommendations.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsHeartRateText.setTextColor(getResources().getColor(R.color.grey));
        detailsHeartRate.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsSosText.setTextColor(getResources().getColor(R.color.grey));
        detailsSos.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        recyclerViewDetailsAlerts.setVisibility(View.GONE);
        recyclerViewDetailsMessages.setVisibility(View.GONE);
        recyclerViewDetailsRecommendations.setVisibility(View.GONE);
        recyclerViewDetailsHeartRate.setVisibility(View.GONE);
        recyclerViewDetailsSos.setVisibility(View.GONE);
        detailsNoData.setVisibility(View.GONE);

        ArrayList<MessageObject> m = new ArrayList<>();
        detailMessagesAdapter.setAdapterList(m);
        accidentDetailsMessagesCopy = new ArrayList<>(accidentDetailsMessages);
        messagesAdded = 0;
        addItems();
        detailTitleVal1.setText(getString(R.string.alert_title_hint));
        detailTitleVal1.setVisibility(View.VISIBLE);
        detailTitleVal2.setText(getString(R.string.alert_message_hint));
        detailTitleVal2.setVisibility(View.VISIBLE);
        infoDetailTitleSection.setVisibility(View.VISIBLE);
        recyclerViewDetailsMessages.setVisibility(View.VISIBLE);
        if(accidentDetailsMessages.size()==0){
            detailsNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDetailsSos(){
        selectedCategory=4;
        detailsAlertsText.setTextColor(getResources().getColor(R.color.grey));
        detailsAlerts.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsMessagesText.setTextColor(getResources().getColor(R.color.grey));
        detailsMessages.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsRecommendationsText.setTextColor(getResources().getColor(R.color.grey));
        detailsRecommendations.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsHeartRateText.setTextColor(getResources().getColor(R.color.grey));
        detailsHeartRate.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsSosText.setTextColor(getResources().getColor(R.color.white));
        detailsSos.setBackground(getResources().getDrawable(R.drawable.blue_no_round_button));
        recyclerViewDetailsAlerts.setVisibility(View.GONE);
        recyclerViewDetailsMessages.setVisibility(View.GONE);
        recyclerViewDetailsRecommendations.setVisibility(View.GONE);
        recyclerViewDetailsHeartRate.setVisibility(View.GONE);
        recyclerViewDetailsSos.setVisibility(View.GONE);
        detailsNoData.setVisibility(View.GONE);
        ArrayList<SosAlert> s = new ArrayList<>();
        detailSosAdapter.setAdapterList(s);
        accidentDetailsSosCopy = new ArrayList<>(accidentDetailsSos);
        sosAdded = 0;
        addItems();
        detailTitleVal1.setText(getString(R.string.location));
        detailTitleVal1.setVisibility(View.VISIBLE);
        detailTitleVal2.setVisibility(View.GONE);
        infoDetailTitleSection.setVisibility(View.VISIBLE);
        recyclerViewDetailsSos.setVisibility(View.VISIBLE);
        if(accidentDetailsSos.size()==0){
            detailsNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDetailsHeartRate(){
        selectedCategory=5;
        detailsAlertsText.setTextColor(getResources().getColor(R.color.grey));
        detailsAlerts.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsMessagesText.setTextColor(getResources().getColor(R.color.grey));
        detailsMessages.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsRecommendationsText.setTextColor(getResources().getColor(R.color.grey));
        detailsRecommendations.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        detailsHeartRateText.setTextColor(getResources().getColor(R.color.white));
        detailsHeartRate.setBackground(getResources().getDrawable(R.drawable.blue_no_round_button));
        detailsSosText.setTextColor(getResources().getColor(R.color.grey));
        detailsSos.setBackground(getResources().getDrawable(R.drawable.light_grey_no_round_button_normal));
        recyclerViewDetailsAlerts.setVisibility(View.GONE);
        recyclerViewDetailsMessages.setVisibility(View.GONE);
        recyclerViewDetailsRecommendations.setVisibility(View.GONE);
        recyclerViewDetailsHeartRate.setVisibility(View.GONE);
        recyclerViewDetailsSos.setVisibility(View.GONE);
        detailsNoData.setVisibility(View.GONE);

        accidentDetailsDataCopyTrim = new ArrayList<>();
        accidentDetailsDataCopy = new ArrayList<>(accidentDetailsData);
        detailHeartRateAdapter.setAdapterList(accidentDetailsDataCopyTrim);
        int step = 0;
        for(int i=1;i<accidentDetailsDataCopy.size();i++){
            if(step==0){
                accidentDetailsDataCopyTrim.add(accidentDetailsDataCopy.get(i));
            }
            step++;
            if(step==12){
                step=0;
            }
        }
        heartRateAdded = 0;
        addItems();
        detailTitleVal1.setText(getString(R.string.heart_rate));
        detailTitleVal1.setVisibility(View.VISIBLE);
        detailTitleVal2.setVisibility(View.GONE);
        infoDetailTitleSection.setVisibility(View.VISIBLE);
        recyclerViewDetailsHeartRate.setVisibility(View.VISIBLE);
        if(accidentDetailsData.size()==0){
            detailsNoData.setVisibility(View.VISIBLE);
        }
    }

    public void addItems(){
        if(selectedCategory==5){
            if (heartRateAdded != accidentDetailsDataCopyTrim.size()) {
                int nextN = heartRateAdded + newN;
                if (nextN > accidentDetailsDataCopyTrim.size()) {
                    nextN = accidentDetailsDataCopyTrim.size();
                }
                for (int i = heartRateAdded; i < nextN; i++) {
                    detailHeartRateAdapter.addData(accidentDetailsDataCopyTrim.get(i));
                    heartRateAdded++;
                }
            }
        }else if(selectedCategory==4){
            if (sosAdded != accidentDetailsSosCopy.size()) {
                int nextN = sosAdded + newN;
                if (nextN > accidentDetailsSosCopy.size()) {
                    nextN = accidentDetailsSosCopy.size();
                }
                for (int i = sosAdded; i < nextN; i++) {
                    detailSosAdapter.addData(accidentDetailsSosCopy.get(i));
                    sosAdded++;
                }
            }
        }else if(selectedCategory==3){
            if (messagesAdded != accidentDetailsMessagesCopy.size()) {
                int nextN = messagesAdded + newN;
                if (nextN > accidentDetailsMessagesCopy.size()) {
                    nextN = accidentDetailsMessagesCopy.size();
                }
                for (int i = messagesAdded; i < nextN; i++) {
                    detailMessagesAdapter.addData(accidentDetailsMessagesCopy.get(i));
                    messagesAdded++;
                }
            }
        }else if(selectedCategory==2){
            if (recommendationsAdded != accidentDetailsRecommendationsCopy.size()) {
                int nextN = recommendationsAdded + newN;
                if (nextN > accidentDetailsRecommendationsCopy.size()) {
                    nextN = accidentDetailsRecommendationsCopy.size();
                }
                for (int i = recommendationsAdded; i < nextN; i++) {
                    detailRecommendationsAdapter.addData(accidentDetailsRecommendationsCopy.get(i));
                    recommendationsAdded++;
                }
            }
        }else if(selectedCategory==1) {
                if (alertsAdded != accidentDetailsAlertsCopy.size()) {
                    int nextN = alertsAdded + newN;
                    if (nextN > accidentDetailsAlertsCopy.size()) {
                        nextN = accidentDetailsAlertsCopy.size();
                    }
                    for (int i = alertsAdded; i < nextN; i++) {
                        detailAlertsAdapter.addData(accidentDetailsAlertsCopy.get(i));
                        alertsAdded++;
                    }
                }
            }else {
                return;
            }

    }


    private String getIdByUsername(String x){
        String id = "none";
        for(int i=0;i<accidentDetailsWorker.size();i++){
            if(accidentDetailsWorker.get(i).getWorkerProfileInfo().getUserName().equals(x)){
                return accidentDetailsWorker.get(i).getWorkerProfileInfo().getUserId();
            }
        }
        return id;
    }

    private void sortAccidentDetailsData(){
        ArrayList<WorkerDataEntry> subList = new ArrayList<>(accidentDetailsData);
        ArrayList<WorkerDataEntry> sortedList = new ArrayList();
        int minIndex = getMinIndexData(subList);
        while(minIndex>=0 ){
            sortedList.add(subList.get(minIndex));
            subList.remove(minIndex);
            minIndex = getMinIndexData(subList);
        }
        accidentDetailsData = new ArrayList<>(sortedList);
    }

    private void sortAccidentDetailsSos(){
        ArrayList<SosAlert> subList = new ArrayList<>(accidentDetailsSos);
        ArrayList<SosAlert> sortedList = new ArrayList();
        int minIndex = getMinIndexSos(subList);
        while(minIndex>=0 ){
            sortedList.add(subList.get(minIndex));
            subList.remove(minIndex);
            minIndex = getMinIndexSos(subList);
        }
        accidentDetailsSos = new ArrayList<>(sortedList);
    }

    private Integer getMinIndexData( ArrayList<WorkerDataEntry> list){
        if(list.size()>0){
            int minIndex = 0;
            String min = list.get(0).getTime();
            for(int i=0;i<list.size();i++){
                if(min.compareTo(list.get(i).getTime())>0){
                    min = list.get(i).getTime();
                    minIndex = i;
                }
            }
            return minIndex;
        }
        return -1;
    }

    private Integer getMinIndexSos( ArrayList<SosAlert> list){
        if(list.size()>0){
            int minIndex = 0;
            String min = list.get(0).getTime();
            for(int i=1;i<list.size();i++){
                if(min.compareTo(list.get(i).getTime())>0){
                    min = list.get(i).getTime();
                    minIndex = i;
                }
            }
            return minIndex;
        }
        return -1;
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

    @Override
    public void itemClickLocationDetailsSosAlert(String latLog) {
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