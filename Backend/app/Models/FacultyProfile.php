<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class FacultyProfile extends Model
{
    use HasFactory;

    protected $primaryKey = 'faculty_user_id';
    protected $fillable = [
        'faculty_user_id',
        'faculty_name',
        'department_id',
        'degree',
        'phone_number',
        'office_location',
        'avatar'
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'faculty_user_id', 'UserId');
    }

    public function department()
    {
        return $this->belongsTo(Department::class, 'department_id', 'DepartmentId');
    }
} 