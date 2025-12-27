package com.example.summonsimulator;

import android.content.Context;

public class GachaSettings {

    // 改為 public 或提供 Getter，方便 Kotlin 存取
    public double rate1Star;
    public double rate2Star;
    public double rate3Star;
    public double rateFocus;
    public int pityCount;
    public int costSingle;
    public int costTen;
    public int stoneCount;

    public GachaSettings(double rate1Star, double rate2Star, double rate3Star, double rateFocus,
                         int pityCount, int costSingle, int costTen, int stoneCount) {
        this.rate1Star = rate1Star;
        this.rate2Star = rate2Star;
        this.rate3Star = rate3Star;
        this.rateFocus = rateFocus;
        this.pityCount = pityCount;
        this.costSingle = costSingle;
        this.costTen = costTen;
        this.stoneCount = stoneCount;
    }

    // --- Getter 方法 ---
    public double getRate1Star() { return rate1Star; }
    public double getRate2Star() { return rate2Star; }
    public double getRate3Star() { return rate3Star; }
    public double getRateFocus() { return rateFocus; }
    public int getPityCount() { return pityCount; }
    public int getCostSingle() { return costSingle; }
    public int getCostTen() { return costTen; }
    public int getStoneCount() { return stoneCount; }

    // --- 靜態讀取方法 ---
    public static GachaSettings load(Context context) {
        SSDBHelper dbHelper = new SSDBHelper(context);
        return dbHelper.getGachaSettings();
    }

    // --- 儲存方法 ---
    public boolean save(Context context) {
        SSDBHelper dbHelper = new SSDBHelper(context);
        return dbHelper.updateGachaSettings(this);
    }
}