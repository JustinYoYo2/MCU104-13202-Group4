package com.example.summonsimulator;

import android.content.Context;

public class GachaSettings {

    private double rate1Star;
    private double rate2Star;
    private double rate3Star;
    private double rateFocus;
    private int pityCount;
    private int costSingle;
    private int costTen;
    private int stoneCount;

    public GachaSettings(double rate1Star, double rate2Star, double rate3Star, double rateFocus, int pityCount, int costSingle, int costTen, int stoneCount) {
        this.rate1Star = rate1Star;
        this.rate2Star = rate2Star;
        this.rate3Star = rate3Star;
        this.rateFocus = rateFocus;
        this.pityCount = pityCount;
        this.costSingle = costSingle;
        this.costTen = costTen;
        this.stoneCount = stoneCount;
    }

    public double getRate1Star() { return rate1Star; }  //抓取一星機率值
    public double getRate2Star() { return rate2Star; }  //抓取二星機率值
    public double getRate3Star() { return rate3Star; }  //抓取三星機率值
    public double getRateFocus() { return rateFocus; }  //抓取當池機率
    public int getPityCount() { return pityCount; }  //抓取保底次數
    public int getCostSingle() { return costSingle; }  //抓取單抽花費
    public int getCostTen() { return costTen; }  //抓取十抽花費
    public int getStoneCount() {return stoneCount;}  //抓取目前石頭數量

    public static GachaSettings load(Context context) {
        SSDBHelper dbHelper = new SSDBHelper(context);

        return dbHelper.getGachaSettings();
    }

    public boolean save(Context context) {
        SSDBHelper dbHelper = new SSDBHelper(context);

        return dbHelper.updateGachaSettings(this);
    }
}
/**
 * 範例：GachaActivity 扣除石頭的步驟
     * 初始化： 在 onCreate 裡初始化 dbHelper = new SSDBHelper(this);
     * 讀取（檢查與顯示）： 呼叫 int current = dbHelper.getStoneCount();
     * 寫入（扣除）： 呼叫 dbHelper.updateStoneCount(-160); (假設單抽消耗 160)
 *
 * 範例：TopUpFragment 增加石頭的步驟
     * 初始化： 在 onViewCreated 裡初始化 dbHelper = SSDBHelper(context);
     * 讀取（顯示）： 呼叫 val current = dbHelper.getStoneCount()
     * 寫入（增加）： 呼叫 dbHelper.updateStoneCount(500) (假設購買 500 顆)
 * 石頭數量抓取
    * 初始化：GachaSettings settings = GachaSettings.load(this);
    * 抓取值：int currentStones = settings.getStoneCount();
 * **/
