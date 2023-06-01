package com.dotsoft.smartsoniadashboard.objects.cms;

import java.util.ArrayList;

public class SmartwatchCMS {

    public class CustomFields{
        public String id;
    }

    public class Result{
        public CustomFields custom_fields;
    }

    public class Root{
        public String respond;
        public ArrayList<Result> result;
    }



}
