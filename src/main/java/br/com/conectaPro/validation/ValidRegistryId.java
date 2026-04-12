package br.com.conectaPro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegistryIdValidator.class)
public @interface ValidRegistryId {

    String message() default "CPF/CNPJ inválido para o tipo de usuário";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}