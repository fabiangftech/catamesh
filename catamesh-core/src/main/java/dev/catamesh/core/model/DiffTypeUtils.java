package dev.catamesh.core.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;

final class DiffTypeUtils {

    private DiffTypeUtils() {
    }

    static boolean isValueType(Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        }

        if (Number.class.isAssignableFrom(type)) {
            return true;
        }

        if (type == Boolean.class
            || type == Character.class
            || type == String.class
            || type == BigDecimal.class
            || type == BigInteger.class) {
            return true;
        }

        if (Enum.class.isAssignableFrom(type)) {
            return true;
        }

        return Temporal.class.isAssignableFrom(type);
    }
}
