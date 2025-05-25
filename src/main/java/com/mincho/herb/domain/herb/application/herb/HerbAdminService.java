package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HerbAdminService {

    void createHerb(HerbCreateRequestDTO herbCreateRequestDTO, List<MultipartFile> imageFiles);
    void removeHerb(Long id);
    void updateHerb(HerbUpdateRequestDTO herbUpdateRequestDTO, List<MultipartFile> imageFiles, Long herbId);
    void insertMany() throws IOException;




}
