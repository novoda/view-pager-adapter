package com.novoda.viewpageradapter;

import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

public class ViewIdGenerator {

    public int generateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            return PreApi17ViewIdGenerator.generateViewId();
        }
    }

    private static final class PreApi17ViewIdGenerator { // copied (and reformatted) from View

        private static final AtomicInteger NEXT_GENERATED_ID = new AtomicInteger(1);
        private static final int START_OF_AAPT_GENERATED_IDS = 0x00FFFFFF;

        private PreApi17ViewIdGenerator() {
            // utility class
        }

        /**
         * Generate a value suitable for use in {@link #setId(int)}.
         * This value will not collide with ID values generated at build time by aapt for R.id.
         *
         * @return a generated ID value
         */
        public static int generateViewId() {
            while (true) {
                final int result = NEXT_GENERATED_ID.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > START_OF_AAPT_GENERATED_IDS) {
                    newValue = 1; // Roll over to 1, not 0.
                }
                if (NEXT_GENERATED_ID.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }
}
