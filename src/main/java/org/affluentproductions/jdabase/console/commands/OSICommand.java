package org.affluentproductions.jdabase.console.commands;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.console.ConsoleCommand;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class OSICommand extends ConsoleCommand {

    public OSICommand() {
        super("osi", "Prints information about the OS");
    }

    @Override
    public void run(String[] args, JDABase jdaBase) {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
        String _sla = String.valueOf(systemLoadAverage);
        if (_sla.length() > 7) _sla = _sla.substring(0, 7);
        String os = operatingSystemMXBean.getName() + " [ver=" + operatingSystemMXBean.getVersion() + ";arch="
                + operatingSystemMXBean.getArch() + "]";
        Runtime runtime = Runtime.getRuntime();
        int mb = 1024 * 1024;
        long maxMemory = runtime.maxMemory() / mb;
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / mb;
        System.out.println("[OS INFO] OS: " + os);
        System.out.println("[OS INFO] CPU: " + availableProcessors + " available processors | " + _sla + " load avg.");
        System.out.println("[OS INFO] Memory: " + usedMemory + "/" + maxMemory + "MB in use");
    }
}
