package com.abrenoch.hyperiongrabber.tv.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.leanback.widget.GuidanceStylist;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Relative layout implementation that lays out child views based on provided keyline percent(
 * distance of TitleView baseline from the top).
 *
 * Repositioning child views in PreDraw callback in {@link GuidanceStylist} was interfering with
 * fragment transition. To avoid that, we do that in the onLayout pass.
 *
 * Nino: Copied from Leanback code, changed to align icon bottom with description bottom
 */
class SettingsRelativeLayout extends RelativeLayout {
    private float mTitleKeylinePercent;

    public SettingsRelativeLayout(Context context) {
        this(context, null);
    }

    public SettingsRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTitleKeylinePercent = getKeyLinePercent(context);
    }

    public static float getKeyLinePercent(Context context) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                androidx.leanback.R.styleable.LeanbackGuidedStepTheme);
        float percent = ta.getFloat(androidx.leanback.R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40);
        ta.recycle();
        return percent;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        View mTitleView = getRootView().findViewById(androidx.leanback.R.id.guidance_title);
        View mBreadcrumbView = getRootView().findViewById(androidx.leanback.R.id.guidance_breadcrumb);
        View mDescriptionView = getRootView().findViewById(
                androidx.leanback.R.id.guidance_description);
        ImageView mIconView = getRootView().findViewById(androidx.leanback.R.id.guidance_icon);
        int mTitleKeylinePixels = (int) (getMeasuredHeight() * mTitleKeylinePercent / 100);

        if (mTitleView != null && mTitleView.getParent() == this) {
            int titleViewBaseline = mTitleView.getBaseline();
            int mBreadcrumbViewHeight = mBreadcrumbView == null ? 0 : mBreadcrumbView.getMeasuredHeight();
            int guidanceTextContainerTop = mTitleKeylinePixels
                    - titleViewBaseline - mBreadcrumbViewHeight - mTitleView.getPaddingTop();
            int offset = guidanceTextContainerTop - (mBreadcrumbView == null ? 0 : mBreadcrumbView.getTop());

            if (mBreadcrumbView != null && mBreadcrumbView.getParent() == this) {
                mBreadcrumbView.offsetTopAndBottom(offset);
            }

            mTitleView.offsetTopAndBottom(offset);

            if (mDescriptionView != null && mDescriptionView.getParent() == this) {
                mDescriptionView.offsetTopAndBottom(offset);
            }
        }

        if (mIconView != null && mIconView.getParent() == this) {
            Drawable drawable = mIconView.getDrawable();
            if (drawable != null && mDescriptionView != null) {
                int iconOffset = mDescriptionView.getBottom() - mIconView.getBottom();
                mIconView.offsetTopAndBottom(
                        iconOffset);
            }
        }
    }
}
