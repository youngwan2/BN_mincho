package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.domain.Post;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostViewsRepository postViewsRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final AuthUtils authUtils;

    /**
     * 게시글 목록 조회
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param searchConditionDTO 검색 조건 DTO
     * @return PostResponseDTO 게시글 목록 응답 DTO
     */
    @Override
    public PostResponseDTO getPostsByCondition(int page, int size, SearchConditionDTO searchConditionDTO) {
        PageInfoDTO pageInfoDTO = PageInfoDTO.builder().page(page).size(size).build();

        // 현재 사용자 이메일 가져오기 (로그인하지 않았다면 null)
        String email = authUtils.userCheck();

        // 포스트 엔티티 목록
        return postRepository.findAllByConditions(searchConditionDTO, pageInfoDTO, email);
    }

    /**
     * 게시글 상세 조회
     *
     * @param id 게시글 ID
     * @return DetailPostResponseDTO 게시글 상세 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public DetailPostResponseDTO getDetailPostById(Long id) {
        // 현재 사용자 이메일 가져오기 (로그인하지 않았다면 null)
        String email = authUtils.userCheck();

        // 조회수 증가 처리
        updateViewCount(id);

        // QueryDSL을 사용하여 게시글 상세 정보 조회 (태그 포함)
        DetailPostDTO detailPost = postRepository.findDetailPostById(id, email);

        // 조회수를 1 증가시킨 값으로 DetailPostResponseDTO 구성
        return DetailPostResponseDTO.builder()
                .id(detailPost.getId())
                .title(detailPost.getTitle())
                .contents(detailPost.getContents())
                .author(detailPost.getAuthor())
                .category(detailPost.getCategory())
                .isMine(detailPost.getIsMine())
                .likeCount(detailPost.getLikeCount())
                .viewCount(detailPost.getViewCount() + 1) // 조회수 1 증가
                .createdAt(detailPost.getCreatedAt())
                .tags(detailPost.getTags()) // 태그 목록 포함
                .build();
    }

    /**
     * 게시글 조회수 증가 처리
     *
     * @param postId 게시글 ID
     */
    private void updateViewCount(Long postId) {
        PostViewsEntity postViewsEntity = postViewsRepository.findByPostId(postId);

        // 조회수 정보가 없으면 초기 조회수 0으로 생성
        if (postViewsEntity == null) {
            PostEntity postEntity = postRepository.findById(postId);
            postViewsRepository.save(
                    PostViewsEntity.builder()
                            .viewCount(0L)
                            .post(postEntity)
                            .build()
            );
        } else {
            // 조회수 증가 (1 증가)
            postViewsEntity.setViewCount(postViewsEntity.getViewCount() + 1);
            postViewsRepository.save(postViewsEntity);
        }
    }

    /**
     * 게시글 ID로 게시글 조회
     *
     * @param id 게시글 ID
     * @return PostEntity 게시글 엔티티
     */
    @Override
    public PostEntity getPostById(Long id) {
        return postRepository.findById(id);
    }

    /**
     * 유저 ID로 게시글 목록 조회
     *
     * @param userId 유저 ID
     * @param pageable 페이징 정보
     * @return UserPostResponseDTO 유저 게시글 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public UserPostResponseDTO getUserPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.findAllByUserId(userId, pageable);
    }


    /**
     * 게시글 등록
     *
     * @param postRequestDTO 게시글 요청 DTO
     * @param email 유저 이메일
     */
    @Override
    @Transactional
    public void addPost(PostRequestDTO postRequestDTO, String email) {
        // 유저 조회
        UserEntity userEntity = userService.getUserByEmail(email);

        // 바꾼 카테고리
        PostCategoryEntity changedCategoryEntity = postCategoryRepository.findByType(postRequestDTO.getCategoryType());

        if(changedCategoryEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 카테고리는 존재하지 않습니다.");
        }

        // 포스트 저장
        Post post = Post.builder()
                        .title(postRequestDTO.getTitle())
                        .contents(postRequestDTO.getContents())
                        .isDeleted(false)
                        .tags(postRequestDTO.getTags())
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

    /**
     * 게시글 수정
     *
     * @param postRequestDTO 게시글 요청 DTO
     * @param id 게시글 ID
     * @param email 유저 이메일
     */
    @Override
    @Transactional
    public void update(PostRequestDTO postRequestDTO, Long id, String email) {
        UserEntity userEntity = postRepository.findAuthorByPostIdAndEmail(id, email);

        PostEntity oldPostEntity = postRepository.findById(id);
        String oldContent = oldPostEntity.getContents(); // 수정 전 콘텐츠
        Boolean isDeleted = oldPostEntity.getIsDeleted(); // 기존 삭제 상태 유지

        PostCategoryEntity updatedPostCategoryEntity = postCategoryRepository.findByType(postRequestDTO.getCategoryType());

        PostEntity unsavedPostEntity = PostEntity.builder()
                      .id(id)
                      .category(updatedPostCategoryEntity)
                      .user(userEntity)
                      .title(postRequestDTO.getTitle())
                      .contents(postRequestDTO.getContents())
                      .tags(postRequestDTO.getTags())
                      .isDeleted(isDeleted) // 기존 삭제 상태 유지
                      .pined(oldPostEntity.getPined()) // 기존 고정 상태 유지
                      .build();

        PostEntity newPostEntity = postRepository.save(unsavedPostEntity);

        String newContent = newPostEntity.getContents(); // 수정 후 콘텐츠
        s3Service.deleteRemovedOldImages(oldContent, newContent); // 안 쓰는 이미지 S3 에서 제거
    }

    /**
     * 게시글 삭제
     *
     * @param id 게시글 ID
     * @param email 유저 이메일
     */
    @Override
    @Transactional
    public void removePost(Long id, String email) {
        Long userId = postRepository.findAuthorIdByPostIdAndEmail(id, email);
        if(userId != null){

            PostEntity postEntity = postRepository.findById(id);

            String content = postEntity.getContents();

            // S3 에서 기존 컨텐츠에 저장되어 있던 이미지를 모두 제거함
            s3Service.deleteAllImagesInContent(content);

            postEntity.changeIsDeleted(true); // 게시글 삭제 상태로 변경
        }
    }

    /**
     * 마이페이지 - 유저 게시글 목록 조회
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return List<MypagePostsDTO> 유저 게시글 목록 DTO 리스트
     */
    @Override
    public List<MypagePostsDTO> getUserPosts(int page, int size) {
        String email = authUtils.userCheck();

        Pageable pageable = PageRequest.of(page, size);

        UserEntity userEntity = userService.getUserByEmail(email);

        // 게시글 목록
        List<PostEntity> postEntities = postRepository.findByUserId(userEntity.getId(), pageable).toList();
        return postEntities.stream().map((postEntity)->{
            return MypagePostsDTO.builder()
                    .id(postEntity.getId())
                    .title(postEntity.getTitle())
                    .createdAt(postEntity.getCreatedAt())
                    .build();
        }).toList();
    }

    /**
     * 인기 태그 목록을 조회합니다.
     *
     * @param limit 최대 태그 수
     * @return 태그명과 사용 횟수 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<TagCountDTO> getPopularTags(int limit) {
        return postRepository.findTagsWithCount(limit);
    }
}
