package orm.assosiation;

import jakarta.persistence.FetchType;

public class RelationPath {

    private final JoinType joinType;
    private final FetchType fetchType;

    public RelationPath(FetchType fetchType, JoinType joinType) {
        this.fetchType = fetchType;
        this.joinType = joinType;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public JoinType getJoinType() {
        return joinType;
    }
}
