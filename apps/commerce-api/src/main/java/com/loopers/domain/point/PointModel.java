package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import static com.loopers.support.utils.Validation.Message.MESSAGE_POINT_CHARGE;

@Entity
@Table(name = "point")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PointModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private int point;

    public PointModel(String userId, int point) {
        this.userId = userId;
        this.point = point;
    }

    public void addPoint(int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_POINT_CHARGE);
        }
        this.point += amount;
    }

}
