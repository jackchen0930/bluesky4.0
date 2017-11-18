package com.tongyong.ct.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.tongyong.ct.R;


/**
 * SwipeRefreshLayout基类
 */
@SuppressLint("ResourceAsColor")
public class BaseSwipeRefreshLayout extends SwipeRefreshLayout {

    private CanChildScrollUpCallback mCanChildScrollUpCallback;

	public BaseSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BaseSwipeRefreshLayout(Context context) {
		super(context);
		init();
	}

	private void init(){
		//白天和默认配色方案
		this.setColorSchemeColors(getResources().getColor(R.color.schem_color1),
				getResources().getColor(R.color.schem_color2),
				getResources().getColor(R.color.schem_color3),
				getResources().getColor(R.color.schem_color4));
		this.setProgressBackgroundColorSchemeResource(R.color.back_swipe_day);
        //v21版初始化完成之前刷新界面不会显示出来，这里手动配置一下
        setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
	}

    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    public static interface CanChildScrollUpCallback {
        public boolean canSwipeRefreshChildScrollUp();
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canSwipeRefreshChildScrollUp();
        }
        return super.canChildScrollUp();
    }

}
