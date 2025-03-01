package com.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.vinipx.testngdoc.annotations.Docs;

/**
 * API Integration Test class demonstrating different test scenarios
 * This class simulates testing a REST API with various endpoints
 */
public class ApiIntegrationTest {
    
    private MockApiClient apiClient;
    
    @BeforeClass
    public void setupApiClient() {
        apiClient = new MockApiClient("https://api.example.com");
    }
    
    @Test
    @Docs(tags = {"api", "get", "positive"})
    public void testGetUserEndpoint() {
        // Test GET /users/{id} endpoint
        ApiResponse response = apiClient.get("/users/123");
        
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200 OK");
        Assert.assertNotNull(response.getBody(), "Response body should not be null");
        Assert.assertEquals(response.getContentType(), "application/json", "Content type should be JSON");
    }
    
    @Test
    @Docs(tags = {"api", "post", "positive"})
    public void testCreateUserEndpoint() {
        // Test POST /users endpoint
        String requestBody = "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}";
        ApiResponse response = apiClient.post("/users", requestBody);
        
        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201 Created");
        Assert.assertTrue(response.getBody().contains("\"id\":"), "Response should contain user ID");
    }
    
    @Test
    @Docs(tags = {"api", "put", "positive"})
    public void testUpdateUserEndpoint() {
        // Test PUT /users/{id} endpoint
        String requestBody = "{\"name\":\"John Updated\",\"email\":\"john.updated@example.com\"}";
        ApiResponse response = apiClient.put("/users/123", requestBody);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200 OK");
        Assert.assertTrue(response.getBody().contains("\"updated_at\":"), "Response should contain updated timestamp");
    }
    
    @Test
    @Docs(tags = {"api", "delete", "positive"})
    public void testDeleteUserEndpoint() {
        // Test DELETE /users/{id} endpoint
        ApiResponse response = apiClient.delete("/users/123");
        
        Assert.assertEquals(response.getStatusCode(), 204, "Status code should be 204 No Content");
        Assert.assertTrue(response.getBody().isEmpty(), "Response body should be empty");
    }
    
    @Test
    @Docs(tags = {"api", "get", "negative"})
    public void testGetNonExistentUser() {
        // Test GET /users/{id} with non-existent ID
        ApiResponse response = apiClient.get("/users/999");
        
        Assert.assertEquals(response.getStatusCode(), 404, "Status code should be 404 Not Found");
        Assert.assertTrue(response.getBody().contains("\"error\":"), "Response should contain error message");
    }
    
    // Mock classes for demonstration purposes
    
    private static class MockApiClient {
        private final String baseUrl;
        
        public MockApiClient(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public ApiResponse get(String endpoint) {
            // Simulate GET request
            if (endpoint.equals("/users/123")) {
                return new ApiResponse(200, "{\"id\":123,\"name\":\"John Doe\",\"email\":\"john@example.com\"}", "application/json");
            } else if (endpoint.equals("/users/999")) {
                return new ApiResponse(404, "{\"error\":\"User not found\"}", "application/json");
            }
            return new ApiResponse(500, "{\"error\":\"Internal server error\"}", "application/json");
        }
        
        public ApiResponse post(String endpoint, String body) {
            // Simulate POST request
            if (endpoint.equals("/users")) {
                return new ApiResponse(201, "{\"id\":456,\"name\":\"John Doe\",\"email\":\"john@example.com\"}", "application/json");
            }
            return new ApiResponse(500, "{\"error\":\"Internal server error\"}", "application/json");
        }
        
        public ApiResponse put(String endpoint, String body) {
            // Simulate PUT request
            if (endpoint.equals("/users/123")) {
                return new ApiResponse(200, "{\"id\":123,\"name\":\"John Updated\",\"email\":\"john.updated@example.com\",\"updated_at\":\"2025-03-01T12:00:00Z\"}", "application/json");
            }
            return new ApiResponse(500, "{\"error\":\"Internal server error\"}", "application/json");
        }
        
        public ApiResponse delete(String endpoint) {
            // Simulate DELETE request
            if (endpoint.equals("/users/123")) {
                return new ApiResponse(204, "", "application/json");
            }
            return new ApiResponse(500, "{\"error\":\"Internal server error\"}", "application/json");
        }
    }
    
    private static class ApiResponse {
        private final int statusCode;
        private final String body;
        private final String contentType;
        
        public ApiResponse(int statusCode, String body, String contentType) {
            this.statusCode = statusCode;
            this.body = body;
            this.contentType = contentType;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
        
        public String getBody() {
            return body;
        }
        
        public String getContentType() {
            return contentType;
        }
    }
}
