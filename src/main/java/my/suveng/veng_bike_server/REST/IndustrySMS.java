package my.suveng.veng_bike_server.REST;

import my.suveng.veng_bike_server.REST.common.Config;
import my.suveng.veng_bike_server.REST.common.HttpUtil;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * 验证码通知短信接口
 * 
 * @ClassName: IndustrySMS
 * @Description: 验证码通知短信接口
 *
 */
@Component
public class IndustrySMS
{
	private static String operation = "/industrySMS/sendSMS";

	private static String accountSid = Config.ACCOUNT_SID;
	private String to = "18320664028";
	private String smsContent;

	/**
	 * 验证码通知短信
	 */
	private void execute()
	{
		String tmpSmsContent = null;
	    try{
	      tmpSmsContent = URLEncoder.encode(smsContent, "UTF-8");
	    }catch(Exception e){
	      
	    }
	    String url = Config.BASE_URL + operation;
	    String body = "accountSid=" + accountSid + "&to=" + to + "&smsContent=" + tmpSmsContent
	        + HttpUtil.createCommonParam();

	    // 提交请求
	    String result = HttpUtil.post(url, body);
	    System.out.println("result:" + System.lineSeparator() + result);
	}
	public void send(String phone,String code){
		this.setTo(phone);
		StringBuilder content=new StringBuilder();
		content.append("【东软睿道】您的验证码为");
		content.append(code);
		content.append("，请于3分钟内正确输入，如非本人操作，请忽略此短信。");
		this.setSmsContent(content.toString());
		this.execute();
	}

	public static String getOperation() {
		return operation;
	}

	public static void setOperation(String operation) {
		IndustrySMS.operation = operation;
	}

	public static String getAccountSid() {
		return accountSid;
	}

	public static void setAccountSid(String accountSid) {
		IndustrySMS.accountSid = accountSid;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
}
