package com.ngenderic.datacollector;
public class CatItem {
    private String mCountryName;
    private String mId;

    public CatItem(String countryName,String id) {
        mCountryName = countryName;
        mId=id;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public String getmId() {
        return mId;
    }
}