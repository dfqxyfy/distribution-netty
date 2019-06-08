package cn.ccs.dist.service;


import cn.ccs.dist.reg.zookeeper.RegClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RegInfoService {

    @Autowired
    private RegClient regClient;


    public String getReg(){
        return regClient.getLeastChild();
    }

}
