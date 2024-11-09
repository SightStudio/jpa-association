package orm.dsl.step.dml;

public interface InnerJoinForFetchStep<E> extends WhereForFetchStep<E> {

    // 엔티티의 연관관계를 파악하여 FetchType.EAGER에 대한 조인절을 만들게 한다.
    InnerJoinForFetchStep<E> joinAll();

    // 수동으로 엔티티를 조인한다.
    <T> InnerJoinForFetchStep<E> join(T associatedEntity);

}
