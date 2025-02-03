package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.domain.Author;
import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.post.domain.PostCategory;
import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.dto.ResponseDetailPostDTO;
import com.mincho.herb.domain.post.dto.ResponsePostDTO;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.post.repository.postLike.PostLikeRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    // 카테고리 별 게시글 조회
    @Override
    public List<ResponsePostDTO> getPostsByCategory(int page, int size, String category) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        List<Object[]> postEntities = postRepository.findAllByCategoryWithLikeCount(category, pageable);

        if(postEntities.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 목록이 존재하지 않습니다.");
        }

        return postEntities.stream().map((row)-> {
            PostEntity postEntity = (PostEntity) row[0]; // 게시글 정보
            Long likeCount = (Long) row[1]; // 좋아요
            Author author = Author.builder()
                    .id(postEntity.getMember().getId())
                    .nickname(postEntity.getMember().getProfile().getNickname())
                    .build();
            return ResponsePostDTO.builder()
                    .id(postEntity.getId())
                    .category(category)
                    .title(postEntity.getTitle())
                    .author(author)
                    .likeCount(likeCount)
                    .createdAt(postEntity.getCreatedAt())
                    .build();
        }).toList();
    }

    @Override
    public ResponseDetailPostDTO getDetailPostById(Long id) {
        Object[][] objects = postRepository.findDetailPostById(id);
        PostEntity postEntity = null;
        Long likeCount =0L;
        for(Object[] o : objects){
            postEntity = (PostEntity) o[0];
            likeCount = (Long) o[1];

        }
        if(postEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        Author author = Author.builder()
                .id(postEntity.getMember().getId())
                .nickname(postEntity.getMember().getProfile().getNickname())
                .build();

        return ResponseDetailPostDTO.builder()
                .id(postEntity.getId())
                .title(postEntity.getTitle())
                .contents(postEntity.getContents())
                .author(author)
                .category(postEntity.getCategory().getCategory())
                .likeCount(likeCount)
                .createdAt(postEntity.getCreatedAt())
                .build();
    }

    @Override
    public PostEntity getPostById(Long id) {
        return postRepository.findById(id);
    }

    // 게시글 추가
    @Override
    @Transactional
    public void addPost(RequestPostDTO requestPostDTO, String email) {
        // 유저 조회
        UserEntity userEntity = userRepository.findByEmail(email);

        // 카테고리 저장 및 조회
        PostCategory postCategory = PostCategory.builder()
                .category(requestPostDTO.getCategory())
                .build();

        PostCategoryEntity savedPostCategoryEntity = postCategoryRepository.findByCategory(requestPostDTO.getCategory());

        if(savedPostCategoryEntity == null) {
            PostCategoryEntity unsavedPostCategoryEntity = PostCategoryEntity.toEntity(postCategory);
            savedPostCategoryEntity = postCategoryRepository.save(unsavedPostCategoryEntity);
        }

        // 포스트 저장
        Post post = Post.builder().title(requestPostDTO.getTitle())
                        .contents(requestPostDTO.getContents())
                        .build();
        PostEntity unsavedPostEntity =  PostEntity.toEntity(post, userEntity, savedPostCategoryEntity);
        
        postRepository.save(unsavedPostEntity);

    }

    // 게시글 수정
    @Override
    public void update(RequestPostDTO requestPostDTO, Long id, String email) {
        UserEntity userEntity = postRepository.findAuthorByPostIdAndEmail(id, email);
        PostCategoryEntity updatedPostCategoryEntity = postCategoryRepository.findByCategory(requestPostDTO.getCategory());
        PostEntity unsavedPostEntity = PostEntity.builder()
                      .id(id)
                      .category(updatedPostCategoryEntity)
                      .member(userEntity)
                      .title(requestPostDTO.getTitle())
                      .contents(requestPostDTO.getContents())
                      .build();

        postRepository.save(unsavedPostEntity);

    }

    // 게시글 삭제
    @Override
    public void removePost(Long id, String email) {
        Long userId = postRepository.findAuthorIdByPostIdAndEmail(id, email);
        if(userId != null){
            postRepository.deleteById(id);
        }
    }
}
