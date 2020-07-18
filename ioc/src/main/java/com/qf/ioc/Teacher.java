package com.qf.ioc;

public class Teacher {
    public Water water;
    public void drink(){
        System.out.println("老师喝"+water.getType());
    }
}
