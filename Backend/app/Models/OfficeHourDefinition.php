<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class OfficeHourDefinition extends Model
{
    use HasFactory;

    protected $primaryKey = 'DefinitionId';
    
    protected $fillable = [
        'faculty_user_id',
        'DayOfWeek',
        'StartTime',
        'EndTime',
        'SlotDurationMinutes',
        'MaxStudentsPerSlot',
        'Note'
    ];

    protected $casts = [
        'StartTime' => 'datetime',
        'EndTime' => 'datetime'
    ];

    public function faculty()
    {
        return $this->belongsTo(User::class, 'faculty_user_id', 'UserId');
    }

    public function slots()
    {
        return $this->hasMany(AvailableSlot::class, 'DefinitionId', 'DefinitionId');
    }

    public function getDayNameAttribute()
    {
        $days = [
            1 => 'Thứ 2',
            2 => 'Thứ 3', 
            3 => 'Thứ 4',
            4 => 'Thứ 5',
            5 => 'Thứ 6',
            6 => 'Thứ 7',
            0 => 'Chủ nhật'
        ];
        
        return $days[$this->DayOfWeek] ?? 'Không xác định';
    }
} 