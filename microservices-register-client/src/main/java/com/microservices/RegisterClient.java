package com.microservices;

import com.microservices.model.App;
import com.microservices.model.EndPoint;
import com.microservices.model.Entity;
import com.microservices.model.StackTraceWSElement;
import com.microservices.utils.RegisterUtilService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by stephen on 27/02/2016.
 */
@Component
@Singleton
@EnableScheduling
public class RegisterClient<T> {

    @Inject
    ConfigurationTester configurationTester;

    @Inject
    RegisterUtilService<T> registerUtil;

    @Inject
    App app;
    /**
     * Children list
     */
    private List<App> childrenApp;

    @PostConstruct
    public void init() {
        //test configuration file
        configurationTester.testConfiguration();
        registerUtil.register();
        getChildren();
    }

    @PreDestroy
    public void destroy() {
        registerUtil.unregister();
    }

    @Scheduled(fixedDelayString  = "${config.heartBeatDelay}")
    public void getChildren() {
        // get childs
        childrenApp = registerUtil.getChildren().getData();
    }
    /**
     * Add StackCall inside entity
     * @param entity
     * @param endPoint
     * @return
     */
    public Entity addStackCall(Entity entity, EndPoint endPoint) {
        entity.getStackTrace().add(new StackTraceWSElement(app,endPoint));
        return entity;
    }

    /**
     * Return endPoint that match with method
     * @param localClass
     * @return
     */
    public EndPoint getEndPoint(Class localClass) {
        Method m = localClass.getEnclosingMethod();
        String path = ((Path)m.getAnnotationsByType(Path.class)[0]).value();
        String method = null;


        if(m.isAnnotationPresent(POST.class)) {
            method = HttpMethod.POST;
        }else if(m.isAnnotationPresent(GET.class)) {
            method = HttpMethod.GET;
        }else if(m.isAnnotationPresent(PUT.class)) {
            method = HttpMethod.PUT;
        }else if(m.isAnnotationPresent(DELETE.class)) {
            method = HttpMethod.DELETE;
        }
        EndPoint endPoint = new EndPoint(path, method) ;
        return endPoint;
    }


    public List<App> getChildrenApp() {
        return childrenApp;
    }

    public void setChildrenApp(List<App> childrenApp) {
        this.childrenApp = childrenApp;
    }

    public RegisterUtilService<T> getRegisterUtil() {
        return registerUtil;
    }

    public void setRegisterUtil(RegisterUtilService<T> registerUtil) {
        this.registerUtil = registerUtil;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
