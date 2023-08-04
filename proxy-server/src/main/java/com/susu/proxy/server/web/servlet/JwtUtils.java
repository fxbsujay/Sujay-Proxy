package com.susu.proxy.server.web.servlet;

import com.susu.proxy.core.common.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtils {

    public static final long EXPIRE = 1000 * 60 * 60 * 24;

    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    public static String encryption(String username){
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")

                .setSubject("greenLeaf")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                .claim("username", username)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
    }

    /**
     * 判断是否存在与有效
     */
    public static boolean check(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断是否存在与有效
     */
    public static boolean check(HttpServletRequest request) {
        String jwtToken = request.getHeader("s-token");
        return check(jwtToken);
    }

    public static String decode(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("username");
    }

    /**
     * 根据token获取用户名
     */
    public static String decode(HttpServletRequest request) {
        String token = request.getHeader("s-token");
        return decode(token);
    }

    public static void main(String[] args) {
        String token = encryption("admin");
        System.out.println(token);
        System.out.println(check(token));
        String username = decode(token);
        System.out.println(username);
    }
}
