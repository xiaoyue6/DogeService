package com.cuit.DogeMusicDataService.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.better517Na.AdsManageSystem.BaseCtrl.BaseController;
import com.better517Na.AdsManageSystem.Helper.UploadImageR;
import com.better517Na.AdsManageSystem.ViewModels.Common.VMUserInfo;
import com.better517Na.AdsManageSystem.business.admin.IAdsManageBusiness;
import com.better517na.adsManageService.model.dto.AdDto;
import com.better517na.commons.Response;
import com.better517na.logcompontent.util.PropertiesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Controller
@RequestMapping("/Upload")
public class UploadAdsController extends BaseController{
	@Resource(name="adsManageBusiness")
    private IAdsManageBusiness IAdsManageBusiness;
	
	private String uploadUrl="";
	/**
	 * 返回广告上传页面
	 * @return
	 */
	@RequestMapping(value="/UploadAds")
	public String upLoadAds(HttpServletRequest req, String role){
		req.setAttribute("role", role);
		return "user/uploadAds";
	}
	
	/**
	 * 根据keyID数据
	 * @param adsID
	 * @return
	 */
	@RequestMapping(value="/FindAds", produces="text/html;charset=utf-8")
	@ResponseBody
	public String findAds(String adsID){
		String res = IAdsManageBusiness.serachAdsByID(adsID);
		return res;
	}
	
	/**
	 * 上传方法
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/UpLoadFile")
	public @ResponseBody String UpLoadFile(
			String adsID,
			String code,
			String type,
			String title,
			String a,
			String link,
			String content,
			String description,
			String typeOp,
			String role,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		String res = "";
		String path = request.getContextPath();
		response.setContentType("text/html;charset=UTF-8");
		// 图片
		if(type.equals("2")){
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			
			// 获得文件：
			List<MultipartFile> mfList = multipartRequest.getFiles("file");
			for (MultipartFile mf : mfList) {
				try {
					// 上传
					String fileName = this.addFile(mf);
					BufferedImage bi=ImageIO.read(mf.getInputStream());//获取图片buffer对象

					AdDto uploadAdDto = new AdDto();
					int typeInt = Integer.parseInt(type);// 转byte类型
					uploadAdDto.setType((byte)typeInt);
					uploadAdDto.setTitle(title);
					uploadAdDto.setLinkType(Integer.parseInt(a));
					uploadAdDto.setLink(link);
					uploadAdDto.setContent(fileName);
					uploadAdDto.setDescription(description);
					VMUserInfo userInfo = (VMUserInfo) request.getSession().getAttribute("LoginStaff");
					uploadAdDto.setAdHeight(bi.getHeight());
					uploadAdDto.setAdWidth(bi.getWidth());
					uploadAdDto.setIsDelete(0);
					 
					// 修改
					if (null != adsID && !"".equals(adsID)) {
						uploadAdDto.setUpdateUserID(userInfo.getStaffId());
						uploadAdDto.setAdID(adsID);
						res = IAdsManageBusiness.updateAd(new Gson().toJson(uploadAdDto));
					}else{
						SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
						String time=formatter.format(new Date());
						uploadAdDto.setUploadTime(time);
						uploadAdDto.setUpdateUserID(userInfo.getStaffId());
						uploadAdDto.setUploadUserID(userInfo.getStaffId());
						res = IAdsManageBusiness.insetAd(new Gson().toJson(uploadAdDto));
					}
					
					Response resObj = new Gson().fromJson(res, new TypeToken<Response<String>>(){}.getType());
					
					String adId = (String)resObj.getContent();
//					
					// 保存成功
					if(resObj.getCode()==1){
						if("1".equals(typeOp)){
							if(role.equals("admin")){
								String adInfo = "{\"adID\":\"" + adId + "\",\"type\":\"" + 2 + "\",\"title\":\"" + title +"\",\"content\":\"" + content + "\",\"adWidth\":\"" + bi.getWidth() + "\",\"adHeight\":\"" + bi.getHeight() + "\"}";
								response.getWriter().write("<script>parent.layer.closeAll();parent.layer.msg('保存成功!,跳转页面中...',1,1,function(){parent.location.href='" + path + "/Upload/AdminSaveSuccess?adInfo=" + adInfo + "&&isEdit=" + 0 + "';});</script>");
								
							}else{
								String adInfo = "{\"adID\":\"" + adId + "\",\"type\":\"" + 2 + "\",\"title\":\"" + title +"\",\"content\":\"" + content + "\",\"adWidth\":\"" + bi.getWidth() + "\",\"adHeight\":\"" + bi.getHeight() + "\"}";
								response.getWriter().write("<script>parent.layer.closeAll();parent.layer.msg('保存成功!,跳转页面中...',1,1,function(){parent.location.href='" + path + "/Upload/UserSaveSuccess?adInfo=" + adInfo + "&&isEdit=" + 0 + "';});</script>");
					
							}
						}else{
							String adInfo = "{\"adID\":\"" + adId + "\",\"type\":\"" + 2 + "\",\"title\":\"" + title +"\",\"content\":\"" + content + "\",\"adWidth\":\"" + bi.getWidth() + "\",\"adHeight\":\"" + bi.getHeight() + "\"}";
							response.getWriter().write("<script>parent.layer.closeAll();parent.layer.msg('保存成功!,跳转页面中...',1,1,function(){parent.location.href='" + path + "/CommonRelateAdsManage/RelateAds?adInfo=" + adInfo + "&&isEdit=" + 0 + "&&role=" + role + "'});</script>");
						}
					}else{
						response.getWriter().write("<script>parent.layer.closeAll();parent.layer.alert('保存失败!');</script>");
					}
					
				} catch (IOException e) {
					response.getWriter().write("<script>parent.layer.closeAll();parent.layer.alert('由于IO问题，导致上传失败');</script>");
				} catch (InterruptedException e) {
					response.getWriter().write("<script>parent.layer.closeAll();parent.layer.alert('由于签名问题，导致失败');</script>");
				} catch (RuntimeException e) {
					response.getWriter().write("<script>parent.layer.closeAll();parent.layer.alert('"+e.getLocalizedMessage()+"');</script>");
				}
			}
		}else{
			AdDto uploadAdDto = new AdDto();
			int typeInt = Integer.parseInt(type);// 转byte类型
			uploadAdDto.setType((byte)typeInt);
			uploadAdDto.setTitle(title);
			uploadAdDto.setLinkType(Integer.parseInt(a));
			uploadAdDto.setLink(link);
			uploadAdDto.setContent(content);
			uploadAdDto.setDescription(description);
			VMUserInfo userInfo = (VMUserInfo) request.getSession().getAttribute("LoginStaff");
			uploadAdDto.setAdHeight(0);
			uploadAdDto.setAdWidth(0);
			uploadAdDto.setIsDelete(0);
			
			
			// 修改
			if (null != adsID && !"".equals(adsID)) {
				uploadAdDto.setUpdateUserID(userInfo.getStaffId());
				uploadAdDto.setAdID(adsID);
				res = IAdsManageBusiness.updateAd(new Gson().toJson(uploadAdDto));
			}else{
				SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				String time=formatter.format(new Date()); 
				uploadAdDto.setUploadTime(time);
				uploadAdDto.setUploadUserID(userInfo.getStaffId());
				uploadAdDto.setUpdateUserID(userInfo.getStaffId());
				res = IAdsManageBusiness.insetAd(new Gson().toJson(uploadAdDto));
			}
			
			
			Response resObj = new Gson().fromJson(res, new TypeToken<Response<String>>(){}.getType());
			
			String adId = (String)resObj.getContent();
			// 保存成功
			if(resObj.getCode()==1){
				// 判断点击是哪个按钮
				if("1".equals(typeOp)){
					if(role.equals("admin")){
						String adInfo = "{\"adID\":\"" + adId + "\",\"type\":\"" + 1 + "\",\"title\":\"" + title + "\",\"content\":\"" + content + "\"}";
						response.getWriter().write("<script>parent.layer.closeAll();parent.layer.msg('保存成功!,跳转页面中...',1,1,function(){parent.location.href='" + path + "/Upload/AdminSaveSuccess?adInfo=" + adInfo + "&&isEdit=" + 0 + "';});</script>");
						
					}else{
						String adInfo = "{\"adID\":\"" + adId + "\",\"type\":\"" + 1 + "\",\"title\":\"" + title + "\",\"content\":\"" + content + "\"}";
						response.getWriter().write("<script>parent.layer.closeAll();parent.layer.msg('保存成功!,跳转页面中...',1,1,function(){parent.location.href='" + path + "/Upload/UserSaveSuccess?adInfo=" + adInfo + "&&isEdit=" + 0 + "';});</script>");
			
					}
				}else{
					String adInfo = "{\"adID\":\"" + adId + "\",\"type\":\"" + 1 + "\",\"title\":\"" + title + "\",\"content\":\"" + content + "\"}";
					response.getWriter().write("<script>parent.layer.closeAll();parent.layer.msg('保存成功!,跳转页面中...',1,1,function(){parent.location.href='" + path + "/CommonRelateAdsManage/RelateAds?adInfo=" + adInfo + "&&isEdit=" + 0 + "&&role=" + role + "'});</script>");
					
				}					
			}else{
				response.getWriter().write("<script>parent.layer.closeAll();parent.layer.alert('保存失败!');</script>");
			}
		}
		
		return null;
	}
	

	/**
	 * 上传图片文件
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String addFile(MultipartFile mf) throws IOException, InterruptedException {
		HttpClient httpclient = new DefaultHttpClient();//申明一个httpClient端
		if(uploadUrl==null||uploadUrl.equals(""))
		{
			uploadUrl= PropertiesManager.readPropertiesByName("resourceServer.uploadUrl","jdbc.properties");
		}
		int uploadResult=0;
		String returnPath="";
		String subPath="";
		String[] uploadUrlList=uploadUrl.split(",");
		Gson son=new Gson();//创建一个Gson对象转换返回的 response 的json数据格式
		HttpEntity httpEntity=getMultipartEntityBuilder(mf).build();//生成http post实体
		//能够保证在任何情况下都会将底层的HTTP连接释放回连接管理器。
		ResponseHandler<String> responseHandler = new BasicResponseHandler();//使用ResponseHandler执行请求
		for(int i=0;i<uploadUrlList.length;i++)
		{
			HttpPost httpPost=new HttpPost(uploadUrlList[i]);//设置不同的URL路径的不同post请求
			httpPost.setEntity(httpEntity);//设置请求参数
			String responseBody = httpclient.execute(httpPost,responseHandler);
			String result=new String(responseBody.getBytes("ISO-8859-1"),"UTF-8");//将返回回来的String转换编码
        	UploadImageR uploadImageRVo=son.fromJson(result, UploadImageR.class);//接收返回的String类型转换成UploadImageR对象
        	if(uploadImageRVo!=null&&uploadImageRVo.getResult().equals("1"))//上传成功判断
        	{
        		uploadResult++;//上传统计参数
        		subPath=son.toJson((String)uploadImageRVo.getMessage());//重置上传返回的图片路径
        		returnPath=subPath.substring(1, subPath.length()-1);
        	}
        	else
        	{
        		uploadResult=0;//上传参数归零
        		return null;//上传失败的返回
        	}
        	System.out.println(returnPath);
		}
		if(uploadResult==0)
		{
		return null;
		}
		else
		{
			return returnPath;
		}
	}
	/**
	 * 设置HttpClient请求的表单内容
	 * @throws IOException 
	 */
	public MultipartEntityBuilder getMultipartEntityBuilder(MultipartFile mf) throws IOException{
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();//设置请求表单，
    	multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
    	byte[] buffer=mf.getBytes();//创建一个byte数组，将遍历到的文件对象转换为byte数组对象
    	ContentBody contentBody=new ByteArrayBody(buffer,getFileNewName(mf.getOriginalFilename()));//添加表单内容
		multipartEntityBuilder.addPart("AdsImage", contentBody);//将内容绑定到表单提交对象上
		return multipartEntityBuilder;
	}
	//设置上传图片的新图片名字
	public String getFileNewName(String fileOriginalName)
	{
		//拼接上传图片的文件名+++++++++++++++++++开始标示++++++++++++++++++++
		String [] stringList=fileOriginalName.split("\\.");
		String fileType="."+stringList[stringList.length-1];
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");//时间格式为yyyyMM
		String firstFileName=sdf.format(new Date())+"-";//将当前时间设置为yyyyMM格式
		String s = UUID.randomUUID().toString();//生成一个唯一表示UUID
		//拼接上传图片的文件名+++++++++++++++++++结束标示++++++++++++++++++++
		return firstFileName+s+fileType;
	}
	/**
	 * 返回管理员保存成功页面
	 * @return
	 */
	@RequestMapping("/AdminSaveSuccess")
	public String adminSaveSuccess(){
		return "admin/saveSuccess";
	}
	
	/**
	 * 返回员工保存成功页面
	 * @return
	 */
	@RequestMapping("/UserSaveSuccess")
	public String userSaveSuccess(){
		return "user/saveSuccess";
	}
	
}





















