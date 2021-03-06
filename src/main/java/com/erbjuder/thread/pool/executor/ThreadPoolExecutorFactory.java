package com.erbjuder.thread.pool.executor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sun.appserv.server.LifecycleEvent;
import com.sun.appserv.server.ServerLifecycleException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;

/**
 *
 * @author Stefan Andersson
 */
public class ThreadPoolExecutorFactory implements
        javax.naming.spi.ObjectFactory,
        com.sun.appserv.server.LifecycleListener,
        java.io.Serializable {

    @Override
    public Object getObjectInstance(Object obj,
            javax.naming.Name name,
            Context nameCtx,
            Hashtable<?, ?> environment) throws Exception {

        ThreadPoolExecutor tp = ThreadPoolExecutor.getInstance();
//        Logger.getLogger(ThreadPoolExecutorFactory.class.getName()).log(Level.SEVERE, "obj=" + obj);
//        Logger.getLogger(ThreadPoolExecutorFactory.class.getName()).log(Level.SEVERE, "name=" + name);
//        Logger.getLogger(ThreadPoolExecutorFactory.class.getName()).log(Level.SEVERE, "nameCtx=" + nameCtx);
//        Logger.getLogger(ThreadPoolExecutorFactory.class.getName()).log(Level.SEVERE, "tp=" + tp);

        try {
            Reference reference = (Reference) obj;
            Enumeration<?> enumeration = reference.getAll();
            TimeUnit timeUnit = ThreadPoolExecutor.defaultTimeUnit;
            long keepAliveTime = ThreadPoolExecutor.defaultKeepAliveTime;
            while (enumeration.hasMoreElements()) {
                RefAddr refAddr = (RefAddr) enumeration.nextElement();
                String pname = refAddr.getType();
                String pvalue = (String) refAddr.getContent();
                if ("corePoolSize".equalsIgnoreCase(pname)) {
                    tp.setCorePoolSize(Integer.parseInt(pvalue));
                } else if ("maximumPoolSize".equalsIgnoreCase(pname)) {
                    tp.setMaximumPoolSize(Integer.parseInt(pvalue));
                } else if ("timeUnit".equalsIgnoreCase(pname)) {
                    timeUnit = TimeUnit.valueOf(pvalue);
                } else if ("keepAliveTime".equalsIgnoreCase(pname)) {
                    keepAliveTime = Long.parseLong(pvalue);
                } else if ("allowCoreThreadTimeOut".equalsIgnoreCase(pname)) {
                    tp.allowCoreThreadTimeOut(Boolean.parseBoolean(pvalue));
                } else if ("prestartAllCoreThreads".equalsIgnoreCase(pname)) {
                    if (Boolean.parseBoolean(pvalue)) {
                        tp.prestartAllCoreThreads();
                    }
                } else {
                    throw new IllegalArgumentException("Unrecognized property name: " + pname);
                }
            }
            tp.setKeepAliveTime(keepAliveTime, timeUnit);
        } catch (Exception e) {
            throw (NamingException) (new NamingException()).initCause(e);
        }
        return tp;
    }

    @Override
    public void handleEvent(LifecycleEvent event) throws ServerLifecycleException {
        if (event.getEventType() == LifecycleEvent.TERMINATION_EVENT
                || event.getEventType() == LifecycleEvent.SHUTDOWN_EVENT) {
            ThreadPoolExecutor tp = ThreadPoolExecutor.getInstance();
            System.out.println("About to purge and shutdown " + tp + ", active thread count: "
                    + tp.getActiveCount());
            tp.purge();
            tp.shutdown();

        }
    }

}