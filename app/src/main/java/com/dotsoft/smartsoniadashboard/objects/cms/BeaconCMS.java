package com.dotsoft.smartsoniadashboard.objects.cms;

import java.util.ArrayList;

public class BeaconCMS {

    public class CustomFields{
        public String beacon_id;
        public String info;
    }


    public class Result{
        public String ID;
        public CustomFields custom_fields;
    }

    public class Root{
        public String respond;
        public ArrayList<Result> result;
    }


}
