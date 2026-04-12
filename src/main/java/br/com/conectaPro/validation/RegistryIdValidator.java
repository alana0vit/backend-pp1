package br.com.conectaPro.validation;

import br.com.conectaPro.api.user.UserRequest;
import br.com.conectaPro.model.user.UserType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

public class RegistryIdValidator implements ConstraintValidator<ValidRegistryId, UserRequest> {

    private final CPFValidator cpfValidator = new CPFValidator();
    private final CNPJValidator cnpjValidator = new CNPJValidator();

    @Override
    public boolean isValid(UserRequest user, ConstraintValidatorContext context) {

        if (user.getRegistryId() == null || user.getUserType() == null) {
            return false;
        }

        if (user.getUserType() == UserType.CLIENT) {
            return cpfValidator.isValid(user.getRegistryId(), null);
        }

        if (user.getUserType() == UserType.PROFESSIONAL) {
            return cnpjValidator.isValid(user.getRegistryId(), null);
        }

        return false;
    }
}