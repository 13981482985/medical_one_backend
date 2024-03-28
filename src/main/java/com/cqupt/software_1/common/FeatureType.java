package com.cqupt.software_1.common;

// TODO 公共模块新增
public enum FeatureType {
    DIAGNOSIS(0, "diagnosis"),
//    EXAMINE(1, "examine"),
    PATHOLOGY(2, "pathology"),
    VITAL_SIGNS(3, "vital_signs");

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
