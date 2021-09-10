package com.alibaba.jvm.sandbox.core;

import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.lang3.StringUtils;

import static com.alibaba.jvm.sandbox.core.util.SandboxStringUtils.getCauseMessage;

/**
 * 沙箱内核启动器
 *
 * sh脚本 启动的应该是这个类，
 * 这个类在maven的mainClass节点配置
 *
 *
 * Created by luanjia@taobao.com on 16/10/2.
 */
public class CoreLauncher {


    /**
     *
     *
     * @param targetJvmPid   目标PID
     * @param agentJarPath   sandbox-agent.jar的路径
     * @param token token
     * @throws Exception
     */
    public CoreLauncher(final String targetJvmPid,
                        final String agentJarPath,
                        final String token) throws Exception {

        // 加载agent
        attachAgent(targetJvmPid, agentJarPath, token);

    }

    /**
     * 内核启动程序
     *
     * main方法入口
     *
     * @param args 参数
     *             [0] : PID
     *             [1] : agent.jar's value
     *             [2] : token
     */
    public static void main(String[] args) {
        try {

            // check args  检查参数
            if (args.length != 3
                    || StringUtils.isBlank(args[0])
                    || StringUtils.isBlank(args[1])
                    || StringUtils.isBlank(args[2])) {
                throw new IllegalArgumentException("illegal args");
            }
            // 调用构造函数
            new CoreLauncher(args[0], args[1], args[2]);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.err.println("sandbox load jvm failed : " + getCauseMessage(t));
            System.exit(-1);
        }
    }

    // 加载Agent
    private void attachAgent(final String targetJvmPid,
                             final String agentJarPath,
                             final String cfg) throws Exception {

        VirtualMachine vmObj = null;
        try {
            // 这里就是Java agent的标准写法，对一个已经存在的Java进程进行附加， 用的就是这种方法。
            // 核心参数就是targetJvmPid
            vmObj = VirtualMachine.attach(targetJvmPid);
            if (vmObj != null) {
                // 加载sandbox-agent.jar     之后应该跳转到sandbox-agent
                vmObj.loadAgent(agentJarPath, cfg);
            }

        } finally {
            if (null != vmObj) {
                vmObj.detach();
            }
        }

    }

}
