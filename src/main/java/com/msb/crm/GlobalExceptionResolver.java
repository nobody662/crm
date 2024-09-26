package com.msb.crm;

import com.alibaba.fastjson.JSON;

import com.msb.crm.base.ResultInfo;
import com.msb.crm.exceptions.AuthException;
import com.msb.crm.exceptions.NoLoginException;
import com.msb.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        /**
         *   方法返回值类型
         *       视图
         *       json
         *  如何判断方法返回的是视图  还是 json?
         *     约定:如果方法级别配置@ResponseBody  方法响应内容为json  反之 方法响应内容为html页面
         */
        ModelAndView mv=new ModelAndView("error");
       //设置异常信息
        mv.addObject("code",500);
        mv.addObject("msg","系统异常，请稍后再试...");

        /**
         * 非法拦截请求
         * 判断是否抛出未登录异常 如果有未登录异常 跳转到登录页面
         */
        if(ex instanceof NoLoginException){
            ModelAndView mv1=new ModelAndView("redirect:/index");
            return  mv1;
        }


        //判断HandlerMethod
        if(handler instanceof  HandlerMethod){
         //类型转换
            HandlerMethod hm = (HandlerMethod) handler;
            //通过反射获得注解对象 没有则返回空
            ResponseBody responseBody= hm.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if(null == responseBody){
                /**
                 * 方法响应内容为视图
                 */
                //判断是否为自定义异常
                if(ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    mv.addObject("msg",pe.getMsg());
                    mv.addObject("code",pe.getCode());
                } else  if(ex instanceof AuthException){    //权限认证异常
                    AuthException pe = (AuthException) ex;
                    mv.addObject("msg",pe.getMsg());
                    mv.addObject("code",pe.getCode());
                }
                return mv;
            }else{
                /**
                 *  方法响应内容为json
                 */
                //设置默认的异常处理
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常，请稍后再试...");

                //判断是否为自定义异常
                if(ex instanceof  ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }else if(ex instanceof AuthException){  //权限认证异常
                    AuthException pe = (AuthException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }


                //设置相应类型以及编码格式 json类型
                response.setContentType("application/json;charset=utf-8");
                response.setCharacterEncoding("utf-8");
                //得到输出字符流
                PrintWriter pw=null;
                try {
                    //转成json字符串输出
                    pw=response.getWriter();
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(null !=pw){
                        pw.close();
                    }
                }
                return null;
            }
        }else{
            return mv;
        }

    }
}
