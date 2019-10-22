package pl.com.sages;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class TimeOperatorsTest {

    private Flux<Integer> fluxWithDelays() {
        return Flux.create(sink -> {
            try {
                sink.next(0);
                sink.next(1);
                Thread.sleep(200);
                sink.next(2);
                sink.next(3);
                Thread.sleep(400);
                sink.next(4);
                sink.next(5);
                Thread.sleep(100);
                sink.next(6);
                sink.next(7);
                Thread.sleep(300);
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    @Test
    public void testDelayElements() throws Exception {
        fluxWithDelays()
                .delayElements(Duration.ofMillis(200))
                .elapsed()
                .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));
        Thread.sleep(2000);
    }

    @Test
    public void testDelaySequence() throws Exception {
        fluxWithDelays()
                .delaySequence(Duration.ofMillis(200))
                .elapsed()
                .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));
        Thread.sleep(2000);
    }

    @Test
    public void sampleTest() throws Exception {
        fluxWithDelays()
                .sample(Duration.ofMillis(200))
                .elapsed()
                .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));
        Thread.sleep(2000);
    }

}
