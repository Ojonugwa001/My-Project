package com.example.ojonugwa.barcodescanner;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Ojonugwa on 3/18/2018.
 */

public class Scanner {
    private ObjectAnimator animator;
    private View mScannerLayout;
    private View mScannerBar;

    public void scannerAnimator(View scannerLayout, View scannerBar){

        //Scanner overlay
        mScannerLayout = scannerLayout;
        mScannerBar = scannerBar;

        animator = null;

        ViewTreeObserver vto = mScannerLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mScannerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mScannerLayout.getViewTreeObserver().
                            removeGlobalOnLayoutListener(this);

                } else {
                    mScannerLayout.getViewTreeObserver().
                            removeOnGlobalLayoutListener(this);
                }

                float destination = (mScannerLayout.getY() +
                        mScannerLayout.getHeight());

                animator = ObjectAnimator.ofFloat(mScannerBar, "translationY",
                        mScannerLayout.getY(),
                        destination);

                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(1000);
                animator.start();

            }
        });
    }
}
