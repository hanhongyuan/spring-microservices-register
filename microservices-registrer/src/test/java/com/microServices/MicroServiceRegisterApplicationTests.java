package com.microServices;

import com.microServices.cron.DirectoryCleaner;
import com.microServices.facade.DirectoryFacade;
import com.microServices.model.App;
import com.microServices.model.Config;
import com.microServices.model.Directory;
import com.microServices.model.EndPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by stephen on 28/02/2016.
 * Tests
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MicroServiceRegisterApplication.class)
public class MicroServiceRegisterApplicationTests {

    @Inject
    Directory directory;

    @Inject
    DirectoryFacade directoryFacade;


    @Inject
    App parentApp;

    @Mock
    Config config;

    @Inject
    @InjectMocks
    DirectoryCleaner directoryCleaner;
    private App childApp;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        // remove appTTL to test cleaning
        when(config.getAppTTL()).thenReturn(10);

        childApp = new App();
        childApp.setPath("/eu/rest");
        childApp.setApp("eu");
        childApp.setHostName("127.0.0.1");
        childApp.setPort(8080);
        childApp.setInstanceID("1");
        ArrayList<EndPoint> endPoints = new ArrayList<>();
        endPoints.add(new EndPoint("validate", HttpMethod.POST, EndPoint.ExecutePosition.AFTER));
        childApp.setEndPoints(endPoints);
        childApp.setParentApp(parentApp.getApp());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void registerApp() throws InterruptedException {
        directory.setRegisteredApp(new ArrayList<>());
        directoryFacade.register(parentApp);
        List<App> registerApps = directory.getRegisteredApps();
        assertEquals("There is one registered parentApp", 1, registerApps.size());
        assertEquals("My parentApp is registered", parentApp.getApp(), registerApps.get(0).getApp());
        Date oldLastUpdate = registerApps.get(0).getLastUpdate();

        //register second time the same parentApp
        Thread.sleep(100);
        directoryFacade.register(parentApp);
        registerApps = directory.getRegisteredApps();
        assertEquals("There is one registered parentApp", 1, registerApps.size());
        assertEquals("My parentApp is registered", parentApp.getApp(), registerApps.get(0).getApp());
        assertNotEquals("LastUpdated must have changed", oldLastUpdate, registerApps.get(0).getLastUpdate());
    }


    @Test
    public void findById() throws InterruptedException {
        directory.setRegisteredApp(new ArrayList<>());
        registerApp();
        Optional<App> registeredApp = directoryFacade.findRegisteredById(parentApp.getApp());
        assertTrue("Found a registered parentApp", registeredApp.isPresent());
        assertEquals("My parentApp is registered", parentApp.getApp(), registeredApp.get().getApp());

    }

    @Test
    public void appTTLToOld() throws InterruptedException {
        directory.setRegisteredApp(new ArrayList<>());
        registerApp();
        Thread.sleep(100);
        directoryCleaner.clean();
        List<App> registerApps = directory.getRegisteredApps();
        assertEquals("Clean must remove my parentApp", 0, registerApps.size());
    }

    @Test
    public void appTTLOk() throws InterruptedException {
        directory.setRegisteredApp(new ArrayList<>());
        registerApp();
        directoryCleaner.clean();
        List<App> registerApps = directory.getRegisteredApps();
        assertEquals("Clean must not remove my parentApp", 1, registerApps.size());
    }

    @Test
    public void appChildren() {
        directory.setRegisteredApp(new ArrayList<>());
        directoryFacade.register(parentApp);
        directoryFacade.register(childApp);
        List<App> registeredChildren = directoryFacade.findRegisteredChildrenById(parentApp.getApp());
        assertEquals("There is one registered childApp", 1, registeredChildren.size());
        assertEquals("My childApp is a child", childApp.getApp(), registeredChildren.get(0).getApp());
    }


}