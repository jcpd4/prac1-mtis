package com.practica1.empleados.config;

import com.practica1.empleados.impl.EmpleadosImpl;
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
    private EmpleadosImpl empleadosImpl;

    @Bean
    public Endpoint endpointEmpleados() {
        EndpointImpl endpoint = new EndpointImpl(bus, empleadosImpl);
        endpoint.publish("/empleados");
        return endpoint;
    }
}
