package main.java.com.YNY.Happytails.dogNum.service;

import com.multi.happytails.community.model.dto.DogloveDTO;
import com.multi.happytails.dogNum.dao.DogNumDAO;
import com.multi.happytails.dogNum.dto.DogNumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.multi.happytails.dogNum.service
 * fileName       : DogNumService
 * author         : User
 * date           : 2024-08-05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-05        User       최초 생성
 */
@Service
public class DogNumService {
    private final DogNumDAO dogNumDAO;

    @Autowired
    public DogNumService(DogNumDAO dogNumDAO) {
        this.dogNumDAO = dogNumDAO;
    }

    public long insert(DogNumDTO dogNumDTO) {
        dogNumDAO.insert(dogNumDTO);
        return dogNumDAO.getCurrentDogNumNo();
    }

    public DogNumDTO getDogInfoByDogregno(String dogRegNo) {
        return dogNumDAO.getDogInfoByDognum(dogRegNo);
    }

    public DogloveDTO findById(Long dogRegNo) {
        return dogNumDAO.findById(dogRegNo);
    }


    public boolean isDogRegNoExists(String dogRegNo) {
        return dogNumDAO.isDogRegNoExists(dogRegNo);

    }
}
