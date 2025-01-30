package com.mincho.herb.domain.favorite.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.ValidationUtils;
import com.mincho.herb.domain.favorite.application.FavoriteHerbService;
import com.mincho.herb.domain.favorite.dto.RequestFavoriteHerb;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/favorite-herbs")
@RequiredArgsConstructor
public class FavoriteHerbController {
    private final ValidationUtils validationUtils;
    private final FavoriteHerbService favoriteHerbService;

    @PostMapping()
    public ResponseEntity<?> addFavoriteHerb(@RequestParam("herbName") String herbName, @RequestBody RequestFavoriteHerb requestFavoriteHerb, BindingResult result){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email.isEmpty()){
            return new ErrorResponse().getResponse(401, "요청 권한이 없습니다.", HttpErrorType.UNAUTHORIZED);
        }
        if(herbName.isEmpty()){
            return new ErrorResponse().getResponse(400, "herbName 은 필수입니다.", HttpErrorType.BAD_REQUEST);
        }

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        favoriteHerbService.addFavoriteHerb( requestFavoriteHerb.getUrl(), email, herbName) ;
        return new SuccessResponse<>().getResponse(201, "성공적으로 등록되었습니다.", HttpSuccessType.CREATED);

    }
}
