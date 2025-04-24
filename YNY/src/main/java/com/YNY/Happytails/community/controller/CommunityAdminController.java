package main.java.com.YNY.Happytails.community.controller;

import com.multi.happytails.community.reply.service.ReplyService;
import com.multi.happytails.community.service.ChatDogService;
import com.multi.happytails.community.service.ConferenceService;
import com.multi.happytails.community.service.DogloveService;
import com.multi.happytails.upload.model.dto.UploadDto;
import com.multi.happytails.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CommunityAdminController {

    @Autowired
    private DogloveService dogloveService;

    @Autowired
    private ChatDogService chatDogService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private ReplyService replyService;

    final String IMAGE_DOGLOVE = "L";
    final String IMAGE_CHATDOG = "C";
    final String IMAGE_CONFERENCE = "O";


    //내새꾸자랑 게시글 삭제 + 댓글 삭제 + 이미지 삭제
    @GetMapping("/doglove/delete/{dogloveNo}")
    public String deleteDoglove(@PathVariable Long dogloveNo) {
        dogloveService.delete(dogloveNo);

        replyService.replyDeleteAll("L", Math.toIntExact(dogloveNo));

        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_DOGLOVE, dogloveNo);

        if (uploadDtos != null && !uploadDtos.isEmpty()) {
            for (UploadDto uploadDto : uploadDtos) {
                uploadService.uploadDelete(uploadDto.getImageNo());
            }
        }
        return "redirect:/admin/dogLoveAdmin";
    }


    //떠들개 게시글 삭제 + 댓글 삭제 + 이미지 삭제
    @GetMapping("/chatdog/delete/{chatdogNo}")
    public String deleteChatdog(@PathVariable Long chatdogNo) {
        chatDogService.delete(chatdogNo);

        replyService.replyDeleteAll("C", Math.toIntExact(chatdogNo));


        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_CHATDOG, chatdogNo);

        if (uploadDtos != null && !uploadDtos.isEmpty()) {
            for (UploadDto uploadDto : uploadDtos) {
                uploadService.uploadDelete(uploadDto.getImageNo());
            }
        }
        return "redirect:/admin/chatdogAdmin";
    }


    //집사 회의 게시글 삭제 + 댓글 삭제 + 이미지 삭제
    @GetMapping("/conference/delete/{conferenceNo}")
    public String deleteConference(@PathVariable Long conferenceNo) {
        conferenceService.delete(conferenceNo);

        replyService.replyDeleteAll("O", Math.toIntExact(conferenceNo));


        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_CONFERENCE, conferenceNo);

        if (uploadDtos != null && !uploadDtos.isEmpty()) {
            for (UploadDto uploadDto : uploadDtos) {
                uploadService.uploadDelete(uploadDto.getImageNo());
            }
        }
        return "redirect:/admin/conferenceAdmin";
    }


}