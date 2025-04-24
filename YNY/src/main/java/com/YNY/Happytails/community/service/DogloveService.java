package main.java.com.YNY.Happytails.community.service;

import com.multi.happytails.community.model.dao.DogloveDAO;
import com.multi.happytails.community.model.dto.DogloveDTO;
import com.multi.happytails.community.reply.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogloveService {

    private static final String CATEGORY_CODE = "DOGLOVE_CODE";

    @Autowired
    private ReplyService replyService;


    @Autowired
    private DogloveDAO dogloveDAO;

    public DogloveService(DogloveDAO dogloveDAO) {
        this.dogloveDAO = dogloveDAO;
    }

    public Page<DogloveDTO> findAllSortedByDate(int page, int size) {
        List<DogloveDTO> doglove = dogloveDAO.findAll("date", page * size, size);
        long total = dogloveDAO.countAll();
        return new PageImpl<>(doglove, PageRequest.of(page, size), total);
    }

    public Page<DogloveDTO> findAllSortedByRecommendation(int page, int size) {
        List<DogloveDTO> doglove = dogloveDAO.findAll("recommendCount", page * size, size);
        long total = dogloveDAO.countAll();
        return new PageImpl<>(doglove, PageRequest.of(page, size), total);
    }

    public DogloveDTO findById(long dogloveNo) {
        return dogloveDAO.findById(dogloveNo);
    }

    public long dogloveInsert(DogloveDTO dogloveDTO) {
        dogloveDTO.setCategoryCode(CATEGORY_CODE);
        dogloveDAO.dogloveInsert(dogloveDTO);
        return dogloveDAO.getCurrentDogloveNo();
    }

    public void dgRecommendCount(long dogloveNo, String userId) {
        dogloveDAO.dgRecommendCount(dogloveNo);
    }

    public void delete(long dogloveNo) {
        dogloveDAO.delete(dogloveNo);
    }

    public Page<DogloveDTO> dlsearch(String keyword, int page, int size, String sort) {
        List<DogloveDTO> searchResults = dogloveDAO.dlsearch(keyword, sort, page * size, size);
        long total = dogloveDAO.countDogloveSearch(keyword);
        return new PageImpl<>(searchResults, PageRequest.of(page, size), total);
    }

    public int update(DogloveDTO dogloveDTO) {
        return dogloveDAO.update(dogloveDTO);
    }

    public List<DogloveDTO> mainDogLoveList() {
        return dogloveDAO.mainDogLoveList();
    }

    public List<DogloveDTO> getSortedByRecommendation(String sort) {
        return dogloveDAO.getSortedByRecommendation(sort);
    }
}



