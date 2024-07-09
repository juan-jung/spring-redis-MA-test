package com.example.pricecompareredis.vo;


import lombok.Data;

import java.util.List;

@Data
public class ProductGrp {
    private String prodGrpId; // FPG0001
    private List<Product> productList; // {{7fadd0bf-2ff5-488f-bcac-cf5af5452c26,25000}, {}...}
}
