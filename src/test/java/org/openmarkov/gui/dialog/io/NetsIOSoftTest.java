/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.io;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.ProbNet;

import java.net.URL;
import java.util.ArrayList;


public class NetsIOSoftTest {

    ArrayList<String> urlsToTest = new ArrayList<>();

    @Before
    public void setUp() {
        // BN
        urlsToTest.add("https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/master/bn/BN-asia.pgmx");
        // DAN
        urlsToTest.add("https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/master/dan/DAN-decide-test.pgmx");
        // ID
        urlsToTest.add("https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/master/id/ID-decide-test.pgmx");
        // MID
        urlsToTest.add("https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/master/mid/MID-Chancellor.pgmx");
        // POMDP
        urlsToTest.add("https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/master/pomdp/POMDP-coffee-robot.pgmx");
        // Dec-POMDP
        urlsToTest.add("https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/master/pomdp/Dec-POMDP-wireless-network.pgmx");
    }

    @Test
    public void testURLConnection() throws Exception {
        for (String urlString : urlsToTest) {
            URL url = new URL(urlString);
            NetsIO.openNetworkURL(url);
        }
    }
}
