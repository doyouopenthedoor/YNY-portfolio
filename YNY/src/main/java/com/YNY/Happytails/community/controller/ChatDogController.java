package main.java.com.YNY.Happytails.community.controller;

import com.multi.happytails.community.model.dto.ChatDogDTO;
import com.multi.happytails.community.reply.model.dto.ReplyDTO;
import com.multi.happytails.community.reply.service.ReplyService;
import com.multi.happytails.community.service.ChatDogService;
import com.multi.happytails.upload.model.dto.UploadDto;
import com.multi.happytails.upload.service.UploadService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/community/chatdog")
public class ChatDogController {

    @Autowired
    private ChatDogService chatDogService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ReplyService replyService;

    final String replyCategoryCode = "C";


    final String IMAGE_CODE = "C";

    final String categoryCode = "CHATDOG_CODE";

    @GetMapping
    public String chatDogList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        Page<ChatDogDTO> chatdogPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            chatdogPage = chatDogService.cdsearch(keyword.trim(), page - 1, size, sort);
        } else {
            // 검색어가 없는 경우
            if ("recommendCount".equals(sort)) {
                chatdogPage = chatDogService.findAllSortedByRecommendation(page - 1, size);
            } else {
                chatdogPage = chatDogService.findAllSortedByDate(page - 1, size);
            }
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("chatdog", chatdogPage.getContent());
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", chatdogPage.getNumber() + 1);
        model.addAttribute("totalPages", chatdogPage.getTotalPages());
        model.addAttribute("totalItems", chatdogPage.getTotalElements());

        return "community/chatdoglist";
    }

    @GetMapping("/{chatdogNo}")
    public String chatdogDetail(@PathVariable("chatdogNo") Long chatdogNo,
                                Model model) {

        ChatDogDTO chatdog = chatDogService.findById(chatdogNo);
        //댓글 조회
        List<ReplyDTO> reply = replyService.getReplyByForeignNo(replyCategoryCode, Math.toIntExact(chatdogNo));

        List<UploadDto> uploadDtoList = uploadService.uploadSelect(IMAGE_CODE, chatdogNo);
        UploadDto uploadDto = UploadDto.builder().categoryCode(categoryCode).foreignNo(chatdogNo).build();


        // 조회된 데이터가 있을 경우
        if (chatdog != null) {
            model.addAttribute("chatdog", chatdog);
            model.addAttribute("reply", reply);
            model.addAttribute("imageFiles", uploadDtoList);
            model.addAttribute("uploadDto", uploadDto);
            return "community/chatdogdetail";
        } else {
            return "redirect:/community/chatdog";
        }

    }

    @GetMapping("/create")
    public String chatdogCreate(Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }

        return "community/chatdogcreate";
    }

    @PostMapping
    public String save(@ModelAttribute ChatDogDTO chatDogDTO,
                       @RequestParam("imageFiles") @Nullable List<MultipartFile> imageFiles,
                       Principal principal, Model model) {

        String userId = principal.getName();
        chatDogDTO.setUserId(userId);

        //게시판 카테고리 코드
        chatDogDTO.setCategoryCode(categoryCode);
        chatDogDTO.setCreateTime(LocalDateTime.now());

        if (chatDogDTO.getTitle() == null || chatDogDTO.getTitle().trim().isEmpty() ||
                chatDogDTO.getContent() == null || chatDogDTO.getContent().trim().isEmpty()) {
            model.addAttribute("errorMessage");
            return "community/chatdogcreate";
        }
        UploadDto uploadDto = new UploadDto();
        uploadDto.setForeignNo(chatDogService.insert(chatDogDTO));

        uploadDto.setCategoryCode(IMAGE_CODE); // 이미지 카테고리 코드
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                uploadDto.setFile(imageFiles.get(i));
                uploadService.uploadInsert(uploadDto);
            }
        }

        return "redirect:/community/chatdog";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long chatdogNo, Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        String userId = principal.getName();


        ChatDogDTO chatDog = chatDogService.findById(chatdogNo);

        chatDogService.delete(chatdogNo);

        replyService.replyDeleteAll("C", Math.toIntExact(chatdogNo));

        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_CODE, chatdogNo);

        if (uploadDtos != null && !uploadDtos.isEmpty()) {
            for (UploadDto uploadDto : uploadDtos) {
                uploadService.uploadDelete(uploadDto.getImageNo());
            }
        }
        return "redirect:/community/chatdog";
    }

    @GetMapping("/update/{chatdogNo}")
    public String updateForm(@PathVariable Long chatdogNo,
                             Model model, Principal principal ) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        String userId = principal.getName();

        ChatDogDTO chatdog = chatDogService.findById(chatdogNo);
        if (chatdog == null || !chatdog.getUserId().equals(userId)) {
            return "redirect:/community/chatdog";
        }

        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_CODE, chatdogNo);

        model.addAttribute("chatdog", chatdog);
        model.addAttribute("uploadDtos", uploadDtos);



        return "community/chatdogupdate";
    }

    @PostMapping("/update/{chatdogNo}")
    public String update(@PathVariable  Long chatdogNo,
                         @ModelAttribute ChatDogDTO chatDogDTO,
                         @RequestParam(value = "imageFiles") @Nullable List<MultipartFile> imageFiles,
                         @RequestParam(value = "imageUpdateFiles") @Nullable List<MultipartFile> imageUpdateFiles,
                         @RequestParam(value = "imageDeleteImageNo") @Nullable List<Long> imageDeleteImageNo,
                         @RequestParam(value = "imageUpdateImageNo") @Nullable List<Long> imageUpdateImageNo,
                         Principal principal, Model model) {

        String userId = principal.getName();

        // 기존 게시글 조회
        ChatDogDTO chatdog = chatDogService.findById(chatdogNo);
        if (chatdog == null || !chatdog.getUserId().equals(userId)) {
            return "redirect:/community/chatdog";
        }
        //빈 문자열일 때 수정 x
        if (chatDogDTO.getTitle() == null || chatDogDTO.getTitle().trim().isEmpty() ||
                chatDogDTO.getContent() == null || chatDogDTO.getContent().trim().isEmpty()) {
            model.addAttribute("errorMessage");
            return "community/chatdogcupdate";
        }

        int result = chatDogService.update(chatDogDTO);

        if (result == 1) {
            // 이미지 삭제
            if (imageDeleteImageNo != null && !imageDeleteImageNo.isEmpty()) {
                for (Long imageNo : imageDeleteImageNo) {
                    uploadService.uploadDelete(imageNo);
                }
            }

            // 이미지 업데이트
            if (imageUpdateFiles != null && !imageUpdateFiles.isEmpty() && imageUpdateImageNo != null) {
                for (int i = 0; i < imageUpdateFiles.size(); i++) {
                    if (i < imageUpdateImageNo.size()) {
                        Long imageNo = imageUpdateImageNo.get(i);
                        MultipartFile file = imageUpdateFiles.get(i);
                        uploadService.uploadUpdate(imageNo, file);
                    }
                }
            }

            // 새 이미지 추가
            if (imageFiles != null && !imageFiles.isEmpty()) {
                UploadDto uploadDto = new UploadDto();
                uploadDto.setForeignNo(chatDogDTO.getChatdogNo());
                uploadDto.setCategoryCode(IMAGE_CODE);

                for (MultipartFile file : imageFiles) {
                    uploadDto.setFile(file);
                    uploadService.uploadInsert(uploadDto);
                }
            }
        }

        return "redirect:/community/chatdog";
    }

    @PostMapping("/chatdogrecommend")
    public String chatdogrecommend(@RequestParam Long chatdogNo,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        String userId = principal.getName();
        chatDogService.cdcommendCount(chatdogNo, userId);
        redirectAttributes.addAttribute("chatdogNo", chatdogNo);
        return "redirect:/community/chatdog/{chatdogNo}";
    }

}
