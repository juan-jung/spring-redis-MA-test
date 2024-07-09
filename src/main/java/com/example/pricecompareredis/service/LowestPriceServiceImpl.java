package com.example.pricecompareredis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LowestPriceServiceImpl implements LowestPriceService{

    private final RedisTemplate myProdPriceRedisTemplate;

    @Override
    public Set getZsetValue(String key) {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedisTemplate.opsForZSet().rangeWithScores(key, 0, 9);
        return myTempSet;
    }
}
