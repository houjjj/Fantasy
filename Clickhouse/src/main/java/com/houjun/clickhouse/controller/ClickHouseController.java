package com.houjun.clickhouse.controller;

import com.houjun.clickhouse.service.ClickHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clickhouse")
public class ClickHouseController {

    private final ClickHouseService clickHouseService;

    @Autowired
    public ClickHouseController(ClickHouseService clickHouseService) {
        this.clickHouseService = clickHouseService;
    }

    @GetMapping("/query")
    public ResponseEntity<List<Map<String, Object>>> query(@RequestParam String sql) {
        try {
            List<Map<String, Object>> result = clickHouseService.executeQuery(sql);
            return ResponseEntity.ok(result);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
