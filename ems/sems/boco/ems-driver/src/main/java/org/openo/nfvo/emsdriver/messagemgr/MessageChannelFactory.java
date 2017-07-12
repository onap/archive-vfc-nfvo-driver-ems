/**
 * Copyright 2017 BOCO Corporation.
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
package org.openo.nfvo.emsdriver.messagemgr;

import java.util.HashMap;
import java.util.Map;

public class MessageChannelFactory {
	
	public static Map<String, MessageChannel> map = new HashMap<String, MessageChannel>();

	public synchronized static MessageChannel getMessageChannel(String key,Integer size){
		if(map.get(key) != null){
			return map.get(key);
		}
		MessageChannel mc = null;
		if(size != null && size > 0){
			mc = new MessageChannel(size);
		}else{
			mc = new MessageChannel();
		}
		
		map.put(key, mc);
		return mc;
	}
	
	public synchronized static MessageChannel getMessageChannel(String key){
		if(map.get(key) != null){
			return map.get(key);
		}
		MessageChannel mc = new MessageChannel();
		
		map.put(key, mc);
		return mc;
	}
	
	public synchronized static boolean destroyMessageChannel(String key){
		if(map.get(key) != null){
			map.remove(key);
			return true;
		}
		return false;
	}
	
	public synchronized static void clean(){
		map.clear();
	}
}
