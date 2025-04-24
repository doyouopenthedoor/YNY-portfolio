package main.java.com.YNY.Happytails.community.reply.model.dao;

import com.multi.happytails.community.reply.model.dto.ReplyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReplyDAO {

    void insertReply(ReplyDTO replyDTO);
    void updateReply(ReplyDTO replyDTO);
    void deleteReply(int communityReplyNo);

    List<ReplyDTO> selectReplyByForeignNo(String communityCategoryCode, int foreignNo);

    void replyDeleteAll(String communityCategoryCode, int foreignNo);

    ReplyDTO selectReplyById(int communityReplyNo);

    void updateReply(@Param("communityReplyNo") int communityReplyNo, @Param("content") String content);



}