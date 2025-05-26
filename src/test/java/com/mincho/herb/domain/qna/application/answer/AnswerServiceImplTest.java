package com.mincho.herb.domain.qna.application.answer;


import com.mincho.herb.domain.qna.application.answerImage.AnswerImageService;
import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.QnaEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.qna.QnaRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * AnswerServiceImplTest
 *
 * <p>단위 테스트 클래스 - AnswerServiceImpl의 주요 기능들을 검증합니다.</p>
 *
 * <ul>
 *     <li>create() : 정상 생성 및 예외 처리 테스트</li>
 *     <li>update() : 답변 수정 기능 테스트</li>
 *     <li>delete() : 답변 삭제 기능 및 이미지 삭제 테스트</li>
 *     <li>adopt() : 답변 채택 기능 테스트</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class AnswerServiceImplTest {

    @InjectMocks
    private AnswerServiceImpl answerService;

    @Mock
    private QnaRepository qnaRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerImageService answerImageService;

    @Mock
    private UserService userService;

    @Mock
    private AuthUtils authUtils;

    private UserEntity member;
    private QnaEntity qna;
    private AnswerRequestDTO requestDTO;


    /**
     * 테스트 실행 전 공통 데이터 세팅
     */
    @BeforeEach
    void setUp() {
        member = UserEntity.builder().id(1L).email("test@example.com").build();
        qna = QnaEntity.builder().id(1L).writer(UserEntity.builder().id(2L).build()).build();
        requestDTO = new AnswerRequestDTO();
        requestDTO.setContent("답변 내용");
    }

    /**
     * 답변 생성 성공 시 검증 테스트
     */
    @Test
    void create_Success() {
        // given
        when(authUtils.userCheck()).thenReturn("test@example.com");
        when(userService.getUserByEmail(eq("test@example.com"))).thenReturn(member);
        when(qnaRepository.findById(1L)).thenReturn(qna);
        when(answerRepository.existsByQnaIdAndWriterId(1L, 1L)).thenReturn(false);
        when(answerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        answerService.create(1L, requestDTO, null);

        // then
        verify(answerRepository, times(1)).save(any(AnswerEntity.class));
        verify(answerImageService).imageUpload(isNull(), any(AnswerEntity.class));
    }


    /**
     * 본인 질문에 답변 시 예외 발생 테스트
     */
    @Test
    void create_WhenMyQna_ThrowException() {
        // given
        qna.setWriter(member); // 본인 질문
        when(authUtils.userCheck()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(member);
        when(qnaRepository.findById(1L)).thenReturn(qna);

        // expect
        assertThatThrownBy(() -> answerService.create(1L, requestDTO, null))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("본인의 질문에 답변할 수 없습니다.");
    }

    /**
     * 답변 수정 성공 테스트
     */
    @Test
    void update_Success() {
        // given
        AnswerEntity answer = AnswerEntity.builder().id(1L).writer(member).build();
        when(authUtils.userCheck()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(member);
        when(answerRepository.findById(1L)).thenReturn(answer);

        // when
        answerService.update(1L, requestDTO);

        // then
        assertThat(answer.getContent()).isEqualTo("답변 내용");
    }


    /**
     * 답변 삭제 시 이미지 포함 정상 삭제 테스트
     */
    @Test
    void delete_Success() {
        // given
        AnswerEntity answer = AnswerEntity.builder().id(1L).writer(member).build();
        when(authUtils.userCheck()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(member);
        when(answerRepository.findById(1L)).thenReturn(answer);

        // when
        answerService.delete(1L);

        // then
        verify(answerImageService).imageDelete(anyList(), eq(answer));
        verify(answerRepository).deleteById(1L);
    }

    /**
     * 답변 채택 성공 테스트
     */
    @Test
    void adopt_Success() {
        // given
        QnaEntity qna = QnaEntity.builder().id(10L).writer(member).build();
        AnswerEntity answer = AnswerEntity.builder().id(1L).qna(qna).isAdopted(false).build();

        when(authUtils.userCheck()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(member);
        when(answerRepository.findById(1L)).thenReturn(answer);
        when(qnaRepository.findById(1L)).thenReturn(qna);
        when(answerRepository.existsByQnaIdAndIdAndIsAdoptedTrue(10L, 1L)).thenReturn(false);

        // when
        answerService.adopt(1L);

        // then
        assertThat(answer.getIsAdopted()).isTrue();
    }
}
