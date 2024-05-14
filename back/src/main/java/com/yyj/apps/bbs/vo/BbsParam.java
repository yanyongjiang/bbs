package com.yyj.apps.bbs.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BbsParam {

    private String lid; //登陆账号
    private String un;  //用户名
    private String pw;  //密码

    private String tm; //题名
    private String cont;  //内容
    private Timestamp ngd;  //创建时间
    private Long uid; //用户id

    private Long id;
    private Long pkid;
    private String fpa; //文件路径
    private String fname; //文件路径
    private Timestamp fngd;  //创建时间
    private String fs; //文件大小
    private String ftid; //文件临时id

    private Long start;
    private Long limit;
    private String searchkey;
    private String pty;

}
