<?php

// Test script to check API endpoint
$baseUrl = 'http://localhost:8000';

// Test 1: Check if server is running
echo "Testing server connection...\n";
$response = file_get_contents($baseUrl . '/api/test');
echo "Test endpoint response: " . $response . "\n\n";

// Test 2: Check faculty bookings by week (without auth - should fail)
echo "Testing faculty bookings by week without auth...\n";
$context = stream_context_create([
    'http' => [
        'method' => 'GET',
        'header' => 'Content-Type: application/json'
    ]
]);

$response = file_get_contents($baseUrl . '/api/faculty/bookings/by-week?start_date=2025-06-16&end_date=2025-06-22', false, $context);
echo "Response without auth: " . $response . "\n\n";

// Test 3: Check if we can get a token first
echo "Testing login to get token...\n";
$loginData = json_encode([
    'email' => 'nguyentuant@tlu.edu.vn',
    'password' => 'password123'
]);

$context = stream_context_create([
    'http' => [
        'method' => 'POST',
        'header' => 'Content-Type: application/json',
        'content' => $loginData
    ]
]);

$response = file_get_contents($baseUrl . '/api/login', false, $context);
echo "Login response: " . $response . "\n\n";

// Parse token from response
$loginResult = json_decode($response, true);
if (isset($loginResult['token'])) {
    $token = $loginResult['token'];
    echo "Got token: " . substr($token, 0, 20) . "...\n\n";
    
    // Test 4: Check faculty bookings by week with auth
    echo "Testing faculty bookings by week with auth...\n";
    $context = stream_context_create([
        'http' => [
            'method' => 'GET',
            'header' => 'Content-Type: application/json' . "\r\n" .
                       'Authorization: Bearer ' . $token
        ]
    ]);
    
    $response = file_get_contents($baseUrl . '/api/faculty/bookings/by-week?start_date=2025-06-16&end_date=2025-06-22', false, $context);
    echo "Response with auth: " . $response . "\n\n";
} else {
    echo "Failed to get token from login response\n";
} 