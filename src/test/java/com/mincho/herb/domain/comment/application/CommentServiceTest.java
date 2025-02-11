package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.RequestCommentCreateDTO;
import com.mincho.herb.domain.comment.dto.RequestCommentUpdateDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private MemberEntity mockMember;
    private PostEntity mockPost;

    @BeforeEach
    void setUp() {
        mockMember = MemberEntity.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        mockPost = PostEntity.builder()
                .id(1L)
                .title("Test Post")
                .build();

    }

    // 댓글 추가
    @Test
    void addComment_NoParentComment() {
        // given
        RequestCommentCreateDTO requestDTO = RequestCommentCreateDTO.builder()
                        .postId(1L)
                        .parentCommentId(null)
                        .contents("댓글 내용")
                        .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockMember);
        when(postRepository.findById(requestDTO.getPostId())).thenReturn(mockPost);

        // when
        commentService.addComment(requestDTO, "test@example.com");

        // then
        verify(commentRepository, times(1)).save(argThat(comment ->
                comment.getLevel() == 0 && comment.getContents().equals("댓글 내용")
        ));
    }

    @Test
    void addComment_ExistsParentComment_Level_1() {
        // given
        CommentEntity parentComment = CommentEntity.builder()
                .id(10L)
                .level(0L)
                .contents("부모 댓글")
                .post(mockPost)
                .member(mockMember)
                .build();

        RequestCommentCreateDTO requestDTO = RequestCommentCreateDTO.builder()
                .postId(1L)
                .parentCommentId(10L)
                .contents("자식 댓글")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockMember);
        when(postRepository.findById( requestDTO.getPostId())).thenReturn(mockPost);
        when(commentRepository.findById(requestDTO.getParentCommentId())).thenReturn(parentComment);

        // when
        commentService.addComment(requestDTO, "test@example.com");

        // then
        verify(commentRepository, times(1)).save(argThat(comment ->
                comment.getLevel() == 1L && comment.getContents().equals("자식 댓글")
        ));
    }

    @Test
    void addComment_Calculate_ParentCommentLevel() {
        // given
        // - 할아버지 댓글
        CommentEntity grandParentComment = CommentEntity.builder()
                .id(5L)
                .level(0L)
                .contents("최상위 댓글")
                .post(mockPost)
                .member(mockMember)
                .build();

        // - 부모 댓글
        CommentEntity parentComment = CommentEntity.builder()
                .id(10L)
                .level(1L)
                .contents("부모 댓글")
                .post(mockPost)
                .member(mockMember)
                .parentComment(grandParentComment)
                .build();
        
        // - 추가할 자식 댓글
        RequestCommentCreateDTO requestDTO = RequestCommentCreateDTO.builder()
                .postId(1L)
                .parentCommentId(10L)
                .contents("댓글의 댓글의 댓글")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockMember);
        when(postRepository.findById(requestDTO.getPostId())).thenReturn(mockPost);
        when(commentRepository.findById(requestDTO.getParentCommentId())).thenReturn(parentComment);

        // when
        commentService.addComment(requestDTO, "test@example.com");

        // then
        verify(commentRepository, times(1)).save(argThat(comment ->
                comment.getLevel() == 2L && comment.getContents().equals("댓글의 댓글의 댓글")
        ));
    }


    /* 댓글 논리적 삭제/수정 */
    @Test
    void updateComment_shouldModifyCommentDetails(){
        // given

        RequestCommentUpdateDTO dto = RequestCommentUpdateDTO.builder()
                .id(5L)
                .isDeleted(true)
                .contents("test")
                .build();

        CommentEntity mockComment = CommentEntity.builder()
                .id(5L)
                .level(0L)
                .contents(dto.getContents())
                .post(mockPost)
                .member(mockMember)
                .deleted(dto.getIsDeleted())
                .build();

        when(commentRepository.findById(dto.getId())).thenReturn(mockComment);

        // when
        commentService.updateComment(dto);

        // then
        verify(commentRepository, times(1)).save(argThat(comment ->
                comment.getDeleted() && comment.getContents().equalsIgnoreCase("test")
                ));
    }
}
