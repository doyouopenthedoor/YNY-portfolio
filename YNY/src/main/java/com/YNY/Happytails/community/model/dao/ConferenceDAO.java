package main.java.com.YNY.Happytails.community.model.dao;

import com.multi.happytails.community.model.dto.ConferenceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConferenceDAO {


    ConferenceDTO findById(long ConferenceNo);

    void delete(long ConferenceNo);

    void insert(ConferenceDTO ConferenceDTO);

    public int getCurrenConferenceNo();

    void cfcommendCount(long ConferenceNo);

    int update(ConferenceDTO conference);

    public List<ConferenceDTO> cfsearch(@Param("keyword") String keyword, @Param("sort") String sort, @Param("offset") int offset, @Param("limit") int limit);

    long countConferenceSearch(String keyword);

    List<ConferenceDTO> findAll(String sortOrder, int offset, int limit);


    long countAll();


}
