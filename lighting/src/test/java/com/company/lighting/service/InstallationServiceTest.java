package com.company.lighting.service;

import com.company.lighting.data.Installation;
import com.company.lighting.data.InstallationBuilder;
import com.company.lighting.dto.InstallationDto;
import com.company.lighting.dto.InstallationDtoBuilder;
import com.company.lighting.exception.InvalidLinkException;
import com.company.lighting.exception.NoInstallationFound;
import com.company.lighting.repository.InstallationRepository;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author silay.ugurlu
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InstallationServiceTest {

    @Autowired
    private InstallationService installationService;

    @MockBean
    private InstallationRepository repositoryMock;

    @Test
    public void testOnlineLampsWithOneGateway() throws InvalidLinkException, NoInstallationFound {

        Installation installation = new InstallationBuilder("testInstallation")
                .setGateways(Arrays.asList("g1"))
                .setLamps(Arrays.asList("l1", "l2", "l3"))
                .addLinkedElement("g1", "l1")
                .addLinkedElement("g1", "l2")
                .addLinkedElement("l1", "l3")
                .build();

        when(repositoryMock.findInstallation("testInstallation")).thenReturn(installation);

        boolean result = installationService.checkAllLampsOnline("testInstallation");
        assertTrue(result);
    }

    @Test
    public void testOnlineLampsWithNoGateway() throws InvalidLinkException, NoInstallationFound {

        Installation installation = new InstallationBuilder("testInstallation")
                .setLamps(Arrays.asList("l1", "l2", "l3"))
                .addLinkedElement("l1", "l3")
                .addLinkedElement("l2", "l3")
                .build();

        when(repositoryMock.findInstallation("testInstallation")).thenReturn(installation);

        boolean result = installationService.checkAllLampsOnline("testInstallation");
        assertTrue(!result);
    }

    @Test(expected = InvalidLinkException.class)
    public void importInvalidLink() throws InvalidLinkException {

        InstallationDto installationDto = new InstallationDtoBuilder("testInstallation")
                .setGateways("g1")
                .setLamps("l1", "l2")
                .addLink("g1", "15")
                .build();

        installationService.importInstallation(installationDto);
    }

    @Test
    public void testOnlineLampsWithTwoGateways() throws InvalidLinkException, NoInstallationFound {

        Installation installation = new InstallationBuilder("testInstallation")
                .setGateways(Arrays.asList("g1", "g2"))
                .setLamps(Arrays.asList("l1", "l2", "l3"))
                .addLinkedElement("g1", "l1")
                .addLinkedElement("g1", "l2")
                .addLinkedElement("g2", "l3")
                .build();

        when(repositoryMock.findInstallation("testInstallation")).thenReturn(installation);

        boolean result = installationService.checkAllLampsOnline("testInstallation");
        assertTrue(result);
    }

    @Test
    public void testOfflineLamps() throws InvalidLinkException, NoInstallationFound {
        Installation installation = new InstallationBuilder("testInstallation")
                .setGateways(Arrays.asList("g1"))
                .setLamps(Arrays.asList("l1", "l2", "l3"))
                .addLinkedElement("g1", "l1")
                .addLinkedElement("g1", "l2")
                .build();

        when(repositoryMock.findInstallation("testInstallation")).thenReturn(installation);

        boolean result = installationService.checkAllLampsOnline("testInstallation");
        assertTrue(!result); // l3 is offline
    }

    @Test
    public void testOfflineLampsToOnline() throws InvalidLinkException, NoInstallationFound {
        Installation installation = new InstallationBuilder("testInstallation")
                .setGateways(Arrays.asList("g1"))
                .setLamps(Arrays.asList("l1", "l2", "l3"))
                .addLinkedElement("g1", "l1")
                .addLinkedElement("g1", "l2")
                .build();

        when(repositoryMock.findInstallation("testInstallation")).thenReturn(installation);

        InstallationDto installationDto = installationService.makeAllLampsOnline("testInstallation");

        InstallationBuilder installationBuilder = new InstallationBuilder(installationDto.getName())
                .setGateways(installationDto.getGateways())
                .setLamps(installationDto.getLamps());

        //put links to installation linked elements
        List<String[]> links = installationDto.getLinks();
        if (links != null) {
            for (String[] link : links) {
                String source = link[0];
                String destination = link[1];
                installationBuilder.addLinkedElement(source, destination);
            }
        }

        Installation installationResult = installationBuilder.build();

        when(repositoryMock.findInstallation("testInstallationResult")).thenReturn(installationResult);

        boolean result = installationService.checkAllLampsOnline("testInstallationResult");
        assertTrue(result);
    }

}
