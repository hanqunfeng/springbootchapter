package org.example.aspect;/**
 * Created by hanqf on 2020/3/5 10:47.
 */


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.example.aspect.enhance.UserValidator;
import org.example.aspect.enhance.UserValidatorImpl;
import org.example.model.User;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/5 10:47
 */
@Component
//标识是一个Aspect代理类
@Aspect
//如果有多个切面拦截相同的切点，可以用@Order指定执行顺序
//@Order(1)
public class MyAspect {

    //增强指定的类，使其具有声明的接口实现
    @DeclareParents(value = "org.example.service.UserServiceImpl+", defaultImpl = UserValidatorImpl.class)
    public UserValidator userValidator;

    //切点
    //execution 表示在执行的时候，拦截里面的正则匹配的方法
    //*表示任意返回类型的方法
    //org.example.service.UserServiceImpl 指定目标对象的全限定名称；
    //printUser 指定目标对象的方法
    //(..)表示任意参数进行匹配。
    //@Pointcut("execution(* org.example.service.UserServiceImpl.printUser(..))")
    @Pointcut("execution(* org.example.service.*.*(..))")
    public void pointCut() {

    }

    //切面
    //事前通知，方法前执行
    //可以为方法传递目标对象的方法参数
    @Before("pointCut() && args(user)")
    public boolean before(JoinPoint joinPoint, User user) {
        System.out.println("before...."+user);
        System.out.println(joinPoint.getArgs()); //目标方法的参数数组
        System.out.println(joinPoint.getArgs()[0]); //这里就是user
        System.out.println(joinPoint.getTarget()); //目标对象，就是org.example.service.UserServiceImpl
        System.out.println(joinPoint.getThis()); //目标对象，就是org.example.service.UserServiceImpl
        System.out.println(joinPoint.getKind()); //method-execution
        System.out.println(joinPoint.getSignature()); //void org.example.service.UserService.printUser(User)
        return true;
    }



    //相同的切面按方法名称排序，使用@Order注解不起作用这里先执行after1,后执行after2
    //事后通知，方法后执行，无论是否抛出异常都会执行，在AfterReturning前运行
    @After("pointCut() && args(user)")
    public void after1(JoinPoint joinPoint, User user) {
        System.out.println("after1...."+user);
    }

    //事后通知，方法后执行，无论是否抛出异常都会执行，在AfterReturning前运行
    @After("pointCut() && args(user)")
    public void after2(JoinPoint joinPoint, User user) {
        System.out.println("after2...."+user);
    }




    //返回通知，正常返回结果后执行
    @AfterReturning("pointCut() && args(user)")
    public void afterReturning(JoinPoint joinPoint, User user) {
        System.out.println("afterReturning...."+user);
    }

    //异常通知，异常时执行
    @AfterThrowing("pointCut() && args(user)")
    public void afterThrowing(JoinPoint joinPoint, User user) {
        System.out.println("afterThrowing...."+user);
    }

    //环绕通知,一般不建议使用，可以通过@Before和@AfterReturning实现
    @Around("pointCut() && args(user)")
    public void around(ProceedingJoinPoint proceedingJoinPoint, User user) throws Throwable {
        //在before前执行，据说xml配置方式，是会在before后运行，这里要注意
        System.out.println("arount before...."+user);
        System.out.println(proceedingJoinPoint.getArgs()[0]); //这里就是user
        System.out.println(proceedingJoinPoint.getTarget()); //目标对象，就是org.example.service.UserServiceImpl
        System.out.println(proceedingJoinPoint.getThis()); //目标对象，就是org.example.service.UserServiceImpl

        //实际执行的方法
        proceedingJoinPoint.proceed();
        //在after前执行，如果方法抛出异常则不执行
        System.out.println("arount after...."+user);
    }


}
