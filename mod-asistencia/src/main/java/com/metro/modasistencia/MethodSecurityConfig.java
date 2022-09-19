package com.metro.modasistencia;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

// Clase para habilitar la configuracion de seguridad en el controlador
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true) //Se habilita la notacion @PreAuthorize
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

}
