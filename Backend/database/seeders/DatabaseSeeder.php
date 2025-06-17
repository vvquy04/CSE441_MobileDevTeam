<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Role;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // Tạo các vai trò
        $roles = [
            ['RoleName' => 'admin'],
            ['RoleName' => 'faculty'],
            ['RoleName' => 'student']
        ];

        foreach ($roles as $role) {
            Role::firstOrCreate($role);
        }

        // Tạo tài khoản admin
        $admin = User::create([
            'email' => 'admin@tlu.edu.vn',
            'password' => Hash::make('password123'),
            'email_verified_at' => now(),
        ]);

        // Gán vai trò admin cho tài khoản
        $adminRole = Role::where('RoleName', 'admin')->first();
        if ($adminRole) {
            $admin->roles()->attach($adminRole->RoleId);
        }
    }
} 