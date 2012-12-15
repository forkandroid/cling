/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.test.resources;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.RecoveringUDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.mock.MockUpnpServiceConfiguration;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.test.data.SampleData;
import org.seamless.util.io.IO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class InvalidUDA10DeviceDescriptorParsingTest {

    @DataProvider(name = "strict")
    public String[][] getStrict() throws Exception {
        return new String[][]{
            {"/invalidxml/device/atb_miviewtv.xml"},
            {"/invalidxml/device/doubletwist.xml"},
            {"/invalidxml/device/eyetv_netstream_sat.xml"},
            {"/invalidxml/device/makemkv.xml"},
            {"/invalidxml/device/tpg.xml"},
            {"/invalidxml/device/ceton_infinitv.xml"},
            {"/invalidxml/device/zyxel_miviewtv.xml"},
            {"/invalidxml/device/perfectwave.xml"},
            {"/invalidxml/device/escient.xml"},
            {"/invalidxml/device/eyecon.xml"},
            {"/invalidxml/device/kodak.xml"},
            {"/invalidxml/device/plutinosoft.xml"},
            {"/invalidxml/device/samsung.xml"},
        };
    }

    @DataProvider(name = "recoverable")
    public String[][] getRecoverable() throws Exception {
        return new String[][]{
            {"/invalidxml/device/missing_namespaces.xml"},
            {"/invalidxml/device/ushare.xml"},
            {"/invalidxml/device/lg.xml"},
            {"/invalidxml/device/readydlna.xml"},
        };
    }

    @DataProvider(name = "unrecoverable")
    public String[][] getUnrecoverable() throws Exception {
        return new String[][]{
            {"/invalidxml/device/unrecoverable/pms.xml"},
            {"/invalidxml/device/unrecoverable/awox.xml"},
            {"/invalidxml/device/philips.xml"},
            {"/invalidxml/device/simplecenter.xml"},
            {"/invalidxml/device/ums.xml"},
        };
    }

    /* ############################## TEST FAILURE ############################ */

    @Test(dataProvider = "recoverable", expectedExceptions = DescriptorBindingException.class)
    public void readFailure(String recoverable) throws Exception {
        readDevice(recoverable, new MockUpnpService());
    }

    @Test(dataProvider = "unrecoverable", expectedExceptions = Exception.class)
    public void readRecoveringFailure(String unrecoverable) throws Exception {
        readDevice(
            unrecoverable,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

    /* ############################## TEST SUCCESS ############################ */

    @Test(dataProvider = "strict")
    public void read(String strict) throws Exception {
        readDevice(strict, new MockUpnpService());
    }

    @Test(dataProvider = "strict")
    public void readRecoveringStrict(String strict) throws Exception {
        readDevice(
            strict,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

    @Test(dataProvider = "recoverable")
    public void readRecovering(String recoverable) throws Exception {
        readDevice(
            recoverable,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

	protected void readDevice(String invalidXMLFile, UpnpService upnpService) throws Exception {
		RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
		upnpService.getConfiguration().getDeviceDescriptorBinderUDA10()
            .describe(device, IO.readLines(getClass().getResourceAsStream(invalidXMLFile)));
	}

}
