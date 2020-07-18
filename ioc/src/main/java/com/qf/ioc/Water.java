package com.qf.ioc;


import com.qf.ioc.framework.annotation.Component;

@Component
public class Water implements IWater{
    private String type;

    public String getType() {
        return "水";
    }

    public void setType(String type) {
        this.type = type;
    }
}
