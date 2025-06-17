<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('available_slots', function (Blueprint $table) {
            $table->id('SlotId');
            $table->foreignId('FacultyUserId')->constrained('users', 'UserId');
            $table->dateTime('StartTime');
            $table->dateTime('EndTime');
            $table->integer('MaxStudents')->default(1);
            $table->boolean('IsAvailable')->default(true);
            $table->foreignId('DefinitionId')->nullable()->constrained('office_hour_definitions', 'DefinitionId');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('available_slots');
    }
}; 