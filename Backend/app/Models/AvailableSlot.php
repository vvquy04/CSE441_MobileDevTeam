<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AvailableSlot extends Model
{
    protected $primaryKey = 'SlotId';
    protected $fillable = [
        'faculty_user_id',
        'StartTime',
        'EndTime',
        'MaxStudents',
        'IsAvailable',
        'DefinitionId',
    ];

    protected $casts = [
        'StartTime' => 'datetime',
        'EndTime' => 'datetime',
        'IsAvailable' => 'boolean',
    ];

    public function faculty()
    {
        return $this->belongsTo(User::class, 'faculty_user_id', 'UserId');
    }

    // public function definition()
    // {
    //     return $this->belongsTo(OfficeHourDefinition::class, 'DefinitionId', 'DefinitionId');
    // }
} 