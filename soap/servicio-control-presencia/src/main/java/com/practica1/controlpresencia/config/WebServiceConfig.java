package com.practica1.controlpresencia.config;

import com.practica1.controlpresencia.impl.ControlPresenciaImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {
    @Autowired private Bus bus;
    @Autowired private ControlPresenciaImpl impl;

    @Bean
    public Endpoint endpointControlPresencia() {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/controlpresencia");
        return endpoint;
    }
}
