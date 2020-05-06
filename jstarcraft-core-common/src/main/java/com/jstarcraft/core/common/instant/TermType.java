package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * 节气类型
 * 
 * <pre>
 * 计算公式:[Y*D+C]-L
 * 公式解读:
 * Y值 = 年数%100
 * D值 = 0.2422
 * C值 = 世纪-值
 * L值 = 闰年数
 * </pre>
 * 
 * @author Birdy
 *
 */
public enum TermType {

    XiaoHan(6.11F, 5.4055F), // 小寒
    DaHan(20.84F, 20.12F), // 大寒
    LiChun(4.6295F, 3.87F), // 立春
    YuShui(19.4599F, 18.73F), // 雨水
    JingZhe(6.3826F, 5.63F), // 惊蛰
    ChunFen(21.4155F, 20.646F), // 春分
    QingMing(5.59F, 4.81F), // 清明
    GuYu(20.888F, 20.1F), // 谷雨
    LiXia(6.318F, 5.52F), // 立夏
    XiaoMan(21.86F, 21.04F), // 小满
    MangZhong(6.5F, 5.678F), // 芒种
    XiaZhi(22.2F, 21.37F), // 夏至
    XiaoShu(7.928F, 7.108F), // 小暑
    DaShu(23.65F, 22.83F), // 大暑
    LiQiu(8.35F, 7.5F), // 立秋
    ChuShu(23.95F, 23.13F), // 处暑
    BaiLu(8.44F, 7.646F), // 白露
    QiuFen(23.822F, 23.042F), // 秋分
    HanLu(9.098F, 8.318F), // 寒露
    ShuangJiang(24.218F, 23.438F), // 霜降
    LiDong(8.218F, 7.438F), // 立冬
    XiaoXue(23.08F, 22.36F), // 小雪
    DaXue(7.9F, 7.18F), // 大雪
    DongZhi(22.6F, 21.94F); // 冬至

    /**
     * 支持的最小年份
     */
    public final static int MINIMUM_YEAR = 1900;

    /**
     * 支持的最大年份
     */
    public final static int MAXIMUM_YEAR = 2099;

    private final static double d = 0.2422D;

    private final static Int2IntMap table = new Int2IntOpenHashMap();

    static {
        table.put(1902 * 100 + TermType.MangZhong.ordinal(), 1);
        table.put(1911 * 100 + TermType.LiXia.ordinal(), 1);
        table.put(1914 * 100 + TermType.LiChun.ordinal(), -1);
        table.put(1914 * 100 + TermType.DongZhi.ordinal(), 1);
        table.put(1915 * 100 + TermType.JingZhe.ordinal(), -1);
        table.put(1922 * 100 + TermType.DaShu.ordinal(), 1);
        table.put(1925 * 100 + TermType.XiaoShu.ordinal(), 1);
        table.put(1927 * 100 + TermType.BaiLu.ordinal(), 1);
        table.put(1928 * 100 + TermType.XiaZhi.ordinal(), 1);
        table.put(1942 * 100 + TermType.QiuFen.ordinal(), 1);
        table.put(1947 * 100 + TermType.LiChun.ordinal(), -1);
        table.put(1947 * 100 + TermType.DongZhi.ordinal(), 1);
        table.put(1948 * 100 + TermType.JingZhe.ordinal(), -1);
        table.put(1951 * 100 + TermType.DongZhi.ordinal(), 1);
        table.put(1978 * 100 + TermType.XiaoXue.ordinal(), 1);
        table.put(1979 * 100 + TermType.DaHan.ordinal(), 1);
        table.put(1980 * 100 + TermType.DongZhi.ordinal(), 1);
        table.put(1982 * 100 + TermType.XiaoHan.ordinal(), 1);
        table.put(1984 * 100 + TermType.DongZhi.ordinal(), 1);
        table.put(2000 * 100 + TermType.XiaoHan.ordinal(), 1);
        table.put(2000 * 100 + TermType.DaHan.ordinal(), 1);
        table.put(2000 * 100 + TermType.LiChun.ordinal(), 1);
        table.put(2000 * 100 + TermType.YuShui.ordinal(), 1);
        table.put(2002 * 100 + TermType.LiQiu.ordinal(), 1);
        table.put(2008 * 100 + TermType.XiaoMan.ordinal(), 1);
        table.put(2016 * 100 + TermType.XiaoShu.ordinal(), 1);
        table.put(2019 * 100 + TermType.XiaoHan.ordinal(), -1);
        table.put(2021 * 100 + TermType.DongZhi.ordinal(), -1);
        table.put(2026 * 100 + TermType.YuShui.ordinal(), -1);
        table.put(2082 * 100 + TermType.DaHan.ordinal(), 1);
        table.put(2084 * 100 + TermType.ChunFen.ordinal(), 1);
        table.put(2089 * 100 + TermType.ShuangJiang.ordinal(), 1);
        table.put(2089 * 100 + TermType.LiDong.ordinal(), 1);
    }

    /** 世纪值 */
    private float[] centuries;

    TermType(float... centuries) {
        this.centuries = centuries;
    }

    /**
     * 获取指定年份的世纪值
     * 
     * @param year
     * @return
     */
    public float getCentury(int year) {
        return centuries[year / 100 - 19];
    }

    /**
     * 获取指定年份的节气日期
     * 
     * @param year
     * @return
     */
    public LocalDate getDate(int year) {
        // 步骤1:年数%100
        int y = year % 100;
        int month = ordinal() / 2 + 1;
        int l;
        // 步骤2:凡闰年3月1日前闰年数要减一,即:L=[(Y-1)/4],因为小寒,大寒,立春,雨水4个节气都小于3月1日
        if (((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) && month < 3) {// 闰年
            l = (y - 1) / 4;
        } else {
            l = y / 4;
        }
        double c = getCentury(year);
        // 步骤3:使用公式[Y*D+C]-L计算
        int day = (int) (y * d + c) - l;
        // 步骤4:加上特殊的年分的节气偏移量
        int shift = table.get(year * 100 + ordinal());
        day += shift;
        return LocalDate.of(year, month, day);
    }

}
