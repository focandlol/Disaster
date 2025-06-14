package focandlol.disaster.controller;

import focandlol.disaster.dto.CommentCreateDto;
import focandlol.disaster.dto.CommentUpdateDto;
import focandlol.disaster.dto.CustomUserDetails;
import focandlol.disaster.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public void save(@AuthenticationPrincipal CustomUserDetails customUserDetails,
      @RequestBody CommentCreateDto dto) {
    commentService.save(dto.getReportId(), customUserDetails.getId(), dto);
  }

  @PostMapping("{commentId}/reply")
  public void reply(@PathVariable Long commentId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @RequestBody CommentCreateDto dto) {
    commentService.replySave(commentId, customUserDetails.getId(), dto);
  }

  @PutMapping("{commentId}")
  public void update(@PathVariable Long commentId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @RequestBody CommentUpdateDto dto) {
    commentService.update(commentId, customUserDetails.getId(), dto);
  }

  @DeleteMapping("{commentId}")
  public void delete(@PathVariable Long commentId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    commentService.delete(commentId, customUserDetails.getId());
  }

}
