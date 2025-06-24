<?php

namespace App\Http\Controllers\Faculty;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Booking;
use App\Models\AvailableSlot;
use Illuminate\Support\Facades\Auth;

class BookingController extends Controller
{
    public function index()
    {
        $facultyId = Auth::id();
        
        $bookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->with(['student', 'slot'])
        ->orderBy('BookingTime', 'desc')
        ->paginate(15);
        
        return view('faculty.bookings.index', compact('bookings'));
    }

    public function show($id)
    {
        $facultyId = Auth::id();
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->with(['student', 'slot'])
        ->findOrFail($id);
        
        return view('faculty.bookings.show', compact('booking'));
    }

    public function approve($id)
    {
        $facultyId = Auth::id();
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'pending')
        ->findOrFail($id);

        $booking->Status = 'confirmed';
        $booking->save();

        return redirect()->route('faculty.bookings.index')
            ->with('success', 'Booking đã được phê duyệt thành công!');
    }

    public function reject(Request $request, $id)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'rejection_reason' => 'required|string|max:500'
        ]);
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'pending')
        ->findOrFail($id);

        $booking->Status = 'rejected';
        $booking->CancellationReason = $request->rejection_reason;
        $booking->CancellationTime = now();
        $booking->save();

        return redirect()->route('faculty.bookings.index')
            ->with('success', 'Booking đã được từ chối!');
    }

    public function cancel(Request $request, $id)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'cancellation_reason' => 'required|string|max:500'
        ]);
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->whereIn('Status', ['confirmed', 'pending'])
        ->findOrFail($id);

        $booking->Status = 'cancelled';
        $booking->CancellationReason = $request->cancellation_reason;
        $booking->CancellationTime = now();
        $booking->save();

        return redirect()->route('faculty.bookings.index')
            ->with('success', 'Booking đã được hủy thành công!');
    }

    public function pending()
    {
        $facultyId = Auth::id();
        
        $pendingBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'pending')
        ->with(['student', 'slot'])
        ->orderBy('BookingTime', 'asc')
        ->paginate(15);
        
        return view('faculty.bookings.pending', compact('pendingBookings'));
    }

    public function confirmed()
    {
        $facultyId = Auth::id();
        
        $confirmedBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'confirmed')
        ->with(['student', 'slot'])
        ->orderBy('BookingTime', 'asc')
        ->paginate(15);
        
        return view('faculty.bookings.confirmed', compact('confirmedBookings'));
    }

    public function completed()
    {
        $facultyId = Auth::id();
        
        $completedBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'completed')
        ->with(['student', 'slot'])
        ->orderBy('BookingTime', 'desc')
        ->paginate(15);
        
        return view('faculty.bookings.completed', compact('completedBookings'));
    }

    public function markCompleted($id)
    {
        $facultyId = Auth::id();
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'confirmed')
        ->findOrFail($id);

        $booking->Status = 'completed';
        $booking->save();

        return redirect()->route('faculty.bookings.index')
            ->with('success', 'Booking đã được đánh dấu hoàn thành!');
    }
} 