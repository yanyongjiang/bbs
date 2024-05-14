package com.yyj.apps.framework.Interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yyj.apps.framework.constant.TokenConstant;
import com.yyj.apps.framework.util.TokenUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //这里的"token",前端header传回来的token参数名字是什么这里就叫什么
        String token = request.getHeader(TokenConstant.AUTH_TOKEN);
        if (token != null) {
            boolean result = TokenUtils.verify(token.substring(7));
            if (result) {
                if(TokenUtils.isTokenAlmostExpired(token.substring(7))){
                    response.setHeader(TokenConstant.AUTH_TOKEN, TokenUtils.sign(TokenUtils.getTokenAccount(token.substring(7))));
                    response.setHeader("Access-Control-Expose-Headers", TokenConstant.AUTH_TOKEN);
                    return true;
                }
            } else {
                response.setContentType("application/json; charset=utf-8");
                try {
                    JSONObject json = new JSONObject();
                    json.put("msg", "token verify fail");
                    json.put("code", "500");
                    response.getWriter().append(json.toString());
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }
}