package tk.beason.pulltorefresh;

import android.animation.Animator;
import android.animation.ValueAnimator;

/**

 * 动画监听
 */

public abstract class AbsAnimatorListener implements Animator.AnimatorListener {

    public abstract void onAnimationUpdate(ValueAnimator animation);

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
