package tk.beason.pulltorefresh.headers.google;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import tk.beason.pulltorefresh.AbsAnimatorListener;
import tk.beason.pulltorefresh.IPullToRefreshHeader;
import tk.beason.pulltorefresh.utils.UnitUtils;
import tk.beason.pulltorefresh.utils.ViewHelper;


public class GoogleStyleHeader extends LinearLayout implements IPullToRefreshHeader {

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private CircularProgressDrawable mProgress;
    /**
     * 当前状态
     */
    private int mStatus;
    private int mMaxPullDownHeight;

    public GoogleStyleHeader(Context context) {
        super(context);
        init();
    }

    private void init() {
        final int padding = UnitUtils.dip2px(8);
        setGravity(Gravity.CENTER);
        final int size = UnitUtils.dip2px(40);
        mMaxPullDownHeight = size * 4;
        LayoutParams lp = new LayoutParams(size, size);
        lp.topMargin = padding;
        lp.bottomMargin = padding;


        mProgress = new CircularProgressDrawable(getContext());
        mProgress.setStyle(CircularProgressDrawable.DEFAULT);
        mProgress.setColorSchemeColors(
                ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(getContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(getContext(), android.R.color.holo_orange_light),
                ContextCompat.getColor(getContext(), android.R.color.holo_red_light));

        CircleImageView imageView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT);
        imageView.setImageDrawable(mProgress);

        addView(imageView, lp);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setStatus(int status) {
        if (status == STATUS_NORMAL && mStatus != STATUS_NORMAL && mStatus != STATUS_READY) {
            mProgress.stop();
        }
        mStatus = status;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public int getMovingDistance() {
        return Math.abs(getBottom());
    }

    @Override
    public int moving(ViewGroup parent, int offset) {
        final float moving = getBottom();
        final int height = getMeasuredHeight();
        float originalDragPercent = Math.max(moving - height, 0) / (mMaxPullDownHeight - height);

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float strokeStart = dragPercent * .8f;
        mProgress.setArrowEnabled(true);
        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, dragPercent));

        if (Math.abs(moving) >= getMaxPullDownHeight() && offset < 0) {
            return 0;
        } else if (getTop() - offset < -getHeaderHeight()) {
            int adjust = getHeaderHeight() + getTop();
            ViewCompat.offsetTopAndBottom(this, -adjust);
        } else {
            ViewCompat.offsetTopAndBottom(this, -offset);
        }
        return 0;
    }

    @Override
    public int refreshing(ViewGroup parent, @Nullable AbsAnimatorListener listener) {
        int offset = -getTop();
        ViewHelper.movingY(this, offset, listener);
        mProgress.start();
        return 0;
    }

    @Override
    public int cancelRefresh(ViewGroup parent) {
        int offset = -getTop() - getHeaderHeight();
        ViewHelper.movingY(this, offset);
        return 0;
    }

    @Override
    public int refreshSuccess(ViewGroup parent) {
        int offset = -getHeaderHeight();
        ViewHelper.movingY(this, offset);
        return 0;
    }

    @Override
    public int refreshFailed(ViewGroup parent) {
        int offset = -getHeaderHeight();
        ViewHelper.movingY(this, offset);
        return 0;
    }

    @Override
    public int getMaxPullDownHeight() {
        return mMaxPullDownHeight;
    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }

    @Override
    public boolean isEffectiveDistance() {
        return Math.abs(getBottom()) > getHeaderHeight();
    }

    /**
     * 设置颜色样式
     */
    @SuppressWarnings("unused")
    public void setColorSchemeColors(@ColorInt int... colors) {
        mProgress.setColorSchemeColors(colors);
    }

    /**
     * 设置颜色样式
     */
    @SuppressWarnings("unused")
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        final Context context = getContext();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }
}
