package main.java.com.YNY.Happytails.community.model.dao;

import com.multi.happytails.community.model.dto.ChatDogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatDogDAO {

    ChatDogDTO findById(long chatdogNo);

    void delete(long chatdogNo);

    void insert(ChatDogDTO chatdogDTO);

    public int getCurrentChatDogNo();

    void cdcommendCount(long chatdogNo);
    
    int update(ChatDogDTO chatdog);

    //검색
    public List<ChatDogDTO> cdsearch(@Param("keyword") String keyword, @Param("sort") String sort, @Param("offset") int offset, @Param("limit") int limit);

    //페이징
    List<ChatDogDTO> findAll(String sortOrder, int offset, int limit);
    
    long countAll();

    long countChatdogSearch(String keyword);


    List<ChatDogDTO> mainChatDogList();

}
