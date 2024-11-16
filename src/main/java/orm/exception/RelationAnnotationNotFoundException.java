package orm.exception;

public class RelationAnnotationNotFoundException extends OrmPersistenceException {

    public RelationAnnotationNotFoundException(String message) {
        super(message);
    }

    public RelationAnnotationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
