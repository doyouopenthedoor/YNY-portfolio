package main.java.com.YNY.Happytails.community.model.dao;

import com.multi.happytails.community.model.dto.DogloveDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DogloveDAO {

    DogloveDTO findById(long dogloveNo);

    void delete(long dogloveNo);

    void dogloveInsert(DogloveDTO dogloveDTO);

    public int getCurrentDogloveNo();

    int update(DogloveDTO doglove);

    void dgRecommendCount(long dogloveNo);

    List<DogloveDTO> findAll(String sortOrder, int offset, int limit);

    public List<DogloveDTO> dlsearch(@Param("keyword") String keyword, @Param("sort") String sort, @Param("offset") int offset, @Param("limit") int limit);

    long countDogloveSearch(String keyword);

    long countAll();

    List<DogloveDTO> mainDogLoveList();

    List<DogloveDTO> getSortedByRecommendation(@Param("sort") String sort);
}
