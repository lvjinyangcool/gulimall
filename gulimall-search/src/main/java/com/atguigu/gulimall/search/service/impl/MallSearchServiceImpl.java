package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.GuliMallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.BrandVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param){
        SearchResult result = new SearchResult();
        //1.??????????????????DSL??????
        SearchRequest request = this.buildSearchRequest(param);
        try {
            //2.??????????????????
            SearchResponse response = restHighLevelClient.search(request, GuliMallElasticSearchConfig.COMMON_OPTIONS);
            //3.????????????????????????????????????????????????
            result = this.buildSearchResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ??????ES????????????
     *
     * @param param ????????????
     * @return ??????
     */
    private SearchRequest buildSearchRequest(SearchParam param){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         *  1. ???????????? ??????(??????????????????????????????????????????????????????) ?????????????????????Query
         */
        //1.1 must -- ????????????
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 1.2 bool - filter Catalog3Id  ????????????????????????
        if(param.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 1.3 bool - filter brandId   ????????????ID??????
        if(!CollectionUtils.isEmpty(param.getBrandId())){
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 1.4 bool - filter attrs    ??????????????????
        if(!CollectionUtils.isEmpty(param.getAttrs())){
            /**
             * attrs=1_??????:??????&attrs=2_5???:6???
             */
            for(String attr : param.getAttrs()){
                String[] s = attr.split("_");
                String attrId = s[0];   //?????????id
                String[] attrValues = s[1].split(":"); //??????????????????
                BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();
                nestBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                //??????????????????????????????nested??????
                boolQuery.filter(QueryBuilders.nestedQuery("attrs",nestBoolQuery, ScoreMode.None));
            }
        }
        // 1.5 bool - filter attrs hasStock  ???????????????????????????
        if(param.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }
        // 1.6 bool - filter attrs skuPrice   ??????sku??????????????????
        if(StringUtils.isNotBlank(param.getSkuPrice())){
            /**
             * ????????????
             * skuPrice=1_500/_500/500_
             */
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = param.getSkuPrice();
            if(skuPrice.indexOf("_") == 0){
                //_500
                rangeQueryBuilder.to(skuPrice.substring(skuPrice.indexOf("_")+1));
            }else if(skuPrice.indexOf("_") == skuPrice.length() - 1){
                //500_
                rangeQueryBuilder.from(skuPrice.substring(0,skuPrice.indexOf("_")));
            }else {
                //1_500
                rangeQueryBuilder.from(skuPrice.substring(0,skuPrice.indexOf("_"))).to(skuPrice.substring(skuPrice.indexOf("_")+1));
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        sourceBuilder.query(boolQuery);

        /**
         * 2.????????????????????????
         */
        //2.1 ??????
        if(StringUtils.isNotBlank(param.getSort())){
            String sort = param.getSort();
            //sort=saleCount_asc
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }
        //2.2??????  param.getPageNum() ?????????  from: ????????????
        sourceBuilder.from((param.getPageNum()-1) * EsConstant.PRODUCT_PAGE_SIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        // 3.??????
        if(StringUtils.isNotBlank(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        /**
         * 3.??????
         */
        //3.1 ????????????
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //????????????????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);

        //3.2 ????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);

        //3.3 ????????????
        NestedAggregationBuilder nested_attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.3.0 ????????????????????????attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 3.3.1 ?????????????????????attrId?????????attrName
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 3.3.2 ?????????????????????attrId?????????????????????????????????attrValue	???????????????????????????????????? ?????????50
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        // 3.3.3 ???????????????????????????????????????
        nested_attr_agg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(nested_attr_agg);

        log.info("\n???????????????->\n" + sourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * ???ES?????????????????????SearchResult??????
     *
     * @param response ES????????????
     * @param param ????????????
     * @return SearchResult
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param){
        SearchResult searchResult = new SearchResult();
        List<SkuEsModel> products = new ArrayList<>();
        SearchHits hits = response.getHits();
        if(hits.getHits() != null && hits.getHits().length > 0){
            for(SearchHit hit : hits.getHits()){
                String source = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(source, SkuEsModel.class);
                if(StringUtils.isNotBlank(param.getKeyword())){
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    String skuTitle = highlightFields.get("skuTitle").getFragments()[0].string();
                    skuEsModel.setSkuTitle(skuTitle);
                }
                products.add(skuEsModel);
            }
        }
        //1.???????????????????????????
        searchResult.setProducts(products);

        //2.???????????????????????????????????????
        List<SearchResult.AttrVO> attrVOList = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id.getBuckets()) {
            SearchResult.AttrVO attrVo = new SearchResult.AttrVO();
            // 2.1 ???????????????id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            // 2.2 ?????????????????????
            String attr_name = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            // 2.3 ????????????????????????
            List<? extends Terms.Bucket> list = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets();

            List<String> attr_value = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attr_value);
            attrVOList.add(attrVo);
        }
        searchResult.setAttrVOS(attrVOList);

        //3.???????????????????????????????????????
        List<SearchResult.BrandVO> brandVOList = new ArrayList<>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVO brandVo = new SearchResult.BrandVO();
            // 3.1 ???????????????id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            // 3.2 ??????????????????
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            // 3.3 ?????????????????????
            String brandImg = ((ParsedStringTerms) (bucket.getAggregations().get("brand_img_agg"))).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVOList.add(brandVo);
        }
        searchResult.setBrandVOS(brandVOList);

        //4.???????????????????????????????????????
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVO> catalogVOList = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets){
            SearchResult.CatalogVO catalogVO = new SearchResult.CatalogVO();
            //????????????id
            catalogVO.setCatalogId(bucket.getKeyAsNumber().longValue());
            //?????????????????????
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVO.setCatalogName(catalogName);
            catalogVOList.add(catalogVO);
        }
        searchResult.setCatalogVOS(catalogVOList);

        // 5.????????????
        searchResult.setPageNum(param.getPageNum());
        // 6. ????????????
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        // 7. ?????????
        Long totalPages = total % EsConstant.PRODUCT_PAGE_SIZE == 0 ? total / EsConstant.PRODUCT_PAGE_SIZE : total / EsConstant.PRODUCT_PAGE_SIZE+1;
        searchResult.setTotalPages(totalPages);
        // ???????????????
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1;i <= totalPages; i++){
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        // 6.???????????????????????????
        if(param.getAttrs() != null){
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.getAttrsInfo(Long.parseLong(s[0]));
                // ??????????????????????????????????????? ????????????????????????
                searchResult.getAttrIds().add(Long.parseLong(s[0]));
                if(r.getCode() == 0){
                    AttrResponseVo data = r.getData(new TypeReference<AttrResponseVo>(){});
                    navVo.setName(data.getAttrName());
                }else{
                    // ???????????????id????????????
                    navVo.setName(s[0]);
                }
                // ???????????????????????? ??????????????????
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }

        // ???????????????
        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = searchResult.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setName("??????");
            // TODO ????????????????????????
            R r = productFeignService.brandInfo(param.getBrandId());
            if(r.getCode() == 0){
                List<BrandVo> brand = r.getData("data", new TypeReference<List<BrandVo>>() {});
                StringBuilder buffer = new StringBuilder();
                // ??????????????????ID
                String replace = "";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getBrandName()).append(";");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }

        return searchResult;
    }

    /**
     * ????????????
     * key ??????????????????key
     */
    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
            // ??????????????????????????????java????????????
            encode = encode.replace("+","%20");
            encode = encode.replace("%28", "(").replace("%29",")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param.get_queryString().replace("&" + key + "=" + encode, "");
    }
}
