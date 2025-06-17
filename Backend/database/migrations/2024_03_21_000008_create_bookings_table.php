<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('bookings', function (Blueprint $table) {
            $table->id('BookingId');
            $table->foreignId('SlotId')->constrained('available_slots', 'SlotId');
            $table->foreignId('StudentUserId')->constrained('users', 'UserId');
            $table->dateTime('BookingTime');
            $table->string('Purpose', 500)->nullable();
            $table->string('Status', 50);
            $table->dateTime('CancellationTime')->nullable();
            $table->string('CancellationReason', 500)->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('bookings');
    }
}; 