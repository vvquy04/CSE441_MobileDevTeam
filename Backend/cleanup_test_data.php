<?php

require_once 'vendor/autoload.php';

$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use App\Models\StudentProfile;
use App\Models\UserRole;

echo "Cleaning up test data...\n";

// Xóa user test@student.com
$testUser = User::where('email', 'test@student.com')->first();
if ($testUser) {
    // Xóa student profile
    StudentProfile::where('StudentUserId', $testUser->UserId)->delete();
    echo "Deleted student profile for test user\n";
    
    // Xóa user roles
    UserRole::where('UserId', $testUser->UserId)->delete();
    echo "Deleted user roles for test user\n";
    
    // Xóa personal access tokens
    $testUser->tokens()->delete();
    echo "Deleted personal access tokens for test user\n";
    
    // Xóa user
    $testUser->delete();
    echo "Deleted test user\n";
} else {
    echo "Test user not found\n";
}

echo "Cleanup completed!\n";
echo "Remaining users: ";
echo User::all()->pluck('UserId', 'email');
echo "\n"; 