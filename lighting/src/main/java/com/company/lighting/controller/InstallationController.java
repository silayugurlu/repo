package com.company.lighting.controller;

import com.company.lighting.dto.InstallationDto;
import com.company.lighting.exception.InvalidLinkException;
import com.company.lighting.exception.NoInstallationFound;
import com.company.lighting.service.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author silay.ugurlu
 */
@RestController
@RequestMapping("/lighting")
public class InstallationController {

    @Autowired
    InstallationService service;

    @RequestMapping(method = RequestMethod.GET, path = "/test")
    public String test() {
        return "/lighting/test";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/installation")
    @ResponseStatus(HttpStatus.CREATED)
    public String importInstallation(@RequestBody InstallationDto installation) throws InvalidLinkException {
        return service.importInstallation(installation);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/installation/{name}")
    public InstallationDto getInstallation(@PathVariable String name) throws NoInstallationFound {
        return service.getInstallation(name);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/checkAllLampsOnline/{name}")
    public boolean checkAllLampsOnline(@PathVariable String name) throws NoInstallationFound {
        return service.checkAllLampsOnline(name);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/makeAllLampsOnline/{name}")
    public InstallationDto makeAllLampsOnline(@PathVariable String name) throws NoInstallationFound {
        return service.makeAllLampsOnline(name);
    }

    @ExceptionHandler(value = InvalidLinkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidLinkExceptionHandler(InvalidLinkException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = NoInstallationFound.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String installationNotFoundExceptionHandler(NoInstallationFound e) {
        return e.getMessage();
    }

}
