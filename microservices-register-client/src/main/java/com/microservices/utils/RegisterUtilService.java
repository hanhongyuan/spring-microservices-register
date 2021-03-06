package com.microservices.utils;

import com.microservices.ConfigurationTester;
import com.microservices.RegisterThread;
import com.microservices.model.application.Application;
import com.microservices.dto.AppListDTO;
import com.microservices.model.application.EndPoint;
import com.microservices.model.Register;
import com.microservices.producer.AppProducer;
import com.microservices.producer.RegisterProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by stephen on 27/02/2016.
 */
@Named
public class RegisterUtilService<T> {
    private static final Logger logger = LoggerFactory.getLogger(RegisterUtilService.class);

    @Inject
    ConfigurationTester configurationTester;

    @Inject
    public RegisterWSUtilService registerWSUtilsService;

    @Inject
    public AppProducer appProducer;

    @Inject
    public RegisterProducer registerProducer;

    @Inject
    private ChildrenWSUtilService<T> childrenWSUtilsService;

    private Application currentApp;
    private Register register;

    public RegisterUtilService() {
        super();
    }

    @PostConstruct
    public void init(){
        logger.debug("RegisterUtilService:init");
        currentApp = appProducer.get();
        register = registerProducer.get();
        configurationTester.checkRegisterConfiguration(register);
    }

    /**
     * Call this to register your service
     * Required register.yml file inside your resource folder
     */
    public void register() {
        logger.debug("RegisterUtilService:register");
        RegisterThread registerInit = new RegisterThread(currentApp, register);
        registerInit.start();
    }

    /**
     * Call this to unRegister your service
     * Required register.yml file inside your resource folder
     */
    public void unregister() {
        logger.debug("RegisterUtilService:unregister");
        Response response = registerWSUtilsService.callUnregisterWS();

        if (!isOk(response)) {
            throw new RuntimeException("Unable to unRegister application " + currentApp.getId());
        }
    }

    /**
     * Call this to get app your service's children
     * Required register.yml file inside your resource folder
     *
     * @return
     */
    public List<Application> getChildren() {
        logger.debug("RegisterUtilService:getChildren");
        AppListDTO result = null;
        try {
            result = (AppListDTO) registerWSUtilsService.callChildrenWS(AppListDTO.class);
        }catch (Exception ex){
            logger.error("From "+currentApp.getId()+": Unable to getChildren application ",ex);
            return null;
        }
        Preconditions.checkNotNull(result,"From "+currentApp.getId()+": Unable to getChildren application ");
        return result.getData();
    }

    /**
     * Call this to execute endPoint on your service's children
     * Required register.yml file inside your resource folder
     *
     * @param childApp
     * @param endPoint
     * @param param
     * @return
     */
    public T executeOnChild(Application childApp, EndPoint endPoint, T param) {
        logger.debug("RegisterUtilService:executeOnChild "+ childApp + ", " + endPoint);
        T result = param;
        // execute only on child that have same endPoints
        if (childApp.getEndPoints().contains(endPoint)) {
            try {
                result = childrenWSUtilsService.executeOnChildrenWS(childApp, endPoint, result);
            } catch (Exception e) {
                logger.error("From Application "+currentApp.getId()+":"+currentApp.getInstanceId()
                        +" Unable to execute endPoint on children application " + childApp.getId()+":"+childApp.getInstanceId() +
                        " " + endPoint + " ", e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private boolean isOk(Response response) {
        return response.getStatus() == 200 || response.getStatus() == 204;
    }
}
