package orm.assosiation;

import jakarta.persistence.FetchType;
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
