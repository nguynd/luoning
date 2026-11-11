package com.yuddy.luoning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// Validation dat thang tren thanh phan cua record, @Valid van hoat dong nhu class thuong
public record ExamConfigRequest(
        @Min(value = 0, message = "So cau khong duoc am") @Max(value = 200, message = "Toi da 200 cau moi muc") int level1Count,
        @Min(value = 0, message = "So cau khong duoc am") @Max(value = 200, message = "Toi da 200 cau moi muc") int level2Count,
        @Min(value = 0, message = "So cau khong duoc am") @Max(value = 200, message = "Toi da 200 cau moi muc") int level3Count,
        @Min(value = 1, message = "Thoi gian toi thieu 1 phut") @Max(value = 300, message = "Thoi gian toi da 300 phut") int durationMinutes,
        boolean open) {
}
