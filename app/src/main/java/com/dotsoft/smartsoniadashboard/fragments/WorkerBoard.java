package com.dotsoft.smartsoniadashboard.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.dotsoft.smartsoniadashboard.DataHolder;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.activities.Dashboard;
import com.dotsoft.smartsoniadashboard.adapters.SmartwatchAdapter;
import com.dotsoft.smartsoniadashboard.adapters.WorkerAdapter;
import com.dotsoft.smartsoniadashboard.objects.Beacon;
import com.dotsoft.smartsoniadashboard.objects.Smartwatch;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.Worker;
import com.dotsoft.smartsoniadashboard.objects.cms.BeaconCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.SmartwatchCMS;
import com.dotsoft.smartsoniadashboard.objects.cms.UserCMS;
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


public class WorkerBoard extends Fragment implements WorkerAdapter.HandleEventClick,SmartwatchAdapter.HandleEventClick  {
    private final String TAG = "WORKER BOARD";
    private ArrayList<Worker> workerList = new ArrayList<>();
    private ArrayList<Smartwatch> smartwatchesList = new ArrayList<>();
    private final int REFRESH_DATA_DB_DASHBOARD_EVERY = 1000 * 15; // 15 sec

    private WorkerAdapter workersAdapter;
    private SmartwatchAdapter smartwatchAdapter;
    private RecyclerView recyclerViewWorkers, recyclerViewSmartwatch;
    private RelativeLayout confirmSection,confirmSectionInner, addWorkerSection, addWorkerButton,cancelButton,addForm,
            backgroundEdit, editWorkerButton, cancelEditWorkerButton, smartwatchListLayout;
    private TextInputEditText usernameId,email,password,repeatPassword;
    private TextInputLayout usernameIdLayout, emailLayout, passwordLayout,repeatPasswordLayout;
    private ScrollView addWorkerInnerSection,editWorkerInnerSection;
    private ProgressBar loading;
    private ImageView refresh;
    private TextView addWorkerTitle, addWorkerText, editWorkerTitle, selectedSmartwatchTv;
    private boolean onCall = false;
    private TextView logoutNo,logoutYes, confirmText;
    private Animation messageAnimationReveal, messageAnimationGone;
    private Handler timer;
    private final Handler handler = new Handler();
    private Runnable runnable,r;
    private String checkInSwitch;
    private String selectedSmartwatch = "";
    @SuppressLint("UseSwitchCompatOrMaterialCode") Switch checkInSwitcher;
    private boolean okToShow = true;

    public WorkerBoard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_worker_board, container, false);
        try {
            Log.e(TAG,"DATE: " + ((Dashboard) getActivity()).getSelectedDate());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
                    getWorkerList();
                    getSmartWatchesList();
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

        // set add worker
        addWorkerSection = (RelativeLayout) view.findViewById(R.id.background_add_form_section);
        addWorkerInnerSection = (ScrollView) view.findViewById(R.id.add_form_section);
        addWorkerButton = (RelativeLayout) view.findViewById(R.id.add_beacon);
        cancelButton = (RelativeLayout) view.findViewById(R.id.cancel_beacon);
        addForm = (RelativeLayout) view.findViewById(R.id.add_form);
        addWorkerText = (TextView) view.findViewById(R.id.add_beacon_text);
        addWorkerTitle = (TextView) view.findViewById(R.id.add_beacon_title);

        //set input fields
        usernameId = (TextInputEditText) view.findViewById(R.id.username_input_text);
        email = (TextInputEditText) view.findViewById(R.id.email_input_text);
        password = (TextInputEditText) view.findViewById(R.id.password_input_text);
        usernameIdLayout = (TextInputLayout) view.findViewById(R.id.username_input_section);
        emailLayout = (TextInputLayout) view.findViewById(R.id.email_input_section);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.password_input_section);
        repeatPassword = (TextInputEditText) view.findViewById(R.id.repeat_password_input_text);
        repeatPasswordLayout = (TextInputLayout) view.findViewById(R.id.repeat_password_input_section);

        //set edit worker
        editWorkerButton = (RelativeLayout) view.findViewById(R.id.edit_worker);
        editWorkerTitle = (TextView) view.findViewById(R.id.edit_worker_title);
        selectedSmartwatchTv = (TextView) view.findViewById(R.id.selected_smartwatch);
        selectedSmartwatchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smartwatchListLayout.setVisibility(View.VISIBLE);
            }
        });
        smartwatchListLayout = (RelativeLayout) view.findViewById(R.id.smartwatch_list_form_transparent);
        backgroundEdit = (RelativeLayout) view.findViewById(R.id.background_edit_form_section);
        editWorkerInnerSection = (ScrollView) view.findViewById(R.id.edit_form_section);
        cancelEditWorkerButton = (RelativeLayout) view.findViewById(R.id.cancel_edit_worker);

        //set switch to check in worker
        checkInSwitcher = (Switch) view.findViewById(R.id.switch_check_in);
        checkInSwitcher.setVisibility(View.GONE);
        checkInSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setCheckIn("1");
                }else {
                    setCheckIn("0");
                }
            }
        });

        // check if beacons have been loaded
        recyclerViewWorkers = (RecyclerView) view.findViewById(R.id.recycler_view_beacons);
        recyclerViewWorkers.setVisibility(View.GONE);
        if(DataHolder.getInstance().getWorkersList()!=null){
            workerList = DataHolder.getInstance().getWorkersList();
            recyclerViewWorkers.setVisibility(View.VISIBLE);
        }else {
            // Load Beacons
            getWorkerList();
        }

        if(DataHolder.getInstance().getSmartwatchesList()!=null){
            smartwatchesList = DataHolder.getInstance().getSmartwatchesList();
        }else {
            // Load watches;
            getSmartWatchesList();
        }


        //set workers recycler view
        RecyclerView.LayoutManager mLrecyclerViewWorkers = new GridLayoutManager(getContext(), 4);
        recyclerViewWorkers.setLayoutManager(mLrecyclerViewWorkers);
        workersAdapter = new WorkerAdapter(workerList,this,getContext());
        recyclerViewWorkers.setAdapter(workersAdapter);

        //set smartwatches recycler view
        recyclerViewSmartwatch = (RecyclerView) view.findViewById(R.id.recycler_view_smartwatch_list);
        RecyclerView.LayoutManager mLrecyclerViewSmartwatches = new GridLayoutManager(getContext(), 1);
        recyclerViewSmartwatch.setLayoutManager(mLrecyclerViewSmartwatches);
        smartwatchAdapter = new SmartwatchAdapter(smartwatchesList,this);
        recyclerViewSmartwatch.setAdapter(smartwatchAdapter);

        showData();

        /**
         *  set repeated process, every 15 sec. Get sos data
         */
        r = new Runnable() {
            public void run() {
                if(!onCall){
                    if(((Dashboard) (getActivity())).isToday()) {
                        Log.e(TAG,"TODAY");
                        getWorkerList();
                        getSmartWatchesList();
                    }else {
                        Log.e(TAG,"NOT TODAY");

                    }
                }
                handler.postDelayed(this, REFRESH_DATA_DB_DASHBOARD_EVERY);
            }
        };

        handler.postDelayed(r, REFRESH_DATA_DB_DASHBOARD_EVERY);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((Dashboard) (getActivity())).setDialogOpen(false);
        handler.removeCallbacks(r);
        okToShow = false;
    }

    public void getWorkerList(){
        onCall = true;
        showLoading();
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
                            workerList = workers;
                            hideLoading();
                            showWorkers();
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

    private void showWorkers(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            ArrayList subList = new ArrayList<>(workerList);
                            for(int i =0;i< subList.size();i++){
                                workersAdapter.setAdapterList(subList);
                                if(recyclerViewWorkers.getVisibility()==View.GONE){
                                    recyclerViewWorkers.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }, 100);
        }
    }

    private void showSmartwatches(){
        if(okToShow){
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override public void run() {
                            ArrayList subList = new ArrayList<>(smartwatchesList);
                            for(int i =0;i< subList.size();i++){
                                smartwatchAdapter.setAdapterList(subList);
                            }
                        }
                    }, 100);
        }
    }

    public void getSmartWatchesList(){
        onCall = true;
        showLoading();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/getposts/?orderby=date&order=DES&siteid=&perpage=1000000&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq&custom_post=smartwatches";
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
                        SmartwatchCMS.Root smartwatchesResponse = new Gson().fromJson(myResponse,SmartwatchCMS.Root.class);
                        ArrayList<Smartwatch> smartwatches = new ArrayList();
                        if(smartwatchesResponse.respond.equals("1")){
                            String id;
                            for(int i=0;i<smartwatchesResponse.result.size();i++) {
                                id = smartwatchesResponse.result.get(i).custom_fields.id;
                                smartwatches.add(new Smartwatch(id));
                            }
                            smartwatches.add(new Smartwatch("NONE"));
                            DataHolder.getInstance().setSmartwatchesList(smartwatches);
                            smartwatchesList = smartwatches;
                            showSmartwatches();
                            hideLoading();
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
            if(((Dashboard) (getActivity())).isToday()){
                try{
                    ((Dashboard) getActivity()).hideCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addNewWorkerForm();
            }else {
                try {
                    ((Dashboard) (getActivity())).showCannotEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void addNewWorkerForm(){
        ((Dashboard) (getActivity())).setDialogOpen(true);
        usernameId.setText("");
        email.setText("");
        password.setText("");
        addWorkerSection.setVisibility(View.VISIBLE);
        addWorkerInnerSection.startAnimation(messageAnimationReveal);
        addWorkerInnerSection.setVisibility(View.VISIBLE);
        addWorkerInnerSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
            }
        });
        addWorkerSection.setOnClickListener(new View.OnClickListener() {
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
                ((Dashboard) (getActivity())).setDialogOpen(false);

                ((Dashboard) getActivity()).hideKeyboard();
                addWorkerInnerSection.startAnimation(messageAnimationGone);
                addWorkerInnerSection.setVisibility(View.GONE);
                // hide after 200 milsec
                timer.removeCallbacks(runnable);
                timer.postDelayed( runnable = () -> {
                    try{
                        ((Dashboard) getActivity()).showCalendar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addWorkerSection.setVisibility(View.GONE);
                }, 200);

            }
        });

        addWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) getActivity()).hideKeyboard();
                //add beacon
                usernameIdLayout.setErrorEnabled(false);
                emailLayout.setErrorEnabled(false);
                passwordLayout.setErrorEnabled(false);
                repeatPasswordLayout.setErrorEnabled(false);
                usernameId.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                email.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                password.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                repeatPassword.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button));
                String usernameText = usernameId.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String repeatPasswordText = repeatPassword.getText().toString();
                if(usernameText.equals("")){
                    usernameIdLayout.setErrorEnabled(true);
                    usernameIdLayout.setError(getString(R.string.empty_field_username));
                    usernameId.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                } else if (usernameText.contains(" ")) {
                    usernameIdLayout.setErrorEnabled(true);
                    usernameIdLayout.setError(getString(R.string.username_space_error));
                    usernameId.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else if(emailText.equals("")){
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError(getString(R.string.empty_field_email));
                    email.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else if(passwordText.equals("")){
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError(getString(R.string.empty_field_password));
                    password.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else if(!passwordText.equals(repeatPasswordText)){
                    repeatPasswordLayout.setErrorEnabled(true);
                    repeatPasswordLayout.setError(getString(R.string.password_not_match_error));
                    repeatPassword.setBackground(getContext().getDrawable(R.drawable.light_grey_no_round_button_red_stroke));
                }else{
                    ((Dashboard) (getActivity())).setDialogOpen(false);
                    String time = ((Dashboard) getActivity()).getTimestamp();
                    addNewWorker(usernameText,emailText,passwordText,time);
                    addWorkerInnerSection.startAnimation(messageAnimationGone);
                    addWorkerInnerSection.setVisibility(View.GONE);
                    // hide after 200 milsec
                    timer.removeCallbacks(runnable);
                    timer.postDelayed( runnable = () -> {
                        try{
                            ((Dashboard) getActivity()).showCalendar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addWorkerSection.setVisibility(View.GONE);
                    }, 200);
                }
            }
        });
    }

    public void addNewWorker(String username, String email, String password,String time){
        String newInfo = "0,,"+time+"|";
        showLoading();
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/signup/?username="+username+"&password="+password+"&email="+email+"&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq"+
                "&custom_field={\"user_role\":\"WORKER\",\"user_info\":\""+newInfo+"\"}";
        Request request = new Request.Builder()
                .url(registerUrl)
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
                String myResponse = response.body().string();
                Log.e("REGISTER",myResponse);
                if (response.isSuccessful()){
                    if(okToShow){
                        Root rootResponse = new Gson().fromJson(myResponse, Root.class);
                        if(rootResponse.respond.equals("1")){
                            workerList = new ArrayList<>();
                            getWorkerList();
                            try {
                                ((Dashboard) getActivity()).hideWaitMessage();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
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

    @Override
    public void itemClickDelete(String id) {
        if(!onCall){
            if(((Dashboard) (getActivity())).isToday()) {
                try{
                    ((Dashboard) getActivity()).hideCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirmDelete(id);
            }else {
                try {
                    ((Dashboard) (getActivity())).showCannotEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void confirmDelete(String username){
        ((Dashboard) (getActivity())).setDialogOpen(true);

        String str = getString(R.string.delete_worker).toString() +" "+username +" ?";
        confirmText.setText(str);
        confirmSection.setVisibility(View.VISIBLE);
        confirmSectionInner.startAnimation(messageAnimationReveal);
        confirmSectionInner.setVisibility(View.VISIBLE);

        logoutNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Dashboard) (getActivity())).setDialogOpen(false);

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
                ((Dashboard) (getActivity())).setDialogOpen(false);
                //delete worker
                deleteWorker(username);
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

    private void deleteWorker(String workerId){
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        showLoading();
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String registerUrl = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/custom_service/?service=delete_the_user&username="+workerId+"&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq";
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
                if (response.isSuccessful()) {
                    if(okToShow){
                        workerList = new ArrayList<>();
                        getWorkerList();
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
    public void itemClick(String code) {
        selectedSmartwatch = code;
        selectedSmartwatchTv.setText(code);
        smartwatchListLayout.setVisibility(View.GONE);
    }

    @Override
    public void onStop(){
        super.onStop();
        okToShow = false;
    }

    private void setCheckIn(String n){
        checkInSwitch = n;
    }

    public void editWorker(String userID, String oldInfo){
        try {
            ((Dashboard) getActivity()).showWaitMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String time = ((Dashboard) getActivity()).getTimestamp();
        String newInfo = checkInSwitch + "," + selectedSmartwatch + "," + time + "|" + oldInfo ;
        showLoading();
        onCall = true;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String Url = "http://smart-sonia.eu.144-76-38-75.comitech.gr/api/custom_service/?service=update_meta&my_meta_value="+newInfo+"&my_user_id="+userID+
                "&auth_key=l8qjQ5vXHfDlsmwkD4tPZantq";
        Log.e(TAG,Url);

        Request request = new Request.Builder()
                .url(Url)
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
                if (response.isSuccessful()) {
                    if(okToShow){
                        workerList = new ArrayList<>();
                        getWorkerList();
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

    @Override
    public void itemClickEdit(String userName, String userID,String s, boolean c, String oldInfo) {
        if(!onCall) {
            if(((Dashboard) (getActivity())).isToday()) {
                try{
                    ((Dashboard) getActivity()).hideCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String title = getString(R.string.worker_edit_title) + " " + userName;
                editWorkerTitle.setText(title);
                selectedSmartwatch = s;
                checkInSwitcher.setChecked(c);
                selectedSmartwatchTv.setText(s);
                backgroundEdit.setVisibility(View.VISIBLE);
                editWorkerInnerSection.startAnimation(messageAnimationReveal);
                editWorkerInnerSection.setVisibility(View.VISIBLE);
                ((Dashboard) (getActivity())).setDialogOpen(true);

                editWorkerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Dashboard) (getActivity())).setDialogOpen(false);
                        editWorker(userID, oldInfo);
                        editWorkerInnerSection.startAnimation(messageAnimationGone);
                        editWorkerInnerSection.setVisibility(View.GONE);
                        // hide after 200 milsec
                        timer.removeCallbacks(runnable);
                        timer.postDelayed(runnable = () -> {
                            try{
                                ((Dashboard) getActivity()).showCalendar();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            backgroundEdit.setVisibility(View.GONE);
                        }, 200);
                    }
                });

                cancelEditWorkerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Dashboard) (getActivity())).setDialogOpen(false);
                        editWorkerInnerSection.startAnimation(messageAnimationGone);
                        editWorkerInnerSection.setVisibility(View.GONE);
                        // hide after 200 milsec
                        timer.removeCallbacks(runnable);
                        timer.postDelayed(runnable = () -> {
                            try{
                                ((Dashboard) getActivity()).showCalendar();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            backgroundEdit.setVisibility(View.GONE);
                        }, 200);
                    }
                });
            }else {
                try {
                    ((Dashboard) (getActivity())).showCannotEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void showData(){
        if(okToShow){
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
            for(int i =0;i< workerList.size();i++){
                workerList.get(i).setSelectedDay(((Dashboard) (getActivity())).getSelectedDate());
            }
            showWorkers();
        }
    }

    public static class Root{
        public String respond;
    }
}