<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;

class Booking extends Model
{
    protected $primaryKey = 'BookingId';
    
    protected $fillable = [
        'SlotId',
        'StudentUserId',
        'BookingTime',
        'Purpose',
        'Status',
        'CancellationTime',
        'CancellationReason'
    ];

    protected $casts = [
        'BookingTime' => 'datetime',
        'CancellationTime' => 'datetime'
    ];

    protected $appends = ['booking_date', 'booking_time_range'];

    public function getBookingDateAttribute()
    {
        if ($this->slot) {
            return Carbon::parse($this->slot->StartTime)->format('d/m/Y');
        }
        return null;
    }

    public function getBookingTimeRangeAttribute()
    {
        if ($this->slot) {
            $startTime = Carbon::parse($this->slot->StartTime)->format('H:i');
            $endTime = Carbon::parse($this->slot->EndTime)->format('H:i');
            return $startTime . ' - ' . $endTime;
        }
        return null;
    }

    public function slot()
    {
        return $this->belongsTo(AvailableSlot::class, 'SlotId');
    }

    public function student()
    {
        return $this->belongsTo(User::class, 'StudentUserId');
    }

    public function faculty()
    {
        return $this->hasOneThrough(
            User::class,
            AvailableSlot::class,
            'SlotId',
            'UserId',
            'SlotId',
            'faculty_user_id'
        );
    }
}