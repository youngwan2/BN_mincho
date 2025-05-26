package com.mincho.herb.domain.tag.api;

import com.mincho.herb.domain.tag.application.TagService;
import com.mincho.herb.domain.tag.dto.TagRequestDTO;
import com.mincho.herb.domain.tag.dto.TagDTO;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final AuthUtils authUtils;

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tagType) {

        TagTypeEnum tagTypeEnum = null;
        if (tagType != null) {
            try {
                tagTypeEnum = TagTypeEnum.valueOf(tagType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효한 태그 타입아님. 허용된 값의 유형: " + Arrays.toString(TagTypeEnum.values()));
            }
        }
        return ResponseEntity.ok(tagService.getAllTags(keyword, tagTypeEnum));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody TagRequestDTO tagRequestDTO) {
        String userEmail = authUtils.userCheck();
        if (userEmail == null) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "태그 생성 권한이 없습니다.");
        }
        return ResponseEntity.ok(tagService.createTag(tagRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(@PathVariable Long id, @RequestBody TagRequestDTO tagRequestDTO) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "태그 수정 권한이 없습니다.");
        }
        return ResponseEntity.ok(tagService.updateTag(id, tagRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "태그 삭제 권한이 없습니다.");
        }
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
