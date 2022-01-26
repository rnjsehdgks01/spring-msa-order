package com.msa.order.controller.api;

import com.msa.order.controller.converter.EntityToModelConverter;
import com.msa.order.domain.Orders;
import com.msa.order.dto.OrderAddRequestDto;
import com.msa.order.service.OrderService;
import com.msa.order.service.ProductRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/orders")
@RestController
public class OrderApiController {
    private final EntityToModelConverter entityToModelConverter;
    private final ProductRemoteService productRemoteService;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<EntityModel<Orders>> addOrder(@RequestBody @Valid OrderAddRequestDto entity) {
        // product에서 exception 발생 시 exception 정보 return됨
        productRemoteService.purchaseProduct(entity.getProductId(), entity.getAmount());
        Orders order = orderService.addOrder(entity);
        // 여기 말고 service에서 순서 정해줘야 함. product transaction 순서 등
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entityToModelConverter.toModel(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrder(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityToModelConverter.getListLink(orderService.deleteOrder(id)));
    }
}
