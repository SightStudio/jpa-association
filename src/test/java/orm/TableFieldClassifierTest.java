package orm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Order;
import persistence.sql.ddl.OrderItem;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class TableFieldClassifierTest {

    @Test
    @DisplayName("TableFieldClassifier 는 연관관계 필드와 연관관계가 아닌 필드를 분류해준다.")
    void TableFieldClassifier_테스트() {
        // given
        Order order = new Order("12131");

        // when
        var tableFieldClassifier = new TableFieldClassifier<>(order);

        // then
        assertSoftly(softly -> {
            softly.assertThat(tableFieldClassifier.getTableFields().size()).isEqualTo(2); // 연관관계 아닌 필드 2
            softly.assertThat(tableFieldClassifier.getRelationFields().size()).isEqualTo(1); // 연관관계인 필드 1
        });
    }

    @Test
    @DisplayName("TableFieldClassifier#getValuedRelationFields는 연관관계 필드중에 값이 있는 필드만 가져온다.")
    void TableFieldClassifier_테스트_2() {
        // given
        Order order = new Order("12131");
        order.addOrderItem(new OrderItem("product1", 10));
        order.addOrderItem(new OrderItem("product2", 11));

        Order singleOrder = new Order("12131");

        var classifier_연관관계_값 = new TableFieldClassifier<>(order);
        var classifier_단일 = new TableFieldClassifier<>(singleOrder);

        // when
        var result_연관관계 = classifier_연관관계_값.getValuedRelationFields();
        var result_단일 = classifier_단일.getValuedRelationFields();

        // then
        assertSoftly(softly -> {
            softly.assertThat(result_연관관계).asList().hasSize(1); // orderItems 필드 하나에만 연관관계 값 존재
            softly.assertThat(result_단일).asList().hasSize(0);
        });
    }
}
