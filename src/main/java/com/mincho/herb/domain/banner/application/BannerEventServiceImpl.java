package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerEventServiceImpl implements BannerEventService {
    private final BannerRepository bannerRepository;

    @Override
    @Transactional
    public void handleBannerClick(Long id) {
        bannerRepository.incrementClickCount(id);
        log.debug("배너 클릭 처리. ID: {}", id);
    }

    @Override
    @Transactional
    public void handleBannerView(Long id) {
        bannerRepository.incrementViewCount(id);
        log.debug("배너 노출 처리. ID: {}", id);
    }
}

