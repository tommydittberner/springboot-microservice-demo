package com.tommydittberner.inventoryservice.service;

import com.tommydittberner.inventoryservice.dto.InventoryResponse;
import com.tommydittberner.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional(readOnly = true)
//    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        // simulate timeout
//        log.info("Wait started");
//        Thread.sleep(5001);
//        log.info("Wait ended");
        return repository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventoryItem -> InventoryResponse.builder()
                            .skuCode(inventoryItem.getSkuCode())
                            .isInStock(inventoryItem.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
