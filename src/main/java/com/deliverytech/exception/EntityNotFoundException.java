package com.deliverytech.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s com ID %d não encontrado", entityName, id));
    }

}
