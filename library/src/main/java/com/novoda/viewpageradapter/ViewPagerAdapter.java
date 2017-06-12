package com.novoda.viewpageradapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ViewPagerAdapter<V extends View> extends PagerAdapter {

    private static final String STATE_KEY_VIEW_STATES = "states";
    private final Map<V, Integer> instantiatedViews = new HashMap<>();               // TODO errrrrrrrrrrrrr
    private SparseArray<Parcelable> viewStates = new SparseArray<>();
    private final Set<Integer> knownIds = new HashSet<>();

    @Override
    public V instantiateItem(ViewGroup container, int position) {
        V view = createView(container, position);

        if (expectsToSaveStateAt(position)) {
            assertViewStateCanBeSavedFor(view, position);
        }

        bindView(view, position, viewStates);
        instantiatedViews.put(view, position);
        container.addView(view);
        saveViewIdIfSet(view);

        // key with which to associate this view
        return view;
    }

    protected boolean expectsToSaveStateAt(int position) {
        return false;
    }

    private void assertViewStateCanBeSavedFor(V view, int position) {
        if (view.getId() == View.NO_ID || knownIds.contains(view.getId())) {
            throw new IllegalArgumentException("The view created for position " + position + " must have a unique view ID");
        }
    }

    private void saveViewIdIfSet(V view) {
        if (view.getId() == View.NO_ID) {
            return;
        }
        knownIds.add(view.getId());
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
     * @param view       the view to bind
     * @param position   the position of the data set that is to be represented by this view
     * @param viewStates all the saved view states
     */
    protected final void bindView(V view, int position, SparseArray<Parcelable> viewStates) {
        bindView(view, position);
        if (expectsToSaveStateAt(position)) {
            view.restoreHierarchyState(viewStates);
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
            bindView(entry.getKey(), position, viewStates);
        }
    }

    @SuppressWarnings("unchecked")    // `key` is the object we return in `instantiateItem(ViewGroup container, int position)`
    @Override
    public void destroyItem(ViewGroup container, int position, Object key) {
        V view = (V) key;
        saveViewState(view);
        container.removeView(view);
        instantiatedViews.remove(view);

        if (view.getId() != View.NO_ID) {
            knownIds.remove(view.getId());
        }
    }

    private void saveViewState(V view) {
        view.saveHierarchyState(viewStates);
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (viewStates.size() > 0) {
            state = new Bundle();
            state.putSparseParcelableArray(STATE_KEY_VIEW_STATES, viewStates);
        }
        return state;
    }

    @Override
    public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
        super.restoreState(state, loader);
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            viewStates = bundle.getSparseParcelableArray(STATE_KEY_VIEW_STATES);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object key) {
        return view.getId() == ((View) key).getId();
    }
}
