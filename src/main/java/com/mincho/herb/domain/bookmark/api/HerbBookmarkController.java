package com.mincho.herb.domain.bookmark.api;

import com.mincho.herb.domain.bookmark.application.HerbBookmarkService;
import com.mincho.herb.domain.bookmark.dto.HerbBookmarkCountResponse;
import com.mincho.herb.domain.bookmark.dto.HerbBookmarkRequestDTO;
import com.mincho.herb.domain.bookmark.dto.HerbBookmarkResponseDTO;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HerbBookmarkController {

    private final HerbBookmarkService herbBookmarkService;

    // 관심약초 추가
    @PostMapping("/users/me/herbs/{herbId}/herb-bookmarks")
    public ResponseEntity<?> addHerbBookmark(
            @PathVariable Long herbId,
            @Valid @RequestBody HerbBookmarkRequestDTO herbBookmarkRequestDTO) {

        herbBookmarkService.addHerbBookmark(herbBookmarkRequestDTO.getUrl(), herbId);
        return new SuccessResponse<>().getResponse(201, "성공적으로 등록되었습니다.", HttpSuccessType.CREATED);
    }

    // 관심약초 제거
    @DeleteMapping("/users/me/herbs/{herbId}/herb-bookmarks")
    public ResponseEntity<Map<String, String>> removeFavoriteHerb(@PathVariable("herbId") Long herbId) {
        herbBookmarkService.removeHerbBookmark(herbId);
        return new SuccessResponse<>().getResponse(200, "성공적으로 제거 되었습니다.", HttpSuccessType.OK);
    }

    // 관심약초 전체 개수(약초 별)
    @GetMapping("/herbs/{herbId}/herb-bookmarks/count")
    public ResponseEntity<?> getHerbBookmarkCount(@PathVariable("herbId") Long herbId) {
        Boolean isBookmarked = herbBookmarkService.isBookmarked(herbId);
        Long bookmarkCount = herbBookmarkService.getBookmarkCount(herbId);

        HerbBookmarkCountResponse bookmarkCountResponse = HerbBookmarkCountResponse.builder()
                .count(bookmarkCount)
                .isBookmarked(isBookmarked)
                .build();

        return new SuccessResponse<>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, bookmarkCountResponse);
    }

    // 관심약초 조회(사용자 별)
    @GetMapping("/users/me/herbs/herb-bookmarks")
    public ResponseEntity<HerbBookmarkResponseDTO> getBookmarks(
            @RequestParam int page,
            @RequestParam int size) {

        HerbBookmarkResponseDTO herbBookmarkResponseDTO = herbBookmarkService.getBookmarks(page, size);
        return ResponseEntity.ok(herbBookmarkResponseDTO);
    }
}
