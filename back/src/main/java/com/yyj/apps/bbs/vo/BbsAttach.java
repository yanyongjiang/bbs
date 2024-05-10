package com.yyj.apps.bbs.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Table(name="bbsattach")
public class BbsAttach implements Serializable {
    @AutoID
    private Long id;
    private Long pkid;
    private String fpa; //文件路径
    private String fname;  //文件名称
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Timestamp fngd;  //创建时间
    private String fs; //文件大小
    private int sn; //文件临时id
}