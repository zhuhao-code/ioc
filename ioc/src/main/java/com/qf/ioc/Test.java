package com.qf.ioc;

import com.qf.ioc.framework.Injection;

public class Test {
    public static void main(String[] args) {
        Injection injection = new Injection("com.qf.ioc");

        try {
            Leader leader = (Leader) injection.getBean("leader");
            System.out.println(leader);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
