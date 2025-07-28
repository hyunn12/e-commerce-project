package com.loopers.domain.brand;

import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository {

    Brand findById(Long id);

}
