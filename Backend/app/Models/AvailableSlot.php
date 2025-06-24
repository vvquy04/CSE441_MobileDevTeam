<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AvailableSlot extends Model
{
    use HasFactory;

    protected $primaryKey = 'SlotId';
    
    protected $fillable = [
        'faculty_user_id',
        'StartTime',
        'EndTime',
        'MaxStudents',
        'IsAvailable',
        'DefinitionId'
    ];

    protected $casts = [
        'StartTime' => 'datetime',
        'EndTime' => 'datetime',
        'IsAvailable' => 'boolean'
    ];

    public function faculty()
    {
        return $this->belongsTo(User::class, 'faculty_user_id', 'UserId');
    }

    public function bookings()
    {
        return $this->hasMany(Booking::class, 'SlotId', 'SlotId');
    }

    public function definition()
    {
        return $this->belongsTo(OfficeHourDefinition::class, 'DefinitionId', 'DefinitionId');
    }

    public function getCurrentBookingsCountAttribute()
    {
        return $this->bookings()->whereIn('Status', ['pending', 'confirmed'])->count();
    }

    public function getIsFullAttribute()
    {
        return $this->current_bookings_count >= $this->MaxStudents;
    }

    public function getAvailableSpotsAttribute()
    {
        return max(0, $this->MaxStudents - $this->current_bookings_count);
    }
} 