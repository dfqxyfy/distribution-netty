package cn.ccs.dist.controller;

import cn.ccs.dist.service.PushService;
import cn.ccs.dist.service.RegInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ApiController extends BaseController {


    @Autowired
    RegInfoService regInfoService;

    @Autowired
    private PushService pushService;

    @RequestMapping("regInfo")
    public String getReg(){
        return success(regInfoService.getReg());
    }


    @RequestMapping("sendUser")
    public String send(String userId,String msg){
        return success(pushService.send(userId,msg));
    }
}
