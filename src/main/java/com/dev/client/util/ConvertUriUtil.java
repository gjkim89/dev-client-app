package com.dev.client.util;

import java.net.URI;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

@Component
public class ConvertUriUtil {
	
	//String 으로 받을 경우
		public String converUri(Map<String, String[]> requestMap) {
				String uriParameter = "";
				
				for(String key : requestMap.keySet()) {
					String value[] = requestMap.get(key);
					if(value == null || value.length == 0) {
						//value 값이 없을 경우
						continue;
					}else if(value.length == 1) {
						//배열이 아닌 경우
						uriParameter = (uriParameter.isEmpty() ? "?" : "&") + key + "=" + value[0];
					}else {
						//배열일 경우
						for(int i = 0; i < value.length; i++) {
							uriParameter = (uriParameter.isEmpty() ? "?" : "&") + key + "=" + value[i];
						}//end for
					}//end else
				}//end for

				return uriParameter;
		}

		//URI로 받을 경우
		public URI convertUriBuilder(Map<String, String[]> requestMap, String path) {
				URI uri = null;
				
				try {
					
					//URIBuilder를 사용하여 보다 쉽게 URI 객체 생성
					URIBuilder uriBuilder = new URIBuilder(path + "?");
					
					for(String key : requestMap.keySet()) {
						String[] value = requestMap.get(key);
						if(value == null || value.length == 0) {
							continue;
						}else if(value.length == 1) {
							uriBuilder.addParameter(key, value[0]);
						}else {
							for(int i = 0; i < value.length; i++) {
								uriBuilder.addParameter(key, value[i]);
							}//end for
						}//end else
					}//end for
					
					uri = uriBuilder.build();
					
				} catch (Exception e) {
					e.printStackTrace();
				}//end try catch

				return uri;
		}
	
}
