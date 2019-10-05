package com.example.mopgyphi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.example.mopgyphi.Helper.Config;
import com.example.mopgyphi.Helper.GridAdapter;
import com.example.mopgyphi.Helper.roomHelper.AppDatabase;
import com.example.mopgyphi.Helper.roomHelper.GifEntity;
import com.example.mopgyphi.Models.Gif;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.room.Room;
import es.dmoral.toasty.Toasty;

import static com.example.mopgyphi.MainActivity.db;


public class TrendingHomeFragment extends Fragment {
    private static final int FILE_PICKER_CODE = 3;
    private List<Gif> listaGifova = new ArrayList<>();
    GridView gridView;
    GridAdapter gridAdapter;
    private int DiscoverPage = 0;
    private int myLastVisiblePos;
    SearchView searchView;
    boolean firstSearch = true;
    public static boolean searching;
    public static String globalQuery;
    private OnFragmentInteractionListener mListener;
    FloatingActionButton fab;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    private ProgressDialog mDialog;
    Context context;

    List<GifEntity>  gifEntities = new ArrayList<>();
    boolean empty;
    public TrendingHomeFragment() {
        // Required empty public constructor
    }

    public static TrendingHomeFragment newInstance() {
        TrendingHomeFragment fragment = new TrendingHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.enableLogging();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending_home, container, false);
        gridView = view.findViewById(R.id.GiphyGrid);
        searchView = view.findViewById(R.id.Pretraga);
        fab = view.findViewById(R.id.fab);
        mDialog = new ProgressDialog(view.getContext());
        context = view.getContext();

                if(db.gifDao().getAll()!=null)
                {
                    gifEntities.addAll(db.gifDao().getAll());
                    empty =false;
                }
                else{
                    empty =true;
                }

        /*mDialog.setCancelable("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // This will cancel the putFile operation
                //mRequest.abort();
            }
        });*/

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onFragmentInteraction(gifEntities.get(position).downsurl);
            }
        });


        gridView.setOnScrollListener(new EndlessScrollListener(){
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                GetMoreGifs();
                return true;
            }
        });
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                pullToRefresh.setRefreshing(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                firstSearch = false;
                searching=true;
                DiscoverPage=0;
                searchItems(s);
                globalQuery=s;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("") && !firstSearch) {
                    listaGifova.clear();
                    DiscoverPage=0;
                    searching=false;
                    globalQuery=s;
                    loadInEmptyList();
                }
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndOpenFilePicker(v);
            }
        });


        return view;
    }


    private void openFile(View v) {
        try {
            new MaterialFilePicker()
                    .withSupportFragment(this)
                    .withRequestCode(FILE_PICKER_REQUEST_CODE)
                    .withHiddenFiles(true)
                    .withFilter(Pattern.compile(".*\\.mp4$"))
                    .withPath("/storage/emulated/0/DCIM")
                    .start();
        }
        catch (Exception e){
            new MaterialFilePicker()
                    .withSupportFragment(this)
                    .withRequestCode(FILE_PICKER_REQUEST_CODE)
                    .withHiddenFiles(true)
                    .withFilter(Pattern.compile(".*\\.mp4$"))
                    .start();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (path != null) {
                File file = new File(path);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                mDialog.setMax(100);
                mDialog.setMessage("Uploading " + file.getName());
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDialog.setProgress(0);
                if(file_size<102400) {
                    //Log.d("Path (fragment): ", path);
                   // Toast.makeText(getContext(), "Picked file in fragment: " + path, Toast.LENGTH_LONG).show();

                    AndroidNetworking.upload(Config.uploadVideo())
                            .addMultipartFile("file", file)
                            //.addMultipartParameter("key","value")
                            //.setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .setTag("uptag")
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                   // Log.d("Uploaded:", ((bytesUploaded / totalBytes) + " %"));
                                    mDialog.show();
                                    int percent = (int)(100.0*(double)bytesUploaded/totalBytes + 0.5);
                                    mDialog.setProgress(percent);
                                    mDialog.setCancelable(true);
                                    mDialog.setCanceledOnTouchOutside(true);
                                    mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            if(mDialog.getProgress()!=100)
                                            AndroidNetworking.forceCancel("uptag");
                                            mDialog.cancel();
                                            mDialog.dismiss();
                                        }
                                    });
                                    if(mDialog.getProgress()==100)
                                    {
                                        mDialog.dismiss();
                                        Toasty.success(context, "USPJEH: Vaš video će ubrzo biti dostupan na giphy.com/channel/Faski", Toast.LENGTH_LONG, true).show();
                                    }
                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if(mDialog.getProgress()==100 && response.toString().contains("\"status\":400"))
                                    {
                                        Toasty.error(context, "Došlo je do upload greške, pokušajte ponovo.", Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                                @Override
                                public void onError(ANError error) {
                                    Toasty.info(context, "Video nije uploadovan.", Toast.LENGTH_SHORT, true).show();

                                    if(error.getErrorCode()==429)
                                    {
                                        Toasty.error(context, "Predjen je maksimalni broj dnevnih uploada.", Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                            });
                   /* FileInputStream is = null;
                    try
                    {
                        is = new FileInputStream(path);

                        byte [] byteArr = IOUtils.toByteArray(is);

                        AndroidNetworking.post(Config.uploadVideo(byteArr)).build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response)
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                            }
                        });
                    }
                    catch (IOException ioe)
                    {}
                    finally
                    {
                        // close things
                    }*/

                }
                else{
                    Log.d("Path file oversized: ", path);
                    Toasty.error(context, "Molimo odaberite video manji od 100MB.", Toast.LENGTH_SHORT, true).show();

                    //Toast.makeText(getContext(), "Molimo odaberite video manji od 100MB", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void checkPermissionsAndOpenFilePicker(View v) {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                showError(v);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFile(v);
        }
    }

    private void showError(View v) {
        Toasty.error(v.getContext(), "Morate dozvoliti storage permisije!", Toast.LENGTH_SHORT, true).show();

    }

    @Override
    public void onResume() {
        super.onResume();

        loadInEmptyList();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String GifURL);
    }

    private void searchItems(String s) {


        AndroidNetworking.get(Config.getSearchResult(DiscoverPage, s)).build().getAsJSONObject((new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                if(!empty)
                {
                    listaGifova.clear();
                    gifEntities.clear();
                    db.gifDao().deleteAll();
                    empty=true;
                }
                Gson gson = new Gson();
                try {
                    // Log.wtf("Odgovor:",response.toString());

                    JSONArray array = response.getJSONArray("data");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject myData = array.getJSONObject(i);
                        JsonObject element = gson.fromJson(myData.toString(), JsonObject.class);
                        Gif gifObj = gson.fromJson(element, Gif.class);
                        listaGifova.add(gifObj);
                        gifEntities.add(new GifEntity(gifObj.getImages().getUrldownlarge(), gifObj.getImages().getUrldownlarge()));
                        db.gifDao().insertAll(gifEntities.get(gifEntities.size()-1));
                        empty=false;
                    }
                    DiscoverPage += 30;
                    PostaviGrid();
                } catch (JSONException e) {
                }

                //Log.wtf("Velicina", Integer.toString(listaGifova.size()));
            }

            @Override
            public void onError(ANError anError) {
                Log.wtf("Greska:", anError.getResponse().message());
            }
        }));
    }
        private void refreshData() {
            listaGifova.clear();
            DiscoverPage=0;
            loadInEmptyList();
        }
        private void loadInEmptyList()
        {
            if(listaGifova.isEmpty())
            {
                if(!searching)
                AndroidNetworking.get(Config.trendingURLRequest).build().getAsJSONObject((new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!empty)
                        {
                                gifEntities.clear();
                            db.gifDao().deleteAll();
                                empty =true;
                        }
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Glide.get(context).clearDiskCache();

                            }
                        });
                        Gson gson = new Gson();
                        try {
                            // Log.wtf("Odgovor:",response.toString());

                            JSONArray array = response.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject myData = array.getJSONObject(i);
                                JsonObject element = gson.fromJson(myData.toString(), JsonObject.class);
                                Gif gifObj = gson.fromJson(element, Gif.class);
                                listaGifova.add(gifObj);
                                gifEntities.add(new GifEntity(gifObj.getImages().getUrldownlarge(), gifObj.getImages().getUrldownlarge()));
                                db.gifDao().insertAll(gifEntities.get(gifEntities.size()-1));
                                empty=false;
                                if(!MainActivity.online) {
                                    MainActivity.online = true;
                                    Toasty.success(context, "Dobrodošli natrag!", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                            DiscoverPage+=30;
                            PostaviGrid();
                        } catch (JSONException e) {
                        }

                        //Log.wtf("Velicina", Integer.toString(listaGifova.size()));
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Log.wtf("Greska:", anError.getResponse().message());
                        Toasty.error(context, "Provjerite vašu konekciju!", Toast.LENGTH_SHORT, true).show();
                        MainActivity.online=false;
                        PostaviGrid();
                    }
                }));
else {
    searchView.setQuery(globalQuery,true);
}

            }

        }
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            int pos=gridView.getFirstVisiblePosition();
            //Toast.makeText(this, "FirstVisible:"+pos, Toast.LENGTH_SHORT).show();
            gridView.setSelection(pos);

            gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 3);

        }
        private void do_upgradeGrid() {
            gridAdapter.notifyDataSetChanged();
        }
        private void PostaviGrid() {
            gridAdapter = new GridAdapter(context, gifEntities);
            gridView.setAdapter(gridAdapter);
        }

        public abstract class EndlessScrollListener  implements AbsListView.OnScrollListener {
            // The minimum number of items to have below your current scroll position
            // before loading more.
            private int visibleThreshold = 3;
            // The current offset index of data you have loaded
            private int currentPage = 1;
            // The total number of items in the dataset after the last load
            private int previousTotalItemCount = 0;
            // True if we are still waiting for the last set of data to load.
            private boolean loading = true;
            // Sets the starting page index
            private int startingPageIndex = 0;




            public EndlessScrollListener() {
            }



            public EndlessScrollListener(int visibleThreshold) {
                this.visibleThreshold = visibleThreshold;
            }

            public EndlessScrollListener(int visibleThreshold, int startPage) {
                this.visibleThreshold = visibleThreshold;
                this.startingPageIndex = startPage;
                this.currentPage = startPage;
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {

                // If the total item count is zero and the previous isn't, assume the
                // list is invalidated and should be reset back to initial state
                if (totalItemCount < previousTotalItemCount) {
                    this.currentPage = this.startingPageIndex;
                    this.previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) { this.loading = true; }

                }
                // If it's still loading, we check to see if the dataset count has
                // changed, if so we conclude it has finished loading and update the current page
                // number and total item count.
                if (loading && (totalItemCount > previousTotalItemCount)) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;

                }

                // If it isn't currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
                    loading = onLoadMore(currentPage + 1, totalItemCount);

                }
            }

            // Defines the process for actually loading more data based on page
            // Returns true if more data is being loaded; returns false if there is no more data to load.

            public boolean onLoadMore(int page, int totalItemsCount) {
                GetMoreGifs();
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Don't take any action on changed
            }
        }

        private void GetMoreGifs() {
            if (!searching) {
                AndroidNetworking.get(Config.getTrendingURLoffest(DiscoverPage)).build().getAsJSONObject((new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        try {
                            // Log.wtf("Odgovor:",response.toString());

                            JSONArray array = response.getJSONArray("data");
                            List<Gif> noviGifovi = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject myData = array.getJSONObject(i);
                                JsonObject element = gson.fromJson(myData.toString(), JsonObject.class);
                                Gif gifObj = gson.fromJson(element, Gif.class);
                                listaGifova.add(gifObj);
                                gifEntities.add(new GifEntity(gifObj.getImages().getUrldownlarge(), gifObj.getImages().getUrldownlarge()));
                                db.gifDao().insertAll(gifEntities.get(gifEntities.size()-1));
                            }
                            DiscoverPage += 30;
                            do_upgradeGrid();
                        } catch (JSONException e) {
                        }

                        //Log.wtf("Velicina", Integer.toString(listaGifova.size()));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.wtf("Greska:", anError.getResponse().message());
                    }
                }));
            }
            else {
                AndroidNetworking.get(Config.getSearchResult(DiscoverPage, globalQuery)).build().getAsJSONObject((new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        try {
                            // Log.wtf("Odgovor:",response.toString());

                            JSONArray array = response.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject myData = array.getJSONObject(i);
                                JsonObject element = gson.fromJson(myData.toString(), JsonObject.class);
                                Gif gifObj = gson.fromJson(element, Gif.class);
                                listaGifova.add(gifObj);
                                gifEntities.add(new GifEntity(gifObj.getImages().getUrldownlarge(), gifObj.getImages().getUrldownlarge()));
                                db.gifDao().insertAll(gifEntities.get(gifEntities.size()-1));
                            }
                            DiscoverPage += 30;
                            do_upgradeGrid();

                        } catch (JSONException e) {
                        }

                        //Log.wtf("Velicina", Integer.toString(listaGifova.size()));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.wtf("Greska:", anError.getResponse().message());
                    }
                }));
            }
        }
    }

