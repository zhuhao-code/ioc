package com.qf.ioc;

import com.qf.ioc.framework.annotation.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test2 {

    private final static HashMap<Class, List<Class>> fatherSonHashMap = new HashMap<Class, List<Class>>();

    public static void main(String[] args) throws ClassNotFoundException {
        String basePackage = "com.qf.ioc";
        URL resource = Test2.class.getResource("/");
        String resourcePath = resource.getPath();
        String replaceAll = basePackage.replaceAll("\\.", "/");
        String path = resourcePath+replaceAll;

        File file = new File(path);
        File[] files = file.listFiles();
        for (File file1 : files) {
            String name = file1.getName();
            if(file1.isFile()&&name.endsWith(".class")){
                String[] split = name.split("\\.");
                Class<?> aClass = Class.forName(basePackage + "." + split[0]);
                Component annotation = aClass.getAnnotation(Component.class);
                if(annotation!=null){
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for(int j=0;j<interfaces.length;j++){
                        Class<?> anInterface = interfaces[j];
                        List<Class> sonList = fatherSonHashMap.get(anInterface);
                        if (sonList==null){
                            ArrayList<Class> sonClassesList = new ArrayList<Class>();
                            sonClassesList.add(aClass);
                            fatherSonHashMap.put(anInterface,sonClassesList);
                        }else {
                            sonList.add(aClass);
                        }
                    }
                }

                System.out.println(fatherSonHashMap);
            }
        }
    }
}
