package us.pdavidson.tourine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TourineReporterInstanceHolderTest {

    private String key1 = "key1";
    private String key2 = "key2";

    @Mock
    private TourineReporter reporter1;

    @Mock
    private TourineReporter reporter2;

    @Before
    public void setUp() throws Exception {
        for(String key : TourineReporterInstanceHolder.keySet()) {
            TourineReporterInstanceHolder.clear(key);
        }
    }

    @Test
    public void testSet() throws Exception {
        TourineReporterInstanceHolder.set(key1, reporter1);
        TourineReporterInstanceHolder.set(key2, reporter2);

        assertThat(TourineReporterInstanceHolder.keySet()).containsOnly(key1, key2);

    }

    @Test(expected = IllegalStateException.class)
    public void testSet_WhenAlreadyPresent_ThenException() throws Exception {

        TourineReporterInstanceHolder.set(key1, reporter1);
        assertThat(TourineReporterInstanceHolder.keySet()).containsOnly(key1);

        TourineReporterInstanceHolder.set(key1, reporter1);


    }

    @Test
    public void testGet() throws Exception {
        TourineReporterInstanceHolder.set(key1, reporter1);
        TourineReporterInstanceHolder.set(key2, reporter2);

        TourineReporter actual1 = TourineReporterInstanceHolder.get(key1);
        assertThat(actual1).isSameAs(reporter1);

        TourineReporter actual2 = TourineReporterInstanceHolder.get(key2);
        assertThat(actual2).isSameAs(reporter2);
    }


    @Test(expected = IllegalStateException.class)
    public void testGet_WhenNotPresent_ThenException() throws Exception {

        TourineReporterInstanceHolder.get(key2);

        // Should never hit here
        assertThat(false).isTrue();
    }

    @Test
    public void testClear() throws Exception {

        TourineReporterInstanceHolder.set(key1, reporter1);
        assertThat(TourineReporterInstanceHolder.contains(key1)).isTrue();

        TourineReporterInstanceHolder.clear(key1);
        assertThat(TourineReporterInstanceHolder.contains(key1)).isFalse();



    }

}