package com.practica1.validaciones.config;

import com.practica1.validaciones.impl.ValidacionesImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {

    @Autowired
    private Bus bus;

    @Autowired
    private ValidacionesImpl validacionesImpl;

    @Bean
    public Endpoint endpointValidaciones() {
        EndpointImpl endpoint = new EndpointImpl(bus, validacionesImpl);
        endpoint.publish("/validaciones");
        return endpoint;
    }
}
