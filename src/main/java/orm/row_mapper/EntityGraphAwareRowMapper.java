package orm.row_mapper;

import jdbc.RowMapper;
import orm.EntityFieldProperty;
import orm.TableEntity;
import orm.TableField;
import orm.assosiation.RelationField;
import orm.assosiation.RelationFields;
import orm.exception.RowMapperException;
import orm.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 엔티티 연관관계를 인지한 row mapper
 *
 * @param <T>
 */
public class EntityGraphAwareRowMapper<T> implements RowMapper<T> {

    private final Class<T> type;
    private final TableEntity<T> tableEntity;
    private final RelationFields relationFields;
    private final Map<Object, T> resultIdMap;

    public EntityGraphAwareRowMapper(TableEntity<T> tableEntity, RelationFields relationFields) {
        this.type = tableEntity.getTableClass();
        this.tableEntity = tableEntity;
        this.relationFields = relationFields;

        this.resultIdMap = new HashMap<>();
    }

    public T mapRow(ResultSet rs) throws RowMapperException {
        try {
            String idFieldName = tableEntity.getId().getFieldName();
            Object idValue = rs.getObject(idFieldName);
            T entity = mapRowIntoEntity(rs, idValue);

            while(rs.next()) {
                Object newIdValue = rs.getObject(idFieldName);
                if (newIdValue.equals(idValue)) {
                    entity = mapRowIntoEntity(rs, idValue);
                    continue;
                }

                // 다음 row의 id값이 바뀌면 엔티티 매핑 종료
                return entity;
            }

            // 다음 row 가 없을 경우 매핑된 엔티티 반환
            return entity;
        } catch (Exception e) {
            throw new RowMapperException("Failed to map row to " + type.getName(), e);
        }
    }

    /**
     * resultSet의 하나의 row를 엔티티로 매핑한다.
     */
    private T mapRowIntoEntity(ResultSet rs, Object idValue) {
        // 루트 엔티티 매핑
        T rootEntity = resultIdMap.get(idValue);
        if (rootEntity == null) {
            rootEntity = createRootEntityInstance(rs);
            resultIdMap.put(idValue, rootEntity);
        }

        // 연관관계 매핑
        Field[] relationFields = filterRelationField(rootEntity);
        for (Field relationField : relationFields) {
            mapRelationFieldsInRow(rs, rootEntity, relationField);
        }

        return rootEntity;
    }

    private Field[] filterRelationField(T rootEntity) {
        return Arrays.stream(rootEntity.getClass().getDeclaredFields())
                .filter(field -> new EntityFieldProperty(field).isRelationAnnotation())
                .toArray(Field[]::new);
    }

    /**
     * 연관관계 필드를 매핑한다.
     * OneToMany 연관관계는 루트 엔티티에 List를 생성하고, 하나의 row단위로만 연관관계를 세팅한다.
     *
     * @param rs            ResultSet
     * @param rootEntity    루트 엔티티
     * @param relationField 연관관계 필드
     */
    private void mapRelationFieldsInRow(ResultSet rs, T rootEntity, Field relationField) {
        EntityFieldProperty entityFieldProperty = new EntityFieldProperty(relationField);
        if (!entityFieldProperty.isOneToManyAssociated()) {
            return;
        }

        relationField.setAccessible(true);

        try {
            List<Object> list = (List<Object>) relationField.get(rootEntity);
            if (list == null) {
                list = new ArrayList<>();
                relationField.set(rootEntity, list);
            }

            Class<?> relationClass = ReflectionUtils.extractGenericSignature(relationField);
            Object relationEntity = createRelationEntityInstance(rs, relationClass);
            list.add(relationEntity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // 루트 엔티티에 대한 값을 매핑
    public T createRootEntityInstance(ResultSet rs) {
        try {
            T rootEntity = type.getDeclaredConstructor().newInstance();

            // 루트 엔티티 << 컬럼명, 필드 >> 매핑
            Map<String, TableField> classFieldMap = tableEntity.getAllFields().stream()
                    .collect(Collectors.toMap(TableField::getClassFieldName, Function.identity()));

            mapColumnToEntity(rs, classFieldMap, rootEntity, tableEntity.getAliasName());
            return rootEntity;
        } catch (Exception e) {
            throw new RowMapperException("Failed to create new instance of " + type.getName(), e);
        }
    }

    // 연관관계 엔티티에 대한 값을 매핑
    public Object createRelationEntityInstance(ResultSet rs, Class<?> relationType) {
        try {
            Object relationEntity = relationType.getDeclaredConstructor().newInstance();
            RelationField relationField = relationFields.getRelationFieldsOfType(relationEntity.getClass());
            TableEntity<?> joinTableEntity = relationField.getJoinTableEntity();

            // 연관 엔티티 << 컬럼명, 연관관계 필드 >> 매핑
            Map<String, TableField> classFieldMap = joinTableEntity.getAllFields().stream()
                    .collect(Collectors.toMap(TableField::getClassFieldName, Function.identity()));

            mapColumnToEntity(rs, classFieldMap, relationEntity, joinTableEntity.getAliasName());
            return relationEntity;
        } catch (Exception e) {
            throw new RowMapperException("Failed to create new instance of " + type.getName(), e);
        }
    }

    // resultSet 안에 엔티티 필드들에 값을 바인딩한다.
    private void mapColumnToEntity(ResultSet rs, Map<String, TableField> classFieldMap, Object relationEntity, String tableAlias) throws IllegalAccessException, SQLException {
        Field[] fields = relationEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            var entityProperty = new EntityFieldProperty(field);
            if (entityProperty.isRelationAnnotation()) {
                continue;
            }

            TableField tableField = classFieldMap.get(field.getName());
            if (tableField != null) {
                field.setAccessible(true);
                field.set(relationEntity, rs.getObject("%s_%s".formatted(tableAlias, tableField.getFieldName())));
            }
        }
    }
}

