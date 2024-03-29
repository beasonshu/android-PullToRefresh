package tk.beason.pulltorefresh;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public interface IPullToRefreshHeader {

    /**
     * 正常状态
     */
    int STATUS_NORMAL = 0;
    /**
     * 准备状态
     */
    int STATUS_READY = 1;
    /**
     * 正在刷新
     */
    int STATUS_REFRESHING = 2;
    /**
     * 刷新成功
     */
    int STATUS_SUCCESS = 3;
    /**
     * 刷新失败
     */
    int STATUS_FAILED = 4;

    @IntDef({STATUS_NORMAL, STATUS_READY, STATUS_REFRESHING, STATUS_SUCCESS, STATUS_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    @interface status {
    }

    /**
     * 获取Header
     */
    @NonNull
    View getView();

    /**
     * 设置状态
     */
    void setStatus(@IPullToRefreshHeader.status int status);

    /**
     * 获取当前状态
     */
    int getStatus();

    /**
     * 获取移动的距离
     */
    int getMovingDistance();

    /**
     * 移动
     *
     * @param parent 父View
     * @param offset 当前事件移动的距离
     * @param fitTop fitsSystemWindows 为true的时候顶部高度
     * @return header移动的距离
     */
    int moving(ViewGroup parent, final int offset, final int fitTop);

    /**
     * 刷新中....
     *
     * @param fitTop fitsSystemWindows 为true的时候顶部高度
     */
    int refreshing(ViewGroup parent, final int fitTop, @Nullable AbsAnimatorListener listener);

    /**
     * 取消刷新
     */
    int cancelRefresh(ViewGroup parent);

    /**
     * 刷新成功
     *
     * @param fitTop fitsSystemWindows 为true的时候顶部高度
     */
    int refreshSuccess(ViewGroup parent, final int fitTop);

    /**
     * 刷新失败
     *
     * @param fitTop fitsSystemWindows 为true的时候顶部高度
     */
    int refreshFailed(ViewGroup parent, final int fitTop);

    /**
     * 获取下拉的最大高度
     */
    int getMaxPullDownHeight();

    /**
     * 获取Header的高度
     */
    int getHeaderHeight();

    /**
     * 判断当前移动距离是否是有效距离
     *
     * @param fitTop fitsSystemWindows 为true的时候顶部高度
     */
    boolean isEffectiveDistance(final int fitTop);
}
