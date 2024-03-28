package com.cqupt.software_1.common;


import java.util.HashMap;

public class MissDataCompleteMethods {

    public HashMap<String,String> methodMap;


    public MissDataCompleteMethods() {
        methodMap = new HashMap<>();
        methodMap.put("众数填充","modePadding");
        methodMap.put("最邻近插值","nearestNeighborInterpolation");
        methodMap.put("前向填充","forwardFilling");
        methodMap.put("均数替换","meanReplacement");
        methodMap.put("回归分析替换","regressionAnalysisReplacement");
        methodMap.put("热卡填充","eucarFilling");
        methodMap.put("中位数替换","medianReplacement");
    }
}
