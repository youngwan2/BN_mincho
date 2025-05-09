package com.mincho.herb.domain.admin.api;

import com.mincho.herb.domain.admin.application.notice.NoticeService;
import com.mincho.herb.domain.admin.dto.notice.NoticeRequestDTO;
import com.mincho.herb.domain.admin.dto.notice.NoticeResponseDTO;
import com.mincho.herb.domain.admin.dto.notice.NoticeSearchConditionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid NoticeRequestDTO dto) {
        noticeService.create(dto);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid NoticeRequestDTO dto) {
        noticeService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<NoticeResponseDTO> search(NoticeSearchConditionDTO condition, Pageable pageable) {
        return ResponseEntity.ok(noticeService.search(condition, pageable));
    }
}
