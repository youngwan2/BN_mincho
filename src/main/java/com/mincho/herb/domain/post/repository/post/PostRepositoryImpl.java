package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository{
    private final PostJpaRepository postJpaRepository;

    @Override
    public void save(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
    }

    @Override
    public Object[][] findByPostId(Long postId) {
        return postJpaRepository.findByPostId(postId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 게시글은 존재하지 않습니다."));
    }

    @Override
    public PostEntity findById(Long postId) {
        return postJpaRepository.findById(postId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 게시글은 존재하지 않습니다."));
    }

    @Override
    public List<Object[]> findAllByCategoryWithLikeCount(String category, Pageable pageable) {
        return postJpaRepository.findAllByCategoryWithLikeCount(category, pageable).stream().toList() ;
    }

    // 해당 포스트를 작성한 유저 조회
    @Override
    public Long findAuthorIdByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorIdByPostIdAndEmail(postId, email)
                .orElseThrow(()-> new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 있는 유저가 아닙니다."));
    }

    @Override
    public MemberEntity findAuthorByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorByPostIdAndEmail(postId, email)
                .orElseThrow(()-> new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 있는 유저가 아닙니다."));
    }

    @Override
    public void update(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
    }

    @Override
    public void deleteById(Long id) {
        postJpaRepository.deleteById(id);
    }

    // 카테고리 별 게시글 개수
    @Override
    public int countByCategory(String category) {
        return 0;
    }

    // 사용자 별 게시글 수
    @Override
    public int countByMemberId(Long memberId) {
        int count = postJpaRepository.countByMemberId(memberId);
        return count;
    }

    // 카테고리 별 게시글 수 통계
    @Override
    public List<PostCountDTO> countsByCategory() {
        return postJpaRepository.countPostsByCategory();
    }
}
