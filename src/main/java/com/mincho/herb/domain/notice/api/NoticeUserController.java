package com.mincho.herb.domain.notice.api;

import com.mincho.herb.domain.notice.application.NoticeUserService;
import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeUserController {

    private final NoticeUserService noticeUserService;


    @GetMapping
    public ResponseEntity<NoticeResponseDTO> search(NoticeSearchConditionDTO condition, Pageable pageable) {
        return ResponseEntity.ok(noticeUserService.search(condition, pageable));
    }
}
