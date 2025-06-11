package focandlol.disaster.security;

import focandlol.disaster.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
  private final SecretKey secretKey;
  private final Long tokenValidTime;

  public JwtProvider(@Value("${spring.jwt.secret}") String secret,
      @Value("${spring.jwt.time}") Long tokenTime) {
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
    tokenValidTime = tokenTime;
  }

  /**
   * jwt 토큰 검증, UsernamePasswordAuthenticationToken 생성
   */
  public Authentication authentication(String token) {
    Jws<Claims> jws = verifyToken(token);

    CustomUserDetails details = CustomUserDetails.builder()
        .id(getId(jws))
        .email(getEmail(jws))
        .build();

    return new UsernamePasswordAuthenticationToken(details, null);
  }

  /**
   * 토큰 생성
   */
  public String createJwt(CustomUserDetails userDetails) {

    return Jwts.builder()
        .claim("id", userDetails.getId())
        .claim("email", userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + tokenValidTime))
        .signWith(secretKey)
        .compact();
  }

  /**
   * 토큰 검증, signature 유효하면 claim 정보 반환
   */
  public Jws<Claims> verifyToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
  }

  public Long getId(Jws<Claims> jws) {
    return jws.getPayload().get("id", Long.class);
  }

  public String getUsername(Jws<Claims> jws) {
    return jws.getPayload().get("username", String.class);
  }

  public String getName(Jws<Claims> jws) {
    return jws.getPayload().get("name", String.class);
  }

  public String getEmail(Jws<Claims> jws) {
    return jws.getPayload().get("email", String.class);
  }

  public List<String> getRoles(Jws<Claims> jws) {
    return jws.getPayload().get("roles", List.class);
  }
}
