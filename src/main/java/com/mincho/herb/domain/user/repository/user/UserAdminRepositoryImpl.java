package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.dto.AdminUserListDTO;
import com.mincho.herb.domain.user.dto.AdminUserResponseDTO;
import com.mincho.herb.domain.user.dto.SortInfoDTO;
import com.mincho.herb.domain.user.dto.UserListSearchCondition;
import com.mincho.herb.domain.user.entity.QProfileEntity;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.entity.UserStatusEnum;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserAdminRepositoryImpl implements UserAdminRepository {

    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public void save(UserEntity userEntity) {
        userJpaRepository.save(userEntity);
    }


    /**
     * 유저 정보 조회
     * @param condition 검색 조건
     * @param sortInfoDTO 정렬 조건
     * @param pageable 페이징 조건
     * @return AdminUserResponseDTO
      */
    @Override
    public AdminUserResponseDTO getUserInfo(UserListSearchCondition condition, SortInfoDTO sortInfoDTO, Pageable pageable) {

        QUserEntity user = QUserEntity.userEntity;
        QProfileEntity profile =QProfileEntity.profileEntity;

        String status = condition.getStatus();
        String keyword = condition.getKeyword();

        // 검색 조건
        BooleanBuilder builder = new BooleanBuilder();

        if(keyword != null && !keyword.isEmpty()) {
            builder.and(user.profile.nickname.like("%"+keyword+"%")).or(user.email.like("%"+keyword+"%"));
        }
        
        if(status != null && !status.isEmpty()){
            status = switch (status) {
                case "deleted" -> "DELETED";
                case "inactive" -> "INACTIVE";
                case "active" -> "ACTIVE";
                default -> throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 status 값입니다.");
            };
            builder.and(user.status.eq(UserStatusEnum.valueOf(status)));
        }

        // 정렬 조건
        OrderSpecifier<?> sortBuilder = null;
        if(sortInfoDTO.getSort() !=null) {
            if(sortInfoDTO.getSort().equals("createdAt") || sortInfoDTO.getSort().equals("lastLoginAt")) {
                sortBuilder =  sortInfoDTO.getOrder().equals("asc") ? user.id.asc() : user.id.desc();
            }
        }


        List<AdminUserListDTO> users = jpaQueryFactory
                .select(Projections.constructor(AdminUserListDTO.class,
                        user.id,
                        user.profile.nickname,
                        user.email,
                        new CaseBuilder()
                                .when(user.role.eq("ROLE_USER")).then("user")
                                .otherwise("admin"),
                        new CaseBuilder()
                                .when(user.status.eq(UserStatusEnum.ACTIVE)).then("active")
                                .when(user.status.eq(UserStatusEnum.INACTIVE)).then("inactive")
                                .otherwise("deleted"),
                        user.createdAt,
                        user.lastLoginAt,
                        new CaseBuilder()
                                .when(user.providerId.isNotNull()).then("social")
                                .otherwise("email")
                        ))
                .from(user)
                .leftJoin(profile).on(user.id.eq(profile.user.id))
                .where(builder)
                .orderBy(sortBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory.select(user.count()).from(user)
                .where(builder)
                .fetchOne();

        return AdminUserResponseDTO.builder()
                .users(users)
                .totalCount(totalCount)
                .build();
    }

    @Override
    public void updateUserStatus(UserEntity userEntity) {
        userJpaRepository.save(userEntity);
    }

    @Override
    public void updateUserRole(UserEntity userEntity) {
        userJpaRepository.save(userEntity);

    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        userJpaRepository.save(userEntity);
    }
}
