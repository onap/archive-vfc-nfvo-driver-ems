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
package org.onap.vfc.nfvo.emsdriver.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectMsg;
import org.onap.vfc.nfvo.emsdriver.commons.utils.DriverThread;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationManager;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannel;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannelFactory;

public class CollectMsgReceiverThread extends DriverThread {
	protected static final Logger logger = LoggerFactory.getLogger(CollectMsgReceiverThread.class);

	private long timeStamp = System.currentTimeMillis();

	private MessageChannel collectChannel;

	private TaskThreadService taskService;

	private int threadMaxNum = 100;

	@Override
	public void dispose() {
		collectChannel = MessageChannelFactory
				.getMessageChannel(Constant.COLLECT_CHANNEL_KEY);
		taskService = TaskThreadService.getInstance(threadMaxNum);
		taskService.start();

		while (isRun()) {

			try {
				if (System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE) {
					timeStamp = System.currentTimeMillis();
					logger.debug("COLLECT_CHANNEL Msg size :"
							+ collectChannel.size());
				}
				Object obj = collectChannel.poll();
				if (obj == null) {
					Thread.sleep(10);
					continue;
				}
				if (obj instanceof CollectMsg) {
					CollectMsg collectMsg = (CollectMsg) obj;
					taskService.add(collectMsg);
					logger.debug("receive a CollectMsg id = " + collectMsg.getId());
				} else {
					logger.error("receive Objcet not CollectMsg " + obj);
				}

			} catch (Exception e) {
				logger.error("dispatch alarm exception", e);

			}
		}

	}

	/**
	 * @return the threadMaxNum
	 */
	public int getThreadMaxNum() {
		return threadMaxNum;
	}

	/**
	 * @param threadMaxNum  the threadMaxNum to set
	 */
	public void setThreadMaxNum(int threadMaxNum) {
		this.threadMaxNum = threadMaxNum;
	}

	/**
	 * @return the taskService
	 */
	public TaskThreadService getTaskService() {
		return taskService;
	}

}
