<?php

namespace App\Models;

// HEAD
use Illuminate\Database\Eloquent\Factories\HasFactory;
//
// vanquy_refactor
use Illuminate\Database\Eloquent\Model;

class AvailableSlot extends Model
{
// HEAD
    use HasFactory;

    protected $primaryKey = 'SlotId';
    
//
    protected $primaryKey = 'SlotId';
// vanquy_refactor
    protected $fillable = [
        'faculty_user_id',
        'StartTime',
        'EndTime',
        'MaxStudents',
        'IsAvailable',
// HEAD
        'DefinitionId'
//
        'DefinitionId',
// vanquy_refactor
    ];

    protected $casts = [
        'StartTime' => 'datetime',
        'EndTime' => 'datetime',
// HEAD
        'IsAvailable' => 'boolean'
//
        'IsAvailable' => 'boolean',
// vanquy_refactor
    ];

    public function faculty()
    {
        return $this->belongsTo(User::class, 'faculty_user_id', 'UserId');
    }

// HEAD
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
//
    // public function definition()
    // {
    //     return $this->belongsTo(OfficeHourDefinition::class, 'DefinitionId', 'DefinitionId');
    // }
// vanquy_refactor
} 