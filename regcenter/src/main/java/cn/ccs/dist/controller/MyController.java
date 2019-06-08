package cn.ccs.dist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @RequestMapping("getDividends.php")
    public String dividends(String addr){
        //return HttpClient.get("https://btcdiv.com/getDividends.php?addr=aaa",new HashMap<>(),new HashMap<>());

      return "";
    }

    @RequestMapping("restart")
    public String deploy(){
        System.out.println("restart...................");
        try {
            //Runtime.getRuntime().exec("/ccs/app/start.sh");
        } catch (Exception e) {
            System.out.println("restart error...................");
            e.printStackTrace();
            return "false";
        }
        return "success";
    }
}
