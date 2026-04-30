package com.practica1.controlaccesos.config;

import com.practica1.controlaccesos.impl.ControlAccesosImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServiceConfig {

    @Autowired private Bus bus;
    @Autowired private ControlAccesosImpl impl;

    @Bean
    public Endpoint endpointControlAccesos() {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/controlaccesos");
        return endpoint;
    }
}
