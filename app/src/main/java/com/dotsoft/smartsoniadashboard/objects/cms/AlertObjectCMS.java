package com.dotsoft.smartsoniadashboard.objects.cms;

import java.util.ArrayList;

public class AlertObjectCMS {

    public class PostMeta{
        public String worker_name;
        public String risk_grading;
        public String read;
        public String timestamp;
        public String date_split_alert;
    }

    public class Result{
        public String ID;
        public String post_title;
        public PostMeta postmeta;
    }

    public class Root{
        public String respond;
        public ArrayList<Result> result;
    }

}
