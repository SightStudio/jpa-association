package orm;

import jakarta.persistence.*;

import java.lang.reflect.Field;

public record EntityFieldProperty(
        Field field,
        boolean isTransientAnnotated,
        boolean isColumnAnnotated,
        boolean isIdAnnotated,
        boolean isAssociated
) {
    public EntityFieldProperty(Field field) {
        this(
                field,
                field.isAnnotationPresent(Transient.class),
                field.isAnnotationPresent(Column.class),
                field.isAnnotationPresent(Id.class),
                isAssociated(field)
        );
    }

    private static boolean isAssociated(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return true;
        }

        if (field.isAnnotationPresent(ManyToOne.class)) {
            return true;
        }

        if (field.isAnnotationPresent(OneToOne.class)) {
            return true;
        }

        if (field.isAnnotationPresent(ManyToMany.class)) {
            return true;
        }

        return false;
    }

    // @Transient와 @Column이 동시에 존재하는 경우
    public boolean hasConflictTransientColumn() {
        return isTransientAnnotated && isColumnAnnotated;
    }
}
