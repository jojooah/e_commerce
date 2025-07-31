# 🛒 E-Commerce 조회 성능 최적화 보고서


---

## 📑 목차

1. [주요 조회 기능 식별](#1-주요-조회-기능-식별)
2. [병목 원인 분석](#2-병목-원인-분석)
3. [최적화 방안 제안](#3-최적화-방안-제안)
    - 3.1 [쿼리 재설계](#31-쿼리-재설계)
    - 3.2 [인덱스 설계](#32-인덱스-설계)
    - 3.3 [기타 최적화 기법](#33-기타-최적화-기법)
4. [검증 및 권장 사항](#4-검증-및-권장-사항)

---

## 1. 주요 조회 기능 식별

| 기능(Module)            | 매퍼 메서드                                           | SQL                                             |
|-------------------------|-------------------------------------------------------|---------------------------------------------------|
| ** 단건 주문 조회**     | `OrderMapper.selectOrderById(orderId)`                | `SELECT … FROM orders o LEFT JOIN order_item i …` |
| ** 결제 정보 조회**     | `PaymentMapper.selectPaymentById(paymentId)`          | `SELECT … FROM payment WHERE payment_id = #{id}`  |
| ** 결제 이력 조회**     | `PaymentHistoryMapper.selectByPaymentId(id)`          | `SELECT * FROM payment_history WHERE payment_id = #{id}` |
| ** 사용자 포인트 조회**   | `PointMapper.findPointByUserId(userId)`               | `SELECT point FROM point WHERE user_id = #{userId}` |

---

## 2. 병목 원인 분석

### 2.1 주문 조회 (`selectOrderById`)
- **문제점**
    - `orders ⇆ order_item` 조인 시 `order_item.order_id`에 인덱스 미존재 → 풀 테이블 스캔
    - MyBatis 컬렉션 매핑 시 N+1 쿼리 발생 가능
- **징후**
    - 주문 항목이 많을 때 응답 지연
    - EXPLAIN 결과 `order_item` 테이블 `ALL` 스캔

### 2.2 결제 이력 조회 (`payment_history`)
- **문제점**
    - `payment_history.payment_id`에 인덱스 미존재 → 전체 스캔
- **징후**
    - 히스토리 건수가 많으면 DB CPU 사용률 급증

### 2.3 만약 like 조건을 쓴다면 (`LIKE '%…%'`)
- **문제점**
    - 와일드카드(`%`) 양쪽 사용 시 인덱스 미스 → 전체 스캔
- **징후**
    - 대량 상품 검색 시 문자열 매칭 비용 급증

---

## 3. 최적화 방안 제안

### 3.1 쿼리 재설계

1. **조인 분리 호출**
   ```sql
   -- Before
   SELECT o.*, i.*
     FROM orders o
     LEFT JOIN order_item i ON o.order_id = i.order_id
    WHERE o.order_id = :orderId;

   -- After
   -- 1) 주문 메타
   SELECT order_id, user_id, payment_status, reg_date, upd_date
     FROM orders
    WHERE order_id = :orderId;

   -- 2) 주문 항목
   SELECT order_item_id, order_id, product_id, unit_price, quantity
     FROM order_item
    WHERE order_id = :orderId;
### 3.2 인덱스 설계

| 테이블            | 컬럼             | 제안 인덱스                                | 비고                                         |
|-------------------|------------------|-------------------------------------------|----------------------------------------------|
| `orders`          | `user_id`        | `idx_orders_user(user_id, order_id)`      | 사용자별 주문 목록 조회 최적화               |
| `order_item`      | `order_id`       | `idx_item_order(order_id)`                | FK 조인 가속                                 |
| `payment`         | `user_id`        | `idx_payment_user(user_id)`               | 사용자별 결제 내역 조회 가속                 |
| `payment_history` | `payment_id`     | `idx_phistory_payment(payment_id)`        | 결제별 이력 빠른 조회                        |
| `product`         | `product_code`   | `uniq_prod_code(product_code)`            | 코드 기반 직접 조회                         |
|                   | `name`           | `FULLTEXT(name)`                          | 와일드카드 검색용 (MySQL InnoDB Fulltext)    |
| `point`           | `user_id`        | `idx_point_user(user_id)`                 | 포인트 조회 가속                             |

> **Tip:** 자주 쓰는 조합 조건에는 복합 인덱스(`(user_id, payment_status)`, `(order_id, product_id)` 등) 추가 고려  
