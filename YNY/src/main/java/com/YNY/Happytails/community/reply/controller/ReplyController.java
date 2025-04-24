package main.java.com.YNY.Happytails.community.reply.controller;

import com.multi.happytails.community.reply.model.dto.ReplyDTO;
import com.multi.happytails.community.reply.service.ReplyCode;
import com.multi.happytails.community.reply.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/community")
public class ReplyController {

    @Autowired
    private ReplyService replyService;


    @PostMapping("/{communityCategoryCode}/{foreignNo}/reply/add")
    public String addReply(
            @PathVariable int foreignNo,
            @PathVariable String communityCategoryCode,
            @RequestParam String content,
            Principal principal) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        String writerId = principal.getName();
        ReplyDTO replyDTO = new ReplyDTO();
        replyDTO.setCommunityCategoryCode(communityCategoryCode);
        replyDTO.setForeignNo(foreignNo);
        replyDTO.setContent(content);
        replyDTO.setWriterId(writerId);
        replyService.addReply(replyDTO);

        return "redirect:/community/" + getRedirectUrl(communityCategoryCode) + "/" + foreignNo;
    }


    @PostMapping("/{communityCategoryCode}/{foreignNo}/reply/delete/{communityReplyNo}")
    @ResponseBody
    public ResponseEntity<?> deleteUserReply(@PathVariable String communityCategoryCode,
                                             @PathVariable int foreignNo,
                                             @PathVariable int communityReplyNo,
                                             Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null) {
            response.put("success", false);
            response.put("message", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String currentUserId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            // 댓글 작성자 확인 또는 관리자 권한 확인
            if (replyService.isReplyWriter(communityReplyNo, currentUserId) || isAdmin) {
                replyService.deleteReply(communityReplyNo);
                response.put("success", true);
                response.put("message", "댓글이 성공적으로 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "댓글 삭제 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/{communityCategoryCode}/{foreignNo}/reply/update/{communityReplyNo}")
    @ResponseBody
    public ResponseEntity<String> updateReplyAjax( Model model,@ModelAttribute ReplyDTO replyDTO,
            @PathVariable("communityReplyNo") int communityReplyNo,
            @RequestParam("content") String content) {

        // 댓글 내용 검증
        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("errorMessage", "댓글 내용을 입력해주세요.");
            return ResponseEntity.badRequest().body("댓글 내용을 입력해주세요.");
        }
        
        try {
            replyService.updateReply(communityReplyNo, content);
            return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류가 발생했습니다.");
        }


    }

    private String getRedirectUrl(String communityCategoryCode) {
        switch (ReplyCode.valueOf(communityCategoryCode)) {
            case L:
                return "doglove";
            case C:
                return "chatdog";
            case O:
                return "conference";
            case R:
                return "reels";
            default:
                throw new IllegalArgumentException("Invalid community category code: " + communityCategoryCode);
        }
    }
}