package com.company.lighting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 *
 * @author silay.ugurlu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class InstallationRestApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testImportInstallation() throws Exception {
        mockMvc.perform(get("/lighting/test/")).andExpect(status().isOk());
    }

    @Test
    public void testGetInstallationNotExist() throws Exception {
        mockMvc.perform(get("/lighting/installation/nameTest"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(("There is no installation with name : nameTest")));
    }

}
