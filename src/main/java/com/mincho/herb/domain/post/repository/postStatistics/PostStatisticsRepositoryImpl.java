package com.mincho.herb.domain.post.repository.postStatistics;

import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.repository.post.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostStatisticsRepositoryImpl implements PostStatisticsRepository {

    private final PostJpaRepository postJpaRepository;
    @Override
    public List<PostCountDTO> countsByCategory() {
        return  postJpaRepository.countPostsByCategory();
    }
}
