<?php
require_once 'vendor/autoload.php';
$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

$student = \App\Models\StudentProfile::whereNotNull('avatar')->first();
if($student) {
    $avatarUrl = asset('storage/' . $student->avatar);
    echo "Avatar URL: " . $avatarUrl . "\n";
    echo "File path: " . storage_path('app/public/' . $student->avatar) . "\n";
    echo "File exists: " . (file_exists(storage_path('app/public/' . $student->avatar)) ? 'YES' : 'NO') . "\n";
    
    // Test if URL is accessible
    $headers = get_headers($avatarUrl);
    echo "HTTP Response: " . $headers[0] . "\n";
} else {
    echo "No student with avatar found\n";
} 