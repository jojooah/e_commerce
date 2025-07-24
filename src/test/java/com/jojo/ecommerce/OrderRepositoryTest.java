package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.OrderRepositoryMap;
import com.jojo.ecommerce.application.dto.CreateOrderRequest;
import com.jojo.ecommerce.application.dto.ProductDto;
import com.jojo.ecommerce.application.exception.OrderNotFoundException;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.domain.model.Order;
import com.jojo.ecommerce.domain.model.OrderItem;
import com.jojo.ecommerce.domain.model.STATUS_TYPE;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderRepositoryTest {

    private OrderRepositoryPort repo;

    @BeforeEach
    void setUp() {
        repo = new OrderRepositoryMap();
    }


    @Test
    void 주문_저장_테스트() {
        Order order = new Order(42L);

        Order saved = repo.saveOrder(order);

        assertSame(order, saved, "save()는 입력한 Order 객체를 그대로 반환해야 한다");
    }


    @Test
    void 결제취소_상태_변경_테스트() throws Exception {
        //주문 저장
        Order orderMock = mock(Order.class);

        // getOrderId() 호출 시 99L 을 리턴
        when(orderMock.getOrderId()).thenReturn(99L);
        repo.saveOrder(orderMock);

        //최초 저장시 상태는 "결재 대기"
        assertEquals(STATUS_TYPE.PAYMENT_PENDING, orderMock.getPaymentStatus());

        //상태 "결제 완료" 로 변경
        orderMock.paymentCompleted();
        repo.updateOrder(orderMock);
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, repo.findByOrderId(99L));

        //상태 "결제 취소"로 변경
        orderMock.paymentCanceled();
        repo.updateOrder(orderMock);
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, repo.findByOrderId(99L));

    }

    @Test
    void 잘못된_주문ID_처리_테스트() {
        Order orderMock = mock(Order.class);

        // getOrderId() 호출 시 99L 을 리턴
        when(orderMock.getOrderId()).thenReturn(99L);

        assertThrows(OrderNotFoundException.class,
                () -> repo.updateOrder(orderMock),
                "유효하지 않은 주문ID이면 OrderNotFoundException 이 발생해야 한다");

        // getOrderId 만 호출되고, 다른 메서드는 호출되지 않았는지 검증
        verify(orderMock).getOrderId();
        verifyNoMoreInteractions(orderMock);
    }

    @Test
    void 상품_주문_만들기_테스트(){
        //상품생성
        List<ProductDto> productDtoList = new ArrayList<>();
        productDtoList.add(new ProductDto(1L, "상품1",2, 1000, 1));
        productDtoList.add(new ProductDto(2L, "상품2",2, 2000, 2));

        // 상품 주문 정보 생성
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(1L,null,3,3000,productDtoList);

        // 주문 생성
        Order order = new Order(createOrderRequest.getUserId());
        Order saved = repo.saveOrder(order);

        // 상품 주문정보 돌면서 주문 아이템 생성
        for(ProductDto productDto : createOrderRequest.getProductDtoList()){
            order.addOrderItem(new OrderItem(order.getOrderId(),productDto.getProductId(), productDto.getQauntity()));
        }

        repo.saveOrder(order);
        assertEquals(2, repo.findByOrderId(saved.getOrderId()).getOrderItems().size());

    }
}
