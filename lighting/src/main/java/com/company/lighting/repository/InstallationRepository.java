package com.company.lighting.repository;

import com.company.lighting.data.Installation;

/**
 *
 * @author silay.ugurlu
 */
public interface InstallationRepository {
    
    public String importInstallation(Installation installation);
   
    public Installation findInstallation(String installationName);
    
}
