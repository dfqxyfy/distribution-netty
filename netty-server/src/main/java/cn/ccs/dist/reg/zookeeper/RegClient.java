package cn.ccs.dist.reg.zookeeper;

import cn.ccs.dist.reg.entity.RegInfoProperties;
import cn.ccs.dist.reg.entity.ServerStatus;
import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by szekinwin on 2017/7/8.
 */
@Configuration
public class RegClient {

    private RegInfoProperties regInfo;
    //zookeeper连接地址
    private static final String CONNECT_ADR="localhost:2181";

    //5000，连接超时时间
    static final ZkClient zkClient = new ZkClient(CONNECT_ADR,5000);

    private String basServerPath;

    public RegClient(RegInfoProperties regInfo) {
        this.regInfo = regInfo;
    }
    public void init(){
        basServerPath = "/servers/" + regInfo.getNettyServer() + "_" + regInfo.getNettyPort();
        //服务器信息创建
        if(!zkClient.exists(basServerPath))
            zkClient.createPersistent(basServerPath, true);
        if(!zkClient.exists(basServerPath + "/serverInfo"))
            zkClient.createPersistent(basServerPath + "/serverInfo", regInfo);
        if(!zkClient.exists(basServerPath + "/status"))
            zkClient.createEphemeral(basServerPath + "/status", ServerStatus.VALID);
        if(!zkClient.exists(basServerPath + "/nums")) {
            zkClient.createPersistent(basServerPath + "/nums", 0);
        }else{
            zkClient.writeData(basServerPath + "/nums", 0);
        }
    }

    public String getLeastChild(){
        List<String> children = zkClient.getChildren("/servers");
        TreeMap<Integer,String> map  = new TreeMap<>();
        for(int i=0;i<children.size();i++){
            Object nums = zkClient.readData("/servers/"+children.get(i) + "/nums");
            map.put((int)nums,zkClient.readData("/servers/"+children.get(i)+"/serverInfo"));
        }
        return JSON.toJSONString(map.get(map.firstKey()));
    }

    public void addReg(){
        final Object nums = zkClient.readData(basServerPath + "/nums");
        int addNums=0;
        if(nums!=null){
            addNums=(int)nums+1;
            zkClient.writeData(basServerPath + "/nums",addNums);
        }
    }

    public void delReg(){
        final Object nums = zkClient.readData(basServerPath + "/nums");
        if(nums!=null){

            int addNums=(int)nums-1;
            if(addNums>=0)
                zkClient.writeData(basServerPath + "/nums",addNums);
        }
    }


}