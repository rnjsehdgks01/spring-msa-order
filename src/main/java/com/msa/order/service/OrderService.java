package com.msa.order.service;

import com.msa.order.domain.Orders;
import com.msa.order.dto.OrderAddRequestDto;
import com.msa.order.repository.OrderRepository;
import com.msa.order.repository.OrdersSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final int ORDER_PAGE_SIZE = 5;
    private final OrderRepository orderRepository;
    private final OrdersSpecification orderSpecification;

    @Transactional(readOnly = true)
    public Orders findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<Orders> findPagingOrders(String orderer, int offset) {
        return orderRepository.findAll(orderSpecification.searchForOrderer(orderer)
                , PageRequest.of(offset, ORDER_PAGE_SIZE, Sort.Direction.DESC, "id"));
    }

    @Transactional
    public Orders addOrder(OrderAddRequestDto order) {
        return orderRepository.save(order.toEntity());
    }

    @Transactional
    public String deleteOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        String orderer = order.getOrderer(); // delete 후 리스트 링크를 전달하기 위해 가져옴
        orderRepository.delete(order);
        return orderer;
    }
}
