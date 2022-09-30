package com.example.dataType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * <h1>BigDecimal</h1>
 * Created by hanqf on 2022/9/30 11:09.
 */


public class BigDecimalUtil {

    public static void main(String[] args) {
        //double
        BigDecimal double1 = new BigDecimal(1.664);
        BigDecimal double2 = new BigDecimal(1.551);
        //字符串
        BigDecimal string1 = new BigDecimal("1.664");
        BigDecimal string2 = new BigDecimal("1.551");


        //加法add
        System.out.println(double1.add(double2));
        //3.2149999999999998578914528479799628257751464843750000
        //总长度保留3位（不含小数点），并且4舍5入
        System.out.println(double1.add(double2, new MathContext(3, RoundingMode.HALF_UP)));
        //3.21

        System.out.println(string1.add(string2));
        //3.215
        System.out.println(string1.add(string2, new MathContext(3, RoundingMode.HALF_UP)));
        //3.22

        //减法subtract
        System.out.println(double1.subtract(double2));
        //0.1129999999999999893418589635984972119331359863281250
        System.out.println(double1.subtract(double2, new MathContext(3, RoundingMode.HALF_UP)));
        //0.113

        System.out.println(string1.subtract(string2));
        //0.113
        System.out.println(string1.subtract(string2, new MathContext(3, RoundingMode.HALF_UP)));
        //0.113

        //乘法multiply
        System.out.println(double1.multiply(double2));
        //2.58086399999999977216269542168448017027012661692440307774058945965156564028575303382240235805511474609375
        System.out.println(double1.multiply(double2, new MathContext(3, RoundingMode.HALF_UP)));
        //2.58

        System.out.println(string1.multiply(string2));
        //2.580864
        System.out.println(string1.multiply(string2, new MathContext(3, RoundingMode.HALF_UP)));
        //2.58

        //除法divide
        //保留3位小数，四舍五入
        System.out.println(double1.divide(double2,3,RoundingMode.HALF_UP));
        //1.073
        System.out.println(double1.divide(double2, new MathContext(3, RoundingMode.HALF_UP)));
        //1.07

        //保留3位小数，四舍五入
        System.out.println(string1.divide(string2,3,RoundingMode.HALF_UP));
        //1.073
        System.out.println(string1.divide(string2, new MathContext(3, RoundingMode.HALF_UP)));
        //1.07

        System.out.println(BigDecimal.valueOf(112345L));
        //转浮点数，两位小数
        System.out.println(BigDecimal.valueOf(112345L,2));
        //1123.45
        System.out.println(BigDecimal.valueOf(112345L,3));
        //112.345

    }
}
