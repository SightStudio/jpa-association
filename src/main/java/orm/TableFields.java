package orm;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TableField에 대한 일급객체
 */
public class TableFields {

    // allFields 중 변경된 필드를 추적하기 위한 BitSet
    private final BitSet changedFieldsBitset;
    private final List<TableField> allFields;

    public TableFields(List<TableField> allFields) {
        this.allFields = allFields;
        this.changedFieldsBitset = new BitSet(allFields.size());
    }

    public List<TableField> getAllFields() {
        return allFields;
    }

    public void setFieldChanged(int index, boolean changed) {
        changedFieldsBitset.set(index, changed);
    }

    // id를 제외한 모든 필드 추출 (연관관계 제외)
    public List<TableField> getNonIdFields() {
        return allFields.stream()
                .filter(field -> !field.isId())
                .toList();
    }

    // 변경된 필드 추출
    public List<TableField> getChangedFields() {
        List<TableField> allFields = this.allFields;
        List<TableField> result = new ArrayList<>(allFields.size());

        for (int i = 0; i < allFields.size(); i++) {
            if (this.changedFieldsBitset.get(i)) {
                result.add(allFields.get(i));
            }
        }
        return result;
    }

    // 모든 필드를 주어진 필드로 교체한다.
    public void replaceAllFields(List<? extends TableField> newTableFields) {
        Map<String, Object> fieldValueMap = newTableFields.stream()
                .filter(tableField -> tableField.getFieldValue() != null)
                .collect(Collectors.toMap(TableField::getFieldName, TableField::getFieldValue));

        for (TableField field : allFields) {
            Object fieldValue = fieldValueMap.get(field.getFieldName());
            field.setFieldValue(fieldValue);
        }
    }

    public int size() {
        return this.getAllFields().size();
    }
}
