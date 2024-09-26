package com.msb.crm.query;


import com.msb.crm.base.BaseQuery;

public class SaleChanceQuery extends BaseQuery {
    /*营销机会管理 条件查询*/
    // 客户名
    private String customerName;
    // 创建人
    private String createMan;
    // 分配状态
    private Integer state;

    /*客户开发计划 条件查询*/
    //分配人
    private Integer assignMan;
    // 开发状态
    private Integer devResult;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getAssignMan() {
        return assignMan;
    }

    public void setAssignMan(Integer assignMan) {
        this.assignMan = assignMan;
    }

    public Integer getDevResult() {
        return devResult;
    }

    public void setDevResult(Integer devResult) {
        this.devResult = devResult;
    }
}
