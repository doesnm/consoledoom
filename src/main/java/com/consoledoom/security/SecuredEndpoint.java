// src/main/java/com/consoledoom/security/SecuredEndpoint.java
package com.consoledoom.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking methods that require specific permissions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecuredEndpoint {
    Permission[] requiredPermissions();

    Role minimumRole() default Role.PLAYER;
}
