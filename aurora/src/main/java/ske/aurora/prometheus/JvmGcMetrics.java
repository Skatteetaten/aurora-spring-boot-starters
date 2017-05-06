package ske.aurora.prometheus;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;

import io.prometheus.client.Collector;
import io.prometheus.client.Histogram;

public class JvmGcMetrics extends Collector {

    public static final String KEY_ACTION = "action";
    public static final String KEY_CAUSE = "cause";
    public static final String KEY_NAME = "gc";

    private Histogram histogram;

    private final NotificationListener gcListener = (notification, handback) -> {
        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            final GarbageCollectionNotificationInfo info =
                GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            Histogram.Child child = histogram.labels(info.getGcName(), info.getGcCause(), info.getGcAction());
            child.observe(info.getGcInfo().getDuration() / Collector.MILLISECONDS_PER_SECOND);
        }
    };

    public JvmGcMetrics() {
        histogram = Histogram.build().name("jvm_gc_hist").help("garbage collection metrics as a histogram")
            .labelNames(new String[] { KEY_NAME, KEY_CAUSE, KEY_ACTION }).create();

        for (GarbageCollectorMXBean gcbean : ManagementFactory.getGarbageCollectorMXBeans()) {
            final NotificationEmitter emitter = (NotificationEmitter) gcbean;
            emitter.addNotificationListener(gcListener, null, null);
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return histogram.collect();
    }

}
