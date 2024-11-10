package orm.assosiation;

import jakarta.persistence.FetchType;
import orm.exception.EntityClassTypeNotInRelationException;
import orm.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 엔티티의 연관관계 필드들에 대한 일급객체
 */
public class RelationFields {

    private final List<RelationField> relationFieldList;

    public RelationFields() {
        this.relationFieldList = Collections.emptyList();
    }

    public RelationFields(RelationFields relationField) {
        this.relationFieldList = deepCopyRelationFields(relationField);
    }

    public RelationFields(List<RelationField> relationFields) {
        this.relationFieldList = relationFields;
    }

    public List<RelationField> getRelationList() {
        return relationFieldList;
    }

    // 연관 관계 필드중 값이 있는 필드만 추출
    public List<RelationField> getValuedRelationList() {
        return relationFieldList.stream()
                .filter(RelationField::isValuedRelationField)
                .toList();
    }

    // 연관관계 필드중에 특정 클래스 타입인것들만 추출
    public RelationField getRelationFieldsOfType(Class<?> clazz) {
        return relationFieldList.stream()
                .filter(relationField -> relationField.tableEntityClass().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new EntityClassTypeNotInRelationException("연관관계 목록에 해당 엔티티 타입이 없습니다" + clazz));
    }

    // EAGER 타입의 연관관계 필드만 추출
    public List<RelationField> getEagerRelationList() {
        return relationFieldList.stream()
                .filter(relationField -> relationField.getFetchType() == FetchType.EAGER)
                .toList();
    }

    public boolean hasRelation() {
        return CollectionUtils.isNotEmpty(relationFieldList);
    }

    private List<RelationField> deepCopyRelationFields(RelationFields relationFields) {
        return relationFields.getRelationList().stream()
                .map(RelationField::new)
                .toList();
    }
}
