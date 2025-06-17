<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Department extends Model
{
    protected $primaryKey = 'DepartmentId';
    public $incrementing = false; // DepartmentId is not auto-incrementing in the current migration
    protected $keyType = 'string';
    
    protected $fillable = [
        'DepartmentId', // Allow DepartmentId to be fillable for manual assignment in seeder, etc.
        'DepartmentName'
    ];

    public function faculty()
    {
        return $this->hasMany(FacultyProfile::class, 'DepartmentId');
    }

    public function students()
    {
        return $this->hasMany(StudentProfile::class, 'DepartmentId');
    }
} 