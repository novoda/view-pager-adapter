package com.novoda.viewpageradapter;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

public class ViewPagerAdapterTest {

    private static final int NO_ID = -1;
    private static final int A_POSITON = 0;
    @IdRes
    private static final int AN_ID = 0x5c;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    ViewIdGenerator viewIdGenerator;

    @Mock
    ViewPagerAdapterState viewPagerAdapterState;

    @Mock
    LinearLayout container;

    @Mock
    Map<View, Integer> instansiatedViews;

    StubView createdView;

    private StubAdapter stubAdapter;

    @Before
    public void setUp() {
        stubAdapter = spy(new StubAdapter());
        createdView = spy(new StubView());
        given(viewPagerAdapterState.getId(anyInt())).willReturn(NO_ID);
    }

    @Test
    public void instantiated() {
        InOrder inOrder = inOrder(stubAdapter, createdView, instansiatedViews, container);
        given(viewPagerAdapterState.getId(A_POSITON)).willReturn(NO_ID);
        given(viewIdGenerator.generateViewId()).willReturn(AN_ID);

        View instantiatedItem = stubAdapter.instantiateItem(container, A_POSITON);

        inOrder.verify(stubAdapter).createView(container, A_POSITON);
        inOrder.verify(createdView).setId(AN_ID);
        inOrder.verify(stubAdapter).bindView(createdView, A_POSITON);
        inOrder.verify(instansiatedViews).put(createdView, A_POSITON);
        inOrder.verify(container).addView(createdView);

        assert instantiatedItem.equals(createdView);
    }

    @Test
    public void givenNoViewIdState_whenInstantiatingItem_thenTheViewIdIsGenerated() {
        int expectedViewId = 1000;
        given(viewPagerAdapterState.getId(A_POSITON)).willReturn(NO_ID);
        given(viewIdGenerator.generateViewId()).willReturn(expectedViewId);

        View instantiatedItem = stubAdapter.instantiateItem(container, A_POSITON);

        assert instantiatedItem.getId() == expectedViewId;
    }

    @Test
    public void givenViewIdState_whenInstantiatingItem_thenTheViewIdIsRestored() {
        int expectedViewId = 1000;
        given(viewPagerAdapterState.getId(A_POSITON)).willReturn(expectedViewId);

        View instantiatedItem = stubAdapter.instantiateItem(container, A_POSITON);

        assert instantiatedItem.getId() == expectedViewId;
    }

    @Test
    public void givenAViewHasState_whenInstantiatingItem_thenTheViewStateIsRestored() {
        // TODO
    }

    @Test
    public void givenAViewHasNoState_whenInstantiatingItem_thenTheNoViewStateIsRestored() {
        // TODO
    }

    private class StubAdapter extends ViewPagerAdapter<View> {

        StubAdapter() {
            super(instansiatedViews, viewIdGenerator, viewPagerAdapterState);
        }

        @Override
        protected View createView(ViewGroup container, int position) {
            return createdView;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    private class StubView extends View {

        private int id;

        public StubView() {
            super(null);
        }

        @Override
        public void setId(@IdRes int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

}