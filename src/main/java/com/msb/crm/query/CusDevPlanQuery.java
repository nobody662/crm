package com.msb.crm.query;


import com.msb.crm.base.BaseQuery;

public class CusDevPlanQuery extends BaseQuery {
    // 营销机会id
    private Integer saleChanceId;

    public Integer getSaleChanceId() {
        return saleChanceId;
    }

    public void setSaleChanceId(Integer saleChanceId) {
        this.saleChanceId = saleChanceId;
    }
}
