package com.example.pricecompareredis.controller;

import com.example.pricecompareredis.service.LowestPriceService;
import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class LowestPriceController {

    private final LowestPriceService lowestPriceService;

    @GetMapping("/getZsetValue")
    public Set getZsetValue(String key) {
        try {
            return lowestPriceService.getZsetValue(key);
        }  catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/product")
    public int setNewProduct(@RequestBody Product product) {
        return lowestPriceService.setNewProduct(product);
    }

    @PutMapping("/productGroup")
    public int setNewProductGroup(@RequestBody ProductGrp productGrp) {
        return lowestPriceService.setNewProductGroup(productGrp);
    }

    @PutMapping("/productGrpToKeyword")
    public int setNewProductGrpToKeyword(String keyword, String productGrpID, double score) {
        return lowestPriceService.setNewProductGrpToKeyword(keyword, productGrpID, score);
    }

    @GetMapping("/productPrice/lowest")
    public Keyword getLowestPriceProductByKeyword(String keyword)  {
        return  lowestPriceService.getLowestPriceProductByKeyword(keyword);
    }
}
