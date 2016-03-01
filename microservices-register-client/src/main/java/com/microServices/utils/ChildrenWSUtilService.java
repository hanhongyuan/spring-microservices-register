package com.microservices.utils;

import com.microservices.model.App;
import com.microservices.model.EndPoint;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by stephen on 27/02/2016.
 */
@Component
@Singleton
public class ChildrenWSUtilService<T> {
    /**
     * @param app
     * @param endPoint
     * @param result
     * @return
     */
    public T executeOnChildrenWS(App app, EndPoint endPoint, T result) {

        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target("http://" + app.getHostName() + ":"
                + app.getPort());
        target = target.path( "/" +app.getPath()+"/"+ endPoint.getPath());

        Invocation.Builder builder = target.request();
        builder.accept(MediaType.APPLICATION_JSON);
        switch (endPoint.getMethod()) {
            case HttpMethod.GET:
                return builder.get((Class<T>) result.getClass().getClass());
            case HttpMethod.POST:
                return builder.post(Entity.json(result), (Class<T>) result.getClass());
            case HttpMethod.PUT:
                return builder.put(Entity.json(result), (Class<T>) result.getClass());
            case HttpMethod.DELETE:
                return builder.delete((Class<T>) result.getClass());
            default:
                throw new UnsupportedOperationException("Unable to execute method " + endPoint.getMethod());
        }
    }

}
