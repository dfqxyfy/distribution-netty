package cn.ccs.dist.controller;

import cn.ccs.dist.service.RegInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ApiController extends BaseController {


    @Autowired
    RegInfoService regInfoService;

    @RequestMapping("regInfo")
    public String deploy(){
        return success(regInfoService.getReg());
    }
}
