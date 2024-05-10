package com.yyj.apps.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.yyj.apps.bbs.service.UserService;
import com.yyj.apps.bbs.vo.BbsParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;


@Slf4j
@RestController
@RequestMapping("bbs/user/")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "reg")
	@ResponseBody
	public String reg(@RequestBody BbsParam param, HttpServletRequest request) {
		JSONObject result = new JSONObject(true);
		result.put("result","0");
		result.put("errmsg","");
		userService.reg(param,result);
		return result.toString();
	}

	@RequestMapping(value = "log")
	@ResponseBody
	public String log(@RequestBody BbsParam param, HttpServletRequest request) {
		JSONObject result = new JSONObject(true);
		result.put("result","0");
		result.put("errmsg","");
		userService.log(param,result);
		return result.toString();
	}

    @RequestMapping(value = "savepost")
    @ResponseBody
    public String savepost(@RequestPart(value = "files") MultipartFile[] uploadfile,HttpServletRequest request) throws IOException {
        BbsParam param=new BbsParam();
        param.setTm(request.getParameter("tm"));
		param.setCont(request.getParameter("cont"));
		param.setLid(request.getParameter("lid"));
		param.setPty(request.getParameter("pty"));
		if(null!=request.getParameter("uid")){
			param.setUid(Long.parseLong(request.getParameter("uid")));
		}
		JSONObject result = new JSONObject(true);
        result.put("result","0");
        result.put("errmsg","");
        userService.savepost(param,uploadfile,result);
        return result.toString();
    }

	@RequestMapping(value = "listpost")
	@ResponseBody
	public String listpost(@RequestBody BbsParam param,HttpServletRequest request) throws IOException {
		JSONObject result = new JSONObject(true);
		result.put("result","0");
		result.put("errmsg","");
		userService.listpost(param,result);
		return result.toString();
	}

	@RequestMapping(value = "getonepost")
	@ResponseBody
	public String getonepost(@RequestBody BbsParam param,HttpServletRequest request) throws IOException {
		JSONObject result = new JSONObject(true);
		result.put("result","0");
		result.put("errmsg","");
		userService.getonepost(param,result);
		return result.toString();
	}

	@RequestMapping(value = "downloadfile")
	@ResponseBody
	public void downloadFile(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String path=request.getParameter("fpa");
		String fileName=request.getParameter("fname");
		OutputStream os = null;
		InputStream inputStream = null;
		try {
			String filePath = userService.downloadFile(path);
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
			inputStream = new FileInputStream(filePath);
			os = response.getOutputStream();
			byte[] b = new byte[2048];
			int length;
			while ((length = inputStream.read(b)) > 0) {
				os.write(b, 0, length);
			}
		} catch (Exception e) {
			log.error("download失败:", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					log.error("download-outputStream失败:", e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("download-outputStream失败:" + e);
				}
			}
		}

	}

	@RequestMapping(value = "removePost")
	@ResponseBody
	public String removePost(@RequestBody BbsParam param,HttpServletRequest request) throws IOException {
		JSONObject result = new JSONObject(true);
		result.put("result","0");
		result.put("errmsg","");
		userService.removePost(param,result);
		return result.toString();
	}
}
