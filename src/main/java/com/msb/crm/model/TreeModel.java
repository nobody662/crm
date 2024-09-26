package com.msb.crm.model;

public class TreeModel {
    //t_module表
    private Integer id;  //复选框的id
    private Integer pId; //父id
    private String name;

    private boolean checked = false; //判断复选框是否勾选 如果是ture,否就是false
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
