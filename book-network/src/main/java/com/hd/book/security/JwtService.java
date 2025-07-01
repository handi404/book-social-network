package com.hd.book.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;


    // 仅使用用户详细信息生成令牌
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // 使用额外声明和用户详细信息生成令牌
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /*
    * 构建令牌
    * */
    public String buildToken(
            Map<String, Object> extraClaims, // 额外声明
            UserDetails userDetails,
            long expiration // 到期日期
    ){
        // 用户权限列表
        var authorities = userDetails.getAuthorities()
                .stream()
                // getAuthority: 返回授予权限的表示（如果授予的权限无法以足够精确的字符串形式表示，则返回空值）。
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // subject(主题)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 发布日期，这些信息将帮助我们计算到期日期或检查令牌是否仍然有效
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 到期日期
                .claim("authorities", authorities) // 追加声明：(name, value)
                .signWith(getSignInKey()) // 用于签署此令牌的密钥进行签名
                .compact(); // 生成并返回令牌
    }

    /*
    * 验证令牌是否有效（令牌是否属于用户 && 令牌是否过期）
    * */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return (extractUsername(token).equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // token是否过期，过期返回true
    public boolean isTokenExpired(String token) {
        /* public boolean before(Date when)
         * true：如果当前 Date 对象表示的时间严格早于参数 when 表示的时间。
         * false：如果当前 Date 对象表示的时间等于或晚于参数 when 表示的时间。
         * */
        // 若过期时间比当前时间晚，说明还未过期，返回false
        return extractExpiration(token).before(new Date());
    }

    // 获取token到期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取令牌主题
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /*
     * Function<T, R> 是一个函数式接口。简单来说，它代表了一个“接收一个参数并产生一个结果”的函数。
     * T：代表输入参数的类型 (Type of input)。
     * R：代表返回结果的类型 (Type of result)。
     * */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        // apply：触发实例化的Function的方法
        return claimsResolver.apply(claims);
    }

    /*
    * 获取令牌中拥有的所有声明
    * Claims 来自 io.jsonwebtoken，添加的jjwt依赖
    * */
    public Claims extractAllClaims(String token) {
        // Jwts 来自 io.jsonwebtoken
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // 签名密钥 - 创建、生成或解码令牌时，需要使用签名密钥
                .build() // 它是一个构建器，一旦对象被构建，就可以调用方法 如 parseClaimsJwt()
                .parseClaimsJws(token) // 解析令牌，一旦令牌被解析， 就可以调用方法获取主体
                .getBody();
    }

    private Key getSignInKey() {
        // 解码密钥
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // HMAC（Hash Message Authentication Code(哈希消息验证码)）
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
