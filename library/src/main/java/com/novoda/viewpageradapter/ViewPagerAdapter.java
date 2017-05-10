package com.novoda.viewpageradapter;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public abstract class ViewPagerAdapter<V extends View> extends PagerAdapter {

    private final Map<V, Integer> instantiatedViews = new HashMap<>();
    private final ViewIdGenerator viewIdGenerator = new ViewIdGenerator();

    private ViewPagerAdapterState viewPagerAdapterState = ViewPagerAdapterState.newInstance();

    @Override
    public V instantiateItem(ViewGroup container, int position) {
        V view = createView(container, position);
        SparseArray<Parcelable> viewState = viewPagerAdapterState.getViewState(position);

        int restoredId = viewPagerAdapterState.getId(position);
        view.setId(restoredId == View.NO_ID ? viewIdGenerator.generateViewId() : restoredId);

        bindView(view, position, viewState);
        instantiatedViews.put(view, position);
        container.addView(view);

        // key with which to associate this view
        return view;
    }

    /**
     * Inflate the view representing an item at the given position.
     * <p>
     * Do not add the view to the container, this is handled.
     *
     * @param container the parent view from which sizing information can be grabbed during inflation
     * @param position  the position of the data set that is to be represented by this view
     * @return the inflated and view
     */
    protected abstract V createView(ViewGroup container, int position);

    /**
     * Bind the view to the item at the given position with view state.
     *
     * @param view      the view to bind
     * @param position  the position of the data set that is to be represented by this view
     * @param viewState the state of the view
     */
    protected void bindView(V view, int position, @Nullable SparseArray<Parcelable> viewState) {
        bindView(view, position);
        if (viewState != null) {
            view.restoreHierarchyState(viewState);
        }
    }

    /**
     * Bind the view to the item at the given position.
     *
     * @param view     the view to bind
     * @param position the position of the data set that is to be represented by this view
     */
    protected void bindView(V view, int position) {
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            SparseArray<Parcelable> viewState = viewPagerAdapterState.getViewState(position);
            bindView(entry.getKey(), position, viewState);
        }
    }

    @SuppressWarnings("unchecked")
    // `key` is the object we return in `instantiateItem(ViewGroup container, int position)`
    @Override
    public void destroyItem(ViewGroup container, int position, Object key) {
        V view = (V) key;
        saveViewState(position, view);
        container.removeView(view);
        instantiatedViews.remove(view);
    }

    private void saveViewState(int position, V view) {
        SparseArray<Parcelable> viewState = new SparseArray<>();
        view.saveHierarchyState(viewState);
        viewPagerAdapterState.put(view.getId(), position, viewState);
    }

    @Override
    public Parcelable saveState() {
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            V view = entry.getKey();
            saveViewState(position, view);
        }
        return viewPagerAdapterState;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state instanceof ViewPagerAdapterState) {
            this.viewPagerAdapterState = ((ViewPagerAdapterState) state);
        } else {
            super.restoreState(state, loader);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object key) {
        return view.equals(key);
    }
}
