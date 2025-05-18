package com.mincho.herb.domain.notice.api;

import com.mincho.herb.domain.notice.application.NoticeAdminService;
import com.mincho.herb.domain.notice.dto.NoticeDTO;
import com.mincho.herb.domain.notice.dto.NoticeRequestDTO;
import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeAdminService noticeAdminService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid NoticeRequestDTO dto) {
        noticeAdminService.create(dto);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid NoticeRequestDTO dto) {
        noticeAdminService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeAdminService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<NoticeResponseDTO> search(NoticeSearchConditionDTO condition, Pageable pageable) {
        return ResponseEntity.ok(noticeAdminService.search(condition, pageable));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDTO> getNotice(
            @PathVariable Long noticeId
    ){
        return ResponseEntity.ok(noticeAdminService.getNotice(noticeId));
    }
}
