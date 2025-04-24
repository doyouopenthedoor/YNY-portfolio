package main.java.com.YNY.Happytails.community.reply.service;

import com.multi.happytails.community.reply.model.dao.ReplyDAO;
import com.multi.happytails.community.reply.model.dto.ReplyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReplyService {

    @Autowired
    private ReplyDAO replyDAO;

    public void addReply(ReplyDTO replyDTO) {
        LocalDateTime now = LocalDateTime.now();
        replyDAO.insertReply(replyDTO);
    }

    public List<ReplyDTO> getReplyByForeignNo(String communityCategoryCode, int foreignNo) {
        return replyDAO.selectReplyByForeignNo(communityCategoryCode, foreignNo);
    }

    public void deleteReply(int communityReplyNo) {
        replyDAO.deleteReply(communityReplyNo);
    }

    // 게시글 삭제할 때 댓글도 전체 삭제
    public void replyDeleteAll(String communityCategoryCode, int foreignNo) {
        replyDAO.replyDeleteAll(communityCategoryCode, foreignNo);
    }

    public boolean isReplyWriter(int communityReplyNo, String writerId) {
        ReplyDTO reply = replyDAO.selectReplyById(communityReplyNo);
        return reply != null && reply.getWriterId().equals(writerId);
    }


    public ReplyDTO getReplyById(int communityReplyNo) {
        return replyDAO.selectReplyById(communityReplyNo);
    }


    public void updateReply(int communityReplyNo, String content) {

        replyDAO.updateReply(communityReplyNo, content);
    }

}




