package com.company.lighting.service;


import com.company.lighting.data.Installation;
import com.company.lighting.data.InstallationBuilder;
import com.company.lighting.data.Lamp;
import com.company.lighting.data.LightingElement;
import com.company.lighting.dto.InstallationDto;
import com.company.lighting.dto.InstallationDtoBuilder;
import com.company.lighting.exception.InvalidLinkException;
import com.company.lighting.exception.NoInstallationFound;
import com.company.lighting.repository.InstallationRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author silay.ugurlu
 */
@Service
public class InstallationServiceImpl implements InstallationService {

    @Autowired
    private InstallationRepository repository;

    @Override
    public String importInstallation(InstallationDto installationDto) throws InvalidLinkException {

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

        Installation installation = installationBuilder.build();

        //import installation to repository
        return repository.importInstallation(installation);
    }

    @Override
    public InstallationDto getInstallation(String installationName) throws NoInstallationFound {

        //find installation from repository
        Installation installation = repository.findInstallation(installationName);

        if (installation == null) {
            throw new NoInstallationFound("There is no installation with name : " + installationName);
        }
        //build installationdto

        InstallationDto installationDto = convertInstallationToDto(installationName, installation);
        return installationDto;
    }

    @Override
    public boolean checkAllLampsOnline(String installationName) throws NoInstallationFound {
        //find installation from repository
        Installation installation = repository.findInstallation(installationName);

        if (installation == null) {
            throw new NoInstallationFound("There is no installation with name : " + installationName);
        }
        Map<String, Boolean> visited = new HashMap<>();

        int countOfVisitedLamps = 0;
        Set<String> gateways = installation.getGateways().keySet();
        if (gateways != null) {

            //traverse linked elements in installation starting from each gateway
            for (String gateway : gateways) {
                LinkedList<String> queue = new LinkedList<>();
                queue.add(gateway);

                while (!queue.isEmpty()) {  //check if there is more element to visit
                    String element = queue.poll();

                    Set<LightingElement> linkedElements = installation.getLinkedElements().getOrDefault(element, new HashSet<>());

                    //visit each linked element breadth-first
                    countOfVisitedLamps = linkedElements.stream().filter((e) -> (!visited.containsKey(e.getName()))).map((e) -> {
                        //visit element if it is not visited before and add it to the queue to get and visit its linked elements

                        visited.put(e.getName(), Boolean.TRUE);
                        return e;
                    }).map((e) -> {
                        //count visited lamps
                        queue.add(e.getName());
                        return e;
                    }).filter((e) -> (e instanceof Lamp)).map((_item) -> 1).reduce(countOfVisitedLamps, Integer::sum);
                }
            }
        }
        if (installation.getLamps() != null) {
            // if count of visited lamps are equal to the count of total lamps, it means all lamps are online
            return countOfVisitedLamps == installation.getLamps().size();
        }

        return false;

    }

    @Override
    public InstallationDto makeAllLampsOnline(String installationName) throws NoInstallationFound {
        //find installation from repository
        Installation installation = repository.findInstallation(installationName);

        if (installation == null) {
            throw new NoInstallationFound("There is no installation with name : " + installationName);
        }
        Set<String> gateways = installation.getGateways().keySet();

        //there is no gateway, can throw exception
        if (gateways == null) {
            return null;
        }

        Map<String, Integer> gatewaySize = new LinkedHashMap<>();
        //calculate count of connected elements for each gateway and return 
        Map<String, Lamp> offlineLamps = calculateSizeOfGateways(installation, gateways, gatewaySize);

        //calculate count of connected lamps for a determined root lamp
        Map<String, Integer> offlineNetworkSize = calculateOfflineLampsSize(offlineLamps, installation);

        try {
            //add links and make all lamps connected
            connectOfflineLamps(offlineNetworkSize, gatewaySize, installation);
        } catch (InvalidLinkException ex) {
            Logger.getLogger(InstallationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        //import updated installation to the repository
        repository.importInstallation(installation);

        InstallationDto installationDto = convertInstallationToDto(installationName, installation);
        return installationDto;
    }

    /**
     * add link from offline lamps to gateways that has minimum size in
     * installation
     *
     * @param offlineNetworkSize
     * @param gatewaySize
     * @param installation
     */
    private void connectOfflineLamps(Map<String, Integer> offlineNetworkSize, Map<String, Integer> gatewaySize, Installation installation) throws InvalidLinkException {
        for (String lampName : offlineNetworkSize.keySet()) {

            // sort gateways by their count of connected elements
            gatewaySize = sortByValue(gatewaySize);
            Integer countOfLamps = offlineNetworkSize.get(lampName);

            //get the first gateway that has minimum count of connection
            String gateway = gatewaySize.keySet().iterator().next();

            installation.addLinkedElement(lampName, gateway);

            //update the count of connected elements for teh gateway
            gatewaySize.put(gateway, gatewaySize.get(gateway) + countOfLamps);
        }
    }

    /**
     * select a root lamp, calculate and return count of connected lamps
     *
     * @param offlineLamps
     * @param installation
     * @return offlinemaps includes it coount of connected lamps
     */
    private Map<String, Integer> calculateOfflineLampsSize(Map<String, Lamp> offlineLamps, Installation installation) {
        Map<String, Integer> offlineNetworkSize = new HashMap<>();
        Map<String, Boolean> visitedOfflineLamps = new HashMap<>();
        offlineLamps.keySet().stream().filter((lamp) -> (!visitedOfflineLamps.containsKey(lamp))).forEach((lamp) -> {
            Map<String, Boolean> visitedLamps = new HashMap<>();
            LinkedList<String> queue = new LinkedList<>();

            int countOfVisitedElements = 1;
            queue.add(lamp);

            while (!queue.isEmpty()) {  //check if there is more element to visit
                String element = queue.poll();

                Set<LightingElement> linkedElements = installation.getLinkedElements().getOrDefault(element, new HashSet<>());

                //visit each linked element breadth-first
                for (LightingElement e : linkedElements) {

                    countOfVisitedElements++;
                    if (!visitedLamps.containsKey(e.getName())) {
                        //visit element if it is not visited before and add it to the queue to get and visit its linked elements

                        visitedOfflineLamps.put(e.getName(), Boolean.TRUE);
                        visitedLamps.put(e.getName(), Boolean.TRUE);
                        queue.add(e.getName());

                    }
                }
            }
            offlineNetworkSize.put(lamp, countOfVisitedElements);
        });
        return offlineNetworkSize;
    }

    /**
     * calculate count of connected elements for each gateway and return offline
     * lamps
     *
     * @param installation
     * @param gateways
     * @param gatewaySize
     * @return offline lamps
     */
    private Map<String, Lamp> calculateSizeOfGateways(Installation installation, Set<String> gateways, Map<String, Integer> gatewaySize) {
        //keep lamps not connected
        Map<String, Lamp> offlineLamps = new HashMap<>();
        offlineLamps.putAll(installation.getLamps());
        //traverse linked elements in installation starting from each gateway
        gateways.stream().forEach((gateway) -> {
            LinkedList<String> queue = new LinkedList<>();

            Map<String, Boolean> visited = new HashMap<>();
            int countOfVisitedElements = 1;
            queue.add(gateway);

            while (!queue.isEmpty()) {  //check if there is more element to visit
                String element = queue.poll();

                Set<LightingElement> linkedElements = installation.getLinkedElements().getOrDefault(element, new HashSet<>());

                //visit each linked element breadth-first
                for (LightingElement e : linkedElements) {

                    // track count of linked elements of gateway
                    countOfVisitedElements++;

                    if (!visited.containsKey(e.getName())) {
                        //visit element if it is not visited before and add it to the queue to get and visit its linked elements
                        visited.put(e.getName(), Boolean.TRUE);
                        queue.add(e.getName());

                        if (e instanceof Lamp) {
                            //lamp is online, remove from offline lamps map
                            offlineLamps.remove(e.getName());
                        }
                    }
                }
            }

            gatewaySize.put(gateway, countOfVisitedElements);
        });
        return offlineLamps;
    }

    private Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // Convert Map to List of Map
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> (o1.getValue()).compareTo(o2.getValue()));

        // Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        list.stream().forEach((entry) -> {
            sortedMap.put(entry.getKey(), entry.getValue());
        });

        return sortedMap;
    }

    private InstallationDto convertInstallationToDto(String installationName, Installation installation) {
        InstallationDtoBuilder installationDtoBuilder = new InstallationDtoBuilder(installationName)
                .setGateways(installation.getGateways().keySet())
                .setLamps(installation.getLamps().keySet());

        //put links to InstallationDtoBuilder
        Map<String, Set<LightingElement>> linkedElements = installation.getLinkedElements();
        if (linkedElements != null) {
            for (String source : linkedElements.keySet()) {
                for (LightingElement element : linkedElements.get(source)) {
                    installationDtoBuilder = installationDtoBuilder.addLink(source, element.getName());

                    //TODO : remove source element from the connection list of destination 
                    //so that link is not added to installationDto.links twice for each direction
                }
            }
        }
        return installationDtoBuilder.build();
    }

}
