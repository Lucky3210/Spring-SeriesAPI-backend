package com.Series.SeriesAPI.DTO;

public record ResetPassword(
        String oldPassword,
        String newPassword,
        String repeatNewPassword
) {
}
