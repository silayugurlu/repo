package com.company.lighting.controller;

import com.company.lighting.dto.InstallationDto;
import com.company.lighting.dto.InstallationDtoBuilder;
import com.company.lighting.exception.InvalidLinkException;
import com.company.lighting.exception.NoInstallationFound;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author silay.ugurlu
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InstallationControllerIntegrationTest {

    @Autowired
    InstallationController installationController;

    @Test
    public void testImportInstallationOfflineLamps() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1")
                .setLamps("l1", "l2")
                .addLink("g1", "l1")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(!result);

    }

    @Test
    public void testImportInstallationOfflineLampsToOnline() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1")
                .setLamps("l1", "l2")
                .addLink("g1", "l1")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(!result);

        installationController.makeAllLampsOnline("test");

        boolean result1 = installationController.checkAllLampsOnline("test");
        assertTrue(result1);

    }

    @Test
    public void testImportInstallationOnlineLampsTwoGateways() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1", "g2")
                .setLamps("l1", "l2")
                .addLink("g1", "l1")
                .addLink("g2", "l2")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(result);

    }

    @Test
    public void testImportInstallationOnlineLampsOneGateway() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1")
                .setLamps("l1", "l2", "l3")
                .addLink("g1", "l1")
                .addLink("l1", "l2")
                .addLink("l3", "l2")
                .addLink("l3", "l1")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(result);

    }

    @Test
    public void testImportInstallationNoLamps() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1", "g2")
                .addLink("g1", "g2")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(result);

    }

    @Test
    public void testImportInstallationNoData() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(result);

    }

    @Test
    public void testImportInstallationNoGateways() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setLamps("l1", "l2")
                .addLink("l2", "l1")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(!result);

    }

    @Test(expected = NoInstallationFound.class)
    public void testInstallationNotFound() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setLamps("l1", "l2")
                .addLink("l2", "l1")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        installationController.getInstallation("testNotImported");
    }

    @Test(expected = InvalidLinkException.class)
    public void testImportInstallationInvalidLink() throws InvalidLinkException {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1", "g2")
                .setLamps("l1", "l2")
                .addLink("g1", "l1")
                .addLink("l2", "l5")
                .build();

        installationController.importInstallation(installationDto);
    }

    @Test
    public void testImportInstallationTooManyElements() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1")
                .setLamps("l1", "l2")
                .addLink("g1", "l1")
                .addLink("l2", "l1")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(result);

    }

    @Test
    public void testImportPartialOfflineInstallationTooManyElements() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1", "g2")
                .setLamps("l1", "l2", "l3", "l4", "l5", "l6", "l7", "l8", "l9", "l10", "l11", "l12")
                .addLink("g2", "l1")
                .addLink("l2", "l1")
                //
                .addLink("g1", "l3")
                .addLink("g1", "l4")
                .addLink("l4", "l5")
                .addLink("l6", "l5")
                //
                .addLink("l7", "l8")
                .addLink("l12", "l8")
                .addLink("l12", "l7")
                //
                .addLink("l9", "l10")
                .addLink("l10", "l11")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(!result);

        installationController.makeAllLampsOnline("test");
        boolean newResult = installationController.checkAllLampsOnline("test");
        assertTrue(newResult);

    }

    @Test
    public void testImportPartialOfflineInstallation() throws InvalidLinkException, NoInstallationFound {

        InstallationDto installationDto = new InstallationDtoBuilder("test")
                .setGateways("g1", "g2")
                .setLamps("l1", "l2", "l3", "l4", "l5", "l6", "l7", "l8", "l9")
                .addLink("g2", "l1")
                .addLink("l2", "l1")
                //
                .addLink("g1", "l3")
                .addLink("g1", "l4")
                .addLink("l4", "l5")
                .addLink("l6", "l5")
                //
                .addLink("l7", "l8")
                .addLink("l9", "l8")
                .addLink("l9", "l7")
                .build();

        String name = installationController.importInstallation(installationDto);

        assertEquals("test", name);

        boolean result = installationController.checkAllLampsOnline("test");
        assertTrue(!result);

        installationController.makeAllLampsOnline("test");
        boolean newResult = installationController.checkAllLampsOnline("test");
        assertTrue(newResult);
    }
}
