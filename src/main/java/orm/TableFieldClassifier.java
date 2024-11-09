package orm;

import orm.assosiation.RelationField;
import orm.assosiation.RelationFields;
import orm.exception.InvalidEntityException;
import orm.exception.NotYetImplementedException;
import orm.settings.JpaSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 엔티티에서 테이블 필드와 연관관계 필드를 분류하는 클래스
 * @param <E> 엔티티 제네릭
 */
public class TableFieldClassifier<E> {

    private TableFields tableFields;
    private RelationFields relationFields;

    public TableFieldClassifier(E entity, JpaSettings settings) {
        classifyAllFields(entity, settings);
    }

    public TableFieldClassifier(E entity) {
        classifyAllFields(entity, JpaSettings.ofDefault());
    }

    public RelationFields getRelationFields() {
        return relationFields;
    }

    public TableFields getTableFields() {
        return tableFields;
    }

    /**
     * 모든 DB 필드를 연관관계와 연관관계가 아닌 필드로 분리한다.
     */
    private void classifyAllFields(E entity, JpaSettings settings) {
        Class<?> entityClass = entity.getClass();
        Field[] declaredFields = entityClass.getDeclaredFields();

        // 연관관계가 아닌 필드
        List<TableField> columnList = new ArrayList<>(declaredFields.length);

        // 연관관계 필드
        List<RelationField> association = new ArrayList<>(declaredFields.length);

        for (Field declaredField : declaredFields) {
            var entityProperty = new EntityFieldProperty(declaredField);
            throwIfContainsTransientColumn(entityProperty, entityClass);

            if (entityProperty.isTransientAnnotated()) {
                continue;
            }

            // 연관 관계 분류 - 단일
            if (entityProperty.isManyToOneAssociated()) {
                throw new NotYetImplementedException("ManyToOne은 사용하지 않음");
            }

            // 연관 관계 분류 - 다중
            if (entityProperty.isOneToManyAssociated()) {
                association.add(RelationField.ofOneToManyRelation(declaredField, settings));
                continue;
            }

            // 일반 필드 분류
            columnList.add(createTableField(declaredField, entity, settings, entityProperty));
        }

        this.tableFields = new TableFields(columnList);
        this.relationFields = new RelationFields(association);
    }

    // @Transient와 @Column이 동시에 존재하는 경우 금지
    private void throwIfContainsTransientColumn(EntityFieldProperty entityFieldProperty, Class<?> entityClass) {
        if (entityFieldProperty.hasConflictTransientColumn()) {
            throw new InvalidEntityException(String.format(
                    "class %s @Transient & @Column cannot be used in same field"
                    , entityClass.getName())
            );
        }
    }

    // @Id가 존재 여부에 따라 테이블 필드 생성
    private TableField createTableField(Field declaredField, E entity, JpaSettings settings, EntityFieldProperty entityProperty) {
        if (entityProperty.isIdAnnotated()) {
            return new TablePrimaryField(declaredField, entity, settings);
        }

        return new TableField(declaredField, entity, settings);
    }
}
