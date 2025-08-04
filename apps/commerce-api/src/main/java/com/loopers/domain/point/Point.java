package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.loopers.support.utils.Validation.Message.*;

@Getter
@Entity
@Table(name = "point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "point", nullable = false)
    private int point;

    public Point(Long userId, int point) {
        if (point < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_POINT_CREATE);
        }
        this.userId = userId;
        this.point = point;
    }

    public void addPoint(int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_POINT_CHARGE);
        }
        this.point += amount;
    }

    public void usePoint(int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_POINT_USE);
        }
        if (amount > this.point) {
            throw new CoreException(ErrorType.CONFLICT, MESSAGE_POINT_NOT_ENOUGH);
        }
        this.point -= amount;
    }
}
