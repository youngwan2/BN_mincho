package com.mincho.herb.domain.post.repository;

import com.mincho.herb.domain.post.dto.PostStatisticsDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostJpaRepository;
import com.mincho.herb.domain.post.repository.post.PostRepositoryImpl;
import com.mincho.herb.domain.post.repository.postStatistics.PostStatisticsRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(PostRepositoryImplTest.class);
    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private PostRepositoryImpl postRepository;
    private PostStatisticsRepositoryImpl postStatisticsRepository;

    @BeforeEach
    void setUp() {
        postRepository = new PostRepositoryImpl(postJpaRepository, new JPAQueryFactory(entityManager));
        postStatisticsRepository = new PostStatisticsRepositoryImpl(postJpaRepository, new JPAQueryFactory(entityManager));
    }

    @Test
    void save_ShouldSavePostEntity() {
        // given
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle("Test Post");
        postEntity.setCreatedAt(LocalDateTime.now());

        // when
        PostEntity savedEntity = postRepository.save(postEntity);

        // then
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getTitle()).isEqualTo("Test Post");
    }

    @Test
    void findPostStatics_ShouldReturnPostStatistics() {
        // given
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle("Test Post");
        postEntity.setCreatedAt(LocalDateTime.now());
        postJpaRepository.save(postEntity);

        // when
        PostStatisticsDTO statistics = postStatisticsRepository.findPostStatics();

        // then
        assertThat(statistics.getTotalCount()).isGreaterThan(0);
    }

}
