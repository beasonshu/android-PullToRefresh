package tk.beason.pulltorefresh.headers.classic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import tk.beason.pulltorefresh.AbsAnimatorListener;
import tk.beason.pulltorefresh.IPullToRefreshHeader;
import tk.beason.pulltorefresh.R;
import tk.beason.pulltorefresh.utils.ViewHelper;



public class ClassicHeader extends LinearLayout implements IPullToRefreshHeader {
    private static final int ROTATE_ANIM_DURATION = 380;
    /**
     * 当前状态
     */
    private int mStatus;
    /**
     * Icon
     */
    private ImageView mIconView;
    /**
     * Text
     */
    private TextView mTextView;
    /**
     * 进度条
     */
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;
    private RotateAnimation mRotateLoading;

    public ClassicHeader(Context context) {
        this(context, null);
    }

    public ClassicHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        setMinimumHeight(context.getResources().getDimensionPixelOffset(R.dimen.pull2refresh_classic_header_min_h));

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        mRotateLoading = new RotateAnimation(0, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateLoading.setDuration(ROTATE_ANIM_DURATION * 2);
        mRotateLoading.setRepeatCount(Animation.INFINITE);
        mRotateLoading.setFillAfter(false);

        addIcon();
        addText();
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setStatus(@status int status) {
        switch (status) {
            case STATUS_NORMAL:
                mIconView.setImageResource(R.drawable.ic_pull2refresh_arrow);
                mTextView.setText(R.string.pull2refresh_header_normal);
                if (mStatus == STATUS_READY) {
                    mIconView.startAnimation(mRotateDownAnim);
                }

                if (mStatus == STATUS_REFRESHING) {
                    mIconView.clearAnimation();
                }

                break;
            case STATUS_READY:
                if (mStatus != STATUS_READY) {
                    mIconView.clearAnimation();
                    mIconView.startAnimation(mRotateUpAnim);
                }
                mIconView.setImageResource(R.drawable.ic_pull2refresh_arrow);
                mTextView.setText(R.string.pull2refresh_header_ready);
                break;
            case STATUS_REFRESHING:
                mIconView.clearAnimation();
                mIconView.setImageResource(R.drawable.ic_pull2refresh_loading);
                mIconView.startAnimation(mRotateLoading);
                mTextView.setText(R.string.pull2refresh_header_loading);
                requestLayout();
                break;
            case STATUS_FAILED:
                mIconView.clearAnimation();
                mIconView.setVisibility(VISIBLE);
                mIconView.setImageResource(R.drawable.ic_pull2refresh_refresh_fail);
                mTextView.setText(R.string.pull2refresh_header_fail);
                break;
            case STATUS_SUCCESS:
                mIconView.clearAnimation();
                mIconView.setVisibility(VISIBLE);
                mIconView.setImageResource(R.drawable.ic_pull2refresh_refresh_success);
                mTextView.setText(R.string.pull2refresh_header_success);
                break;
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
    public int moving(ViewGroup parent, int offset, int fitTop) {
        return 0;
    }

    @Override
    public int refreshing(ViewGroup parent, int fitTop, @Nullable AbsAnimatorListener listener) {
        return 0;
    }


    @Override
    public int cancelRefresh(ViewGroup parent) {
        int offset = -getTop() - getHeaderHeight();
        ViewHelper.movingY(this, offset);
        return offset;
    }

    @Override
    public int refreshSuccess(ViewGroup parent, int fitTop) {
        return 0;
    }

    @Override
    public int refreshFailed(ViewGroup parent, int fitTop) {
        return 0;
    }


    @Override
    public int getMaxPullDownHeight() {
        return (getMeasuredHeight() * 3);
    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }

    @Override
    public boolean isEffectiveDistance(int fitTop) {
        return false;
    }


    private void addIcon() {
        if (mIconView == null) {
            int size = getImageSize();
            mIconView = new ImageView(getContext());
            mIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LayoutParams lp = new LayoutParams(size, size);
            mIconView.setImageResource(R.drawable.ic_pull2refresh_arrow);
            addView(mIconView, lp);
        }
    }


    private void addText() {
        if (mTextView == null) {
            mTextView = new AppCompatTextView(getContext());
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 15;
            mTextView.setText(R.string.pull2refresh_header_normal);
            addView(mTextView, lp);
        }
    }


    private int getImageSize() {
        return getContext().getResources().getDimensionPixelOffset(R.dimen.pull2refresh_classic_header_image_size);
    }
}
