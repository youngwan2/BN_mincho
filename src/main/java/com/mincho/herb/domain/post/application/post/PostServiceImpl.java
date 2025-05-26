package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.domain.Author;
import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.post.domain.ViewCount;
import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.PostViewsEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.post.repository.postViews.PostViewsRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.page.PageInfoDTO;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import com.mincho.herb.infra.auth.S3Service;
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
    private final PostViewsRepository postViewsRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final AuthUtils authUtils;


    // 조건 별 게시글 조회
    @Override
    public PostResponseDTO getPostsByCondition(int page, int size, SearchConditionDTO searchConditionDTO) {
        PageInfoDTO pageInfoDTO = PageInfoDTO.builder().page(page).size(size).build();

        // 포스트 엔티티 목록
        List<PostDTO> posts = postRepository.findAllByConditions(searchConditionDTO, pageInfoDTO);

        // 포스트가 비어 있는 경우
        if(posts.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 목록이 존재하지 않습니다.");
        }
        // 포스트 + 개수
        return PostResponseDTO.builder()
                .posts(posts) // 게시글 목록
                .build();
    }

    // 포스트 상세 조회
    @Override
    public DetailPostResponseDTO getDetailPostById(Long id) {
        String email = authUtils.userCheck();

        UserEntity userEntity = userService.getUserByEmailOrNull(email);
        Long userId = userEntity == null ? 0L : userEntity.getId();


        PostViewsEntity postViewsEntity = postViewsRepository.findByPostId(id);
        Long prevPostViewCount = 0L;

        // view(조회) 정보 없으면 조회 데이터 추가
        if(postViewsEntity == null){
            PostEntity postEntity = postRepository.findById(id);

            postViewsRepository.save(
                    PostViewsEntity.builder()
                            .viewCount(0L)
                            .post(postEntity)
                            .build()
            );
        } else {
            prevPostViewCount = postViewsEntity.getViewCount();
        }

        // 조회수 업데이트
        postViewsRepository.updatePostViewCount(ViewCount.builder().build().increase(prevPostViewCount), id);

        // TODO: 취약한 방식이므로 향후 DTO 등의 안전한 방식으로 변경 필
        Object[][] objects = postRepository.findByPostId(id);
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
                .nickname(postEntity.getUser().getProfile().getNickname())
                .profileImage(postEntity.getUser().getProfile().getAvatarUrl())
                .build();


        log.debug("userId:{}, {}", userId, postEntity.getUser().getId());

        return DetailPostResponseDTO.builder()
                .id(postEntity.getId())
                .title(postEntity.getTitle())
                .contents(postEntity.getContents())
                .author(author)
                .category(postEntity.getCategory().getCategory())
                .isMine(postEntity.getUser().getId().equals(userId))
                .likeCount(likeCount)
                .viewCount(prevPostViewCount+1)
                .createdAt(postEntity.getCreatedAt())
                .build();
    }

    // 게시글 상세 조회
    @Override
    public PostEntity getPostById(Long id) {
        return postRepository.findById(id);
    }


    // 게시글 추가
    @Override
    @Transactional
    public void addPost(PostRequestDTO postRequestDTO, String email) {
        // 유저 조회
        UserEntity userEntity = userService.getUserByEmail(email);

        // 바꾼 카테고리
        PostCategoryEntity changedCategoryEntity = postCategoryRepository.findByCategory(postRequestDTO.getCategory());

        if(changedCategoryEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 카테고리는 존재하지 않습니다.");
        }

        // 포스트 저장
        Post post = Post.builder()
                        .title(postRequestDTO.getTitle())
                        .contents(postRequestDTO.getContents())
                        .build();
        PostEntity unsavedPostEntity =  PostEntity.toEntity(post, userEntity, changedCategoryEntity);
        PostEntity savedPostEntity = postRepository.save(unsavedPostEntity);

        // 포스트 조회수 초기 상태 설정
        PostViewsEntity unsavedViewsEntity = PostViewsEntity.builder()
                .post(savedPostEntity)
                .viewCount(0L)
                .build();
        postViewsRepository.save(unsavedViewsEntity);


    }

    // 게시글 수정
    @Override
    @Transactional
    public void update(PostRequestDTO postRequestDTO, Long id, String email) {
        UserEntity userEntity = postRepository.findAuthorByPostIdAndEmail(id, email);

        PostEntity oldPostEntity = postRepository.findById(id);
        String oldContent = oldPostEntity.getContents(); // 수정 전 콘텐츠

        PostCategoryEntity updatedPostCategoryEntity = postCategoryRepository.findByCategory(postRequestDTO.getCategory());

        PostEntity unsavedPostEntity = PostEntity.builder()
                      .id(id)
                      .category(updatedPostCategoryEntity)
                      .user(userEntity)
                      .title(postRequestDTO.getTitle())
                      .contents(postRequestDTO.getContents())
                      .build();

        PostEntity newPostEntity = postRepository.save(unsavedPostEntity);

        String newContent = newPostEntity.getContents(); // 수정 후 콘텐츠
        s3Service.deleteRemovedOldImages(oldContent, newContent); // 안 쓰는 이미지 S3 에서 제거
    }

    // 게시글 삭제
    @Override
    @Transactional
    public void removePost(Long id, String email) {
        Long userId = postRepository.findAuthorIdByPostIdAndEmail(id, email);
        if(userId != null){

            PostEntity postEntity = postRepository.findById(id);

            String content = postEntity.getContents();

            // S3 에서 기존 컨텐츠에 저장되어 있던 이미지를 모두 제거함
            s3Service.deleteAllImagesInContent(content);

            // 게시글 삭제
            postRepository.deleteById(id);
        }
    }


    /** 마이페이지 */
    // 유저가 작성한 게시글 목록
    @Override
    public List<MypagePostsDTO> getUserPosts(int page, int size) {
        String email = authUtils.userCheck();

        Pageable pageable = PageRequest.of(page, size);

        UserEntity userEntity = userService.getUserByEmail(email);

        // 게시글 목록
        List<PostEntity> postEntities= postRepository.findByUserId(userEntity.getId(), pageable).toList();
        return postEntities.stream().map((postEntity)->{
            return MypagePostsDTO.builder()
                    .id(postEntity.getId())
                    .title(postEntity.getTitle())
                    .createdAt(postEntity.getCreatedAt())
                    .build();
        }).toList();

    }
}
