package com.tushar.dbassist.dbassist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
public class MongoConnectionProvider {

    public MongoClient createMongoClient(String connectionString) {
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalArgumentException("MongoDB connection string must be provided.");
        }
        return MongoClients.create(connectionString);
    }

    public MongoDatabase getDatabase(MongoClient client, String databaseName) {
        Objects.requireNonNull(client, "MongoClient cannot be null.");
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("MongoDB database name must be provided.");
        }
        return client.getDatabase(databaseName);
    }

    public String generateSchemaSummary(MongoDatabase database) {
        List<String> collectionNames = new ArrayList<>();
        database.listCollectionNames().into(collectionNames);

        StringBuilder english = new StringBuilder();
        english.append("The database '").append(database.getName()).append("' contains ")
                .append(collectionNames.size()).append(collectionNames.size() == 1 ? " collection." : " collections.");

        if (collectionNames.isEmpty()) {
            return english.toString();
        }

        for (String collectionName : collectionNames) {
            english.append("\n\nThe '").append(collectionName).append("' collection includes fields such as ");
            MongoCollection<Document> collection = database.getCollection(collectionName);
            List<Document> sampleDocuments = collection.find().limit(10).into(new ArrayList<>());
            if (sampleDocuments.isEmpty()) {
                english.append("no documents yet.");
                continue;
            }

            Map<String, Set<String>> fieldTypes = new TreeMap<>();
            for (Document document : sampleDocuments) {
                collectFieldTypes(document, fieldTypes, "");
            }

            int count = 0;
            List<String> sampleFields = new ArrayList<>();
            for (Map.Entry<String, Set<String>> fieldEntry : fieldTypes.entrySet()) {
                if (count >= 8) {
                    break;
                }
                String types = String.join(" or ", fieldEntry.getValue());
                sampleFields.add(fieldEntry.getKey() + " (" + types + ")");
                count++;
            }

            english.append(String.join(", ", sampleFields));
            if (fieldTypes.size() > sampleFields.size()) {
                english.append(", and more fields.");
            } else {
                english.append(".");
            }
        }

        return english.toString();
    }

    private void collectFieldTypes(Document document, Map<String, Set<String>> fieldTypes, String prefix) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            String fieldName = prefix.isBlank() ? entry.getKey() : prefix + "." + entry.getKey();
            String typeName = inferType(entry.getValue());
            fieldTypes.computeIfAbsent(fieldName, key -> new TreeSet<>()).add(typeName);

            if (entry.getValue() instanceof Document nestedDoc) {
                collectFieldTypes(nestedDoc, fieldTypes, fieldName);
            }
        }
    }

    private String inferType(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Document) {
            return "document";
        }
        if (value instanceof List<?>) {
            return "array";
        }
        if (value instanceof Integer) {
            return "int";
        }
        if (value instanceof Long) {
            return "long";
        }
        if (value instanceof Double) {
            return "double";
        }
        if (value instanceof Boolean) {
            return "boolean";
        }
        if (value instanceof String) {
            return "string";
        }
        if (value instanceof java.util.Date) {
            return "date";
        }
        return value.getClass().getSimpleName().toLowerCase();
    }
}
