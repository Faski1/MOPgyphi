package com.example.mopgyphi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mopgyphi.Helper.Config;
import com.example.mopgyphi.Helper.GlideApp;
import com.example.mopgyphi.Helper.MyGson;
import com.example.mopgyphi.Models.Gif;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GifOpenedFragment extends Fragment {

    private String mParam1;

    private OnFragmentInteractionListener mListener;

    ImageView imageView;
    public GifOpenedFragment() {
        // Required empty public constructor
    }

    public static GifOpenedFragment newInstance() {
        GifOpenedFragment fragment = new GifOpenedFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gif_opened, container, false);
        imageView = view.findViewById(R.id.fullScreenGif);
        final SwipeRefreshLayout pullToRefresh2 = view.findViewById(R.id.pullToRefresh2);
        pullToRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Glide.with(view.getContext()).asGif().load(mParam1)
                        .placeholder(R.drawable.progress_animation)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(imageView);

                pullToRefresh2.setRefreshing(false);
            }
        });

        GlideApp.with(view.getContext()).asGif().load(mParam1)
                .placeholder(R.drawable.progress_animation)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);

        return view;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
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
        void onFragmentInteraction();
    }
}
