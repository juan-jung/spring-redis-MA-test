package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LowestPriceServiceImpl implements LowestPriceService{

    private final RedisTemplate myProdPriceRedisTemplate;

    @Override
    public Set getZsetValue(String key) {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedisTemplate.opsForZSet().rangeWithScores(key, 0, 9);
        return myTempSet;
    }

    @Override
    public int setNewProduct(Product product) {
        int rank = 0;
        myProdPriceRedisTemplate.opsForZSet().add(product.getProdGrpId(), product.getProductId(), product.getPrice());
        rank = myProdPriceRedisTemplate.opsForZSet().rank(product.getProdGrpId(),  product.getProductId()).intValue();
        return rank;
    }


}
