package orm;


import orm.exception.InvalidEntityException;
import orm.settings.JpaSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TableField에 대한 일급객체
 */
public class TableFields <E> {

    // allFields 중 변경된 필드를 추적하기 위한 BitSet
    private final BitSet changedFieldsBitset;
    private final List<TableField> allFields;

    public TableFields(E entity, JpaSettings settings) {
        this.allFields = initAllFields(entity, settings);
        this.changedFieldsBitset = new BitSet(allFields.size());
    }

    public List<TableField> getAllFields() {
        return allFields;
    }

    public void setFieldChanged(int index, boolean changed) {
        changedFieldsBitset.set(index, changed);
    }

    /**
     * 모든 DB 필드 추출 (연관관계 제외)
     *
     * @param entity 엔티티 클래스
     * @return List<TableField> 모든 영속성 필드
     */
    private List<TableField> initAllFields(E entity, JpaSettings settings) {
        Class<?> entityClass = entity.getClass();
        Field[] declaredFields = entityClass.getDeclaredFields();

        List<TableField> list = new ArrayList<>(declaredFields.length);

        for (Field declaredField : declaredFields) {
            var entityProperty = new EntityFieldProperty(declaredField);
            throwIfContainsTransientColumn(entityProperty, entityClass);

            if (entityProperty.isTransientAnnotated()) {
                continue;
            }

            if (entityProperty.isAssociated()) {
                continue;
            }

            list.add(createTableField(declaredField, entity, settings, entityProperty));
        }
        return list;
    }

    // id를 제외한 모든 필드 추출 (연관관계 제외)
    public List<TableField> getNonIdFields() {
        return allFields.stream()
                .filter(field -> !field.isId())
                .toList();
    }

    // 변경된 필드 추출
    public List<TableField> getChangedFields() {
        List<TableField> allFields = this.allFields;
        List<TableField> result = new ArrayList<>(allFields.size());

        for (int i = 0; i < allFields.size(); i++) {
            if (this.changedFieldsBitset.get(i)) {
                result.add(allFields.get(i));
            }
        }
        return result;
    }

    // 모든 필드를 주어진 필드로 교체한다.
    public void replaceAllFields(List<? extends TableField> newTableFields) {
        Map<String, Object> fieldValueMap = newTableFields.stream()
                .collect(Collectors.toMap(TableField::getFieldName, TableField::getFieldValue));

        for (TableField field : allFields) {
            Object fieldValue = fieldValueMap.get(field.getFieldName());
            field.setFieldValue(fieldValue);
        }
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
