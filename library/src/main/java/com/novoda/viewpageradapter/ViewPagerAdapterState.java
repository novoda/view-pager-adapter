package com.novoda.viewpageradapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class ViewPagerAdapterState implements Parcelable {

    public static final Creator<ViewPagerAdapterState> CREATOR = new Creator<ViewPagerAdapterState>() {

        public ViewPagerAdapterState createFromParcel(Parcel in) {
            return ViewPagerAdapterState.from(in);
        }

        public ViewPagerAdapterState[] newArray(int size) {
            return new ViewPagerAdapterState[size];
        }
    };

    private final SparseArray<SparseArray<Parcelable>> viewStates;

    public static ViewPagerAdapterState newInstance() {
        return new ViewPagerAdapterState(new SparseArray<SparseArray<Parcelable>>());
    }

    private static ViewPagerAdapterState from(Parcel in) {
        Bundle bundle = in.readBundle();
        SparseArray<SparseArray<Parcelable>> viewStates = extractViewStatesFrom(bundle);
        return new ViewPagerAdapterState(viewStates);
    }

    private static SparseArray<SparseArray<Parcelable>> extractViewStatesFrom(Bundle bundle) {
        SparseArray<SparseArray<Parcelable>> viewStates = new SparseArray<>(bundle.keySet().size());
        for (String key : bundle.keySet()) {
            SparseArray<Parcelable> sparseParcelableArray = bundle.getSparseParcelableArray(key);
            viewStates.put(Integer.parseInt(key), sparseParcelableArray);
        }
        return viewStates;
    }

    private ViewPagerAdapterState(SparseArray<SparseArray<Parcelable>> viewStates) {
        this.viewStates = viewStates;
    }

    public void put(int position, SparseArray<Parcelable> viewState) {
        viewStates.put(position, viewState);
    }

    public SparseArray<Parcelable> get(int position) {
        return viewStates.get(position);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        for (int i = 0; i < viewStates.size(); i++) {
            SparseArray<Parcelable> viewState = viewStates.get(i);
            bundle.putSparseParcelableArray(String.valueOf(i), viewState);
        }
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
