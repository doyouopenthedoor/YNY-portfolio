package main.java.com.YNY.Happytails.community.reply.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyDTO {

    private int communityReplyNo;
    private String communityCategoryCode;
    private int foreignNo;
    private String writerId;
    private String content;

}
