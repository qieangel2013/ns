package com.ns.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/** 
* @ClassName: com.ns.util.HttpClientTestUtil 
* @Description: 利用httpGet的方式模拟浏览器请求,用来测试控制层
* @author danie
* @date 2016年8月23日 上午11:12:01 
*  
*/
public class HttpClientTestUtil {

	private static Logger LOG = Logger.getLogger(HttpClientTestUtil.class);

	public static void get(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		/**
		 * @GET
		 * */
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse chResp = null;
		try {
			chResp = httpclient.execute(httpGet);
			LOG.info("GET response over...");
			// 1.获取相应对象的状态行：HTTP/1.1 200 OK
			LOG.info(chResp.getStatusLine());
			// 2.获取相应实体
			HttpEntity et = chResp.getEntity();

			InputStream ins = null;
			BufferedReader br = null;
			// String type = "text/html";
			String type = "application/json";
			String contentType = et.getContentType().getValue();
			System.out.println(contentType);
			try {
				ins = et.getContent();
				if (contentType.contains(type)) {
					br = new BufferedReader(new InputStreamReader(ins));
					String line = br.readLine();
					while (line != null) {
						System.out.println(line);
						line = br.readLine();
					}
				}
			} finally {
				br.close();
				ins.close();
			}
			EntityUtils.consume(et);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				chResp.close();
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// public static void post(String url) {
	// CloseableHttpClient httpclient = HttpClients.createDefault();
	// /**
	// * @POST
	// * */
	// HttpPost httpPost = new HttpPost(url);
	// List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
	// nvpList.add(new BasicNameValuePair("Name", "nieweijun"));
	// nvpList.add(new BasicNameValuePair("Password", "xxx#33"));
	// CloseableHttpResponse response2 = null;
	// StatusLine statusLine = null;
	// try {
	// httpPost.setEntity(new UrlEncodedFormEntity(nvpList));
	// response2 = httpclient.execute(httpPost);
	// statusLine = response2.getStatusLine();
	// System.out.println("状态行\t" + statusLine);
	// System.out.println("状态行状态短语\t" + statusLine.getReasonPhrase());
	// System.out.println("状态行状态码\t" + statusLine.getStatusCode());
	// System.out.println("状态行协议版本\t" + statusLine.getProtocolVersion());
	// HttpEntity entity2 = response2.getEntity();
	//
	// InputStream ins = entity2.getContent();// ...
	// EntityUtils.consume(entity2);
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// response2.close();
	// httpclient.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }

	public static void main(String[] args) {
		String skus = "1991038,1991037,1990863,1991034,1990864,1938380,1990861,1991036,1938489,1991035,1990862,1938282,1990868,1938280,1990960,1990961,1990866,1938278,1938272,1938373,1938374,1991030,1991031,1938376,1954742,1990979,1990870,1990975,1990978,1990971,1990972,1990877,1938271,1938495,1938361,1991040,1938269,1954751,1990988,1990841,1938113,1990842,1991014,1990986,1938111,1990987,1991015,1991018,1954725,1990981,1990848,1938119,1990845,1938118,1990846,1990985,1990843,1990844,1991010,1990997,1991025,1990851,1938100,1991024,1991023,1938103,1938391,1990853,1991029,1991028,1991027,1991026,1990858,1990859,1990992,1938105,1990854,1990993,1941425,1990995,1990996,1938106,1938387,1954730,1991020,1938382,1938384,1954804,1938234,1938450,1938122,1938456,1938123,1954710,1938227,1938329,1938328,1931317,1944708,1938463,1990887,1938469,1990886,1938156,1990883,1990882,1938356,1938355,1938354,1938353,1938350,1938358,1938357,1938472,1990899,1938474,1938347,1938348,1938407,1938081,1938924,1965216,1938080,1938302,1938079,1938075,1938074,1938077,1938076,1974873,1938073,1938070,1954657,1974869,1938316,1974857,1938426,1938427,1933823,1938557,1954679,1933818,1938438,1990909,1938194,1990908,1990907,1990911,1990912,1990915,1990914,1990917,1990918,1990919,1990825,1990827,1990925,1944577,1990928,1990829,1990930,1954697,1990833,1990832,1990839,1990838,1990837,1990836,1990938,1990937,1990935,1991007,1990831,1990830,1990933,1990932,1991002,1990939,1938295,1938899,1944581,1938297,1938298,1938098,1990805,1938099,1990941,1990942,1944599,1944692,1938090,1938284,1990809,1938094,1938089,1990810,1990955,1938507,1990959,1990958";
		String[] skuArr = skus.split(",");
		List<String> skuList = Arrays.asList(skuArr);
		int size = skuArr.length;
		int times = 0;
		final String dir = "D:/tmp/data";
		final int ONT_TIME_SIZE = 100;
		// 如果size>=100，分次查
		if (size % ONT_TIME_SIZE == 0) {
			times = size / ONT_TIME_SIZE;
		} else {
			times = size / ONT_TIME_SIZE + 1;
		}
		String url = "http://young.bdp.jd.com/jsf-client/jsf/cbj/?skus=";
		for (int n = 1; n <= times; n++) {
			int startIndex = ONT_TIME_SIZE * (n - 1); // include
			int endIndex = n * 100 <= size ? n * 100 : size; // exclusive
			String fname = n + ".html";
			StringBuffer skuTrail = new StringBuffer();
			for (String s : skuList.subList(startIndex, endIndex)) {
				skuTrail.append(s).append(",");
			}
			skuTrail.deleteCharAt(skuTrail.length() - 1);
			System.out.println(url + skuTrail);
			get(url + skuTrail);
		}
	}

}
