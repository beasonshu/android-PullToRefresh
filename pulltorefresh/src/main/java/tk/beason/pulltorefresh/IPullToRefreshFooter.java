package tk.beason.pulltorefresh;

import androidx.annotation.IntDef;

import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public interface IPullToRefreshFooter {
    /**
     * 普通状态
     */
    int STATUS_NORMAL = 0;
    /**
     * 准备状态
     */
    int STATUS_READY = 1;
    /**
     * 加载状态
     */
    int STATUS_LOADING = 2;
    /**
     * 刷新失败
     */
    int STATUS_FAILED = 3;
    /**
     * 全部加载完毕
     */
    int STATUS_END = 4;

    @IntDef({STATUS_NORMAL, STATUS_READY, STATUS_LOADING, STATUS_FAILED, STATUS_END})
    @Retention(RetentionPolicy.SOURCE)
    @interface status {
    }

    /**
     * 初始化
     */
    void init(ViewGroup parent);
    /**
     * 设置状态
     */
    void setStatus(@IPullToRefreshFooter.status int status);
    /**
     * 获取当前状态
     */
    int getStatus();
    /**
     * 加载成功
     */
    int loadSuccess(ViewGroup parent);

    /**
     * 加载失败
     */
    int loadFailed(ViewGroup parent);
    /**
     * 是否可用
     */
    void setEnabled(boolean enable);

    /**
     * 设置点击Footer的事件
     */
    void setOnClickFooterListener(View.OnClickListener listener);
}
