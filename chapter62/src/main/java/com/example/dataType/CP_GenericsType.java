package com.example.dataType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * <h1>获取泛型类型</h1>
 * Created by hanqf on 2022/6/27 15:52.
 * <p>
 * 参考资料：
 * https://www.jianshu.com/p/67e76d00c80c
 *
 * 使用方法：
 * Type type = new CP_GenericsType<List<String>>() {}.getType();
 * 注意这里一定要加上{}，表示使用的是匿名内部类
 */


public abstract class CP_GenericsType<T> {

    //泛型类型，如 CP_GenericsType<List<String>>，那么type就是List<String>
    private Type type;

    //泛型的实际Class类型，如 CP_GenericsType<List<String>>，那么这里classType只表示List，而不会包含List的声明泛型
    private Class<T> classType;

    public CP_GenericsType() {
        Type superclass = getClass().getGenericSuperclass();
        //获取第一个泛型类型
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        //判断当前类型是否依旧是一个泛型类型，如List<String>
        if (this.type instanceof ParameterizedType) {
            //值转换为外层类型，如List
            this.classType = (Class<T>) ((ParameterizedType) this.type).getRawType();
        }
        //普通类型则转换为Class
        else {
            this.classType = (Class<T>) this.type;
        }
    }

    public Class<T> getClassType() {
        return classType;
    }

    public Type getType() {
        return type;
    }

    /**
     * 打印泛型详细信息
    */
    public void parseGenericInfo() {
        // CP_GenericsType<java.util.List<java.lang.String>>
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        System.out.println(genericSuperclass);
        // java.util.List<java.lang.String>
        Type targetGenericClass = genericSuperclass.getActualTypeArguments()[0];
        System.out.println(targetGenericClass);
        Class targetClass;
        if (targetGenericClass instanceof ParameterizedType) {
            // interface java.util.List
            targetClass = (Class) ((ParameterizedType) targetGenericClass).getRawType();
            System.out.println(targetClass);

            for (int i = 0; i < ((ParameterizedType) targetGenericClass).getActualTypeArguments().length; i++) {
                //E -> class java.lang.String
                System.out.println(targetClass.getTypeParameters()[i] + " -> " + ((ParameterizedType) targetGenericClass).getActualTypeArguments()[i]);
            }
        } else {
            targetClass = (Class) targetGenericClass;
            System.out.println(targetClass);
        }
    }

    public static void main(String[] args) {
        final Class<List<String>> aClass = new CP_GenericsType<List<String>>() {}.getClassType();
        //interface java.util.List
        System.out.println(aClass);

        final Type type = new CP_GenericsType<List<String>>() {}.getType();
        //java.util.List<java.lang.String>
        System.out.println(type);

        new CP_GenericsType<List<String>>() {}.parseGenericInfo();

    }

}
