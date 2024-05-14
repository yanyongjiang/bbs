package com.yyj.apps.framework.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yyj.apps.framework.constant.TokenConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
@Slf4j
public class TokenUtils {

    //token到期时间  1000等于1秒
    private static final long EXPIRE_TIME= 30*60*1000;
    //密钥盐
    private static final String TOKEN_SECRET="l2i3t4i5a6npei**3nkjnj??";

    /**
     * 生成token
     * @param account
     * @return
     */
    public static String sign(String account){

        String token=null;
        try {
            Date expireAt=new Date(System.currentTimeMillis()+EXPIRE_TIME);
            token = JWT.create()
                    //发行人
                    .withIssuer("auth0")
                    //存放数据
                    .withClaim("account",account)
                    //过期时间
                    .withExpiresAt(expireAt)
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (IllegalArgumentException| JWTCreationException je) {
           log.info("sign",je);
        }
        return token;
    }


    /**
     * token验证
     * @param token
     * @return
     */
    public static String getTokenAccount(String token){
        try {
            //创建token验证器
            JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            return decodedJWT.getClaim("account").asString();
        } catch (IllegalArgumentException | JWTVerificationException e) {
            //抛出错误即为验证不通过
            log.info("getTokenAccount",e);
            return null;
        }
    }

    /**
     * token验证
     * @param token
     * @return
     */
    public static Boolean verify(String token){
        try {
            //创建token验证器
            JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            Date expireAt= decodedJWT.getExpiresAt();
            if(expireAt.compareTo(new Date())<0){
                return false;
            }
        } catch (IllegalArgumentException | JWTVerificationException e) {
            //抛出错误即为验证不通过
            log.info("getTokenAccount",e);
            return false;
        }
        return true;
    }

    /**
     * token验证
     * @param token
     * @return
     */
    public static Boolean isTokenAlmostExpired(String token){
        try {
            //创建token验证器
            JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            Date expireAt= decodedJWT.getExpiresAt();
            if((expireAt.getTime()+5*60*1000)-new Date().getTime()>0){
                return true;
            }
        } catch (IllegalArgumentException | JWTVerificationException e) {
            //抛出错误即为验证不通过
            log.info("isTokenAlmostExpired",e);
            return false;
        }
        return false;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        ServletRequestAttributes s=getRequestAttributes();
        return s!=null?s.getRequest():null;
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        ServletRequestAttributes s=getRequestAttributes();
        return s!=null?s.getResponse():null;
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取请求头部中的token
     *author:maxinbo
     *@return
     *@String
     */
    public static String getToken() {
        return getToken(getRequest());
    }

    /**
     * 根据httpRequest获取请求头部中的token
     *author:maxinbo
     *@param httpRequest
     *@return
     *@String
     */
    public static String getToken( HttpServletRequest httpRequest) {
        String token=httpRequest.getHeader(TokenConstant.AUTH_TOKEN);
        if(token==null||"".equals(token)) {
            return null;
        }
        return token;
    }

    /**
     * token验证
     * @return
     */
    public static String getTokenAccount(){
        try {
            String token=getToken();
            //创建token验证器
            JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            return decodedJWT.getClaim("account").asString();
        } catch (IllegalArgumentException | JWTVerificationException e) {
            //抛出错误即为验证不通过
            log.info("getTokenAccount",e);
            return null;
        }
    }
}