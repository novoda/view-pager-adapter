package com.novoda.viewpageradapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;

public class ViewPagerAdapterState implements Parcelable {

    public static final Creator<ViewPagerAdapterState> CREATOR = new Creator<ViewPagerAdapterState>() {

        public ViewPagerAdapterState createFromParcel(Parcel in) {
            return ViewPagerAdapterState.from(in);
        }

        public ViewPagerAdapterState[] newArray(int size) {
            return new ViewPagerAdapterState[size];
        }
    };

    private static final String KEY_VIEW_IDS = "id";
    private static final String KEY_VIEW_STATE = "state";

    private final SparseIntArray viewIds;
    private final SparseArray<SparseArray<Parcelable>> viewStates;

    public static ViewPagerAdapterState newInstance() {
        SparseIntArray viewIds = new SparseIntArray();
        SparseArray<SparseArray<Parcelable>> viewStates = new SparseArray<>();
        return new ViewPagerAdapterState(viewIds, viewStates);
    }

    private static ViewPagerAdapterState from(Parcel in) {
        Bundle bundle = in.readBundle(ViewPagerAdapterState.class.getClassLoader());
        SparseArray<SparseArray<Parcelable>> viewStates = extractViewStatesFrom(bundle);
        SparseIntArray viewIds = extractIdsFrom(bundle);
        return new ViewPagerAdapterState(viewIds, viewStates);
    }

    private static SparseIntArray extractIdsFrom(Bundle bundle) {
        SparseIntArray output = new SparseIntArray();
        int[] ids = bundle.getIntArray(KEY_VIEW_IDS);
        for (int index = 0; index < ids.length; index++) {
            output.put(index, ids[index]);
        }
        return output;
    }

    private static SparseArray<SparseArray<Parcelable>> extractViewStatesFrom(Bundle bundle) {
        Bundle viewStateBundle = bundle.getBundle(KEY_VIEW_STATE);
        SparseArray<SparseArray<Parcelable>> viewStates = new SparseArray<>(viewStateBundle.keySet().size());
        for (String key : viewStateBundle.keySet()) {
            SparseArray<Parcelable> sparseParcelableArray = viewStateBundle.getSparseParcelableArray(key);
            viewStates.put(Integer.parseInt(key), sparseParcelableArray);
        }
        return viewStates;
    }

    private ViewPagerAdapterState(SparseIntArray viewIds, SparseArray<SparseArray<Parcelable>> viewStates) {
        this.viewIds = viewIds;
        this.viewStates = viewStates;
    }

    public void put(int viewId, int position, SparseArray<Parcelable> viewState) {
        viewIds.put(position, viewId);
        viewStates.put(position, viewState);
    }

    public SparseArray<Parcelable> getViewState(int position) {
        return viewStates.get(position);
    }

    public int getId(int position) {
        return viewIds.get(position, View.NO_ID);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        int[] viewIds = viewIdsArray();
        bundle.putIntArray(KEY_VIEW_IDS, viewIds);
        Bundle viewStateBundle = viewStateBundle();
        bundle.putBundle(KEY_VIEW_STATE, viewStateBundle);
        dest.writeBundle(bundle);
    }

    private int[] viewIdsArray() {
        int[] output = new int[viewIds.size()];
        for (int index = 0; index < viewIds.size(); index++) {
            output[index] = viewIds.get(index);
        }
        return output;
    }

    private Bundle viewStateBundle() {
        Bundle viewStateBundle = new Bundle();
        for (int i = 0; i < viewStates.size(); i++) {
            SparseArray<Parcelable> viewState = viewStates.get(i);
            viewStateBundle.putSparseParcelableArray(String.valueOf(i), viewState);
        }
        return viewStateBundle;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
