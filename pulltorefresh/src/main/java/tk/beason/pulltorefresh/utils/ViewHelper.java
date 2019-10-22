package tk.beason.pulltorefresh.utils;

import android.animation.ValueAnimator;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import tk.beason.pulltorefresh.AbsAnimatorListener;
import tk.beason.pulltorefresh.R;

/**
 * Created by Bright.Yu on 2017/3/27.
 * View Helper
 */

public class ViewHelper {
    /**
     * 每1000 像素 需要移动的时间
     */
    private static final int DEFAULT_1000_PIXELS_TIMES = 1200;

    /**
     * View 进行移动
     */
    public static void movingY(final @NonNull View view, int distance) {
        int duration = calculatingDuration(distance);
        movingY(view, duration, distance, null);
    }

    @SuppressWarnings("WeakerAccess")
    public static void movingY(final @NonNull View view, int distance, final AbsAnimatorListener listener) {
        int duration = calculatingDuration(distance);
        movingY(view, duration, distance, listener);
    }

    @SuppressWarnings("WeakerAccess")
    public static void movingY(final @NonNull View view, @IntRange(from = 1) int duration, int distance, final AbsAnimatorListener listener) {
        view.clearAnimation();

        ValueAnimator animator = ValueAnimator.ofInt(distance);
        animator.setTarget(view);
        animator.setDuration(duration);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
        view.setTag(R.id.tag_pull2refresh_moving_y, 0);
        view.setTag(R.id.tag_pull2refresh_animation, animator);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int last = (int) view.getTag(R.id.tag_pull2refresh_moving_y);
                final int move = (int) animation.getAnimatedValue();
                int offset = move - last;
                view.setTag(R.id.tag_pull2refresh_moving_y, move);
                ViewCompat.offsetTopAndBottom(view, offset);
                if (listener != null) {
                    listener.onAnimationUpdate(animation);
                }
            }
        });
    }

    /**
     * 计算移动动画时长
     */
    private static int calculatingDuration(final int distance) {
        int desTime = DEFAULT_1000_PIXELS_TIMES * Math.abs(distance) / 1000;
        return Math.max(desTime, 500);
    }
}
