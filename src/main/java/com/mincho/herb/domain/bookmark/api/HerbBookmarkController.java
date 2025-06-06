package com.mincho.herb.domain.bookmark.api;

import com.mincho.herb.domain.bookmark.application.HerbBookmarkService;
import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkCountResponse;
import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkRequestDTO;
import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkResponseDTO;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Herb Bookmark", description = "관심 허브(북마크) 관련 API")
public class HerbBookmarkController {

    private final HerbBookmarkService herbBookmarkService;

    // 관심약초 추가
    @PostMapping("/users/me/herbs/{herbId}/herb-bookmarks")
    @Operation(summary = "관심 허브 추가", description = "사용자의 관심 허브(북마크)를 추가합니다.")
    public ResponseEntity<?> addHerbBookmark(
            @Parameter(description = "허브 ID", required = true) @PathVariable Long herbId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "관심 허브 요청 DTO", required = true)
            @Valid @RequestBody HerbBookmarkRequestDTO herbBookmarkRequestDTO) {

        herbBookmarkService.addHerbBookmark(herbBookmarkRequestDTO.getUrl(), herbId);
        return new SuccessResponse<>().getResponse(201, "성공적으로 등록되었습니다.", HttpSuccessType.CREATED);
    }

    // 관심약초 제거
    @DeleteMapping("/users/me/herbs/{herbId}/herb-bookmarks")
    @Operation(summary = "관심 허브 제거", description = "사용자의 관심 허브(북마크)를 제거합니다.")
    public ResponseEntity<Map<String, String>> removeFavoriteHerb(
            @Parameter(description = "허브 ID", required = true) @PathVariable("herbId") Long herbId) {
        herbBookmarkService.removeHerbBookmark(herbId);
        return new SuccessResponse<>().getResponse(200, "성공적으로 제거 되었습니다.", HttpSuccessType.OK);
    }

    // 관심약초 전체 개수(약초 별)
    @GetMapping("/herbs/{herbId}/herb-bookmarks/count")
    @Operation(summary = "관심 허브 개수 조회", description = "특정 허브의 관심 허브(북마크) 개수를 조회합니다.")
    public ResponseEntity<?> getHerbBookmarkCount(
            @Parameter(description = "허브 ID", required = true) @PathVariable("herbId") Long herbId) {
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
    @Operation(summary = "사용자 관심 허브 조회", description = "사용자의 관심 허브(북마크) 목록을 조회합니다.")
    public ResponseEntity<HerbBookmarkResponseDTO> getBookmarks(
            @Parameter(description = "페이지 번호", required = true) @RequestParam int page,
            @Parameter(description = "페이지 크기", required = true) @RequestParam int size) {

        HerbBookmarkResponseDTO herbBookmarkResponseDTO = herbBookmarkService.getBookmarks(page, size);
        return ResponseEntity.ok(herbBookmarkResponseDTO);
    }

    // 관심약초 조회(사용자 ID 별)
    @GetMapping("/users/{userId}/herbs/herb-bookmarks")
    @Operation(summary = "사용자 ID로 관심 허브 조회", description = "특정 사용자의 관심 허브(북마크) 목록을 조회합니다.")
    public ResponseEntity<HerbBookmarkResponseDTO> getBookmarksByUserId(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            Pageable pageable) {

        HerbBookmarkResponseDTO herbBookmarkResponseDTO = herbBookmarkService.getBookmarksByUserId(userId,pageable);
        return ResponseEntity.ok(herbBookmarkResponseDTO);
    }
}
