package com.hxf.search.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxf.doc.ProductDoc;
import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Product;
import com.hxf.search.service.SearchService;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;

import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R search(ProductSearchParam productSearchParam) {

        SearchRequest searchRequest = new SearchRequest("product");

        String search = productSearchParam.getSearch();

        if (StringUtils.isEmpty(search)) {
            searchRequest.source().query(QueryBuilders.matchAllQuery());
        } else {
            searchRequest.source().query(QueryBuilders.matchQuery("all",productSearchParam.getSearch()));
        }

        searchRequest.source().from((productSearchParam.getCurrentPage()-1)*productSearchParam.getPageSize());
        searchRequest.source().size(productSearchParam.getPageSize());

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("????????????");
        }

        //???????????????
        //?????????????????????
        SearchHits hits = searchResponse.getHits();
        //?????????????????????
        long total = hits.getTotalHits().value;

        SearchHit[] hitsHits = hits.getHits();

        List<Product> productList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (SearchHit hitsHit : hitsHits) {
            //????????????json??????
            String sourceAsString = hitsHit.getSourceAsString();
            Product product = null;
            try {
                product = objectMapper.readValue(sourceAsString, Product.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            productList.add(product);
        }

        R r = R.ok(null,productList,total);
        log.info("SearchServiceImpl.search????????????,??????:{}",r);
        System.out.println(r);
        return r;
    }

    @Override
    public R save(Product product) throws IOException {
        IndexRequest indexRequest = new IndexRequest("product").id(product.getProductId().toString());

        ProductDoc productDoc = new ProductDoc(product);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(productDoc);

        indexRequest.source(json, XContentType.JSON);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        return R.ok("??????????????????");
    }

    @Override
    public R remove(Integer productId) throws IOException {
        DeleteRequest request = new DeleteRequest("product").id(productId.toString());

        restHighLevelClient.delete(request,RequestOptions.DEFAULT);
        return R.ok("es????????????????????????!");
    }
}
