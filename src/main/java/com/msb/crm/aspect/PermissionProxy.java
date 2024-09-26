package com.msb.crm.aspect;

import com.msb.crm.annoation.RequiredPermission;
import com.msb.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {
    //注入session
    @Resource
    private HttpSession session;

    // 拦截带有 @RequiredPermission 注解的方法
    @Around("@annotation(com.msb.crm.annoation.RequiredPermission)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Object result=null;
        // 可以在这里添加自定义的前置逻辑，如日志、权限校验等

        //得到当前用户拥有的权限 (session作用域)
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        //判断用户是否拥有权限
        if(null==permissions || permissions.size()<1){
            // 抛出自定义异常
            throw new AuthException();
        }

        //使用 MethodSignature 获取当前方法的签名信息
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //通过反射，获取方法上 @RequiredPermission 注解的实例，以便后续校验注解中的权限码。
        RequiredPermission requiredPermission =methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        //判断注解上对应的状态码
        if (!(permissions.contains(requiredPermission.code()))){
            //如果权限中不包括当前方法注解指定的权限码,则抛出异常
            throw new AuthException();
        }

        // 执行目标方法
        result = pjp.proceed();  // 继续执行方法

        return result;
    }
}