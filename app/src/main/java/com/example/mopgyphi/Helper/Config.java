package com.example.mopgyphi.Helper;


public class Config {

    public static String trendingURLRequest = "http://api.giphy.com/v1/gifs/trending?api_key=Xxie4OYLx9ohRD42CWCGDGrpSI8LaRGg&limit=18";
    public static String searchTrendingURLRequest = "http://api.giphy.com/v1/gifs/search?api_key=Xxie4OYLx9ohRD42CWCGDGrpSI8LaRGg";
    public static String getGifByIdRequest = "http://api.giphy.com/v1/gifs?api_key=Xxie4OYLx9ohRD42CWCGDGrpSI8LaRGg";
    public static String VideoUploadPost = "http://upload.giphy.com/v1/gifs?api_key=Xxie4OYLx9ohRD42CWCGDGrpSI8LaRGg";


    public static String getTrendingURLoffest(int offset)
    {
        return trendingURLRequest+"&offset="+offset;
    }

    public static String getSearchResult(int offset, String query)
    {
        return searchTrendingURLRequest+"&q="+query+"&offset="+offset+"&limit=18";
    }

    public static String getGifById(String gifId)
    {
        return getGifByIdRequest+"&ids="+gifId;
    }

    public static String uploadVideo()
    {
        return VideoUploadPost;
    }

}
