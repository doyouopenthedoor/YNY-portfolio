package main.java.com.YNY.Happytails.community.service;

import com.multi.happytails.community.model.dao.ChatDogDAO;
import com.multi.happytails.community.model.dto.ChatDogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatDogService {

    private static final String CATEGORY_CODE = "CHATDOG_CODE";

    @Autowired
    private ChatDogDAO chatdogDAO;

    public ChatDogService(ChatDogDAO chatdogDAO) {
        this.chatdogDAO = chatdogDAO;
    }

    public Page<ChatDogDTO> findAllSortedByDate(int page, int size) {
        List<ChatDogDTO> chatdog = chatdogDAO.findAll("date", page * size, size);
        long total = chatdogDAO.countAll();
        return new PageImpl<>(chatdog, PageRequest.of(page, size), total);
    }

    public Page<ChatDogDTO> findAllSortedByRecommendation(int page, int size) {
        List<ChatDogDTO> chatdog = chatdogDAO.findAll("recommendCount", page * size, size);
        long total = chatdogDAO.countAll();
        return new PageImpl<>(chatdog, PageRequest.of(page, size), total);    }

    public ChatDogDTO findById(long chatdogNo) {
        return chatdogDAO.findById(chatdogNo);
    }

    public long insert(ChatDogDTO chatdogDTO) {
        chatdogDTO.setCategoryCode(CATEGORY_CODE);
        chatdogDAO.insert(chatdogDTO);
        return chatdogDAO.getCurrentChatDogNo();
    }

    public void cdcommendCount(long chatdogNo, String userId) {
        chatdogDAO.cdcommendCount(chatdogNo);
    }

    public void delete(long chatdogNo) {
        chatdogDAO.delete(chatdogNo);
    }

    public int update(ChatDogDTO chatDogDTO) {
        return chatdogDAO.update(chatDogDTO);

    }

    public Page<ChatDogDTO> cdsearch(String keyword, int page, int size, String sort) {
        List<ChatDogDTO> searchResults = chatdogDAO.cdsearch(keyword, sort, page * size, size);
        long total = chatdogDAO.countChatdogSearch(keyword);
        return new PageImpl<>(searchResults, PageRequest.of(page, size), total);
    }







    public List<ChatDogDTO> mainChatDogList() {
        return chatdogDAO.mainChatDogList();
    }


}