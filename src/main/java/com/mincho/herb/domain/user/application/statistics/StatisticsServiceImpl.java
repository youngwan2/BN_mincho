package com.mincho.herb.domain.user.application.statistics;

import com.mincho.herb.domain.bookmark.repository.herbBookmark.HerbBookmarkRepository;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.post.repository.postStatistics.PostStatisticsRepository;
import com.mincho.herb.domain.user.dto.StatisticsResponseDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CommentRepository commentRepository;
    private final HerbBookmarkRepository herbBookmarkRepository;
    private final PostStatisticsRepository postStatisticsRepository;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    // 사용자 콘텐츠 통계
    @Override
    public StatisticsResponseDTO getStat() {

        String email = authUtils.userCheck();

        UserEntity userEntity = userRepository.findByEmail(email);

        log.info("조회 유저의 ID:{}", userEntity.getId());
        Long commentCount = commentRepository.countByMemberId(userEntity.getId());
        Long herbBookmarkCount = herbBookmarkRepository.countByUserId(userEntity.getId());
        Long postCount = postStatisticsRepository.countByUserId(userEntity.getId());

        return StatisticsResponseDTO.builder()
                .bookmarkCount(herbBookmarkCount)
                .commentCount(commentCount)
                .postCount(postCount)
                .build();
    }
}
