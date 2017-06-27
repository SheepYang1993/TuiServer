package me.sheepyang.tuiserver.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sheepyang.tuiserver.R;

/**
 * Created by Administrator on 2017/4/21.
 */

public class QBar extends RelativeLayout {
    private boolean mIsBack;
    private int mRightBackgroundId;
    private float mRightTextSize;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    private int mRightDrawableId;
    @BindView(R.id.ll_right)
    LinearLayout mLlRight;
    private String mRightText;
    private String mTitle;
    private Context mContext;
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_right)
    TextView mTvRight;
    private View mView;
    private OnClickListener mOnRightClickListener;

    public QBar(Context context) {
        this(context, null);
    }

    public QBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (isInEditMode()) {
            Utils.init(mContext);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QBar, defStyleAttr, 0);
        try {
            mTitle = a.getString(R.styleable.QBar_qb_title);
            mRightText = a.getString(R.styleable.QBar_qb_right_text);
            mIsBack = a.getBoolean(R.styleable.QBar_qb_is_back, false);
            mRightDrawableId = a.getResourceId(R.styleable.QBar_qb_right_drawable, -1);
            mRightBackgroundId = a.getResourceId(R.styleable.QBar_qb_right_background, -1);
            mRightTextSize = SizeUtils.px2sp(a.getDimensionPixelSize(R.styleable.QBar_qb_right_text_size, SizeUtils.sp2px(17)));
        } finally {
            a.recycle();
        }
        // 加载布局
        mView = LayoutInflater.from(context).inflate(R.layout.layout_qbar, this);
        ButterKnife.bind(mView);
        init();
    }

    private void init() {
        if (!TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(mTitle);
        }
        if (!TextUtils.isEmpty(mRightText)) {
            mTvRight.setText(mRightText);
        }
        mTvRight.setTextSize(mRightTextSize);
        if (mRightBackgroundId != -1) {
            mLlRight.setBackgroundResource(mRightBackgroundId);
        }
        if (!TextUtils.isEmpty(mRightText) && mRightDrawableId != -1) {
            mLlRight.setVisibility(VISIBLE);
            mTvRight.setVisibility(VISIBLE);
            mIvRight.setVisibility(VISIBLE);
            mTvRight.setTextColor(Color.BLACK);
            mIvRight.setImageResource(mRightDrawableId);
        } else if (!TextUtils.isEmpty(mRightText)) {
            mLlRight.setVisibility(VISIBLE);
            mTvRight.setVisibility(VISIBLE);
            mIvRight.setVisibility(GONE);
            mTvRight.setTextColor(Color.WHITE);
            mLlRight.setBackground(null);
        } else if (mRightDrawableId != -1) {
            mLlRight.setVisibility(VISIBLE);
            mTvRight.setVisibility(GONE);
            mIvRight.setVisibility(VISIBLE);
            mIvRight.setImageResource(mRightDrawableId);
            mLlRight.setBackground(null);
        } else {
            mLlRight.setVisibility(GONE);
        }

        mIvLeft.setVisibility(mIsBack ? View.VISIBLE : View.GONE);
        mIvLeft.setOnClickListener((View v) -> {
            if (mContext instanceof Activity) {
                ((Activity) mContext).onBackPressed();

            }
        });
        mLlRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRightClickListener != null) {
                    mOnRightClickListener.onClick(v);
                }
            }
        });
    }

    public void setRightText(String text) {
        mRightText = text;
        if (mTvRight != null && !TextUtils.isEmpty(mRightText)) {
            mTvRight.setText(mRightText);
            mLlRight.setVisibility(VISIBLE);
//            mTvRight.setVisibility(VISIBLE);
        }
    }

    public void setOnRightClickListener(OnClickListener onRightClickListener) {
        mOnRightClickListener = onRightClickListener;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
        if (mTvTitle != null && !TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(mTitle);
        }
    }

    public void setBack(boolean isBack) {
        mIsBack = isBack;
        mIvLeft.setVisibility(mIsBack ? View.VISIBLE : View.GONE);
    }
}
