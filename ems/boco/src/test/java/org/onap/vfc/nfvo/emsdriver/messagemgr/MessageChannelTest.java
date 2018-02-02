/*
 * Copyright 2017 BOCO Corporation.  CMCC Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.vfc.nfvo.emsdriver.messagemgr;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageChannelTest {

    private MessageChannel messageChannel;

    @Before
    public void setUp() {
        messageChannel = new MessageChannel();
    }

    @Test
    public void MessageChannel() {
        MessageChannel messageChannel = new MessageChannel(10);
        assertNotNull(messageChannel.getQueue());
        MessageChannel messageChannel1 = new MessageChannel(0);
        assertNotNull(messageChannel1.getQueue());
    }

    @Test
    public void put() throws Exception {
        Object obj = new Object();
        messageChannel.put(obj);

        assertEquals(1, messageChannel.size());
    }

    @Test
    public void get() throws Exception {
        Object obj = new Object();
        messageChannel.put(obj);
        Object objr = messageChannel.get();
        assertNotNull(objr);
    }

    @Test
    public void poll() throws Exception {
        Object obj = new Object();
        messageChannel.put(obj);
        Object objr = messageChannel.poll();
        assertNotNull(objr);
    }

    @Test
    public void clear() throws Exception {
        Object obj = new Object();
        messageChannel.put(obj);
        messageChannel.clear();
        assertEquals(0, messageChannel.size());
    }

}
