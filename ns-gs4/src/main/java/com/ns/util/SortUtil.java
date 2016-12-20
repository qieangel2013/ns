package com.ns.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jd.big.data.common.dashboard.model.DeptMonitorGrossprofitVo;
import com.jd.big.data.common.dashboard.model.DeptMonitorVo;
import com.jd.big.data.common.dashboard.util.ConstUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PV现货率/出库率/区域ABCBand的排序
 * Created by danie on 2016/10/20.
 */
public class SortUtil {

    /**
     * 按照部门父子顺序排序:1.父子部门;2.同级别内按照value值倒序
     *
     * @param list 所有的节点
     * @return
     */
    public static List<? extends DeptMonitorVo> sortAllDeptLevelList(List<? extends DeptMonitorVo> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        /**
         * 1.分成4组
         */
        List<DeptMonitorVo> listJd = new ArrayList<>();
        List<DeptMonitorVo> listBu = new ArrayList<>();
        List<DeptMonitorVo> listDept1 = new ArrayList<>();
        List<DeptMonitorVo> listDept2 = new ArrayList<>();
        List<DeptMonitorVo> listDept3 = new ArrayList<>();
        for (DeptMonitorVo vo : list) {
            if (vo != null && vo.getDept_name() != null && ConstUtil.JD_ALL_DEPT_NAME.equals(vo.getDept_name())) {
                listJd.add(vo);
            }else if (vo.getBu_id() != null && vo.getDept_id_1() == null) {
                listBu.add(vo);
            } else if (vo.getBu_id() != null && vo.getDept_id_1() != null && vo.getDept_id_2() == null) {
                listDept1.add(vo);
            } else if (vo.getBu_id() != null && vo.getDept_id_1() != null && vo.getDept_id_2() != null && vo.getDept_id_3() == null) {
                listDept2.add(vo);
            } else if (vo.getBu_id() != null && vo.getDept_id_1() != null && vo.getDept_id_2() != null && vo.getDept_id_3() != null) {
                listDept3.add(vo);
            }
        }

        /**
         * 2.每组排序
         */
        if (!listBu.isEmpty()) {
            Collections.sort(listBu);
        }
        if (!listDept1.isEmpty()) {
            Collections.sort(listDept1);
        }
        if (!listDept2.isEmpty()) {
            Collections.sort(listDept2);
        }
        if (!listDept3.isEmpty()) {
            Collections.sort(listDept3);
        }

        /**
         * q1: VP:[buid != null || ]
         * 3.认亲-认上层节点-追加顺序
         */
        List<DeptMonitorVo> result2 = sortParentAndSon3(listDept3, listDept2);
        List<DeptMonitorVo> result1 = sortParentAndSon2(result2, listDept1);
        List<DeptMonitorVo> result0 = sortParentAndSon1(result1, listBu);
        List<DeptMonitorVo> resultAll = sortParentAndSon0(result0, listJd);

        return resultAll;
    }

    /**
     * 两层排序,然后返回父亲列表
     *
     * @param d3List
     * @param d2List
     */
    private static List<DeptMonitorVo> sortParentAndSon3(List<DeptMonitorVo> d3List, List<DeptMonitorVo> d2List) {
        List<DeptMonitorVo> result = new ArrayList<>();
        /**
         * 1.三级列表为空,不用填充到对应的二级列表后面了, 直接返回二级列表
         */
        if (d3List.isEmpty() && !d2List.isEmpty()) {
            return d2List;
        } else if (!d3List.isEmpty() && d2List.isEmpty()) {
            return d3List;
        } else if (d3List.isEmpty() && d2List.isEmpty()) {
            return result;
        }

        for (DeptMonitorVo d2Vo : d2List) {
            // 1. 先加入父亲
            result.add(d2Vo);
            long dept2 = d2Vo.getDept_id_2().longValue();

            // 2. 再便利子节点中的符合项, 加入
            for (DeptMonitorVo d3Vo : d3List) {
                long sonDept2 = d3Vo.getDept_id_2().longValue();
                if (dept2 == sonDept2) {
                    result.add(d3Vo);
                }
            }
        }

        return result;
    }

    /**
     * 两层排序,然后返回父亲列表
     *
     * @param d2List
     * @param d1List
     */
    private static List<DeptMonitorVo> sortParentAndSon2(List<DeptMonitorVo> d2List, List<DeptMonitorVo> d1List) {
        List<DeptMonitorVo> result = new ArrayList<>();
        /**
         * 1.二级列表为空,不用填充到对应的一级列表后面了, 直接返回二级列表
         */
        if (d2List.isEmpty() && !d1List.isEmpty()) {
            return d1List;
        } else if (!d2List.isEmpty() && d1List.isEmpty()) {
            return d2List;
        } else if (d2List.isEmpty() && d1List.isEmpty()) {
            return result;
        }

        for (DeptMonitorVo parentVo : d1List) {
            result.add(parentVo);   // 1. 先加入父亲
            long dept1 = parentVo.getDept_id_1().longValue();

            for (DeptMonitorVo sonVo : d2List) { // 2. 再便利子节点中的符合项, 加入
                long sonDept1 = sonVo.getDept_id_1().longValue();
                if (dept1 == sonDept1) {
                    result.add(sonVo);
                }
            }
        }

        return result;
    }

    /**
     * 两层排序,然后返回父亲列表
     *
     * @param d1List
     * @param d0List
     */
    private static List<DeptMonitorVo> sortParentAndSon1(List<DeptMonitorVo> d1List, List<DeptMonitorVo> d0List) {
        List<DeptMonitorVo> result = new ArrayList<>();
        /**
         * 1.一级列表为空,不用填充到对应的0级列表(事业部)后面了, 直接返回一级列表
         */
        if (d1List.isEmpty() && !d0List.isEmpty()) {
            return d0List;
        } else if (!d1List.isEmpty() && d0List.isEmpty()) {
            return d1List;
        } else if (d1List.isEmpty() && d0List.isEmpty()) {
            return result;
        }

        for (DeptMonitorVo parentVo : d0List) {
            // 1. 先加入父亲
            result.add(parentVo);
            long dept0 = parentVo.getBu_id().longValue();

            // 2. 再便利子节点中的符合项, 加入
            for (DeptMonitorVo sonVo : d1List) {
                long sonDept0 = sonVo.getBu_id().longValue();
                if (dept0 == sonDept0) {
                    result.add(sonVo);
                }
            }
        }

        return result;
    }

    /**
     * 两层排序,然后返回父亲列表
     *
     * @param listBu
     * @param listJd
     */
    private static List<DeptMonitorVo> sortParentAndSon0(List<DeptMonitorVo> listBu, List<DeptMonitorVo> listJd) {
        List<DeptMonitorVo> result = new ArrayList<DeptMonitorVo>();
        /**
         * 1.事业部列表为空,不用填充到“商城整体”后面了, 直接返回事业部列表
         */
        if (listBu.isEmpty() && !listJd.isEmpty()) {
            return listJd;
        } else if (!listBu.isEmpty() && listJd.isEmpty()) {
            return listBu;
        } else if (listBu.isEmpty() && listJd.isEmpty()) {
            return result;
        }

        for (DeptMonitorVo deptJd : listJd) {
            // 1. 先加入父亲-商城整体
            result.add(deptJd);
            // 2. 再便利子节点中的符合项, 加入
            for (DeptMonitorVo deptBu : listBu) {
                result.add(deptBu);
            }
        }

        return result;
    }


    public static void main(String[] args) {
//        String dept1String = "[{\"dept_name\":\"3C周边采销部\",\"country_all_day\":1.0,\"bj\":1.0,\"sh\":1.0,\"gz\":1.0,\"cd\":1.0,\"wh\":1.0,\"sy\":1.0,\"xa\":1.0,\"ga\":1.0,\"hz\":1.0,\"bu_id\":1420,\"dept_id_1\":1324,\"dept_id_2\":1334},{\"dept_name\":\"智能数码采销部\",\"country_all_day\":0.96163,\"bj\":0.9632,\"sh\":0.9677,\"gz\":0.94996,\"cd\":0.96186,\"wh\":0.96438,\"sy\":0.963,\"xa\":0.96552,\"ga\":0.96366,\"hz\":0.97454,\"bu_id\":1420,\"dept_id_1\":1324,\"dept_id_2\":1333},{\"dept_name\":\"数码影音采销部\",\"country_all_day\":0.93846,\"bj\":0.94308,\"sh\":0.94065,\"gz\":0.93518,\"cd\":0.94158,\"wh\":0.92635,\"sy\":0.9362,\"xa\":0.94435,\"ga\":0.93955,\"hz\":0.95217,\"bu_id\":1420,\"dept_id_1\":1324,\"dept_id_2\":1332},{\"dept_name\":\"数码业务部\",\"country_all_day\":0.93419,\"bj\":0.94633,\"sh\":0.94231,\"gz\":0.93303,\"cd\":0.92651,\"wh\":0.91655,\"sy\":0.91778,\"xa\":0.92837,\"ga\":0.93629,\"hz\":0.95856,\"bu_id\":1420,\"dept_id_1\":1324},{\"dept_name\":\"数码POP及配件采销部\",\"country_all_day\":0.93051,\"bj\":0.9493,\"sh\":0.93414,\"gz\":0.92707,\"cd\":0.92507,\"wh\":0.91526,\"sy\":0.91529,\"xa\":0.93691,\"ga\":0.92456,\"hz\":0.95797,\"bu_id\":1420,\"dept_id_1\":1324,\"dept_id_2\":1335},{\"dept_name\":\"平板采销部\",\"country_all_day\":0.90506,\"bj\":0.91987,\"sh\":0.92736,\"gz\":0.92255,\"cd\":0.87691,\"wh\":0.86422,\"sy\":0.85504,\"xa\":0.85542,\"ga\":0.91938,\"hz\":0.94745,\"bu_id\":1420,\"dept_id_1\":1324,\"dept_id_2\":1776},{\"dept_name\":\"电子书\",\"country_all_day\":1.0,\"bj\":1.0,\"sh\":1.0,\"gz\":1.0,\"cd\":1.0,\"wh\":1.0,\"sy\":1.0,\"xa\":1.0,\"bu_id\":1420,\"dept_id_1\":31,\"dept_id_2\":43},{\"dept_name\":\"自营图书采销部\",\"country_all_day\":0.96973,\"bj\":0.9522,\"sh\":0.97175,\"gz\":0.97694,\"cd\":0.977,\"wh\":0.97683,\"sy\":0.97297,\"xa\":0.97296,\"bu_id\":1420,\"dept_id_1\":31,\"dept_id_2\":41},{\"dept_name\":\"图书音像业务部\",\"country_all_day\":0.968,\"bj\":0.95127,\"sh\":0.97,\"gz\":0.97537,\"cd\":0.97542,\"wh\":0.97583,\"sy\":0.96917,\"xa\":0.96876,\"bu_id\":1420,\"dept_id_1\":31},{\"dept_name\":\"音像采销部\",\"country_all_day\":0.89063,\"bj\":0.90068,\"sh\":0.8967,\"gz\":0.91775,\"cd\":0.91461,\"wh\":0.92825,\"sy\":0.77064,\"xa\":0.70671,\"bu_id\":1420,\"dept_id_1\":31,\"dept_id_2\":42},{\"dept_name\":\"办公采销部\",\"country_all_day\":0.95925,\"bj\":0.97993,\"sh\":0.95771,\"gz\":0.95925,\"cd\":0.95966,\"wh\":0.95505,\"sy\":0.95023,\"xa\":0.95304,\"ga\":0.94573,\"hz\":0.96364,\"bu_id\":1420,\"dept_id_1\":30,\"dept_id_2\":39},{\"dept_name\":\"外设采销部\",\"country_all_day\":0.94805,\"bj\":0.95503,\"sh\":0.94445,\"gz\":0.94828,\"cd\":0.94679,\"wh\":0.93955,\"sy\":0.94585,\"xa\":0.94658,\"ga\":0.95417,\"hz\":0.95216,\"bu_id\":1420,\"dept_id_1\":30,\"dept_id_2\":40},{\"dept_name\":\"电脑配件采销部\",\"country_all_day\":0.93659,\"bj\":0.9486,\"sh\":0.93087,\"gz\":0.92917,\"cd\":0.94604,\"wh\":0.94384,\"sy\":0.93015,\"xa\":0.93837,\"ga\":0.93589,\"hz\":0.94406,\"bu_id\":1420,\"dept_id_1\":30,\"dept_id_2\":336},{\"dept_name\":\"电脑办公业务部\",\"country_all_day\":0.91942,\"bj\":0.94834,\"sh\":0.91891,\"gz\":0.90477,\"cd\":0.91616,\"wh\":0.89693,\"sy\":0.92615,\"xa\":0.92688,\"ga\":0.93079,\"hz\":0.92965,\"bu_id\":1420,\"dept_id_1\":30},{\"dept_name\":\"整机采销部\",\"country_all_day\":0.85047,\"bj\":0.91414,\"sh\":0.85749,\"gz\":0.81637,\"cd\":0.83107,\"wh\":0.79688,\"sy\":0.87217,\"xa\":0.87578,\"ga\":0.88881,\"hz\":0.87836,\"bu_id\":1420,\"dept_id_1\":30,\"dept_id_2\":37},{\"dept_name\":\"直供业务部\",\"country_all_day\":0.84758,\"bj\":0.83333,\"sh\":0.84127,\"gz\":0.80693,\"cd\":0.94118,\"wh\":0.87619,\"sy\":0.89474,\"xa\":0.72727,\"ga\":0.89209,\"hz\":0.66667,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":1553},{\"dept_name\":\"通讯业务二部\",\"country_all_day\":0.76592,\"bj\":0.77973,\"sh\":0.74857,\"gz\":0.75667,\"cd\":0.77958,\"wh\":0.76166,\"sy\":0.7784,\"xa\":0.77247,\"ga\":0.77877,\"hz\":0.76151,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":738},{\"dept_name\":\"通讯业务部\",\"country_all_day\":0.65548,\"bj\":0.64468,\"sh\":0.62067,\"gz\":0.67024,\"cd\":0.67957,\"wh\":0.6631,\"sy\":0.65866,\"xa\":0.66686,\"ga\":0.65447,\"hz\":0.62753,\"bu_id\":1420,\"dept_id_1\":1},{\"dept_name\":\"运营商业务部\",\"country_all_day\":0.55414,\"bj\":0.52975,\"sh\":0.55859,\"gz\":0.53727,\"cd\":0.69676,\"wh\":0.50726,\"sy\":0.51485,\"xa\":0.56237,\"ga\":0.51848,\"hz\":0.55759,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":1524},{\"dept_name\":\"通讯业务一部\",\"country_all_day\":0.54539,\"bj\":0.52234,\"sh\":0.50463,\"gz\":0.57716,\"cd\":0.57954,\"wh\":0.56136,\"sy\":0.54289,\"xa\":0.55968,\"ga\":0.52331,\"hz\":0.51473,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":727},{\"dept_name\":\"3C事业部\",\"country_all_day\":0.82657,\"bj\":0.84881,\"sh\":0.82391,\"gz\":0.81995,\"cd\":0.83711,\"wh\":0.81759,\"sy\":0.83341,\"xa\":0.83206,\"ga\":0.81426,\"hz\":0.82026,\"bu_id\":1420}]";
//        List<DeptMonitorStoreVo> result = new Gson().fromJson(dept1String, new TypeToken<List<DeptMonitorStoreVo>>() {
//        }.getType());

//        String dept1String = "[{\"dept_name\":\"通讯业务部\",\"pv_day\":3086,\"pv_lastday_avg\":2730,\"float_ratio\":0.13036,\"pv_month\":55959,\"pv_month_yoy\":4512,\"pv_month_yoy_ratio\":11.40154,\"bu_id\":1420,\"dept_id_1\":1},{\"dept_name\":\"通讯业务一部\",\"pv_day\":1772,\"pv_lastday_avg\":1383,\"float_ratio\":0.28148,\"pv_month\":32167,\"pv_month_yoy\":2553,\"pv_month_yoy_ratio\":11.59584,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":727},{\"dept_name\":\"通讯业务二部\",\"pv_day\":2069,\"pv_lastday_avg\":1126,\"float_ratio\":-0.05053,\"pv_month\":19380,\"pv_month_yoy\":1462,\"pv_month_yoy_ratio\":12.24747,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":738},{\"dept_name\":\"运营商业务部\",\"pv_day\":244,\"pv_lastday_avg\":220,\"float_ratio\":0.1083,\"pv_month\":4406,\"pv_month_yoy\":495,\"pv_month_yoy_ratio\":7.89266,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":1524},{\"dept_name\":\"直供业务部\",\"pv_day\":0,\"pv_lastday_avg\":0,\"float_ratio\":-0.68632,\"pv_month\":4,\"pv_month_yoy\":0,\"bu_id\":1420,\"dept_id_1\":1,\"dept_id_2\":1553}]";
        String dept1String = "[{\"compre_income_month\":236470.384164,\"compre_budget_month\":4500000.0,\"finish_ratio\":0.0543006067,\"time_finish_ratio\":0.967741935,\"time_finish_diff_ratio\":-0.9134413283,\"compre_income_month_owe\":-4.118368325513419E10,\"gross_profit_ratio\":0.060167,\"gross_profit_budget_rate\":1.125,\"dept_name\":\"商城整体\"},{\"compre_income_month\":56779.600141,\"compre_budget_month\":95100.0,\"finish_ratio\":0.6169532437,\"time_finish_ratio\":0.9677,\"time_finish_diff_ratio\":-0.3507886913,\"compre_income_month_owe\":-3.5252657923516E8,\"gross_profit_ratio\":0.041262,\"gross_profit_budget_rate\":0.06448},{\"compre_income_month\":37677.757896,\"time_finish_ratio\":0.9677,\"gross_profit_ratio\":0.064933},{\"compre_income_month\":26220.625428,\"time_finish_ratio\":0.9677,\"gross_profit_ratio\":0.045501},{\"compre_income_month\":122462.416225,\"time_finish_ratio\":0.9677,\"gross_profit_ratio\":0.103218},{\"compre_income_month\":1049.335005,\"time_finish_ratio\":0.9677,\"gross_profit_ratio\":0.096283}]";
        List<DeptMonitorGrossprofitVo> result = new Gson().fromJson(dept1String, new TypeToken<List<DeptMonitorGrossprofitVo>>() {
        }.getType());
        List<? extends DeptMonitorVo> result0 = SortUtil.sortAllDeptLevelList(result);
        System.out.println(new Gson().toJson(result0));
    }
}
