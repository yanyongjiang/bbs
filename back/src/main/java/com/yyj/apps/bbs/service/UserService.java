package com.yyj.apps.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.yyj.apps.bbs.mapper.AttachMapper;
import com.yyj.apps.bbs.mapper.PostMapper;
import com.yyj.apps.bbs.mapper.UserMapper;
import com.yyj.apps.bbs.util.SnowflakeIdUtils;
import com.yyj.apps.bbs.vo.BbsAttach;
import com.yyj.apps.bbs.vo.BbsParam;
import com.yyj.apps.bbs.vo.BbsPost;
import com.yyj.apps.bbs.vo.BbsUser;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description 当前累计流量算力收益活跃矿工查询service
 * @Author yyj
 * @Date 2020-10-29 15:04s
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PostMapper postMapper;

    @Autowired
    AttachMapper attachMapper;

    @Value("${userService.savefilepath}")
    private String savefilepath;

    public JSONObject reg(BbsParam param, JSONObject r) {
        if (checkParam(param, r)) {
            String lid = param.getLid();
            String pw = param.getPw();
            Map p = new HashMap();
            p.put("lid", lid);
            List<Long> lids = userMapper.getSQLManager().select("user.selectUbyLid", Long.class, p);
            if (lids != null && lids.size() > 0) {
                r.put("result", "100");
                r.put("errmsg", "lid is exist");
                return r;
            }
            BbsUser u = new BbsUser();
            u.setLid(lid);
            u.setPw(DigestUtils.md5Hex(pw));
            userMapper.insert(u);
            lids = userMapper.getSQLManager().select("user.selectUbyLid", Long.class, p);
            u.setId(lids.get(0));
            r.put("data", u);
        }
        return r;
    }

    public JSONObject log(BbsParam param, JSONObject r) {
        if (checkParam(param, r)) {
            String lid = param.getLid();
            String pw = param.getPw();
            Map p = new HashMap();
            p.put("lid", lid);
            List<BbsUser> lids = userMapper.getSQLManager().select("user.selectUpwbyLid", BbsUser.class, p);
            if (lids == null || lids.size() == 0) {
                r.put("result", "100");
                r.put("errmsg", "lid is not exist");
                return r;
            }
            BbsUser u=lids.get(0);
            String pwd=u.getPw();
            pwd=DigestUtils.sha256Hex(pwd);
            String pwd1=DigestUtils.sha256Hex(u.getPw().toLowerCase());
            if(pwd.toLowerCase().equals(pw.toLowerCase())||pwd1.toLowerCase().equals(pw.toLowerCase())) {
                u.setPw(null);
                r.put("data", u);
            }else{
                r.put("result", "100");
                r.put("errmsg", "password is wrong");
            }
        }
        return r;
    }

    public boolean checkParam(BbsParam param, JSONObject r) {
        String lid = param.getLid();
        String pw = param.getPw();
        if (lid == null || "".equals(lid)) {
            r.put("result", "100");
            r.put("errmsg", "lid is exist");
            return false;
        }
        if (pw == null || "".equals(pw)) {
            r.put("result", "100");
            r.put("errmsg", "pw is empty");
            return false;
        }
        return true;
    }
    public JSONObject savepost(BbsParam param,MultipartFile[] uploadfile, JSONObject r) throws IOException {
        BbsPost post=new BbsPost();
        post.setTm(param.getTm());
        post.setCont(param.getCont());
        post.setUid(param.getUid());
        post.setLid(param.getLid());
        post.setPty(param.getPty());
        postMapper.getSQLManager().insertTemplate(BbsPost.class,post,true);
        Long id=post.getId();
        if(uploadfile!=null&&uploadfile.length>0){
            int i=1;
            SnowflakeIdUtils idWorker = new SnowflakeIdUtils(3, 1);
            for(MultipartFile file:uploadfile){
                Long path=idWorker.nextId();
                byte[] bytes=file.getBytes();
                if(file.getSize()>1048576&&file.getOriginalFilename()!=null&&
                        (file.getOriginalFilename().endsWith(".jpg")||file.getOriginalFilename().endsWith(".JPG")
                        ||file.getOriginalFilename().endsWith(".png")||file.getOriginalFilename().endsWith(".PNG"))){
                    OutputStream out=new ByteArrayOutputStream();
                    InputStream in=new ByteArrayInputStream(bytes);
                    try{
                        Thumbnails.of(in).scale(1f).outputQuality(0.25f).toOutputStream(out);
                        bytes=((ByteArrayOutputStream) out).toByteArray();
                    }catch (Exception e){
                        log.info("压缩图片出错:",e);
                        continue;
                    }finally {
                       if(null!=out){
                            out.close();
                            out=null;
                        }
                        if(null!=in){
                            in.close();
                            in=null;
                        }
                    }
                }
                BbsAttach attach=new BbsAttach();
                attach.setPkid(id);
                attach.setFname(file.getOriginalFilename());

                attach.setFpa(path+"");
                attach.setSn(i);
                attach.setFs(file.getSize()+"");
                savelocalFile(bytes,savefilepath+"/"+path);
                attachMapper.insertTemplate(attach);
                i++;
            }
        }
        return r;
    }

    public void savelocalFile(byte[] fileBytes,String filePath){
        File imgFile=new File(filePath);
        OutputStream out = null;
        try{
            if(!imgFile.exists()){
                imgFile.createNewFile();
            }
            out = new BufferedOutputStream(new FileOutputStream(imgFile));
            out.write(fileBytes);
            out.close();
            out=null;
        }catch(IOException e){
            log.error("savelocalFile报错",e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("savelocalFile报错2",e);
                }
            }
        }
    }

    public JSONObject listpost(BbsParam param, JSONObject r) {
        Long count = userMapper.getSQLManager().selectSingle("user.listPostCount", param,Long.class);
        List<BbsPost> posts = userMapper.getSQLManager().select("user.listPost", BbsPost.class, param);
        Map p = new HashMap();
        p.put("posts",posts);
        p.put("count",count);
        r.put("data", p);
        return r;
    }

    public JSONObject getonepost(BbsParam param, JSONObject r) {
        BbsPost post = postMapper.unique(param.getId());
        Map p = new HashMap();
        p.put("pkid", param.getId());
        List<BbsAttach> attachs = attachMapper.getSQLManager().select("user.listPostFile", BbsAttach.class, p);
        post.setAttachList(attachs);
        r.put("data", post);
        return r;
    }

    public String downloadFile(String path) {
        return savefilepath+"/"+path;
    }


    /**
     * 删除帖子
     * @param param
     * @param result
     */
    public void removePost(BbsParam param, JSONObject result) {
        postMapper.deleteById(param.getId());
        Map p = new HashMap();
        p.put("pkid", param.getId());
        List<BbsAttach> attachs = attachMapper.getSQLManager().select("user.listPostFile", BbsAttach.class, p);
        for(BbsAttach attach:attachs){
           String fpa=attach.getFpa();
            File file=new File(downloadFile(fpa));
            if(file.exists()){
                file.delete();
            }
            attachMapper.deleteById(attach.getId());
        }
    }
}
