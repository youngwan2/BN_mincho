package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportJpaRepository extends JpaRepository<ReportEntity, Long> {
}
