package com.tushar.dbassist.dbassist.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.MongoClient;
import com.tushar.dbassist.dbassist.model.MongoConnectionRequest;
import com.tushar.dbassist.dbassist.model.MongoSchemaSummaryResponse;
import com.tushar.dbassist.dbassist.service.ConnectionContextStore;
import com.tushar.dbassist.dbassist.service.MongoConnectionProvider;

@RestController
@RequestMapping("/api/mongo")
@CrossOrigin(origins = "*")
public class MongoController {

    @Autowired
    private MongoConnectionProvider mongoConnectionProvider;

    @Autowired
    private ConnectionContextStore connectionContextStore;

    @PostMapping("/schema-summary")
    public ResponseEntity<MongoSchemaSummaryResponse> schemaSummary(@RequestBody MongoConnectionRequest request) {
        try (MongoClient client = mongoConnectionProvider.createMongoClient(request.getConnectionString())) {
            var database = mongoConnectionProvider.getDatabase(client, request.getDatabaseName());
            List<String> collections = new ArrayList<>();
            database.listCollectionNames().into(collections);
            String summary = mongoConnectionProvider.generateSchemaSummary(database);
            String connectionKey = request.getConnectionString() + "::" + request.getDatabaseName();
            connectionContextStore.storeSchemaSummary(connectionKey, summary);

            MongoSchemaSummaryResponse response = new MongoSchemaSummaryResponse(
                    true,
                    "MongoDB schema summary generated successfully.",
                    request.getDatabaseName(),
                    collections,
                    summary
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MongoSchemaSummaryResponse errorResponse = new MongoSchemaSummaryResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Unable to generate MongoDB schema summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
