package holley_server.lysw.util;



import holley_server.lysw.model.RetTypeEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.holley.platform.common.util.StringUtil;

public class HttpRequestUtil {

    private final static Logger logger   = Logger.getLogger(HttpRequestUtil.class);
    private final static String baseurl  = "http://api.map.baidu.com/geoconv/v1/?ak=Gf39Vxv6N9I9pfdb8tyg6GIA&output=json&coords=";
    private  static String ecbaseurl1; // 测试站地址token
    private  static String ecbaseurl2 ;                                                      // 测试站地址

    private static String operatorID = "348375999";
    private static String       operatorSecret = "pDyZVPKYNDYIEtpq";
    private static String       dataSecret     = "7l0lPWSpAmlDFueq";
    private static String       dataSecretIV   = "23U1zg5EhozFPjgK";
    private static String       sigSecret      = "usc7b1vWfBlvzRPr";
    private static int           timeout        = 10000;

     static {
		 InputStream inStream = HttpRequestUtil.class.getClassLoader()
				 .getResourceAsStream("openActive.properties");
		 Properties prop = new Properties();
		 try {
			 prop.load(inStream);
			 ecbaseurl1 = prop.getProperty("openUrl1");
			 ecbaseurl2 = prop.getProperty("openUrl2");
		 } catch (IOException e) {
		 e.printStackTrace();
		 }
     }
     /**
      * 解析加密数据
      * @param enData
      * @param dataSecret
      * @param dataSecretIV
      * @return
      * @throws Exception
      */
     public static JSONObject getDecodeData(JSONObject data) throws Exception {
         String str = ShareSecurityUtil.aesDecrypt(data.getString("data"), dataSecret, dataSecretIV);
         return StringUtil.isEmpty(str) ? null : JSONObject.fromObject(str);
     } 
     
     public static RetTypeEnum getRetResult(JSONObject data) throws Exception {
    	 if(data == null){
    		 return RetTypeEnum.SYS_BUSY;
    	 }
         return RetTypeEnum.getEnmuByValue(CommonUtil.getDataInt(data,"ret"));
     }
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static JSONArray sendGet(String param) {
        String result = "";
        BufferedReader in = null;
        JSONArray array = null;
        try {
            String urlNameString = baseurl + param;

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(10000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JSONObject resultJsonObj = JSONObject.fromObject(result);
            if (resultJsonObj.getInt("status") == 0) {
                String resultListJsonObj = resultJsonObj.get("result").toString();
                array = JSONArray.fromObject(resultListJsonObj);

            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return array;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    public static JSONObject httpUrlConnection(String baseurl, String requestString, String token) {
        BufferedReader responseReader = null;
        try {
            // 建立连接
            URL url = new URL(baseurl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            // //设置连接属性
            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存
            httpConn.setRequestMethod("POST");// 设置URL请求方法
            httpConn.setConnectTimeout(timeout);
            httpConn.setReadTimeout(timeout);
            // String requestString =
            // "{operatorId:348375727,operatorSecret:123,sig:1F28FB25653BF36B6485DB0BDF38839B,data:NEODLvcvWaUNSt6tjAYp/6Uu2b0ALLMNCvrINsCpwm2pdpQ3cMjp8Q9krGXvFHDGHTfo1t8nss4vQ/MJLHRdJA==,timeStamp:123,seq:0001}";

            // 设置请求属性
            // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            byte[] requestStringBytes = requestString.getBytes("UTF-8");
            httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
            httpConn.setRequestProperty("Content-Type", "application/octet-stream");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");
            //
            if (StringUtil.isNotEmpty(token)) {
                httpConn.setRequestProperty("authorization", token);
            }

            // 建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.close();
            // 获得响应状态
            int responseCode = httpConn.getResponseCode();
            StringBuffer sb = new StringBuffer();

            if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
                // 当正确响应时处理数据
                String readLine;

                // 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                // responseReader.close();
            }
            logger.info(sb.toString());
            return JSONObject.fromObject(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (responseReader != null) {
                try {
                    responseReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }


    public static JSONObject httpTest(String actionUrl, Object param, QueryBean queryBean, String token,String url) throws Exception {

        String enstr = ShareSecurityUtil.aesEncrypt(JSON.toJSONString(param, SerializerFeature.WriteMapNullValue), dataSecret, dataSecretIV);
        queryBean.setData(enstr);
        String OperatorID = queryBean.getOperatorId();
        String Data = queryBean.getData().toString();
        String TimeStamp = queryBean.getTimeStamp();
        String Seq = queryBean.getSeq();
        String Sig = OperatorID + Data + TimeStamp + Seq;
        Sig = HMacMD5.getHmacMd5Str(sigSecret, Sig);
        queryBean.setSig(Sig);
        return httpUrlConnection(url + actionUrl, JSON.toJSONString(queryBean, SerializerFeature.WriteMapNullValue), token);

    }

    // TEST
    /**
     * token获取
     * 
     * @param operatorID
     * @throws Exception
     */
    public static JSONObject queryToken() throws Exception {
        // token 请求
        QueryBean qb = new QueryBean();
        qb.setOperatorId(operatorID);
        qb.setTimeStamp(CommonUtil.DateToStr(new Date(), LyswGloblas.TIME_LONG_14));
        qb.setSeq("0001");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("operatorId", operatorID);
        param.put("operatorSecret", operatorSecret);
        return httpTest("query_token", param, qb, null,ecbaseurl1);
    }

 // 银行接口 start
    /**
     * 银行签约
     * 
     * @throws Exception
     */
    public static JSONObject queryBankContract(String token,String hh,String cardNo,String dateTime) throws Exception {
        QueryBean qb = new QueryBean();
        qb.setOperatorId(operatorID);
        qb.setTimeStamp(CommonUtil.DateToStr(new Date(), LyswGloblas.TIME_LONG_14));
        qb.setSeq("0001");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("operatorSecret", operatorSecret);
        param.put("hh", hh);//户号
        param.put("cardNo", cardNo);//卡号
        param.put("dateTime", dateTime);//卡号 yyyy-MM-dd
        return httpTest("query_bank_contract", param, qb, token,ecbaseurl2);
    }
    /**
     * 银行解约
     * 
     * @throws Exception
     */
    public static JSONObject queryBankDissolution(String token,String hh,String cardNo,String dateTime) throws Exception {
        QueryBean qb = new QueryBean();
        qb.setOperatorId(operatorID);
        qb.setTimeStamp(CommonUtil.DateToStr(new Date(), LyswGloblas.TIME_LONG_14));
        qb.setSeq("0001");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("operatorSecret", operatorSecret);
        param.put("hh", hh);//户号
        param.put("cardNo", cardNo);//卡号
        param.put("dateTime", dateTime);//卡号 yyyy-MM-dd
        return httpTest("query_bank_dissolution", param, qb, token,ecbaseurl2);
    }
    /**
     * 银行获取账单
     * 
     * @throws Exception
     */
    public static JSONObject queryBankBills(String token) throws Exception {
        QueryBean qb = new QueryBean();
        qb.setOperatorId(operatorID);
        qb.setTimeStamp(CommonUtil.DateToStr(new Date(), LyswGloblas.TIME_LONG_14));
        qb.setSeq("0001");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("operatorSecret", operatorSecret);
       return httpTest("query_bank_bills", param, qb, token,ecbaseurl2);
    }

    /**
     * 银行结果返回
     * 
     * @throws Exception
     */
    public static JSONObject queryBankBillsResult(String token, List<String> datas) throws Exception {
        QueryBean qb = new QueryBean();
        qb.setOperatorId(operatorID);
        qb.setTimeStamp(CommonUtil.DateToStr(new Date(), LyswGloblas.TIME_LONG_14));
        qb.setSeq("0001");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("operatorSecret", operatorSecret);
        param.put("datas", datas);//数据
        return httpTest("query_bank_bills_result", param, qb, token,ecbaseurl2);
    }
    public static void main(String[] args) throws Exception {
		String s = "ZOb0/v3VybFEvp7CPmrc8MhLQ4h2i6C5SUH1AR4iGz/aP1MeaBx/krUYQbAfibmmD3Pv7JqQEKS6Auu+qpzknBpwmzNfo4olb5GN8fLi540ZrqbpFKOvKHaF0YA4mCwTZnmFuvkcSbMhT+8OJCOInpzuEcXBhQE51J9WcDlJvoPdcFj8huVDHbWSLVD6hQlOyTcK/ZK8HTeklor3+800FGlQdtaAvACfYNClmh916lTWfX3p5iAR/BGZ9e4/ux1blobDyOgKnSRz2F5PvGqkPj9guPbpVNOLwIh5a4Ul59yUrgTIDqfcEIQa+2iLqRwj/UJV66dAPsyuBd0W9+Yg6Nnz+AZlxCUzssNXsuxs7v8ncMCOSfUir/3UCdptrQeL";
		String str = ShareSecurityUtil.aesDecrypt(s, dataSecret, dataSecretIV);
		System.out.println(str);
    }
}
