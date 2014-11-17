package us.pdavidson.tourine;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.functions.Action1;

import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TourineReporterTest {

    TourineReporter reporter;

    @Mock
    private MetricRegistry registry;
    private TourineJsonFormat jsonType = TourineJsonFormat.HYSTRIX;
    private TimeUnit durationUnit = TimeUnit.MILLISECONDS;
    private TimeUnit rateUnit = TimeUnit.MILLISECONDS;
    private MetricFilter filter = MetricFilter.ALL;

    @Mock
    private Timer timer1;

    @Mock
    private Snapshot snapshot1;

    @Mock
    private Timer timer2;

    @Mock
    private Snapshot snapshot2;

    @Mock
    private Timer timer3;

    @Mock
    private Snapshot snapshot3;

    @Mock
    private Timer timer4;

    @Mock
    private Snapshot snapshot4;


    @Before
    public void setUp() throws Exception {
        reporter = spy(new TourineReporter(registry, filter, rateUnit, durationUnit, jsonType));
        when(timer1.getSnapshot()).thenReturn(snapshot1);
        when(timer2.getSnapshot()).thenReturn(snapshot2);
        when(timer3.getSnapshot()).thenReturn(snapshot3);
        when(timer4.getSnapshot()).thenReturn(snapshot4);
    }

    @Test
    public void testEmit() throws Exception {
        final List<String> list = Lists.newArrayList();

        reporter.getTimerObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                list.add(s);
            }
        });


        SortedMap<String, Timer> emission1 = Maps.newTreeMap();
        emission1.put("s1", timer1);
        emission1.put("s2", timer2);

        reporter.emitTimers(emission1);

        SortedMap<String, Timer> emission2 = Maps.newTreeMap();
        emission2.put("s3", timer3);
        emission2.put("s4", timer4);

        reporter.emitTimers(emission2);

        assertThat(list.size()).isEqualTo(4);

    }

    @Test
    public void testReport() throws Exception {
        SortedMap<String, Timer> timers = Maps.newTreeMap();
        timers.put("key1", timer1);
        reporter.report(null, null, null, null, timers);

        verify(reporter).emitTimers(timers);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReport_EmptyMap() throws Exception {

        SortedMap<String, Timer> timers = Maps.newTreeMap();
        reporter.report(null, null, null, null, timers);

        verify(reporter, never()).emitTimers(any(SortedMap.class));
    }
}