package com.dev.client.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.dev.client.dto.MemberDto;
import com.dev.client.util.ConvertUriUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "client")
public class MemberController {
	
	private final RestTemplate restTemplate;
	
	private final ObjectMapper mapper;
	
	private final ConvertUriUtil convertUriUtil;
	
	/* member 전체 조회 */
	@GetMapping(value = "members")
	public String getMembers(Model model, HttpServletRequest request) throws URISyntaxException {
		
		//System.out.println(request.getQueryString());
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String path = "http://localhost:8081/server/members";
		URI uri = new URI(path);
		
		//요청 파라미터 있을 시 분기 로직
		Map<String, String[]> requestMap = request.getParameterMap();
		if(requestMap.isEmpty()) {
			log.info("요청 파라미터 없음");
		}else {
			uri = convertUriUtil.convertUriBuilder(requestMap, path);
			log.info("current URL : {}", uri);
		}//end else
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
			JsonNode node = mapper.readTree(result.getBody());
			
			model.addAttribute("members", node);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "/views/home";
	}
	
	/* memeber 등록 */
	@PostMapping(value = "members")
	@ResponseBody
	public void addMember(@RequestBody MemberDto memberDto) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<MemberDto> httpEntity = new HttpEntity<MemberDto>(memberDto, headers);
		String url = "http://localhost:8081/server/members";
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
			log.info("result : {}", result.getBody());
			log.info("success added : {}", memberDto.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		
	}
	
	/* member 조회 */
	@GetMapping(value = "members/{rowNumber}")
	@ResponseBody
	public JsonNode getMember(@PathVariable long rowNumber){
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String url = "http://localhost:8081/server/members/" + rowNumber;
		
		JsonNode member = null;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			member = mapper.readTree(result.getBody());
			log.info("조회한 member : {}", member);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return member;
		
	}
	
	/* member 수정 */
	@PutMapping(value = "members/{rowNumber}")
	@ResponseBody
	public void modifyMember(@RequestBody MemberDto memberDto, @PathVariable long rowNumber) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<MemberDto> httpEntity = new HttpEntity<MemberDto>(memberDto ,headers);
		String url = "http://localhost:8081/server/members/" + rowNumber;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
			log.info("modify result : {}", result.getBody());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		
	}
	
	/* member 삭제 */
	@DeleteMapping(value = "members/{rowNumber}")
	@ResponseBody
	public void deleteMember(@PathVariable long rowNumber) {
		
		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String url = "http://localhost:8081/server/members/" + rowNumber;
		
		try {
			
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
			log.info("delete result : {}", result.getBody());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
