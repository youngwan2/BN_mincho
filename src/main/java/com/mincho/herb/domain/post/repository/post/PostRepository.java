package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.common.dto.PageInfoDTO;
import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.dto.PostDTO;
import com.mincho.herb.domain.post.dto.SearchConditionDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import java.util.List;

public interface PostRepository {

    PostEntity save(PostEntity postEntity);
    List<PostDTO> findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO);
    Object[][] findByPostId(Long postId);
    PostEntity findById(Long postId);
    Long findAuthorIdByPostIdAndEmail(Long postId, String email);
    MemberEntity findAuthorByPostIdAndEmail(Long postId, String email);
    void update(PostEntity postEntity);
    void deleteById(Long id);
    int countByCategory(String category);
    int countByMemberId(Long memberId);
    List<PostCountDTO> countsByCategory();
}
