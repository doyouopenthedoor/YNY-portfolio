package main.java.com.YNY.Happytails.community.controller;

import com.multi.happytails.community.model.dto.DogloveDTO;
import com.multi.happytails.community.reply.model.dto.ReplyDTO;
import com.multi.happytails.community.reply.service.ReplyService;
import com.multi.happytails.community.service.DogloveService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.multi.happytails.community.controller
 * fileName       : test
 * author         : User
 * date           : 2024-07-24
 * description    : The type Doglove controller.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        User       최초 생성
 */

@Controller
@RequestMapping("/community/doglove")
public class DogloveController {

    @Autowired
    private DogloveService dogloveService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ReplyService replyService;

    /**
     * 댓글코드(내 새꾸 자랑)
     */
    final String replyCategoryCode = "L";


    /**
     * 이미지코드(내 새꾸 자랑)
     */
    final String IMAGE_CODE = "L";

    /**
     * 게시판 카테고리 코드(내 새꾸 자랑)
     */
    final String categoryCode = "DOGLOVE_CODE";

    /**
     * methodName : doglovelist
     * author : Nayoung Yeo
     * description : 게시판 목록을 보여주며 (작성자, 제목, 내용, 날짜, 추천순) 추천순과 최신순 정렬
     *
     * @param sort  정렬 기준을 나타냄 'recommendCount'인 경우 추천 수 기준으로 정렬하고 아닐 경우 최신순 정렬
     * @param model
     * @return 게시판 목록을 보여주는 뷰 이름 반환
     */

    @GetMapping
    public String dogloveList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        Page<DogloveDTO> doglovePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            doglovePage = dogloveService.dlsearch(keyword.trim(), page - 1, size, sort);
        } else {
            // 검색어가 없는 경우
            if ("recommendCount".equals(sort)) {
                doglovePage = dogloveService.findAllSortedByRecommendation(page - 1, size);
            } else {
                doglovePage = dogloveService.findAllSortedByDate(page - 1, size);
            }
        }

        Map<Long, List<UploadDto>> dogloveImages = new HashMap<>();
        for (DogloveDTO doglove : doglovePage.getContent()) {
            List<UploadDto> imageFiles = uploadService.uploadSelect(IMAGE_CODE, doglove.getDogloveNo());
            dogloveImages.put(doglove.getDogloveNo(), imageFiles);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("dogloves", doglovePage.getContent());
        model.addAttribute("dogloveImages", dogloveImages);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", doglovePage.getNumber() + 1);
        model.addAttribute("totalPages", doglovePage.getTotalPages());
        model.addAttribute("totalItems", doglovePage.getTotalElements());

        return "community/doglovelist";
    }

    /**
     * methodName : dogloveDetail
     * author : Nayoung Yeo
     * description : 상세 페이지를 보여줌 작성자, 이미지, 제목, 내용, 댓글, 추천순을 보여줌
     * 댓글 작성 가능(dogloveNo을 가져오고 커뮤니티 카테고리 코드를 하드코딩하여 가져옴
     *
     * @param dogloveNo 상세 페이지의 게시글 No
     * @param model
     * @return 게시글을 찾을 수 없으면 게시판 목록 페이지로 redirect
     */

    @GetMapping("/{dogloveNo}")
    public String dogloveDetail(@PathVariable("dogloveNo") Long dogloveNo,
                                Model model) {

        DogloveDTO doglove = dogloveService.findById(dogloveNo);
        //댓글 조회
        List<ReplyDTO> reply = replyService.getReplyByForeignNo(replyCategoryCode, Math.toIntExact(dogloveNo));

        List<UploadDto> uploadDtoList = uploadService.uploadSelect(IMAGE_CODE, dogloveNo);
        UploadDto uploadDto = UploadDto.builder().categoryCode(categoryCode).foreignNo(dogloveNo).build();


        // 조회된 데이터가 있을 경우
        if (doglove != null) {
            model.addAttribute("doglove", doglove);
            model.addAttribute("reply", reply);
            model.addAttribute("imageFiles", uploadDtoList);
            model.addAttribute("uploadDto", uploadDto);
            return "community/dogloveDetail";
        } else {
            return "redirect:/community/doglove";
        }

    }

    /*
     * methodName : dogloveCreate
     * author : Nayoung Yeo
     * description : 글 생성 페이지를 보여줌
     */
    @GetMapping("/create")
    public String dogloveCreate(Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        return "community/doglovecreate";
    }

    /**
     * methodName : save
     * author : Nayoung Yeo
     * description : 게시글 저장 (제목, 내용, 이미지 업로드)
     *
     * @param imageFiles 업로드 파일 목록 / 여러 개 선택 가능
     * @param dogloveDTO 게시글 정보
     * @param principal  현재 로그인한 사용자 정보
     */
    @PostMapping
    public String create(@ModelAttribute DogloveDTO dogloveDTO,
                         @RequestParam("imageFiles") @Nullable List<MultipartFile> imageFiles,
                         Principal principal) {

        String userId = principal.getName();
        dogloveDTO.setUserId(userId);

        //게시판 카테고리 코드
        dogloveDTO.setCategoryCode(categoryCode);
        dogloveDTO.setCreateTime(LocalDateTime.now());

        UploadDto uploadDto = new UploadDto();
        uploadDto.setForeignNo(dogloveService.dogloveInsert(dogloveDTO));

        uploadDto.setCategoryCode(IMAGE_CODE); // 이미지 카테고리 코드
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                uploadDto.setFile(imageFiles.get(i));
                uploadService.uploadInsert(uploadDto);
            }
        }
        return "redirect:/community/doglove";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long dogloveNo, Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        String userId = principal.getName();

        DogloveDTO doglove = dogloveService.findById(dogloveNo);

        dogloveService.delete(dogloveNo);

        replyService.replyDeleteAll("L", Math.toIntExact(dogloveNo));

        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_CODE, dogloveNo);

        if (uploadDtos != null && !uploadDtos.isEmpty()) {
            for (UploadDto uploadDto : uploadDtos) {
                uploadService.uploadDelete(uploadDto.getImageNo());
            }
        }
        return "redirect:/community/doglove";
    }

    @GetMapping("/update/{dogloveNo}")
    public String updateForm(@PathVariable Long dogloveNo, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        String userId = principal.getName();

        DogloveDTO doglove = dogloveService.findById(dogloveNo);
        if (doglove == null || !doglove.getUserId().equals(userId)) {
            return "redirect:/community/doglove";
        }

        List<UploadDto> uploadDtos = uploadService.uploadSelect(IMAGE_CODE, dogloveNo);

        model.addAttribute("doglove", doglove);
        model.addAttribute("uploadDtos", uploadDtos);

        return "community/dogloveupdate";
    }

    @PostMapping("/update/{dogloveNo}")
    public String update(@PathVariable Long dogloveNo,
                         @ModelAttribute DogloveDTO dogloveDTO,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam(value = "imageFiles") @Nullable List<MultipartFile> imageFiles,
                         @RequestParam(value = "imageUpdateFiles") @Nullable List<MultipartFile> imageUpdateFiles,
                         @RequestParam(value = "imageDeleteImageNo") @Nullable List<Long> imageDeleteImageNo,
                         @RequestParam(value = "imageUpdateImageNo") @Nullable List<Long> imageUpdateImageNo,
                         Principal principal,Model model) {

        String userId = principal.getName();

        // 기존 게시글 조회
        DogloveDTO doglove = dogloveService.findById(dogloveNo);
        if (doglove == null || !doglove.getUserId().equals(userId)) {
            return "redirect:/community/doglove";
        }

        //빈 문자열일 때 수정 x
        if (dogloveDTO.getTitle() == null || dogloveDTO.getTitle().trim().isEmpty() ||
                dogloveDTO.getContent() == null || dogloveDTO.getContent().trim().isEmpty()) {
            model.addAttribute("errorMessage");
            return "community/chatdogcupdate";
        }

        int result = dogloveService.update(dogloveDTO);

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
                uploadDto.setForeignNo(dogloveDTO.getDogloveNo());
                uploadDto.setCategoryCode(IMAGE_CODE);

                for (MultipartFile file : imageFiles) {
                    uploadDto.setFile(file);
                    uploadService.uploadInsert(uploadDto);
                }
            }
        }

        return "redirect:/community/doglove";
    }

    /**
     * /**
     * methodName : dogloveRecommend
     * author : Nayoung Yeo
     * description : 추천수 증가
     *
     * @param dogloveNo
     * @return string
     */
    @PostMapping("/dogloveRecommend")
    public String dogloveRecommend(@RequestParam Long dogloveNo,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/member/login";
        }
        String userId = principal.getName();
        dogloveService.dgRecommendCount(dogloveNo, userId);
        redirectAttributes.addAttribute("dogloveNo", dogloveNo);
        return "redirect:/community/doglove/{dogloveNo}";
    }

}

