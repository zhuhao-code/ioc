package com.qf.ioc;

import com.qf.ioc.framework.annotation.Component;

@Component
public class Coffee implements IWater{


    public String getType() {
        return "拿铁";
    }


}
