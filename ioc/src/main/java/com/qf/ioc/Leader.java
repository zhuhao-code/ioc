package com.qf.ioc;

import com.qf.ioc.framework.annotation.Autowired;
import com.qf.ioc.framework.annotation.Component;

@Component
public class Leader {

    @Autowired
    public IWater water;


    @Autowired
    private Coffee coffee;

//    public void drink(){
//        System.out.println("喝"+water.getType());
//    }
}
