package ezbake.data.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.common.base.Stopwatch;

import ezbake.base.thrift.EzSecurityToken;

public abstract class LoggingUtils {
    private LoggingUtils() {}

    public static Stopwatch createStopWatch() {
        return Stopwatch.createStarted();
    }

    public static void stopAndLogStopWatch(Logger logger, Stopwatch watch, String name) {
        watch.stop();
        logger.info("{}|{} miliseconds", name, watch.elapsed(TimeUnit.MILLISECONDS));
    }

    public static void logResetAndStartStopWatch(Logger logger, Stopwatch watch, String name) {
        watch.stop();
        logger.info("{}|{} miliseconds", name, watch.elapsed(TimeUnit.MILLISECONDS));
        watch.reset();
        watch.start();
    }

    public static String secLog(EzSecurityToken security, Object message) {
        return extractUserInfo(security) + ", " + message.toString();
    }

    private static String extractUserInfo(EzSecurityToken security) {
        final String s = security.getTokenPrincipal().getName() + " , " + security.getAuthorizations();
        return s;
    }
}
