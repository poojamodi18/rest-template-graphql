package com.resttemplate.resttemplategraphql.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class GraphQLRestController {

    static final String API_URL = "https://api.github.com/graphql";
    RestTemplate restTemplate = new RestTemplate();
    String userToken = "";
    static final String AUTH_PREFIX = "Bearer ";
    static final String HTTP_PREFIX = "Authorization";
    static final Logger LOG = LoggerFactory.getLogger(GraphQLRestController.class);

    @GetMapping(value = "/rest-template")
    public String restHome() {
        String url = "http://localhost:8080/";
        RestTemplate template = new RestTemplate();
        return template.getForObject(url, String.class);

    }

    JSONObject getBody(String query, HttpHeaders httpHeaders) {
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, new HttpEntity<>(query, httpHeaders), String.class);
        JSONObject obj = new JSONObject(response);
        return new JSONObject(obj.getString("body"));
    }

    @GetMapping(value = "/user/{token}")
    public Map<String, Object> userData(@PathVariable String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        LOG.info(token);
        httpHeaders.add(HTTP_PREFIX , AUTH_PREFIX + token);

        String query = "{\"query\":\"query { viewer{id} }\"}";
        JSONObject body = getBody(query, httpHeaders);
        userToken = token;
        return body.toMap();
    }

    @CrossOrigin
    @GetMapping(value = "/org1/{name}")
    public JsonNode org1Data(@PathVariable String name) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        LOG.info(name);

        LOG.info(userToken);
        httpHeaders.add(HTTP_PREFIX, AUTH_PREFIX + userToken);
        String query = "{\"query\":\"query{search(query: \\\"is:public " + name + " in:name type:org\\\" type: USER first: 15) {edges{node{...on Organization{name,login}}}}}\"}";
        JSONObject body = getBody(query, httpHeaders);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(body.getJSONObject("data").getJSONObject("search").toString());
    }

    @CrossOrigin
    @GetMapping(value = "/org/{name}")
    public Map<String, Object> orgData(@PathVariable String name) {
        HttpHeaders httpHeaders = new HttpHeaders();
        LOG.info(name);
        LOG.info(userToken);
        httpHeaders.add(HTTP_PREFIX, AUTH_PREFIX + userToken);
        String query = "{\"query\":\"query{search(query: \\\"is:public " + name + " in:name type:org\\\" type: USER first: 10) {edges{node{...on Organization{name,login}}}}}\"}";
        JSONObject body = getBody(query, httpHeaders);
        return body.getJSONObject("data").getJSONObject("search").toMap();
    }

    @GetMapping(value = "/repo/{name}")
    public Map<String, Object> repoData(@PathVariable String name) {
        HttpHeaders httpHeaders = new HttpHeaders();
        LOG.info(name);
        LOG.info(userToken);
        httpHeaders.add(HTTP_PREFIX, AUTH_PREFIX + userToken);
        String query = "{\"query\":\"query { organization(login: \\\"" + name + "\\\") { repositories(first: 15) { edges { repository:node { name } } } } }\"}";
        JSONObject body = getBody(query, httpHeaders);
        return body.toMap();
    }

    @GetMapping(value = "/api")
    public String graphQLTest() {

        HttpHeaders httpHeaders = new HttpHeaders();
        String toke = "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";
        httpHeaders.add(HTTP_PREFIX, AUTH_PREFIX + toke);

        String query = "{\"query\":\"query{ search( query: \\\"is:public key in:name type:org\\\" type: USER first: 70) {userCount edges{node{...on Organization{name}}}}}\"}";
        JSONObject body = getBody(query, httpHeaders);

        LOG.info(String.valueOf(body));
        return "api";
    }
}
