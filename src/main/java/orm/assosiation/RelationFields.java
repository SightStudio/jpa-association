package orm.assosiation;

import jakarta.persistence.FetchType;
import orm.exception.EntityClassTypeNotInRelationException;
import orm.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

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

    public RelationField getRelationFieldsOfType(Class<?> clazz) {
        return relationFieldList.stream()
                .filter(relationField -> relationField.tableEntityClass().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new EntityClassTypeNotInRelationException("연관관계 목록에 해당 엔티티 타입이 없습니다" + clazz));
    }

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
