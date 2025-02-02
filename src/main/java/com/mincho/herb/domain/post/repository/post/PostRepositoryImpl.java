package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository{
    private final PostJpaRepository postJpaRepository;

    @Override
    public void save(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
    }

    @Override
    public PostEntity findById(Long postId) {
        return postJpaRepository.findById(postId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 게시글은 존재하지 않습니다."));
    }

    @Override
    public List<PostEntity> findAllByCategory(String category, Pageable pageable) {
        return postJpaRepository.findAllByCategory(category, pageable).stream().toList();
    }

    // 해당 포스트를 작성한 유저 조회
    @Override
    public Long findAuthorIdByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorIdByPostIdAndEmail(postId, email)
                .orElseThrow(()-> new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 있는 유저가 아닙니다."));
    }

    @Override
    public void deleteById(Long id) {
        postJpaRepository.deleteById(id);
    }
}
