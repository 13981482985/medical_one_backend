package com.cqupt.software_1.common;

// TODO 公共模块新增
public enum FeatureType {
    DIAGNOSIS(0, "is_demography"),
//    EXAMINE(1, "examine"),
    PATHOLOGY(2, "is_sociology"),
    VITAL_SIGNS(3, "is_physiological");

    private final int code;
    private final String name;
    FeatureType(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public int getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
