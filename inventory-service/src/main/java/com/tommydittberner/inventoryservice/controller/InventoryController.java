package com.tommydittberner.inventoryservice.controller;

import com.tommydittberner.inventoryservice.dto.InventoryResponse;
import com.tommydittberner.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam(name = "skuCode") List<String> skuCodeList) {
        return inventoryService.isInStock(skuCodeList);
    }

}
