package com.mincho.herb.domain.favorite.application;


import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.favorite.domain.FavoriteHerb;
import com.mincho.herb.domain.favorite.entity.FavoriteHerbEntity;
import com.mincho.herb.domain.favorite.repository.FavoriteHerbRepository;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteHerbServiceImpl implements FavoriteHerbService {

    private final FavoriteHerbRepository favoriteHerbRepository;
    private final UserRepository userRepository;
    private final HerbRepository herbRepository;



    @Override
    public void addFavoriteHerb(String url, String email, String herbName) {

        MemberEntity memberEntity = userRepository.findByEmail(email);
        HerbEntity herbEntity = herbRepository.findByCntntsSj(herbName);

        if(memberEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "유저 정보를 찾을 수 없습니다.");
        }
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"약초 정보를 찾을 수 없습니다.");
        }

        if(!FavoriteHerb.isValidUrl(url)){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"유효한 url 형식이 아닙니다.");
        }

        FavoriteHerbEntity favoriteHerbEntity = FavoriteHerbEntity.builder()
                        .member(memberEntity)
                        .herb(herbEntity)
                        .url(url)
                        .build();
        favoriteHerbRepository.save(favoriteHerbEntity);

    }

    // 관심 약초 제거
    @Override
    @Transactional
    public void removeFavoriteHerb(Long favoriteHerbId, String email) {
        MemberEntity memberEntity = userRepository.findByEmail(email);

        if(memberEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        }

        favoriteHerbRepository.deleteMemberIdAndFavoriteHerbId(memberEntity.getId(), favoriteHerbId);
    }
}
