package com.phungquocthai.symphony.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import com.phungquocthai.symphony.validator.FileValidator;

@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {
	String message() default "File không hợp lệ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSize() default 5 * 1024 * 1024; // 5MB mặc định
    String[] allowedContentTypes() default {"image/jpeg", "image/png", "image/gif"};
}
