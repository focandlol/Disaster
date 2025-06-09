package focandlol.disaster.dto;

import focandlol.disaster.domain.Member;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

  private Long id;
  private String email;
  private String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public Long getId() {
    return id;
  }

  public static CustomUserDetails from(Member member) {
    return CustomUserDetails.builder()
        .id(member.getId())
        .email(member.getEmail())
        .password(member.getPassword())
        .build();
  }
}
