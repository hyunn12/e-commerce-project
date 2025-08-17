package com.loopers.domain.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public Brand getDetail(Long id) {
        return brandRepository.findById(id);
    }

    public List<Brand> getListByIds(List<Long> ids) {
        return brandRepository.findAllByIds(ids);
    }

    public List<Brand> getTopList() {
        return brandRepository.findTopList();
    }
}
