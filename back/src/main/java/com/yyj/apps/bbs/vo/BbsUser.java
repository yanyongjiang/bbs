package com.yyj.apps.bbs.vo;

import lombok.Data;
import org.beetl.sql.core.annotatoin.*;

import java.io.Serializable;

@Data
@Table(name="bbsuser")
public class BbsUser implements Serializable {
    @AutoID
    private Long id;
    private String lid; //登陆账号
    private String pw;  //密码
}