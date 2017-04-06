package com.prodaas.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.prodaas.constant.Provinces;
import com.prodaas.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by guyu on 2017/3/13.
 */
@Controller
@RequestMapping("/")
public class EntryController {
    public static final String STATUS = "status";
    public static final String MSG = "msg";

    @Autowired
    private CrawlService crawlService;

    int i = 0;

    @RequestMapping("/{province}")
    @ResponseBody
    public Object getGCV(@PathVariable("province") String province) {
        if (!Provinces.containsProvince(province)) {
            DBObject r = new BasicDBObject();
            r.put(STATUS, "err");
            r.put(MSG, "province does not exists: " + province);
            return r;
        }

        DBObject result = crawlService.getGCV(province);
        System.out.println(++i+".try: "+result.get("try"));

        return result;
    }
}
