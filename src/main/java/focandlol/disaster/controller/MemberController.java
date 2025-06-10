package focandlol.disaster.controller;

import focandlol.disaster.domain.Member;
import focandlol.disaster.dto.SignupDto;
import focandlol.disaster.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/signup")
  public ResponseEntity<SignupDto> signup(@RequestBody SignupDto signupDto) {
    return ResponseEntity.ok(memberService.signup(signupDto));
  }

}
