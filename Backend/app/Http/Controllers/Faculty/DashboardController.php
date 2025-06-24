<?php

namespace App\Http\Controllers\Faculty;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Booking;
use App\Models\AvailableSlot;
use App\Models\User;
use Illuminate\Support\Facades\Auth;

class DashboardController extends Controller
{
    public function index()
    {
        $facultyId = Auth::id();
        
        // Lấy thống kê tổng quan
        $data = [
            'total_slots' => AvailableSlot::where('faculty_user_id', $facultyId)->count(),
            'available_slots' => AvailableSlot::where('faculty_user_id', $facultyId)
                ->where('IsAvailable', true)
                ->count(),
            'total_bookings' => Booking::whereHas('slot', function($query) use ($facultyId) {
                $query->where('faculty_user_id', $facultyId);
            })->count(),
            'pending_bookings' => Booking::whereHas('slot', function($query) use ($facultyId) {
                $query->where('faculty_user_id', $facultyId);
            })->where('Status', 'pending')->count(),
            'confirmed_bookings' => Booking::whereHas('slot', function($query) use ($facultyId) {
                $query->where('faculty_user_id', $facultyId);
            })->where('Status', 'confirmed')->count(),
            'recent_bookings' => Booking::with(['student', 'slot'])
                ->whereHas('slot', function($query) use ($facultyId) {
                    $query->where('faculty_user_id', $facultyId);
                })
                ->latest('BookingTime')
                ->take(5)
                ->get(),
            'upcoming_slots' => AvailableSlot::where('faculty_user_id', $facultyId)
                ->where('StartTime', '>', now())
                ->where('IsAvailable', true)
                ->orderBy('StartTime')
                ->take(5)
                ->get()
        ];

        return view('faculty.dashboard', $data);
    }
} 