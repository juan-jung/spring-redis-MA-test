package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

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
        myProdPriceRedisTemplate.opsForZSet().add(product.getProdGrpId(), product.getProductId(), product.getPrice());
        int rank = myProdPriceRedisTemplate.opsForZSet().rank(product.getProdGrpId(),  product.getProductId()).intValue();
        return rank;
    }

    @Override
    public int setNewProductGroup(ProductGrp productGrp) {
        List<Product> products = productGrp.getProductList();
        String productId = products.get(0).getProductId();
        int price = products.get(0).getPrice();
        myProdPriceRedisTemplate.opsForZSet().add(productGrp.getProdGrpId(), productId, price);
        int productCnt = myProdPriceRedisTemplate.opsForZSet().zCard(productGrp.getProdGrpId()).intValue();
        return productCnt;
    }

    public int setNewProductGrpToKeyword(String keyword, String prodcutGrpId,  double score) {
        myProdPriceRedisTemplate.opsForZSet().add(keyword,  prodcutGrpId, score);
        int rank = myProdPriceRedisTemplate.opsForZSet().rank(keyword, prodcutGrpId).intValue();
        return rank;
    }

    @Override
    public Keyword getLowestPriceProductByKeyword(String keyword) {
        Keyword returnInfo = new Keyword();
        List<ProductGrp> tempProdGrp = new ArrayList<>();
        tempProdGrp = getProdGrpUsingKeyword(keyword);

        //keyword를 통해 productGroup  가져오기(10개)
        returnInfo.setKeyword(keyword);
        returnInfo.setProductGrpList(tempProdGrp);

        return returnInfo;
    }

    public List<ProductGrp> getProdGrpUsingKeyword(String keyword)  {
        List<ProductGrp> returnInfo = new ArrayList<>();
        //input keyword로 productGrpId 조회
        List<String> prodGrpIdList = new ArrayList<>();
        prodGrpIdList = List.copyOf(myProdPriceRedisTemplate.opsForZSet().reverseRange(keyword, 0, 9));
        List<Product> tempProdList = new ArrayList<>();
        //prodcutGruopId로 Product:price 가져오기 (10개)
        for(final  String prodGrpId : prodGrpIdList) {
            ProductGrp tempProdGrp = new ProductGrp();

            Set prodAndPriceList  = new HashSet();
            prodAndPriceList =  myProdPriceRedisTemplate.opsForZSet().rangeWithScores(prodGrpId, 0 , 9);

            //product obj bind
            Iterator<Object>  prodPriceObj = prodAndPriceList.iterator();
            while(prodPriceObj.hasNext()) {
                ObjectMapper objectMapper = new ObjectMapper();
                //{"value" :00-000-}, {"score" : 11000}
                Map<String, Object> prodPriceMap  =  objectMapper.convertValue(prodPriceObj.next(), Map.class);
                Product tempProduct = new Product();

                //Product obj bind
                tempProduct.setProductId(prodPriceMap.get("value").toString());
                tempProduct.setPrice(Double.valueOf(prodPriceMap.get("score").toString()).intValue());
                tempProdList.add(tempProduct);
            }
            tempProdGrp.setProdGrpId(prodGrpId);
            tempProdGrp.setProductList(tempProdList);
            returnInfo.add(tempProdGrp);
        }

        return returnInfo;
    }


}
