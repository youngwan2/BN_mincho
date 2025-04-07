package com.mincho.herb.domain.user.application.statistics;

import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.bookmark.repository.HerbBookmarkRepository;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.user.dto.StatisticsResponseDTO;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CommentRepository commentRepository;
    private final HerbBookmarkRepository herbBookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommonUtils commonUtils;

    // 사용자 콘텐츠 통계
    @Override
    public StatisticsResponseDTO getStat() {

        String email = commonUtils.userCheck();

        MemberEntity memberEntity = userRepository.findByEmail(email);

        log.info("조회 유저의 ID:{}", memberEntity.getId());
        Long commentCount = commentRepository.countByMemberId(memberEntity.getId());
        Long herbBookmarkCount = herbBookmarkRepository.countByMemberId(memberEntity.getId());
        Long postCount = postRepository.countByMemberId(memberEntity.getId());

        return StatisticsResponseDTO.builder()
                .bookmarkCount(herbBookmarkCount)
                .commentCount(commentCount)
                .postCount(postCount)
                .build();
    }
}
