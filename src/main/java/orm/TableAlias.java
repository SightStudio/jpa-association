package orm;

import java.util.Random;

public record TableAlias(
        String tableName,
        String alias
) {

    // table alias를 지정하지 않으면 랜덤으로 만든다.
    public TableAlias(String tableName) {
        this(tableName, "%s_%s".formatted(tableName, new Random().nextInt(1000)));
    }
}
