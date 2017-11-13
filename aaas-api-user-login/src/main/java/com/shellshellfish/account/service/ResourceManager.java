package com.shellshellfish.account.service;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceManager {

	public ResourceManager(){;}
	
	public HashMap<String ,Object> response(String pagename,String[] argv){
	    
		HashMap<String, Object> map = null;
		if (pagename.equals("login")) {
			String telnum=argv[0];
			map = logindesc(telnum);
		} else if (pagename.equals("register")) {
			String telnum=argv[0];
			map = registerdesc(telnum);
		} else if (pagename.equals("forgottenpwd")) {
			String telnum=argv[0];
			map = forgottenpwddesc(telnum);
		} else if (pagename.equals("pwdsetting")) {
			map = pwdSettingDesc(argv);
		} else if (pagename.equals("smsverification")) {
			map = smsVerificationDesc(argv);
		} else if (pagename.equals("addbankcard")) {
			map = addBankCardDesc(argv);
		} else if (pagename.equals("bklist")) {
			map = bkListDesc();
		}
		return map;
	}
	
	//for forgottenpwd url
	public HashMap<String,Object> forgottenpwddesc(String telnum){
		HashMap<String,Object> rsmap= new HashMap<String,Object>();
		
		rsmap.put("name","forgottenpwd");
		
		if (telnum==null)
			rsmap.put("telnum","");
		else
			rsmap.put("telnum",telnum);
			
		rsmap.put("verificationcode","");
		rsmap.put("verifycodeget","");
		rsmap.put("pwdsetting",""); //for 密码设置
		
		HashMap<String,Object> linkitemmap=new HashMap<String,Object>();
		HashMap<String,Object> selfmap=new HashMap<String,Object>();
		HashMap<String,Object> selfitemmap=new HashMap<String,Object>();
		selfitemmap.put("href", "/api/forgottenpwd");
		selfitemmap.put("describedBy", "/api/forgottenpwd.json");
		linkitemmap.put("self",selfitemmap);
		
		HashMap<String,Object> relatedmap=new HashMap<String,Object>();
		HashMap<String,Object> relateditem1map=new HashMap<String,Object>();
		relateditem1map.put("href", "/api/verifycodeget");		
		relatedmap.put("verifycodeget",relateditem1map);
		linkitemmap.put("related", relatedmap);
		
		//for pwd setting button 
		
		HashMap<String,Object> registermap=new HashMap<String,Object>();
		HashMap<String,Object> registeritemmap=new HashMap<String,Object>();
		registeritemmap.put("href", "/api/topwdsetting"); // for password setting button
		registeritemmap.put("describedBy", "/api/pwdsetting.json");
		registeritemmap.put("method", "POST");
		registermap.put("topwdsetting", registeritemmap);		 
		linkitemmap.put("execute", registermap);// post
		
		rsmap.put("_links", linkitemmap);
		return rsmap;
		
		
	}
	
	//for register url
	public HashMap<String,Object> registerdesc(String telnum){
		HashMap<String,Object> rsmap= new HashMap<String,Object>();
		rsmap.put("name","register");
		//rsmap.put("title","注册");
		rsmap.put("telsuffix","");
		
		if (telnum==null)
		   rsmap.put("telnum","");
		else
		   rsmap.put("telnum",telnum);
		
		rsmap.put("agreement","");
		
		HashMap<String,Object> linkitemmap=new HashMap<String,Object>();
		HashMap<String,Object> selfmap=new HashMap<String,Object>();
		HashMap<String,Object> selfitemmap=new HashMap<String,Object>();
		selfitemmap.put("href", "/api/register");
		selfitemmap.put("describedBy", "/api/register.json");
		linkitemmap.put("self",selfitemmap);
		
		HashMap<String,Object> relatedmap=new HashMap<String,Object>();
		HashMap<String,Object> relateditem1map=new HashMap<String,Object>();
		relateditem1map.put("href", "/api/register");		
		relatedmap.put("agreement",relateditem1map);
		
		HashMap<String,Object> relateditem2map=new HashMap<String,Object>();
		relateditem2map.put("href", "/api/agreement");
		relatedmap.put("agreement",relateditem2map);		
		linkitemmap.put("related", relatedmap);
		
		HashMap<String,Object> executemap=new HashMap<String,Object>();
		HashMap<String,Object> registermap=new HashMap<String,Object>();
		HashMap<String,Object> registeritemmap=new HashMap<String,Object>();
		registeritemmap.put("href", "/api/tosmsverification");
		registeritemmap.put("describedBy", "/api/smsverification.json");
		registeritemmap.put("method", "POST");
		registermap.put("register", registeritemmap);
		 
		linkitemmap.put("execute", registermap);// post
		
		rsmap.put("_links", linkitemmap);
		
	    return rsmap;
	}
	
	//for login url
	public HashMap<String,Object> logindesc(String telnum){
		//login resource
		HashMap<String,Object> rsmap= new HashMap<String,Object>();
		rsmap.put("name","login");
		//rsmap.put("title","登录");
		
		if (telnum==null)
			rsmap.put("telnum","");
		else
			rsmap.put("telnum",telnum);
			
		rsmap.put("password","");
		//rsmap.put("describedby", "/api/login.json");
		
		HashMap<String,Object> linkitemmap=new HashMap<String,Object>();
		HashMap<String,Object> selfmap=new HashMap<String,Object>();
		HashMap<String,Object> selfitemmap=new HashMap<String,Object>();
		selfitemmap.put("href", "/api/login");
		selfitemmap.put("describedBy", "/api/login.json");
		linkitemmap.put("self",selfitemmap);
		
	    
	    HashMap<String,Object> relatedmap=new HashMap<String,Object>();
		HashMap<String,Object> relateditem1map=new HashMap<String,Object>();
		relateditem1map.put("href", "/api/register");
		//HashMap<String,Object> fastregismap=new HashMap<String,Object>();
		
		relatedmap.put("fastRegistration",relateditem1map);
		
		HashMap<String,Object> relateditem2map=new HashMap<String,Object>();
		relateditem2map.put("href", "/api/forgottenpwd");
		relatedmap.put("forgottenPwd",relateditem2map);		
		linkitemmap.put("related", relatedmap);
		
		
		HashMap<String,Object> loginmap=new HashMap<String,Object>();
		HashMap<String,Object> loginitemmap=new HashMap<String,Object>();
		loginitemmap.put("href", "/api/loginverify");
		loginitemmap.put("describedBy", "/api/loginverify.json");
		loginitemmap.put("method", "POST");
		loginmap.put("login", loginitemmap);
		//executemap.put("execute",loginmap); 
		linkitemmap.put("execute", loginmap);// post
		
		rsmap.put("_links", linkitemmap);
		return rsmap;
		/*
	     //editor "tel,pwd"
	    HashMap<String,Object> editormap=new HashMap<String,Object>();
	    HashMap<String,Object>[] emap= new HashMap[2];
	    HashMap<String,Object> emap1= new HashMap<String,Object>();
	    emap1.put("name", "telnum");
	    HashMap<String,Object> emap2= new HashMap<String,Object>();
	    emap2.put("name", "password");
	    emap[0]=emap1;
	    emap[1]=emap2;
	    //editormap.put("name", "telphone");	    
	    rsmap.put("telnum", emap[0]);
	    rsmap.put("password", emap[1]);
	    //link reg,forgottnpwd
	    HashMap<String,Object> linkmap=new HashMap<String,Object>();
	    HashMap<String,Object>[] hmap= new HashMap[2];
	    HashMap<String,Object> hmap1= new HashMap<String,Object>();
	    
	    hmap1.put("name", "registration");
	    hmap1.put("href", "/api/register");
	    hmap1.put("describedby", "/api/registration.json");
	    
	    HashMap<String,Object> hmap2= new HashMap<String,Object>();
	    
	    hmap2.put("name", "forgottenpwd");
	    hmap2.put("href", "/api/forgottenpwd");
	    hmap2.put("describedby", "/api/pwd/forgottenpwd.json");
	    
	    hmap[0]=hmap1;
	    hmap[1]=hmap2;	    
	    //linkmap.put("name", "[快速注册,忘记密码 ?]");
	    rsmap.put("registration", hmap[0]);
	    rsmap.put("forgottenpwd", hmap[1]);
	    
	    //button register
	    HashMap<String,Object> buttonmap=new HashMap<String,Object>();
	    buttonmap.put("name", "loginclick");
	    rsmap.put("loginclick", buttonmap);
	   */
	    
	    
	}
	
	// for pwdsetting url
	public HashMap<String, Object> pwdSettingDesc(String[] telnum) {
		HashMap<String, Object> rsmap = new HashMap<String, Object>();
		Pattern regExp = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
		Matcher m = regExp.matcher(telnum[0]);
		if (!m.find()) {
			rsmap.put("status", "手机号格式不正确");
			return rsmap;
		}
		// pwdsetting resource
		rsmap.put("name", "pwdsetting");
		// rsmap.put("title", "密码设置");
		rsmap.put("pwdsetting", "");
		rsmap.put("pwdconfirm", "");
		rsmap.put("telnum", telnum[0]);

		HashMap<String, Object> linkitemmap = new HashMap<String, Object>();
		HashMap<String, Object> selfitemmap = new HashMap<String, Object>();
		selfitemmap.put("href", "/api/pwdsetting");
		selfitemmap.put("describedBy", "/api/pwdsetting.json");
		linkitemmap.put("self", selfitemmap);

		HashMap<String, Object> executemap = new HashMap<String, Object>();
		HashMap<String, Object> loginitemmap = new HashMap<String, Object>();
		loginitemmap.put("href", "/api/pwdconfirm");
		loginitemmap.put("describedBy", "/api/pwdconfirm.json");
		loginitemmap.put("method", "POST");
		executemap.put("pwdsetting", loginitemmap);
		linkitemmap.put("execute", executemap);// post

		rsmap.put("_links", linkitemmap);
		return rsmap;
	}

	// for smsverification url
	public HashMap<String, Object> smsVerificationDesc(String[] telnum) {
		// smsverification resource
		HashMap<String, Object> rsmap = new HashMap<String, Object>();
		/// =====start======///
		Pattern regExp = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
		Matcher m = regExp.matcher(telnum[0]);
		if (!m.find()) {
			rsmap.put("status", "手机号格式不正确");
			return rsmap;
		}
		/// =====end=======///

		rsmap.put("name", "smsverification");
		// rsmap.put("title", "短信验证");
		rsmap.put("smssuffix", "");
		rsmap.put("telnum", telnum[0]);
		rsmap.put("identifyingcode", "");

		HashMap<String, Object> linkitemmap = new HashMap<String, Object>();
		HashMap<String, Object> selfitemmap = new HashMap<String, Object>();
		selfitemmap.put("href", "/api/smsverification");
		selfitemmap.put("describedBy", "/api/smsverification.json");
		linkitemmap.put("self", selfitemmap);

		HashMap<String, Object> relatedmap = new HashMap<String, Object>();
		HashMap<String, Object> relateditem1map = new HashMap<String, Object>();
		relateditem1map.put("href", "/api/resend");
		relatedmap.put("resendidentifycode", relateditem1map);
		linkitemmap.put("related", relatedmap);

		HashMap<String, Object> executemap = new HashMap<String, Object>();
		HashMap<String, Object> smsitemmap = new HashMap<String, Object>();
		smsitemmap.put("href", "/api/smsverconfirm");
		smsitemmap.put("describedBy", "/api/smsverconfirm.json");
		smsitemmap.put("method", "POST");
		executemap.put("conform", smsitemmap);
		linkitemmap.put("execute", executemap);// post

		rsmap.put("_links", linkitemmap);
		return rsmap;
	}

	// for addbankcard url
	public HashMap<String, Object> addBankCardDesc(String[] telnum) {
		// addbankcard resource
		HashMap<String, Object> rsmap = new HashMap<String, Object>();

		/// =====start======///
		Pattern regExp = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
		Matcher m = regExp.matcher(telnum[0]);
		if (!m.find()) {
			rsmap.put("status", "手机号格式不正确");
			return rsmap;
		}
		/// =====end=======///

		rsmap.put("name", "addbankcard");
		// rsmap.put("title", "短信验证");
		rsmap.put("cardnumber", "");
		rsmap.put("supportedcard ", "");
		rsmap.put("telnum ", telnum[0]);

		HashMap<String, Object> linkitemmap = new HashMap<String, Object>();
		HashMap<String, Object> selfitemmap = new HashMap<String, Object>();
		selfitemmap.put("href", "/api/addbankcard");
		selfitemmap.put("describedBy", "/api/addbankcard.json");
		linkitemmap.put("self", selfitemmap);

		HashMap<String, Object> relatedmap = new HashMap<String, Object>();
		HashMap<String, Object> relateditem1map = new HashMap<String, Object>();
		relateditem1map.put("href", "/api/viewbklist");
		relatedmap.put("view", relateditem1map);
		linkitemmap.put("related", relatedmap);

		HashMap<String, Object> executemap = new HashMap<String, Object>();
		HashMap<String, Object> smsitemmap = new HashMap<String, Object>();
		smsitemmap.put("href", "/api/bkverrify");
		smsitemmap.put("describedBy", "/api/bkverrify.json");
		smsitemmap.put("method", "POST");
		executemap.put("next", smsitemmap);
		linkitemmap.put("execute", executemap);// post

		rsmap.put("_links", linkitemmap);
		return rsmap;
	}

	// for bklist url
	public HashMap<String, Object> bkListDesc() {
		// bklist resource
		HashMap<String, Object> rsmap = new HashMap<String, Object>();
		rsmap.put("name", "bklist");
		// rsmap.put("title", "短信验证");
		rsmap.put("bank", "");

		return rsmap;
	}
	
}
