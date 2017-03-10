package com.austin.timepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.austin.timepicker.wheel.OnWheelChangedListener;
import com.austin.timepicker.wheel.WheelView;
import com.austin.timepicker.wheel.adapters.ArrayWheelAdapter;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TimePicker implements CanShow, OnWheelChangedListener {


    private Context context;


    private AlertDialog timePickerDialog;

    /**
     * 显示几年的数据
     */
    private int mYearCount;

    private int mStartYear;

    private View timePickerView;

    private WheelView mViewYear;

    private WheelView mViewMonth;

    private WheelView mViewDay;

    private RelativeLayout mRelativeTitleLayout;

    private TextView mTvOK;

    private TextView mTvTitle;

    private TextView mTvCancel;

    /**
     */
    protected String[] mYearDatas;

    /**
     */
//    protected Map<String, String[]> mMonthDatasMap = new HashMap<String, String[]>();
    protected String[] mMonthDatas;

    /**
     * key - 年 values - 日
     */
    protected Map<String, String[]> mDayDatasMap = new HashMap<String, String[]>();


    /**
     */
    protected String mCurrentYearName;

    /**
     */
    protected String mCurrentMonthName;

    /**
     */
    protected String mCurrentDayName = "";


    private OnDayItemClickListener listener;
    private String[] m30Datas;
    private String[] m31Datas;
    private String[] m29Datas;
    private String[] m28Datas;
    String[] selectedDaysData;



    public interface OnDayItemClickListener {
        void onSelected(String... daySelected);
    }

    public void setOnDayItemClickListener(OnDayItemClickListener listener) {
        this.listener = listener;
    }




    /**
     * Default text color
     */
    public static final int DEFAULT_TEXT_COLOR = 0xFF585858;

    /**
     * Default text size
     */
    public static final int DEFAULT_TEXT_SIZE = 18;

    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;

    private int textSize = DEFAULT_TEXT_SIZE;

    /**
     * 滚轮显示的item个数
     */
    private static final int DEF_VISIBLE_ITEMS = 5;

    // Count of visible items
    private int visibleItems = DEF_VISIBLE_ITEMS;

    /**
     */
    private boolean isYearCyclic = true;

    /**
     */
    private boolean isMonthCyclic = true;

    /**
     */
    private boolean isDayCyclic = true;

    /**
     * item间距
     */
    private int itemPadding = 5;


    private String cancelTextColorStr = "#000000";

    private String confirmTextColorStr = "#0000FF";

    private String titleBackgroundColorStr = "#E9E9E9";

    /**
     */
    private String defaultYearName = "2017";

    /**
     */
    private String defaultMonthName = "01";

    /**
     */
    private String defaultDayName = "01";

    /**
     * 两级联动
     */
    private boolean onlyShowYearAndMonth = false;

    /**
     * 标题
     */
    private String mTitle = "选择日期";

    private TimePicker(Builder builder) {
        this.context = builder.mContext;

        this.mYearCount = builder.mYearCount;
        this.mStartYear = builder.mStartYear;

        this.mTitle = builder.mTitle;
        this.textColor = builder.mTextColor;
        this.textSize = builder.mTextSize;
        this.itemPadding = builder.itemPadding;

        this.visibleItems = builder.mVisibleItems;

        this.isYearCyclic = builder.isYearCyclic;
        this.isDayCyclic = builder.isDayCyclic;
        this.isMonthCyclic = builder.isMonthCyclic;


        this.titleBackgroundColorStr = builder.titleBackgroundColorStr;
        this.confirmTextColorStr = builder.confirmTextColorStr;
        this.cancelTextColorStr = builder.cancelTextColorStr;

        this.defaultDayName = builder.defaultDayName;
        this.defaultMonthName = builder.defaultMonthName;
        this.defaultYearName = builder.defaultYearName;

        this.onlyShowYearAndMonth = builder.showYearAndMonth;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        timePickerView = layoutInflater.inflate(R.layout.timepicker, null);

        mViewYear = (WheelView) timePickerView.findViewById(R.id.id_first_wheel);
        mViewMonth = (WheelView) timePickerView.findViewById(R.id.id_second_wheel);
        mViewDay = (WheelView) timePickerView.findViewById(R.id.id_third_wheel);

        mRelativeTitleLayout = (RelativeLayout) timePickerView.findViewById(R.id.rl_title);
        mTvOK = (TextView) timePickerView.findViewById(R.id.tv_confirm);
        mTvTitle = (TextView) timePickerView.findViewById(R.id.tv_title);
        mTvCancel = (TextView) timePickerView.findViewById(R.id.tv_cancel);


        timePickerDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(timePickerView)
                .create();

        Window window = timePickerDialog.getWindow();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.getDecorView().setLayoutParams(params);

        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.push_bottom_in);


        /**
         * 设置标题背景颜色
         */
        if (!TextUtils.isEmpty(this.titleBackgroundColorStr)) {
            GradientDrawable background = (GradientDrawable) mRelativeTitleLayout.getBackground();
            background.setColor(Color.parseColor(this.titleBackgroundColorStr));
        }

        /**
         * 设置标题
         */
        if (!TextUtils.isEmpty(this.mTitle)) {
            mTvTitle.setText(this.mTitle);
        }

        //设置确认按钮文字颜色
        if (!TextUtils.isEmpty(this.confirmTextColorStr)) {
            mTvOK.setTextColor(Color.parseColor(this.confirmTextColorStr));
        }

        //设置取消按钮文字颜色
        if (!TextUtils.isEmpty(this.cancelTextColorStr)) {
            mTvCancel.setTextColor(Color.parseColor(this.cancelTextColorStr));
        }

        //只显示省市两级联动
        if (this.onlyShowYearAndMonth) {
            mViewDay.setVisibility(View.GONE);
        } else {
            mViewDay.setVisibility(View.VISIBLE);
        }

        initDatas(context);

        // 添加change事件
        mViewYear.addChangingListener(this);
        // 添加change事件
        mViewMonth.addChangingListener(this);
        // 添加change事件
        mViewDay.addChangingListener(this);


        // 添加onclick事件
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        mTvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onlyShowYearAndMonth) {
                    listener.onSelected(mCurrentYearName, mCurrentMonthName);
                } else {
                    listener.onSelected(mCurrentYearName, mCurrentMonthName, mCurrentDayName);
                }
                hide();
            }
        });

    }




    @Override
    public void setType(int type) {
    }

    @Override
    public void show() {
        if (!isShow()) {
            setUpData();
//            popwindow.showAtLocation(timePickerView, Gravity.BOTTOM, 0, 0);
            timePickerDialog.show();
        }
    }

    @Override
    public void hide() {
        if (isShow()) {
            //popwindow.dismiss();
            timePickerDialog.dismiss();
        }
    }

    @Override
    public boolean isShow() {
//        return popwindow.isShowing();
        return timePickerDialog.isShowing();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewYear) {
            updateMonth();
        } else if (wheel == mViewMonth) {
            defaultMonthName = mCurrentMonthName;
            updateDay();
        } else if (wheel == mViewDay) {
            mCurrentDayName = selectedDaysData[newValue];
            defaultDayName = mCurrentDayName;
        }
    }

    protected void initDatas(Context context) {
        //年数据
        mYearDatas = new String[mYearCount];
        for (int i = 0; i < mYearCount; i++) {
            mYearDatas[i] = mStartYear + i+"";
        }


        //月数据(12个月，不用设置也可以)
        /*
        mMonthDatasMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < mYearCount; i++) {
            int actualMonthCount = calendar.getActualMaximum(Calendar.YEAR);
            String[] monthInYear = new String[actualMonthCount];
            for (int j = 0; j < monthInYear.length; j++) {
                monthInYear[j] = j+1+"";
            }
            mMonthDatasMap.put(mYearDatas[i], monthInYear);
        }
        */
        mMonthDatas = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};


        m31Datas = new String[31];
        for (int j = 0; j < m31Datas.length; j++) {
            DecimalFormat df = new DecimalFormat("00");
            m31Datas[j] = df.format(j+1);
        }

        m30Datas = Arrays.copyOf(m31Datas, 30);

        m29Datas = Arrays.copyOf(m31Datas, 29);

        m28Datas = Arrays.copyOf(m31Datas, 28);

    }



    private void setUpData() {
        int yearDefault = -1;
        if (!TextUtils.isEmpty(defaultYearName) && mYearDatas.length > 0) {
            for (int i = 0; i < mYearDatas.length; i++) {
                if (mYearDatas[i].contains(defaultYearName)) {
                    yearDefault = i;
                    break;
                }
            }
        }
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>(context, mYearDatas);
        mViewYear.setViewAdapter(arrayWheelAdapter);
        if (-1 != yearDefault) {
            mViewYear.setCurrentItem(yearDefault);
        }


        // 设置可见条目数量
        mViewYear.setVisibleItems(visibleItems);
        mViewMonth.setVisibleItems(visibleItems);
        mViewDay.setVisibleItems(visibleItems);

        mViewYear.setCyclic(isYearCyclic);
        mViewMonth.setCyclic(isMonthCyclic);
        mViewDay.setCyclic(isDayCyclic);

        arrayWheelAdapter.setPadding(itemPadding);
        arrayWheelAdapter.setTextColor(textColor);
        arrayWheelAdapter.setTextSize(textSize);

        updateMonth();
    }


    /**
     */
    private void updateMonth() {
        int pCurrent = mViewYear.getCurrentItem();
        mCurrentYearName = mYearDatas[pCurrent];

        if (mMonthDatas == null) {
            mMonthDatas = new String[]{""};
        }

        int monthDefault = -1;
        if (!TextUtils.isEmpty(defaultMonthName) && mMonthDatas.length > 0) {
            for (int i = 0; i < mMonthDatas.length; i++) {
                if (mMonthDatas[i].contains(defaultMonthName)) {
                    monthDefault = i;
                    break;
                }
            }
        }

        ArrayWheelAdapter cityWheel = new ArrayWheelAdapter<String>(context, mMonthDatas);
        // 设置可见条目数量
        cityWheel.setTextColor(textColor);
        cityWheel.setTextSize(textSize);
        mViewMonth.setViewAdapter(cityWheel);
        if (-1 != monthDefault) {
            mViewMonth.setCurrentItem(monthDefault);
        } else {
            mViewMonth.setCurrentItem(0);
        }

        cityWheel.setPadding(itemPadding);
        updateDay();
    }



    /**
     */
    private void updateDay() {

        int mItem = mViewMonth.getCurrentItem();
        mCurrentMonthName = mMonthDatas[mItem];

        defaultMonthName = mCurrentMonthName;
        switch (mItem+1) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                selectedDaysData = m31Datas;
                break;

            case 2:
                int yearInt = Integer.parseInt(mCurrentYearName);
                if (yearInt % 4 == 0 && yearInt % 100 != 0 || yearInt % 400 == 0) {
                    selectedDaysData = m29Datas;
                }else{
                    selectedDaysData = m28Datas;
                }
                break;
            default:
                selectedDaysData = m30Datas;
        }




        if (selectedDaysData == null) {
            selectedDaysData = new String[]{""};
        }

        int dayDefault = -1;
        if (!TextUtils.isEmpty(defaultDayName) && selectedDaysData.length > 0) {
            for (int i = 0; i < selectedDaysData.length; i++) {
                if (selectedDaysData[i].contains(defaultDayName)) {
                    dayDefault = i;
                    break;
                }
            }
        }

        ArrayWheelAdapter dayWheelAdapter = new ArrayWheelAdapter<String>(context, selectedDaysData);
        // 设置可见条目数量
        dayWheelAdapter.setTextColor(textColor);
        dayWheelAdapter.setTextSize(textSize);
        mViewDay.setViewAdapter(dayWheelAdapter);

        if (-1 != dayDefault) {
            mViewDay.setCurrentItem(dayDefault);
            //获取默认设置的区
            mCurrentDayName = defaultDayName;
        } else {
            mViewDay.setCurrentItem(0);
            //获取第一个区名称
            mCurrentDayName = selectedDaysData[0];
        }
        dayWheelAdapter.setPadding(itemPadding);
    }



    public static class Builder {
        /**
         * Default text color
         */
        public static final int DEFAULT_TEXT_COLOR = 0xFF585858;

        /**
         * Default text size
         */
        public static final int DEFAULT_TEXT_SIZE = 18;

        /**
         * 滚轮显示的item个数
         */
        private static final int DEF_VISIBLE_ITEMS = 5;

        /**
         * 默认显示几年的数据
         */
        private static final int DEF_YEAR_COUNT = 10;


        private Context mContext;

        /**
         * 显示几年的数据
         */
        private int mYearCount = DEF_YEAR_COUNT;

        /**
         * 从哪年开始
         */
        private int mStartYear = 2017;

        // Text settings
        private int mTextColor = DEFAULT_TEXT_COLOR;

        private int mTextSize = DEFAULT_TEXT_SIZE;

        // Count of visible items
        private int mVisibleItems = DEF_VISIBLE_ITEMS;


        /**
         * 省滚轮是否循环滚动
         */
        private boolean isYearCyclic = true;

        /**
         * 市滚轮是否循环滚动
         */
        private boolean isMonthCyclic = true;

        /**
         * 区滚轮是否循环滚动
         */
        private boolean isDayCyclic = true;


        /**
         * item间距
         */
        private int itemPadding = 5;


        /**
         * Color.BLACK
         */
        private String cancelTextColorStr = "#000000";


        /**
         * Color.BLUE
         */
        private String confirmTextColorStr = "#0000FF";

        /**
         * 标题背景颜色
         */
        private String titleBackgroundColorStr = "#E9E9E9";

        /**
         */
        private String defaultYearName = "";

        /**
         */
        private String defaultMonthName = "01";

        /**
         */
        private String defaultDayName = "01";

        /**
         * 标题
         */
        private String mTitle = "选择时间";

        /**
         * 两级联动
         */
        private boolean showYearAndMonth = false;


        public Builder(Context context) {
            this.mContext = context;
            defaultYearName = "" + Calendar.getInstance().get(Calendar.YEAR);
        }

        /**
         * 设置标题背景颜色
         *
         * @param colorBg
         * @return
         */
        public Builder titleBackgroundColor(String colorBg) {
            this.titleBackgroundColorStr = colorBg;
            return this;
        }

        /**
         * 设置标题
         *
         * @param mtitle
         * @return
         */
        public Builder title(String mtitle) {
            this.mTitle = mtitle;
            return this;
        }

        /**
         * 是否只显示省市两级联动
         *
         * @param flag
         * @return
         */
        public Builder onlyShowYearAndMonth(boolean flag) {
            this.showYearAndMonth = flag;
            return this;
        }

        /**
         * @param defaultYearName
         * @return
         */
        public Builder defaultYear(String defaultYearName) {
            this.defaultYearName = defaultYearName;
            return this;
        }

        /**
         * 第一次默认得显示城市，一般配合定位，使用
         *
         * @param defaultMonthName
         * @return
         */
        public Builder defaultMonth(String defaultMonthName) {
            this.defaultMonthName = defaultMonthName;
            return this;
        }

        /**
         * 第一次默认地区显示，一般配合定位，使用
         *
         * @param defaultDayName
         * @return
         */
        public Builder defaultDay(String defaultDayName) {
            this.defaultDayName = defaultDayName;
            return this;
        }


        /**
         * 确认按钮文字颜色
         *
         * @param color
         * @return
         */
        public Builder confirmTextColor(String color) {
            this.confirmTextColorStr = color;
            return this;
        }


        /**
         * 取消按钮文字颜色
         *
         * @param color
         * @return
         */
        public Builder cancelTextColor(String color) {
            this.cancelTextColorStr = color;
            return this;
        }

        /**
         * item文字颜色
         *
         * @param textColor
         * @return
         */
        public Builder textColor(int textColor) {
            this.mTextColor = textColor;
            return this;
        }

        /**
         * item文字大小
         *
         * @param textSize
         * @return
         */
        public Builder textSize(int textSize) {
            this.mTextSize = textSize;
            return this;
        }

        /**
         * 滚轮显示的item个数
         *
         * @param visibleItems
         * @return
         */
        public Builder visibleItemsCount(int visibleItems) {
            this.mVisibleItems = visibleItems;
            return this;
        }

        /**
         *
         * @param isYearCyclic
         * @return
         */
        public Builder yearCyclic(boolean isYearCyclic) {
            this.isYearCyclic = isYearCyclic;
            return this;
        }

        /**
         *
         * @param isMonthCyclic
         * @return
         */
        public Builder monthCyclic(boolean isMonthCyclic) {
            this.isMonthCyclic = isMonthCyclic;
            return this;
        }

        /**
         *
         * @param isDayCyclic
         * @return
         */
        public Builder dayCyclic(boolean isDayCyclic) {
            this.isDayCyclic = isDayCyclic;
            return this;
        }

        /**
         * item间距
         *
         * @param itemPadding
         * @return
         */
        public Builder itemPadding(int itemPadding) {
            this.itemPadding = itemPadding;
            return this;
        }

        public TimePicker build() {
            TimePicker cityPicker = new TimePicker(this);
            return cityPicker;
        }

        /**
         * 设置显示几年的数据
         * @param mYearCount
         */
        public Builder setmYearCount(int mYearCount) {
            this.mYearCount = mYearCount;
            return this;
        }

        public Builder setStartYear(int mStartYear) {
            this.mStartYear = mStartYear;
            return this;
        }
    }







}
