package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/7/17.
 */

public class OrderDesc extends BaseBean {
    private int estimateTimeType;
    private String descriptions;

    public int getEstimateTimeType() {
        return estimateTimeType;
    }

    public void setEstimateTimeType(int estimateTimeType) {
        this.estimateTimeType = estimateTimeType;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
}
