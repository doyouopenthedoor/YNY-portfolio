package main.java.com.YNY.Happytails.community.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatDogDTO {
    private Long chatdogNo;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private int recommendCount;
    private String categoryCode;

}
