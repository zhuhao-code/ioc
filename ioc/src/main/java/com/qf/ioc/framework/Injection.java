package com.qf.ioc.framework;

import com.qf.ioc.Test2;
import com.qf.ioc.framework.annotation.Autowired;
import com.qf.ioc.framework.annotation.Component;
import com.qf.ioc.framework.exception.UnexpectedBeanDefitionalException;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Injection {

    private final static HashMap<Class, List<Class>> fatherSonHashMap = new HashMap<Class, List<Class>>();

    public static final HashMap<String, Object> beansMap = new HashMap<String, Object>();

    private static final List<Class> beansList = new ArrayList<Class>();

    private String basePackage;

    public Injection() {
    }

    public Injection(String basePackage) {
        this.basePackage = basePackage;

        try {
            init(basePackage);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getBean(String id){
        return beansMap.get(id);
    }

    public Object inject(Class leaderClass) throws Exception{

        try {
            //通过反射给water赋值
            Field[] declaredFields = leaderClass.getDeclaredFields();
            String id1 = getId(leaderClass);
            Object leader = beansMap.get(id1);
            if(leader == null){
                leader = leaderClass.newInstance();
            }

            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                field.setAccessible(true);

                Autowired annotation = field.getAnnotation(Autowired.class);
                if (annotation!=null){

                    Class aClass = field.getType();

                    if(aClass.isInterface()){

                        List<Class> classList = fatherSonHashMap.get(aClass);

                        if(classList == null){
                            throw new NullPointerException();
                        }


                        if (classList != null && classList.size()==1){
                            Class sonClass = classList.get(0);

                            String id = getId(sonClass);
                            Object bean = beansMap.get(id);
                            if(bean == null){
                                Object instance = sonClass.newInstance();
                                field.set(leader,instance);
                                beansMap.put(id,instance);
                            }else{
                                field.set(leader,bean);
                            }

                            field.set(leader,sonClass.newInstance());
                        }else {
                            boolean isAutowiredFail = false;

                            for (int j = 0; j < classList.size(); j++) {
                                Class sonClass = classList.get(j);
                                String fieldName = field.getName();
                                String substring = fieldName.substring(1, fieldName.length());
                                String upperCase = fieldName.toUpperCase();
                                char charAt = upperCase.charAt(0);
                                String className = charAt+substring;
                                if(sonClass.getName().endsWith(className)){
                                    String id = getId(sonClass);
                                    Object bean = beansMap.get(id);
                                    if(bean == null){
                                        Object instance = sonClass.newInstance();
                                        field.set(leader,instance);
                                        beansMap.put(id,instance);
                                    }else{
                                        field.set(leader,bean);
                                    }
                                    isAutowiredFail = true;
                                }
                            }
                            if (isAutowiredFail){
                                throw new UnexpectedBeanDefitionalException("找到了两个对象,无法识别该注入哪一个 ");
                            }
                        }
                    }else {
                        String id = getId(aClass);
                        Object bean = beansMap.get(id);
                        if(bean == null){
                            Object instance = aClass.newInstance();
                            field.set(leader,instance);
                            beansMap.put(id,instance);
                        }else{
                            field.set(leader,bean);
                        }
                    }
                }
            }
            return leader;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void init(String basePackage) throws Exception {
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


                    beansList.add(aClass);

                    
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
            }
        }

        for (int i = 0,size = beansList.size(); i < size; i++) {
            Class aClass = beansList.get(i);
            Object obj = inject(aClass);
            String id = getId(aClass);
            beansMap.put(id,obj);
        }
        System.out.println(beansMap);
    }

    private String getId(Class aClass){
        String tempId = aClass.getName();
        int lastIndex = tempId.lastIndexOf(".");
        tempId = tempId.substring(lastIndex+1,tempId.length());
        char charAt = tempId.toLowerCase().charAt(0);
        String substring = tempId.substring(1, tempId.length());
        String id = charAt+substring;
        return id;
    }

}
