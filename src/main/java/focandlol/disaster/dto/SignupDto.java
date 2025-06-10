package focandlol.disaster.dto;

import focandlol.disaster.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

  private String email;

  private String password;
}
