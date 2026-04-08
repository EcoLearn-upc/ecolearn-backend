package com.upc.ecolearn;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/ping")
    public String ping() {
        String dbName = mongoTemplate.getDb().getName();
        return "✅ Conectado a MongoDB: " + dbName;
    }

    @GetMapping("/colecciones")
    public Object colecciones() {
        return mongoTemplate.getCollectionNames();
    }
}