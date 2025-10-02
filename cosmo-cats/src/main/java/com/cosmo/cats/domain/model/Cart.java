package com.cosmo.cats.domain.model;

import java.util.List;

public class Cart {
    private String id;
    private List<String> productIds;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getProductIds() { return productIds; }
    public void setProductIds(List<String> productIds) { this.productIds = productIds; }
}
