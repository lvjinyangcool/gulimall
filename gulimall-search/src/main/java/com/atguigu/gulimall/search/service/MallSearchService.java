package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

@Service
public interface MallSearchService {

    /**
     * 首页搜索功能 (ES检索)
     *
     * @param param 检索的所有参数
     * @return 返回的检索结果。里面包含页面所需的所有信息
     */
    public SearchResult search(SearchParam param);


}
