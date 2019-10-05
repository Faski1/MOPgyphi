package com.example.mopgyphi.Models;

import com.google.gson.annotations.SerializedName;

public class Images {

    @SerializedName("fixed_height")
    fixed_height fixed_height;
    @SerializedName("downsized_large")
    downsized_large downsized_large;

    public String getUrl() {
        return fixed_height.getUrl();
    }

    public String getUrldownlarge() {
        return downsized_large.getUrl();
    }

    public void setUrl(String url) {
        this.fixed_height.setUrl(url);
    }
}
