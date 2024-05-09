/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.runtime.spring.async;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;

// import java.lang.management.ManagementFactory;
// import java.lang.management.RuntimeMXBean;

// class CloseFD {
//     static int getCurrentPID() {
//         RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
//         String name = runtimeMXBean.getName(); // 格式为PID@hostname
//         try {
//             return Integer.parseInt(name.substring(0, name.indexOf('@')));
//         } catch (NumberFormatException | IndexOutOfBoundsException e) {
//             e.printStackTrace();
//             return -1; // 如果无法解析PID，返回-1表示失败
//         }
//     }

//     public static void closeAll() {
//         int pid = getCurrentPID(); // 你要关闭的进程的PID

//         try {
//             Process process = Runtime.getRuntime().exec("lsof -p " + pid);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(
//                     process.getInputStream()));

//             String line;
//             while ((line = reader.readLine()) != null) {
//                 if (line.contains("/")) { // 假设打开的文件描述符行包含了路径信息
//                     String[] parts = line.split("\\s+"); // 使用空格分隔行内容
//                     String fd = parts[3]; // 文件描述符所在的列
//                     int fdnum = 0;
//                     try {
//                         fdnum = Integer.parseInt(fd.replaceAll("[^0-9]", ""));
//                     } catch (NumberFormatException e) {
//                         continue;
//                     }
//                     String type = parts[4]; // 文件描述符类型所在的列
//                     if (!type.equals("REG") && !type.equals("DIR")) {
//                         continue;
//                     }
//                     System.out.println("Closing fd: " + line);
//                     Runtime.getRuntime().exec(String.format("exec %d>&-", fdnum));
//                     // closeFD(pid, fd);
//                 }
//             }
//             reader.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     static void closeFD(int pid, String fd) {
//         // try {
//         // Runtime.getRuntime().exec("kill -9 " + pid + " " + fd); //
//         // 替换成你实际需要的关闭文件描述符的命令
//         // System.out.println("Closed FD " + fd + " for process " + pid);
//         // } catch (IOException e) {
//         // System.out.println("Failed to close FD " + fd + " for process " + pid + ": "
//         // + e.getMessage());
//         // }
//     }
// }

/**
 * @author qilong.zql
 * @since 2.6.0
 */
public class AsyncTaskExecutionListener implements PriorityOrdered,
                                       ApplicationListener<ContextRefreshedEvent>,
                                       ApplicationContextAware, Resource {
    private ApplicationContext applicationContext;

    // protected Path writeConfig(String content) throws IOException {
    // Path config = Files.createTempFile(getClass().getName(), ".yaml");
    // Files.writeString(config, content);
    // return config;
    // }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        System.out.println("beforeCheckpoint sofa");
        // CloseFD.closeAll();
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        System.out.println("afterRestore sofa");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
            AsyncTaskExecutor.ensureAsyncTasksFinish();
        }
        Core.getGlobalContext().register(this);
        System.out.println("Spring init final");
    }

    @Override
    public int getOrder() {
        // invoked after {@literal
        // com.alipay.sofa.isle.spring.listener.SofaModuleContextRefreshedListener}
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}