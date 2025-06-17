<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Booking;
use App\Models\Department;
use App\Models\Role;

class DashboardController extends Controller
{
    public function index()
    {
        $adminRole = Role::where('RoleName', 'admin')->first();
        $facultyRole = Role::where('RoleName', 'faculty')->first();
        $studentRole = Role::where('RoleName', 'student')->first();

        $data = [
            'total_users' => User::count(),
            'total_faculty' => $facultyRole ? User::whereHas('roles', function($q) use ($facultyRole) {
                $q->where('roles.RoleId', $facultyRole->RoleId);
            })->count() : 0,
            'total_students' => $studentRole ? User::whereHas('roles', function($q) use ($studentRole) {
                $q->where('roles.RoleId', $studentRole->RoleId);
            })->count() : 0,
            'total_bookings' => Booking::count(),
            'total_departments' => Department::count(),
            'recent_bookings' => Booking::with(['student', 'faculty'])
                ->latest()
                ->take(5)
                ->get()
        ];

        return view('admin.dashboard', $data);
    }
} 