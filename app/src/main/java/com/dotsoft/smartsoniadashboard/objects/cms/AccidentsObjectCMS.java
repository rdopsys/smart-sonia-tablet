package com.dotsoft.smartsoniadashboard.objects.cms;

import java.util.ArrayList;

public class AccidentsObjectCMS {

    public class CustomFields{
        public String worker_name;
        public String notes;
        public String dangerous_zone;
        public String timestamp;
        public String date_split_accident;
    }

    public class Result{
        public String ID;
        public String post_title;
        public CustomFields custom_fields;
    }

    public class Root{
        public String respond;
        public ArrayList<Result> result;
    }

}
