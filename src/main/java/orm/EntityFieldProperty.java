package orm;

import jakarta.persistence.*;

import java.lang.reflect.Field;

public record EntityFieldProperty(
        Field field,
        boolean isTransientAnnotated,
        boolean isColumnAnnotated,
        boolean isIdAnnotated,
        boolean isOneToOneAssociated,
        boolean isOneToManyAssociated,
        boolean isManyToOneAssociated,
        boolean isManyToManyAssociated
) {
    public EntityFieldProperty(Field field) {
        this(
                field,
                field.isAnnotationPresent(Transient.class),
                field.isAnnotationPresent(Column.class),
                field.isAnnotationPresent(Id.class),
                field.isAnnotationPresent(OneToOne.class),
                field.isAnnotationPresent(OneToMany.class),
                field.isAnnotationPresent(ManyToOne.class),
                field.isAnnotationPresent(ManyToMany.class)
        );
    }

    // @Transient와 @Column이 동시에 존재하는 경우
    public boolean hasConflictTransientColumn() {
        return isTransientAnnotated && isColumnAnnotated;
    }
}
