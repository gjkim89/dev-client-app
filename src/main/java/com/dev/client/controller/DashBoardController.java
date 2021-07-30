package com.dev.client.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.dev.client.model.Age;
import com.dev.client.model.Pie;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "dashboard")
public class DashBoardController {
	
	private final RestTemplate restTemplate;
	
	private final ObjectMapper mapper;
	
	@GetMapping(value = "status")
	public String getStatus() {
		
		return "views/dashboard";
	}
	
	@GetMapping(value = "member")
	public String getMember(Model model) {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String url = "http://localhost:8081/server/members?size=1000";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date(System.currentTimeMillis()));		
		
		int todayNum = 0;
		int notMember = 0;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			JsonNode node = mapper.readTree(result.getBody());
			
			model.addAttribute("members", node);
			
			for(int i = 0; i < node.get("content").size(); i++) {
				if(node.get("content").get(i).get("isMember").booleanValue() != false) {
					continue;
				}else if(node.get("content").get(i).get("isMember").booleanValue() == false) {
					notMember++;
				}
			}
			
			model.addAttribute("notMember", notMember);
			
			
			for(int i = 0; i < node.get("content").size(); i++) {
				if(!sdf.format(sdf.parse(node.get("content").get(i).get("createdTimeAt").textValue())).equals(today)) {
					continue;
				}else if(sdf.format(sdf.parse(node.get("content").get(i).get("createdTimeAt").textValue())).equals(today)) {
					todayNum++;
				}
			}
			
			model.addAttribute("todayMember", todayNum);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "/views/dashboard2";
	}
	
	@GetMapping(value = "pie")
	@ResponseBody
	public Pie getPie() {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String url = "http://localhost:8081/server/members?size=1000";
		
		Pie pie = null;
		
		int maleNum = 0;
		int femaleNum = 0;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			JsonNode members = mapper.readTree(result.getBody()).findPath("content");
			
			pie = new Pie();
			
			for(int i = 0; i < members.size(); i++) {
				if(members.get(i).get("sex").textValue().equals("남")) {
					maleNum++;
				}else if(members.get(i).get("sex").textValue().equals("여")) {
					femaleNum++;
				}
			}
			
			pie.setMale(maleNum);
			pie.setFemale(femaleNum);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pie;
		
	}
	
	@GetMapping(value = "age")
	@ResponseBody
	public Age getAge() {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String url = "http://localhost:8081/server/members?size=1000";
		
		Age age = null;
		
		int teenNum = 0;
		int twentyNum = 0;
		int thirtyNum = 0;
		int fourtyNum = 0;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			JsonNode members = mapper.readTree(result.getBody()).findPath("content");
			
			age = new Age();
			
			for(int i = 0; i < members.size(); i++) {
				if(members.get(i).get("age").intValue() >= 10 && members.get(i).get("age").intValue() < 20) {
					teenNum++;
				}else if(members.get(i).get("age").intValue() >= 20 && members.get(i).get("age").intValue() < 30) {
					twentyNum++;
				}else if(members.get(i).get("age").intValue() >= 30 && members.get(i).get("age").intValue() < 40) {
					thirtyNum++;
				}else if(members.get(i).get("age").intValue() >= 40 && members.get(i).get("age").intValue() < 50) {
					fourtyNum++;
				}else {
					continue;
				}
			}
			
			age.setTeenager(teenNum);
			age.setTwenty(twentyNum);
			age.setThirty(thirtyNum);
			age.setFourty(fourtyNum);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return age;
	}
	
	@GetMapping(value = "date")
	@ResponseBody
	public LinkedHashMap<String, Integer> getDate() {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String url = "http://localhost:8081/server/members?size=1000";
		
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date(System.currentTimeMillis()));
		String day1 = sdf.format(new Date(System.currentTimeMillis()+(1000*60*60*24*-1)));
		String day2 = sdf.format(new Date(System.currentTimeMillis()+(1000*60*60*24*-2)));
		String day3 = sdf.format(new Date(System.currentTimeMillis()+(1000*60*60*24*-3)));
		String day4 = sdf.format(new Date(System.currentTimeMillis()+(1000*60*60*24*-4)));
		String day5 = sdf.format(new Date(System.currentTimeMillis()+(1000*60*60*24*-5)));
		String day6 = sdf.format(new Date(System.currentTimeMillis()+(1000*60*60*24*-6)));
		
		int todayNum = 0;
		int day1Num = 0;
		int day2Num = 0;
		int day3Num = 0;
		int day4Num = 0;
		int day5Num = 0;
		int day6Num = 0;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			JsonNode node = mapper.readTree(result.getBody()).findPath("content");
			
			
			for(int i = 0; i < node.size(); i++) {
				if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(today)) {
					todayNum++;
				}else if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(day1)) {
					day1Num++;
				}else if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(day2)) {
					day2Num++;
				}else if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(day3)) {
					day3Num++;
				}else if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(day4)) {
					day4Num++;
				}else if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(day5)) {
					day5Num++;
				}else if(sdf.format(sdf.parse(node.get(i).get("createdTimeAt").textValue())).equals(day6)) {
					day6Num++;
				}else {
					continue;
				}
			}
			
			
			map.put(day6, day6Num);
			map.put(day5, day5Num);
			map.put(day4, day4Num);
			map.put(day3, day3Num);
			map.put(day2, day2Num);
			map.put(day1, day1Num);
			map.put(today, todayNum);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
}
