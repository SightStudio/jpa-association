package orm.dsl.render;

import orm.TableEntity;
import orm.assosiation.RelationFields;
import orm.dsl.condition.Conditions;

public abstract class SelectRenderer<E> {

    protected final TableEntity<E> tableEntity;
    protected final Conditions conditions;
    protected final RelationFields relationFields;

    public SelectRenderer(TableEntity<E> tableEntity, Conditions conditions, RelationFields relationFields) {
        this.tableEntity = tableEntity;
        this.conditions = conditions;
        this.relationFields = relationFields;
    }

    public abstract String renderSql();
}
