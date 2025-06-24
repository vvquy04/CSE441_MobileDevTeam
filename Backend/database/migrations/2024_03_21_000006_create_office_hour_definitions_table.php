<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('office_hour_definitions', function (Blueprint $table) {
            $table->id('DefinitionId');
            $table->foreignId('faculty_user_id')->constrained('users', 'UserId');
            $table->integer('DayOfWeek');
            $table->time('StartTime');
            $table->time('EndTime');
            $table->integer('SlotDurationMinutes');
            $table->integer('MaxStudentsPerSlot');
            $table->string('Note', 500)->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('office_hour_definitions');
    }
}; 