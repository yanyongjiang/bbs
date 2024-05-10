package com.yyj.apps.bbs.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@Table(name="bbspost")
public class BbsPost implements Serializable {
    @AutoID
    private Long id;
    private String tm; //题名
    private String cont;  //内容
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Timestamp ngd;  //创建时间
    private String sta; //1
    private Long uid; //用户id
    private String lid;//用户登陆账号
    private String pty;//1旅游景点2IT技术3个人资料

    private List<BbsAttach> attachList;
}