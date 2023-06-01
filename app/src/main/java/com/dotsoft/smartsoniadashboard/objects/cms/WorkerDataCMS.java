package com.dotsoft.smartsoniadashboard.objects.cms;

import java.util.ArrayList;

public class WorkerDataCMS {

    public class CustomFields{
        public String height;
        public String timestamp;
        public String heartrate;
        public String location;
        public String beacon_id;
        public String beacon_distance;
    }


    public class Result{
        public CustomFields custom_fields;
    }

    public class Root{
        public String respond;
        public ArrayList<Result> result;
    }


}
