package orm.meta;

import jakarta.persistence.*;
import orm.ColumnMeta;
import orm.exception.NotYetImplementedException;
import orm.exception.RelationAnnotationNotFoundException;
import orm.settings.JpaSettings;

import java.lang.reflect.Field;

public final class EntityFieldMeta {

    private final Field field;
    private final JpaSettings jpaSettings;

    // @Column을 고려한 실제 필드명
    private final String fieldName;

    // @Column을 고려하지 않은 엔티티 필드들의 자바 필드명
    private final String classFieldName;

    // @Column 어노테이션 메타데이터
    private final ColumnMeta columnMeta;

    public EntityFieldMeta(Field field, JpaSettings jpaSettings) {
        this.jpaSettings = jpaSettings;
        this.field = field;
        this.fieldName = extractFieldName(field);
        this.classFieldName = field.getName();
        this.columnMeta = ColumnMeta.from(field.getAnnotation(Column.class));
    }

    public EntityFieldMeta(Field field) {
        this(field, JpaSettings.ofDefault());
    }

    // @Transient와 @Column이 동시에 존재하는 경우
    public boolean hasConflictTransientColumn() {
        return isTransientAnnotated() && isColumnAnnotated();
    }

    public boolean hasRelationAnnotation() {
        return isOneToOneAssociated() || isOneToManyAssociated() || isManyToOneAssociated() || isManyToManyAssociated();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getClassFieldName() {
        return classFieldName;
    }

    public boolean isTransientAnnotated() {
        return field.isAnnotationPresent(Transient.class);
    }

    public boolean isColumnAnnotated() {
        return field.isAnnotationPresent(Column.class);
    }

    public boolean isIdAnnotated() {
        return field.isAnnotationPresent(Id.class);
    }

    public boolean isOneToOneAssociated() {
        return field.isAnnotationPresent(OneToOne.class);
    }

    public boolean isOneToManyAssociated() {
        return field.isAnnotationPresent(OneToMany.class);
    }

    public boolean isManyToOneAssociated() {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public boolean isManyToManyAssociated() {
        return field.isAnnotationPresent(ManyToMany.class);
    }

    public ColumnMeta getColumnMeta() {
        return columnMeta;
    }

    public Field getField() {
        return field;
    }

    public FetchType getFetchType() {
        if (!this.hasRelationAnnotation()) {
            throw new RelationAnnotationNotFoundException("%s는 연관관계 필드가 아닙니다.".formatted(field.getName()));
        }

        if(isOneToManyAssociated()) {
            return field.getAnnotation(OneToMany.class).fetch();
        }

        throw new NotYetImplementedException("getFetchType()에서 OneToMany를 제외한 나머지는 구현되지 않았습니다.");
    }

    private String extractFieldName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return jpaSettings.getNamingStrategy().namingColumn(column, field);
    }
}
