package com.dotsoft.smartsoniadashboard.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.BeaconAdapter;
import com.dotsoft.smartsoniadashboard.objects.Beacon;
import com.dotsoft.smartsoniadashboard.objects.cms.BeaconCMS;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Beacons extends Fragment implements BeaconAdapter.HandleEventClick {
    private final String TAG = "BEACONS";
    private ArrayList<Beacon> beaconsList = new ArrayList<>();
    private BeaconAdapter beaconsAdapter;
    private RecyclerView recyclerViewBeacons;
    private RelativeLayout confirmSection,confirmSectionInner, addBeaconSection, addBeaconButton,cancelButton,addForm;
    private TextInputEditText majorId,location,notes;
    private TextInputLayout majorIdLayout, locationLayout, noteLayout;
    private ScrollView addBeaconInnerSection;
    private ProgressBar loading;
    private ImageView refresh;
    private TextView addBeaconTitle, addBeaconText;
    private boolean onCall = false;
    private TextView logoutNo,logoutYes, confirmText;
    private Animation messageAnimationReveal, messageAnimationGone;
    private Handler timer;
    private Runnable runnable;
    private String activeSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode") Switch activeSwitcher;
    private boolean okToShow = true;

    public Beacons() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beacons, container, false);
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
                    getBeaconList();
                }
            }
        });

        //set up confirm window
        confirmSection = (RelativeLayout) view.findViewById(R.id.confirm_delete_section);
        confirmSectionInner = (RelativeLayout) view.findViewById(R.id.confirm_delete_inner_section);
        confirmText = (TextView) view.findViewById(R.id.confirm_text);
        logoutNo = (TextView) view.findViewById(R.id.confirm_no);
        logoutYes = (TextView) view.findViewById(R.id.confirm_yes);

        //set animations and timer
        messageAnimationReveal = AnimationUtils.loadAnimation(getContext(), R.anim.fadein_once);
        messageAnimationGone = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout_once);
        timer = new Handler();

        // set beacon add
        addBeaconSection = (RelativeLayout) view.findViewById(R.id.background_add_form_section);
        addBeaconInnerSection = (ScrollView) view.findViewById(R.id.add_form_section);
        addBeaconButton = (RelativeLayout) view.findViewById(R.id.add_beacon);
        cancelButton = (RelativeLayout) view.findViewById(R.id.cancel_beacon);
        addForm = (RelativeLayout) view.findViewById(R.id.add_form);
        addBeaconText = (TextView) view.findViewById(R.id.add_beacon_text);
        addBeaconTitle = (TextView) view.findViewById(R.id.add_beacon_title);

        //set input fields
        majorId = (TextInputEditText) view.findViewById(R.id.major_id_input_text);
        location = (TextInputEditText) view.findViewById(R.id.location_input_text);
        notes = (TextInputEditText) view.findViewById(R.id.note_input_text);
        majorIdLayout = (TextInputLayout) view.findViewById(R.id.major_id_input_section);
        locationLayout = (TextInputLayout) view.findViewById(R.id.location_input_section);
        noteLayout = (TextInputLayout) view.findViewById(R.id.note_input_section);


        //set switch to active beacons
        activeSwitcher = (Switch) view.findViewById(R.id.switchActive);
        activeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setActive("1");
                }else {
                    setActive("0");
                }
            }
        });

        // check if beacons have been loaded
        recyclerViewBeacons = (RecyclerView) view.findViewById(R.id.recycler_view_beacons);
        recyclerViewBeacons.setVisibility(View.GONE);
        if(DataHolder.getInstance().getBeaconsList()!=null){
            beaconsList = DataHolder.getInstance().getBeaconsList();
            recyclerViewBeacons.setVisibility(View.VISIBLE);
        }else {
            // Load Beacons
            getBeaconList();
        }

        //set beacon recycler view
        RecyclerView.LayoutManager mLrecyclerViewBeacons = new GridLayoutManager(getContext(), 4);
        recyclerViewBeacons.setLayoutManager(mLrecyclerViewBeacons);
        beaconsAdapter = new BeaconAdapter(beaconsList,this);
        recyclerViewBeacons.setAdapter(beaconsAdapter);

        showData();

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        okToShow = false;
    }

    private void showBeacons(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            ArrayList subList = new ArrayList<>(beaconsList);
                            beaconsAdapter.setAdapterList(subList);
                            if(recyclerViewBeacons.getVisibility()==View.GONE){
                                    recyclerViewBeacons.setVisibility(View.VISIBLE);
                                }
                        }
                    }, 100);
        }
    }

    public void addNewBeacon(String majorId, String location, String notes,String active,String timestamp){
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String newInfo = active+","+location+","+notes+","+timestamp+"|";
        showLoading();
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String sendURL = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/newpost/?custom_post=beacons&post_status=publish&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&ACCESS_TOKEN=" +DataHolder.getInstance().getSelectedUser().getAccessToken()+"&subject="+majorId+
                "&custom_field={\"beacon_id\":\""+majorId+"\",\"info\":\""+newInfo+"\",\"active\":\""+active+"\"}";
        Request request = new Request.Builder()
                .url(sendURL)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                hideLoading();
                onCall = false;
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    if(okToShow){
                        beaconsList = new ArrayList<>();
                        getBeaconList();
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

    private void deleteBeacon(String beaconId){
        showLoading();
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/custom_service/?service=delete_by_id&postid="+beaconId+"&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq";
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
                }            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();
                if (response.isSuccessful()) {
                    if(okToShow){
                        beaconsList = new ArrayList<>();
                        getBeaconList();
                        try {
                            ((Dashboard) getActivity()).hideWaitMessage();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    hideLoading();
                    //Log.e("RESPONSE - onResponse", "getting generatedRoutes is NOT Successful" + myResponse);
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }                }
                onCall = false;
            }
        });
    }

    public void getBeaconList(){
        onCall = true;
        showLoading();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=DES&siteid=&perpage=1000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=beacons";
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
                }                onCall = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String myResponse = response.body().string();

                if (response.isSuccessful()){
                    if(okToShow){
                        BeaconCMS.Root beaconResponse = new Gson().fromJson(myResponse,BeaconCMS.Root.class);
                        ArrayList<Beacon> beacons = new ArrayList();
                        if(beaconResponse.respond.equals("1")){
                            String majorId,location,note,postId,beaconOn,info,timestamp;
                            for(int i=0;i<beaconResponse.result.size();i++){
                                majorId = beaconResponse.result.get(i).custom_fields.beacon_id;
                                postId = beaconResponse.result.get(i).ID;
                                info = beaconResponse.result.get(i).custom_fields.info;
                                beacons.add(new Beacon(majorId,postId,info));
                                if(info==null){
                                    timestamp = "";
                                    beaconOn = "";
                                    location = "";
                                    note = "";
                                    beacons.get(beacons.size()-1).addInfo(location,note,beaconOn,timestamp);
                                }else {
                                    String[] revision = info.split("\\|");
                                    for(int y=0;y<revision.length;y++){
                                        String[] value = revision[y].split(",");
                                        if(value.length==4){
                                            timestamp = value[3];
                                            beaconOn = value[0];
                                            location = value[1];
                                            note = value[2];
                                            beacons.get(beacons.size()-1).addInfo(location,note,beaconOn,timestamp);
                                        }else if(y==0){
                                            timestamp = "";
                                            beaconOn = "";
                                            location = "";
                                            note = "";
                                            beacons.get(beacons.size()-1).addInfo(location,note,beaconOn,timestamp);
                                        }
                                    }
                                }
                            }
                            DataHolder.getInstance().setBeaconsList(beacons);
                            beaconsList = beacons;
                            hideLoading();
                            showData();
                        }else{
                            hideLoading();
                            try {
                                ((Dashboard) getActivity()).showErrorLoadingData();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }                        }
                    }
                }else{
                    hideLoading();
                    try {
                        ((Dashboard) getActivity()).showErrorLoadingData();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }                }
                onCall = false;
            }
        });
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

    @Override
    public void onStop(){
        super.onStop();
        okToShow = false;
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
    public void itemClickAdd() {
        if(!onCall){
            if(((Dashboard) (getActivity())).isToday()) {
                try{
                    ((Dashboard) getActivity()).hideCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addBeaconTitle.setText(getString(R.string.beacon_add));
                addBeaconText.setText(getString(R.string.beacon_add));
                addNewBeaconForm();
            }
            else {
                try {
                    ((Dashboard) (getActivity())).showCannotEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        }
    }

    @Override
    public void itemClickEdit(String id, String m, String l, String n, boolean a, String i) {

        if(!onCall){
            if(((Dashboard) (getActivity())).isToday()) {
                try{
                    ((Dashboard) getActivity()).hideCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String editTitle = getString(R.string.beacon_edit_title) + " "+m;
                addBeaconTitle.setText(editTitle);
                addBeaconText.setText(getString(R.string.beacon_edit_btn));
                location.setText(l);
                notes.setText(n);
                activeSwitcher.setChecked(a);
                editBeaconForm(id,i);
            }
            else {
                try {
                    ((Dashboard) (getActivity())).showCannotEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        }
    }

    @Override
    public void itemClickDelete(String id, String major) {
        if(!onCall){
            if(((Dashboard) (getActivity())).isToday()) {
                try{
                    ((Dashboard) getActivity()).hideCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirmDelete(id,major);
            }else {
                try {
                    ((Dashboard) (getActivity())).showCannotEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void editBeaconForm(String id,String i){
        majorIdLayout.setVisibility(View.GONE);
        addBeaconSection.setVisibility(View.VISIBLE);
        addBeaconInnerSection.startAnimation(messageAnimationReveal);
        addBeaconInnerSection.setVisibility(View.VISIBLE);
        addBeaconInnerSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });
        addBeaconSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });
        addForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                addBeaconInnerSection.startAnimation(messageAnimationGone);
                addBeaconInnerSection.setVisibility(View.GONE);
                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    try{
                        ((Dashboard) getActivity()).showCalendar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addBeaconSection.setVisibility(View.GONE);
                }, 200);

            }
        });

        addBeaconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                //add beacon
                locationLayout.setErrorEnabled(false);
                noteLayout.setErrorEnabled(false);
                location.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                notes.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                String locationText = location.getText().toString();
                String notesText = notes.getText().toString();
                if(locationText.equals("")){
                    locationLayout.setErrorEnabled(true);
                    locationLayout.setError(getString(R.string.empty_field_location));
                    location.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else if(notesText.length()>35){
                    noteLayout.setErrorEnabled(true);
                    noteLayout.setError(getString(R.string.error_notes));
                    notes.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else{
                    String time = ((Dashboard) getActivity()).getTimestamp();
                    editBeacon(id,activeSwitch,locationText,notesText,time,i);
                    addBeaconInnerSection.startAnimation(messageAnimationGone);
                    addBeaconInnerSection.setVisibility(View.GONE);
                    // hide after 200 milsec
                    timer.removeCallbacks(runnable);
                    timer.postDelayed( runnable = () -> {
                        try{
                            ((Dashboard) getActivity()).showCalendar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addBeaconSection.setVisibility(View.GONE);
                    }, 200);
                }
            }
        });
    }

    private void setActive(String n){
        activeSwitch = n;
    }

    private void editBeacon(String id, String active,String locationText, String notesText,String timestamp, String i){
        String newInfo = active+","+locationText+","+notesText+","+timestamp+"|";
        String finalInfo = newInfo + i;
        showLoading();
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String sendURL = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/updatepost/?id="+id+"&post_status=publish&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq" +
                "&ACCESS_TOKEN=" +DataHolder.getInstance().getSelectedUser().getAccessToken()+"&custom_field={\"info\":\""+finalInfo+"\",\"active\":\""+active+"\"}";
        Request request = new Request.Builder()
                .url(sendURL)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                hideLoading();
                onCall = false;
                try {
                    ((Dashboard) getActivity()).showErrorLoadingData();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    if(okToShow){
                        beaconsList = new ArrayList<>();
                        getBeaconList();
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
                    }                }
                onCall = false;
            }
        });
    }

    private void addNewBeaconForm(){
        activeSwitcher.setChecked(false);
        majorId.setText("");
        majorIdLayout.setVisibility(View.VISIBLE);
        location.setText("");
        notes.setText("");
        addBeaconSection.setVisibility(View.VISIBLE);
        addBeaconInnerSection.startAnimation(messageAnimationReveal);
        addBeaconInnerSection.setVisibility(View.VISIBLE);
        addBeaconInnerSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });
        addBeaconSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });
        addForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                addBeaconInnerSection.startAnimation(messageAnimationGone);
                addBeaconInnerSection.setVisibility(View.GONE);
                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    try{
                        ((Dashboard) getActivity()).showCalendar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addBeaconSection.setVisibility(View.GONE);
                }, 200);

            }
        });

        addBeaconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                //add beacon
                majorIdLayout.setErrorEnabled(false);
                locationLayout.setErrorEnabled(false);
                noteLayout.setErrorEnabled(false);
                majorId.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                location.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                notes.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                String majorText = majorId.getText().toString();
                String locationText = location.getText().toString();
                String notesText = notes.getText().toString();
                if(majorText.equals("")){
                    majorIdLayout.setErrorEnabled(true);
                    majorIdLayout.setError(getString(R.string.empty_field_majorId));
                    majorId.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else if(locationText.equals("")){
                    locationLayout.setErrorEnabled(true);
                    locationLayout.setError(getString(R.string.empty_field_location));
                    location.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else if(notesText.length()>35){
                    noteLayout.setErrorEnabled(true);
                    noteLayout.setError(getString(R.string.error_notes));
                    notes.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else{
                    String time = ((Dashboard) getActivity()).getTimestamp();
                    addNewBeacon(majorText,locationText,notesText,activeSwitch,time);
                    addBeaconInnerSection.startAnimation(messageAnimationGone);
                    addBeaconInnerSection.setVisibility(View.GONE);
                    // hide after 200 milsec
                    timer.removeCallbacks(runnable);
                    timer.postDelayed( runnable = () -> {
                        try{
                            ((Dashboard) getActivity()).showCalendar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addBeaconSection.setVisibility(View.GONE);
                    }, 200);
                }
            }
        });
    }

    private void confirmDelete(String id,String major){
        String str = getString(R.string.delete_beacon).toString() +" "+major +" ?";
        confirmText.setText(str);
        confirmSection.setVisibility(View.VISIBLE);
        confirmSectionInner.startAnimation(messageAnimationReveal);
        confirmSectionInner.setVisibility(View.VISIBLE);

        logoutNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSectionInner.startAnimation(messageAnimationGone);
                confirmSectionInner.setVisibility(View.GONE);

                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    try{
                        ((Dashboard) getActivity()).showCalendar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    confirmSection.setVisibility(View.GONE);
                }, 200);

            }
        });


        logoutYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete beacon
                deleteBeacon(id);

                confirmSectionInner.startAnimation(messageAnimationGone);
                confirmSectionInner.setVisibility(View.GONE);

                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    try{
                        ((Dashboard) getActivity()).showCalendar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    confirmSection.setVisibility(View.GONE);
                }, 200);

            }
        });
    }

    public void showData(){
        try{
            if(((Dashboard) (getActivity())).isToday()) {
                if(refresh.getVisibility()==View.GONE && loading.getVisibility()!=View.VISIBLE){
                    refresh.setVisibility(View.VISIBLE);
                }
            }else {
                refresh.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(okToShow){
            for(int i =0;i< beaconsList.size();i++){
                beaconsList.get(i).setSelectedDay(((Dashboard) (getActivity())).getSelectedDate());
            }
            showBeacons();
        }
    }

}