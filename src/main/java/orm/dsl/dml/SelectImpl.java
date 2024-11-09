package orm.dsl.dml;

import jdbc.RowMapper;
import orm.TableEntity;
import orm.assosiation.RelationFields;
import orm.dsl.QueryRunner;
import orm.dsl.condition.Condition;
import orm.dsl.condition.Conditions;
import orm.dsl.condition.EqualCondition;
import orm.dsl.render.SelectRenderer;
import orm.dsl.render.SimpleSelectRenderer;
import orm.dsl.render.WithJoinQueryRenderer;
import orm.dsl.step.dml.ConditionForFetchStep;
import orm.dsl.step.dml.InnerJoinForFetchStep;
import orm.dsl.step.dml.SelectFromStep;
import orm.row_mapper.DefaultRowMapper;

import java.util.List;

public abstract class SelectImpl<E> implements SelectFromStep<E> {

    private final QueryRunner queryRunner;
    private final TableEntity<E> tableEntity;
    private final Conditions conditions;

    private RelationFields relationFields;

    private boolean hasJoin;

    public SelectImpl(TableEntity<E> tableEntity, QueryRunner queryRunner) {
        this.tableEntity = tableEntity;
        this.queryRunner = queryRunner;
        this.conditions = new Conditions();
        this.relationFields = new RelationFields();
        this.hasJoin = false;
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
    public ConditionForFetchStep<E> whereWithId(Object id) {
        final String idFieldName = tableEntity.getId().getFieldName();
        final String fieldName = tableEntity.hasAlias()
                ? tableEntity.getAliasName() + "." + idFieldName
                : idFieldName;

        this.conditions.clear();
        this.conditions.add(new EqualCondition(fieldName, id));
        return this;
    }

    @Override
    public String extractSql() {
        final SelectRenderer<E> selectRenderer = hasJoin
                ? new WithJoinQueryRenderer<>(tableEntity, conditions, relationFields)
                : new SimpleSelectRenderer<>(tableEntity, conditions);

        return selectRenderer.renderSql();
    }

    @Override
    public InnerJoinForFetchStep<E> joinAllEager() {
        this.hasJoin = true;
        this.tableEntity.addAliasIfNotAssigned();
        this.relationFields = new RelationFields(tableEntity.getRelationFields());
        return this;
    }

    @Override
    public <T> InnerJoinForFetchStep<E> join(T associatedEntity) {
        this.hasJoin = true;
        this.tableEntity.addAliasIfNotAssigned();
        return this;
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
