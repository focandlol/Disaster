package focandlol.disaster.service;

import static focandlol.disaster.exception.ErrorCode.ALREADY_EXISTS_EMAIL;

import focandlol.disaster.domain.Member;
import focandlol.disaster.dto.SignupDto;
import focandlol.disaster.exception.CustomException;
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
    if(memberRepository.existsByEmail(signupDto.getEmail())){
      throw new CustomException(ALREADY_EXISTS_EMAIL, null);
    }
    memberRepository.save(Member.builder()
        .email(signupDto.getEmail())
        .password(passwordEncoder.encode(signupDto.getPassword()))
        .build());

    return signupDto;
  }


}
