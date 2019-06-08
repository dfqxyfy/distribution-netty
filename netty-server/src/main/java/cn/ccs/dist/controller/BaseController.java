package cn.ccs.dist.controller;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    protected String success(Object obj){
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",obj);
        return JSON.toJSONString(map);
    }
}
