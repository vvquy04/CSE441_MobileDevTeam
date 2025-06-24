<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Role;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;

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

        // Tạo tài khoản admin nếu chưa có
        $admin = User::firstOrCreate(
            ['email' => 'admin@tlu.edu.vn'],
            [
                'password' => Hash::make('password123'),
                'email_verified_at' => now(),
            ]
        );

        // Gán vai trò admin cho tài khoản
        $adminRole = Role::where('RoleName', 'admin')->first();
        if ($adminRole) {
            $admin->roles()->attach($adminRole->RoleId);
        }

        // Chỉ seed slot và chỉ insert user mới nếu chưa tồn tại
        $faculty1 = \App\Models\User::firstOrCreate(
            ['email' => 'faculty1@example.com'],
            [
                'password' => bcrypt('password'),
            ]
        );
        $faculty2 = \App\Models\User::firstOrCreate(
            ['email' => 'faculty2@example.com'],
            [
                'password' => bcrypt('password'),
            ]
        );
        // Seed available_slots
        DB::table('available_slots')->insertOrIgnore([
            [
                'faculty_user_id' => $faculty1->UserId,
                'StartTime' => '2025-07-01 08:00:00',
                'EndTime' => '2025-07-01 08:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => $faculty1->UserId,
                'StartTime' => '2025-07-01 09:00:00',
                'EndTime' => '2025-07-01 09:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => false,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => $faculty2->UserId,
                'StartTime' => '2025-07-02 10:00:00',
                'EndTime' => '2025-07-02 10:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => $faculty2->UserId,
                'StartTime' => '2025-07-02 11:00:00',
                'EndTime' => '2025-07-02 11:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => false,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => $faculty1->UserId,
                'StartTime' => '2025-07-03 14:00:00',
                'EndTime' => '2025-07-03 14:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            // Seed slot cho các faculty có id 6, 7, 8, 10 (SlotId tự tăng)
            [
                'faculty_user_id' => 6,
                'StartTime' => '2025-07-05 08:00:00',
                'EndTime' => '2025-07-05 08:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 6,
                'StartTime' => '2025-07-05 09:00:00',
                'EndTime' => '2025-07-05 09:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => false,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 7,
                'StartTime' => '2025-07-06 10:00:00',
                'EndTime' => '2025-07-06 10:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 8,
                'StartTime' => '2025-07-07 14:00:00',
                'EndTime' => '2025-07-07 14:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 10,
                'StartTime' => '2025-07-08 15:00:00',
                'EndTime' => '2025-07-08 15:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 10,
                'StartTime' => '2025-07-08 16:00:00',
                'EndTime' => '2025-07-08 16:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => false,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);

        // Seed available slots cho các faculty id 6, 7, 8, 10
        $slots = [
            [
                'faculty_user_id' => 6,
                'StartTime' => '2025-07-05 08:00:00',
                'EndTime' => '2025-07-05 08:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 6,
                'StartTime' => '2025-07-05 09:00:00',
                'EndTime' => '2025-07-05 09:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => false,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 7,
                'StartTime' => '2025-07-06 10:00:00',
                'EndTime' => '2025-07-06 10:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 8,
                'StartTime' => '2025-07-07 14:00:00',
                'EndTime' => '2025-07-07 14:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 10,
                'StartTime' => '2025-07-08 15:00:00',
                'EndTime' => '2025-07-08 15:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => true,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'faculty_user_id' => 10,
                'StartTime' => '2025-07-08 16:00:00',
                'EndTime' => '2025-07-08 16:30:00',
                'MaxStudents' => 1,
                'IsAvailable' => false,
                'DefinitionId' => null,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ];
        foreach ($slots as $slot) {
            \App\Models\AvailableSlot::firstOrCreate(
                [
                    'faculty_user_id' => $slot['faculty_user_id'],
                    'StartTime' => $slot['StartTime'],
                ],
                $slot
            );
        }
    }
} 