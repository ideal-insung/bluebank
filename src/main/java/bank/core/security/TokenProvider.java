package bank.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secret; // 프로퍼티에서 읽어오는 값 (String)

    private Key secretKey;

    private final long validityInMilliseconds = 3600000; // 1시간
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 문자열로 받은 secretKey를 실제 Key 객체로 변환
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }

    public String createToken(Long userid){
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(userid.toString()) //사용자 아이디를 subject로 설정
                .setIssuedAt(now)              //발행시간
                .setExpiration(validity)       //만료시간
                .signWith(SECRET_KEY) //비밀키로 서
                .compact();
    }

    public Long getId(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
