package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.dto.PageInfoDTO;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.post.domain.Author;
import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.post.domain.PostCategory;
import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.PostViewsEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.post.repository.postViews.PostViewsRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostViewsRepository postViewsRepository;
    private final UserRepository userRepository;
    private final CommonUtils commonUtils;

    // 조건 별 게시글 조회
    @Override
    public PostResponseDTO getPostsByCondition(int page, int size, SearchConditionDTO searchConditionDTO) {
        PageInfoDTO pageInfoDTO = PageInfoDTO.builder().page((long) page).size((long) size).build();
        
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

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        MemberEntity memberEntity = userRepository.findByEmail2(email);


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
                .nickname(postEntity.getMember().getProfile().getNickname())
                .build();

        return DetailPostResponseDTO.builder()
                .id(postEntity.getId())
                .title(postEntity.getTitle())
                .contents(postEntity.getContents())
                .author(author)
                .category(postEntity.getCategory().getCategory())
                .isMine(postEntity.getMember().getId().equals(memberEntity.getId()))
                .likeCount(likeCount)
                .createdAt(postEntity.getCreatedAt())
                .build();
    }

    // 게시글 상세 조회
    @Override
    public PostEntity getPostById(Long id) {
        return postRepository.findById(id);
    }


    // 카테고리별 게시글 통계
    @Override
    public List<PostCountDTO> getPostStatistics() {
        List<PostCountDTO> counts = postRepository.countsByCategory();
        if(counts.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회할 게시글 통계가 존재하지 않습니다.");
        }
        return counts;
    }


    // 게시글 추가
    @Override
    @Transactional
    public void addPost(PostRequestDTO postRequestDTO, String email) {
        // 유저 조회
        MemberEntity memberEntity = userRepository.findByEmail(email);


        // 해당 카테고리가 실제로 존재하는지 검증
        PostCategoryEntity postCategoryEntity = postCategoryRepository.findByCategory(postRequestDTO.getCategory());

        if(postCategoryEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 카테고리는 존재하지 않습니다.");
        }

        // 카테고리 엔티티 조회
        PostCategoryEntity savedPostCategoryEntity = postCategoryRepository.findByCategory(postRequestDTO.getCategory());


        // 포스트 저장
        Post post = Post.builder().title(postRequestDTO.getTitle())
                        .contents(postRequestDTO.getContents())
                        .build();
        PostEntity unsavedPostEntity =  PostEntity.toEntity(post, memberEntity, savedPostCategoryEntity);
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
    public void update(PostRequestDTO postRequestDTO, Long id, String email) {
        MemberEntity memberEntity = postRepository.findAuthorByPostIdAndEmail(id, email);

        PostCategoryEntity updatedPostCategoryEntity = postCategoryRepository.findByCategory(postRequestDTO.getCategory());

        PostEntity unsavedPostEntity = PostEntity.builder()
                      .id(id)
                      .category(updatedPostCategoryEntity)
                      .member(memberEntity)
                      .title(postRequestDTO.getTitle())
                      .contents(postRequestDTO.getContents())
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


    /** 마이페이지 */
    // 유저가 작성한 게시글 목록
    @Override
    public List<MypagePostsDTO> getUserPosts(int page, int size) {
        String email = commonUtils.userCheck();

        Pageable pageable = PageRequest.of(page, size);

        MemberEntity memberEntity = userRepository.findByEmail(email);

        // 게시글 목록
        List<PostEntity> postEntities= postRepository.findByMemberId(memberEntity.getId(), pageable).toList();
        return postEntities.stream().map((postEntity)->{
            return MypagePostsDTO.builder()
                    .id(postEntity.getId())
                    .title(postEntity.getTitle())
                    .createdAt(postEntity.getCreatedAt())
                    .build();
        }).toList();

    }
}
