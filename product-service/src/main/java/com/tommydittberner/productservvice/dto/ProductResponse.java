package com.tommydittberner.productservvice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Model entities and DTOs should be separated as a best practice, even though they might be very similar.
 * If the model is extended with some field for business logic reasons they should not automatically get
 * exposed. With a DTO this is prevented. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
