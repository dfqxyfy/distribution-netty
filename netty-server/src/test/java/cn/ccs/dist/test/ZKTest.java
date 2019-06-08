package cn.ccs.dist.test;

import org.I0Itec.zkclient.ZkClient;

public class ZKTest {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("localhost:2181",5000);
        System.out.println(zkClient.exists("/servers/localhost_8899/nums"));
        final Object nums = zkClient.readData( "/servers/localhost_8899/nums");
        System.out.println("************************************8");
        System.out.println(nums);
        System.out.println("************************************8");
        zkClient.close();
    }

}
