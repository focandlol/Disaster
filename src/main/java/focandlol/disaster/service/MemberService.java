package focandlol.disaster.service;

import focandlol.disaster.domain.Member;
import focandlol.disaster.dto.SignupDto;
import focandlol.disaster.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public SignupDto signup(SignupDto signupDto) {
    memberRepository.save(Member.builder()
        .email(signupDto.getEmail())
        .password(passwordEncoder.encode(signupDto.getPassword()))
        .build());

    return signupDto;
  }


}
