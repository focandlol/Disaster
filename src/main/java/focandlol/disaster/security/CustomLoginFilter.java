package focandlol.disaster.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import focandlol.disaster.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;
  private final JwtProvider jwtProvider;

  public CustomLoginFilter(String filterProcessUrl, AuthenticationManager authenticationManager,
      ObjectMapper objectMapper, JwtProvider jwtProvider) {
    this.setFilterProcessesUrl(filterProcessUrl);
    this.setAuthenticationManager(authenticationManager);
    this.objectMapper = objectMapper;
    this.jwtProvider = jwtProvider;
  }


  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String username;
    String password;

    /**
     * 로그인 요청 시 인증을 시도
     * 요청 본문(body)에서 username(이메일)과 password(패스워드)를 추출한 후,
     * UsernamePasswordAuthenticationToken을 생성하여 인증을 위임
     */
    try {
      String requestBody = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));

      if (requestBody.isEmpty()) {
        throw new RuntimeException("Empty request body");
      }

      Map<String, String> jsonMap = objectMapper.readValue(requestBody, Map.class);

      username = jsonMap.get("username");
      password = jsonMap.get("password");
    } catch (IOException e) {
      throw new RuntimeException("fail to read request body", e);
    }

    /**
     * 토큰 만들고 인증 시도
     */
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username, password, null);

    return this.getAuthenticationManager().authenticate(authToken);
  }

  /**
   * 로그인 인증 성공 시 호출 인증된 customUserDetails를 토대로 jwt token 생성 후 header에 넣어서 리턴
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

    String token = jwtProvider.createJwt(userDetails);

    response.addHeader("Authorization", "Bearer " + token);
    log.info("login com");
  }

//  /**
//   * 로그인 인증 실패 시 호출
//   */
//  @Override
//  protected void unsuccessfulAuthentication(HttpServletRequest request,
//      HttpServletResponse response, AuthenticationException failed)
//      throws IOException, ServletException {
//    ErrorResponse error = new ErrorResponse(FAILED_LOGIN);
//    response.setStatus(FAILED_LOGIN.getStatus().value());
//    response.setCharacterEncoding("UTF-8");
//    response.setContentType("application/json");
//    response.getWriter().write(objectMapper.writeValueAsString(error));
//  }

}
