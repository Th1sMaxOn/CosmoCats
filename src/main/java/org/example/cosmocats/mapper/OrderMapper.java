package org.example.cosmocats.mapper;

import org.example.cosmocats.domain.Order;
import org.example.cosmocats.domain.OrderLine;
import org.example.cosmocats.repository.entity.OrderEntity;
import org.example.cosmocats.repository.entity.OrderLineEntity;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.example.cosmocats.web.dto.OrderLineDto;
import org.example.cosmocats.web.dto.OrderResponseDto; // <--- Додай імпорт
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, CustomerMapper.class})
public interface OrderMapper {

    // === Entity <-> Domain (Старе без змін) ===
    Order toDomain(OrderEntity entity);
    OrderEntity toEntity(Order domain);
    OrderLine toDomain(OrderLineEntity entity);
    @Mapping(target = "order", ignore = true)
    OrderLineEntity toEntity(OrderLine domain);

    @AfterMapping
    default void linkLines(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getLines() != null) {
            orderEntity.getLines().forEach(line -> line.setOrder(orderEntity));
        }
    }

    // === Domain -> DTO  ===

    // Request -> Domain
    @Mapping(target = "customer.email", source = "customerEmail")
    @Mapping(target = "customer.fullName", source = "customerFullName")
    @Mapping(target = "lines", source = "lines")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "customer.id", ignore = true)
    Order toDomain(CreateOrderRequest request);

    // DTO -> Domain (Line)
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "priceAtPurchase", source = "priceAtPurchase")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product.name", ignore = true)
    @Mapping(target = "product.description", ignore = true)
    @Mapping(target = "product.price", ignore = true)
    @Mapping(target = "product.category", ignore = true)
    OrderLine toDomain(OrderLineDto dto);

    // Domain -> Response DTO
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "customerFullName", source = "customer.fullName")
    @Mapping(target = "lines", source = "lines")
    @Mapping(target = "totalAmount", expression = "java(calculateTotal(domain))") // Рахуємо суму
    OrderResponseDto toDto(Order domain);

    @Mapping(target = "productId", source = "product.id")
    OrderLineDto toDto(OrderLine line);

    default BigDecimal calculateTotal(Order order) {
        if (order.getLines() == null) return BigDecimal.ZERO;
        return order.getLines().stream()
                .map(line -> line.getPriceAtPurchase().multiply(BigDecimal.valueOf(line.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}