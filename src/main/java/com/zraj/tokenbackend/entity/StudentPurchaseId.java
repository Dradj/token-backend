package com.zraj.tokenbackend.entity;

import java.io.Serializable;
import java.util.Objects;

public class StudentPurchaseId implements Serializable {
    private Long studentId;
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StudentPurchaseId that)) return false;
        return Objects.equals(studentId, that.studentId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, productId);
    }
}
