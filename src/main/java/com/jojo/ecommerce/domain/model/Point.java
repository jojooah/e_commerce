package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.application.exception.InsufficientPointException;
import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Point extends Common {
    private Long pointId;
    private Long userId;
    private int point; // 포인트 총액

    public Point(Long userId, int point) {
        this.userId = userId;
        this.point = point;
    }

    public Point addPoint(int point) {
        return new Point(userId, this.point + point);
    }

    public Point minusPoint(int point) {
        if (point > this.point) {
            throw new InsufficientPointException("차감 금액 " + point + "가 잔액 " + this.point + "보다 큽니다.");
        }
        return new Point(userId, this.point - point);
    }
}
