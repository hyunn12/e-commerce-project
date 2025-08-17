package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final BrandJpaRepository brandJpaRepository;
    private final BrandQueryDslRepository brandQueryDslRepository;

    @Override
    public Brand findById(Long id) {
        return brandJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Brand> findAllByIds(List<Long> ids) {
        return brandJpaRepository.findAllById(ids);
    }

    @Override
    public List<Brand> findTopList() {
        return brandQueryDslRepository.findTopList();
    }
}
