package main.java.com.YNY.Happytails.community.service;

import com.multi.happytails.community.model.dao.ConferenceDAO;
import com.multi.happytails.community.model.dto.ConferenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConferenceService {

    private static final String CATEGORY_CODE = "CONFERENCE_CODE";

    @Autowired
    private ConferenceDAO conferenceDAO;

    public ConferenceService(ConferenceDAO conferenceDAO) {
        this.conferenceDAO = conferenceDAO;
    }

    public Page<ConferenceDTO> findAllSortedByDate(int page, int size) {
        List<ConferenceDTO> conferences = conferenceDAO.findAll("date", page * size, size);
        long total = conferenceDAO.countAll();
        return new PageImpl<>(conferences, PageRequest.of(page, size), total);
    }

    public Page<ConferenceDTO> findAllSortedByRecommendation(int page, int size) {
        List<ConferenceDTO> conferences = conferenceDAO.findAll("recommendCount", page * size, size);
        long total = conferenceDAO.countAll();
        return new PageImpl<>(conferences, PageRequest.of(page, size), total);
    }

    public ConferenceDTO findById(long conferenceNo) {
        return conferenceDAO.findById(conferenceNo);
    }

    public long insert(ConferenceDTO conferenceDTO) {
        conferenceDTO.setCategoryCode(CATEGORY_CODE);
        conferenceDAO.insert(conferenceDTO);
        return conferenceDAO.getCurrenConferenceNo();
    }

    public void cfcommendCount(long conferenceNo, String userId) {
        conferenceDAO.cfcommendCount(conferenceNo);
    }

    public void delete(long conferenceNo) {
        conferenceDAO.delete(conferenceNo);
    }

    public int update(ConferenceDTO conferenceDTO) {
        return conferenceDAO.update(conferenceDTO);

    }

    public Page<ConferenceDTO> cfsearch(String keyword, int page, int size, String sort) {
        List<ConferenceDTO> searchResults = conferenceDAO.cfsearch(keyword, sort, page * size, size);
        long total = conferenceDAO.countConferenceSearch(keyword);
        return new PageImpl<>(searchResults, PageRequest.of(page, size), total);
    }

}
