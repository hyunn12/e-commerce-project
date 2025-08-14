package com.loopers.domain.brand;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository {

    Brand findById(Long id);

    List<Brand> findAllByIds(List<Long> ids);

    List<Brand> findTopList(int limit);
}
