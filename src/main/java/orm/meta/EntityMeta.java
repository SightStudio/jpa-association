package orm.meta;

import orm.settings.JpaSettings;
import orm.validator.EntityValidator;

public class EntityMeta {

    private final String tableName;

    public EntityMeta(Object entity, JpaSettings jpaSettings) {
        new EntityValidator<>(entity).validate();
        this.tableName = jpaSettings.getNamingStrategy().namingTable(entity.getClass());
    }

    public String getTableName() {
        return tableName;
    }
}
