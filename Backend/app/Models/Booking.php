<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

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