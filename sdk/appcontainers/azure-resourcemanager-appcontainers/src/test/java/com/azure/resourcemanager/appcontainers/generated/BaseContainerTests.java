// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.appcontainers.models.BaseContainer;
import com.azure.resourcemanager.appcontainers.models.ContainerResources;
import com.azure.resourcemanager.appcontainers.models.EnvironmentVar;
import com.azure.resourcemanager.appcontainers.models.VolumeMount;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BaseContainerTests {
    @Test
    public void testDeserialize() {
        BaseContainer model =
            BinaryData
                .fromString(
                    "{\"image\":\"wmgxcxrsl\",\"name\":\"utwu\",\"command\":[\"rpkhjwn\",\"yqsluic\",\"dggkzzlvmbmpa\"],\"args\":[\"dfvue\",\"yw\",\"bpfvm\"],\"env\":[{\"name\":\"fouyf\",\"value\":\"akcp\",\"secretRef\":\"yzvqt\"},{\"name\":\"ubex\",\"value\":\"zksmondj\",\"secretRef\":\"uxvypomgkopkwh\"}],\"resources\":{\"cpu\":52.49920535887086,\"memory\":\"qgxy\",\"ephemeralStorage\":\"ocmbqfqvmkcxoza\"},\"volumeMounts\":[{\"volumeName\":\"lxprglyatddckcbc\",\"mountPath\":\"jrjxgciqibrhosx\"},{\"volumeName\":\"qrhzoymibmrqyib\",\"mountPath\":\"wfluszdt\"},{\"volumeName\":\"rkwofyyvoqa\",\"mountPath\":\"iexpbtgiwbwo\"}]}")
                .toObject(BaseContainer.class);
        Assertions.assertEquals("wmgxcxrsl", model.image());
        Assertions.assertEquals("utwu", model.name());
        Assertions.assertEquals("rpkhjwn", model.command().get(0));
        Assertions.assertEquals("dfvue", model.args().get(0));
        Assertions.assertEquals("fouyf", model.env().get(0).name());
        Assertions.assertEquals("akcp", model.env().get(0).value());
        Assertions.assertEquals("yzvqt", model.env().get(0).secretRef());
        Assertions.assertEquals(52.49920535887086D, model.resources().cpu());
        Assertions.assertEquals("qgxy", model.resources().memory());
        Assertions.assertEquals("lxprglyatddckcbc", model.volumeMounts().get(0).volumeName());
        Assertions.assertEquals("jrjxgciqibrhosx", model.volumeMounts().get(0).mountPath());
    }

    @Test
    public void testSerialize() {
        BaseContainer model =
            new BaseContainer()
                .withImage("wmgxcxrsl")
                .withName("utwu")
                .withCommand(Arrays.asList("rpkhjwn", "yqsluic", "dggkzzlvmbmpa"))
                .withArgs(Arrays.asList("dfvue", "yw", "bpfvm"))
                .withEnv(
                    Arrays
                        .asList(
                            new EnvironmentVar().withName("fouyf").withValue("akcp").withSecretRef("yzvqt"),
                            new EnvironmentVar()
                                .withName("ubex")
                                .withValue("zksmondj")
                                .withSecretRef("uxvypomgkopkwh")))
                .withResources(new ContainerResources().withCpu(52.49920535887086D).withMemory("qgxy"))
                .withVolumeMounts(
                    Arrays
                        .asList(
                            new VolumeMount().withVolumeName("lxprglyatddckcbc").withMountPath("jrjxgciqibrhosx"),
                            new VolumeMount().withVolumeName("qrhzoymibmrqyib").withMountPath("wfluszdt"),
                            new VolumeMount().withVolumeName("rkwofyyvoqa").withMountPath("iexpbtgiwbwo")));
        model = BinaryData.fromObject(model).toObject(BaseContainer.class);
        Assertions.assertEquals("wmgxcxrsl", model.image());
        Assertions.assertEquals("utwu", model.name());
        Assertions.assertEquals("rpkhjwn", model.command().get(0));
        Assertions.assertEquals("dfvue", model.args().get(0));
        Assertions.assertEquals("fouyf", model.env().get(0).name());
        Assertions.assertEquals("akcp", model.env().get(0).value());
        Assertions.assertEquals("yzvqt", model.env().get(0).secretRef());
        Assertions.assertEquals(52.49920535887086D, model.resources().cpu());
        Assertions.assertEquals("qgxy", model.resources().memory());
        Assertions.assertEquals("lxprglyatddckcbc", model.volumeMounts().get(0).volumeName());
        Assertions.assertEquals("jrjxgciqibrhosx", model.volumeMounts().get(0).mountPath());
    }
}
