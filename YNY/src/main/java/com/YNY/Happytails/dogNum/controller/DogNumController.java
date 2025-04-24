package main.java.com.YNY.Happytails.dogNum.controller;

import com.multi.happytails.dogNum.dto.DogNumDTO;
import com.multi.happytails.dogNum.service.DogNumService;
import com.multi.happytails.upload.model.dto.UploadDto;
import com.multi.happytails.upload.service.UploadService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/dogNum")
public class DogNumController {

    private final DogNumService dogNumService;
    private final UploadService uploadService;
    private final String IMAGE_CODE = "N";
    private final String SERVICE_KEY = "WO6eYkS2IohC2ttnnnk4oJVRUweEG%2F9Pvi9HokXptwYM1PokLcexnxHVoThQZk%2FAl2mAfd1WltIinmxbGpmodQ%3D%3D";
    private final String API_URL = "http://apis.data.go.kr/1543061/animalInfoSrvc/animalInfo";

    @Autowired
    public DogNumController(DogNumService dogNumService, UploadService uploadService) {
        this.dogNumService = dogNumService;
        this.uploadService = uploadService;
    }

    @GetMapping("/dogNumForm")
    public String showForm(Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }

        return "dogNum/dogNumForm";
    }

    @GetMapping("/lookup")
    @ResponseBody
    public ResponseEntity<String> lookupEndpoint(@RequestParam String dog_reg_no, @RequestParam String owner_nm) {
        String result = lookUp(dog_reg_no, owner_nm);
        return ResponseEntity.ok(result);
    }

    private String lookUp(String dog_reg_no, String owner_nm) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?serviceKey=%s&dog_reg_no=%s&owner_nm=%s", API_URL, SERVICE_KEY, dog_reg_no, owner_nm);
        return restTemplate.getForObject(url, String.class);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveDogNum(@RequestParam String dogRegNo,
                                             @RequestParam String dogNm,
                                             @RequestParam String sexNm,
                                             @RequestParam String kindNm,
                                             @RequestParam String neuterYn,
                                             @RequestParam("imageFiles") @Nullable List<MultipartFile> imageFiles,
                                             RedirectAttributes redirectAttributes) {
        // 등록번호 중복 체크
        if (dogNumService.isDogRegNoExists(dogRegNo)) {
            return ResponseEntity.badRequest().body("동물 등록 번호가 이미 존재합니다.");
        }

        DogNumDTO dogNumDTO = new DogNumDTO();
        dogNumDTO.setDogregno(dogRegNo);
        dogNumDTO.setDognm(dogNm);
        dogNumDTO.setSexnm(sexNm);
        dogNumDTO.setKindnm(kindNm);
        dogNumDTO.setNeuteryn(neuterYn);

        Long id = dogNumService.insert(dogNumDTO);

        UploadDto uploadDto = new UploadDto();
        uploadDto.setForeignNo(id);
        uploadDto.setCategoryCode(IMAGE_CODE);
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                uploadDto.setFile(imageFiles.get(i));
                uploadService.uploadInsert(uploadDto);
            }
        }
        String message = "댕댕이 등록증 정보가 저장 되었습니다.";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/dogNumSave")
    public String dogCertificate(
            @RequestParam("dogRegNo") String dogRegNo,
            Model model) {
        // 강아지 정보를 조회
        DogNumDTO dogInfo = dogNumService.getDogInfoByDogregno(dogRegNo);

        List<UploadDto> uploadDtoList = uploadService.uploadSelect(IMAGE_CODE, Long.parseLong(dogRegNo));

        if (dogInfo != null) {
            // 모델에 강아지 정보 추가
            model.addAttribute("dogRegNo", dogInfo.getDogregno());
            model.addAttribute("dogNm", dogInfo.getDognm());
            model.addAttribute("sexNm", dogInfo.getSexnm());
            model.addAttribute("kindNm", dogInfo.getKindnm());
            model.addAttribute("neuterYn", dogInfo.getNeuteryn());
            model.addAttribute("uploadDtoList", uploadDtoList);

        } else {
            // 강아지 정보가 없는 경우 에러 처리
            model.addAttribute("errorMessage", "강아지 정보를 찾을 수 없습니다.");
        }

        return "dogNum/dogNumSave";
    }

}
