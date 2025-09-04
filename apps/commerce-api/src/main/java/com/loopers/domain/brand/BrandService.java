package com.loopers.domain.brand;

import com.loopers.domain.event.BrandEventPublisher;
import com.loopers.domain.event.dto.BrandModifyEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandEventPublisher brandEventPublisher;

    public Brand getDetail(Long id) {
        Brand brand = brandRepository.findById(id);
        if (brand == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "브랜드 정보를 찾을 수 없습니다.");
        }
        return brand;
    }

    public List<Brand> getListByIds(List<Long> ids) {
        return brandRepository.findAllByIds(ids);
    }

    public List<Brand> getTopList() {
        return brandRepository.findTopList();
    }

    @Transactional
    public Brand modify(Long id, String name, String description) {
        Brand brand = getDetail(id);
        brand.update(name, description);

        brandEventPublisher.publish(BrandModifyEvent.of(brand.getId()));

        return brand;
    }
}
