package orm.assosiation;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import orm.TableEntity;
import orm.settings.JpaSettings;

import java.lang.reflect.Field;

import static orm.util.ReflectionUtils.extractGenericSignature;

public class RelationField {

    private final FetchType fetchType;
    private final TableEntity<?> tableEntity;
    private final String joinColumnName;

    private RelationField(FetchType fetchType, TableEntity<?> tableEntity, String joinColumnName) {
        this.fetchType = fetchType;
        this.joinColumnName = joinColumnName;
        this.tableEntity = tableEntity;
    }

    public RelationField(RelationField relationField) {
        this.fetchType = relationField.fetchType;
        this.joinColumnName = relationField.joinColumnName;
        this.tableEntity = deepCopyTableEntity(relationField.tableEntity);
    }

    public static RelationField ofOneToManyRelation(Field field, JpaSettings settings) {
        var tableEntity = new TableEntity<>(extractGenericSignature(field), settings)
                .addAliasIfNotAssigned();

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        boolean hasJoinColumn = field.isAnnotationPresent(JoinColumn.class);

        String joinColumnName = hasJoinColumn
                ? field.getAnnotation(JoinColumn.class).name()
                : tableEntity.getId().getFieldName();

        return new RelationField(oneToMany.fetch(), tableEntity, joinColumnName);
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public TableEntity<?> getJoinTableEntity() {
        return tableEntity;
    }

    public Class<?> tableEntityClass() {
        return tableEntity.getTableClass();
    }

    public String getAliasName() {
        return tableEntity.getAliasName();
    }

    public String getTableName() {
        return tableEntity.getTableName();
    }

    private TableEntity<?> deepCopyTableEntity(TableEntity<?> tableEntity) {
        var newTableEntity = new TableEntity<>(tableEntity.getEntity());
        if (tableEntity.hasAlias()) {
            newTableEntity.addAliasIfNotAssigned(tableEntity.getAlias());
        }
        return newTableEntity;
    }
}
