<?php
require_once 'vendor/autoload.php';
$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

$student = \App\Models\StudentProfile::whereNotNull('avatar')->first();
if($student) {
    $avatarUrl = 'http://10.0.2.2:8000/storage/' . $student->avatar;
    echo "Avatar URL: " . $avatarUrl . "\n";
    
    // Test direct file access
    $filePath = storage_path('app/public/' . $student->avatar);
    echo "File path: " . $filePath . "\n";
    echo "File exists: " . (file_exists($filePath) ? 'YES' : 'NO') . "\n";
    echo "File size: " . (file_exists($filePath) ? filesize($filePath) : 'N/A') . " bytes\n";
    
    // Test HTTP access
    $context = stream_context_create([
        'http' => [
            'method' => 'GET',
            'timeout' => 10,
            'header' => [
                'User-Agent: Test/1.0'
            ]
        ]
    ]);
    
    $response = file_get_contents($avatarUrl, false, $context);
    if ($response !== false) {
        echo "HTTP access: SUCCESS\n";
        echo "Response length: " . strlen($response) . " bytes\n";
    } else {
        echo "HTTP access: FAILED\n";
        $error = error_get_last();
        echo "Error: " . ($error ? $error['message'] : 'Unknown error') . "\n";
    }
} else {
    echo "No student with avatar found\n";
} 