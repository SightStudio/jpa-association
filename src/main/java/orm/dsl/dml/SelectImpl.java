package orm.dsl.dml;

import jdbc.RowMapper;
import orm.TableEntity;
import orm.dsl.QueryRenderer;
import orm.dsl.QueryRunner;
import orm.dsl.condition.Condition;
import orm.dsl.condition.Conditions;
import orm.dsl.condition.EqualCondition;
import orm.dsl.step.dml.ConditionForFetchStep;
import orm.dsl.step.dml.SelectFromStep;
import orm.exception.NotYetImplementedException;
import orm.row_mapper.DefaultRowMapper;

import java.util.List;

public abstract class SelectImpl<E> implements SelectFromStep<E>{

    private final QueryRunner queryRunner;
    private final TableEntity<E> tableEntity;
    private final Conditions conditions;

    public SelectImpl(TableEntity<E> tableEntity, QueryRunner queryRunner) {
        this.tableEntity = tableEntity;
        this.queryRunner = queryRunner;
        this.conditions = new Conditions();
    }

    @Override
    public ConditionForFetchStep<E> where(Condition condition) {
        conditions.add(condition);
        return this;
    }

    @Override
    public ConditionForFetchStep<E> where(Condition... conditions) {
        this.conditions.addAll(List.of(conditions));
        return this;
    }

    @Override
    public String extractSql() {
        QueryRenderer queryRenderer = new QueryRenderer();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        queryBuilder.append(queryRenderer.joinColumnNamesWithComma(tableEntity.getAllFields()));
        queryBuilder.append(" FROM ");
        queryBuilder.append(tableEntity.getTableName());

        if (conditions.hasCondition()) {
            queryBuilder.append(queryRenderer.renderWhere(conditions));
        }

        return queryBuilder.toString();
    }

    @Override
    public E fetchOne(RowMapper<E> rowMapper) {
        return queryRunner.fetchOne(extractSql(), rowMapper);
    }

    // 모든 검색조건을 날려 findAll로 만듬
    @Override
    public ConditionForFetchStep<E> findAll() {
        this.conditions.clear();
        return this;
    }

    @Override
    public ConditionForFetchStep<E> findById(Object id) {
        this.conditions.clear();
        this.conditions.add(new EqualCondition(tableEntity.getId().getFieldName(), id));
        return this;
    }

    @Override
    public E fetchOne() {
        return queryRunner.fetchOne(extractSql(), new DefaultRowMapper<>(tableEntity));
    }

    @Override
    public List<E> fetch() {
        return queryRunner.fetch(extractSql(), new DefaultRowMapper<>(tableEntity));
    }
}
