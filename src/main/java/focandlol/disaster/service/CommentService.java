package focandlol.disaster.service;

import static focandlol.disaster.exception.ErrorCode.*;

import focandlol.disaster.domain.Comment;
import focandlol.disaster.domain.Member;
import focandlol.disaster.domain.Report;
import focandlol.disaster.dto.CommentCreateDto;
import focandlol.disaster.dto.CommentUpdateDto;
import focandlol.disaster.exception.CustomException;
import focandlol.disaster.repository.CommentRepository;
import focandlol.disaster.repository.MemberRepository;
import focandlol.disaster.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;

  public void save(Long reportId, Long memberId, CommentCreateDto dto) {
    Report report = getReport(reportId);
    Member member = getMember(memberId);

    commentRepository.save(Comment.from(dto.getContent(), report, member, null, null));
  }

  @Transactional
  public void replySave(Long commentId, Long memberId, CommentCreateDto dto) {
    Comment comment = getComment(commentId);
    Member member = getMember(memberId);

    Member replyMember = null;
    Comment parent = null;

    if (comment.getParent() != null){
      parent = comment;
    }else{
      parent = comment.getParent();
      replyMember = comment.getMember();
    }

    commentRepository.save(
        Comment.from(dto.getContent(), comment.getReport(), member, parent, replyMember));
  }

  @Transactional
  public void update(Long commentId, Long memberId, CommentUpdateDto dto) {
    Comment comment = getComment(commentId);
    Member member = getMember(memberId);

    validSameMember(member, comment);

    comment.setContent(dto.getContent());
  }

  public void delete(Long commentId, Long memberId){
    Comment comment = getComment(commentId);
    Member member = getMember(memberId);

    validSameMember(member, comment);

    commentRepository.delete(comment);
  }

  private static void validSameMember(Member member, Comment comment) {
    if(!member.getId().equals(comment.getMember().getId())){
      throw new CustomException(ANOTHER_MEMBER, null);
    }
  }

  private Member getMember(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND, null));
  }

  private Report getReport(Long reportId) {
    return reportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND, null));
  }

  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND, null));
  }

}
