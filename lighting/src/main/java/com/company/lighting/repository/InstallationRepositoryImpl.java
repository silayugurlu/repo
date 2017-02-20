package com.company.lighting.repository;

import com.company.lighting.data.Installation;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 *
 * @author silay.ugurlu
 */
@Repository
public class InstallationRepositoryImpl implements InstallationRepository {

    private Map<String, Installation> installationMap = new HashMap<>();

    @Override
    public String importInstallation(Installation installation) {
        installationMap.put(installation.getName(), installation);
        return installation.getName();
    }

    @Override
    public Installation findInstallation(String installationName) {
        return installationMap.get(installationName);
    }

}
