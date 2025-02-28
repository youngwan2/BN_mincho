package com.mincho.herb.domain.bookmark.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.bookmark.application.HerbBookmarkService;
import com.mincho.herb.domain.bookmark.dto.RequestHerbBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HerbBookmarkController {
    private final CommonUtils commonUtils;
    private final HerbBookmarkService herbBookmarkService;

    // 허브 북마크 추가
    @PostMapping("/users/me/herbs/{herbId}/herb-bookmarks")
    public ResponseEntity<?> addHerbBookmark(
            @PathVariable Long herbId,
            @RequestBody RequestHerbBookmark requestHerbBookmark,
            BindingResult result){


        if(herbId == null){
            return new ErrorResponse().getResponse(400, "herbId 은 필수입니다.", HttpErrorType.BAD_REQUEST);
        }

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        herbBookmarkService.addHerbBookmark(requestHerbBookmark.getUrl(), herbId ) ;
        return new SuccessResponse<>().getResponse(201, "성공적으로 등록되었습니다.", HttpSuccessType.CREATED);
    }

    @DeleteMapping("/users/me/herbs/{herbId}/herb-bookmarks/{bookmarkId}")
    public ResponseEntity<Map<String, String>> removeFavoriteHerb(@PathVariable("bookmarkId") Long bookmarkId){
        if(bookmarkId == null){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 요청 형식을 확인해주세요 ",HttpErrorType.BAD_REQUEST);
        }
        herbBookmarkService.removeHerbBookmark(bookmarkId);

        return new SuccessResponse<>().getResponse(200, "성공적으로 제거 되었습니다.",HttpSuccessType.OK);
    }

    // 허브 북마크 전체 개수
    @GetMapping("/herbs/{herbId}/herb-bookmarks/count")
    public ResponseEntity<?> getHerbBookmarkCount(@PathVariable("herbId") Long herbId){
        if(herbId == null){
            return new ErrorResponse().getResponse(400, "herbId는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }
       Integer bookmarkCount = herbBookmarkService.getBookmarkCount(herbId);
       Map<String, Integer> map = new HashMap<>();
       map.put("count", bookmarkCount);

       return new SuccessResponse<>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, map);
    }
}