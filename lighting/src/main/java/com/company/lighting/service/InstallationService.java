package com.company.lighting.service;

import com.company.lighting.dto.InstallationDto;
import com.company.lighting.exception.InvalidLinkException;
import com.company.lighting.exception.NoInstallationFound;

/**
 *
 * @author silay.ugurlu
 */
public interface InstallationService {

    
    /**
     * Get lamps,gateways and links in installation parameter and put them to
     * repository
     * 
     * @param installation
     * @return
     * @throws InvalidLinkException 
     */
    public String importInstallation(InstallationDto installation) throws InvalidLinkException;

    /**
     * get installation name as parameter and returns the elements and links in
     * the installation
     *
     * @param installationName
     * @return installation contains lamps,gateways and links
     * @throws NoInstallationFound if there is no installation having the given installationName
     */
    public InstallationDto getInstallation(String installationName) throws NoInstallationFound;

    /**
     * checks if all lamps online. a lamp is online if it can find a path to a
     * gateway
     *
     * @param installationName
     * @return true is all lamps are online, false if not
     * @throws NoInstallationFound if there is no installation having the given installationName
     */
    public boolean checkAllLampsOnline(String installationName) throws NoInstallationFound;

    /**
     * adds the necessary links among offline lamps and makes all the lamps
     * online
     *
     * @param installationName
     * @return updated installation
     * @throws NoInstallationFound if there is no installation having the given installationName
     */
    public InstallationDto makeAllLampsOnline(String installationName) throws NoInstallationFound;
}
